package com.omnizia.pubmedservice.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.stream.Stream;

public class HostnameUtils {

  public static String getBiopythonAppHostname() {
    /* if (isRunningInDocker()) {
      return "http://biopython-service:5000";
    } else {
      return "http://localhost:5000";
    }*/
    return "http://biopython-service:5000";
  }

  public static String getDBConfigHostname() {
    /*if (isRunningInDocker()) {
      return "spring-job-config-db:5432";
    } else {
      return "localhost:5434";
    }*/

    return "spring-job-config-db:5432";
  }

  private static boolean isRunningInDocker() {
    Path cgroupPath = Paths.get("/proc/1/cgroup");
    try (Stream<String> lines = Files.lines(cgroupPath)) {
      return lines.anyMatch(line -> line.contains("docker"));
    } catch (Exception e) {
      return false;
    }
  }
}
