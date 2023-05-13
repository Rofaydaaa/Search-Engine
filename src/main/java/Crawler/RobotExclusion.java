package Crawler;

import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public class RobotExclusion {

  // Member Variables
  private ConcurrentHashMap<String, RobotRules> websiteRules = new ConcurrentHashMap<>();
  String userAgent = "*";

  // Check if the URL is allowed to be crawled
  public boolean robotSafe(URL url) throws URISyntaxException {
    String baseURL = url.getHost();
    String urlString = url.toString();

    // Prepare the robots.txt file for the given URL
    checkRobotsText(url, baseURL);

    // Check if the URL is allowed to be crawled
    boolean allowed = RobotRuleParser.isDisallowedRules(urlString, websiteRules.get(baseURL).disallowedRules);

    return !allowed;
  }

  // Get the robots.txt file and return it as a list of strings
  public static List<String> getRobotTxt(String domain) throws URISyntaxException {
    List<String> robotTxt = new ArrayList<>();

    try {
      // Get the robots.txt file
      URI uri = new URI(domain + "/robots.txt");
      URL url = uri.toURL();
      // Set the connection timeout to 40 seconds
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setReadTimeout(40000
      );
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      String line;
      while ((line = in.readLine()) != null) {
        robotTxt.add(line);
      }
      in.close(); // Close the BufferedReader
    } catch (FileNotFoundException e) {
      //System.err.println("Robots.txt file not found: " + e.getMessage());
    } catch (IOException e) {
      //System.err.println("Error reading robots.txt file: " + e.getMessage());
    }

    return robotTxt;
  }

  // Update Rules if there are any changes
  private void checkRobotsText(URL url, String baseURL) throws URISyntaxException {
    RobotRules rules = websiteRules.putIfAbsent(baseURL, new RobotRules(false)); // If absent then add it to the map as false

    // If the robot text is not fetched yet then fetch it and parse it
    if (rules == null) { //
      updateRules(baseURL, RobotRuleParser.parse(getRobotTxt(url.toString()), userAgent));
      return;
    }

    // If the robot text is fetched but not parsed yet then wait for it to be parsed
    // and it is synchronized so that only one thread can access it at a time
    synchronized (rules) {
      while (!rules.status) {
        try {
          rules.wait();
        } catch (InterruptedException e) {
          System.err.println("Error in waiting for the rules to be updated: " + e.getMessage());
        }
      }
    }
  }

  // Update the rules in the map
  // synchronized so that only one thread can access it at a time
  private void updateRules(String baseURL, List<String> rules) {
    RobotRules robotRules = websiteRules.get(baseURL);
    synchronized (robotRules) {
      robotRules.disallowedRules = rules;
      robotRules.status = true;
      robotRules.notifyAll();
    }
  }
}
