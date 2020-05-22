package com.shoppament.ui.activity.registration;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.shoppament.data.models.PictureModel;
import com.shoppament.data.models.ShopKeeperDataModel;
import com.shoppament.data.models.SlotTimingModel;
import com.shoppament.data.repo.RegistrationRepository;
import com.shoppament.ui.base.BaseViewModel;
import com.shoppament.utils.TimeFormatManager;

import java.util.ArrayList;
import java.util.List;

public class RegistrationViewModel extends BaseViewModel {
    private RegistrationRepository registrationRepository;
    private ShopKeeperDataModel shopKeeperDataModel;

    private List<String> shopTypesList = new ArrayList<>();
    private List<PictureModel> pictureModels = new ArrayList<>();
    public MutableLiveData<List<SlotTimingModel>> slotTimingLiveData = null;
    private MutableLiveData<Integer> perSlotTimeLiveData = new MutableLiveData<>();

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        registrationRepository = new RegistrationRepository(application);
    }

    MutableLiveData<List<PictureModel>> uploadNewPicture(PictureModel pictureModel){
        MutableLiveData<List<PictureModel>> uploadNewPictureLiveData = new MutableLiveData<>();
        if(pictureModel != null) {
            pictureModels.add(pictureModel);
            uploadNewPictureLiveData.setValue(pictureModels);
        }
        return uploadNewPictureLiveData;
    }

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

    boolean isUploadNewPictureEnabled() {
        return pictureModels.size() < 5;
    }

    MutableLiveData<List<String>> addShopType(String type){
        MutableLiveData<List<String>> shopTypeLiveData = new MutableLiveData<>();
        if(type != null && !shopTypesList.contains(type)) {
            shopTypesList.add(type);
            shopTypeLiveData.setValue(shopTypesList);
        }
        return shopTypeLiveData;
    }

    List<String> getShopTypesList() {
        return shopTypesList;
    }

    List<PictureModel> getPictureModels() {
        return pictureModels;
    }

    MutableLiveData<List<SlotTimingModel>> getSlotTimingLiveData() {
        if(slotTimingLiveData == null) {
            slotTimingLiveData = new MutableLiveData<>();
            slotTimingLiveData.setValue(new ArrayList<SlotTimingModel>());
        }
        return slotTimingLiveData;
    }

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

    MutableLiveData<Integer> getPerSlotTime(int totalCapacity, String averageHours, String averageMinutes) {
        perSlotTimeLiveData.setValue(totalCapacity * TimeFormatManager.getInstance().getMinutesFromHhMm(averageHours, averageMinutes));
        return perSlotTimeLiveData;
    }

    private Integer getPerSlotTimeValue() {
        return perSlotTimeLiveData.getValue();
    }

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

//        int slotsNumber = (int) result;
//        if(result - slotsNumber == 0){
//            slotsNumber--;
//        }

        List<SlotTimingModel> slotTimingModels = new ArrayList<>();
        SlotTimingModel slotTimingModel = new SlotTimingModel();
        slotTimingModel.setFromDate(TimeFormatManager.getInstance().format12Hours(startingTimeMinutes));
//        slotTimingModel.setToDate(TimeFormatManager.getInstance().format12Hours(endingTimeMinutes));
//        slotTimingModels.add(slotTimingModel);

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
//        slotTimingModel.setToDate(TimeFormatManager.getInstance().format12Hours(endingTimeMinutes));
//        slotTimingModels.add(slotTimingModel);

        slotTimingLiveData.setValue(slotTimingModels);
    }

    private void deleteSelectedSlot() {

    }

    private void sendOtp() {

    }

    MutableLiveData<ArrayList<String>> showShopTypeList(){
        MutableLiveData<ArrayList<String>> dataListLiveData = new MutableLiveData<>();
        dataListLiveData.setValue(registrationRepository.getShopTypes());
        return dataListLiveData;
    }

    MutableLiveData<ArrayList<String>> getCities(){
        MutableLiveData<ArrayList<String>> dataListLiveData = new MutableLiveData<>();
        dataListLiveData.setValue(registrationRepository.getCities());
        return dataListLiveData;
    }

    MutableLiveData<ArrayList<String>> getStates(){
        MutableLiveData<ArrayList<String>> dataListLiveData = new MutableLiveData<>();
        dataListLiveData.setValue(registrationRepository.getStates());
        return dataListLiveData;
    }

    MutableLiveData<ArrayList<String>> getCountries(){
        MutableLiveData<ArrayList<String>> dataListLiveData = new MutableLiveData<>();
        dataListLiveData.setValue(registrationRepository.getCountries());
        return dataListLiveData;
    }

//    public void submitTheRegistration() {
//        if(shopKeeperDataModel == null)
//            shopKeeperDataModel = new ShopKeeperDataModel();
//
//        shopKeeperDataModel.setsh
//    }

    public MutableLiveData<String> fetchData(){
        return registrationRepository.fetchData();
    }

}
