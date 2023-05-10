package Crawler;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("ALL")
public class Traverser {
  public List<String> Links = new LinkedList<String>();
  public Document htmlDoc;
  public HashSet<String> visitedLinks = new HashSet<String>();
  private RobotExclusion robotExculsion;

  public void traverse(Database DB, String url) {
    try {
      String metaWords = " ";
      // The url is safe to crawl
      if (!robotExculsion.robotSafe(URI.create(url).toURL())) {
        System.out.println("The url is not safe to crawl");
        continue;
      }
      // Check if the url is visited
      if (visitedLinks.contains(url)) {
        System.out.println("The url is visited");
        continue;
      }
      // Get the html document
      htmlDoc = Jsoup.connect(url).get();
      Element body = htmlDoc.body(); // Get the body of the html document
      this.htmlDoc = Jsoup.parse(htmlDoc.toString()); // Parse the html document
      // Save the html document in a file with the name of the url
      String fileName = "htmldocs/" + url.substring(url.lastIndexOf("/") + 1) + ".txt";
      try {
        FileWriter myWriter = new FileWriter(fileName);
        synchronized (myWriter) // Synchronized so that only one thread can access it at a time
        {
          myWriter.write(this.htmlDoc.toString());
          myWriter.close();
        }
      } catch (IOException e) {
        System.out.println("An error occurred in writing the html documents.");
        e.printStackTrace();
      }
      // Get meta words
      for (Element meta : htmlDoc.select("meta")) {
        if (meta.attr("name").toLowerCase().equals("keywords")) {
          metaWords = meta.attr("content").toLowerCase();
          break;
        }
      }
      Elements linksOnPage = htmlDoc.select("a[href]"); // Get all the links on the page
      for (Element link : linksOnPage) {
        this.Links.add(link.absUrl("href")); // Add the links to the list
        if (this.Links.size() > 50) // If the list size is greater than 50
        {
          break;
        }
      }
      // Add the url to the visited list
      visitedLinks.add(url);
      // Add the url to the database
      DB.addURL(url, fileName);
      DB.insertLink(getLinks()); // Insert the links in the database
      DB.insertHref(getLinks(), url); // Insert the hrefs in the database

    } catch (IOException e) {
      System.out.println("Error in traversing the url.");
      e.printStackTrace();
    }
  }

  // Retraverse the url updating the links and hrefs
  public void Retraverse(Database DB, String url) {
    try {
      Document htmlDoc = Jsoup.connect(url).get();
      Elements linksOnPage = htmlDoc.select("a[href]"); // Get all the links on the page
      for (Element link : linksOnPage) {
        this.Links.add(link.absUrl("href")); // Add the links to the list
        if (this.Links.size() > 20) // If the list size is greater than 20
        {
          break;
        }
      }
      DB.updateLink(url); // Update the links in the database
      DB.updateHref(getLinks(), url); // Update the hrefs in the database
    } catch (IOException e) {
      System.out.println("Error in retraversing the url.");
      e.printStackTrace();
    }
  }

  public List<String> getLinks() {
    return this.Links;
  }
}
