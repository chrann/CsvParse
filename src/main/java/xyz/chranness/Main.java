package xyz.chranness;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
			System.out.println("時刻？");
			long nanoTime = System.nanoTime();
			System.out.println(nanoTime);
			int nano = (int) (nanoTime % 1000);
			long microTime = nanoTime / 1000;
			System.out.println(microTime);
			int micro = (int) (microTime % 1000);
			long milliTime = microTime / 1000;
			System.out.println(milliTime);
			int milli = (int) (milliTime % 1000);
			int secTime = (int) (milliTime / 1000);
			System.out.println(secTime);
			int sec = secTime % 60;
			int min = secTime / 60 % 60;
			int hour = secTime / 60 / 60 % 24;
			int days = secTime / 60 / 60 / 24;
			System.out.println(": " + days + "日 " + hour + "時間 " + min + "分 " + sec + "秒 " + milli + "ミリ秒 " + micro
					+ "マイクロ秒 " + nano + "ナノ秒");

//			System.out.println("エンター入力まで待機します");
//			Scanner scanner = new Scanner(System.in);
//			String s = scanner.nextLine();
//			scanner.close();

		}

		// /work/test.csv                 utf8
		// /work/personal_infomation.csv  utf8
		// /work/zenkoku.csv              sjis? なぜか39415行目からエラーになる
		// /work/zenkokuUTF8.csv          utf8
		// /work/テストデータ - TM-WebTools.csv utf8
		// /work/dummy.cgi                sjis
		// /work/dummy.csv                utf8
		
		
		{
			long startTime = System.nanoTime();

			Path file = Paths.get("/work/dummy.cgi");

			MyCsvIterator it = parser.getLines(file, StandardCharsets.UTF_8);
			int i = 0;
			while(it.hasNext()) {
				i++;
				MyCsvRow row = it.next();
//				System.out.println(row);
			}
			System.out.println(i + "件");
			long endTime = System.nanoTime();
			System.out.println("startTime: " + startTime);
			System.out.println("endTime  : " + endTime);

			long nanoSec = endTime - startTime;
			long microSec = nanoSec / 1000;
			long milliSec = microSec / 1000;
			int sec = (int) (milliSec / 1000);
			int days = (sec / 60 / 60 / 24);
			int hours = (sec / 60 / 60 - days * 24);
			int mins = (sec / 60 - days * 24 * 60 - hours * 60);
			sec = sec % 60;
			System.out.println("経過時間: " + days + "日 " + hours + "時間 " + mins + "分 " + sec + "秒");

		}
		{
			long startTime = System.nanoTime();

			Path file = Paths.get("/work/dummy.cgi");

			CsvMapper mapper = new CsvMapper();

			CsvSchema csvSchema = mapper.schemaFor(String[].class);
			mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

			MappingIterator<String[]> it = mapper.readerFor(String[].class).with(csvSchema).readValues(file.toFile());

			String[] rtn = null;
			int i = 0;
			while (it.hasNext()) {
				i++;
				rtn = it.next();
			}
			System.out.println(i + "件");
			long endTime = System.nanoTime();
			System.out.println("startTime: " + startTime);
			System.out.println("endTime  : " + endTime);

			long nanoSec = endTime - startTime;
			long microSec = nanoSec / 1000;
			long milliSec = microSec / 1000;
			int sec = (int) (milliSec / 1000);
			int days = (sec / 60 / 60 / 24);
			int hours = (sec / 60 / 60 - days * 24);
			int mins = (sec / 60 - days * 24 * 60 - hours * 60);
			sec = sec % 60;
			System.out.println("経過時間: " + days + "日 " + hours + "時間 " + mins + "分 " + sec + "秒");

		}

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
