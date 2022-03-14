<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="org.apache.hadoop.fs.Path" %>
<%@ page import="org.apache.mahout.utils.clustering.ClusterDumper" %>
<%@ page import="org.apache.hadoop.conf.Configuration" %>
<%@ page import="java.util.*" %>
<%@ page import= "org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>KMeans Results</title>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<h1>KMeans Results</h1>
<%
Path output = new Path("hdfs://localhost:9000/KMeansOutput");
Path outGlob = new Path(output, "clusters-*-final");
Path clusteredPoints = new Path(output, "clusteredPoints");

ClusterDumper clusterDumper = new ClusterDumper(outGlob, clusteredPoints);
long maxpoints = 100;
Configuration conf = new Configuration();
Map<Integer,List<WeightedPropertyVectorWritable>> pointsMap = clusterDumper.readPoints(clusteredPoints,maxpoints, conf);
for (Map.Entry entry : pointsMap.entrySet())
{
    out.println("identifier: VL-" + entry.getKey() + ", Points: " );
    %>
    <br>
    <%
    out.println(entry.getValue()); 
    %>
    <br><br>
<%
}
%>
</body>
</html>