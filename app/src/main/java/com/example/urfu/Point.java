package com.example.urfu;

import android.os.Parcel;
import android.os.Parcelable;

public class Point implements Parcelable {

    private final int mId;
    private final String mName;
    private final String mAltName;

    public Point(int mId, String mName, String mAltName) {
        this.mId = mId;
        this.mName = mName;
        this.mAltName = mAltName;
    }

    protected Point(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mAltName = in.readString();
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public int getId() {

        return mId;
    }

    public String getName() {

        return mName;
    }

    public String getAltName() {

        return mAltName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mAltName);

        //dest.writeStringArray(new String[] {String.valueOf(mId), mName, mAltName });
    }
}
