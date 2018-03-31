/*
 * Copyright (c) 2017-2018 Aditya Saligrama.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package io.github.saligrama.util;

/**
 * @author Aditya Saligrama
 */
class Utils {

	/**
	 * Greedily convert string to contiguous array of 5 bits.
	 *
	 * @param _str string to convert to bit array
	 * @return bit array representation of _str
	 */
	public static int[] strToBitArr(String _str) {
		int[] ret = new int[_str.length() * Constants.UTILS_CHAR_BITLEN];

		for (int i = 0; i < _str.length(); i++) {
			int asciiConvert = ((int) _str.charAt(i));
			int ndx = i * Constants.UTILS_CHAR_BITLEN;
			for (int j = 0; j < Constants.UTILS_CHAR_BITLEN; j++) {
				if (asciiConvert >= Constants.UTILS_BITSET[j]) {
					asciiConvert -= Constants.UTILS_BITSET[j];
					ret[ndx + j] = 1;
				}
			}
		}

		return ret;
	}

	/**
	 * Convert bit array back to string
	 *
	 * @param _bits bit array to convert to string
	 * @return string representation of _bits
	 */
	public static String bitArrToStr(int[] _bits) {
		// bit array should be a concatenation of bit representations of characters of a set length
		assert _bits.length % Constants.UTILS_CHAR_BITLEN == 0;

		String ret = "";
		int next = 0;
		for (int i = 0; i < _bits.length; i++) {
			if (i > 0 && i % Constants.UTILS_CHAR_BITLEN == 0) {
				ret += Character.toString((char) next);
				next = 0;
			}

			if (_bits[i] == 1)
				next += Constants.UTILS_BITSET[i % Constants.UTILS_CHAR_BITLEN];
		}

		// account for last char
		return ret + Character.toString((char) next);
	}
}
