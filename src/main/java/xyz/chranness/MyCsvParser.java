package xyz.chranness;

import java.io.BufferedReader;
import java.io.IOException;
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

	public MyCsvIterator getLines(Path file) throws IOException {
		it = new MyCsvIterator(file, this);
		return it;
	}

	public MyCsvIterator getLines(Path file, Charset charset) throws IOException {
		it = new MyCsvIterator(file, this, charset);
		return it;
	}

	public MyCsv parse(Path file) throws IOException {
		BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line = br.readLine()) != null) {
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

}
