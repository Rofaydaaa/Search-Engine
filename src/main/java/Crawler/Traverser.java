package Crawler;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
      Connection connection = Jsoup.connect(url).userAgent(userAgent).timeout(20000); // Set the timeout to 20 seconds
      Document htmlDoc = connection.get();
      // Compute the content hash of the html document
      String contentHash = getContentHash(htmlDoc.toString());
      // Check if the content hash already exists in the database
      if (DB.containsContentHash(contentHash)) {
        System.out.println("The url has the same content as a previously crawled page");
        return false;
      }
      this.htmlDoc = Jsoup.parse(htmlDoc.toString()); // Parse the html document
      // Save the html document in a file with the name of the url
      String fileName = "htmldocs/" + url.hashCode() + ".html";
      try (FileWriter myWriter = new FileWriter(fileName)) {
        synchronized (myWriter) {
          myWriter.write(this.htmlDoc.toString());
        }
        visitedLinks.add(url); // Add the url to the visited list
        DB.addURL(url, fileName,contentHash); // Add the url to the database
      } catch (IOException e) {
        System.out.println("An error occurred in writing the html documents.");
        e.printStackTrace();
        File file = new File(fileName);
        if (file.exists()) {
          file.delete(); // Delete the file if it exists
        }
        return false;
      }
      // Get all the links on the page
      Elements linksOnPage = htmlDoc.select("a[href]"); // Get all the links on the page
      for (Element link : linksOnPage) {
        this.Links.add(link.absUrl("href")); // Add the links to the list
        if (this.Links.size() > 50) // If the list size is greater than 50
        {
          break;
        }
      }
      
      DB.updateHref(url); // Update the hrefs in the database
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
    } catch (NoSuchAlgorithmException e2) {
      System.out.println("Error in computing the content hash.");
      e2.printStackTrace();
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

  private String getContentHash(String content) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(hash);
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }
  
  public List<String> getLinks() {
    return this.Links;
  }
}
