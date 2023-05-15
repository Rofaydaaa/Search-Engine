package Crawler;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RobotRuleParser {

  public static List<String> parse(List<String> robotText, String userAgent) {
    List<String> disallowedRules = new ArrayList<>();
    String tempUserAgent = null;
    for (String line : robotText) {
      line = line.toLowerCase();
      if (line.isEmpty() || line.startsWith("#")) // If empty or comment
      {
        continue;
      }
      if (line.startsWith("user-agent:")) {
        tempUserAgent = line.substring("user-agent:".length()).trim(); // Get the user agent
      } // If the user agent is the same as the one we are looking for
      else if (tempUserAgent != null && (tempUserAgent.equals("*") || tempUserAgent.equals(userAgent))) {
        if (line.startsWith("disallow:")) {
          String disallowPath = line.substring("disallow:".length()).trim();
          disallowPath = disallowPath.replaceAll("\\*", ".*"); // replace wildcard character with regex
          disallowPath = disallowPath.replaceAll("\\?", "[?]"); // replace question mark with regex
          // Add to the list
          if (!disallowPath.isEmpty())
            disallowedRules.add(disallowPath);
        }
      }
    }
    return disallowedRules;
  }

  public static boolean isDisallowedRules(String url, List<String> disallowedRules) {
    // Compile the patterns and store them in a list
    for (String rule : disallowedRules) {
      try {
        Pattern pattern = Pattern.compile(rule);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
          return true;
        }
      } catch (PatternSyntaxException e) {
        System.err.println("Invalid regex pattern: " + e.getMessage());
      }
    }
    return false;
  }
}
