package my.web.apps.mapreduce;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class HashtagsCount {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        private ObjectMapper mapper = new ObjectMapper();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String line = value.toString();
            JsonNode arrNode = mapper.readTree(line).get("hashtags");
            if (arrNode.isArray()){
                for (final JsonNode objNode: arrNode){
                    System.out.println(objNode);
                    word.set(String.valueOf(objNode));
                    output.collect(word, one);
                }
            }
//            // get Hashtags JSONArray
////            JSONObject json = readJSON
////            JSONArray hashtags = jsonNode.getJSONArray("hashtags");
//            List<String> hashtag_list = new ArrayList<String>();
//            // Save hashtag text to the list
//            if(hashtags != null && hashtags.length() > 0){
//                for(int i = 0 ; i < hashtags.length(); i++) {
//                    hashtag_list.add(hashtags.getJSONObject(i).getString("text"));
//                }
//            }
//            java.util.Map<String,String> rootAsMap = mapper.readValue(line, java.util.Map.class);
//            StringTokenizer tokenizer = new StringTokenizer(rootAsMap.get("text"));
//            while (tokenizer.hasMoreTokens()) {
//                word.set(tokenizer.nextToken());
//                output.collect(word, one);
//            }
        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            output.collect(key, new IntWritable(sum));
        }
    }

    public void Run() throws Exception {
        JobConf conf = new JobConf(HashtagsCount.class);
        conf.setJobName("wordcount");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        /* Deprecated ?
         * conf.setInputPath(new Path(args[0]));
         * conf.setOutputPath(new Path(args[1]));
         */
        String arguments[] = new String[2];
        arguments[0] = "hdfs://localhost:9000/deserialized_tweets/des_tweets.txt";
        arguments[1] = "hdfs://localhost:9000/MapReduceOutput/MostUsedHashtags"; //Output directory

        FileInputFormat.setInputPaths(conf, new Path(arguments[0]));
        FileOutputFormat.setOutputPath(conf, new Path(arguments[1]));
        
        //Delete the hdfs directory if it already exists
        FileSystem hdfs = FileSystem.get( new URI( "hdfs://localhost:9000/MapReduceOutput/MostUsedHashtags" ), conf );
        Path file = new Path("hdfs://localhost:9000/MapReduceOutput/MostUsedHashtags");
        if ( hdfs.exists( file )) { hdfs.delete( file, true ); }
        JobClient.runJob(conf);
    }

}
