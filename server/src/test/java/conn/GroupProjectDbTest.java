package conn;

import javax.ws.rs.core.Response;

import org.junit.Test;

import fcu.selab.progedu.db.service.GroupDbService;
import fcu.selab.progedu.db.service.ProjectDbService;
import fcu.selab.progedu.service.GroupProjectContributionAnalysisService;

public class GroupProjectDbTest {
  ProjectDbService pdb = ProjectDbService.getInstance();
  GroupDbService gdb = GroupDbService.getInstance();
  String groupName = "group4";
  String projectName = "project4";

  @Test
  public void m1() {
    GroupProjectContributionAnalysisService gpcs = new GroupProjectContributionAnalysisService();

    Response r = gpcs.getCommitStatus(groupName, projectName);
    System.out.println(r.getEntity());
  }
}
