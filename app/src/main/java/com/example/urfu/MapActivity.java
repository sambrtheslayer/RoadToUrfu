package com.example.urfu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private MapView map = null;
    //private MyLocationNewOverlay locationOverlay;
    private LocationManager locationManager;

    //Братюня ниже респектабельный, он помогает делать геолокацию, а также строить маршруты. Также он наследуется от MyLocationNewOverlay,
    //что позволяет переписывать логику работы геолокации
    private MyLocationListener locationOverlay;
    private CompassOverlay mCompassOverlay;

    private final int MAX_ROUTES = 1;
    private Point selectedPoint;
    private OverlayItem selectedOverlayItem;
    private Polyline roadOverlayLine;
    private Location lastFixLocation;
    private ArrayList<Polyline> allOverlayLines = new ArrayList<>();

    //TODO: переименовать переменные
    private ImageButton btn_zoom_in;
    private ImageButton btn_zoom_out;
    private ImageButton user_location;

    //TODO: убрать кнопку и запихнуть её в шторку
    private ImageButton buildRouteButton;
    //TODO: просто убрать кнопку и вызывать функцию потом
    private ImageButton clearRouteButton;


    Dialog myDialog;
    // private ImageView fullScreenImage;

    HashMap<Integer, Point> hashMapPoints = new HashMap<>();
    ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
    private ArrayList<Bitmap> loadedImages = new ArrayList<>();
    private ListView additionalInfoListView;
    private ArrayAdapter<String> additionalInfoAdapter;
    Context ctx;

    private BottomSheetBehavior mBottomSheetBehavior;
    private ConstraintLayout mCustomBottomSheet;

    private GridView imageGridView;
    private ArrayList<ImageView> images = new ArrayList<>();
    private ArrayList<ProgressBar> progressBars = new ArrayList<>();

    private final long ANIMATION_ZOOM_DELAY = 500L;

    //MotionEvent. - содержит набор разновидностей событий. Проверка события через event.getAction()
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Это отлов события нажатия, в данном случае - "палец ввех"
        if (event.getAction() == MotionEvent.ACTION_UP) {
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
        Log.e("Check fields", selectedPoint.getAddress());
        Log.e("Check fields", selectedPoint.getContacts());
        Log.e("Check fields", selectedPoint.getSite());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //region Cache configuration
        Configuration.getInstance().setOsmdroidBasePath(new File(getCacheDir().getAbsolutePath(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath().getAbsolutePath(), "tile"));
        //endregion

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.map_activity);

        initializeImageViewForPhotoes();
        initializeProgressBars();
        initializeAdditionalInfo();

        setOnClickListenerForImages();

        //TODO: здесь указать gridView;
        //imageGridView = findViewById();

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

        mCustomBottomSheet = findViewById(R.id.custom_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mCustomBottomSheet);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

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
        //locationOverlay = new MyLocationNewOverlay(provider, map);
        locationOverlay = new MyLocationListener(provider, map);
        user_location = findViewById(R.id.user_location);

        user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //locationOverlay.disableFollowLocation();
                locationOverlay.enableFollowLocation();
            }
        });

        buildRouteButton = findViewById(R.id.buildRoute);
        buildRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Вообще так-то можно несколько маршрутов забацать, пока поставил ограничение)
                if (allOverlayLines.size() < MAX_ROUTES) {

                    locationOverlay.needToBuildRoute = true;
                    try {
                        if (lastFixLocation != null)
                            locationOverlay.buildRouteFromCurrentLocToDestPoint(lastFixLocation);
                    } catch (Exception e) {
                        Log.e("Last fix location", e.getMessage());
                    }
                } else {
                    locationOverlay.needToBuildRoute = true;
                    try {
                        if (lastFixLocation != null) {
                            for (Polyline route : allOverlayLines) {
                                map.getOverlays().remove(route);
                            }
                            locationOverlay.buildRouteFromCurrentLocToDestPoint(lastFixLocation);
                            map.invalidate();
                        }
                    } catch (Exception e) {
                        Log.e("Last fix location", e.getMessage());
                    }
                }
            }

        });

        clearRouteButton = findViewById(R.id.clearRoute);
        clearRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roadOverlayLine != null) {
                    locationOverlay.needToBuildRoute = false;

                    for (Polyline route : allOverlayLines) {
                        map.getOverlays().remove(route);
                    }
                    //map.getOverlays().remove(roadOverlayLine);
                    //roadOverlayLine = null;
                    map.invalidate();
                }
            }
        });

        // Отвечает за первый старт локации (полезно ли?).
        locationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                Log.e("MyTag", String.format("First location fix: %s", locationOverlay.getLastFix()));
                Log.e("MyTag", String.format("First location fix: %s", locationOverlay.getLastFix()));
                Log.e("MyTag", String.format("First location fix: %s", locationOverlay.getLastFix()));
                Log.e("MyTag", String.format("First location fix: %s", locationOverlay.getLastFix()));
                //lastFixLocation = locationOverlay.getLastFix();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mCompassOverlay = new CompassOverlay(ctx, new InternalCompassOrientationProvider(ctx),
                map);
        map.getOverlays().add(mCompassOverlay);
        map.getOverlays().add(locationOverlay);
        map.getOverlays().add(this.mCompassOverlay);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0l,0f,this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);

        map.getOverlayManager().add(locationOverlay);

        //endregion

        myDialog = new Dialog(this);
    }

    private void initializeAdditionalInfo() {
        additionalInfoListView = findViewById(R.id.additionalInfoListView);

        ArrayList<String> additionalInfo = new ArrayList<>();

        additionalInfo.add(selectedPoint.getAddress());
        additionalInfo.add(selectedPoint.getContacts());
        additionalInfo.add(selectedPoint.getSite());

        additionalInfoAdapter = new ArrayAdapter<>(ctx, R.layout.array_adapter_custom_layout, additionalInfo);

        additionalInfoListView.setAdapter(additionalInfoAdapter);
        Log.e("listview", additionalInfoListView.toString());
        Log.e("adapter", additionalInfoAdapter.toString());
    }

    private void initializeProgressBars() {
        progressBars.add(findViewById(R.id.progressImg1));
        progressBars.add(findViewById(R.id.progressImg2));
        progressBars.add(findViewById(R.id.progressImg3));
    }

    private void initializeImageViewForPhotoes() {
        images.add(findViewById(R.id.mainImg));
        images.add(findViewById(R.id.mainImg2));
        images.add(findViewById(R.id.mainImg3));

        setGoneLoadedImagesFromMemory();
    }

    private void setGoneLoadedImagesFromMemory() {
        for (int i = 0; i < 3; i++) {
            images.get(i).setVisibility(View.INVISIBLE);
        }
    }

    private void setNullObjectsInImages() {
        int size = images.size();
        images = new ArrayList<>(size);

        initializeImageViewForPhotoes();
    }

    private void setOnClickListenerForImages() {
        images.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopup(0);
            }
        });
        images.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopup(1);
            }
        });
        images.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopup(2);
            }
        });
    }


    public void ShowPopup(int id) {
        if (images.get(id) != null) {
            ImageView fullScreenImage;

            myDialog.setContentView(R.layout.popup_window);

            fullScreenImage = (ImageView) myDialog.findViewById(R.id.full_screen_image);
            fullScreenImage.setImageDrawable(images.get(id).getDrawable());


            myDialog.show();
        }
    }


    /*
    public void onButtonShowPopupWindowClick(View view, int id) {

        fullScreenImage = findViewById(R.id.full_screen_image);
        Log.e("full", String.valueOf(fullScreenImage));
        fullScreenImage.setImageDrawable(images.get(id).getDrawable());

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);


        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }


    /*
    private void StartFullScreenActivity(int imgid) {
        Intent intent = new Intent(getApplicationContext(), FullScreenPhotoActivity.class);
        Log.e("id", String.valueOf(imgid));
        Log.e("true id", String.valueOf(R.id.mainImg));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgid);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("image",  byteArray);
        startActivity(intent);
    }
     */


    @Override
    protected void onStart() {
        super.onStart();
        //new DownloadImageTask().execute(selectedPoint);
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            //this fails on AVD 19s, even with the appcompat check, says no provided named gps is available
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);
        }catch (Exception ex){}

        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0l,0f,this);
        }catch (Exception ex){}

        locationOverlay.enableFollowLocation();
        locationOverlay.enableMyLocation();
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
        mCompassOverlay.disableCompass();
        locationOverlay.disableFollowLocation();
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

            } else {
                Log.e("Permission: ", "Granted");
            }

        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.e("LocationListener", location.toString());

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private String progress = "";

        @Override
        protected Bitmap doInBackground(String... urls) {
            /*
            photoes
        */
            progress = urls[1];
            Bitmap loadedImage = null;
            String pathToImage = "http://roadtourfu.ai-info.ru/image/";

            JSONObject object = null;
            try {
                loadedImage = null;
                // Помещение точек в список.

                Log.e("In doinbg", pathToImage);

                pathToImage = pathToImage + urls[0];

                InputStream in = new java.net.URL(pathToImage).openStream();

                loadedImage = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return loadedImage;
        }

        @Override
        protected void onPostExecute(Bitmap image) {

            Log.e("Counter", progress);
            images.get(Integer.valueOf(progress)).setImageBitmap(image);
            images.get(Integer.valueOf(progress)).setVisibility(View.VISIBLE);
            //Log.e("Итого", loadedImages.toString());
            //image.setImageBitmap(result);

        }
        /*@Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            Log.e("Counter", String.valueOf(progress))
            images.get(progress).setImageBitmap(values[0]);
            progress++;
            //mInfoTextView.setText("Этаж: " + values[0]);
        }

         */
    }

    private void loadPhotoesFromHostById(int id) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final String baseHostApiUrl = "http://roadtourfu.ai-info.ru/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        String url = baseHostApiUrl + "/data/get_photoes.php";

        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(id))
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
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    final String myResponse = response.body().string();

                    try {
                        // Объявляется экземпляр класса JSONObject, где аргумент -
                        // это полученная строка от сервера.
                        Log.e("Response", myResponse);

                        JSONArray jsonArray = new JSONArray(myResponse);

                        // Обязательно запускать через этот поток, иначе будет ошибка изменения элементов вне потока
                        // Формируется Photo из Json
                        //new DownloadImageTask().execute(jsonArray);
                        //initializeProgressBars();
                        MapActivity.this.runOnUiThread(() -> buildPhotoesByJson(jsonArray));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void buildPhotoesByJson(JSONArray jsonArray) {
        /*
            photoes
        */
        //new DownloadImageTask().execute(jsonArray);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = null;
            try {
                object = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                // Помещение точек в список.
                assert object != null;

                String photoesUrl = object.getString("photoes");
                //new DownloadImageTask().execute(selectedPoint);
                //new DownloadImageTask().execute(photoesUrl, String.valueOf(i));
                //progressBars.get(i).setVisibility(View.VISIBLE);
                Log.e("Full path", "http://roadtourfu.ai-info.ru/image/" + photoesUrl);
                PicassoTrustAll.getInstance(getApplicationContext()).load("http://roadtourfu.ai-info.ru/image/" + photoesUrl).into(images.get(i));

                //progressBars.get(i).setVisibility(View.INVISIBLE);
                images.get(i).setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void getPointsFromHost() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final String baseHostApiUrl = "http://roadtourfu.ai-info.ru/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        String url = baseHostApiUrl + "/data/get_points.php";

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
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    final String myResponse = response.body().string();

                    try {
                        // Объявляется экземпляр класса JSONObject, где аргумент -
                        // это полученная строка от сервера.
                        Log.e("Response", myResponse);

                        JSONArray jsonArray = new JSONArray(myResponse);

                        // Обязательно запускать через этот поток, иначе будет ошибка изменения элементов вне потока
                        // Формируется Categories из Json
                        MapActivity.this.runOnUiThread(() -> buildPointsByJson(jsonArray));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void buildPointsByJson(JSONArray jsonArray) {
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
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = null;
            try {
                object = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
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

                String address = object.getString("point_address");

                String contacts = object.getString("point_contacts");

                String site = object.getString("point_site");

                hashMapPoints.put(i, new Point(category_id, name, alt_name, latitude, longitude, /*image,*/ description, alt_description, address, contacts, site));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String[] local_points = new String[hashMapPoints.size()];

        for (int i = 0; i < hashMapPoints.size(); i++) {
            String name = Objects.requireNonNull(hashMapPoints.get(i)).getName();

            String alt_name = Objects.requireNonNull(hashMapPoints.get(i)).getAltName();

            String full_name = alt_name + " " + "\n" + " " + name;

            local_points[i] = full_name;
        }

        Log.e("Adding points", "Here");
/*
        items.add(new OverlayItem("Title", "Description", new GeoPoint(56.800091d, 59.909221d))); // Lat/Lon decimal degrees
        items.get(0).setMarker(getDrawable(R.drawable.wine_bottle));
        //the overlay
        items.add(new OverlayItem("Title", "Description", new GeoPoint(56.79511011, 59.9230577)));
        items.get(1).setMarker(getDrawable(R.drawable.place_holder));
*/

        //TODO: Здесь нужно убрать дубль, потому что selectedPoint есть также и в ответе от сервера
        /*Log.e("Getting overlay", selectedPoint.getOverlayItem().toString());
        items.add(selectedPoint.getOverlayItem(selectedPoint.getId()));
        items.get(2).setMarker(getDrawable(R.drawable.ic_place_black_36dp));*/

        assert hashMapPoints != null;
        for (int i = 0; i < hashMapPoints.size(); i++) {
            items.add(hashMapPoints.get(i).getOverlayItem(hashMapPoints.get(i).getId()));
            Log.e("Hashmap Points", items.get(i).getUid());
            items.get(i).setMarker(getDrawable(R.drawable.ic_lens_black));
        }

        map.invalidate();

        initializeTapSettings();
    }

    private void initializeTapSettings() {
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Log.e("HUI", "tapped");
                        //map.getController().setCenter(item.getPoint());
                        map.getController().animateTo(item.getPoint(), 19.5, ANIMATION_ZOOM_DELAY);

                        //region TODO: debug zone
                        int currentId = selectedPoint.getId();
                        int tappingId = Integer.valueOf(item.getUid());

                        Log.e("Current ID", String.valueOf(currentId));
                        Log.e("Tapping ID", String.valueOf(tappingId));

                        // Если нажата отличная от первоначальной точки точка, то ставим новый значок на изначальную - чёрный кружок,
                        // на новую - чёрный пин, переназначаем selectedPoint и фокусируемся на новой точке
                        if (currentId != tappingId) {
                            setNullObjectsInImages();
                            //setGoneLoadedImagesFromMemory();

                            item.setMarker(getDrawable(R.drawable.ic_place_black_36dp));
                            changeSelectedOverlayItem(currentId);

                            selectedOverlayItem.setMarker(getDrawable(R.drawable.ic_lens_black));

                            findAndSetupNewSelectedPoint(tappingId);

                            initializeImageViewForPhotoes();

                            loadPhotoesFromHostById(selectedPoint.getId());

                            initializeAdditionalInfo();

                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Log.e("HUI2", "long tapped");
                        return true;
                    }

                }, ctx);

        initializePointOnFirstStart();
        mOverlay.setFocus(selectedPoint.getOverlayItem());
        //mOverlay.setFocus(items.get(0));
        map.getOverlays().add(mOverlay);
    }

    private void changeSelectedOverlayItem(int id) {
        for (int i = 0; i < items.size(); i++) {
            if (Integer.valueOf(items.get(i).getUid()) == id) {
                selectedOverlayItem = items.get(i);
                assert loadedImages != null;
                //TODO: тут добавить подгрузку Bitmap'ов из списка
                //Log.e("Photoes", String.valueOf(loadedImages.size()));
                //ImageView image = findViewById(R.id.mainImg);
                //image.setImageBitmap(result);
            }
        }
    }

    private void findAndSetupNewSelectedPoint(int id) {
        for (int i = 0; i < hashMapPoints.size(); i++) {
            if (hashMapPoints.get(i).getId() == id)
                selectedPoint = hashMapPoints.get(i);
        }
    }

    private void initializePointOnFirstStart() {
        loadPhotoesFromHostById(selectedPoint.getId());
        setupMarkerForFirstOverlayItem(selectedPoint.getId());

        Log.e("Selected OverlayItem", selectedOverlayItem.getUid());
        Log.e("Selected Point", String.valueOf(selectedPoint.getId()));
    }

    private void setupMarkerForFirstOverlayItem(int id) {
        for (int i = 0; i < items.size(); i++) {
            if (Integer.valueOf(items.get(i).getUid()) == id) {
                selectedOverlayItem = items.get(i);
                selectedOverlayItem.setMarker(getDrawable(R.drawable.ic_place_black_36dp));

                assert loadedImages != null;
                //TODO: тут добавить подгрузку Bitmap'ов из списка
                // Log.e("Photoes", String.valueOf(loadedImages.size()));
                /*
                ImageView image = findViewById(R.id.mainImg);
                ImageView image2 = findViewById(R.id.mainImg2);
                ImageView image3 = findViewById(R.id.mainImg3);

                image.setImageBitmap(loadedImages.get(0));
                image2.setImageBitmap(loadedImages.get(1));
                image3.setImageBitmap(loadedImages.get(2));
                 */

            }
        }
    }

    public class MyLocationListener extends MyLocationNewOverlay {
        private RoadManager mRoadManager;
        private GeoPoint mDestinationPoint;
        private ArrayList<GeoPoint> mCurrentRoute = new ArrayList<>();
        private Road mRoad;
        private double distanceFromCurrentPosToDestPoint;
        private final double deltaDistance = 0.000001;

        public boolean needToBuildRoute;

        public MyLocationListener(IMyLocationProvider myLocationProvider, MapView mapView) {
            super(myLocationProvider, mapView);

            mRoadManager = new GraphHopperRoadManager("0382a8c3-5f12-4c7a-918b-f42298e68f7b", false);
            mRoadManager.addRequestOption("vehicle=foot");

            if (selectedPoint != null)
                mDestinationPoint = new GeoPoint(selectedPoint.getLatitude(), selectedPoint.getLongitude());
        }

        // Отвечает за изменение локации.

        @Override
        public void onLocationChanged(Location location, IMyLocationProvider source) {

            // Log.e("Location: ", location.getLatitude() + ", " + location.getLongitude());

            if(!isMyLocationEnabled()) {
                setOptionsMenuEnabled(true);
                mCompassOverlay.enableCompass();
                enableFollowLocation();
            }

            if (lastFixLocation == null)
                lastFixLocation = location;

            try {
                if (roadOverlayLine != null) {
                    checkUserIsInDestPoint(location);
                }

                if (this.needToBuildRoute) {
                    buildRouteFromCurrentLocToDestPoint(location);
                }

                lastFixLocation = location;
            } catch (Exception e) {
                Log.e("onLocationChanged exc", e.getMessage());
            }
        }

        private void checkUserIsInDestPoint(Location location) {
            try {
                distanceFromCurrentPosToDestPoint = calculateDistance(selectedPoint, location);

                Log.e("Distance", String.valueOf(distanceFromCurrentPosToDestPoint));

                if (distanceFromCurrentPosToDestPoint < deltaDistance) {

                    this.needToBuildRoute = false;

                    if (mCurrentRoute != null) {
                        mCurrentRoute.clear();
                        map.getOverlays().remove(roadOverlayLine);
                        map.invalidate();
                    }

                }
            } catch (Exception e) {
                Log.e("Check user exception", e.getMessage());
            }
        }

        public void buildRouteFromCurrentLocToDestPoint(Location location) {
            try {
                if (mCurrentRoute != null)
                    mCurrentRoute.clear();

                if (selectedPoint != null)
                    this.mDestinationPoint = new GeoPoint(selectedPoint.getLatitude(), selectedPoint.getLongitude());

                mCurrentRoute.add(new GeoPoint(location.getLatitude(), location.getLongitude()));
                mCurrentRoute.add(mDestinationPoint);

                mRoad = mRoadManager.getRoad(mCurrentRoute);

                roadOverlayLine = GraphHopperRoadManager.buildRoadOverlay(mRoad);
                roadOverlayLine.getOutlinePaint().setColor(Color.argb(255, 252, 149, 150));
                roadOverlayLine.getOutlinePaint().setStrokeWidth(10f);

                for (Polyline route : allOverlayLines) {
                    map.getOverlays().remove(route);
                }
                allOverlayLines.clear();

                map.getOverlays().add(roadOverlayLine);
                allOverlayLines.add(roadOverlayLine);

                map.invalidate();

                Log.e("Route", "Route has been rebuilt");
            } catch (Exception e) {
                Log.e("Build route exception", e.getMessage());
            }
        }

        private double calculateDistance(Point destinationPoint, Location currentLocation) {
            try {
                double x1 = destinationPoint.getLatitude();
                double y1 = destinationPoint.getLongitude();

                double x2 = currentLocation.getLatitude();
                double y2 = currentLocation.getLongitude();

                return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
            } catch (Exception e) {
                Log.e("Calc route exception", e.getMessage());
            }
            return 0;
        }
    }
}

