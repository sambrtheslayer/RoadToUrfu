package com.example.urfu;

public enum Language {
    Chinese("0"),
    English("1");

    private String mId;

    Language(String id) {
        mId = id;
    }

    public String getId() { return mId; };
}
