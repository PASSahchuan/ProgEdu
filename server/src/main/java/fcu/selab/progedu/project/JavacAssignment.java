package fcu.selab.progedu.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fcu.selab.progedu.conn.JenkinsService;
import fcu.selab.progedu.service.StatusService;
import fcu.selab.progedu.status.StatusEnum;
import fcu.selab.progedu.utils.ExceptionUtil;

public class JavacAssignment extends ProjectType {
  private static final Logger LOGGER = LoggerFactory.getLogger(JavacAssignment.class);

  @Override
  public ProjectTypeEnum getProjectType() {
    return ProjectTypeEnum.JAVAC;
  }

  @Override
  public String getSampleTemplate() {
    return "JavacQuickStart.zip";
  }

  @Override
  public String getJenkinsJobConfigPath() {
    URL url = this.getClass().getResource("/jenkins/config_javac.xml");
    return url.getPath();
  }

  @Override
  public StatusEnum checkStatusType(int num, String username, String assignmentName) {
    StatusEnum status;
    StatusService statusService = StatusService.getInstance();
    if (statusService.isInitialization(num)) {
      status = StatusEnum.INITIALIZATION;
    } else {
      JenkinsService jenkinsService = JenkinsService.getInstance();
      String jobName = username + "_" + assignmentName;
      String console = jenkinsService.getConsole(jobName, num);

      if (statusService.isBuildSuccess(console)) {
        status = StatusEnum.BUILD_SUCCESS;
      } else {
        status = StatusEnum.COMPILE_FAILURE;
      }
    }
    return status;
  }

  /**
   * Create a file with compile command in test directory
   * @param assignmentPath assignmentPath
   */
  private void createCommandFile(String assignmentPath) {
    String command = searchJavaFile(assignmentPath);
    List<String> lines = Arrays.asList(command);
    Path file = Paths.get(assignmentPath + "-command");
    try {
      Files.write(file, lines, StandardCharsets.UTF_8);
    } catch (IOException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
    }
  }

  /**
   * Raed the command file in test directory and return command string
   *
   * @param assignmentPath assignmentPath
   */
  private String getCommandFromFile(String assignmentPath) {
    StringBuilder sb = new StringBuilder();

    try (BufferedReader br = Files.newBufferedReader(Paths.get(assignmentPath + "-command"))) {
      String line;

      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } catch (IOException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
    }
    return sb.toString();
  }

  /**
   * Search all Java file in this assignment
   *
   * @param assignmentPath assignmentPath
   */
  public String searchJavaFile(String assignmentPath) {
    List<String> fileList = null;
    String assignmentName = new File(assignmentPath).getName();

    try (Stream<Path> walk = Files.walk(Paths.get(assignmentPath))) {

      fileList = walk.filter(Files::isRegularFile).map(x -> x.toString())
          .filter(f -> f.endsWith(".java")).collect(Collectors.toList());

    } catch (IOException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
    }
    System.out.print(getCommand(fileList, assignmentName));
    return getCommand(fileList, assignmentName);
  }

  /**
   *
   * @param fileList fileList
   * @param assignmentName assignmentName
   */
  private String getCommand(List<String> fileList, String assignmentName) {
    String command = "";

    for (String absolutePath : fileList) {
      String subPath = absolutePath
          .substring(absolutePath.indexOf(assignmentName) + assignmentName.length() + 1);
      
      // For windows to linux, todo [change to use Path()]
      subPath = subPath.replace("\\", "/"); 

      command += "javac " + subPath + "\n";
    }
    command += "echo \"BUILD SUCCESS\"";

    return command;
  }
}
