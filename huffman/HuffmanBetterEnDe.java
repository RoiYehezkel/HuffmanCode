package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Assignment 1
 * Submitted by: 
 * Student 1: Sofia Naer 	ID# 333815397
 * Student 2: Roi Yehezkel 	ID# 315331959
 */

public class HuffmanBetterEnDe extends HuffmanEncoderDecoder {
	PriorityQueue<Node> minHeapEn;
	PriorityQueue<Node> minHeapDe;
	HashMap<Integer, String> mapEn;
	Node rootEn;
	Node rootDe;

	public HuffmanBetterEnDe() {
		freq = new int[256 * 256];
		minHeapEn = new PriorityQueue<Node>(new MyComparator());
		minHeapDe = new PriorityQueue<Node>(new MyComparator());
		mapEn = new HashMap<Integer, String>();
		rootEn = null;
		rootDe = null;
	}

	public void Compress(String[] input_names, String[] output_names) {
		try {
			File file_in = new File(input_names[0]);
			output = new FileOutputStream(output_names[0]);
			ObjectOutputStream save = new ObjectOutputStream(output);
			byte[] data = readFromFile(file_in, input); // read bytes from file to data
			minHeapEn = freqAnalyze(data, freq, minHeapEn); // analyze the text to frequency of character
			writeSymbol(save, freq, minHeapEn); // write the symbol to file
			rootEn = buildHuffmanTree(minHeapEn);
			createHuffmanCodeDe(rootEn, "", mapEn); // translate huffman tree to huffman code
			encodeDe(save, data, mapEn);
			System.out.println("The file has encoded");
			save.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void Decompress(String[] input_names, String[] output_names) {
		try {
			File file_in = new File(input_names[0]);
			File file_out = new File(output_names[0]);
			input = new FileInputStream(file_in);
			ObjectInputStream readFile = new ObjectInputStream(input);
			output = new FileOutputStream(file_out);
			minHeapDe = readSymbol(readFile, minHeapDe);
			rootDe = buildHuffmanTree(minHeapDe);
			decode(readFile, rootDe, output);
			System.out.println("The file has decoded");
			readFile.close();
			input.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeSymbol(ObjectOutputStream out, int[] f, PriorityQueue<Node> heap) throws IOException {
		int oneByte = 0; // count character with one symbol
		for (int i = 0; i < 256; i++) {
			if (f[i] != 0) {
				oneByte++;
			}
		}
		out.writeInt(oneByte); // write number of character with one symbol
		out.writeInt(heap.size() - oneByte); // write number of character with two symbols
		/*
		 * loop to write all the symbol with two character to file
		 */
		for (int i = 256; i < f.length; i++) {
			if (f[i] != 0) {
				String s = Integer.toBinaryString(i);
				String upper = s.substring(0, s.length() - 8);
				String lower = s.substring(s.length() - 8);
				int up = StringToInt(upper);
				int low = StringToInt(lower);
				out.write((byte) up); // write the right symbol
				out.write((byte) low); // write the left symbol
				out.writeInt(f[i]); // write the frequency
			}
		}
		/*
		 * loop to write all the symbol with one character to file
		 */
		for (int i = 0; i < 256; i++) {
			if (f[i] != 0) {
				out.write((byte) (i - 128)); // write the symbol
				out.writeInt(f[i]); // write the frequency
			}
		}
	}

	public PriorityQueue<Node> freqAnalyze(byte[] d, int[] f, PriorityQueue<Node> heap) {
		/*
		 * count frequency of pairs of character from file to freq
		 */
		for (int i = 0; i < d.length; i++) {
			String s = IntToString(d[i]);
			if (i < d.length - 1) {
				i++;
				s += IntToString(d[i]);
				int p = StringToInt(s);
				f[p]++;
			} else {
				f[(byte) d[i] + 128]++; // if the number of bytes in the file is odd then the last byte we count as a one character
			}
		}
		/*
		 * if the number of frequency of every pair is more than 100 so we give him a node
		 * else we break him to two seperate parts of two character and count them in the 
		 * right place 
		 */
		for (int i = 256; i < f.length; i++) {
			String s = Integer.toBinaryString(i);
			String upper = s.substring(0, s.length() - 8);
			String lower = s.substring(s.length() - 8);
			int up = StringToInt(upper);
			int low = StringToInt(lower);
			if (f[i] > 100) {
				heap.add(new Node((byte) up, (byte) low, f[i]));
			} else {
				while (f[i] > 0) {
					f[(byte) up + 128]++;
					f[(byte) low + 128]++;
					f[i]--;
				}
			}
		}
		/*
		 * create new node for the symbol with only one character
		 */
		for (int i = 0; i < 256; i++) {
			if (f[i] != 0) {
				heap.add(new Node((byte) (i - 128), f[i]));
			}
		}
		// return the min heap
		return heap;
	}

	public void createHuffmanCodeDe(Node root, String s, HashMap<Integer, String> map) {
		if (root.left == null && root.right == null) {
			if (root.ch2 == null) // create huffman code for one character
			{
				map.put((int) root.ch, s);
			} else // create huffman code for two character
			{
				String c1 = IntToString(root.ch);
				String c2 = IntToString(root.ch2);
				int val = StringToInt(c1 + c2);
				map.put(val, s);
			}
			return;
		}
		createHuffmanCodeDe(root.left, s + "1", map);
		createHuffmanCodeDe(root.right, s + "0", map);
	}

	public void encodeDe(ObjectOutputStream out, byte[] d, HashMap<Integer, String> map) throws IOException {
		String encodedData = "";
		for (int i = 0; i < d.length; i++) {
			int key = d[i];
			// we want to read the next byte of data to check if we have symbol for both of them together
			if (i < d.length - 1) {
				String c1 = IntToString(d[i]);
				String c2 = IntToString(d[i + 1]);
				if (map.containsKey(StringToInt(c1 + c2))) {
					if (d[i] != 0) // the combinatin of c1+c2 is greater than 255 so we want skip on bytes in the file
					{
						key = StringToInt(c1 + c2);
						i++;
					}
				}
			}
			encodedData += map.get(key); // get huffman code using hashMap
			if (encodedData.length() > 1000) // write parts of the encoded data to the file
				encodedData = encodeWord(encodedData, out, 8);
		}
		encodedData = encodeWord(encodedData, out, 8);
		if (encodedData.length() > 0) // for the last part of the encoded data
		{
			byte lastByte = (byte) encodedData.length(); // the real size of the last part
			byte newCode = StringToByte(encodedData); // the last part
			out.write(newCode);
			out.write(lastByte); // write the size of the last part
		}
	}

	public int StringToInt(String s) {
		int num = Integer.parseInt(s, 2);
		return num;
	}

	public PriorityQueue<Node> readSymbol(ObjectInputStream in, PriorityQueue<Node> heap) throws IOException {
		int oneByte = in.readInt(); // read the number of symbol with one character
		int twoBytes = in.readInt(); // read the number of symbol with two character
		/*
		 * we read in loop the symbol with two character and there frequency and add them 
		 * as a new node to the min heap
		 */
		for (int i = 0; i < twoBytes; i++) {
			int ch1 = in.read();
			int ch2 = in.read();
			int freq = in.readInt();
			heap.add(new Node((byte) ch1, (byte) ch2, freq));
		}
		/*
		 * we read in loop the symbol with one character and there frequency and add them 
		 * as a new node to the min heap
		 */
		for (int i = 0; i < oneByte; i++) {
			int ch1 = in.read();
			int freq = in.readInt();
			heap.add(new Node((byte) ch1, freq)); // create min heap
		}
		return heap;
	}

	public String findLetter(String s, Node root, FileOutputStream out) throws IOException {
		Node current = root;
		int i = 0;
		/*
		 * move on the huffman tree until we find the letter
		 */
		while (current.left != null && current.right != null) {
			if (s.charAt(i) == '0')
				current = current.right;
			else
				current = current.left;
			i++;
		}
		out.write(current.ch);
		if (current.ch2 != null) // the node have two character
			out.write(current.ch2);
		return s.substring(i);
	}
}