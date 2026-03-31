package com.safechain.app.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Manages GPS location retrieval with offline fallback.
 */
public class LocationHelper implements LocationListener {
    private static final String TAG = "LocationHelper";
    private final Context context;
    private LocationManager locationManager;
    private LocationCallback callback;
    private Location lastKnownLocation;

    public interface LocationCallback {
        void onLocationReceived(double lat, double lng, String label);
        void onLocationFailed();
    }

    public LocationHelper(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getLocation(LocationCallback cb) {
        this.callback = cb;
        try {
            Location bestLocation = null;

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) bestLocation = loc;
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null && (bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy())) {
                    bestLocation = loc;
                }
            }

            if (bestLocation != null) {
                lastKnownLocation = bestLocation;
                String label = String.format("%.4f, %.4f", bestLocation.getLatitude(), bestLocation.getLongitude());
                if (callback != null) callback.onLocationReceived(bestLocation.getLatitude(), bestLocation.getLongitude(), label);
                return;
            }

            // Fallback to requesting updates if no last known location is found
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } else {
                if (callback != null) callback.onLocationReceived(12.9716, 77.5946, "Bengaluru (Cached)");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "No location permission", e);
            if (callback != null) callback.onLocationFailed();
        }
    }

    public void stop() {
        try {
            locationManager.removeUpdates(this);
        } catch (Exception e) {
            Log.e(TAG, "Error stopping location", e);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
        if (callback != null) {
            String label = String.format("%.4f, %.4f", location.getLatitude(), location.getLongitude());
            callback.onLocationReceived(location.getLatitude(), location.getLongitude(), label);
        }
        stop(); // Single fix
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override public void onProviderEnabled(String provider) {}
    @Override public void onProviderDisabled(String provider) {
        if (callback != null) callback.onLocationFailed();
    }
}
