package my.web.apps.db;

import java.io.File;
import java.io.IOException;

public class KafkaOpener {
	public void Open() throws InterruptedException, IOException {
		ProcessBuilder zookeeper = new ProcessBuilder();
		zookeeper.command("cmd", "/c", "start", "zookeeper-server-start.bat", "../../config/zookeeper.properties")
        .directory(new File("C:\\kafka_2.13-2.7.0\\bin\\windows"));
		
		ProcessBuilder broker = new ProcessBuilder();
		broker.command("cmd", "/c", "start", "kafka-server-start.bat", "../../config/server.properties")
        .directory(new File("C:\\kafka_2.13-2.7.0\\bin\\windows"));
		try {
			Process start_zookeeper = zookeeper.start();
			Process start_broker = broker.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}