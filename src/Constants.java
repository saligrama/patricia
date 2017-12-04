/*
  All constants are defined here.
  @author Aditya Saligrama
  @since  2017-10-13
*/

public class Constants
{
	// for Utils: converting from string to bit array and vice versa
	public static final int UTILS_CHAR_BITLEN = 5;
	public static final int UTILS_ASCII_UCASE = 65;
	public static final int[] UTILS_BITSET = new int[]{16, 8, 4, 2, 1};

	// for Node
	public static final int NODE_NUM_CHILDREN = 2;

	// for Client
	public static final String CLIENT_ADD_COMMAND = "/ADD";
	public static final String CLIENT_REMOVE_COMMAND = "/REMOVE";
	public static final String CLIENT_FIND_COMMAND = "/FIND";
	public static final String CLIENT_ADD_FILE_COMMAND = "/AFILE";
	public static final String CLIENT_REMOVE_FILE_COMMAND = "/RFILE";
	public static final String CLIENT_PRINT_COMMAND = "/PRINT";
	public static final String CLIENT_PRINT_STAT_COMMAND = "STAT";
	public static final String CLIENT_PRINT_TREE_COMMAND = "TREE";
	public static final String CLIENT_QUIT_COMMAND = "/QUIT";

	// for Logger
	public static final int LOGGER_BYTE_NUM_BITS = 8;
	public static final int LOGGER_JVM_CLASS_HEADER_SIZE_BYTES = 16;
	public static final int LOGGER_JVM_ARRAY_HEADER_SIZE_BYTES = 12;
	public static final int LOGGER_JVM_INT_SIZE_BYTES = 4;
	public static final int LOGGER_JVM_REFERENCE_SIZE_BYTES = 8;
}
