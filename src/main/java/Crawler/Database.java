package Crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Database {
  MongoClient mongoClient;
  MongoDatabase crawlerDB;
  MongoCollection<Document> linksCollection;
  MongoCollection<Document> hrefCollection;
  MongoCollection<Document> linksPathCollection;

  public Database() {
    String uri = "mongodb+srv://rofaydabassem:GMeRcAsBR0nwwXfC@cluster0.sxccutr.mongodb.net/";
    ConnectionString connectionString = new ConnectionString(uri);
    MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
    mongoClient = com.mongodb.client.MongoClients.create(settings);
    crawlerDB = mongoClient.getDatabase("SearchEngine");
    linksCollection = crawlerDB.getCollection("Links");
    hrefCollection = crawlerDB.getCollection("Href");
    linksPathCollection = crawlerDB.getCollection("URL");
  }

  public void insertLink(List<String> links) {
    List<Document> entry =new ArrayList<>();
    for (String link : links) {
      entry.add(new Document("url", link).append("visited", false).append("indexed", false));
    }
    linksCollection.insertMany(entry);
  }

  public void insertHref(List<String> links, String url) {
    List<Document> entry =new ArrayList<>();
    for (String link : links) {
      entry.add(new Document("url", link).append("refBy", url));
    }
    hrefCollection.insertMany(entry);
  }

  public void addURL(String url, String fileName) {
    Document doc = new Document("url", url).append("visited", true).append("indexed", false)
        .append("filepath", fileName);
    linksPathCollection.insertOne(doc);
  }

  // public void updateLink(String url) {
  //   linksCollection.updateOne(new Document("url", url), new Document("$set", new Document("visited", true)));
  // }

  // public void updateHref(List<String> links, String url) {
  //   // TODO: implement method
  // }

  public void getQueue(List<String> urls) {
    urls.clear();
    for (Document doc : linksCollection.find(new Document("visited", false))) {
      urls.add(doc.getString("url"));
    }
  }

  public void getVisited(Set<String> visitedURLs) {
    visitedURLs.clear();
    for (Document doc : linksPathCollection.find(new Document("visited", true))) {
      visitedURLs.add(doc.getString("url"));
    }
  }

  public void getVisitedList(List<String> urls) {
    urls.clear();
    for (Document doc : linksPathCollection.find(new Document("visited", true))) {
      urls.add(doc.getString("url"));
    }
  }

  // public void insertDate(Date date) {
  //   Document doc = new Document("name", "last_crawl_date").append("value", date);
  //   crawlerDB.getCollection("settings").insertOne(doc);
  // }

  // public void updateDate(Date date) {
  //   Document filter = new Document("name", "last_crawl_date");
  //   Document update = new Document("$set", new Document("value", date));
  //   crawlerDB.getCollection("settings").updateOne(filter, update);
  // }

  // public void getDate(Date recrawlDate) {
  //   Document doc = crawlerDB.getCollection("settings").find(new Document("name", "last_crawl_date")).first();
  //   if (doc != null) {
  //     recrawlDate.setTime(doc.getDate("value").getTime() + 2 * 60 * 60 * 1000); // Recrawl every 2 hours
  //   }
  // }

  public void close() {
    mongoClient.close();
  }
}