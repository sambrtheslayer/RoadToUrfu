package com.example.urfu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

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


public class CategoryActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;
    ImageButton btnBack;
    SharedPreferences settings;
    int position;
    HashMap<Integer, Point> hashMapPoints = new HashMap<>();
    ImageButton settingsButton;
    Button campusButton;
    Button attractionsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings();
        // Disable landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.category_activity);

        campusButton = findViewById(R.id.campus);
        attractionsButton = findViewById(R.id.attractions);

        searchView = findViewById(R.id.searchView);
        settingsButton = findViewById(R.id.settingsButton);

        CheckCurrentLanguage();

        try {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
        } catch (Exception e) {
            Log.e("Search error", e.getMessage());
        }

        btnBack = findViewById(R.id.ButtonBack);
        Bundle argument = getIntent().getExtras();

        if (argument != null) {
            position = argument.getInt("pos");
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        getPointsFromHost();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Category Activ. Resume", "Method has resumed");

        if (listView != null) {

            disableProgressBar();

            listView.setVisibility(ListView.VISIBLE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Category Activ. Pause", "Method has paused");
    }

    private void CheckCurrentLanguage()
    {
        String currentLanguage = settings.getString("Language", "N/A");
        if(currentLanguage.equals(Language.Chinese.getId()))
        {
            campusButton.setText(R.string.campus_ch);
            attractionsButton.setText(R.string.attractions_ch);
            searchView.setQueryHint("动漫是我的生活");
            Log.e("text size ch", String.valueOf(campusButton.getTextSize()));
        }
        else if(currentLanguage.equals(Language.English.getId()))
        {
            campusButton.setText(R.string.campus_eng);
            campusButton.setTextSize(13);
            searchView.setQueryHint("Find a campus building");
            attractionsButton.setText(R.string.attractions_eng);
            Log.e("text size eng", String.valueOf(campusButton.getTextSize()));
        }
    }

    public void getPointsFromHost() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final String baseHostApiUrl = "http://roadtourfu.ai-info.ru/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        String url = baseHostApiUrl + "/data/get_points.php";
        String readLanguageSetting = settings.getString("Language", "N/A");

        FormBody formBody = new FormBody.Builder()
                .add("category", String.valueOf(position))
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
                        CategoryActivity.this.runOnUiThread(() -> buildPointsByJson(jsonArray));
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

        setupAdapterAndListview(local_points);
    }

    private int getIdFromString(JSONObject object) throws JSONException {
        return Integer.parseInt(object.getString("point_id"));
    }

    private void setupAdapterAndListview(String[] points) {
        try {
            disableProgressBar();

            searchView = findViewById(R.id.searchView);

            listView = findViewById(R.id.myList);

            adapter = new ArrayAdapter<>(this,
                    R.layout.array_adapter_custom_layout, points);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {

                new MapActivityHandler(position).run();

                disableProgressBar();

            /*Intent intent = new Intent(CategoryActivity.this, MapActivity.class);

            Log.e("position", String.valueOf(position));

            Log.e("hashMapPoints", String.valueOf(hashMapPoints.size()));

            Log.e("hashMapPoints getname", hashMapPoints.get(position).getName());

            Log.e("hash getaltname", hashMapPoints.get(position).getAltName());

            intent.putExtra("point", hashMapPoints.get(position));

            startActivity(intent);*/
            });
        } catch (Exception e) {
        }
    }

    private void disableProgressBar() {
        ProgressBar progress = findViewById(R.id.progressbar);
        progress.setVisibility(ProgressBar.GONE);
    }

    private void enableProgressBar() {
        ProgressBar progress = findViewById(R.id.progressbar);
        progress.setVisibility(ProgressBar.VISIBLE);
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

    private void loadSettings() {
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
    }

    class MapActivityHandler implements Runnable {
        private final int position;

        public MapActivityHandler(int position) {
            this.position = position;
        }

        @Override
        public void run() {

            Handler mapActivityHandler = new Handler();
            mapActivityHandler.post(new Runnable() {
                @Override
                public void run() {

                    listView.setVisibility(ListView.GONE);

                    enableProgressBar();

                    Intent intent = new Intent(CategoryActivity.this, MapActivity.class);

                    Log.e("position", String.valueOf(position));

                    Log.e("hashMapPoints", String.valueOf(hashMapPoints.size()));

                    Log.e("hashMapPoints getname", hashMapPoints.get(position).getName());

                    Log.e("hash getaltname", hashMapPoints.get(position).getAltName());

                    intent.putExtra("point", hashMapPoints.get(position));

                    startActivity(intent);
                }

            });
        }
    }
}