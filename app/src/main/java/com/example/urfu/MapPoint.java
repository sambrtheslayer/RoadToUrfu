package com.example.urfu;

import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

public class MapPoint {

    private GeoPoint mGeoPoint;
    private OverlayItem mOverlayItem;
    private int mId;

    public void setIcon(Drawable icon) {
        if (mOverlayItem != null) {
            mOverlayItem.setMarker(icon);
        }
    }

    public void setId(int id) {
        mId = id;
    }
}
