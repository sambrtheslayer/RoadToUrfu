package com.example.urfu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    final String[] Category_Campus1 = new String[]
            {
                    "育大楼\nУчебные корпуса",
                    "体育设施\nСпортивные объекты",
                    "宿舍\nОбщежития",
                    "大学医院\nМСЧ"
            };

    HashMap<Integer,Category> Category_Campus;
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
        listView = findViewById(R.id.myList);





        Category_Campus1[0] = Objects.requireNonNull(Category_Campus.get(0)).getName();
        Category_Campus1[1] = Objects.requireNonNull(Category_Campus.get(1)).getName();
        Category_Campus1[2] = Objects.requireNonNull(Category_Campus.get(2)).getName();
        Category_Campus1[3] = Objects.requireNonNull(Category_Campus.get(3)).getName();


        adapter = new ArrayAdapter<>(this,
                R.layout.array_adapter_custom_layout, Category_Campus1);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EducationalBuilding.class);
                intent.putExtra("pos", position);
                startActivity(intent);
            }
        });

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
}