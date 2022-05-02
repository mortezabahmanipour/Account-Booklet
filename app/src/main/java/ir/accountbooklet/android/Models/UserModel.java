package ir.accountbooklet.android.Models;

public class UserModel {

  private String userId;
  private long userAmount;
  private String userName;
  private String userFatherName;
  private String userPhone;
  private String userHomePhone;
  private String userAddress;
  private int pageNumber;
  private long startDateAto;
  private long finishDate;
  private String userDescription;
  private int type;

  public UserModel(String userId, long userAmount, String userName, String userFatherName, String userPhone, String userHomePhone, String userAddress, int pageNumber, long startDateAto, long finishDate, String userDescription, int type) {
    this.userId = userId;
    this.userAmount = userAmount;
    this.userName = userName;
    this.userFatherName = userFatherName;
    this.userPhone = userPhone;
    this.userHomePhone = userHomePhone;
    this.userAddress = userAddress;
    this.pageNumber = pageNumber;
    this.startDateAto = startDateAto;
    this.finishDate = finishDate;
    this.userDescription = userDescription;
    this.type = type;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getUserFatherName() {
    return userFatherName;
  }

  public String getUserAddress() {
    return userAddress;
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

  public String getUserDescription() {
    return userDescription;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public long getStartDateAto() {
    return startDateAto;
  }

  public long getFinishDate() {
    return finishDate;
  }

  public int getType() {
    return type;
  }
}
