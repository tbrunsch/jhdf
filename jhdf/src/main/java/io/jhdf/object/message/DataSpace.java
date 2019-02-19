package io.jhdf.object.message;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.stream.IntStream;

import io.jhdf.Superblock;
import io.jhdf.Utils;
import io.jhdf.exceptions.HdfException;

public class DataSpace {

	private final byte version;
	private final boolean maxSizesPresent;
	private final int[] dimensions;
	private final int[] maxSizes;
	private final byte type;
	private final long totalLentgh;

	private DataSpace(ByteBuffer bb, Superblock sb) {

		version = bb.get();
		int numberOfdimensions = bb.get();
		byte[] flagBits = new byte[1];
		bb.get(flagBits);
		BitSet flags = BitSet.valueOf(flagBits);
		maxSizesPresent = flags.get(0);

		if (version == 1) {
			// Skip 5 reserved bytes
			bb.position(bb.position() + 5);
			type = -1;
		} else if (version == 2) {
			type = bb.get();
		} else {
			throw new HdfException("Unreconized version = " + version);
		}

		// Dimensions sizes
		if (numberOfdimensions != 0) {
			dimensions = new int[numberOfdimensions];
			for (int i = 0; i < numberOfdimensions; i++) {
				dimensions[i] = Utils.readBytesAsUnsignedInt(bb, sb.getSizeOfLengths());
			}
		} else {
			dimensions = new int[0];
		}

		// Max dimension sizes
		if (maxSizesPresent) {
			maxSizes = new int[numberOfdimensions];
			for (int i = 0; i < numberOfdimensions; i++) {
				maxSizes[i] = Utils.readBytesAsUnsignedInt(bb, sb.getSizeOfLengths());
			}
		} else {
			maxSizes = new int[0];
		}

		// Calculate the total lentgh by multiplying all dimensions
		totalLentgh = IntStream.of(dimensions)
				.mapToLong(Long::valueOf) // Convert to long to avoid int overflow
				.reduce(1, Math::multiplyExact);

		// Permutation indices - Note never implemented in HDF library!
	}

	public static DataSpace readDataSpace(ByteBuffer bb, Superblock sb) {
		return new DataSpace(bb, sb);
	}

	/**
	 * Gets the total number of elements in this dataspace.
	 * 
	 * @return the total number of elements in this dataspace
	 */
	public long getTotalLentgh() {
		return totalLentgh;
	}

	public int getType() {
		return type;
	}

	public int getVersion() {
		return version;
	}

	public int[] getDimensions() {
		return dimensions;
	}

	public int[] getMaxSizes() {
		return maxSizes;
	}

	public boolean isMaxSizesPresent() {
		return maxSizesPresent;
	}

}
