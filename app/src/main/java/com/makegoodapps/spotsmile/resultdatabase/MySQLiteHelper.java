package com.makegoodapps.spotsmile.resultdatabase;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_NAME_RESULTS = "spot_results";
  public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MONTH = "month";
  public static final String COLUMN_DATE = "date";
  public static final String COLUMN_TIME_SPAN = "time_span";
  public static final String COLUMN_RESULT = "results";

  private static final String DATABASE_NAME = "results.db";
  private static final int DATABASE_VERSION = 1;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table "
      + TABLE_NAME_RESULTS + "(" + COLUMN_ID + " integer primary key autoincrement, "
      + COLUMN_MONTH + " text not null, " + COLUMN_DATE + " text not null, "
      + COLUMN_TIME_SPAN + " text not null, "+ COLUMN_RESULT + " text not null "+ ");";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
            "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RESULTS);
    onCreate(db);
  }

} 