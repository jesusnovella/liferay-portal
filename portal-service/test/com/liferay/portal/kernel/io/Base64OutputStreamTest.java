/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.io;

import com.liferay.portal.kernel.test.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * <a href="Base64OutputStreamTest.java.html"><b><i>View Source</i></b></a>
 *
 * @author Tina Tian
 */
public class Base64OutputStreamTest extends TestCase {

	public void testClose() throws Exception {
		File testFile = new File(_testFilePath);

		Base64OutputStream base64OutputStream = new Base64OutputStream(
			new FileOutputStream(testFile));
		base64OutputStream.write('A');
		base64OutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(testFile);
		byte[] bytes = new byte[4];
		fileInputStream.read(bytes);
		fileInputStream.close();

		if ((bytes[3] != '=') || (bytes[2] != '=')) {
			fail("test close() failed");
		}

		testFile.delete();
	}

	public void testFlush() throws Exception {
		File testFile = new File(_testFilePath);

		Base64OutputStream base64OutputStream = new Base64OutputStream(
			new FileOutputStream(testFile));
		base64OutputStream.write('A');
		base64OutputStream.flush();

		FileInputStream fileInputStream = new FileInputStream(testFile);
		byte[] bytes = new byte[4];
		int returnValue = fileInputStream.read(bytes);
		fileInputStream.close();

		assertEquals(4, returnValue);

		testFile.delete();

		testFile = new File(_testFilePath);

		base64OutputStream = new Base64OutputStream(
			new FileOutputStream(testFile));
		base64OutputStream.write('A');
		base64OutputStream.write('B');
		base64OutputStream.flush();

		fileInputStream = new FileInputStream(testFile);
		returnValue = fileInputStream.read(bytes);
		fileInputStream.close();
		assertEquals(4, returnValue);

		testFile.delete();
	}

	public void testWrite_byteArr() throws Exception {
		File testFile = new File(_testFilePath);

		byte[] bytes = {'a', 'b', 'c'};
		Base64OutputStream base64OutputStream = new Base64OutputStream(
			new FileOutputStream(testFile));
		base64OutputStream.write(bytes);
		base64OutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(testFile);
		byte[] bytes = new byte[4];
		int result = fileInputStream.read(bytes);
		fileInputStream.close();

		assertEquals(4, result);

		testFile.delete();
	}

	public void testWrite_int() throws Exception {
		File testFile = new File(_testFilePath);

		Base64OutputStream base64OutputStream = new Base64OutputStream(
			new FileOutputStream(testFile));
		base64OutputStream.write('A');
		base64OutputStream.write('A');
		base64OutputStream.write('A');
		base64OutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(testFile);
		byte[] bytes = new byte[4];
		int returnValue = fileInputStream.read(bytes);
		fileInputStream.close();

		assertEquals(4, returnValue);

		testFile.delete();
	}

	public void testWrite_3args() throws Exception {
		File testFile = new File(_testFilePath);

		byte[] bytes = {'a', 'b', 'c', 'a', 'b', 'c'};
		int offset = 0;
		int length = 1;
		Base64OutputStream base64OutputStream = new Base64OutputStream(
			new FileOutputStream(testFile));
		base64OutputStream.write(bytes, offset, length);

		length = 2;
		offset = 1;
		base64OutputStream.write(bytes, offset, length);

		length = 3;
		offset = 3;
		base64OutputStream.write(bytes, offset, length);

		base64OutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(testFile);
		byte [] bytes = new byte[8];
		int returnValue = fileInputStream.read(bytes);
		fileInputStream.close();

		assertEquals(8, returnValue);

		testFile.delete();
	}

	public void testEncodeUnit_byte() throws Exception {
		File testFile = new File(_testFilePath);

		Base64OutputStream base64OutputStream = new Base64OutputStream(
			new FileOutputStream(testFile));
		base64OutputStream.encodeUnit((byte)'a');
		base64OutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(testFile);
		byte [] bytes = new byte[4];
		int returnValue = fileInputStream.read(bytes);
		fileInputStream.close();

		assertEquals(4, returnValue);

		testFile.delete();

	}

	public void testEncodeUnit_byte_byte() throws Exception {
		File testFile = new File(_testFilePath);

		byte byte1 = 'a';
		byte byte2 = 'b';
		Base64OutputStream base64OutputStream = new Base64OutputStream(
			new FileOutputStream(testFile));
		base64OutputStream.encodeUnit(byte1, byte2);
		base64OutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(testFile);
		byte [] bytes = new byte[4];
		int returnValue = fileInputStream.read(bytes);
		fileInputStream.close();

		assertEquals(4, returnValue);

		testFile.delete();
	}

	public void testEncodeUnit_3args() throws Exception {
		File testFile = new File(_testFilePath);

		byte byte1 = 'a';
		byte byte2 = 'b';
		byte byte3 = 'c';
		Base64OutputStream base64OutputStream = new Base64OutputStream(
			new FileOutputStream(testFile));
		base64OutputStream.encodeUnit(byte1, byte2, byte3);
		base64OutputStream.close();

		FileInputStream fileInputStream = new FileInputStream(testFile);
		byte [] bytes = new byte[4];
		int returnValue = fileInputStream.read(bytes);
		fileInputStream.close();

		assertEquals(4, returnValue);

		testFile.delete();
	}

	public void testGetChar() {
		try {
			File testFile = new File(_testFilePath);

			int sixbit = 0;
			Base64OutputStream base64OutputStream = new Base64OutputStream(
				new FileOutputStream(testFile));

			char expResult = 'A';
			char returnValue = base64OutputStream.getChar(sixbit);
			assertEquals(expResult, returnValue);

			sixbit = 64;
			expResult = '?';
			returnValue = base64OutputStream.getChar(sixbit);
			assertEquals(expResult, returnValue);

			testFile.delete();
		}
		catch (Exception e) {
			fail("Test getChar failed");
		}

	}

	private static String _testFilePath =
		System.getProperty("user.dir") + File.separator + "test.txt";

}