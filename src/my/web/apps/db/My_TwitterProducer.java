package my.web.apps.db;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class My_TwitterProducer {

	public static void RunProducer(String keyword1, String keyword2, String keyword3, int tweetsNumber) {
		Properties props = new Properties();
		props.put("bootstrap.servers","localhost:9092");
		props.put("acks","all");
		props.put("retries",0);
		props.put("batch.size",16384);
		props.put("linger.ms",1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
		
		Producer<String, String> producer = null;
		
		try
		{
			producer = new KafkaProducer<>(props);
			String consumerKey = "esjhzgNOweGTePhS4DWnjtJJb";
			String consumerSecret = "Q6HO07az1NP6xXQTOGy8YtgjHwH6LZ1pOiKkFSs1PMzbwCRWpY";
			String token = "1120798090921947136-ItkUaQzI2Wdn9Z1JsbCSIE8hXAODW4";
			String secret = "Gj8aui1yQ9T0CtfWzzSGGqJAREY1tFYKvotokKxFHPpwi";
			BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
			StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
			endpoint.trackTerms(Lists.newArrayList(keyword1, keyword2, keyword3)); //keywords
			Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
			
			Client client = new ClientBuilder()
					.hosts(Constants.STREAM_HOST)
					.endpoint(endpoint)
					.authentication(auth)
					.processor(new StringDelimitedProcessor(queue))
					.build();
			
			client.connect();
			
			for (int i=0; i<=tweetsNumber; i++)
			{
				String msg = queue.take();
				producer.send(new ProducerRecord<String,String>("final-project-topic", msg));
				System.out.println("Sent: " + msg);
			}
			producer.close();
			client.stop();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

}
