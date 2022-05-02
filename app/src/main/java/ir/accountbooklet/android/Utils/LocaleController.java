package ir.accountbooklet.android.Utils;

import java.util.Calendar;
import java.util.Locale;

public class LocaleController {

  public static class LocaleDate {
    public int date;
    public int year;
    public int month;
    public int day;
    public int weekDay;
    public String strWeekDay;
    public String strMonth;
    public String strTime;

    public String getDate() {
      return date <= 0 ? "" : String.format(Locale.getDefault(), "%04d/%02d/%02d", year, month, day);
    }
    public String getDateBackup() {
      return date <= 0 ? "" : String.format(Locale.getDefault(), "%04d.%02d.%02d", year, month, day) + "_" + strTime.replace(":", ".");
    }

    public String getDateAndHour() {
      return date <= 0 ? "" : getDate() + " " + strTime;
    }

    public String getDateWithCalculate() {
      LocaleDate todayDate = LocaleController.getLocaleDate(System.currentTimeMillis() / 1000);
      return date <= 0 ? "" : todayDate.year == year && todayDate.month == month && (day <= todayDate.day && day >= todayDate.day - 6 && weekDay <= todayDate.weekDay) ? todayDate.day == day ? strTime : strWeekDay : todayDate.year != year ? getDateAndHour() : getDateLongYear();
    }

    public String getDateLongYear() {
      LocaleDate todayDate = LocaleController.getLocaleDate(System.currentTimeMillis() / 1000);
      return date <= 0 ? "" : todayDate.year != year ? strMonth + ", " + year : day + " " + strMonth;
    }
  }

  private static volatile LocaleController Instance = null;
  public static LocaleController getInstance() {
    LocaleController localInstance = Instance;
    if (localInstance == null) {
      synchronized (LocaleController.class) {
        localInstance = Instance;
        if (localInstance == null) {
          Instance = localInstance = new LocaleController();
        }
      }
    }
    return localInstance;
  }

  private LocaleDate createLocaleDate(long date) {
    LocaleDate localeDate = new LocaleDate();
    if (date <= 0) {
      return localeDate;
    }

    Calendar calendar = Calendar.getInstance();
    date *= 1000;
    calendar.setTimeInMillis(date);

    String[] weekDayNames = {"شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنج شنبه", "جمعه"};
    String[] monthNames = {"فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"};

    int ld;
    int mYear = calendar.get(Calendar.YEAR);
    int mMonth = calendar.get(Calendar.MONTH) + 1;
    int mDay = calendar.get(Calendar.DATE);
    int mWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
    if (mWeekDay == 7) {
      mWeekDay = 0;
    }
    int year;
    int month;
    int day;

    String strWeekDay = weekDayNames[mWeekDay];
    String strTime = String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

    int[] buf1 = new int[12];
    int[] buf2 = new int[12];

    buf1[0] = 0;
    buf1[1] = 31;
    buf1[2] = 59;
    buf1[3] = 90;
    buf1[4] = 120;
    buf1[5] = 151;
    buf1[6] = 181;
    buf1[7] = 212;
    buf1[8] = 243;
    buf1[9] = 273;
    buf1[10] = 304;
    buf1[11] = 334;

    buf2[0] = 0;
    buf2[1] = 31;
    buf2[2] = 60;
    buf2[3] = 91;
    buf2[4] = 121;
    buf2[5] = 152;
    buf2[6] = 182;
    buf2[7] = 213;
    buf2[8] = 244;
    buf2[9] = 274;
    buf2[10] = 305;
    buf2[11] = 335;

    if ((mYear % 4) != 0) {
      day = buf1[mMonth - 1] + mDay;

      if (day > 79) {
        day = day - 79;
        if (day <= 186) {
          if (day % 31 == 0) {
            month = day / 31;
            day = 31;
          } else {
            month = (day / 31) + 1;
            day = (day % 31);
          }
          year = mYear - 621;
        } else {
          day = day - 186;

          if (day % 30 == 0) {
            month = (day / 30) + 6;
            day = 30;
          } else {
            month = (day / 30) + 7;
            day = (day % 30);
          }
          year = mYear - 621;
        }
      } else {
        if ((mYear > 1996) && (mYear % 4) == 1) {
          ld = 11;
        } else {
          ld = 10;
        }
        day = day + ld;

        if (day % 30 == 0) {
          month = (day / 30) + 9;
          day = 30;
        } else {
          month = (day / 30) + 10;
          day = (day % 30);
        }
        year = mYear - 622;
      }
    } else {
      day = buf2[mMonth - 1] + mDay;

      if (mYear >= 1996) {
        ld = 79;
      } else {
        ld = 80;
      }
      if (day > ld) {
        day = day - ld;
        if (day <= 186) {
          if (day % 31 == 0) {
            month = (day / 31);
            day = 31;
          } else {
            month = (day / 31) + 1;
            day = (day % 31);
          }
          year = mYear - 621;
        } else {
          day = day - 186;

          if (day % 30 == 0) {
            month = (day / 30) + 6;
            day = 30;
          } else {
            month = (day / 30) + 7;
            day = (day % 30);
          }
          year = mYear - 621;
        }
      } else {
        day = day + 10;

        if (day % 30 == 0) {
          month = (day / 30) + 9;
          day = 30;
        } else {
          month = (day / 30) + 10;
          day = (day % 30);
        }
        year = mYear - 622;
      }
    }

    localeDate.date = (int) (date / 1000) ;
    localeDate.year = year;
    localeDate.month = month;
    localeDate.day = day;
    localeDate.weekDay = mWeekDay;
    localeDate.strMonth = monthNames[month - 1];
    localeDate.strWeekDay = strWeekDay;
    localeDate.strTime = strTime;
    return localeDate;
  }

  public static boolean equals(LocaleDate loc1, LocaleDate loc2) {
    if (loc1 == loc2) {
      return true;
    }
    return loc1 != null && loc2 != null && loc1.year == loc2.year && loc1.month == loc2.month && loc1.day == loc2.day;
  }

  public static LocaleDate getLocaleDate(long date) {
    return getInstance().createLocaleDate(date);
  }
}