package xyz.chranness;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.UUID;

public class BigFileMaker {

	public BigFileMaker() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public static void main(String[] args) throws InterruptedException {
		int MAXLINE = 100000000;
		int PROGRESSLINE = 100000000;

		// 環境ごとに書き換えてください。
		Path file = Paths.get("/work/test.csv");

		System.out.println(file.toAbsolutePath().toString());
		System.out.println("エンター入力まで待機します。上記パスに " + MAXLINE + " 行の大容量ファイルを作っては問題がある場合、ここで動作を停止させてください。");
		Scanner scanner = new Scanner(System.in);
		String s = scanner.nextLine();
		// print the next line
		System.out.println("The line entered by the user: " + s);
		scanner.close();

		// ファイルに書き込むため、FileWriterを生成
		try (FileWriter filewriter = new FileWriter(new File(file.toAbsolutePath().toString()))) {
			int i;
			for (i = 0; i < MAXLINE; i++) {
				if (MAXLINE % PROGRESSLINE == 0) {
					System.out.println("" + PROGRESSLINE + " 行完了");
				}
				// ランダム文字列としてUUIDを使用
				String randomStr = UUID.randomUUID().toString();
				// ファイルに改行コード付きでランダム文字列を書き込む。
				filewriter.write(randomStr + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("完了");

	}

}
