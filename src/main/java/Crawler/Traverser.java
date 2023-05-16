package Crawler;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jsoup.Connection;
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

  public Traverser(RobotExclusion robotExculsion) {
    this.robotExculsion = robotExculsion;
  }

  public boolean traverse(Database DB, String url) {
    try {
      // The url is safe to crawl
      if (!robotExculsion.robotSafe(URI.create(url).toURL())) {
        System.out.println("The url is not safe to crawl");
        return false;
      }
      // Check if the url is visited
      if (visitedLinks.contains(url)) {
        System.out.println("The url is visited");
        return false;
      }
      // Get the html document
      String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36";
      Connection connection = Jsoup.connect(url).userAgent(userAgent).timeout(20000); // Set the timeout to 10 seconds
      Document htmlDoc = connection.get();
      this.htmlDoc = Jsoup.parse(htmlDoc.toString()); // Parse the html document
      // Save the html document in a file with the name of the url
      String fileName = "htmldocs/" + url.hashCode() + ".html";
      try (FileWriter myWriter = new FileWriter(fileName)) {
        synchronized (myWriter) {
          myWriter.write(this.htmlDoc.toString());
        }
        visitedLinks.add(url); // Add the url to the visited list
        DB.addURL(url, fileName); // Add the url to the database
      } catch (IOException e) {
        System.out.println("An error occurred in writing the html documents.");
        e.printStackTrace();
        File file = new File(fileName);
        if (file.exists()) {
          file.delete(); // Delete the file if it exists
        }
        return false;
      }
      // Get meta words
      Elements linksOnPage = htmlDoc.select("a[href]"); // Get all the links on the page
      for (Element link : linksOnPage) {
        this.Links.add(link.absUrl("href")); // Add the links to the list
        if (this.Links.size() > 50) // If the list size is greater than 50
        {
          break;
        }
      }
      
      
      DB.updateHref(url);
      //DB.insertLink(getLinks()); // Insert the links in the database
      if (Links.contains(url)) {
        DB.insertHrefVisited(getLinks(), url);// Insert the hrefs in the database
      }
      else{
        DB.insertHref(getLinks(), url);// Insert the hrefs in the database
      }
    } catch (SocketTimeoutException e) {
      System.out.println("The connection timed out for URL: " + url);
      return false;
    } catch (IOException e) {
      System.out.println("Error in traversing the url (IO Exception).");
      e.printStackTrace();
      return false;
    } catch (URISyntaxException e1) {
      System.out.println("Error in traversing the url (URI Syntax Exception).");
      e1.printStackTrace();
      return false;
    }
    return true;
  }

  // Retraverse the url updating the links and hrefs
  // public void Retraverse(Database DB, String url) {
  //   try {
  //     // Get the html document
  //     String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36";
  //     Connection connection = Jsoup.connect(url).userAgent(userAgent).timeout(10000); // Set the timeout to 10 seconds
  //     Document htmlDoc = connection.get();
  //     Elements linksOnPage = htmlDoc.select("a[href]"); // Get all the links on the page
  //     for (Element link : linksOnPage) {
  //       Links.add(link.absUrl("href")); // Add the links to the list
  //       if (Links.size() > 100) // If the list size is greater than 80
  //       {
  //         break;
  //       }
  //     }
  //     DB.updateLink(url); // Update the links in the database
  //     DB.updateHref(Links, url); // Update the hrefs in the database
  //   } catch (SocketTimeoutException e) {
  //     System.out.println("The connection timed out for URL: " + url);
  //   } catch (IOException e) {
  //     System.out.println("Error in retraversing the url.");
  //     e.printStackTrace();
  //   }
  // }

  public List<String> getLinks() {
    return this.Links;
  }
}
