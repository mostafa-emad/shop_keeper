package com.shoppament.data.models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class AddressLocationModel extends BaseModel {
    private double latitude;
    private double longitude;

    private Address address;

    private String addressLine;
    private String postalCode;
    private String country;
    private String state;
    private String city;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.addressLine = address.getAddressLine(0);
        this.city = address.getLocality();
        this.state = address.getAdminArea();
        this.country = address.getCountryName();
        this.postalCode = address.getPostalCode();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @NonNull
    @Override
    public String toString() {
        return addressLine +"-"+city+"-"+state;
    }

    public LatLng getLocation(Context context) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latLng = null;
        try {
            StringBuilder addressBuilder = new StringBuilder();
            if(addressLine!=null && !addressLine.isEmpty()) {
                addressBuilder.append(addressLine);
            }
            if(city!=null && !city.isEmpty()) {
                if(!addressBuilder.toString().isEmpty())
                    addressBuilder.append(" ");
                addressBuilder.append(city);
            }
            if(country!=null && !country.isEmpty()) {
                if(!addressBuilder.toString().isEmpty())
                    addressBuilder.append(" ");
                addressBuilder.append(country);
            }
            if(addressBuilder.toString().isEmpty())
                return null;

            address = coder.getFromLocationName(addressBuilder.toString(), 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return latLng;
    }
}
