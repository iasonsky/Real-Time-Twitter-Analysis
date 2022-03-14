package my.web.apps.mahout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.json.JSONObject;
import java.util.regex.Pattern;

public class ParseTweets {
	public void Run() throws Exception{
		
		 Configuration conf = new Configuration();
			Path in= new Path("hdfs://localhost:9000/deserialized_tweets/des_tweets.txt");
			FileSystem hdfs_in =FileSystem.get(new URI( "hdfs://localhost:9000/deserialized_tweets" ), conf);
			BufferedReader inputBRJSON=new BufferedReader(new InputStreamReader(hdfs_in.open(in)));
			String line;
		     ArrayList<String> s = new ArrayList<String>();
		     while ((line = inputBRJSON.readLine()) != null) {
		            s.add(line);
		     }
		     inputBRJSON.close();

		 //BufferedReader inputBRJSON =new BufferedReader(new InputStreamReader(in.open()));
//		 BufferedReader inputBRJSON = new BufferedReader(new InputStreamReader(fin));
		 
		 Configuration configuration = new Configuration();
		 configuration.set("fs.defaultFS", "hdfs://localhost:9000");
		 FileSystem fileSystem = FileSystem.get(configuration);
		 //Create a path
		 String fileName = "tweets_preproccessed.txt";
		 Path hdfsWritePath = new Path("hdfs://localhost:9000/preprocessed_tweets/preprocessed_tweets.txt");
		 FSDataOutputStream fsDataOutputStream = fileSystem.create(hdfsWritePath,true);
		 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fsDataOutputStream,StandardCharsets.UTF_8));
		 
//		 FSDataOutputStream out = new Path("hdfs://localhost:9000/preprocessed_tweets/preprocessed_tweets.txt");
//		 FileSystem hdfs_out =FileSystem.get(new URI( "hdfs://localhost:9000/preprocessed_tweets" ), conf);	     
//	 	 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(hdfs_out.open(out)));
	     
	     for (String jsonString : s) {
	            
	        	 JSONObject obj = new JSONObject(jsonString);
               //Parse json
                 String text = obj.getString("text");
                 String location = obj.getString("user_location");
                 
                 // strips off all non-ASCII characters
                 location = location.replaceAll("[^\\x00-\\x7F]", "");
                 text = text.replaceAll("[^\\x00-\\x7F]", "");
                 // remove \n chars
                 location = location.replaceAll("\n"," ");
                 text = text.replaceAll("\n"," ");
                 // remove numbers
                 location = location.replaceAll("[0-9]", "");
                 text = text.replaceAll("[0-9]", "");
                 
                 // remove RT 
                 text = text.replaceAll("RT", "");
                 
                 // Remove @ sign
                 text = text.replaceAll("@[A-Za-z0-9]+:", "");
                 
                 // Remove all Puncts
                 text = text.replaceAll("[\\p{Punct}]", "");
                 location = location.replaceAll("[\\p{Punct}]", "");     
                 
                 // removes non-printable characters from Unicode
                 text = text.replaceAll("\\p{C}", "");
                 location = location.replaceAll("\\p{C}", "");
                 // removes links from text
                 text = text.replaceAll("http\\S+", "");
                 
                 //trim 
                 location = location.trim();
                 text = text.trim();
                 text = text.toLowerCase();
                 
         		 if(! location.isEmpty()) {
         			String usaFilePath = "resources/usa.txt";
         			String euroFilePath = "resources/europe.txt";
         			String asiaFilePath = "resources/asia.txt";
         			
         			ClassLoader classLoader = getClass().getClassLoader();
         			
         			File usa = new File(classLoader.getResource(usaFilePath).getFile());
         			File euro = new File(classLoader.getResource(euroFilePath).getFile());
         			File asia = new File(classLoader.getResource(asiaFilePath).getFile());
         			
//         			//File is found
//        	        System.out.println("File Found : " + usa.exists());
//        	        System.out.println("File Found : " + euro.exists());
//        	        System.out.println("File Found : " + asia.exists());
         			
         			try {
         			    Scanner usa_scanner = new Scanner(usa);
         			    Scanner euro_scanner = new Scanner(euro);
         			    Scanner asia_scanner = new Scanner(asia);
         			    //now read the file line by line...
         			    while (usa_scanner.hasNextLine()) {
         			        String dict_line = usa_scanner.nextLine();
         			        if(dict_line.contains(location)) { 
         			           bw.write("1");
         			           if (text.contains("bitcoin")) {
         			        	  bw.write(" 1");
         			           }else if(text.contains("ethereum")){
         			        	  bw.write(" 2");
         			           }else if(text.contains("dogecoin")) {
         			        	  bw.write(" 3");
         			           }else {
         			        	  bw.write(" 4");
         			           }
         			           bw.newLine(); 
         			        }
         			    }
         			   while (euro_scanner.hasNextLine()) {
        			        String dict_line = euro_scanner.nextLine();
        			        if(dict_line.contains(location)) { 
        			           bw.write("2");
        			           if (text.contains("bitcoin")) {
          			        	  bw.write(" 1");
          			           }else if(text.contains("ethereum")){
          			        	  bw.write(" 2");
          			           }else if(text.contains("dogecoin")) {
          			        	  bw.write(" 3");
          			           }else {
          			        	  bw.write(" 4");
          			           }
        			           bw.newLine(); 
        			        }
        			    }
         			  while (asia_scanner.hasNextLine()) {
      			        String dict_line = asia_scanner.nextLine();
      			        if(dict_line.contains(location)) { 
      			           bw.write("3");
      			         if (text.contains("bitcoin")) {
    			        	  bw.write(" 1");
    			           }else if(text.contains("ethereum")){
    			        	  bw.write(" 2");
    			           }else if(text.contains("dogecoin")) {
    			        	  bw.write(" 3");
    			           }else {
    			        	  bw.write(" 4");
    			           }
      			           bw.newLine(); 
      			        }
         			  }
         			} catch(FileNotFoundException e) { 
         			    //handle this
         			}
            			
         		 }
	    }
	    bw.close();
	    System.out.println("End of program");
	}
}
