<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import= "org.apache.hadoop.fs.Path" %>
<%@ page import= "org.apache.hadoop.fs.FileSystem" %>
<%@ page import= "org.apache.hadoop.fs.FSDataInputStream" %>
<%@ page import= "org.apache.hadoop.conf.Configuration" %>
<%@ page import="java.net.URISyntaxException" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<h1>Most Active Users</h1>
<%
	Configuration conf = new Configuration();
	Path pt= new Path("hdfs://localhost:9000/MapReduceOutput/MostActiveUsers/part-00000");
	FileSystem fs =FileSystem.get(new URI( "hdfs://localhost:9000/MapReduceOutput/MostActiveUsers" ), conf);
	BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
	try {
	  String line;
	  line=br.readLine();
	  while (line != null){
	    out.println(line);
%>
<br>
<%
	    // be sure to read the next line otherwise you'll get an infinite loop
	    line = br.readLine();
	  }
	} finally {
	  // you should close out the BufferedReader
	  br.close();
	}

%>
</body>
</html>