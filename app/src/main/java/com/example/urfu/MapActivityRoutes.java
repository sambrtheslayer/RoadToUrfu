package com.example.urfu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivityRoutes extends AppCompatActivity implements LocationListener, PopupMenu.OnMenuItemClickListener{

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private MapView map = null;
    private SharedPreferences settings;
    private Context ctx;
    private Route selectedRoute;

    private ImageButton settingsButton;
    private Button campusButton;
    private Button attractionsButton;
    private GridView imageGridView;
    private Button gotoMapActivityButton;
    private MyLocationNewOverlay mLocationOverlay;
    private OverlayItem selectedOverlayItem;
    private CompassOverlay mCompassOverlay;
    private RoadManager mRoadManager;
    private Polyline roadOverlayLine;
    private LocationManager locationManager;
    private ArrayList<GeoPoint> mCurrentRoute = new ArrayList<>();

    private ArrayList<Bitmap> loadedImages = new ArrayList<>();
    private ListView additionalInfoListView;
    private ArrayAdapter<String> additionalInfoAdapter;

    private ImageButton btn_zoom_in;
    private ImageButton btn_zoom_out;
    private ImageButton user_location;
    private ImageButton btnBack;
    private ImageButton clearRoute;
    private Road mRoad;


    private final long ANIMATION_ZOOM_DELAY = 500L;
    private BottomSheetBehavior mBottomSheetBehavior;
    private ConstraintLayout mCustomBottomSheet;
    ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
    private ArrayList<ImageView> images = new ArrayList<>();
    HashMap<Integer, Point> hashMapPoints = new HashMap<>();
    private Point selectedPoint;

    private Dialog myDialog;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSettings();


        checkUserLocationPermission();
        ctx = getApplicationContext();
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
        selectedRoute = getIntent().getParcelableExtra("route");


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
        clearRoute = findViewById(R.id.clearRoute);
        gotoMapActivityButton = findViewById(R.id.buildRoute);

        clearRoute.setVisibility(View.GONE);

        CheckCurrentLanguage();

        attractionsButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.active_btn)));
        attractionsButton.setTextColor(Color.parseColor("#FFFFFF"));
        campusButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.nonactive_btn)));
        campusButton.setTextColor(Color.parseColor("#696767"));
        //region Initializing
        //initializeAdditionalInfo();
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

        gotoMapActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivityRoutes.this, MapActivity.class);

                intent.putExtra("point", selectedPoint);

                startActivity(intent);
            }
        });

        campusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivityRoutes.this, MainActivity.class);
                startActivity(intent);
            }
        });
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

        //GeoPoint startPoint = new GeoPoint(selectedPoint.getLatitude(), selectedPoint.getLongitude());

        loadPointsForSelectedRoute();

        //initializeMap();

        //region Marks
        //getPointsFromHost();
        //endregion

        //region User Location

        //provider.addLocationSource(LocationManager.NETWORK_PROVIDER);

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
                        MapActivityRoutes.this.runOnUiThread(() -> buildPhotoesByJson(jsonArray));
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

        imageGridView.setAdapter(new ImageAdapter(ctx, urlList, 3));

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

    private void CheckCurrentLanguage()
    {
        String currentLanguage = settings.getString("Language", "N/A");
        if(currentLanguage.equals(Language.Chinese.getId()))
        {
            campusButton.setText(R.string.campus_ch);
            attractionsButton.setText(R.string.attractions_ch);
            Log.e("text size ch", String.valueOf(campusButton.getTextSize()));
        }
        else if(currentLanguage.equals(Language.English.getId()))
        {
            campusButton.setText(R.string.campus_eng);
            campusButton.setTextSize(13);
            Log.e("text size eng", String.valueOf(campusButton.getTextSize()));
            attractionsButton.setText(R.string.attractions_eng);
        }
    }

    private void initializeAdditionalInfo() {
        additionalInfoListView = findViewById(R.id.additionalInfoListView);

        ArrayList<String> additionalInfo = new ArrayList<>();

        additionalInfo.add(selectedPoint.getName());
        additionalInfo.add(selectedPoint.getAltName());
        additionalInfo.add(selectedPoint.getAltDescription());
        additionalInfo.add(selectedPoint.getDescription());
        additionalInfo.add(selectedPoint.getAddress());


        additionalInfoAdapter = new ArrayAdapter<>(ctx, R.layout.add_info_adapter_custom_layout, additionalInfo);

        additionalInfoListView.setAdapter(additionalInfoAdapter);
        Log.e("listview", additionalInfoListView.toString());
        Log.e("adapter", additionalInfoAdapter.toString());
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

    private void loadSettings() {
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    public void loadPointsForSelectedRoute() {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final String baseHostApiUrl = "http://roadtourfu.ai-info.ru/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        String url = baseHostApiUrl + "/data/get_points.php";
        String readLanguageSetting = settings.getString("Language", "N/A");

        Log.e("Route ID", String.valueOf(selectedRoute.getCategoryId()));

        FormBody formBody = new FormBody.Builder()
                .add("category", String.valueOf(selectedRoute.getCategoryId()))
                .add("lang", readLanguageSetting)
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
                        MapActivityRoutes.this.runOnUiThread(() -> buildPointsByJson(jsonArray));
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
        Log.e("JSONArray", jsonArray.toString());


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

                Log.e("Merging all responses", name + alt_name + latitude + longitude + address);

                Log.e("jsonArray Length", String.valueOf(jsonArray.length()));

                hashMapPoints.put(i, new Point(category_id, name, alt_name, latitude, longitude, classroom, description, alt_description, address, contacts, site));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e("Perfect put", "yes");

        String[] local_points = new String[hashMapPoints.size()];

        for (int i = 0; i < hashMapPoints.size(); i++) {
            String name = Objects.requireNonNull(hashMapPoints.get(i)).getName();

            String alt_name = Objects.requireNonNull(hashMapPoints.get(i)).getAltName();

            String full_name = alt_name + " " + "\n" + " " + name;

            local_points[i] = full_name;
        }

        //Log.e("Selected point before", selectedPoint.toString());

        selectedPoint = hashMapPoints.get(0);
        initializeAdditionalInfo();

        Log.e("Selected point after", selectedPoint.toString());

        assert hashMapPoints != null;
        for (int i = 0; i < hashMapPoints.size(); i++) {
            items.add(hashMapPoints.get(i).getOverlayItem(hashMapPoints.get(i).getId()));
            Log.e("Hashmap Points", items.get(i).getUid());
            items.get(i).setMarker(getDrawable(R.drawable.ic_lens_black));
            mCurrentRoute.add(new GeoPoint(hashMapPoints.get(i).getLatitude(), hashMapPoints.get(i).getLongitude()));
        }

        initializeCurrentRoute();

        map.invalidate();

        initializeMap();

        initializeTapSettings();

        //setupAdapterAndListview(local_points);
    }

    private void initializeCurrentRoute() {

        this.mRoadManager = new GraphHopperRoadManager("0382a8c3-5f12-4c7a-918b-f42298e68f7b", false);
        this.mRoadManager.addRequestOption("vehicle=foot");

        mRoad = mRoadManager.getRoad(mCurrentRoute);

        roadOverlayLine = GraphHopperRoadManager.buildRoadOverlay(mRoad);
        roadOverlayLine.getOutlinePaint().setColor(Color.argb(255, 252, 149, 150));
        roadOverlayLine.getOutlinePaint().setStrokeWidth(10f);
        map.getOverlays().add(roadOverlayLine);
    }

    private void initializeMap() {

        this.mCompassOverlay = new CompassOverlay(ctx, new InternalCompassOrientationProvider(ctx),
                map);
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);



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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mCompassOverlay = new CompassOverlay(ctx, new InternalCompassOrientationProvider(ctx),
                map);
        map.getOverlays().add(mCompassOverlay);
        map.getOverlays().add(this.mLocationOverlay);
        map.getOverlays().add(this.mCompassOverlay);

        //region IMapController(zoom, center, etc)
        // Отвечает за масштабирование и начальную точку.
        IMapController mapController = map.getController();
        mapController.setZoom(19.5);
        mapController.setCenter(new GeoPoint(selectedPoint.getLatitude(), selectedPoint.getLongitude()));
        //endregion

        //mapController.setCenter(startPoint);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0f, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);

        map.getOverlayManager().add(mLocationOverlay);

        //endregion

        myDialog = new Dialog(this);
    }

    private void setNullObjectsInImages() {
        int size = images.size();
        images = new ArrayList<>(size);
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
                changeLanguage(Language.Chinese.getId());
                return true;

            case R.id.english_lang:
                Toast.makeText(this, "English language selected", Toast.LENGTH_SHORT).show();
                changeLanguage(Language.English.getId());
                return true;

            default:
                return false;
        }
    }

    private void changeLanguage(String idLanguage) {
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("Language", idLanguage);
        prefEditor.apply();

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

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
