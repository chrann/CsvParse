package xyz.chranness;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class Main {

	private static String crlf = System.lineSeparator();

	public static void main(String[] args) throws URISyntaxException, IOException {
		MyCsvParser parser = new MyCsvParser();

		{
			Path file = Paths.get(Main.class.getResource("/test2.csv").toURI());

			CsvMapper mapper = new CsvMapper();

			CsvSchema csvSchema = mapper.schemaFor(String[].class);
	        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

			MappingIterator<String[]> it = mapper.readerFor(String[].class).with(csvSchema)
					.readValues(file.toFile());

			String[] rtn = null;
			while (it.hasNext()) {
				rtn = it.next();
				StringBuilder sb = new StringBuilder();
				for (String str : rtn) {
					sb.append(str).append(",");
				}
				System.out.println(sb.toString());
			}

		}
////		{
//			// 環境ごとに書き換えてください。
//			//			System.out.println(file.toAbsolutePath().toString());
//			// ファイルに書き込むため、FileWriterを生成
//			try(FileWriter filewriter = new FileWriter(new File(file.toAbsolutePath().toString()))) {
//			    int i;
//			    for (i = 0; i < 100000000; i++) {
//			        // ランダム文字列としてUUIDを使用
//			        String randomStr = UUID.randomUUID().toString();
//			        // ファイルに改行コード付きでランダム文字列を書き込む。
//			        filewriter.write(randomStr + "\n");
//			    }
//			} catch (Exception e) {
//			    e.printStackTrace();
//			}
//			System.out.println("完了");
//		}
//		System.exit(0);

		{
			Path file = Paths.get(Main.class.getResource("/test2.csv").toURI());
			MyCsv csv = parser.parse(file);
			System.out.println(csv);
		}
		{
			MyCsv csv = parser.parse("abc");
			System.out.println(csv);
		}
		{
			MyCsv csv = parser.parse("  123  , 	 " + crlf + "999.1230" + crlf + "true ,   false " + crlf + "a,b" + crlf
					+ "'',''b,\"abc\",\"");
			System.out.println(csv);
		}

	}

}
