package com.shoppament.ui.activity.registration;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.shoppament.data.models.PictureModel;
import com.shoppament.data.models.SlotTimingModel;
import com.shoppament.data.remote.model.response.BaseResponse;
import com.shoppament.data.repo.RegistrationRepository;
import com.shoppament.ui.base.BaseViewModel;
import com.shoppament.utils.TimeFormatManager;

import java.util.ArrayList;
import java.util.List;

public class RegistrationViewModel extends BaseViewModel {
    private RegistrationRepository registrationRepository;

    private List<String> shopTypesList = new ArrayList<>();
    private List<PictureModel> pictureModels = new ArrayList<>();
    public MutableLiveData<List<SlotTimingModel>> slotTimingLiveData = null;
    private MutableLiveData<Integer> perSlotTimeLiveData = new MutableLiveData<>();

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        registrationRepository = new RegistrationRepository(application);
    }

    /**
     * upload new picture that is has max number 5
     * @param pictureModel
     * @return
     */
    MutableLiveData<List<PictureModel>> uploadNewPicture(PictureModel pictureModel){
        MutableLiveData<List<PictureModel>> uploadNewPictureLiveData = new MutableLiveData<>();
        if(pictureModel != null) {
            pictureModels.add(pictureModel);
            uploadNewPictureLiveData.setValue(pictureModels);
        }
        return uploadNewPictureLiveData;
    }

    /**
     * delete picture and update the list of pictures
     * @param position
     * @return
     */
    MutableLiveData<Boolean> deletePicture(int position){
        MutableLiveData<Boolean> deletePictureLiveData = new MutableLiveData<>();
        try {
            pictureModels.remove(position);
            deletePictureLiveData.setValue(true);
        }catch (Exception e){
            e.printStackTrace();
            deletePictureLiveData.setValue(false);
        }
        return deletePictureLiveData;
    }

    /**
     * delete exist slot per position and update the views
     * @param position
     */
    void deleteSlot(int position){
        try {
            List<SlotTimingModel> slotTimingModels = slotTimingLiveData.getValue();
            if(slotTimingModels != null){
                slotTimingModels.remove(position);
                slotTimingLiveData.setValue(slotTimingModels);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * check if user can upload new pictures by check about size of pictures list
     * @return
     */
    boolean isUploadNewPictureEnabled() {
        return pictureModels.size() < 5;
    }

    /**
     * add shop type to the list of shop types
     * @param type
     * @return
     */
    MutableLiveData<List<String>> addShopType(String type){
        MutableLiveData<List<String>> shopTypeLiveData = new MutableLiveData<>();
        if(type != null && !shopTypesList.contains(type)) {
            shopTypesList.add(type);
            shopTypeLiveData.setValue(shopTypesList);
        }
        return shopTypeLiveData;
    }

    /**
     * return list of available shop types from database
     *
     * @return
     */
    List<String> getShopTypesList() {
        return shopTypesList;
    }

    /**
     * return list of pictures that is uploaded by user later
     *
     * @return
     */
    List<PictureModel> getPictureModels() {
        return pictureModels;
    }

    /**
     * observe slot timing list to refresh UI automatically with any change happens add/delete
     *
     * @return
     */
    MutableLiveData<List<SlotTimingModel>> getSlotTimingLiveData() {
        if(slotTimingLiveData == null) {
            slotTimingLiveData = new MutableLiveData<>();
            slotTimingLiveData.setValue(new ArrayList<SlotTimingModel>());
        }
        return slotTimingLiveData;
    }

    /**
     * Calculate total capacity = insideCapacityValue + outsideCapacityValue
     * @param insideCapacityValue
     * @param outsideCapacityValue
     * @return
     */
    int getTotalCapacity(String insideCapacityValue, String outsideCapacityValue) {
        int insideCapacity = 0;
        if(!insideCapacityValue.isEmpty()){
            insideCapacity = Integer.parseInt(insideCapacityValue);
        }
        int outsideCapacity = 0;
        if(!outsideCapacityValue.isEmpty()){
            outsideCapacity = Integer.parseInt(outsideCapacityValue);
        }
        return insideCapacity + outsideCapacity;
    }

    /**
     * Calculate PerSlot Timing = totalCapacity * averageTime per Minutes
     * @param totalCapacity
     * @param averageHours
     * @param averageMinutes
     * @return
     */
    MutableLiveData<Integer> getPerSlotTime(int totalCapacity, String averageHours, String averageMinutes) {
        perSlotTimeLiveData.setValue(totalCapacity * TimeFormatManager.getInstance().getMinutesFromHhMm(averageHours, averageMinutes));
        return perSlotTimeLiveData;
    }

    /**
     * return Per Slot Timing value to use it in reset slots.
     *
     * @return
     */
    private Integer getPerSlotTimeValue() {
        return perSlotTimeLiveData.getValue();
    }

    /**
     * Calculate if slots can be created or not
     *
     * generate the slots depends on operational time and per slot value
     *
     * @param startingTimeMinutes
     * @param endingTimeMinutes
     */
    void setSlotsAndTimings(int startingTimeMinutes, int endingTimeMinutes) {
        int perSlotTimeMinutes = getPerSlotTimeValue();
        if(perSlotTimeMinutes == 0 || startingTimeMinutes == 0 || endingTimeMinutes == 0)
            return;

        int operationalTimeMinutes = endingTimeMinutes - startingTimeMinutes;
        double result = operationalTimeMinutes / perSlotTimeMinutes;

        //Case I:
        if(result < 3){
            slotTimingLiveData.setValue(new ArrayList<SlotTimingModel>());
            return;
        }

        List<SlotTimingModel> slotTimingModels = new ArrayList<>();
        SlotTimingModel slotTimingModel = new SlotTimingModel();
        slotTimingModel.setFromDate(TimeFormatManager.getInstance().format12Hours(startingTimeMinutes));

        int slotEndTimeMinutes = startingTimeMinutes;
        String slotFormat12Hours;
        while (operationalTimeMinutes > perSlotTimeMinutes){
            slotEndTimeMinutes += perSlotTimeMinutes;
            operationalTimeMinutes -= perSlotTimeMinutes;

            slotFormat12Hours = TimeFormatManager.getInstance().format12Hours(slotEndTimeMinutes);
            slotTimingModel.setToDate(slotFormat12Hours);

            slotTimingModels.add(slotTimingModel);

            slotTimingModel = new SlotTimingModel();
            slotTimingModel.setFromDate(slotFormat12Hours);
        }

        slotTimingLiveData.setValue(slotTimingModels);
    }

    /**
     * send OTP to user to complete registration
     *
     */
    private void sendOtp() {

    }

    /**
     * observe list of shop types from database
     *
     * @return
     */
    MutableLiveData<ArrayList<String>> showShopTypeList(){
        MutableLiveData<ArrayList<String>> dataListLiveData = new MutableLiveData<>();
        dataListLiveData.setValue(registrationRepository.getShopTypes());
        return dataListLiveData;
    }

    /**
     * return list of available cities from database
     *
     * @return
     */
    MutableLiveData<ArrayList<String>> getCities(){
        MutableLiveData<ArrayList<String>> dataListLiveData = new MutableLiveData<>();
        dataListLiveData.setValue(registrationRepository.getCities());
        return dataListLiveData;
    }

    /**
     * return list of available states from database
     *
     * @return
     */
    MutableLiveData<ArrayList<String>> getStates(){
        MutableLiveData<ArrayList<String>> dataListLiveData = new MutableLiveData<>();
        dataListLiveData.setValue(registrationRepository.getStates());
        return dataListLiveData;
    }

    /**
     * return list of available counties from database
     *
     * @return
     */
    MutableLiveData<ArrayList<String>> getCountries(){
        MutableLiveData<ArrayList<String>> dataListLiveData = new MutableLiveData<>();
        dataListLiveData.setValue(registrationRepository.getCountries());
        return dataListLiveData;
    }

    /**
     * submit the registration data
     *
     * @param shopKeeperDataJson
     * @return
     */
    MutableLiveData<BaseResponse> submitTheRegistration(String shopKeeperDataJson){
        return registrationRepository.submitTheRegistration(shopKeeperDataJson);
    }

    public MutableLiveData<String> fetchData(){
        return registrationRepository.fetchData();
    }

}
