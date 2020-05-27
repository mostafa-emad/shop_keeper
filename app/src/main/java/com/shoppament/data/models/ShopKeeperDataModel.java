package com.shoppament.data.models;

import java.util.List;

public class ShopKeeperDataModel extends BaseModel {
    private String shopName;
    private List<String> shopTypeList;
    private String shopDescription;
    private List<PictureModel> pictureModels;

    private String startingOperationalTime;
    private String endingOperationalTime;
    private String averageTime;
    private String insideCapacity;
    private String outsideCapacity;
    private String totalCapacity;
    private String perSlotTime;
    private String perSlotTimeValue;
    private List<SlotTimingModel> slotTimingModels;

    private double latitude;
    private double longitude;

    private String shopDoorNumber;
    private String apartmentStreetName;
    private String pinCode;
    private String country;
    private String state;
    private String city;

    private String phoneNumber;
    private String OTP;
    private String emailID;
    private String GSTNumber;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public List<String> getShopTypeList() {
        return shopTypeList;
    }

    public void setShopTypeList(List<String> shopTypeList) {
        this.shopTypeList = shopTypeList;
    }

    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
    }

    public List<PictureModel> getPictureModels() {
        return pictureModels;
    }

    public void setPictureModels(List<PictureModel> pictureModels) {
        this.pictureModels = pictureModels;
    }

    public String getStartingOperationalTime() {
        return startingOperationalTime;
    }

    public void setStartingOperationalTime(String startingOperationalTime) {
        this.startingOperationalTime = startingOperationalTime;
    }

    public String getEndingOperationalTime() {
        return endingOperationalTime;
    }

    public void setEndingOperationalTime(String endingOperationalTime) {
        this.endingOperationalTime = endingOperationalTime;
    }

    public String getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(String averageTime) {
        this.averageTime = averageTime;
    }

    public String getInsideCapacity() {
        return insideCapacity;
    }

    public void setInsideCapacity(String insideCapacity) {
        this.insideCapacity = insideCapacity;
    }

    public String getOutsideCapacity() {
        return outsideCapacity;
    }

    public void setOutsideCapacity(String outsideCapacity) {
        this.outsideCapacity = outsideCapacity;
    }

    public String getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(String totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public String getPerSlotTime() {
        return perSlotTime;
    }

    public void setPerSlotTime(String perSlotTime) {
        this.perSlotTime = perSlotTime;
    }

    public String getPerSlotTimeValue() {
        return perSlotTimeValue;
    }

    public void setPerSlotTimeValue(String perSlotTimeValue) {
        this.perSlotTimeValue = perSlotTimeValue;
    }

    public List<SlotTimingModel> getSlotTimingModels() {
        return slotTimingModels;
    }

    public void setSlotTimingModels(List<SlotTimingModel> slotTimingModels) {
        this.slotTimingModels = slotTimingModels;
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

    public String getShopDoorNumber() {
        return shopDoorNumber;
    }

    public void setShopDoorNumber(String shopDoorNumber) {
        this.shopDoorNumber = shopDoorNumber;
    }

    public String getApartmentStreetName() {
        return apartmentStreetName;
    }

    public void setApartmentStreetName(String apartmentStreetName) {
        this.apartmentStreetName = apartmentStreetName;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getGSTNumber() {
        return GSTNumber;
    }

    public void setGSTNumber(String GSTNumber) {
        this.GSTNumber = GSTNumber;
    }
}
