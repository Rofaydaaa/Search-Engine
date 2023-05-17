package Crawler;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SuppressWarnings("ALL")
public class Crawler implements Runnable {
  private static int maxPages = 6010; // Maximum number of pages to crawl
  public static int crawlersNumber = 300; // Number of crawlers
  private Set<String> visitedURLs; // Set of visited URLs
  private static List<String> URLs; // List of URLs to be crawled

  int id = 0; // Crawler ID
  Calendar cal; // Calender object
  Database DB; // Database object
  private RobotExclusion robotExculsion;

  // First Constructor
  public Crawler(Database DB, List<String> URLs, int id) {
    this.DB = DB;
    Crawler.URLs = URLs;
    this.id = id;
  }

  // Constructor
  public Crawler(Database DB, Set<String> visitedURLs, List<String> URLs, int id) {
    this.DB = DB;
    this.visitedURLs = visitedURLs;
    Crawler.URLs = URLs;
    robotExculsion = new RobotExclusion();
  }

  // Crawl the URL and store it in database
  public void crawl() throws MalformedURLException, URISyntaxException {
    String currURL = "";
    while (true) {
      synchronized (visitedURLs) // Synchronize the visited URLs as it is shared between threads
      {
        if (visitedURLs.size() >= maxPages) {
          System.out.println("\nFinished Crawling\n");
          break;
        }
      }
      currURL = "";
      synchronized (URLs) // Synchronize the URLs as it is shared between threads
      {
        if (!URLs.isEmpty()) { // Check if the List is empty
          currURL = URLs.remove(0); // Get the URL from the List
          if (!visitedURLs.contains(currURL) && currURL != "") { // Check if the URL is visited
            visitedURLs.add(currURL); // Add the URL to the visited URLs
          } else {
            continue; // Continue if the URL is visited
          }
        }
      }
      // Crawl the URL if not empty
      if (!currURL.equals("")) {
        Traverser traverser = new Traverser(robotExculsion);
        boolean errorFlag = traverser.traverse(DB, currURL);
        if (!errorFlag) {
          synchronized (visitedURLs) {
            // Remove the URL from the visited URLs if it is not crawled
            visitedURLs.remove(currURL);
          }
        } else {
          synchronized (URLs) {
            // Add the links to the List if the URL is crawled
            List<String> links = traverser.getLinks();
            for (String link : links) {
              if (!visitedURLs.contains(link)) {
                synchronized (visitedURLs) {
                  URLs.add(link);
                }
              }
            }
          }
        }
      }
    }
  }

  private static String getContentHash(String content) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(hash);
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }

  public static void main(String[] args) throws Exception {
    Set<String> visitedURLs = new HashSet<String>();
    List<String> URLs = new ArrayList<String>();
    List<Thread> threadsList = new ArrayList<Thread>();
    Thread thread1 = null;
    Database DB = new Database();
    DB.getQueue(URLs);
    DB.getVisited(visitedURLs);
    if (URLs.isEmpty() && visitedURLs.isEmpty()) {
      readSeed(URLs);
    }
    else if (!visitedURLs.isEmpty()) {
      // Add files in /htmldocs in a set
      File folder = new File(".//htmldocs");
      File[] listOfFiles = folder.listFiles();
      Set<String> files = new HashSet<String>();
      for (File file : listOfFiles) {
        if (file.isFile()) {
          files.add(file.getName());
        }
      }
      Set<String> temp = new HashSet<String>();
      DB.getURLHash(temp);
      // Get Difference between visited URLs and files in /htmldocs
      files.removeAll(temp);
      // Get the url of the hash codes from the database
      Set<String> temp2 = new HashSet<String>();
      DB.getURLFromHash(files,temp2);
      visitedURLs.addAll(temp2);
      // Add the difference to the Database
      for (String urlToBeAdded:temp2)
      {
          // Check if the url has the same content as a previously crawled page
          String contentHash = getContentHash(urlToBeAdded);
          if (DB.containsContentHash(contentHash)) {
        System.out.println("The url has the same content as a previously crawled page");
      }
      else{
        // Add the url to the database
          DB.addURL(urlToBeAdded, "htmldocs/" + urlToBeAdded.hashCode() + ".html",contentHash);
      }
    }
  }

    // Create threads
    for (int i = 0; i < crawlersNumber; i++) {
      thread1 = new Thread(new Crawler(DB, visitedURLs, URLs, i));
      thread1.setName(Integer.toString(i));
      threadsList.add(thread1);
      thread1.start();
    }

    // Wait for all threads to finish
    for (Thread thread : threadsList) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // Read the seeds from the file
  public static void readSeed(List<String> URLs) {
    try {
      File file = new File(".//seeds_list.txt");
      Scanner sc = new Scanner(file);
      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        URLs.add(line);
      }
      sc.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    try {
      crawl();
    } catch (MalformedURLException | URISyntaxException e) {
      System.out.println("Error in crawling");
      e.printStackTrace();
    }
  }


}


// // Recrawl
// public void recrawl() {
// String currURL = "";
// while (true) {
// if (Thread.currentThread().getName().equals("0")) {
// synchronized (URLs) // Synchronize the URLs as it is shared between threads
// {
// URLs.clear(); // Clear the List
// DB.getVisitedList(URLs); // Get the visited URLs from the database
// }
// DB.getDate(recrawlDate); // Get the date from the database
// }
// while ((new Date().after(recrawlDate)))
// ; // Wait until the recrawl time after 2 hours
// while (true) {
// synchronized (URLs) // Synchronize the URLs as it is shared between threads
// {
// if (URLs.isEmpty()) {
// Date date = new Date(); // Get the current time
// DB.updateDate(date); // Update the date
// break;
// }
// currURL = URLs.remove(0); // Get the URL from the List
// }
// // Crawl the URL if not empty
// if (!currURL.equals("")) {
// Traverser traverser = new Traverser(robotExculsion);
// traverser.Retraverse(DB, currURL); // Traverse the URL
// }
// }
// }
// }