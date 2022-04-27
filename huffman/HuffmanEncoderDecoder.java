package huffman;


/**
 * Assignment 1
 * Submitted by: 
 * Student 1: Sofia Naer 	ID# 333815397
 * Student 2: Roi Yehezkel 	ID# 315331959
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanEncoderDecoder implements Compressor {
	PriorityQueue<Node> minHeapEn;
	PriorityQueue<Node> minHeapDe;
	HashMap<Byte, String> mapEn;
	int[] freq;
	FileInputStream input;
	FileOutputStream output;
	Node rootEn;
	Node rootDe;

	public HuffmanEncoderDecoder() {
		minHeapEn = new PriorityQueue<Node>(new MyComparator());
		minHeapDe = new PriorityQueue<Node>(new MyComparator());
		mapEn = new HashMap<Byte, String>();
		freq = new int[256];
		rootEn = null;
		rootDe = null;
	}

	@Override
	public void Compress(String[] input_names, String[] output_names) {
		try {
			File file_in = new File(input_names[0]);
			output = new FileOutputStream(output_names[0]);
			ObjectOutputStream save = new ObjectOutputStream(output);
			byte[] data = readFromFile(file_in, input); // read bytes from file to data
			buildHeap(data, minHeapEn);
			writeSymbol(save, freq, minHeapEn); // write to file symbol and frequency
			rootEn = buildHuffmanTree(minHeapEn);
			createHuffmanCode(rootEn, "", mapEn); // translate huffman tree to huffman code
			encode(save, data, mapEn);
			System.out.println("The file has encoded");
			save.close();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] readFromFile(File file, FileInputStream input) throws IOException {
		input = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		input.read(data);
		input.close();
		return data;
	}

	public void writeSymbol(ObjectOutputStream out, int[] f, PriorityQueue<Node> heap) throws IOException {
		out.writeInt(heap.size()); // how many symbol there are
		for (int i = 0; i < f.length; i++) {
			if (f[i] != 0) {
				out.write(i - 128); // write the symbol
				out.writeInt(f[i]); // write the frequency
			}
		}
	}

	public PriorityQueue<Node> buildHeap(byte[] data, PriorityQueue<Node> heap) throws UnsupportedEncodingException {
		for (int i = 0; i < data.length; i++) // analyze frequency
			freq[data[i] + 128]++; // to avoid negative number
		for (int i = 0; i < freq.length; i++) {
			if (freq[i] != 0) {
				heap.add(new Node((byte) (i - 128), freq[i])); // build min heap
			}
		}
		return heap;
	}

	public Node buildHuffmanTree(PriorityQueue<Node> heap) {
		// build the huffman tree by huffman rules
		while (heap.size() != 1) {
			Node left = heap.poll();
			Node right = heap.poll();
			Node f = new Node(left.freq + right.freq, left, right);
			heap.add(f);
		}
		Node root = heap.poll();
		return root;
	}

	public void createHuffmanCode(Node root, String s, HashMap<Byte, String> map) {
		if (root.left == null && root.right == null) {
			map.put(root.ch, s);
			return;
		}
		createHuffmanCode(root.left, s + "1", map);
		createHuffmanCode(root.right, s + "0", map);
	}

	public void encode(ObjectOutputStream out, byte[] d, HashMap<Byte, String> map) throws IOException {
		String encodedData = "";
		for (int i = 0; i < d.length; i++) {
			encodedData += map.get((byte) d[i]); // get huffman code using hashMap
			if (encodedData.length() > 1000) // write parts of the encoded data to the file
			{
				encodedData = encodeWord(encodedData, out, 8);
			}
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

	public String encodeWord(String data, ObjectOutputStream out, int end) throws IOException {
		while (data.length() > end) {
			String word = data.substring(0, 8); // get one byte of encoded data
			data = data.substring(8);
			byte newCode = StringToByte(word); // convert the value of word to byte
			out.write(newCode);
		}
		return data;
	}

	public byte StringToByte(String s) {
		int num = Integer.parseInt(s, 2);
		byte newCode = (byte) num;
		return newCode;
	}

	public PriorityQueue<Node> readSymbol(ObjectInputStream in, PriorityQueue<Node> heap) throws IOException {
		int x = in.readInt(); // read how many symbol there are
		int symbol = x;
		for (int i = 0; i < symbol; i++) {
			int ch = in.read();
			int freq = in.readInt();
			heap.add(new Node((byte) (ch), freq)); // create min heap
		}
		return heap;
	}

	public String IntToString(int val) {
		String data = Integer.toBinaryString(val);
		if (val < 0)
			data = data.substring(data.length() - 8, data.length());
		else
			while (data.length() < 8)
				data = "0" + data;
		// return length of 8 bits in data
		return data;
	}

	public String findLetter(String s, Node root, FileOutputStream out) throws IOException {
		Node current = root;
		int i = 0;
		/*
		 * we move on the huffman tree until we found the real letter
		 */
		while (current.left != null && current.right != null) {
			if (s.charAt(i) == '0')
				current = current.right;
			else
				current = current.left;
			i++;
		}
		out.write(current.ch);
		return s.substring(i);
	}

	public void decode(ObjectInputStream in, Node root, FileOutputStream out) throws IOException {
		String decodedData = "";
		while (true) {
			int x = in.read();
			if (x != -1) // -1 is EOF
			{
				decodedData += IntToString(x);
				if (decodedData.length() > 1000) {
					while (decodedData.length() > 100)
						decodedData = findLetter(decodedData, root, out);
				}
			} else {
				break;
			}
		}
		String lenOfLastByte = decodedData.substring(decodedData.length() - 8); // get the size of the last part
		int lastByte = StringToByte(lenOfLastByte);
		// delete the extra bits in the last part of data
		String temp1 = decodedData.substring(0, decodedData.length() - 16);
		String temp2 = decodedData.substring(decodedData.length() - 8 - lastByte, decodedData.length() - 8);
		decodedData = temp1 + temp2;
		while (decodedData.length() != 0)
			decodedData = findLetter(decodedData, root, out);
	}

	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

}
