<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="my.web.apps.db.My_TwitterProducer" %>
<%@ page import="my.web.apps.db.My_TwitterConsumerAvro" %>
<%@ page import="my.web.apps.db.Deserialize" %>
<%@ page import="my.web.apps.mapreduce.ActiveUsersCount" %>
<%@ page import="my.web.apps.mapreduce.HashtagsCount" %>
<%@ page import="my.web.apps.mapreduce.LocationCount" %>
<%@ page import="my.web.apps.mapreduce.SourceCount" %>
<%@ page import="my.web.apps.mapreduce.UserMentionsCount" %>
<%@ page import="my.web.apps.db.HadoopOpener" %>
<%@ page import="my.web.apps.db.HadoopCloser" %>
<%@ page import="my.web.apps.db.KafkaOpener" %>
<%@ page import="my.web.apps.mahout.ParseTweets" %>
<%@ page import="my.web.apps.mahout.KMeansClusterer" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Main Page</title>
<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
	<%
		if(request.getParameter("start_hadoop_btn") != null){
			HadoopOpener start_hadoop = new HadoopOpener();
			start_hadoop.Open();
			out.println("HDFS and YARN have started!");
		}
		if(request.getParameter("stop_hadoop_btn") != null){
			HadoopCloser stop_hadoop = new HadoopCloser();
			stop_hadoop.Stop();
			out.println("HDFS and YARN have stopped!");
		}
		if(request.getParameter("kafka_btn") != null){
			KafkaOpener kafka = new KafkaOpener();
			kafka.Open();
			out.println("Zookeeper and broker have started!");
		}
		if(request.getParameter("load_btn") != null) {
			
			String keyword1 = request.getParameter("keyw1");
			String keyword2 = request.getParameter("keyw2");
			String keyword3 = request.getParameter("keyw3");
			String tweets_num = request.getParameter("tweets_num");
			int tweets_number = Integer.parseInt(tweets_num);
			
			My_TwitterProducer prod = new My_TwitterProducer();
			prod.RunProducer(keyword1, keyword2, keyword3, tweets_number);
			out.println("Data Successfully Loaded!");
		}
		if(request.getParameter("serialize_btn") != null ){
			My_TwitterConsumerAvro cons = new My_TwitterConsumerAvro();
			cons.RunConsumer();
			out.println("Data Successfully Serialized!");
		}
		if(request.getParameter("deserialize_btn") != null){
			Deserialize deserialization = new Deserialize();
			deserialization.Run();
			out.println("Data Successfully Deserialized!");
		}
		if(request.getParameter("mapred_btn") != null){
			ActiveUsersCount active_users = new ActiveUsersCount();
			HashtagsCount hashtags_count = new HashtagsCount();
			LocationCount location_count = new LocationCount();
			SourceCount source_count = new SourceCount();
			UserMentionsCount user_mentions_count = new UserMentionsCount();
			
			//Run the jobs
			active_users.Run();
			hashtags_count.Run();
			location_count.Run();
			source_count.Run();
			user_mentions_count.Run();
			out.println("All Mapreduce jobs were successful!");
		}
		if(request.getParameter("parse_btn") != null) {
			ParseTweets parser = new ParseTweets();
			parser.Run();
			out.println("Preproccesing was successsful!");
		}
		if(request.getParameter("kmeans_btn") != null) {
			String clusters_num = request.getParameter("clusters_num"); 
			String convergence_delta = request.getParameter("convergence_delta");
			String max_iterations = request.getParameter("max_iterations");
			
			int clustersNumber = Integer.parseInt(clusters_num);
			double convergenceDelta = Double.parseDouble(convergence_delta);
			int maxIterations = Integer.parseInt(max_iterations);
			
			
			KMeansClusterer kmeans = new KMeansClusterer();
			kmeans.Run(clustersNumber, convergenceDelta, maxIterations);
			out.println("KMeans run successfully!");
		}
		
	%>
	<h2> Big Data Final Project <br>Iason Skylitsis:151097 <br> Albano Prifti:151094</h2>

	<form METHOD="post">
		<h2>Start HDFS and YARN</h2>
		<input type="submit" name="start_hadoop_btn" value="Start">
		<input type="submit" name="stop_hadoop_btn" value="Stop">
		<h2>Start Zookeeper and Broker</h2>
		<input type="submit" name="kafka_btn" value="Start">
		<h2>Choose keywords</h2>
			Keyword 1: <input type="text" name="keyw1" value="">
			Keyword 2: <input type="text" name="keyw2" value="">
			Keyword 3: <input type="text" name="keyw3" value=""> <br><br>
			Number of Tweets: <input type="text" name="tweets_num" value="">
			<input type="submit" name="load_btn" value="Load Tweets">
		<h2> Serialize data</h2>
			<input type="submit" name="serialize_btn" value="Serialize Data">
			<input type="button" value="Show serialized data" onclick="window.open('ShowSerializedData.jsp')">
		<h2> Deserialize data</h2>
			<input type="submit" name="deserialize_btn" value="Deserialize Data">
			<input type="button" value="Show deserialized data" onclick="window.open('ShowDeserializedData.jsp')">
		<h2> Run All MapReduce jobs</h2>
			<input type="submit" name="mapred_btn" value="Run MapReduce">
			<input type="button" value="Show Most Active Users" onclick="window.open('ShowActiveUsers.jsp')">
			<input type="button" value="Show Most Used Hashtags" onclick="window.open('ShowHashtags.jsp')">
			<input type="button" value="Show Most Mentioned Users" onclick="window.open('ShowMentionedUsers.jsp')">
			<input type="button" value="Show Most Frequent Locations" onclick="window.open('ShowLocations.jsp')">
			<input type="button" value="Show Most Used Source" onclick="window.open('ShowSources.jsp')">
		<h2> Run KMeans Clustering</h2>
			<input type="submit" name="parse_btn" value="Preproccess the Tweets">
			<input type="button" value="Show Preproccessed Data" onclick="window.open('ShowPreproccessedData.jsp')"><br><br>
			Number of Clusters: <input type="text" name="clusters_num" value="">
			Convergence Delta: <input type="text" name="convergence_delta" value="">
			Max iterations: <input type="text" name="max_iterations" value="">
			<input type="submit" name="kmeans_btn" value="Run KMeans">
			<input type="button" value="Show KMeans Results" onclick="window.open('ShowKmeansResults.jsp')">
	</form>
	
</body>
</html>