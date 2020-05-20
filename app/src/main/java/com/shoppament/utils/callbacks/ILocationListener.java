package com.shoppament.utils.callbacks;

import com.google.android.gms.maps.model.LatLng;

public interface ILocationListener {
    void setMyLocationEnabled(boolean enabled);
    void onCompleted(LatLng latLng);
    void onError(String message);
}
