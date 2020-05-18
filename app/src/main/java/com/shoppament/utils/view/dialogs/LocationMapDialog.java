package com.shoppament.utils.view.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shoppament.R;
import com.shoppament.utils.callbacks.OnObjectChangedListener;

public class LocationMapDialog extends BaseCustomDialog implements OnMapReadyCallback {
    private FragmentManager fragmentManager;
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;

    public LocationMapDialog(Activity activity, FragmentManager fragmentManager, OnObjectChangedListener onObjectChangedListener) {
        super(activity, R.layout.layout_map_dialog, onObjectChangedListener);
        this.fragmentManager = fragmentManager;
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

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(supportMapFragment != null){
                    fragmentManager.beginTransaction().remove(supportMapFragment).commit();
                }
                alert.dismiss();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onObjectChangedListener!=null)
                    onObjectChangedListener.onObjectChanged(0,0,alert);
                alert.dismiss();
            }
        });
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        updateMarker(new LatLng(19.088918, 72.883525));
    }

    private void updateMarker(LatLng latLng) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .anchor(0.5f, 0.5f)
                .title("Title here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }
}
