/*
	Provides functionality to convert a string to a bit array for storage in the PatriciaTree and vice versa.
	@author Aditya Saligrama
	@since  2017-11-04
*/

public class Utils
{
	// greedily convert string to contiguous array of 5 bits
	public static int[] strToBitArr (String _str)
	{
		int[] ret = new int[_str.length() * Constants.UTILS_CHAR_BITLEN];

		for (int i = 0; i < _str.length(); i++) {
			int asciiConvert = ((int) Character.toUpperCase(_str.charAt(i))) - Constants.UTILS_ASCII_UCASE;
			int ndx = i * Constants.UTILS_CHAR_BITLEN;
			for (int j = 0; j < Constants.UTILS_BITSET.length; j++) {
				if (asciiConvert >= Constants.UTILS_BITSET[j]) {
					asciiConvert -= Constants.UTILS_BITSET[j];
					ret[ndx + j] = 1;
				}
			}
		}

		return ret;
	}

	// convert bit array back to string
	public static String bitArrToStr (int[] _bits)
	{
		// bit array should be a concatenation of 5-bit types
		assert _bits.length % 5 == 0;

		String ret = "";
		int next = 0;
		for (int i = 0; i < _bits.length; i++) {
			if (i > 0 && i % 5 == 0) {
				ret += Character.toString((char) (next + Constants.UTILS_ASCII_UCASE));
				next = 0;
			}

			if (_bits[i] == 1)
				next += Constants.UTILS_BITSET[i % 5];
		}

		// account for last char
		return ret + Character.toString((char) (next + Constants.UTILS_ASCII_UCASE));
	}
}
