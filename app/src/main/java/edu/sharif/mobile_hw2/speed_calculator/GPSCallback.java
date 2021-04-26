package edu.sharif.mobile_hw2.speed_calculator;

import android.location.Location;

public interface GPSCallback {
    public abstract void onGPSUpdate(Location location);
}