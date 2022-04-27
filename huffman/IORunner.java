package huffman;

import java.io.IOException;

/**
 * Assignment 1
 * Submitted by: 
 * Student 1: Sofia Naer 	ID# 333815397
 * Student 2: Roi Yehezkel 	ID# 315331959
 */
public class IORunner {

	static String IN_FILE_PATH = "Red_Flowers.bmp";
	static String ENCODED_FILE = "encoded.txt";
	static String DECODED_FILE = "decoded.bmp";

	public static void main(String[] args) throws IOException {
		String[] input = new String[5];
		String[] output = new String[5];
		input[0] = IN_FILE_PATH;
		output[0] = ENCODED_FILE;
		HuffmanEncoderDecoder test = new HuffmanEncoderDecoder();
		test.Compress(input, output);
		String[] input1 = new String[5];
		input1[0] = DECODED_FILE;
		test.Decompress(output, input1);
		/*HuffmanBetterEnDe test1=new HuffmanBetterEnDe();
		String[] input = new String[5];
		String[] output = new String[5];
		input[0] = IN_FILE_PATH;
		output[0] = ENCODED_FILE;
		test1.Compress(input, output);
		String[] input1 = new String[5];
		input1[0] = DECODED_FILE;
		test1.Decompress(output, input1);*/

	}
}
