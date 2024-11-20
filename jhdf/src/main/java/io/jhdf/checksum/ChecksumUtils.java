/*
 * This file is part of jHDF. A pure Java library for accessing HDF5 files.
 *
 * https://jhdf.io
 *
 * Copyright (c) 2024 James Mudd
 *
 * MIT License see 'LICENSE' file
 */
package io.jhdf.checksum;

import io.jhdf.exceptions.HdfChecksumMismatchException;

import java.nio.ByteBuffer;

public final class ChecksumUtils {

	private ChecksumUtils() {
		throw new AssertionError("No instances of ChecksumUtils");
	}

	/**
	 * Checks the last 4 bytes of the buffer are the the Jenkins Lookup 3 Checksum of the rest of the buffer.
	 *
	 * @param buffer the buffer to check
	 * @throws HdfChecksumMismatchException if the checksum is incorrect.
	 */
	public static void validateChecksum(ByteBuffer buffer) {
		int bytesToRead = buffer.limit() - 4 - buffer.position();
		byte[] bytes = new byte[bytesToRead];
		buffer.get(bytes);
		int calculatedChecksum = checksum(bytes);
		int storedChecksum = buffer.getInt();
		if (calculatedChecksum != storedChecksum) {
			throw new HdfChecksumMismatchException(storedChecksum, calculatedChecksum);
		}
	}

	/**
	 * Checks the next 4 bytes of the buffer are the the Jenkins Lookup 3 Checksum of the bytes between the mark and the
	 * current position. The buffer position and mark are moved to after the checksum.
	 *
	 * @param buffer the buffer to check
	 * @throws HdfChecksumMismatchException if the checksum is incorrect.
	 */
	public static void validateChecksumFromMark(ByteBuffer buffer) {
		int position = buffer.position();
		int end = buffer.reset().position(); // Move the buffer to the mark and get the new position
		byte[] bytes = new byte[position - end];
		buffer.get(bytes);
		int calculatedChecksum = checksum(bytes);
		int storedChecksum = buffer.getInt();
		if (calculatedChecksum != storedChecksum) {
			throw new HdfChecksumMismatchException(storedChecksum, calculatedChecksum);
		}
		buffer.mark();
	}

	public static int checksum(ByteBuffer buffer) {
		return JenkinsLookup3HashLittle.hash(buffer);
	}

	public static int checksum(byte[] bytes) {
		return JenkinsLookup3HashLittle.hash(bytes);
	}

}
