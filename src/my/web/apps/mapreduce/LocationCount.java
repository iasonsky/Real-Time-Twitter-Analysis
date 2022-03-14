package my.web.apps.mapreduce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.eclipse.jetty.util.resource.FileResource;

public class LocationCount {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        private ObjectMapper mapper = new ObjectMapper();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            String line = value.toString();
            java.util.Map<String,String> rootAsMap = mapper.readValue(line, java.util.Map.class);
            StringTokenizer tokenizer = new StringTokenizer(rootAsMap.get("user_location"));
            System.out.println(rootAsMap.get("user_location"));
            if (!rootAsMap.get("user_location").isEmpty()){     	
     			String usaFilePath = "resources/usa.txt";
     			String euroFilePath = "resources/europe.txt";
     			String asiaFilePath = "resources/asia.txt";
     			
     			ClassLoader classLoader = getClass().getClassLoader();
     			
     			File usa = new File(classLoader.getResource(usaFilePath).getFile());
     			File euro = new File(classLoader.getResource(euroFilePath).getFile());
     			File asia = new File(classLoader.getResource(asiaFilePath).getFile());
     			
     			
     		
     			
     			//File is found
    	        System.out.println("File Found : " + usa.exists());
    	        System.out.println("File Found : " + euro.exists());
    	        System.out.println("File Found : " + asia.exists());
     			
     			String location = rootAsMap.get("user_location");
     			Boolean found = false;
     			
     			try {
     			    Scanner usa_scanner = new Scanner(usa);
    			    Scanner euro_scanner = new Scanner(euro);
     			    Scanner asia_scanner = new Scanner(asia);
     			    
     			    //now read the file line by line...
     			    while (usa_scanner.hasNextLine()) {
     			        String dict_line = usa_scanner.nextLine();
     			        Pattern re = Pattern.compile("\\b"+location+"\\b",Pattern.CASE_INSENSITIVE);
    			        Matcher match = re.matcher(dict_line);
    			        if(match.find() && !found) {
     			        	word.set("USA");
     		                output.collect(word,one);
     		                found = true;
     			           break;
     			        }
     			    }
     			   while (euro_scanner.hasNextLine() && !found) {
    			        String dict_line = euro_scanner.nextLine();
    			        Pattern re = Pattern.compile("\\b"+location+"\\b",Pattern.CASE_INSENSITIVE);
     			        Matcher match = re.matcher(dict_line);
     			        if(match.find() && !found) {
    			        	word.set("Europe");
    		                output.collect(word,one);
    		                found = true;
    			           break;
    			        }
    			    }
     			   while (asia_scanner.hasNextLine() && !found) {
   			        String dict_line = asia_scanner.nextLine();
   			        Pattern re = Pattern.compile("\\b"+location+"\\b",Pattern.CASE_INSENSITIVE);
   			        Matcher match = re.matcher(dict_line);
   			        if(match.find() && !found) { 
   			        	word.set("Asia");
   		                output.collect(word,one);
   		                found = true;
   			           break;
   			        }
      			   }
     			  // if it wasn't any of the previous then put 'Other'
     			  if(!found) {
     				 word.set("Other");
    	             output.collect(word,one);
     			  }
     			 
//                word.set(rootAsMap.get("user_location"));
//                output.collect(word,one);
            }catch(FileNotFoundException e) { 
 			    //handle this
            	e.printStackTrace();
 			}
        }
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
        JobConf conf = new JobConf(LocationCount.class);
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
        arguments[1] = "hdfs://localhost:9000/MapReduceOutput/MostFrequentLocation"; //Output directory

        FileInputFormat.setInputPaths(conf, new Path(arguments[0]));
        FileOutputFormat.setOutputPath(conf, new Path(arguments[1]));
        
        //Delete the hdfs directory if it already exists
        FileSystem hdfs = FileSystem.get( new URI( "hdfs://localhost:9000/MapReduceOutput/MostFrequentLocation" ), conf );
        Path file = new Path("hdfs://localhost:9000/MapReduceOutput/MostFrequentLocation");
        if ( hdfs.exists( file )) { hdfs.delete( file, true ); }
        
        JobClient.runJob(conf);
    }

}
