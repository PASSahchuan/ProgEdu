package fcu.selab.progedu.data;

import java.util.Date;

public class ReviewRecord {

  private int id;

  private int roId;

  private int rsmId;

  private int score;

  private Date time;

  private String feedback;

  private int round;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getRoId() {
    return roId;
  }

  public void setRoId(int roId) {
    this.roId = roId;
  }

  public int getRsmId() {
    return rsmId;
  }

  public void setRsmId(int rsmId) {
    this.rsmId = rsmId;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  public int getRound() {
    return round;
  }

  public void setRound(int round) {
    this.round = round;
  }
}
