package Crawler;

import java.util.List;
import java.util.ArrayList;

public class RobotRules {

  public List<String> disallowedRules;
  public boolean status;

  public RobotRules(boolean initStatus) {
    disallowedRules = new ArrayList<>();
    status = initStatus;
  }
}
