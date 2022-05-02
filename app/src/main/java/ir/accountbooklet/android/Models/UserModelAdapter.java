package ir.accountbooklet.android.Models;

public class UserModelAdapter {

  private int id;
  private String userId;
  private long userAmount;
  private String userName;
  private String userPhone;
  private String userHomePhone;
  private String startDate;

  public UserModelAdapter(int id, String userId, long userAmount, String userName, String userPhone, String userHomePhone, String startDate) {
    this.id = id;
    this.userId = userId;
    this.userAmount = userAmount;
    this.userName = userName;
    this.userPhone = userPhone;
    this.userHomePhone = userHomePhone;
    this.startDate = startDate;
  }

  public int getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getUserPhone() {
    return userPhone;
  }

  public String getUserHomePhone() {
    return userHomePhone;
  }

  public long getUserAmount() {
    return userAmount;
  }

  public String getStartDate() {
    return startDate;
  }
}
