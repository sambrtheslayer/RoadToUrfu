package com.example.urfu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;


public class CategoryActivity extends AppCompatActivity {
    final String[] Learn_Campus = new String[]
            {
                    "主教学楼\nГУК",
                    "无线电电子与信息技术研究所\nИРИТ-РТФ",
                    "自然科学与数学研究所\nИЕНиМ",
                    "拉尔人文研究所\nУГИ",
            };

    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;
    ImageButton btnBack;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Disable landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.educational_building);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.myList);
        btnBack = findViewById(R.id.ButtonBack);
        Bundle argument = getIntent().getExtras();

        if (argument != null) {
            position = argument.getInt("pos");
        }

        if (position == 0) {
            adapter = new ArrayAdapter<>(this,
                    R.layout.array_adapter_custom_layout, Learn_Campus);
        }

        listView.setAdapter(adapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
}
