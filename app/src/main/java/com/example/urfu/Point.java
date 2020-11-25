package com.example.urfu;

public class Point {

    private final int mId;
    private final String mName;
    private final String mAltName;

    public Point(int mId, String mName, String mAltName) {
        this.mId = mId;
        this.mName = mName;
        this.mAltName = mAltName;
    }

    public int getId() {

        return mId;
    }

    public String getName() {

        return mName;
    }

    public String getAltName() {

        return mAltName;
    }
}
