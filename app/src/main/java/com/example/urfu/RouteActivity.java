package com.example.urfu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class RouteActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ListView availableRoutesListView;
    private ArrayAdapter<String> availableRoutesAdapter;
    private String currentLanguage;
    private SharedPreferences settings;
    private HashMap<Integer, Route> hashMapRoutes = new HashMap<>();
    private ProgressBar progressBar;
    private ImageButton settingsButton;
    private Button campusButton;
    private Button attractionsButton;
    // +=15
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSettings();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.route_activity);

        progressBar = findViewById(R.id.progressbarRoute);

        settingsButton = findViewById(R.id.settingsButton);
        campusButton = findViewById(R.id.campus);
        attractionsButton = findViewById(R.id.attractions);

        campusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkCurrentLanguage();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getRoutesFromHost();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (availableRoutesListView != null) {

            disableProgressBar();
            availableRoutesListView.setVisibility(ListView.VISIBLE);
        }
        checkCurrentLanguage();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void loadSettings() {
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
        currentLanguage = settings.getString("Language", "N/A");
    }

    private void checkCurrentLanguage() {
        String currentLanguage = settings.getString("Language", "N/A");
        if (currentLanguage.equals(Language.Chinese.getId())) {
            campusButton.setText(R.string.campus_ch);
            campusButton.setTextSize(14);
            attractionsButton.setText(R.string.attractions_ch);
        } else if (currentLanguage.equals(Language.English.getId())) {
            campusButton.setText(R.string.campus_eng);
            campusButton.setTextSize(13);
            attractionsButton.setText(R.string.attractions_eng);
        }
    }

    public void getRoutesFromHost() {

        Log.e("We are in getRoutes", "Yes");

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final String baseHostApiUrl = "http://roadtourfu.ai-info.ru/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        // TODO: вынести в константу основной путь
        String url = baseHostApiUrl + "/data/get_routes.php";
        String readLanguageSetting = settings.getString("Language", "N/A");

        FormBody formBody = new FormBody.Builder()
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
                        JSONArray jsonArray = new JSONArray(myResponse);

                        // Обязательно запускать через этот поток, иначе будет ошибка изменения элементов вне потока
                        // Формируется Categories из Json
                        Log.e("json", String.valueOf(jsonArray));

                        RouteActivity.this.runOnUiThread(() -> buildRoutesByJson(jsonArray));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void buildRoutesByJson(JSONArray jsonArray) {
        /*
            route_id
            route_name
            route_alt_name
            route_category_id
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

                int route_id = object.getInt("route_id");//getIdFromString(object);

                String name = object.getString("route_name");

                String alt_name = object.getString("route_alt_name");

                int route_category_id = object.getInt("route_category_id");

                hashMapRoutes.put(i, new Route(route_id, name, alt_name, route_category_id, currentLanguage));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String[] local_points = new String[hashMapRoutes.size()];

        for (int i = 0; i < hashMapRoutes.size(); i++) {
            String name = Objects.requireNonNull(hashMapRoutes.get(i)).getName();

            String alt_name = Objects.requireNonNull(hashMapRoutes.get(i)).getAltName();

            String full_name = alt_name + " " + "\n" + " " + name;

            local_points[i] = full_name;
        }

        setupAdapterAndListview(local_points);
    }

    private void setupAdapterAndListview(String[] points) {
        try {

            disableProgressBar();

            availableRoutesListView = findViewById(R.id.myList);

            availableRoutesAdapter = new ArrayAdapter<>(this,
                    R.layout.array_adapter_custom_layout, points);

            availableRoutesListView.setAdapter(availableRoutesAdapter);

            availableRoutesListView.setOnItemClickListener((parent, view, position, id) -> {

                new MapActivityHandler().run();

                disableProgressBar();

            });
        } catch (Exception e) {
        }
    }

    private void disableProgressBar() {
        progressBar.setVisibility(ProgressBar.GONE);
    }

    private void enableProgressBar() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
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

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

    }


    class MapActivityHandler implements Runnable {

        @Override
        public void run() {

            Handler mapActivityHandler = new Handler();
            mapActivityHandler.post(new Runnable() {
                @Override
                public void run() {

                    availableRoutesListView.setVisibility(ListView.GONE);

                    enableProgressBar();

                    Intent intent = new Intent(RouteActivity.this, MapActivityRoutes.class);

                    intent.putExtra("route", hashMapRoutes.get(position));

                    startActivity(intent);
                }

            });
        }
    }
}
