package com.example.urfu;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Route implements Parcelable {

    //private final HashMap<Integer, Point> mPointsInRoute;
    private final String mRouteName;
    private final String mRouteAltName;
    private final int mRouteCategoryId;
    private final int mRouteId;

    public Route(int routeId, String routeName, String routeAltName, int routeCategoryId, String language) {
        this.mRouteId = routeId;
        this.mRouteName = routeName;
        this.mRouteAltName = routeAltName;
        this.mRouteCategoryId = routeCategoryId;

        //getPointsFromHost(language);
    }
    protected Route(Parcel in) {

        mRouteName = in.readString();
        mRouteAltName = in.readString();
        mRouteCategoryId = in.readInt();
        mRouteId = in.readInt();

    }

    public String getName() { return mRouteName; }
    public String getAltName() { return mRouteAltName; }
    public int getCategoryId() { return mRouteCategoryId; }
    public int getId() { return mRouteId; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mRouteName);
        dest.writeString(mRouteAltName);
        dest.writeInt(mRouteCategoryId);
        dest.writeInt(mRouteId);
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
