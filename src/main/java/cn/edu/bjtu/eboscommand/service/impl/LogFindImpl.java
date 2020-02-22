package cn.edu.bjtu.eboscommand.service.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class LogFindImpl {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    public static String str= "" ;
    public static String read(String key,String value){
        String str1 ;
        MongoClient client = new MongoClient("127.0.0.1");
        MongoDatabase edge = client.getDatabase("edge");
        MongoCollection<Document> log4j2= edge.getCollection("log4j2");
        BasicDBObject bson = new BasicDBObject(key,value);
        FindIterable<Document> documents = log4j2.find(bson);
        for (Document document:documents) {
            str1 =document.getDate("date") +"    [" +document.getString("level") +"]    " + document.getString("message") + "\n";
            str += str1;
        }
        client.close();
        return str;
    }
    public static String readAll(){
        String str2;
        MongoClient client = new MongoClient("127.0.0.1");
        MongoDatabase edge = client.getDatabase("edge");
        MongoCollection<Document> log4j2= edge.getCollection("log4j2");
        FindIterable<Document> documents = log4j2.find();
        for (Document document:documents) {
            str2 =document.getDate("date") +"    [" +document.getString("level") +"]    " + document.getString("message") + "\n";
            str += str2;
        }
        client.close();
        return str;
    }
}
