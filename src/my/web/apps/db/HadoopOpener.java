package my.web.apps.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class HadoopOpener {
	public void Open() throws InterruptedException, IOException {
		Process start_hadoop = Runtime.getRuntime().exec(
			        "cmd /c start start-all.cmd",
			        null,
			        new File("C:\\hadoop-3.1.0\\sbin\\"));
	}
}
