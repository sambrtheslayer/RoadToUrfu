package com.example.urfu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private ArrayList<ImageView> imageArray = new ArrayList<>();

    public ImageAdapter(ArrayList<ImageView> images) {
        this.imageArray = images;

    }

    @Override
    public int getCount() {
        return imageArray.size();
    }

    @Override
    public Object getItem(int position) {
        return imageArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = imageArray.get(position);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(340, 350));
        return imageView;
    }
}
