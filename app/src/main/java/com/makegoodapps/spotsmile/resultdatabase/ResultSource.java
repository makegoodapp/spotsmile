package com.makegoodapps.spotsmile.resultdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ResultSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_MONTH
            , MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_TIME_SPAN, MySQLiteHelper.COLUMN_RESULT};

    public ResultSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createResultData(int month, int date, String timeSpan, int result) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MONTH, month);
        values.put(MySQLiteHelper.COLUMN_DATE, date);
        values.put(MySQLiteHelper.COLUMN_TIME_SPAN, timeSpan);
        values.put(MySQLiteHelper.COLUMN_RESULT, result);

        deleteResult(month, date);

        database.insert(MySQLiteHelper.TABLE_NAME_RESULTS, null, values);

    }

    public void deleteResult(int month, int date) {
        database.delete(MySQLiteHelper.TABLE_NAME_RESULTS, MySQLiteHelper.COLUMN_MONTH + "= '"
                + month + "'" + " AND " + MySQLiteHelper.COLUMN_DATE + "= '" + date + "'", null);
    }

    public void removeAllData() {
        database.delete(MySQLiteHelper.TABLE_NAME_RESULTS, null, null);
    }

    public boolean isEmpty() {
        boolean isEmpty = true;
        Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + MySQLiteHelper.TABLE_NAME_RESULTS, null);

        if(cur != null) {
            cur.moveToFirst();
            isEmpty = cur.getInt(0) == 0;
            cur.close();
            return isEmpty;
        }

        return isEmpty;
    }

    public Cursor getAllResultCursor() {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_RESULTS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getMonthResult(int month) {
        if (!database.isOpen()) {
            open();
        }

        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_RESULTS
                + " WHERE " + MySQLiteHelper.COLUMN_MONTH + "= '" + month + "'", null);

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            return cursor;
        }

        return null;
    }

    public ResultData getResultData(int month, int date) {

        if (!database.isOpen()) {
            open();
        }

        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_RESULTS
                + " WHERE " + MySQLiteHelper.COLUMN_MONTH + "= '" + month + "'"
                + " AND " + MySQLiteHelper.COLUMN_DATE + "= '" + date + "'", null);

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            ResultData resultData = cursorToResultData(cursor);
            cursor.close();
            return resultData;
        }

        return null;
    }

    private ResultData cursorToResultData(Cursor cursor) {
        ResultData result = new ResultData();
        result.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
        result.setMonth(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_MONTH)));
        result.setDate(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_DATE)));
        result.setTimeSpan(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TIME_SPAN)));
        result.setResult(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_RESULT)));

        return result;
    }
} 
