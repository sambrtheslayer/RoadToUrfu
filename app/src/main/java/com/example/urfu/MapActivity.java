package com.example.urfu;

import android.annotation.SuppressLint;
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
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import android.content.SharedPreferences;

public class MapActivity extends AppCompatActivity implements LocationListener, PopupMenu.OnMenuItemClickListener  {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private MapView map = null;
    //private MyLocationNewOverlay locationOverlay;
    private LocationManager locationManager;

    //Братюня ниже респектабельный, он помогает делать геолокацию, а также строить маршруты. Также он наследуется от MyLocationNewOverlay,
    //что позволяет переписывать логику работы геолокации

    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;

    private final int MAX_ROUTES = 1;
    private Point selectedPoint;
    private OverlayItem selectedOverlayItem;
    private Polyline roadOverlayLine;
    private Location lastFixLocation;
    private ArrayList<Polyline> allOverlayLines = new ArrayList<>();

    //region Fields for routes
    private RoadManager mRoadManager;
    private GeoPoint mDestinationPoint;
    private ArrayList<GeoPoint> mCurrentRoute = new ArrayList<>();
    private Road mRoad;
    private double distanceFromCurrentPosToDestPoint;
    private final double deltaDistance = 0.00005;
    private boolean needToBuildRoute;
    //endregion

    //TODO: переименовать переменные
    private ImageButton btn_zoom_in;
    private ImageButton btn_zoom_out;
    private ImageButton user_location;
    private ImageButton btnBack;


    private Button buildRouteButton;
    //TODO: просто убрать кнопку и вызывать функцию потом
    private ImageButton clearRouteButton;


    Dialog myDialog;


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

    SharedPreferences settings;
    ImageButton settingsButton;
    Button campusButton;
    Button attractionsButton;


    private Language CURRENT_LANGUAGE = Language.Chinese; // 0 - Chinese

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
        loadSettings();

        checkUserLocationPermission();
        ctx = getApplicationContext();

        settings = getSharedPreferences("Settings", MODE_PRIVATE);

        selectedPoint = getIntent().getParcelableExtra("point");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //region Cache configuration
        Configuration.getInstance().setOsmdroidBasePath(new File(getCacheDir().getAbsolutePath(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath().getAbsolutePath(), "tile"));
        //endregion

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.map_activity);

        campusButton = findViewById(R.id.campus);
        attractionsButton = findViewById(R.id.attractions);
        settingsButton = findViewById(R.id.settingsButton);
        buildRouteButton = findViewById(R.id.buildRoute);

        CheckCurrentLanguage();

        //region Initializing
        initializeAdditionalInfo();
        //endregion

        // Инициализация объекта MapView.
        map = findViewById(R.id.map);

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
        //endregion


        //region Кастомные кнопки зума
        btn_zoom_in = findViewById(R.id.zoom_in);
        btn_zoom_out = findViewById(R.id.zoom_out);
        //endregion

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

        btnBack = findViewById(R.id.ButtonBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mCustomBottomSheet = findViewById(R.id.custom_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mCustomBottomSheet);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        imageGridView = findViewById(R.id.imageGridView);

        // Создаётся точка, основанная на предыдущем выборе в экране CategoryActivity.
        GeoPoint startPoint = new GeoPoint(selectedPoint.getLatitude(), selectedPoint.getLongitude());


        //region Marks
        getPointsFromHost();
        //endregion

        //region User Location

        //provider.addLocationSource(LocationManager.NETWORK_PROVIDER);

        this.mCompassOverlay = new CompassOverlay(ctx, new InternalCompassOrientationProvider(ctx),
                map);
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        this.mRoadManager = new GraphHopperRoadManager("0382a8c3-5f12-4c7a-918b-f42298e68f7b", false);
        this.mRoadManager.addRequestOption("vehicle=foot");

        if (selectedPoint != null)
            mDestinationPoint = new GeoPoint(selectedPoint.getLatitude(), selectedPoint.getLongitude());

        mLocationOverlay.enableMyLocation();
        //mLocationOverlay.enableFollowLocation();

        mLocationOverlay.setOptionsMenuEnabled(true);
        mCompassOverlay.enableCompass();

        user_location = findViewById(R.id.user_location);
        user_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocationOverlay.isFollowLocationEnabled()) {
                    mLocationOverlay.enableFollowLocation();
                } else {
                    mLocationOverlay.disableFollowLocation();
                }
            }
        });


        buildRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Вообще так-то можно несколько маршрутов забацать, пока поставил ограничение)
                if (allOverlayLines.size() < MAX_ROUTES) {

                    needToBuildRoute = true;
                    try {
                        if (lastFixLocation != null)
                            buildRouteFromCurrentLocToDestPoint(lastFixLocation);

                    } catch (Exception e) {
                        Log.e("Last fix location", e.getMessage());
                    }
                } else {
                    needToBuildRoute = true;
                    try {
                        if (lastFixLocation != null) {
                            for (Polyline route : allOverlayLines) {
                                map.getOverlays().remove(route);
                            }
                            buildRouteFromCurrentLocToDestPoint(lastFixLocation);
                            map.invalidate();
                        }
                    } catch (Exception e) {
                        Log.e("Last fix location", e.getMessage());
                    }
                }
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mLocationOverlay.enableMyLocation();
                mLocationOverlay.enableFollowLocation();
            }

        });


        clearRouteButton = findViewById(R.id.clearRoute);
        clearRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debugFunctionDELETE_AFTER_TESTS();
                if (roadOverlayLine != null) {
                    showRouteHasBeenClearedToast();

                    needToBuildRoute = false;

                    for (Polyline route : allOverlayLines) {
                        map.getOverlays().remove(route);
                    }
                    map.getOverlays().remove(roadOverlayLine);
                    roadOverlayLine = null;
                    map.invalidate();
                }
            }
        });


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mCompassOverlay = new CompassOverlay(ctx, new InternalCompassOrientationProvider(ctx),
                map);
        map.getOverlays().add(mCompassOverlay);
        map.getOverlays().add(this.mLocationOverlay);
        map.getOverlays().add(this.mCompassOverlay);

        mapController.setCenter(startPoint);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0f, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);

        map.getOverlayManager().add(mLocationOverlay);

        //endregion

        myDialog = new Dialog(this);
    }

    private void CheckCurrentLanguage()
    {
        String currentLanguage = settings.getString("Language", "N/A");
        if(currentLanguage.equals(Language.Chinese.getId()))
        {
            campusButton.setText(R.string.campus_ch);
            buildRouteButton.setText(R.string.setRoute_ch);
            attractionsButton.setText(R.string.attractions_ch);
            Log.e("text size ch", String.valueOf(campusButton.getTextSize()));
        }
        else if(currentLanguage.equals(Language.English.getId()))
        {
            campusButton.setText(R.string.campus_eng);
            campusButton.setTextSize(13);
            buildRouteButton.setText(R.string.setRoute_eng);
            Log.e("text size eng", String.valueOf(campusButton.getTextSize()));
            attractionsButton.setText(R.string.attractions_eng);
        }
    }

    public void showLanguageWindow(View v) {
        PopupMenu languageMenu = new PopupMenu(this, v);

        languageMenu.setOnMenuItemClickListener(this);
        languageMenu.inflate(R.menu.language_menu);
        languageMenu.show();
    }

    @SuppressLint({"ShowToast", "NonConstantResourceId"})
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chinese_lang:
                Toast.makeText(this, "选择中文", Toast.LENGTH_SHORT).show();
                changeLanguage("0");
                return true;

            case R.id.english_lang:
                Toast.makeText(this, "English language selected", Toast.LENGTH_SHORT).show();
                changeLanguage("1");
                return true;

            default:
                return false;
        }
    }

    private void changeLanguage(String idLanguage) {
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("Language", idLanguage);
        prefEditor.apply();

        CheckCurrentLanguage();
    }

    private void loadSettings() {
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
    }

    private void initializeAdditionalInfo() {
        additionalInfoListView = findViewById(R.id.additionalInfoListView);

        ArrayList<String> additionalInfo = new ArrayList<>();

        additionalInfo.add(selectedPoint.getAddress());
        additionalInfo.add(selectedPoint.getContacts());
        additionalInfo.add(selectedPoint.getSite());

        additionalInfoAdapter = new ArrayAdapter<>(ctx, R.layout.add_info_adapter_custom_layout, additionalInfo);

        additionalInfoListView.setAdapter(additionalInfoAdapter);
        Log.e("listview", additionalInfoListView.toString());
        Log.e("adapter", additionalInfoAdapter.toString());
    }

    private void setGoneLoadedImagesFromMemory() {
        for (int i = 0; i < 3; i++) {
            images.get(i).setVisibility(View.INVISIBLE);
        }
    }

    private void setNullObjectsInImages() {
        int size = images.size();
        images = new ArrayList<>(size);
    }


    @Override
    protected void onStart() {
        super.onStart();
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
        } catch (Exception ex) {
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0f, this);
        } catch (Exception ex) {
        }

        if (needToBuildRoute) {
            mLocationOverlay.enableFollowLocation();
            mLocationOverlay.enableMyLocation();
        }
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(this);
        } catch (Exception ex) {
        }

        mCompassOverlay.disableCompass();
        mLocationOverlay.disableFollowLocation();
        mLocationOverlay.disableMyLocation();
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

                showUserInDestPointToast();
            }
        } catch (Exception e) {
            Log.e("Check user exception", e.getMessage());
        }
    }

    private void showUserInDestPointToast()
    {
        String alt_text_chinese = "你在那個地方";

        Toast toast = Toast.makeText(ctx, "Вы прибыли на место\n你在那個地方", Toast.LENGTH_SHORT);

        switch(CURRENT_LANGUAGE)
        {
            case Chinese: toast = Toast.makeText(ctx, "Вы прибыли на место\n" + alt_text_chinese, Toast.LENGTH_SHORT); break;
        }

        toast.show();
    }

    private void showRouteHasBeenClearedToast()
    {
        String alt_text_chinese = "路線已清除";

        Toast toast = Toast.makeText(ctx, "Вы прибыли на место\n路線已清除", Toast.LENGTH_SHORT);

        switch(CURRENT_LANGUAGE)
        {
            case Chinese: toast = Toast.makeText(ctx, "Вы прибыли на место\n" + alt_text_chinese, Toast.LENGTH_SHORT); break;
        }

        toast.show();
    }

    private void debugFunctionDELETE_AFTER_TESTS()
    {
        Log.e("Settings", settings.getString("Language", "N/A"));
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void loadPhotoesFromHostById(int id) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final String baseHostApiUrl = "http://roadtourfu.ai-info.ru/api";

        // Конечный ресурс
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
                        MapActivity.this.runOnUiThread(() -> buildPhotoesByJson(jsonArray));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void buildPhotoesByJson(JSONArray jsonArray) {

        String[] urlList = new String[jsonArray.length()];

        Log.e("urlList", String.valueOf(urlList.length));

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
                Log.e("Full path", "http://roadtourfu.ai-info.ru/image/" + photoesUrl);
                urlList[i] = "http://roadtourfu.ai-info.ru/image/" + photoesUrl;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        imageGridView.setAdapter(new ImageAdapter(ctx, urlList));

        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Log.e("Clicked", i + ", " + l);
                    Log.e("View", imageGridView.getAdapter().getView(i, null, null).toString());
                    View image = imageGridView.getAdapter().getView(i, null, null);

                    ImageView fullScreenImage;

                    myDialog.setContentView(R.layout.popup_window);

                    fullScreenImage = (ImageView) myDialog.findViewById(R.id.full_screen_image);
                    Picasso.with(ctx)
                            .load(imageGridView.getAdapter().getItem(i).toString())
                            .noFade()
                            //.fit()
                            //.centerCrop()
                            .placeholder(R.drawable.progress_animation)
                            .into(fullScreenImage);

                    myDialog.show();
                }
                catch(Exception e){
                    Log.e("OnItemClick", e.getMessage());
                }
            }
        });


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

    @SuppressLint("UseCompatLoadingForDrawables")
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

                String classroom = object.getString("point_classroom");

                String description = object.getString("point_description");

                String alt_description = object.getString("point_alt_description");

                String address = object.getString("point_address");

                String contacts = object.getString("point_contacts");

                String site = object.getString("point_site");

                hashMapPoints.put(i, new Point(category_id, name, alt_name, latitude, longitude, classroom, description, alt_description, address, contacts, site));
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
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {

                        map.getController().animateTo(item.getPoint(), 19.5, ANIMATION_ZOOM_DELAY);

                        int currentId = selectedPoint.getId();
                        int tappingId = Integer.parseInt(item.getUid());

                        Log.e("Current ID", String.valueOf(currentId));
                        Log.e("Tapping ID", String.valueOf(tappingId));

                        // Если нажата отличная от первоначальной точки точка, то ставим новый значок на изначальную - чёрный кружок,
                        // на новую - чёрный пин, переназначаем selectedPoint и фокусируемся на новой точке
                        if (currentId != tappingId) {
                            setNullObjectsInImages();

                            item.setMarker(getDrawable(R.drawable.ic_place_black_36dp));
                            changeSelectedOverlayItem(currentId);

                            selectedOverlayItem.setMarker(getDrawable(R.drawable.ic_lens_black));

                            findAndSetupNewSelectedPoint(tappingId);

                            loadPhotoesFromHostById(selectedPoint.getId());

                            initializeAdditionalInfo();

                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return true;
                    }

                }, ctx);

        initializePointOnFirstStart();
        mOverlay.setFocus(selectedPoint.getOverlayItem());
        map.getOverlays().add(mOverlay);
    }

    private void changeSelectedOverlayItem(int id) {
        for (int i = 0; i < items.size(); i++) {
            if (Integer.parseInt(items.get(i).getUid()) == id) {
                selectedOverlayItem = items.get(i);
                assert loadedImages != null;
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setupMarkerForFirstOverlayItem(int id) {
        for (int i = 0; i < items.size(); i++) {
            if (Integer.parseInt(items.get(i).getUid()) == id) {
                selectedOverlayItem = items.get(i);
                selectedOverlayItem.setMarker(getDrawable(R.drawable.ic_place_black_36dp));

                assert loadedImages != null;

            }
        }
    }
}

