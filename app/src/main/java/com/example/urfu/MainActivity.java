package com.example.urfu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private MapView map = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserLocationPermission();

        /*Configuration.getInstance().setCachePath(...)
        Configuration.getInstance().setCacheSizes(...)
        Configuration.getInstance().setOfflineMapsPath(...)
        Configuration.getInstance().setUserAgentValue(...) */

        //region Location
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context),mMapView);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);
        //endregion


        Configuration.getInstance().setOsmdroidBasePath(new File(getCacheDir().getAbsolutePath(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath().getAbsolutePath(), "tile"));
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);

        // Отключение дублирования карты гориз и верт.
        map.setVerticalMapRepetitionEnabled(false);
        map.setHorizontalMapRepetitionEnabled(false);

        map.setTileSource(TileSourceFactory.MAPNIK);

        //region  Marks
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Title", "Description", new GeoPoint(56.800091d,59.909221d))); // Lat/Lon decimal degrees

            //the overlay

        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Log.e("HUI", "tapped");
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Log.e("HUI2", "long tapped");
                        return true;
                    }
                }, ctx);

        mOverlay.setFocus(items.get(0));
        map.getOverlays().add(mOverlay);
        //endregion
    }


    private void checkUserLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
    }
}