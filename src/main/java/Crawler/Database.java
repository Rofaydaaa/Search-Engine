package Crawler;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.crypto.Data;

import org.bson.Document;

public class Database {

  public Database() {
    
  }

  public void insertLink(List<String> links) {

  }

  public void insertHref(List<String> links, String url) {

  }

  public void addURL(String url, String fileName) {

  }

  public void updateLink(String url) {

  }

  public void updateHref(List<String> links, String url) {

  }

  public void getQueue(List<String> uRLs) {

  }

  public void getVisited(Set<String> visitedURLs) {

  }

  public void getVisitedList(List<String> uRLs) {

  }

  public void insertDate(Date date) {
    // Document doc = new Document("Date", date).append("RecrawlDate", "date");
    // crawlerCollection.insertOne(doc);
  }

  public void updateDate(Date date) {
    // Object dateObject = crawlerCollection.find(eq("RecrawlDate",
    // "date")).first().get("Date");
    // if (dateObject==null) {
    // insertDate(date);
    // } else {
    // crawlerCollection.updateOne()
    // }
  }

  public void getDate(Date recrawlDate) {
    // Date Date = (Date) crawlerCollection.find(eq("RecrawlDate",
    // "date")).first().get("Date");
    // recrawlDate.setTime(Date.getTime());
    // recrawlDate.setHours(recrawlDate.getHours() + 1); // Recrawl every 2 hours
  }

}