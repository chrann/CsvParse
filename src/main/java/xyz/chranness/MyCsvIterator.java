/**
 * 
 */
package xyz.chranness;

import java.io.BufferedReader;
import java.io.IOException;
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

	private BufferedReader br;
	private MyCsvParser parser;

	public MyCsvIterator(Path file, MyCsvParser parser, Charset charset) throws IOException {
		BufferedReader br = Files.newBufferedReader(file, charset);
		this.br = br;
		this.parser = parser;

	}

	public MyCsvIterator(Path file, MyCsvParser parser) throws IOException {
		this(file, parser, StandardCharsets.UTF_8);
	}

	@Override
	public boolean hasNext() {
		try {
			return this.br.ready();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public MyCsvRow next() {
		String str = null;
		MyCsvRow row = new MyCsvRow();
		try {
			str = br.readLine();
			this.parser.parseLine(str, row);
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
