package xyz.chranness;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCsvParser {

	private static String lineSep = System.lineSeparator();

	private static Pattern lineSepPat = Pattern.compile("(\\r\\n|[\\r\\n])");

	private static Pattern dQuotePat = Pattern.compile("^\\s*?\"([^\"]*?)\"\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern sQuotePat = Pattern.compile("^\\s*?'([^\"]*?)'\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern boolPat = Pattern.compile("^\\s*?(true|false)\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern nullPat = Pattern.compile("^(\\s*?)(?=(\\r\\n|[,\\r\\n])|$)");
	private static Pattern doublePat = Pattern.compile("^\\s*?(\\d+?\\.\\d+?)\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern longPat = Pattern.compile("^\\s*?(\\d+?)\\s*?(\\r\\n|[,\\r\\n]|$)");
	private static Pattern stringPat = Pattern.compile("^\\s*?(\\S+?.*?\\S*?)\\s*?(\\r\\n|[,\\r\\n]|$)");

	public MyCsv parse(Path file) {
		try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			StringBuilder sb = new StringBuilder();
			String line;
			int i = -1;
			while ((line = br.readLine()) != null) {
				i++;
				if (i != 0) {
					sb.append(lineSep);
				}
				sb.append(line);
			}

			return parse(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new MyCsv();
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

}
