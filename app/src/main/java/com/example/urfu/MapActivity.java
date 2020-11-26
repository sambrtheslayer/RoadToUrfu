package com.example.urfu;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Point point = (Point) getIntent().getParcelableExtra("point");

        Log.e("name", point.getName());
        Log.e("id", String.valueOf(point.getId()));
        Log.e("Altname", point.getName());
    }
}