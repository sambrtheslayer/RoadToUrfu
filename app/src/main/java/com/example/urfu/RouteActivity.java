package com.example.urfu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class RouteActivity extends AppCompatActivity {

    private ListView availableRoutesListView;
    private ArrayAdapter<String> availableRoutesAdapter;
    private String currentLanguage;
    private SharedPreferences settings;
    private HashMap<Integer, Route> hashMapRoutes = new HashMap<>();

    // +=15
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSettings();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //TODO: привязать Layout для активности
        setContentView(R.layout.category_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getRoutesFromHost();

    }

    @Override
    protected void onResume() {
        super.onResume();

        reloadLanguageForActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void loadSettings() {
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
        currentLanguage = settings.getString("Language", "N/A");
    }

    private void reloadLanguageForActivity() {
        //TODO: добавить код на переключение языка во всех элементах
    }

    public void getRoutesFromHost() {
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

            //TODO: searchview, progressbar

            //disableProgressBar();

            //searchView = findViewById(R.id.searchView);

            availableRoutesListView = findViewById(R.id.myList);

            availableRoutesAdapter = new ArrayAdapter<>(this,
                    R.layout.array_adapter_custom_layout, points);

            availableRoutesListView.setAdapter(availableRoutesAdapter);

            availableRoutesListView.setOnItemClickListener((parent, view, position, id) -> {

                new MapActivityHandler().run();

                //TODO:
                //disableProgressBar();

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
    class MapActivityHandler implements Runnable {

        @Override
        public void run() {

            Handler mapActivityHandler = new Handler();
            mapActivityHandler.post(new Runnable() {
                @Override
                public void run() {

                    availableRoutesListView.setVisibility(ListView.GONE);

                    //TODO:
                    //enableProgressBar();

                    Intent intent = new Intent(RouteActivity.this, MapActivity.class);

                    intent.putExtra("route", hashMapRoutes.get(position));

                    startActivity(intent);
                }

            });
        }
    }
}
