/*******************************************************************************
 * This file is part of jHDF. A pure Java library for accessing HDF5 files.
 *
 * http://jhdf.io
 *
 * Copyright 2019 James Mudd
 *
 * MIT License see 'LICENSE' file
 ******************************************************************************/
package io.jhdf.filter;

import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.exceptions.HdfFilterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MissingFilterTest {

	private static final String HDF5_TEST_FILE_NAME = "../test_missing_filter.hdf5bad";

	private static HdfFile hdfFile;

	@BeforeAll
	static void setup() {
		String testFileUrl = MissingFilterTest.class.getResource(HDF5_TEST_FILE_NAME).getFile();
		hdfFile = new HdfFile(new File(testFileUrl));
	}

	@Test
	void testMissingFilter() {
		Dataset dataset = hdfFile.getDatasetByPath("/float32");
		assertThat(dataset, is(notNullValue()));
		HdfFilterException exception = assertThrows(HdfFilterException.class, () -> dataset.getData());

		// The missing filter name
		assertThat(exception.getMessage(), containsString("lzf"));
		// The missing filter id
		assertThat(exception.getMessage(), containsString("32000"));
	}
}