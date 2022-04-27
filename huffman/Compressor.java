package huffman;


public interface Compressor {
	abstract public void Compress(String[] input_names, String[] output_names);

	abstract public void Decompress(String[] input_names, String[] output_names);

	abstract public byte[] CompressWithArray(String[] input_names, String[] output_names);

	abstract public byte[] DecompressWithArray(String[] input_names, String[] output_names);
}