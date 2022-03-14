package my.web.apps.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class HadoopCloser {
	public void Stop() throws InterruptedException, IOException {
		Process stop_hadoop = Runtime.getRuntime().exec(
			        "cmd /c start stop-all.cmd",
			        null,
			        new File("C:\\hadoop-3.1.0\\sbin\\"));
	}
}
