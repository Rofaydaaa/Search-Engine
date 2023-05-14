package Crawler;

import java.io.*;
import java.net.*;
import java.util.*;

@SuppressWarnings("ALL")
public class Crawler implements Runnable {
  private static int maxPages = 6000; // Maximum number of pages to crawl
  public static int crawlersNumber = 100; // Number of crawlers
  private Set<String> visitedURLs; // Set of visited URLs
  private static List<String> URLs; // List of URLs to be crawled

  int id = 0; // Crawler ID
  Calendar cal; // Calender object
  Database DB; // Database object
  boolean recrawlflag;// Recrawl the database
  Date recrawlDate; // Recrawl date
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
    this.id = id;
    recrawlDate = new Date();
    robotExculsion = new RobotExclusion();
    recrawlflag = false;
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
          if (!visitedURLs.contains(currURL)&& currURL !="" ) { // Check if the URL is visited
            visitedURLs.add(currURL); // Add the URL to the visited URLs
          } else {
            continue; // Continue if the URL is visited
          }
        }
      }
      // Crawl the URL if not empty
      if (!currURL.equals("")) {
        Traverser traverser = new Traverser(robotExculsion);
        boolean c = traverser.traverse(DB, currURL); // Traverse the URL
        if (!c)
        {
          synchronized (visitedURLs)
          {
          visitedURLs.remove(currURL); // Remove the URL from the visited URLs if it is not crawled
          }
        }
        else{
        synchronized (URLs) // Synchronize the URLs as it is shared between threads
        {
          URLs.addAll(traverser.getLinks()); // add the all href and link found to the List
        }
      }
      }
    }
    //Date date = new Date(); // Get the current time
    //DB.updateDate(date); // Update the date in the database after finishing crawling
    recrawlflag = true; // Set the recrawl to true
  }

  // // Recrawl
  // public void recrawl() {
  //   String currURL = "";
  //   while (true) {
  //     if (Thread.currentThread().getName().equals("0")) {
  //       synchronized (URLs) // Synchronize the URLs as it is shared between threads
  //       {
  //         URLs.clear(); // Clear the List
  //         DB.getVisitedList(URLs); // Get the visited URLs from the database
  //       }
  //       DB.getDate(recrawlDate); // Get the date from the database
  //     }
  //     while ((new Date().after(recrawlDate)))
  //       ; // Wait until the recrawl time after 2 hours
  //     while (true) {
  //       synchronized (URLs) // Synchronize the URLs as it is shared between threads
  //       {
  //         if (URLs.isEmpty()) {
  //           Date date = new Date(); // Get the current time
  //           DB.updateDate(date); // Update the date
  //           break;
  //         }
  //         currURL = URLs.remove(0); // Get the URL from the List
  //       }
  //       // Crawl the URL if not empty
  //       if (!currURL.equals("")) {
  //         Traverser traverser = new Traverser(robotExculsion);
  //         traverser.Retraverse(DB, currURL); // Traverse the URL
  //       }
  //     }
  //   }
  // }

  public static void main(String[] args) throws Exception {
    Set<String> visitedURLs = new HashSet<String>();
    List<String> URLs = new LinkedList<String>();
    List<Thread> threadsList = new ArrayList<Thread>();
    Thread thread1 = null;
    Database DB = new Database();
    DB.getQueue(URLs);
    DB.getVisited(visitedURLs);
    if (URLs.isEmpty() && visitedURLs.isEmpty()) {
      readSeed(URLs);
    }

    // Calculate the number of URLs to be crawled by each thread
    int numURLsPerThread = URLs.size() / crawlersNumber;

    // Create threads
    for (int i = 0; i < crawlersNumber; i++) {
      int startIndex = i * numURLsPerThread;
      int endIndex = (i == crawlersNumber - 1) ? URLs.size() : (i + 1) * numURLsPerThread;
      List<String> subURLs = URLs.subList(startIndex, endIndex);
      thread1 = new Thread(new Crawler(DB, visitedURLs, subURLs, i));
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
  //   if (recrawlflag)
  //     recrawl();
  }
}
