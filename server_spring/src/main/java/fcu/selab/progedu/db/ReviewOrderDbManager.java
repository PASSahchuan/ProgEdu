package fcu.selab.progedu.db;

import fcu.selab.progedu.data.ReviewOrder;
import fcu.selab.progedu.service.ReviewStatusEnum;
import fcu.selab.progedu.status.StatusEnum;
import fcu.selab.progedu.utils.ExceptionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * review order dbManager.
 */
public class ReviewOrderDbManager {
  private static ReviewOrderDbManager dbManager = new ReviewOrderDbManager();

  public static ReviewOrderDbManager getInstance() {
    return dbManager;
  }

  private IDatabase database = MySqlDatabase.getInstance();

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewOrderDbManager.class);

  public ReviewStatusDbManager reviewStatusDbManager = ReviewStatusDbManager.getInstance();

  /**
   * insert review order.
   *
   * @param pmId pm id
   *
   * @param reviewStatusEnum review status enum
   * @param reviewOrder review status
   * @throws SQLException sqlException
   */
  public void insertReviewOrder(int pmId, ReviewStatusEnum reviewStatusEnum, int reviewOrder) {
    String sql = "INSERT INTO Review_Order(pmId, status, reviewOrder) VALUES (? ,? , ?)";

    Connection conn = null;
    PreparedStatement preStmt = null;

    try {
      conn = database.getConnection();
      preStmt = conn.prepareStatement(sql);
      int reviewStatus = reviewStatusDbManager.getReviewStatusIdByStatus(reviewStatusEnum.toString());

      preStmt.setInt(1, pmId);
      preStmt.setInt(2, reviewStatus);
      preStmt.setInt(3, reviewOrder);

      preStmt.executeQuery();
    } catch (SQLException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
    } finally {
      CloseDBUtil.closeAll(preStmt, conn);
    }
  }

  /**
   * get Review Order By PmId.
   *
   * @param pmId pm id
   * @return review order
   * @throws SQLException exception
   */
  public ReviewOrder getAllReviewOrderByPmId(int pmId, int round) {

    String sql = "SELECT * FROM ProgEdu.Review_Order WHERE pmId = ? AND round = ?";

    Connection conn = null;
    PreparedStatement preStmt = null;
    ResultSet rs = null;

    ReviewOrder reviewOrder = new ReviewOrder();

    try {
      conn = database.getConnection();
      preStmt = conn.prepareStatement(sql);

      preStmt.setInt(1, pmId);
      preStmt.setInt(2, round);
      rs = preStmt.executeQuery();
      while (rs.next()) {
        int statusId = rs.getInt("status");
        reviewOrder.setId(rs.getInt("id"));
        reviewOrder.setPmId(pmId);
        reviewOrder.setReviewStatusEnum(reviewStatusDbManager.getReviewStatusById(statusId));
        reviewOrder.setReviewOrder(rs.getInt("round"));
      }
    } catch (SQLException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
    } finally {
      CloseDBUtil.closeAll(rs, preStmt, conn);
    }
    return reviewOrder;
  }


  /**
   * get review status by assignment id.
   *
   * @param aId assignment id
   * @return review ststus enum
   * @throws SQLException exception
   */
  public List<ReviewOrder> getReviewOrderByAid(int aId, int round) {
    String sql = "SELECT r_o.* FROM Pair_Matching AS p_m, " +
            "Assignment_User AS a_u, Review_Order AS r_o " +
            "WHERE p_m.auId = a_u.id AND p_m.id = r_o.pmId AND a_u.aId = ? AND r_o.round = ?";

    List<ReviewOrder> reviewOrderList = new ArrayList<>();

    Connection conn = null;
    PreparedStatement preStmt = null;
    ResultSet rs = null;

    try {
      conn = database.getConnection();
      preStmt = conn.prepareStatement(sql);

      preStmt.setInt(1, aId);
      preStmt.setInt(2, round);
      rs = preStmt.executeQuery();
      while (rs.next()) {
        ReviewOrder reviewOrder = new ReviewOrder();
        reviewOrder.setId(rs.getInt("id"));
        reviewOrder.setPmId(rs.getInt("pmId"));
        reviewOrder.setReviewStatusEnum(reviewStatusDbManager.getReviewStatusById(rs.getInt("status")));
        reviewOrder.setReviewOrder(rs.getInt("round"));
        reviewOrderList.add(reviewOrder);
      }
    } catch (SQLException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
    } finally {
      CloseDBUtil.closeAll(rs, preStmt, conn);
    }
    return reviewOrderList;
  }

  /**
   * update review status by id.
   *
   * @param id review order id
   * @param status status
   */
  public void updateReviewStatusById(int id, int status) {
    String sql = "UPDATE Review_Order SET status = ? WHERE id = ?";

    Connection conn = null;
    PreparedStatement preStmt = null;

    try {
      conn = database.getConnection();
      preStmt = conn.prepareStatement(sql);

      preStmt.setInt(1, status);
      preStmt.setInt(2, id);
      preStmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
    } finally {
      CloseDBUtil.closeAll(preStmt, conn);
    }
  }

  /**
   * delete review order by id.
   *
   * @param id review order id
   */
  public void deleteReviewOrderById(int id) {
    String sql = "DELETE FROM Review_Order WHERE id = ?";

    Connection conn = null;
    PreparedStatement preStmt = null;

    try {
      conn = database.getConnection();
      preStmt = conn.prepareStatement(sql);

      preStmt.setInt(1, id);
      preStmt.executeUpdate();
    } catch (SQLException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.toString());
    } finally {
      CloseDBUtil.closeAll(preStmt, conn);
    }
  }

  /**
   * get review status by review order and pmId.
   *
   * @param pmId peer match id
   * @param reviewOrder review order
   */
  public ReviewStatusEnum getReviewStatusEnumByPmIdAndReviewOrder(int pmId, int reviewOrder) {
    String sql = "SELECT `status` FROM ProgEdu.Review_Order WHERE `pmId` = ? AND `reviewOrder` = ?";
    ReviewStatusEnum reviewStatusEnum = null;

    Connection conn = null;
    PreparedStatement preStmt = null;
    ResultSet rs = null;

    try {
      conn = database.getConnection();
      preStmt = conn.prepareStatement(sql);

      preStmt.setInt(1, pmId);
      preStmt.setInt(2, reviewOrder);

      rs = preStmt.executeQuery();
      while (rs.next()) {
        int reviewStatusId = rs.getInt("status");
        reviewStatusEnum = reviewStatusDbManager.getReviewStatusById(reviewStatusId);
      }
    } catch (SQLException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
    } finally {
      CloseDBUtil.closeAll(rs, preStmt, conn);
    }
    return reviewStatusEnum;
  }

  /**
   * get review order id by review order and pmId.
   *
   * @param pmId peer match id
   * @param reviewOrder review order
   * @return roId review order id
   */
  public int getReviewOrderId(int pmId, int reviewOrder) {
    String sql = "SELECT `id` FROM ProgEdu.Review_Order WHERE `pmId` = ? AND `reviewOrder` = ?";
    int roId = -1;

    Connection conn = null;
    PreparedStatement preStmt = null;
    ResultSet rs = null;

    try {
      conn = database.getConnection();
      preStmt = conn.prepareStatement(sql);

      preStmt.setInt(1, pmId);
      preStmt.setInt(2, reviewOrder);

      rs = preStmt.executeQuery();
      while (rs.next()) {
        roId = rs.getInt("id");
      }
    } catch (SQLException e) {
      LOGGER.debug(ExceptionUtil.getErrorInfoFromException(e));
      LOGGER.error(e.getMessage());
    } finally {
      CloseDBUtil.closeAll(rs, preStmt, conn);
    }
    return roId;
  }
}