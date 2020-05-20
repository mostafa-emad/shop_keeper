package com.shoppament.utils.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.shoppament.utils.callbacks.ILocationListener;

import java.util.List;

public class LocationController implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    @SuppressLint("StaticFieldLeak")
    private static LocationController locationController;
    private LocationManager locationManager;
    private Activity activity;

    private GoogleApiClient googleApiClient;
    private ILocationListener iLocationListener;

    public static LocationController getInstance() {
        if (locationController == null) {
            synchronized (LocationController.class) {
                LocationController manager = locationController;
                if (manager == null) {
                    synchronized (LocationController.class) {
                        locationController = new LocationController();
                    }
                }
            }
        }
        return locationController;
    }

    public void init(Activity activity){
        this.activity = activity;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean isProviderEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean hasGPSDevice() {
        if (locationManager == null)
            return false;
        final List<String> providers = locationManager.getAllProviders();
        return providers.contains(LocationManager.GPS_PROVIDER);
    }


    public void getGoogleApiLocation(ILocationListener iLocationListener) {
        this.iLocationListener = iLocationListener;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                if(iLocationListener != null) {
                    iLocationListener.setMyLocationEnabled(true);
                }
            }
        }
        else {
            buildGoogleApiClient();
            if(iLocationListener != null) {
                iLocationListener.setMyLocationEnabled(true);
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(iLocationListener != null) {
            iLocationListener.onCompleted(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        //stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(iLocationListener != null)
            iLocationListener.onError(connectionResult.getErrorMessage());
    }
}
