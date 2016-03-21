package com.makegoodapps.spotsmile.resultdatabase;

import java.util.Comparator;

public class ResultData {

	private long mId;
    private int mMonth;
	private int mDate;
	private String mTimeSpan;
	private int mResult;

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }
	
	public int getDate() {
		return mDate;
	}

	public void setDate(int date) {
		mDate = date;
	}

	public String getTimeSpan() {
		return mTimeSpan;
	}

	public void setTimeSpan(String timeSpan) {
		mTimeSpan = timeSpan;
	}

	public int getResult() {
		return mResult;
	}

	public void setResult(int result) {
		mResult = result;
	}

} 
