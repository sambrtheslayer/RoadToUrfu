package com.example.urfu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CategoryActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;
    ImageButton btnBack;
    int position;
    HashMap<Integer, Point> hashMapPoints = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Disable landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.category_activity);

        searchView = findViewById(R.id.searchView);
        //listView = findViewById(R.id.myList);
        btnBack = findViewById(R.id.ButtonBack);
        Bundle argument = getIntent().getExtras();

        if (argument != null) {
            position = argument.getInt("pos");
        }

        //listView.setAdapter(adapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);*/
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

        if(listView != null) {

            disableProgressBar();

            listView.setVisibility(ListView.VISIBLE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Category Activ. Pause", "Method has paused");
    }

    public void getPointsFromHost()
    {
        OkHttpClient client = new OkHttpClient();

        final String baseHostApiUrl = "https://roadtourfu.000webhostapp.com/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        String url = baseHostApiUrl + "/data/get_points.php";

        FormBody formBody = new FormBody.Builder()
                .add("category", String.valueOf(position))
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
                        CategoryActivity.this.runOnUiThread(() -> buildPointsByJson(jsonArray));
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

        setupAdapterAndListview(local_points);
    }

    private int getIdFromString(JSONObject object) throws JSONException {
        return Integer.parseInt(object.getString("point_id"));
    }

    private void setupAdapterAndListview(String[] points)
    {
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
    }
    private void disableProgressBar()
    {
        ProgressBar progress = findViewById(R.id.progressbar);
        progress.setVisibility(ProgressBar.GONE);
    }

    private void enableProgressBar()
    {
        ProgressBar progress = findViewById(R.id.progressbar);
        progress.setVisibility(ProgressBar.VISIBLE);
    }

    class MapActivityHandler implements Runnable
    {
        private final int position;

        public MapActivityHandler(int position)
        {
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
