package fcu.selab.progedu.db;

import fcu.selab.progedu.data.Assignment;
import fcu.selab.progedu.project.ProjectTypeEnum;
import fcu.selab.progedu.utils.ExceptionUtil;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class AssignmentDbManagerTest {

    @Test
    public void addAssignment() {

        Assignment assignment = new Assignment();

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        try {
            date = format.parse(format.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }

        assignment.setName("unit-test-assignment");
        assignment.setCreateTime(date);
        assignment.setReleaseTime(date);
        assignment.setDeadline(date);
        assignment.setDescription("");
        assignment.setType(ProjectTypeEnum.getProjectTypeEnum("maven"));


        assignment.setHasTemplate(false);// Todo no need
        assignment.setTestZipChecksum(0);// Todo no need
        assignment.setTestZipUrl("");// Todo no need


        AssignmentDbManager assignmentDbManager = AssignmentDbManager.getInstance();
        assignmentDbManager.addAssignment(assignment);

        Assignment assignmentFromDB= assignmentDbManager.getAssignmentByName(assignment.getName());
        assertEquals(assignmentFromDB.getName(), assignment.getName());

        assignmentDbManager.deleteAssignment(assignment.getName());
    }
}