package com.example.urfu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Database {
    private MapPoint mCustomPoint;
    private Context mContext;
    private HashMap<Integer, Category> categories = new HashMap<>();

    public Database(Context context) {
        mContext = context;
    }

    public HashMap<Integer, Category> getCategories() {
        return (categories == null) ? new HashMap<>() : categories;
    }

    private GeoPoint getGeoPoint(String responseFromServer) {
        GeoPoint geoPoint = new GeoPoint(0d, 0d);
        try {
            JSONObject formedJsonFromResponse = new JSONObject(responseFromServer);

            try {
                int id = (int) formedJsonFromResponse.get("id");
                double latitude = (double) formedJsonFromResponse.get("lat");
                double longitude = (double) formedJsonFromResponse.get("lon");
                Drawable icon = getIconFromString((String) formedJsonFromResponse.get("icon"));

                geoPoint = formGeoPoint(latitude, longitude);

            } catch (Exception e) {
                Log.e("GeoPoint forming error", e.getMessage());
            }


        } catch (JSONException e) {
            Log.e("JSON error", e.getMessage());
        }

        return geoPoint;
    }

    private GeoPoint formGeoPoint(double latitude, double longitude) {
        return new GeoPoint(latitude, longitude);
    }

    private void setId(int id) {
        if (mCustomPoint != null) {
            mCustomPoint.setId(id);
        }
    }

    private Drawable getIconFromString(String sourceString) {
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.wine_bottle);
        switch (sourceString) {
            case "wine_bottle":
                drawable = ContextCompat.getDrawable(mContext, R.drawable.wine_bottle);
                break;
        }

        return drawable;
    }

    private void buildCategoriesByJson(JSONArray jsonArray) {
        Log.e("somename", String.valueOf(jsonArray));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = null;
            try {
                object = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                // Помещение точек в список.
                int category_id = getIdFromString(object);
                String name = object.getString("category_name");

                categories.put(category_id - 1, new Category(category_id, name, "While nothing here"));
                ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int getIdFromString(JSONObject object) throws JSONException {
        return Integer.valueOf(object.getString("id"));
    }

    public void getCategoriesFromHost() {
        OkHttpClient client = new OkHttpClient();

        // Конечный ресурс, где идёт обработка логина и пароля
        String url = "https://roadtourfu.000webhostapp.com/api/data/get_categories.php";

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
                    final String myResponse = response.body().string();
                    try {
                        // Объявляется экземпляр класса JSONObject, где аргумент -
                        // это полученная строка от сервера.
                        JSONArray jsonArray = new JSONArray(myResponse);

                        // Формируется Categories из Json
                        buildCategoriesByJson(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
