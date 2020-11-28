package com.example.urfu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Point point = (Point) getIntent().getParcelableExtra("point");

        Log.e("name", point.getName());
        Log.e("id точки в базе", String.valueOf(point.getId()));
        Log.e("Altname", point.getAltName());

        // Загрузка изображения у точки
        new DownloadImageTask().execute(point);

        /*ImageView image = findViewById(R.id.imageView);
        image.setImageBitmap(point.getDescriptionImage());*/

        //Log.e("image", point.getDescriptionImage().toString());


    }

    /*@Override
    protected void onStart() {
        super.onStart();

        new DownloadImageTask().execute("https://roadtourfu.000webhostapp.com/image/");
    }*/

    private class DownloadImageTask extends AsyncTask<Point, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Point... points) {

            String pathToImage = "https://roadtourfu.000webhostapp.com/image/";

            pathToImage = pathToImage + "point_" + points[0].getId() + ".PNG";

            Bitmap loadedImage = null;

            try {

                InputStream in = new java.net.URL(pathToImage).openStream();

                loadedImage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {

                Log.e("Error", e.getMessage());

                e.printStackTrace();

            }

            return loadedImage;
        }

        protected void onPostExecute(Bitmap result){

            ImageView image = findViewById(R.id.imageView);

            image.setImageBitmap(result);

        }
    }
}