package com.makegoodapps.spotsmile;

public enum TimeSpanEnum {
	
	TEST_SPAN(1), THREE(3), FIVE(5), TEN(10);

    private final int value;

    private TimeSpanEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


