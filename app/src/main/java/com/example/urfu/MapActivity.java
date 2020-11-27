package com.example.urfu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Point point = (Point) getIntent().getParcelableExtra("point");

        Log.e("name", point.getName());
        Log.e("id точки в базе", String.valueOf(point.getId()));
        Log.e("Altname", point.getAltName());

        Log.e("image", String.valueOf(point.getDescriptionImage()));

        //ImageView imageView = findViewById(R.id.imageView);
        //imageView.setImageBitmap(point.getDescriptionImage());

        //Bitmap bitmap = BitmapFactory.decodeStream()
    }
}