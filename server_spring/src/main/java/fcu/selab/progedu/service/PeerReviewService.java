package fcu.selab.progedu.service;

import javax.ws.rs.core.Response;
import fcu.selab.progedu.data.PairMatching;
import fcu.selab.progedu.data.ReviewSetting;
import fcu.selab.progedu.data.Assignment;
import fcu.selab.progedu.data.User;
import fcu.selab.progedu.db.AssignmentDbManager;
import fcu.selab.progedu.db.ReviewSettingDbManager;
import fcu.selab.progedu.db.PairMatchingDbManager;
import fcu.selab.progedu.db.UserDbManager;

import fcu.selab.progedu.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.sql.SQLException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping(value ="/peerReview")
public class PeerReviewService {

  private AssignmentDbManager assignmentDbManager = AssignmentDbManager.getInstance();
  private ReviewSettingDbManager reviewSettingDbManager = ReviewSettingDbManager.getInstance();
  private PairMatchingDbManager pairMatchingDbManager = PairMatchingDbManager.getInstance();
  private UserDbManager userDbManager = UserDbManager.getInstance();
  private static final Logger LOGGER = LoggerFactory.getLogger(PeerReviewService.class);

  /**
   * get one user's status of reviewing other's hw
   *
   * @param username user name
   */
	@GetMapping("/status/oneUser")
	public ResponseEntity<Object> getReviewStatus(
					@RequestParam("username") String username) {

		HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss.S");
		
		try {
			List<Assignment> assignmentList = assignmentDbManager.getAllReviewAssignment();
			int reviewId = userDbManager.getUserIdByUsername(username);
			JSONArray jsonArray = new JSONArray();

			for(Assignment assignment : assignmentList) {
				ReviewSetting reviewSetting = reviewSettingDbManager.getReviewSetting(assignment.getId());
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("assignmentName", assignment.getName());
        jsonObject.put("amount", reviewSetting.getAmount());
        jsonObject.put("releaseTime", dateFormat.format(assignment.getReleaseTime()));
        jsonObject.put("deadline", dateFormat.format(assignment.getDeadline()));
        jsonObject.put("reviewReleaseTime", dateFormat.format(reviewSetting.getReleaseTime()));
        jsonObject.put("reviewDeadline", dateFormat.format(reviewSetting.getDeadline()));
        jsonObject.put("count", getReviewCompletedCount(assignment.getId(), reviewId));
        jsonObject.put("status", reviewerStatus(assignment.getId(),
            reviewId, reviewSetting.getAmount()).getTypeName());
				
				jsonArray.add(jsonObject);
			}
    	return new ResponseEntity<Object>(jsonArray, headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

  /**
   * get all user's status of reviewing other's hw
   */
  @GetMapping("/status/allUsers")
  public ResponseEntity<Object> getAllReviewStatus() {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");

    try {
      JSONArray array = new JSONArray();
      JSONObject result = new JSONObject();
      List<User> users = getStudents();
      for (User user : users) {
        String username = user.getUsername();
        ResponseEntity<Object> reviewStatus = getReviewStatus(username);
        JSONObject ob = new JSONObject();
        ob.put("username", username);
        ob.put("name", user.getName());
        ob.put("display", user.getDisplay());
        ob.put("reviewStatus", reviewStatus.getBody());

        array.add(ob);
      }
      result.put("allReviewStatus", array);
      return new ResponseEntity<Object>(result, headers, HttpStatus.OK);
    } catch (Exception e){
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
      return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

	private int getReviewCompletedCount(int aid, int reviewId) throws SQLException {
    List<PairMatching> pairMatchingList =
        pairMatchingDbManager.getPairMatchingByAidAndReviewId(aid, reviewId);
    int count = 0;

    for (PairMatching pairMatching : pairMatchingList) {
      if (pairMatching.getReviewStatusEnum().equals(ReviewStatusEnum.COMPLETED)) {
        count++;
      }
    }

    return count;
  }

	/**
   * check reviewer status of his/her review job
   *
   * @param aid      assignment id
   * @param reviewId user id
   */
  private ReviewStatusEnum reviewerStatus(int aid, int reviewId, int amount) throws SQLException {
    List<PairMatching> pairMatchingList =
        pairMatchingDbManager.getPairMatchingByAidAndReviewId(aid, reviewId);
    ReviewStatusEnum resultStatus = ReviewStatusEnum.INIT;
    int initCount = 0;

    for (PairMatching pairMatching : pairMatchingList) {
      if (pairMatching.getReviewStatusEnum().equals(ReviewStatusEnum.UNCOMPLETED)) {
        resultStatus = ReviewStatusEnum.UNCOMPLETED;
        break;
      } else if (pairMatching.getReviewStatusEnum().equals(ReviewStatusEnum.COMPLETED)) {
        resultStatus = ReviewStatusEnum.COMPLETED;
      } else if (pairMatching.getReviewStatusEnum().equals(ReviewStatusEnum.INIT)) {
        initCount++;
      }
    }

    if (initCount == amount) {
      resultStatus = ReviewStatusEnum.INIT;
    }

    return resultStatus;
  }

  /**
   * Get all user which role is student
   *
   * @return all GitLab users
   */
  public List<User> getStudents() {
    List<User> studentUsers = new ArrayList<>();
    List<User> users = userDbManager.getAllUsers();

    for (User user : users) {
      if (user.getRole().contains(RoleEnum.STUDENT)) {
        studentUsers.add(user);
      }
    }
    return studentUsers;
  }
}