package com.example.urfu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

<<<<<<< HEAD
<<<<<<< HEAD


=======
=======
>>>>>>> parent of 4e14d25... Completed code review
    String[] Category_Campus = new String[]
            {
                    "育大楼\nУчебные корпуса",
                    "体育设施\nСпортивные объекты",
                    "宿舍\nОбщежития",
                    "大学医院\nМСЧ"
            };
<<<<<<< HEAD
>>>>>>> parent of 4e14d25... Completed code review
=======
>>>>>>> parent of 4e14d25... Completed code review
    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;
    Database db = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Disable landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db.getCategoriesFromHost();
        Category_Campus = db.getCategories();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        //listView = findViewById(R.id.myList);

        /*adapter = new ArrayAdapter<>(this,
                R.layout.array_adapter_custom_layout, Category_Campus);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EducationalBuilding.class);
                intent.putExtra("pos", position);
                startActivity(intent);
            }
        });*/

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

        // Конечный ресурс, где идёт обработка логина и пароля
        // TODO: вынести в константу основной путь
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
            public void onResponse(Call call, Response response) throws IOException
            {
                if(response.isSuccessful())
                {
                    final String myResponse = response.body().string();
                    try
                    {
                        // Объявляется экземпляр класса JSONObject, где аргумент -
                        // это полученная строка от сервера.
                        JSONArray jsonArray = new JSONArray(myResponse);

                        // Обязательно запускать через этот поток, иначе будет ошибка изменения элементов вне потока
                        // Формируется Categories из Json
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                buildCategoriesByJson(jsonArray);
                            }
                        });
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

        Log.e("somename", String.valueOf(jsonArray));
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
                int category_id = getIdFromString(object);
                String name = object.getString("category_name");
                String alt_name = object.getString("category_alt_name");

                categories.put(category_id - 1, new Category(category_id, name, alt_name));;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        String[] local_Category_Campus = new String[categories.size()];

        for(int i = 0; i < categories.size(); i++)
        {
            String name = categories.get(i).getName();

            String alt_name = categories.get(i).getAltName();

            String full_name = alt_name + "\n" + name;

            local_Category_Campus[i] = full_name;
        }

        setup_adapter_and_listview(local_Category_Campus);

    }

    private void setup_adapter_and_listview(String[] categories)
    {
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.myList);

        adapter = new ArrayAdapter<>(this,
                R.layout.array_adapter_custom_layout, categories);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EducationalBuilding.class);
                intent.putExtra("pos", position);
                startActivity(intent);
            }
        });
    }

    private int getIdFromString(JSONObject object) throws JSONException {
        return Integer.valueOf(object.getString("id"));
    }
}