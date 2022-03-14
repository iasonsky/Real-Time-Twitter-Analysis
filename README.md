# Real-Time-Twitter-Analysis
## Introduction
The goal of this project is to develop a web application written in Java to analyze real-time data from Twitter using the Hadoop ecosystem.
- The data is gathered from Twitter API using Kafka, and is then encoded in Avro format and stored in HDFS.
- Using MapReduce we analyze the data generating simple statistics like most frequent location, most often used hashtags, most active users etc.
- Finally the K-Means Algorithm is implemented using Apache Mahout. 

## Architecture
![image](https://user-images.githubusercontent.com/17927250/158250190-7cf5da9e-83cf-47f1-8e6e-1ba27148c165.png)
The architecture of the application is explained below:
1. We gather our data from Twitter API using Kafka. More specifically the Kafka Producer loads tweets based on 3 key words and saves them in a Kafka Topic.
2. Then the Kafka Consumer loads the data from the topic, encodes them using Avro format and saves them to HDFS.
3. Then we decode the data using the Avro schema and save them to HDFS. The decoded data is going to be the input of the implemented data analysis methods.
4. We then use 2 different ways to analyse the data. 
    1. Using MapReduce. More specifically we implemented 5 different MapReduce jobs to extract useful information regarding:
        - Most active users
        - Most used hashtags
        - Most frequent location
        - Most mentioned users
        - Most popular device for posting the tweets
    2. Using Mahout.
        - First the decoded data are preproccessed in order to be in an input format suitable for K-Means algorithm.
        - The preproccessed data are then loaded from HDFS 
        - Finally the results of the clustering are also saved in HDFS.

## Getting Started
In order to run this project we have to install the following programs and also make sure to set up all enviroment variables correctly so 
everything works as expected.
1.	[Java Development Kit (JDK)](https://www.filehorse.com/download-java-development-kit-64/55825/)
2.	[Java Runtime Environment (JRE)](https://download.cnet.com/Java-Runtime-Environment-JRE/3000-2213_4-10009607.html)
3.	[Eclipse Enterprise Edition](https://www.eclipse.org/downloads/packages/release/2020-06/r/eclipse-ide-enterprise-java-developers)
4.	[Hadoop 3.1.0](https://archive.apache.org/dist/hadoop/common/hadoop-3.1.0/hadoop-3.1.0.tar.gz)
5.	[Kafka](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.7.0/kafka_2.13-2.7.0.tgz)
6.	[Avro](https://downloads.apache.org/avro/avro-1.10.2/)
7.	[Mahout](https://downloads.apache.org/mahout/0.12.0/)
8.	[Tomcat Server](https://tomcat.apache.org/download-90.cgi)
