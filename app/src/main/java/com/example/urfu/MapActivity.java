package com.example.urfu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private MapView map = null;
    private MyLocationNewOverlay locationOverlay;
    private LocationManager locationManager;
    // private MyLocationListener listener;
    private Location currentLocation;
    private Point selectedPoint;

    private ImageButton btn_zoom_in;
    private ImageButton btn_zoom_out;
    private ImageButton user_location;

    HashMap<Integer, Point> hashMapPoints = new HashMap<>();
    ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
    Context ctx;


    private final long ANIMATION_ZOOM_DELAY = 500L;

    //MotionEvent. - содержит набор разновидностей событий. Проверка события через event.getAction()
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Это отлов события нажатия, в данном случае - "палец ввех"
        if(event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            assert map != null;

            // Класс Projection позволяет конвертировать координаты экрана в координаты мира
            Projection projection = map.getProjection();

            double latitudeFromTappedScreenX = projection.fromPixels(x, y).getLatitude();
            double longitudeFromTappedScreenY = projection.fromPixels(x, y).getLongitude();

            Log.e("World coords", latitudeFromTappedScreenX + ", " + longitudeFromTappedScreenY);

            //GeoPoint focusingPoint = new GeoPoint(latitudeFromTappedScreenX, longitudeFromTappedScreenY);

            //map.getController().setCenter(focusingPoint);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkUserLocationPermission();
        ctx = getApplicationContext();

        selectedPoint = getIntent().getParcelableExtra("point");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //region Cache configuration
        Configuration.getInstance().setOsmdroidBasePath(new File(getCacheDir().getAbsolutePath(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath().getAbsolutePath(), "tile"));
        //endregion

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.map_activity);

        // Инициализация объекта MapView.
        map = (MapView) findViewById(R.id.map);

        //region Map Settings
        // Отключение дублирования карты гориз и верт.
        map.setVerticalMapRepetitionEnabled(false);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setTileSource(TileSourceFactory.WIKIMEDIA);
        //map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        //endregion

        //region IMapController(zoom, center, etc)
        // Отвечает за масштабирование и начальную точку.
        IMapController mapController = map.getController();
        mapController.setZoom(19.5);


        // Кастомные кнопки зума
        btn_zoom_in = findViewById(R.id.zoom_in);
        btn_zoom_out = findViewById(R.id.zoom_out);


        btn_zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.getController().zoomIn(ANIMATION_ZOOM_DELAY);
            }
        });

        btn_zoom_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.getController().zoomOut(ANIMATION_ZOOM_DELAY);
            }
        });

        //region Map On Click

        //map.onTouchEvent(MotionEvent.ACTION_BUTTON_PRESS)

        //endregion


        //GeoPoint startPoint = new GeoPoint(56.800091d, 59.909221d);

        // Создаётся точка, основанная на предыдущем выборе в экране CategoryActivity.
        GeoPoint startPoint = new GeoPoint(selectedPoint.getLatitude(), selectedPoint.getLongitude());

        mapController.setCenter(startPoint);
        //endregion

        //region Marks
        getPointsFromHost();
        //endregion

        //region User Location

        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        locationOverlay = new MyLocationNewOverlay(provider, map);
        user_location = findViewById(R.id.user_location);

        user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationOverlay.enableFollowLocation();
            }
        });

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
        } catch (Exception e) {
            Log.e("ex", e.getMessage());
        }

        //endregion

        //Point point = (Point) getIntent().getParcelableExtra("point");


        /*
        Point point = getIntent().getParcelableExtra("point");

        Log.e("name", point.getName());
        Log.e("id точки в базе", String.valueOf(point.getId()));
        Log.e("Altname", point.getAltName()); */

        // Загрузка изображения у точки
        // new DownloadImageTask().execute(point);

        /*ImageView image = findViewById(R.id.imageView);
        image.setImageBitmap(point.getDescriptionImage());*/

        //Log.e("image", point.getDescriptionImage().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();

        new DownloadImageTask().execute(selectedPoint);
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

    private class DownloadImageTask extends AsyncTask<Point, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Point... points) {

            String pathToImage = "https://roadtourfu.000webhostapp.com/image/";

            pathToImage = pathToImage + "point_" + points[0].getId() + ".PNG";

            Bitmap loadedImage = null;

            try {

                InputStream in = new java.net.URL(pathToImage).openStream();

                loadedImage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {

                Log.e("Error", e.getMessage());

                e.printStackTrace();

            }

            return loadedImage;
        }

        protected void onPostExecute(Bitmap result) {

            ImageView image = findViewById(R.id.mainImg);

            image.setImageBitmap(result);

        }
    }

    private void getPointsFromHost()
    {
        OkHttpClient client = new OkHttpClient();

        final String baseHostApiUrl = "https://roadtourfu.000webhostapp.com/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        String url = baseHostApiUrl + "/data/get_all_points.php";

        FormBody formBody = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            // Обработка полученного ответа от сервера.
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                if(response.isSuccessful())
                {
                    assert response.body() != null;

                    final String myResponse = response.body().string();

                    try
                    {
                        // Объявляется экземпляр класса JSONObject, где аргумент -
                        // это полученная строка от сервера.
                        Log.e("Response", myResponse);

                        JSONArray jsonArray = new JSONArray(myResponse);

                        // Обязательно запускать через этот поток, иначе будет ошибка изменения элементов вне потока
                        // Формируется Categories из Json
                        MapActivity.this.runOnUiThread(() -> buildPointsByJson(jsonArray));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
    private void buildPointsByJson(JSONArray jsonArray)
    {
        /*
            point_id
            point_name
            point_alt_name
            point_latitude
            point_longitude
            point_image
            point_description
            point_alt_description
        */
        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject object = null;
            try
            {
                object = jsonArray.getJSONObject(i);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            try
            {
                // Помещение точек в список.
                assert object != null;

                int category_id = object.getInt("point_id");//getIdFromString(object);

                String name = object.getString("point_name");

                String alt_name = object.getString("point_alt_name");

                double latitude = object.getDouble("point_latitude");

                double longitude = object.getDouble("point_longitude");

                String image = object.getString("point_image");

                String description = object.getString("point_description");

                String alt_description = object.getString("point_alt_description");

                hashMapPoints.put(i, new Point(category_id, name, alt_name, latitude, longitude, /*image,*/ description, alt_description));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        String[] local_points = new String[hashMapPoints.size()];

        for(int i = 0; i < hashMapPoints.size(); i++)
        {
            String name = Objects.requireNonNull(hashMapPoints.get(i)).getName();

            String alt_name = Objects.requireNonNull(hashMapPoints.get(i)).getAltName();

            String full_name = alt_name + " " +  "\n" + " " + name;

            local_points[i] = full_name;
        }

        Log.e("Adding points", "Here");

        items.add(new OverlayItem("Title", "Description", new GeoPoint(56.800091d, 59.909221d))); // Lat/Lon decimal degrees
        items.get(0).setMarker(getDrawable(R.drawable.wine_bottle));
        //the overlay
        items.add(new OverlayItem("Title", "Description", new GeoPoint(56.79511011, 59.9230577)));
        items.get(1).setMarker(getDrawable(R.drawable.place_holder));


        //TODO: Здесь нужно убрать дубль, потому что selectedPoint есть также и в ответе от сервера
        Log.e("Getting overlay", selectedPoint.getOverlayItem().toString());
        items.add(selectedPoint.getOverlayItem());
        items.get(2).setMarker(getDrawable(R.drawable.ic_place_black_36dp));

        assert hashMapPoints != null;
        for(int i = 0; i < hashMapPoints.size(); i++)
        {
            items.add(hashMapPoints.get(i).getOverlayItem());
            items.get(3 + i).setMarker(getDrawable(R.drawable.ic_place_black_36dp));
        }
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Log.e("HUI", "tapped");
                        //map.getController().setCenter(item.getPoint());
                        map.getController().animateTo(item.getPoint(), 19.5, ANIMATION_ZOOM_DELAY);

                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Log.e("HUI2", "long tapped");
                        return true;
                    }

                }, ctx);



        mOverlay.setFocus(selectedPoint.getOverlayItem());
        //mOverlay.setFocus(items.get(0));
        map.getOverlays().add(mOverlay);
    }


}

