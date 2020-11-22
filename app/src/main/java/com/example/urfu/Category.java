package com.example.urfu;

public class Category {
    private int mId;
    private String mName;
    private String mAltName;

    public Category(int mId, String mName, String mAltName) {
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
