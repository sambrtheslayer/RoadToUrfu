package com.example.urfu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Disabled landscape mode.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);

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
        }
        catch(Exception e)
        {
            Log.e("Search error", e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            getCategoriesFromHost();
        }
        catch (Exception e) {
            Log.e("Error getting", e.getMessage());
        }
    }

    public void getCategoriesFromHost()
    {
        OkHttpClient client = new OkHttpClient();

        final String baseHostApiUrl = "https://roadtourfu.000webhostapp.com/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        // TODO: вынести в константу основной путь
        String url = baseHostApiUrl + "/data/get_categories.php";

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
                        JSONArray jsonArray = new JSONArray(myResponse);

                        // Обязательно запускать через этот поток, иначе будет ошибка изменения элементов вне потока
                        // Формируется Categories из Json
                        Log.e("json", String.valueOf(jsonArray));

                        MainActivity.this.runOnUiThread(() -> buildCategoriesByJson(jsonArray));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void buildCategoriesByJson(JSONArray jsonArray)
    {
        HashMap<Integer, Category> categories = new HashMap<>();

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

                int category_id = getIdFromString(object);

                String name = object.getString("category_name");

                String alt_name = object.getString("category_alt_name");

                categories.put(category_id, new Category(category_id, name, alt_name));

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        String[] local_Category_Campus = new String[categories.size()];

        for(int i = 0; i < categories.size(); i++)
        {
            String name = Objects.requireNonNull(categories.get(i)).getName();

            String alt_name = Objects.requireNonNull(categories.get(i)).getAltName();

            String full_name = alt_name + " " +  "\n" + " " + name;

            local_Category_Campus[i] = full_name;
        }

        setupAdapterAndListview(local_Category_Campus);
    }

    private void setupAdapterAndListview(String[] categories)
    {
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

    private int getIdFromString(JSONObject object) throws JSONException {
        return Integer.parseInt(object.getString("id"));
    }
}