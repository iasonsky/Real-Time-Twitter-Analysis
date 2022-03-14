package my.web.apps.mapreduce;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.StringTokenizer;

public class SourceCount {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        private ObjectMapper mapper = new ObjectMapper();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String line = value.toString();
            java.util.Map<String,String> rootAsMap = mapper.readValue(line, java.util.Map.class);
            String tokens[] = rootAsMap.get("source").split(">");
            String source[] = tokens[1].split("<");
            System.out.println("source:" + source[0]);
            word.set(source[0]);
            output.collect(word, one);
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
        JobConf conf = new JobConf(ActiveUsersCount.class);
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
        arguments[1] = "hdfs://localhost:9000/MapReduceOutput/MostUsedSource"; //Output directory

        FileInputFormat.setInputPaths(conf, new Path(arguments[0]));
        FileOutputFormat.setOutputPath(conf, new Path(arguments[1]));
        
        //Delete the hdfs directory if it already exists
        FileSystem hdfs = FileSystem.get( new URI( "hdfs://localhost:9000/MapReduceOutput/MostUsedSource" ), conf );
        Path file = new Path("hdfs://localhost:9000/MapReduceOutput/MostUsedSource");
        if ( hdfs.exists( file )) { hdfs.delete( file, true ); }
        
        JobClient.runJob(conf);
    }

}
