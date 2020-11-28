package com.example.urfu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class Point implements Parcelable {

    private final int mId;
    private final String mName;
    private final String mAltName;
    private final double mLatitude;
    private final double mLongitude;
    private final String mDescription;
    private final String mAltDescription;
    private Bitmap mDescriptionImage;

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getAltName() {
        return mAltName;
    }

    public double getLatitude(){
        return  mLatitude;
    }

    public double getLongitude(){
        return  mLongitude;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getAltDescription(){
        return mAltDescription;
    }

    public Bitmap getDescriptionImage(){
        return mDescriptionImage;
    }



    public Point(int mId, String mName, String mAltName, double mLatitude, double mLongitude, String mDescription, String mAltDescription) {
        this.mId = mId;
        this.mName = mName;
        this.mAltName = mAltName;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mDescription = mDescription;
        this.mAltDescription = mAltDescription;
    }

    protected Point(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mAltName = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mDescription = in.readString();
        mAltDescription = in.readString();

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mAltName);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeString(mDescription);
        dest.writeString(mAltDescription);

    }
}
