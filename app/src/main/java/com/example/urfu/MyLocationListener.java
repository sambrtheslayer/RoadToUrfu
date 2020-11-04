package com.example.urfu;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

// Пользовательский класс, в котором можно редактировать свойства локации пользователя.
public class MyLocationListener implements LocationListener
{
    public TextView debugText;

    // Отвечает за изменение локации.
    public void onLocationChanged(final Location loc)
    {
        Log.e("*****", "Location changed: " + loc.toString());

        if(debugText != null)
            debugText.setText(loc.getLatitude() + " " + loc.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
