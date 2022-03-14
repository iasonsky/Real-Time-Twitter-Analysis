package my.web.apps.db;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.mapred.FsInput;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Deserialize {
    public void Run() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        FsInput in = new FsInput(new Path("hdfs://localhost:9000/serialized_tweets/tweets.avro"), conf);
        
        String fileName = "resources/twitter1.avsc";
        ClassLoader classLoader = getClass().getClassLoader();
 
        File avro_file = new File(classLoader.getResource(fileName).getFile());
         
        //File is found
        System.out.println("File Found : " + avro_file.exists());
        Schema schema = new Schema.Parser().parse(avro_file);
        DatumReader<GenericRecord> datumReader = new SpecificDatumReader<>(schema);

        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>( in, datumReader);
        GenericRecord em = null;

        Configuration configuration = new Configuration();
        FileSystem hdfs = FileSystem.get( new URI( "hdfs://localhost:9000/deserialized_tweets" ), configuration );
        Path file = new Path("hdfs://localhost:9000/deserialized_tweets/des_tweets.txt");
        if ( hdfs.exists( file )) { hdfs.delete( file, true ); }
        OutputStream os = hdfs.create( file);
        BufferedWriter br = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) );


        while (dataFileReader.hasNext()) {
            em = dataFileReader.next(em);
            System.out.println(em);
            br.append(em.toString());
            br.append("\n");
        }
        br.close();
        hdfs.close();
        System.out.println("End of program!");
    }
}