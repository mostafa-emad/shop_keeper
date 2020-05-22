package com.shoppament.ui.activity.registration;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.shoppament.R;
import com.shoppament.data.models.AddressLocationModel;
import com.shoppament.data.models.PictureModel;
import com.shoppament.data.models.ShopKeeperDataModel;
import com.shoppament.data.models.SlotTimingModel;
import com.shoppament.databinding.FragmentRegistrationBinding;
import com.shoppament.ui.adapters.ItemsRecyclerAdapter;
import com.shoppament.ui.adapters.PicturesRecyclerAdapter;
import com.shoppament.ui.adapters.SlotsTimingRecyclerAdapter;
import com.shoppament.ui.base.BaseFragment;
import com.shoppament.utils.TimeFormatManager;
import com.shoppament.utils.callbacks.IPictureListener;
import com.shoppament.utils.callbacks.OnObjectChangedListener;
import com.shoppament.utils.callbacks.OnTaskCompletedListener;
import com.shoppament.utils.view.LocationController;
import com.shoppament.utils.view.UploadFileController;
import com.shoppament.utils.view.ViewController;
import com.shoppament.utils.view.dialogs.LocationMapDialog;
import com.shoppament.utils.view.dialogs.OptionsListDialog;
import com.shoppament.utils.view.dialogs.PictureViewDialog;
import com.shoppament.utils.view.dialogs.UploadOptionsDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class RegistrationFragment extends BaseFragment implements View.OnClickListener,IPictureListener {
    private RegistrationViewModel registrationViewModel;
    private FragmentRegistrationBinding registrationBinding;

    private ItemsRecyclerAdapter shopTypesRecyclerAdapter;
    private PicturesRecyclerAdapter picturesRecyclerAdapter;
    private SlotsTimingRecyclerAdapter slotsTimingRecyclerAdapter;

    private Calendar averageTimeCalendar;
    private Calendar startingTimeCalendar;
    private Calendar endingTimeCalendar;

    private AddressLocationModel addressLocationModel;

    private TextWatcher totalCapacityWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            int totalCapacity = registrationViewModel.getTotalCapacity(
                    registrationBinding.insideCapacityEt.getText().toString(),
                    registrationBinding.outsideCapacityEt.getText().toString());

            registrationBinding.totalCapacityTxt.setText(String.valueOf(totalCapacity));

            registrationViewModel.getPerSlotTime(totalCapacity,
                    registrationBinding.averageTimeHhEt.getText().toString(),
                    registrationBinding.averageTimeMmEt.getText().toString())
                    .observe(RegistrationFragment.this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer perSlotTime) {
                    int [] perSlotTimeHmMm = TimeFormatManager.getInstance().getHhMmFromMinutes(perSlotTime.intValue());
                    StringBuilder perSlotTimeValue = new StringBuilder();
                    if(perSlotTimeHmMm[0] != 0){
                        perSlotTimeValue.append(perSlotTimeHmMm[0]);
                        perSlotTimeValue.append(getResources().getString(R.string.time_hours));
                        perSlotTimeValue.append("  ");
                    }
                    perSlotTimeValue.append(perSlotTimeHmMm[1]);
                    perSlotTimeValue.append(getResources().getString(R.string.time_minutes));

                    registrationBinding.perSlotTimeTxt.setText(perSlotTimeValue.toString());
                }
            });
        }
    };

    @Override
    protected void initViews() {
        registrationBinding = DataBindingUtil.setContentView(activity, R.layout.fragment_registration);
        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        registrationBinding.setViewModel(registrationViewModel);
        registrationBinding.setLifecycleOwner(this);

        ((RegistrationActivity)activity).setSupportActionBar(registrationBinding.toolbar);

        registrationBinding.shopTypesRecycler.setLayoutManager(new LinearLayoutManager(activity));
        shopTypesRecyclerAdapter = new ItemsRecyclerAdapter(registrationViewModel.getShopTypesList(),activity);
        registrationBinding.shopTypesRecycler.setAdapter(shopTypesRecyclerAdapter);

        registrationBinding.picturesRecycler.setLayoutManager(new LinearLayoutManager(activity));
        picturesRecyclerAdapter = new PicturesRecyclerAdapter(registrationViewModel.getPictureModels(),activity,this);
        registrationBinding.picturesRecycler.setAdapter(picturesRecyclerAdapter);

        registrationViewModel.getSlotTimingLiveData().observe(this, new Observer<List<SlotTimingModel>>() {
            @Override
            public void onChanged(List<SlotTimingModel> slotTimingModels) {
                if(slotsTimingRecyclerAdapter == null) {
                    registrationBinding.availableSlotTimingsRecycler.setLayoutManager(new LinearLayoutManager(activity));
                    slotsTimingRecyclerAdapter = new SlotsTimingRecyclerAdapter(slotTimingModels, activity);
                    registrationBinding.availableSlotTimingsRecycler.setAdapter(slotsTimingRecyclerAdapter);
                    slotsTimingRecyclerAdapter.setOnObjectChangedListener(new OnObjectChangedListener() {
                        @Override
                        public void onObjectChanged(int id, int position, Object object) {
                            if(id == 1){
                                registrationViewModel.deletePicture(position);
                            }
                        }
                    });
                }else{
                    slotsTimingRecyclerAdapter.setSlotTimingModels(slotTimingModels);
                }
            }
        });
    }

    @Override
    protected void doCreate() {
        registrationBinding.insideCapacityEt.addTextChangedListener(totalCapacityWatcher);
        registrationBinding.outsideCapacityEt.addTextChangedListener(totalCapacityWatcher);
        registrationBinding.averageTimeHhEt.addTextChangedListener(totalCapacityWatcher);
        registrationBinding.averageTimeMmEt.addTextChangedListener(totalCapacityWatcher);

        registrationBinding.uploadPicturesBtn.setOnClickListener(this);
        registrationBinding.selectLocationBtn.setOnClickListener(this);
        registrationBinding.averageTimeHhEt.setOnClickListener(this);
        registrationBinding.averageTimeMmEt.setOnClickListener(this);
        registrationBinding.resetSlotTimingsBtn.setOnClickListener(this);
        registrationBinding.shopTypeTxt.setOnClickListener(this);
        registrationBinding.startingTimeEt.setOnClickListener(this);
        registrationBinding.endingTimeEt.setOnClickListener(this);
        registrationBinding.submitBtn.setOnClickListener(this);
        registrationBinding.cityTxt.setOnClickListener(this);
        registrationBinding.countryTxt.setOnClickListener(this);
        registrationBinding.stateTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.upload_pictures_btn:
                clickToUploadPics();
                break;
            case R.id.average_time_hh_et:
            case R.id.average_time_mm_et:
                getTime();
                break;
            case R.id.reset_slot_timings_btn:
                restSlotTimings();
                break;
            case R.id.starting_time_et:
                getStartingTime();
                break;
            case R.id.ending_time_et:
                getEndingTime();
                break;
            case R.id.city_txt:
                showCitiesList(view);
                break;
            case R.id.country_txt:
                showCountriesList(view);
                break;
            case R.id.state_txt:
                showStatesList(view);
                break;
            case R.id.shop_type_txt:
                showShopTypeList();
                break;
            case R.id.submit_btn:
                submitTheRegistration();
                break;
            case R.id.select_location_btn:
                getLocation();
        }
    }

    void handleUploadPicture(int optionId, Intent data){
        PictureModel pictureModel = null;
        if(optionId == UploadFileController.CAPTURE_PHOTO_ID){
            pictureModel = UploadFileController.getInstance().getCameraPicture();
        }else if(optionId == UploadFileController.UPLOAD_PHOTO_ID && data != null && data.getData() != null){
            pictureModel = UploadFileController.getInstance().getDevicePicture(data,activity);
        }
        if(pictureModel != null){
            registrationViewModel.uploadNewPicture(pictureModel).observe(
                    this, new Observer<List<PictureModel>>() {
                        @Override
                        public void onChanged(List<PictureModel> pictureModels) {
                            if(pictureModels != null) {
                                picturesRecyclerAdapter.setPictureModels(pictureModels);
                            }
                        }
                    });
        }else{
            showUploadPicsError(getResources().getString(R.string.error_uploading_picture));
        }
    }

    private void clickToUploadPics() {
        if(registrationViewModel.isUploadNewPictureEnabled()) {
            new UploadOptionsDialog(activity, new OnTaskCompletedListener() {
                @Override
                public void onCompleted(Object result) {

                }

                @Override
                public void onError(int duration, String message) {
                    showUploadPicsError(message);
                }
            });
        }else{
            showUploadPicsError(getResources().getString(R.string.error_exceed_pictures_max));
        }
    }

    private void showUploadPicsError(String message) {
        showErrorToast(message);
    }

    private void showErrorToast(String message) {
        Toast.makeText(activity,message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSelectedPic(PictureModel pictureModel) {
        new PictureViewDialog(activity,pictureModel.getPath());
    }

    @Override
    public void deleteSelectedPic(final int position) {
        registrationViewModel.deletePicture(position).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isDeleted) {
                if(isDeleted){
                    picturesRecyclerAdapter.notifyItemRemoved(position);
                    picturesRecyclerAdapter.notifyItemRangeChanged(position,1);
                }
            }
        });
    }

    private void getLocation() {
        LocationController.getInstance().init(activity);
        if(!LocationController.getInstance().hasGPSDevice()){
            showLocationErrors(getResources().getString(R.string.error_gps_not_supported));
            return;
        }
        if (!LocationController.getInstance().isProviderEnabled()) {
            showLocationErrors(getResources().getString(R.string.error_gps_not_enabled));
            return;
        }
        addressLocationModel = new AddressLocationModel();
        addressLocationModel.setCountry(registrationBinding.countryTxt.getText().toString());
        addressLocationModel.setCity(registrationBinding.cityTxt.getText().toString());
        addressLocationModel.setState(registrationBinding.stateTxt.getText().toString());
        addressLocationModel.setPostalCode(registrationBinding.pinCodeEt.getText().toString());
        addressLocationModel.setAddressLine(registrationBinding.apartmentStreetNameEt.getText().toString());
        if(addressLocationModel.getLocation(activity)!=null){
            registrationBinding.selectLocationBtn.setText(getResources().getString(R.string.change_location_btn));
        }
        new LocationMapDialog(activity, Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                addressLocationModel,new OnTaskCompletedListener() {
                    @Override
                    public void onCompleted(Object result) {
                        addressLocationModel = (AddressLocationModel) result;
                        registrationBinding.apartmentStreetNameEt.setText(addressLocationModel.getAddressLine());
                        registrationBinding.countryTxt.setText(addressLocationModel.getCountry());
                        registrationBinding.cityTxt.setText(addressLocationModel.getCity());
                        registrationBinding.stateTxt.setText(addressLocationModel.getState());
                        registrationBinding.stateTxt.setText(addressLocationModel.getState());
                        registrationBinding.pinCodeEt.setText(addressLocationModel.getPostalCode());
                        registrationBinding.selectLocationBtn.setText(getResources().getString(R.string.change_location_btn));
                    }

                    @Override
                    public void onError(int duration, String message) {
                        showLocationErrors(message);
                    }
                });
    }

    private void showLocationErrors(String message) {
        showErrorToast(message);
    }

    private void getTime() {
        if(averageTimeCalendar == null)
            averageTimeCalendar = Calendar.getInstance();

        int hour = averageTimeCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = averageTimeCalendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                averageTimeCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                averageTimeCalendar.set(Calendar.MINUTE,selectedMinute);

                registrationBinding.averageTimeHhEt.setText(String.valueOf(selectedHour));
                registrationBinding.averageTimeMmEt.setText(String.valueOf(selectedMinute));
            }
        }, hour, minute, true);
        Objects.requireNonNull(timePickerDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    private void restSlotTimings() {
        if(startingTimeCalendar == null || endingTimeCalendar == null)
            return;

        registrationViewModel.setSlotsAndTimings(
                TimeFormatManager.getInstance().getMinutes(startingTimeCalendar),
                TimeFormatManager.getInstance().getMinutes(endingTimeCalendar));
//                .observe(this, new Observer<List<SlotTimingModel>>() {
//            @Override
//            public void onChanged(List<SlotTimingModel> slotTimingModels) {
//                if(slotTimingModels != null){
//                    slotsTimingRecyclerAdapter.setSlotTimingModels(slotTimingModels);
//                }else{
//                    showErrorToast(getResources().getString(R.string.error_slots_1));
//                }
//            }
//        });
    }

    private void showShopTypeList() {
        registrationViewModel.showShopTypeList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> list) {
                new OptionsListDialog(activity, getResources().getString(R.string.shop_type_et),
                        list, new OnTaskCompletedListener() {
                    @Override
                    public void onCompleted(Object result) {
                        String type = (String)result;
                        registrationBinding.shopTypeTxt.setText(type);
                        registrationViewModel.addShopType(type).observe(RegistrationFragment.this
                                , new Observer<List<String>>() {
                            @Override
                            public void onChanged(List<String> list) {
                                shopTypesRecyclerAdapter.setItems(list);
                            }
                        });
                    }

                    @Override
                    public void onError(int duration, String message) {

                    }
                });
            }
        });
    }

    private void showCitiesList(final View view) {
        registrationViewModel.getCities().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> list) {
                ViewController.getInstance().showOptionsPopupWindow(activity, list, new OnObjectChangedListener() {
                    @Override
                    public void onObjectChanged(int id, int position, Object object) {
                        registrationBinding.cityTxt.setText((String)object);
                    }
                }).showAsDropDown(view);
            }
        });
    }

    private void showCountriesList(final View view) {
        registrationViewModel.getCountries().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> list) {
                ViewController.getInstance().showOptionsPopupWindow(activity, list, new OnObjectChangedListener() {
                    @Override
                    public void onObjectChanged(int id, int position, Object object) {
                        registrationBinding.countryTxt.setText((String)object);
                    }
                }).showAsDropDown(view);
            }
        });
    }

    private void showStatesList(final View view) {
        registrationViewModel.getStates().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> list) {
                ViewController.getInstance().showOptionsPopupWindow(activity, list, new OnObjectChangedListener() {
                    @Override
                    public void onObjectChanged(int id, int position, Object object) {
                        registrationBinding.stateTxt.setText((String)object);
                    }
                }).showAsDropDown(view);
            }
        });
    }

    private void getStartingTime() {
        if(startingTimeCalendar == null)
            startingTimeCalendar = Calendar.getInstance();

        final int hour = startingTimeCalendar.get(Calendar.HOUR_OF_DAY);
        final int minute = startingTimeCalendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                startingTimeCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                startingTimeCalendar.set(Calendar.MINUTE,selectedMinute);

                if(!registrationBinding.endingTimeEt.getText().toString().isEmpty() &&
                        endingTimeCalendar.getTimeInMillis() <= startingTimeCalendar.getTimeInMillis()){
                    startingTimeCalendar.set(Calendar.HOUR_OF_DAY,hour);
                    startingTimeCalendar.set(Calendar.MINUTE,minute);
                    showErrorToast(getResources().getString(R.string.error_grater_starting_time));
                    return;
                }
                registrationBinding.startingTimeEt.setText(
                        TimeFormatManager.getInstance().format12Hours(startingTimeCalendar.getTime()));
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void getEndingTime() {
        if(endingTimeCalendar == null)
            endingTimeCalendar = Calendar.getInstance();

        final int hour = endingTimeCalendar.get(Calendar.HOUR_OF_DAY);
        final int minute = endingTimeCalendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                endingTimeCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                endingTimeCalendar.set(Calendar.MINUTE,selectedMinute);

                if(!registrationBinding.startingTimeEt.getText().toString().isEmpty() &&
                        startingTimeCalendar.getTimeInMillis() >= endingTimeCalendar.getTimeInMillis()){
                    endingTimeCalendar.set(Calendar.HOUR_OF_DAY,hour);
                    endingTimeCalendar.set(Calendar.MINUTE,minute);
                    showErrorToast(getResources().getString(R.string.error_less_ending_time));
                    return;
                }
                registrationBinding.endingTimeEt.setText(
                        TimeFormatManager.getInstance().format12Hours(endingTimeCalendar.getTime()));
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void submitTheRegistration() {
        ShopKeeperDataModel shopKeeperDataModel = new ShopKeeperDataModel();

        String shopName = registrationBinding.shopNameEt.getText().toString();
        if(shopName.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setShopName(shopName);

        String shopType = registrationBinding.shopTypeTxt.getText().toString();
        if(shopType.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setShopType(shopType);

        String shopDescription = registrationBinding.shopDescEt.getText().toString();
        if(shopDescription.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setShopDescription(shopDescription);

        List<PictureModel> pictureModels = registrationViewModel.getPictureModels();
        if(pictureModels.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_pictures));
            return;
        }
        shopKeeperDataModel.setPictureModels(pictureModels);

        String startingTime = registrationBinding.startingTimeEt.getText().toString();
        if(startingTime.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setStartingOperationalTime(startingTime);

        String endingTime = registrationBinding.endingTimeEt.getText().toString();
        if(endingTime.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setEndingOperationalTime(endingTime);

        String shopDoorNumber = registrationBinding.shopDoorNumberEt.getText().toString();
        if(shopDoorNumber.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setShopDoorNumber(shopDoorNumber);

        String apartmentStreetName = registrationBinding.apartmentStreetNameEt.getText().toString();
        if(apartmentStreetName.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setApartmentStreetName(apartmentStreetName);

        String pinCode = registrationBinding.pinCodeEt.getText().toString();
        if(pinCode.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setPinCode(pinCode);

        String country = registrationBinding.countryTxt.getText().toString();
        if(country.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setCountry(country);

        String state = registrationBinding.stateTxt.getText().toString();
        if(state.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setState(state);

        String city = registrationBinding.cityTxt.getText().toString();
        if(city.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setCity(city);

        String averageHHTime = registrationBinding.averageTimeHhEt.getText().toString();
        if(averageHHTime.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setAverageTime(averageHHTime + " : "
                + registrationBinding.averageTimeHhEt.getText().toString());

        String insideCapacity = registrationBinding.insideCapacityEt.getText().toString();
        if(insideCapacity.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setInsideCapacity(insideCapacity);

        String outsideCapacity = registrationBinding.outsideCapacityEt.getText().toString();
        if(outsideCapacity.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setOutsideCapacity(outsideCapacity);
        shopKeeperDataModel.setTotalCapacity(registrationBinding.totalCapacityTxt.getText().toString());
        shopKeeperDataModel.setPerSlotTime(registrationBinding.perSlotTimeTxt.getText().toString());

        //Location

        String phone = registrationBinding.phoneNumberEt.getText().toString();
        if(phone.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        if(!Patterns.PHONE.matcher(phone).matches()){
            showSubmitWarringError(getResources().getString(R.string.error_wrong_phone_number));
            return;
        }
        shopKeeperDataModel.setPhoneNumber("+91"+phone);

        String otp = registrationBinding.otpEt.getText().toString();
        if(otp.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setOTP(otp);

        String email = registrationBinding.emailIdEt.getText().toString();
        if(!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showSubmitWarringError(getResources().getString(R.string.error_wrong_email));
            return;
        }
        shopKeeperDataModel.setEmailID(email);
        shopKeeperDataModel.setGSTNumber(registrationBinding.gstNumberEt.getText().toString());

        String shopKeeperJson = new Gson().toJson(shopKeeperDataModel);
    }

    private void showSubmitWarringError(String error){
        registrationBinding.warningVerifiedMessage2Txt.setText(error);
        registrationBinding.warningVerifiedMessage2Txt.setVisibility(View.VISIBLE);
    }
}
