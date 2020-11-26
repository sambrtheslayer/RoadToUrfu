package com.example.urfu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private MapView map = null;
    private MyLocationNewOverlay locationOverlay;
    private LocationManager locationManager;
    // private MyLocationListener listener;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserLocationPermission();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //region Cache configuration
        Configuration.getInstance().setOsmdroidBasePath(new File(getCacheDir().getAbsolutePath(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath().getAbsolutePath(), "tile"));
        //endregion

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.map_activity);

        // Инициализация объекта MapView.
        map = (MapView) findViewById(R.id.map);

        //region Map Settings
        // Отключение дублирования карты гориз и верт.
        map.setVerticalMapRepetitionEnabled(false);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        //endregion

        //region IMapController(zoom, center, etc)
        // Отвечает за масштабирование и начальную точку.
        IMapController mapController = map.getController();
        mapController.setZoom(19.5);
        GeoPoint startPoint = new GeoPoint(56.800091d, 59.909221d);
        mapController.setCenter(startPoint);
        //endregion

        //region Marks
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Title", "Description", new GeoPoint(56.800091d,59.909221d))); // Lat/Lon decimal degrees
        items.get(0).setMarker(getDrawable(R.drawable.wine_bottle));
        //the overlay
        items.add(new OverlayItem("Title", "Description", new GeoPoint(56.79511011, 59.9230577)));
        items.get(1).setMarker(getDrawable(R.drawable.place_holder));

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

        //region User Location

        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        locationOverlay = new MyLocationNewOverlay(provider, map);
        locationOverlay.enableFollowLocation();

        // Отвечает за первый старт локации (полезно ли?).
        locationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                Log.e("MyTag", String.format("First location fix: %s", locationOverlay.getLastFix()));
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);

        map.getOverlayManager().add(locationOverlay);

        //endregion


        //region Roads and Routes

        try {


            RoadManager roadManager = new GraphHopperRoadManager("0382a8c3-5f12-4c7a-918b-f42298e68f7b", false);
            roadManager.addRequestOption("vehicle=foot");
            //roadManager.addRequestOption("optimize=true");

            ArrayList<GeoPoint> waypoints = new ArrayList<>();

            GeoPoint waypoint1 = new GeoPoint(56.800091d, 59.909221d);
            GeoPoint waypoint2 = new GeoPoint(56.79511011d, 59.9230577d);
            waypoints.add(waypoint1);
            waypoints.add(waypoint2);

            Log.e("Waypoints size", String.valueOf(waypoints.size()));
            Log.e("Waypoints", waypoints.toString());

            Road road = roadManager.getRoad(waypoints);

            Log.e("road high", road.mRouteHigh.toString());
            Log.e("road legs", road.mLegs.toString());
            Log.e("status", String.valueOf(road.mStatus));

            Polyline roadOverlay = GraphHopperRoadManager.buildRoadOverlay(road);

            map.getOverlays().add(roadOverlay);

            map.invalidate();
        }
        catch(Exception e)
        {
            Log.e("ex", e.getMessage());
        }

        //endregion

        //Point point = (Point) getIntent().getParcelableExtra("point");
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        locationOverlay.enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        locationOverlay.disableMyLocation();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
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