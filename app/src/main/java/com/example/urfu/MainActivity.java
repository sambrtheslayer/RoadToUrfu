package com.example.urfu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.icu.util.ChineseCalendar;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
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

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;
    SharedPreferences settings;
    ImageButton settingsButton;
    Button campusButton;
    Button attractionsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSettings();

        // Disabled landscape mode.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        campusButton = findViewById(R.id.campus);
        attractionsButton = findViewById(R.id.attractions);

        searchView = findViewById(R.id.searchView);
        settingsButton = findViewById(R.id.settingsButton);

        attractionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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

        checkCurrentLanguage();
    }

    @Override
    protected void onStart() {
        super.onStart();


        //---------------
        //Intent intent = new Intent(MainActivity.this, RouteActivity.class);

        //startActivity(intent);


        //---------------



        try {
            getCategoriesFromHost();
        } catch (Exception e) {
            Log.e("Error getting", e.getMessage());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentLanguage();
        Log.e("Main Activ. Resume", "Method has resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Main Activ. Pause", "Method has paused");
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


    public void getCategoriesFromHost() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final String baseHostApiUrl = "http://roadtourfu.ai-info.ru/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        // TODO: вынести в константу основной путь
        String url = baseHostApiUrl + "/data/get_categories.php";
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

                        MainActivity.this.runOnUiThread(() -> buildCategoriesByJson(jsonArray));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void buildCategoriesByJson(JSONArray jsonArray) {
        HashMap<Integer, Category> categories = new HashMap<>();

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

                int category_id = getIdFromString(object);

                String name = object.getString("category_name");

                String alt_name = object.getString("category_alt_name");

                categories.put(category_id, new Category(category_id, name, alt_name));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String[] local_Category_Campus = new String[categories.size()];

        for (int i = 0; i < categories.size(); i++) {
            String name = Objects.requireNonNull(categories.get(i)).getName();

            String alt_name = Objects.requireNonNull(categories.get(i)).getAltName();

            String full_name = alt_name + " " + "\n" + " " + name;

            local_Category_Campus[i] = full_name;
        }

        setupAdapterAndListview(local_Category_Campus);
    }

    private void setupAdapterAndListview(String[] categories) {
        disableProgressBar();

        searchView = findViewById(R.id.searchView);

        listView = findViewById(R.id.myList);

        adapter = new ArrayAdapter<>(this,
                R.layout.array_adapter_custom_layout, categories);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);

            intent.putExtra("pos", position);

            startActivity(intent);
        });
    }

    private void disableProgressBar() {
        ProgressBar progress = findViewById(R.id.progressbar);
        progress.setVisibility(ProgressBar.GONE);
    }

    private int getIdFromString(JSONObject object) throws JSONException {
        return Integer.parseInt(object.getString("id"));
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

}