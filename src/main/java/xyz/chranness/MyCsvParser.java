package xyz.chranness;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCsvParser {

	private static Pattern lineSepPat = Pattern.compile("(\\r\\n|[\\r\\n])");

	private static Pattern dQuotePat = Pattern.compile("^\\s*?\"([^\"]*?)\"\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern sQuotePat = Pattern.compile("^\\s*?'([^\"]*?)'\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern boolPat = Pattern.compile("^\\s*?(true|false)\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern nullPat = Pattern.compile("^(\\s*?)(?=(\\r\\n|[,\\r\\n])|$)");
	private static Pattern doublePat = Pattern.compile("^\\s*?(\\d+?\\.\\d+?)\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern longPat = Pattern.compile("^\\s*?(\\d+?)\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern stringPat = Pattern.compile("^\\s*?(\\S+?.*?\\S*?)\\s*?(\\r\\n|[,\\r\\n]|$)");

	/** " から始まって改行コードで中断されるパターン */
	private static Pattern dQInterruptedPat = Pattern.compile("^\\s*?\"([^\"]*?)(\\r\\n|[\\r\\n])");
	/** ' から始まって改行コードで中断されるパターン */
	private static Pattern sQInterruptedPat = Pattern.compile("^\\s*?'([^\"]*?)(\\r\\n|[\\r\\n])");

	private MyCsvIterator it;
	private Charset charset = StandardCharsets.UTF_8;

	private static int MAXBUFFER = 128;

	public MyCsvIterator getLines(Path file) throws IOException {
		it = new MyCsvIterator(file, this, charset);
		return it;
	}

	public MyCsvIterator getLines(Path file, Charset charset) throws IOException {
		this.charset = charset;
		it = new MyCsvIterator(file, this, charset);
		return it;
	}

	public MyCsv parse(Path file) throws IOException {
		BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		String source = sb.toString().trim();
		if ("".equals(source)) {
			return new MyCsv();
		}
		MyCsv csv = new MyCsv();
		MyCsvRow row = new MyCsvRow();
		csv.add(row);
		return parseCore(source, csv, row);
	}

	public MyCsv parse(String source) {
		source = source.trim();
		if ("".equals(source)) {
			return new MyCsv();
		}
		MyCsv csv = new MyCsv();
		MyCsvRow row = new MyCsvRow();
		csv.add(row);
		return parseCore(source, csv, row);
	}

	private MyCsv parseCore(String source, MyCsv csv, MyCsvRow row) {
		if ("".equals(source)) {
			return csv;
		}
		{
			Matcher m = dQuotePat.matcher(source);
			if (m.find()) {
				row.add(m.group(1));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					MyCsvRow newRow = new MyCsvRow();
					csv.add(newRow);
					return parseCore(next, csv, newRow);
				}
				return parseCore(next, csv, row);
			}
		}
		{
			Matcher m = sQuotePat.matcher(source);
			if (m.find()) {
				row.add(m.group(1));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					MyCsvRow newRow = new MyCsvRow();
					csv.add(newRow);
					return parseCore(next, csv, newRow);
				}
				return parseCore(next, csv, row);
			}
		}
		{
			Matcher m = boolPat.matcher(source);
			if (m.find()) {
				row.add(Boolean.parseBoolean(m.group(1)));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					MyCsvRow newRow = new MyCsvRow();
					csv.add(newRow);
					return parseCore(next, csv, newRow);
				}
				return parseCore(next, csv, row);
			}
		}
		{
			Matcher m = nullPat.matcher(source);
			if (m.find()) {
				row.add(null);
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					MyCsvRow newRow = new MyCsvRow();
					csv.add(newRow);
					return parseCore(next, csv, newRow);
				}
				return parseCore(next, csv, row);
			}
		}
		{
			Matcher m = doublePat.matcher(source);
			if (m.find()) {
				row.add(Double.parseDouble(m.group(1)));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					MyCsvRow newRow = new MyCsvRow();
					csv.add(newRow);
					return parseCore(next, csv, newRow);
				}
				return parseCore(next, csv, row);
			}
		}
		{
			Matcher m = longPat.matcher(source);
			if (m.find()) {
				row.add(Long.parseLong(m.group(1)));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					MyCsvRow newRow = new MyCsvRow();
					csv.add(newRow);
					return parseCore(next, csv, newRow);
				}
				return parseCore(next, csv, row);
			}
		}
		{
			Matcher m = stringPat.matcher(source);
			if (m.find()) {
				row.add(m.group(1));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					MyCsvRow newRow = new MyCsvRow();
					csv.add(newRow);
					return parseCore(next, csv, newRow);
				}
				return parseCore(next, csv, row);
			}
		}
		row.add(source);
		return csv;
	}

	public MyCsvRow parseLine(String source, MyCsvRow row) {
		if (source == null || "".equals(source)) {
			return row;
		}
		{
			Matcher m = dQInterruptedPat.matcher(source);
			if (m.find()) {
				// クォートが途切れる中途半端な行だった場合、次の行を確認
				if (this.it.hasNext()) {
					// 存在すれば、次の行を取得してsourceに付加して続行
					String next = source + it.next();
					return parseLine(next, row);
				}
				// 存在しなければ、全体を文字列として処理し、なおかつ行末なので返す
				row.add(source);
				return row;
			}
		}
		{
			Matcher m = sQInterruptedPat.matcher(source);
			if (m.find()) {
				// クォートが途切れる中途半端な行だった場合、次の行を確認
				if (this.it.hasNext()) {
					// 存在すれば、次の行を取得してsourceに付加して続行
					String next = source + it.next();
					return parseLine(next, row);
				}
				// 存在しなければ、全体を文字列として処理し、なおかつ行末なので返す
				row.add(source);
				return row;
			}
		}
		{
			Matcher m = dQuotePat.matcher(source);
			if (m.find()) {
				row.add(m.group(1));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					return row;
				}
				return parseLine(next, row);
			}
		}
		{
			Matcher m = sQuotePat.matcher(source);
			if (m.find()) {
				row.add(m.group(1));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					return row;
				}
				return parseLine(next, row);
			}
		}
		{
			Matcher m = boolPat.matcher(source);
			if (m.find()) {
				row.add(Boolean.parseBoolean(m.group(1)));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					return row;
				}
				return parseLine(next, row);
			}
		}
		{
			Matcher m = nullPat.matcher(source);
			if (m.find()) {
				row.add(null);
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					return row;
				}
				return parseLine(next, row);
			}
		}
		{
			Matcher m = doublePat.matcher(source);
			if (m.find()) {
				row.add(Double.parseDouble(m.group(1)));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					return row;
				}
				return parseLine(next, row);
			}
		}
		{
			Matcher m = longPat.matcher(source);
			if (m.find()) {
				row.add(Long.parseLong(m.group(1)));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					return row;
				}
				return parseLine(next, row);
			}
		}
		{
			Matcher m = stringPat.matcher(source);
			if (m.find()) {
				row.add(m.group(1));
				String next = source.substring(m.end(2));

				Matcher lM = lineSepPat.matcher(m.group(2));
				if (lM.find()) {
					return row;
				}
				return parseLine(next, row);
			}
		}
		row.add(source);
		return row;
	}

	public void parseLine(BufferedInputStream bis, MyCsvRow row) throws IOException {
		ByteBuffer[] buff = { ByteBuffer.allocate(MAXBUFFER) };
		parseLineNormal(bis, row, buff);
	}

	/**
	 * パース行頭や、カンマの後にまだ何も来ていない時に呼ばれる
	 * 
	 * @param bis
	 * @param row
	 * @param buff
	 * @throws IOException
	 */
	private void parseLineNormal(BufferedInputStream bis, MyCsvRow row, ByteBuffer[] buff) throws IOException {
		byte b = (byte) bis.read();

		// 最初にどれだけスペースがあっても無視
		while (isSpace(b)) {
			b = (byte) bis.read();
		}

		// いきなり終了するので、今の値はnull
		if (b == -1 || b == '\n') {
			row.add(null);
			return;
		}
		// いきなり終了するので、今の値はnull。キャリッジリターンの後にラインフィードがあるかチェック
		if (b == '\r') {
			row.add(null);
			bis.mark(1);
			if ((byte) bis.read() == '\n') {
				return;
			}
			bis.reset();
			return;
		}

		// いきなりカンマがあるので、今の値はnull。次の文字へ
		if (b == ',') {
			row.add(null);
			parseLineNormal(bis, row, buff);
			return;
		}

		// tから始まるので true を疑って調査。違えばany処理へ
		if (b == 't' || b == 'T') {
			putInBuff(b, buff);
			putInBuff(b = (byte) bis.read(), buff);
			if (b != 'r' && b != 'R') {
				parseLineAnyChar(bis, row, buff);
				return;
			}
			putInBuff(b = (byte) bis.read(), buff);
			if (b != 'u' && b != 'U') {
				parseLineAnyChar(bis, row, buff);
				return;
			}
			putInBuff(b = (byte) bis.read(), buff);
			if (b != 'e' && b != 'E') {
				parseLineAnyChar(bis, row, buff);
				return;
			}
			putInBuff(b = (byte) bis.read(), buff);
			while (isSpace(b)) {
				putInBuff(b = (byte) bis.read(), buff);
			}
			// trueの後、次に来た空白以外の文字が ,\r\n\0 のいずれでもないならany処理
			if (b == -1 || b == '\n') {
				row.add(true);
				return;
			}
			if (b == '\r') {
				row.add(true);
				bis.mark(1);
				if ((byte) bis.read() == '\n') {
					return;
				}
				bis.reset();
				return;
			}
			if (b == ',') {
				row.add(true);
				buff[0].clear();
				parseLineNormal(bis, row, buff);
				return;
			}
			parseLineAnyChar(bis, row, buff);
			return;
		}

		// fから始まるので false を疑って調査。違えばany処理へ
		if (b == 'f' || b == 'F') {
			putInBuff(b, buff);
			putInBuff(b = (byte) bis.read(), buff);
			if (b != 'a' && b != 'A') {
				parseLineAnyChar(bis, row, buff);
				return;
			}
			putInBuff(b = (byte) bis.read(), buff);
			if (b != 'l' && b != 'L') {
				parseLineAnyChar(bis, row, buff);
				return;
			}
			putInBuff(b = (byte) bis.read(), buff);
			if (b != 's' && b != 'S') {
				parseLineAnyChar(bis, row, buff);
				return;
			}
			putInBuff(b = (byte) bis.read(), buff);
			if (b != 'e' && b != 'E') {
				parseLineAnyChar(bis, row, buff);
				return;
			}
			putInBuff(b = (byte) bis.read(), buff);
			while (isSpace(b)) {
				putInBuff(b = (byte) bis.read(), buff);
			}
			// falseの後、次に来た空白以外の文字が ,\r\n\0 のいずれでもないならany処理
			if (b == -1 || b == '\n') {
				row.add(false);
				return;
			}
			if (b == '\r') {
				row.add(false);
				bis.mark(1);
				if ((byte) bis.read() == '\n') {
					return;
				}
				bis.reset();
				return;
			}
			if (b == ',') {
				row.add(false);
				buff[0].clear();
				parseLineNormal(bis, row, buff);
				return;
			}
			parseLineAnyChar(bis, row, buff);
			return;
		}

		// 数字から始まるので 整数 or 小数 を疑って調査。違えばany
		if (isNumber(b)) {
			putInBuff(b, buff);
			putInBuff(b = (byte) bis.read(), buff);
			while (isNumber(b)) {
				putInBuff(b = (byte) bis.read(), buff);
			}
			// 数字以外の文字が ,\r\n\0 なら 整数 。 . なら小数疑い。 空白 なら整数疑い。いずれでもないならany処理
			if (b == -1 || b == '\n') {
				String str = buff2String(buff[0]);
				row.add(Long.parseLong(str.substring(0, str.length() - 1)));
				return;
			}
			if (b == '\r') {
				String str = buff2String(buff[0]);
				row.add(Long.parseLong(str.substring(0, str.length() - 1)));
				bis.mark(1);
				if ((byte) bis.read() == '\n') {
					return;
				}
				bis.reset();
				return;
			}
			if (b == ',') {
				String str = buff2String(buff[0]);
				row.add(Long.parseLong(str.substring(0, str.length() - 1)));
				parseLineNormal(bis, row, buff);
				return;
			}
			if (b == '.') {
				// 小数疑い
				putInBuff(b = (byte) bis.read(), buff);
				while (isNumber(b)) {
					putInBuff(b = (byte) bis.read(), buff);
				}
				// 数字以外の文字が ,\r\n\0 なら 小数 。 空白 なら小数疑い。いずれでもないならany処理
				if (b == -1 || b == '\n') {
					String str = buff2String(buff[0]);
					row.add(Double.parseDouble(str.substring(0, str.length() - 1)));
					return;
				}
				if (b == '\r') {
					String str = buff2String(buff[0]);
					row.add(Double.parseDouble(str.substring(0, str.length() - 1)));
					bis.mark(1);
					if ((byte) bis.read() == '\n') {
						return;
					}
					bis.reset();
					return;
				}
				if (b == ',') {
					String str = buff2String(buff[0]);
					row.add(Double.parseDouble(str.substring(0, str.length() - 1)));
					parseLineNormal(bis, row, buff);
					return;
				}
				if (isSpace(b)) {
					// 小数疑い
					putInBuff(b = (byte) bis.read(), buff);
					while (isSpace(b)) {
						putInBuff(b = (byte) bis.read(), buff);
					}
					// 小数の後、次に来た空白以外の文字が ,\r\n\0 のいずれでもないならany処理
					if (b == -1 || b == '\n') {
						String str = buff2String(buff[0]);
						row.add(Double.parseDouble(str.substring(0, str.length() - 1)));
						return;
					}
					if (b == '\r') {
						String str = buff2String(buff[0]);
						row.add(Double.parseDouble(str.substring(0, str.length() - 1)));
						bis.mark(1);
						if ((byte) bis.read() == '\n') {
							return;
						}
						bis.reset();
						return;
					}
					if (b == ',') {
						String str = buff2String(buff[0]);
						row.add(Double.parseDouble(str.substring(0, str.length() - 1)));
						parseLineNormal(bis, row, buff);
						return;
					}
					parseLineAnyChar(bis, row, buff);
					return;
				}
				parseLineAnyChar(bis, row, buff);
				return;
			}
			if (isSpace(b)) {
				// 整数疑い
				putInBuff(b = (byte) bis.read(), buff);
				while (isSpace(b)) {
					putInBuff(b = (byte) bis.read(), buff);
				}
				// 整数の後、次に来た空白以外の文字が ,\r\n\0 のいずれでもないならany処理
				if (b == -1 || b == '\n') {
					String str = buff2String(buff[0]);
					row.add(Long.parseLong(str.substring(0, str.length() - 1)));
					return;
				}
				if (b == '\r') {
					String str = buff2String(buff[0]);
					row.add(Long.parseLong(str.substring(0, str.length() - 1)));
					bis.mark(1);
					if ((byte) bis.read() == '\n') {
						return;
					}
					bis.reset();
					return;
				}
				if (b == ',') {
					String str = buff2String(buff[0]);
					row.add(Long.parseLong(str.substring(0, str.length() - 1)));
					parseLineNormal(bis, row, buff);
					return;
				}
				parseLineAnyChar(bis, row, buff);
				return;
			}
			parseLineAnyChar(bis, row, buff);
			return;
		}

		// ダブルクォートから始まるので、ダブルクォート文字列疑い。
		if (b == '"') {
			putInBuff(b, buff);
			putInBuff(b = (byte) bis.read(), buff);
			// エスケープ文字が来たら後の文字を取得
			if (b == '\\') {
				putInBuff((byte) bis.read(), buff);
			}
			// ダブルクォート以外ならずっとループ
			while (b != '"') {
				// ただし終了文字を除く
				if (b == -1) {
					String str = buff2String(buff[0]);
					str = str.substring(1, str.length() - 1);
					row.add(str);
					return;
				}
				// エスケープ文字が来たら後の文字を取得
				if (b == '\\') {
					putInBuff((byte) bis.read(), buff);
				}
				putInBuff(b = (byte) bis.read(), buff);
			}
			// ダブルクォートが来た
			putInBuff(b = (byte) bis.read(), buff);
			while (isSpace(b)) {
				putInBuff(b = (byte) bis.read(), buff);
			}
			// ダブルクォートの後、次に来た空白以外の文字が ,\r\n\0 のいずれでもないならany処理
			if (b == -1 || b == '\n') {
				String str = buff2String(buff[0]);
				str = str.substring(0, str.length() - 1).trim();
				str = str.substring(1, str.length() - 1);
				row.add(str);
				return;
			}
			if (b == '\r') {
				String str = buff2String(buff[0]);
				str = str.substring(0, str.length() - 1).trim();
				str = str.substring(1, str.length() - 1);
				row.add(str);
				bis.mark(1);
				if ((byte) bis.read() == '\n') {
					return;
				}
				bis.reset();
				return;
			}
			if (b == ',') {
				String str = buff2String(buff[0]);
				str = str.substring(0, str.length() - 1).trim();
				str = str.substring(1, str.length() - 1);
				row.add(str);
				parseLineNormal(bis, row, buff);
				return;
			}
			parseLineAnyChar(bis, row, buff);
			return;
		}

		// シングルクォートから始まるので、ダブルクォート文字列疑い。
		if (b == '\'') {
			putInBuff(b, buff);
			putInBuff(b = (byte) bis.read(), buff);
			// エスケープ文字が来たら後の文字を取得
			if (b == '\\') {
				putInBuff((byte) bis.read(), buff);
			}
			// シングルクォート以外ならずっとループ
			while (b != '\'') {
				// ただし終了文字を除く
				if (b == -1) {
					String str = buff2String(buff[0]);
					str = str.substring(1, str.length() - 1);
					row.add(str);
					return;
				}
				// エスケープ文字が来たら後の文字を取得
				if (b == '\\') {
					putInBuff((byte) bis.read(), buff);
				}
				putInBuff(b = (byte) bis.read(), buff);
			}
			// シングルクォートが来た
			putInBuff(b = (byte) bis.read(), buff);
			while (isSpace(b)) {
				putInBuff(b = (byte) bis.read(), buff);
			}
			// シングルクォートの後、次に来た空白以外の文字が ,\r\n\0 のいずれでもないならany処理
			if (b == -1 || b == '\n') {
				String str = buff2String(buff[0]);
				str = str.substring(0, str.length() - 1).trim();
				str = str.substring(1, str.length() - 1);
				row.add(str);
				return;
			}
			if (b == '\r') {
				String str = buff2String(buff[0]);
				str = str.substring(0, str.length() - 1).trim();
				str = str.substring(1, str.length() - 1);
				row.add(str);
				bis.mark(1);
				if ((byte) bis.read() == '\n') {
					return;
				}
				bis.reset();
				return;
			}
			if (b == ',') {
				String str = buff2String(buff[0]);
				str = str.substring(0, str.length() - 1).trim();
				str = str.substring(1, str.length() - 1);
				row.add(str);
				parseLineNormal(bis, row, buff);
				return;
			}
			parseLineAnyChar(bis, row, buff);
			return;
		}

		// 空白、数字、クォート、false / true のいずれでもない何かのため、汎用文字列処理に入る
		putInBuff(b, buff);
		parseLineAnyChar(bis, row, buff);
		return;
	}

	/**
	 * 数字、クォート、false / true のいずれでもない何かが入った場合の処理
	 * 
	 * @param bis
	 * @param row
	 * @param stringBuilder
	 * @throws IOException
	 */
	private void parseLineAnyChar(BufferedInputStream bis, MyCsvRow row, ByteBuffer[] buff) throws IOException {
		byte b = (byte) bis.read();

		// いきなり終了
		if (b == -1 || b == '\n') {
			row.add(buff2String(buff[0]));
			return;
		}
		// いきなり終了。キャリッジリターンの後にラインフィードがあるかチェック
		if (b == '\r') {
			row.add(buff2String(buff[0]));
			bis.mark(1);
			if ((byte) bis.read() == '\n') {
				return;
			}
			bis.reset();
			return;
		}

		// いきなりカンマなら次の通常処理へ
		if (b == ',') {
			row.add(buff2String(buff[0]));
			parseLineNormal(bis, row, buff);
			return;
		}

		putInBuff(b, buff);
		// ,\r\n\0 以外ならずっとループ
		while (b != ',' && b != '\r' && b != '\n' && b != -1) {
			// エスケープ文字が来たら( \0 以外なら)後の文字を取得
			if (b == '\\') {
				if ((b = (byte) bis.read()) == -1) {
					row.add(buff2String(buff[0]));
					return;
				}
				putInBuff(b, buff);
			}
			putInBuff(b = (byte) bis.read(), buff);
		}
		// 終端文字のいずれかが来た
		String str = buff2String(buff[0]);
		str = str.substring(0, str.length() - 1);
		row.add(str);
		if (b == '\r') {
			bis.mark(1);
			if ((byte) bis.read() == '\n') {
				return;
			}
			bis.reset();
		}
		if (b == ',') {
			parseLineNormal(bis, row, buff);
		}
		return;
	}

	private boolean isSpace(int b) {
		return b == ' ' || b == '	';
	}

	private boolean isNumber(int b) {
		return b >= '0' && b <= '9';
	}

	private String buff2String(ByteBuffer buff) {
		buff.flip();
		byte[] bytes = new byte[buff.remaining()];
		buff.get(bytes);
		buff.clear();
		return new String(bytes, charset);
	}

	private void putInBuff(byte b, ByteBuffer[] buff) {
		if (buff[0].hasRemaining()) {
			buff[0].put(b);
			return;
		}
		ByteBuffer taihi = buff[0].duplicate();
		MAXBUFFER *= 2;
		buff[0] = ByteBuffer.allocate(MAXBUFFER);
		taihi.flip();
		buff[0].put(taihi.array());
		buff[0].put(b);
	}
}
