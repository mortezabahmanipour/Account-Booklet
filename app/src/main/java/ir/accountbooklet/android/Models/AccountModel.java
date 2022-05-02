package ir.accountbooklet.android.Models;

import android.graphics.Bitmap;

import java.util.List;

import ir.accountbooklet.android.Customs.RecyclerListView;

public class AccountModel extends RecyclerListView.BaseModel {
  public int id;
  public String accountName;
  public String mobile;
  public String phone;
  public String fatherName;
  public String address;
  public String description;
  public List<AmountsModel> amounts;
  public List<CardsModel> cards;
  public Bitmap bitmap;
  public int type = -1;
  public int pageNumber;
  public long date;
  public long dateClearing;

  public void clear() {
    accountName = null;
    fatherName = null;
    mobile = null;
    phone = null;
    address = null;
    description = null;
    amounts = null;
    cards = null;
    bitmap = null;
    type = -1;
    pageNumber = 0;
    date = 0;
    dateClearing = 0;
  }
}
