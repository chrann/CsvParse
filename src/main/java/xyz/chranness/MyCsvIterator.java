/**
 * 
 */
package xyz.chranness;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * @author chranness
 *
 */
public class MyCsvIterator implements Iterator<MyCsvRow>, Iterable<MyCsvRow> {

	private BufferedInputStream bis;
	private MyCsvParser parser;

	public MyCsvIterator(Path file, MyCsvParser parser, Charset charset) throws IOException {
		InputStream is = Files.newInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is);
		bis.mark(3);
		byte[] bytes = new byte[3];
		bis.read(bytes);
		for (byte b : bytes) {
			System.out.println(b);
		}
		if (bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65) {
			// BOM付きUTF8なのでマークを戻さず実行
			charset = StandardCharsets.UTF_8;
		} else {
			bis.reset();
		}
		this.bis = bis;
		this.parser = parser;

	}

	@Override
	public boolean hasNext() {
		bis.mark(0);
		try {
			if (bis.read() == -1) {
				return false;
			}
			bis.reset();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public MyCsvRow next() {
		MyCsvRow row = new MyCsvRow();
		try {
			this.parser.parseLine(bis, row);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return row;
	}

	@Override
	public Iterator<MyCsvRow> iterator() {
		return this;
	}

}
