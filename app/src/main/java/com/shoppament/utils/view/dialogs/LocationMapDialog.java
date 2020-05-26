package com.shoppament.utils.view.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shoppament.R;
import com.shoppament.data.models.AddressLocationModel;
import com.shoppament.utils.callbacks.ILocationListener;
import com.shoppament.utils.callbacks.OnTaskCompletedListener;
import com.shoppament.utils.view.LocationController;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationMapDialog extends BaseCustomDialog
        implements DialogInterface.OnDismissListener, OnMapReadyCallback {

    private AddressLocationModel addressLocationModel;
    private FragmentManager fragmentManager;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private Marker currentMarker;

    public LocationMapDialog(Activity activity, FragmentManager fragmentManager,
                             AddressLocationModel addressLocationModel,OnTaskCompletedListener onTaskCompletedListener) {
        super(activity, R.layout.layout_map_dialog, onTaskCompletedListener);
        if(isDialogShown())
            return;
        this.fragmentManager = fragmentManager;
        this.addressLocationModel = addressLocationModel;
        init();
        initGoogleMap();
    }

    @Override
    protected void init() {
        super.init();
        TextView doneBtn = rootView.findViewById(R.id.done_btn);

        manager.windowAnimations = R.style.DialogTheme;
        alert.show();
        alert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onTaskCompletedListener!=null)
                    onTaskCompletedListener.onCompleted(addressLocationModel);
                alert.dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if(supportMapFragment != null){
            fragmentManager.beginTransaction().remove(supportMapFragment).commit();
        }
        alert.dismiss();
    }

    private void initGoogleMap() {
        supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map_container);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(onTaskCompletedListener != null)
                onTaskCompletedListener.onError(OnTaskCompletedListener.DEFAULT_MESSAGE_DURATION
                        ,activity.getResources().getString(R.string.error_location_permissions));
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setAllGesturesEnabled(true);
        this.googleMap.getUiSettings().setMapToolbarEnabled(true);
        this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        initMapActions();

        LatLng latLng = addressLocationModel.getLocation(activity);
        if(latLng != null) {
            updateMarker(latLng,true);
        }else{
//            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(Location location) {
//                    updateMarker(new LatLng(location.getLatitude(),location.getLongitude()));
//                }
//            });
            LocationController.getInstance().getGoogleApiLocation(new ILocationListener() {
                @Override
                public void setMyLocationEnabled(boolean enabled) {
                    googleMap.setMyLocationEnabled(enabled);
                }

                @Override
                public void onCompleted(LatLng latLng) {
                    updateMarker(latLng,true);
                }

                @Override
                public void onError(String message) {
                    if(onTaskCompletedListener != null){
                        onTaskCompletedListener.onError(OnTaskCompletedListener.DEFAULT_MESSAGE_DURATION,message);
                    }
                }
            });
        }
    }

    private void initMapActions() {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                updateMarker(latLng,false);
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //create your custom title
                marker.setTitle(addressLocationModel.toString());
                marker.showInfoWindow();

                return true;
            }
        });
    }

    private void updateMarker(LatLng latLng,boolean shouldUpdateCamera) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        //Place current location marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentMarker = googleMap.addMarker(markerOptions);

        if(shouldUpdateCamera) {
            //move map camera
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }

        updateLocationAddress();
    }

    private void updateLocationAddress() {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            if(onTaskCompletedListener != null)
                onTaskCompletedListener.onError(OnTaskCompletedListener.DEFAULT_MESSAGE_DURATION
                        ,activity.getResources().getString(R.string.error_location_permissions));
            return;
        }
        if(addresses == null || addresses.isEmpty()){
            if(onTaskCompletedListener != null)
                onTaskCompletedListener.onError(OnTaskCompletedListener.DEFAULT_MESSAGE_DURATION
                        ,activity.getResources().getString(R.string.error_location_detect_address));
            return;
        }
        addressLocationModel.setAddress(addresses.get(0));
        addressLocationModel.setLatitude(currentMarker.getPosition().latitude);
        addressLocationModel.setLongitude(currentMarker.getPosition().longitude);
    }
}
