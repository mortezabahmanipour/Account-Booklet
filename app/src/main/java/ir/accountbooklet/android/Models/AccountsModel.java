package ir.accountbooklet.android.Models;

import java.util.Random;

import ir.accountbooklet.android.Customs.RecyclerListView;

public class AccountsModel extends RecyclerListView.BaseModel {
  public int id;
  public String accountName;
  public String mobile;
  public String phone;
  public long amount;
  public long date;
  public long dateClearing;

  public AccountsModel(int id, long amount, String accountName, String mobile, String phone, long date, long dateClearing, int type) {
    this.id = id;
    this.amount = amount;
    this.accountName = accountName;
    this.mobile = mobile;
    this.phone = phone;
    this.date = date;
    this.dateClearing = dateClearing;
    this.type = type;
    Random random = new Random();
//    this.thumbnailRndColor = Color.rgb(random.nextInt(50), random.nextInt(50) + 150, random.nextInt(100) + 100);
  }
}
