package my.web.apps.db;

import com.twitter.bijection.Injection;
import com.twitter.bijection.avro.GenericAvroCodecs;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.*;

public class My_TwitterConsumerAvro {

    public void RunConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092");
        props.put("group.id","group-1");
        props.put("enablue.auto.commit","true");
        props.put("auto.commit.interval.ms","1000");
        props.put("auto.offset.reset","earliest");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("fetch.message.max.bytes", "50000000");
        KafkaConsumer<String, String> twitterConsumer = new KafkaConsumer<>(props);
        twitterConsumer.subscribe(Arrays.asList("final-project-topic"));
        try {

        	String fileName = "resources/twitter1.avsc";
            ClassLoader classLoader = getClass().getClassLoader();
     
            File avro_file = new File(classLoader.getResource(fileName).getFile());
             
            //File is found
            System.out.println("File Found : " + avro_file.exists());
            
        	Schema schema = new Schema.Parser().parse(avro_file);
            Injection<GenericRecord, byte[]> recordInjection = GenericAvroCodecs.toBinary(schema);

            KafkaProducer<String, byte[]> producer = new KafkaProducer<>(props);

            DatumWriter<GenericRecord> datumWriter = new SpecificDatumWriter<>(schema);
            DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);

            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(URI.create("hdfs://localhost:9000/serialized_tweets/tweets.avro"),conf);
            OutputStream out = fs.create(new Path("hdfs://localhost:9000/serialized_tweets/tweets.avro"));
            dataFileWriter.create(schema,out);

            while(true) {


                ConsumerRecords<String, String> records = twitterConsumer.poll(2500);
                for (ConsumerRecord<String, String> record : records) {
                    String jsonString = record.value(); //assign your JSON String here
                    JSONObject obj = new JSONObject(jsonString);
                    System.out.println(jsonString);
                    //Parse json
                    String id = obj.getString("id_str");
                    String text = obj.getString("text");
                    long retweetCount = obj.getLong("retweet_count");
                    boolean retweeted = obj.getBoolean("retweeted");
                    String source = obj.getString("source");
//					long inReplyToUserId = obj.getLong("in_reply_to_user_id");
//					String inReplyToStatusId = obj.getString("in_reply_to_status_id");

                    // get User nested object
                    JSONObject userObj = obj.getJSONObject("user");
                    //get User object values
                    int userFriendsCount = userObj.getInt("friends_count");
                    //Check if user location is null (this is common)
                    String userLocation;
                    if (userObj.isNull("location")){
                        userLocation = "";
                    } else {
                        userLocation = userObj.getString("location");
                    }
                    //Check if user description is null (this is common)
                    String userDescription;
                    if (userObj.isNull("description")){
                        userDescription = "";
                    } else{
                        userDescription = userObj.getString("description");
                    }
                    int userStatusesCount = userObj.getInt("statuses_count");
                    int userFollowersCount = userObj.getInt("followers_count");
                    String username = userObj.getString("name");
                    String userScreenName = userObj.getString("screen_name");
                    String createdAt = userObj.getString("created_at");

                    // create Entities object
                    JSONObject entitiesObj = obj.getJSONObject("entities");
                    // get Hashtags JSONArray
                    JSONArray hashtags = entitiesObj.getJSONArray("hashtags");
                    List<String> hashtag_list = new ArrayList<String>();
                    // Save hashtag text to the list
                    if(hashtags != null && hashtags.length() > 0){
                        for(int i = 0 ; i < hashtags.length(); i++) {
                            hashtag_list.add(hashtags.getJSONObject(i).getString("text"));
                        }
                    }

                    //get Mentions JSONArray
                    JSONArray user_mentions = entitiesObj.getJSONArray("user_mentions");
                    List<String> user_mentions_list = new ArrayList<String>();
                    // Save mention name (@name) to the list
                    if(user_mentions != null && user_mentions.length() > 0){
                        for(int i = 0 ; i < user_mentions.length(); i++) {
                            user_mentions_list.add(user_mentions.getJSONObject(i).getString("screen_name"));
                        }
                    }

                    //Put the data to avro record
                    GenericData.Record twitterRecord = new GenericData.Record(schema);
                    twitterRecord.put("id", id);
                    twitterRecord.put("user_friends_count", userFriendsCount);
                    twitterRecord.put("user_location",userLocation);
                    twitterRecord.put("user_description", userDescription);
                    twitterRecord.put("user_statuses_count", userStatusesCount);
                    twitterRecord.put("user_followers_count", userFollowersCount);
                    twitterRecord.put("user_name", username);
                    twitterRecord.put("user_screen_name", userScreenName);
                    twitterRecord.put("created_at", createdAt);
                    twitterRecord.put("text", text);
                    twitterRecord.put("retweet_count", retweetCount);
                    twitterRecord.put("retweeted", retweeted);
                    twitterRecord.put("source", source);
                    twitterRecord.put("hashtags", hashtag_list);
                    twitterRecord.put("user_mentions", user_mentions_list);
//					twitterRecord.put("in_reply_to_user_id", inReplyToUserId);
//					twitterRecord.put("in_reply_to_status_id", inReplyToStatusId);
//					twitterRecord.put("media_url_https", quotedStatusUrl);
//					twitterRecord.put("expanded_url", expandedUrl);

                    dataFileWriter.append(twitterRecord);

//					byte[] bytes = recordInjection.apply(twitterRecord);
//					ProducerRecord<String, byte[]> kafkaRecord = new ProducerRecord<>("serialized_tweets", bytes);
//					producer.send(kafkaRecord);
//					System.out.println("Sent: " + kafkaRecord);
//
//					System.out.printf("offset = %d, value = %s , source = %s", record.offset(), text, source);
//					System.out.println();
                }
                System.out.println("Data successfully serialized!");
                dataFileWriter.close();
                return;

            }
        }catch(Exception e) {
            System.out.println(e);
        }
        finally {
            twitterConsumer.close();
        }


    }
}

