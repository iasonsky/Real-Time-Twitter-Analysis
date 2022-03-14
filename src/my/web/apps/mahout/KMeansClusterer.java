package my.web.apps.mahout;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.clustering.conversion.InputDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.utils.clustering.ClusterDumper;

public class KMeansClusterer {

	public void Run(int clustersNumber, double conDelta, int maxIter) throws Exception {
		Path input = new Path("hdfs://localhost:9000/preprocessed_tweets");
		Path output = new Path("hdfs://localhost:9000/KMeansOutput");
		
		Configuration conf = new Configuration();
		HadoopUtil.delete(conf, output);
		
		run(conf, input, output, new EuclideanDistanceMeasure(), clustersNumber, conDelta, maxIter);
		
	}
	
	public static void run(Configuration conf, Path input, Path output, DistanceMeasure measure, int k, double convergenceDelta, int maxIterations) throws Exception {
		Path directoryContainingConvertedInput = new Path(output, "KmeansOutputData");
		InputDriver.runJob(input, directoryContainingConvertedInput, "org.apache.mahout.math.RandomAccessSparseVector");
		
		Path clusters = new Path(output, "random-seeds");
		clusters = RandomSeedGenerator.buildRandom(conf, directoryContainingConvertedInput, clusters, k, measure);
		
		KMeansDriver.run(conf, directoryContainingConvertedInput, clusters, output, convergenceDelta, maxIterations, true, 0.0, false);
		
		Path outGlob = new Path(output, "clusters-*-final");
		Path clusteredPoints = new Path(output, "clusteredPoints");
		
		ClusterDumper clusterDumper = new ClusterDumper(outGlob, clusteredPoints);
		clusterDumper.printClusters(null);
	} 

}
