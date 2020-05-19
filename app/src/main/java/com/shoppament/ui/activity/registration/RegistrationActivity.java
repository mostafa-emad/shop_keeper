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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.shoppament.R;
import com.shoppament.data.models.PictureModel;
import com.shoppament.data.models.ShopKeeperDataModel;
import com.shoppament.data.models.SlotTimingModel;
import com.shoppament.databinding.ActivityRegistrationBinding;
import com.shoppament.ui.adapters.PicturesRecyclerAdapter;
import com.shoppament.ui.adapters.SlotsTimingRecyclerAdapter;
import com.shoppament.ui.base.BaseActivity;
import com.shoppament.utils.TimeFormatManager;
import com.shoppament.utils.callbacks.IPictureListener;
import com.shoppament.utils.callbacks.OnObjectChangedListener;
import com.shoppament.utils.view.UploadFileController;
import com.shoppament.utils.view.ViewController;
import com.shoppament.utils.view.dialogs.LocationMapDialog;
import com.shoppament.utils.view.dialogs.PictureViewDialog;
import com.shoppament.utils.view.dialogs.UploadOptionsDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class RegistrationActivity extends BaseActivity implements IPictureListener {
    private RegistrationViewModel registrationViewModel;
    private ActivityRegistrationBinding activityRegistrationBinding;

    private PicturesRecyclerAdapter picturesRecyclerAdapter;
    private SlotsTimingRecyclerAdapter slotsTimingRecyclerAdapter;

    private Calendar startingTimeCalendar;
    private Calendar endingTimeCalendar;

    TextWatcher totalCapacityWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            int totalCapacity = registrationViewModel.getTotalCapacity(
                    activityRegistrationBinding.insideCapacityEt.getText().toString(),
                    activityRegistrationBinding.outsideCapacityEt.getText().toString());

            activityRegistrationBinding.totalCapacityTxt.setText(String.valueOf(totalCapacity));

            registrationViewModel.getPerSlotTime(totalCapacity,
                    activityRegistrationBinding.averageTimeHhEt.getText().toString(),
                    activityRegistrationBinding.averageTimeMmEt.getText().toString())
                    .observe(RegistrationActivity.this, new Observer<Integer>() {
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

                    activityRegistrationBinding.perSlotTimeTxt.setText(perSlotTimeValue.toString());
                }
            });
        }
    };

    @Override
    protected void initViews() {
        activityRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityRegistrationBinding.setViewModel(registrationViewModel);
        activityRegistrationBinding.setLifecycleOwner(this);

        activityRegistrationBinding.picturesRecycler.setLayoutManager(new LinearLayoutManager(this));
        picturesRecyclerAdapter = new PicturesRecyclerAdapter(registrationViewModel.getPictureModels(),this,this);
        activityRegistrationBinding.picturesRecycler.setAdapter(picturesRecyclerAdapter);

        activityRegistrationBinding.availableSlotTimingsRecycler.setLayoutManager(new LinearLayoutManager(this));
        slotsTimingRecyclerAdapter = new SlotsTimingRecyclerAdapter(registrationViewModel.getSlotTimingModels(),this);
        activityRegistrationBinding.availableSlotTimingsRecycler.setAdapter(slotsTimingRecyclerAdapter);
    }

    @Override
    protected void doCreate() {
        activityRegistrationBinding.insideCapacityEt.addTextChangedListener(totalCapacityWatcher);
        activityRegistrationBinding.outsideCapacityEt.addTextChangedListener(totalCapacityWatcher);
        activityRegistrationBinding.averageTimeHhEt.addTextChangedListener(totalCapacityWatcher);
        activityRegistrationBinding.averageTimeMmEt.addTextChangedListener(totalCapacityWatcher);
    }

    public void clickToUploadPics(View view) {
        if(registrationViewModel.isUploadNewPictureEnabled()) {
            new UploadOptionsDialog(RegistrationActivity.this);
        }else{
            showUploadPicsError();
        }
    }

    public void showUploadPicsError() {
        Toast.makeText(activity,getResources().getString(R.string.msg_upload_pictures_msg_error),
                Toast.LENGTH_LONG).show();
//                    ViewController.getInstance().showDialog(activity,getResources().getString(R.string.msg_upload_pictures_msg_error));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UploadFileController.CAPTURE_PHOTO_ID){
            registrationViewModel.uploadNewPicture(UploadFileController.getInstance().getCameraPicture()).observe(
                    RegistrationActivity.this, new Observer<List<PictureModel>>() {
                        @Override
                        public void onChanged(List<PictureModel> pictureModels) {
                            if(pictureModels != null) {
                                picturesRecyclerAdapter.setPictureModels(pictureModels);
                            }
                        }
                    });
        }else if(requestCode == UploadFileController.UPLOAD_PHOTO_ID && data != null && data.getData() != null){
            registrationViewModel.uploadNewPicture(UploadFileController.getInstance().getDevicePicture(data,activity)).observe(
                    RegistrationActivity.this, new Observer<List<PictureModel>>() {
                        @Override
                        public void onChanged(List<PictureModel> pictureModels) {
                            if(pictureModels != null) {
                                picturesRecyclerAdapter.setPictureModels(pictureModels);
                            }
                        }
                    });
        }
    }

    public void getLocation(View view) {
        new LocationMapDialog(RegistrationActivity.this,getSupportFragmentManager(), new OnObjectChangedListener() {
            @Override
            public void onObjectChanged(int id, int position, Object object) {
                activityRegistrationBinding.selectLocationBtn.setText(getResources().getString(R.string.change_location_btn));
            }
        });
    }

    private void showLocationErrors() {

    }

    public void getTime(View view) {
//        android.R.style.Theme_Holo_Light_Dialog
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                activityRegistrationBinding.averageTimeHhEt.setText(String.valueOf(selectedHour));
                activityRegistrationBinding.averageTimeMmEt.setText(String.valueOf(selectedMinute));
            }
        }, hour, minute, true);
        Objects.requireNonNull(timePickerDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    public void restSlotTimings(View view) {
        if(startingTimeCalendar == null || endingTimeCalendar == null)
            return;

        registrationViewModel.setSlotsAndTimings(
                TimeFormatManager.getInstance().getMinutes(startingTimeCalendar),
                TimeFormatManager.getInstance().getMinutes(endingTimeCalendar))
                .observe(this, new Observer<List<SlotTimingModel>>() {
            @Override
            public void onChanged(List<SlotTimingModel> slotTimingModels) {
                if(slotTimingModels != null){
                    slotsTimingRecyclerAdapter.setSlotTimingModels(slotTimingModels);
                }
            }
        });
    }

    public void showShopTypeList(final View view) {
        registrationViewModel.showShopTypeList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> list) {
                ViewController.getInstance().showOptionsPopupWindow(activity, list, new OnObjectChangedListener() {
                    @Override
                    public void onObjectChanged(int id, int position, Object object) {
                        activityRegistrationBinding.shopTypeTxt.setText((String)object);
                    }
                }).showAsDropDown(view);
            }
        });
    }

    public void getStartingTime(View view) {
        if(startingTimeCalendar == null)
            startingTimeCalendar = Calendar.getInstance();

        final int hour = startingTimeCalendar.get(Calendar.HOUR_OF_DAY);
        final int minute = startingTimeCalendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                startingTimeCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                startingTimeCalendar.set(Calendar.MINUTE,selectedMinute);
                activityRegistrationBinding.startingTimeEt.setText(
                        TimeFormatManager.getInstance().format12Hours(startingTimeCalendar.getTime()));
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    public void getEndingTime(View view) {
        if(endingTimeCalendar == null)
            endingTimeCalendar = Calendar.getInstance();

        final int hour = endingTimeCalendar.get(Calendar.HOUR_OF_DAY);
        final int minute = endingTimeCalendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                endingTimeCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                endingTimeCalendar.set(Calendar.MINUTE,selectedMinute);
                activityRegistrationBinding.endingTimeEt.setText(
                        TimeFormatManager.getInstance().format12Hours(endingTimeCalendar.getTime()));
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    public void submitTheRegistration(View view) {
        ShopKeeperDataModel shopKeeperDataModel = new ShopKeeperDataModel();

        String shopName = activityRegistrationBinding.shopNameEt.getText().toString();
        if(shopName.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setShopName(shopName);

        String shopType = activityRegistrationBinding.shopTypeTxt.getText().toString();
        if(shopType.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setShopType(shopType);

        String shopDescription = activityRegistrationBinding.shopDescEt.getText().toString();
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

        String startingTime = activityRegistrationBinding.startingTimeEt.getText().toString();
        if(startingTime.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setStartingOperationalTime(startingTime);

        String endingTime = activityRegistrationBinding.endingTimeEt.getText().toString();
        if(endingTime.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setEndingOperationalTime(endingTime);

        String shopDoorNumber = activityRegistrationBinding.shopDoorNumberEt.getText().toString();
        if(shopDoorNumber.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setShopDoorNumber(shopDoorNumber);

        String apartmentStreetName = activityRegistrationBinding.apartmentStreetNameEt.getText().toString();
        if(apartmentStreetName.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setApartmentStreetName(apartmentStreetName);

        String pinCode = activityRegistrationBinding.pinCodeEt.getText().toString();
        if(pinCode.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setPinCode(pinCode);

        String country = activityRegistrationBinding.countryTxt.getText().toString();
        if(country.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setCountry(country);

        String state = activityRegistrationBinding.stateTxt.getText().toString();
        if(state.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setState(state);

        String city = activityRegistrationBinding.cityTxt.getText().toString();
        if(city.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setCity(city);

        String averageHHTime = activityRegistrationBinding.averageTimeHhEt.getText().toString();
        if(averageHHTime.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setAverageTime(averageHHTime + " : "
                + activityRegistrationBinding.averageTimeHhEt.getText().toString());

        String insideCapacity = activityRegistrationBinding.insideCapacityEt.getText().toString();
        if(insideCapacity.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setInsideCapacity(insideCapacity);

        String outsideCapacity = activityRegistrationBinding.outsideCapacityEt.getText().toString();
        if(outsideCapacity.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setOutsideCapacity(outsideCapacity);
        shopKeeperDataModel.setTotalCapacity(activityRegistrationBinding.totalCapacityTxt.getText().toString());
        shopKeeperDataModel.setPerSlotTime(activityRegistrationBinding.perSlotTimeTxt.getText().toString());

//        String totalCapacity = activityRegistrationBinding.totalCapacityTxt.getText().toString();
//        if(totalCapacity.isEmpty()){
//            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
//            return;
//        }
//        shopKeeperDataModel.setTotalCapacity(totalCapacity);
//
//        String preSlotTime = activityRegistrationBinding.perSlotTimeTxt.getText().toString();
//        if(preSlotTime.isEmpty()){
//            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
//            return;
//        }
//        shopKeeperDataModel.setPerSlotTime(preSlotTime);

        //Location

        String phone = activityRegistrationBinding.phoneNumberEt.getText().toString();
        if(phone.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        if(!Patterns.PHONE.matcher(phone).matches()){
            showSubmitWarringError(getResources().getString(R.string.error_wrong_phone_number));
            return;
        }
        shopKeeperDataModel.setPhoneNumber("+91"+phone);

        String otp = activityRegistrationBinding.otpEt.getText().toString();
        if(otp.isEmpty()){
            showSubmitWarringError(getResources().getString(R.string.error_empty_fields));
            return;
        }
        shopKeeperDataModel.setOTP(otp);

        String email = activityRegistrationBinding.emailIdEt.getText().toString();
        if(!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showSubmitWarringError(getResources().getString(R.string.error_wrong_email));
            return;
        }
        shopKeeperDataModel.setEmailID(email);
        shopKeeperDataModel.setGSTNumber(activityRegistrationBinding.gstNumberEt.getText().toString());

        String shopKeeperJson = new Gson().toJson(shopKeeperDataModel);
    }

    private void showSubmitWarringError(String error){
        activityRegistrationBinding.warningVerifiedMessage2Txt.setText(error);
        activityRegistrationBinding.warningVerifiedMessage2Txt.setVisibility(View.VISIBLE);
    }
}
