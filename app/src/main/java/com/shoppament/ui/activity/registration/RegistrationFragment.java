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

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.shoppament.R;
import com.shoppament.data.models.AddressLocationModel;
import com.shoppament.data.models.PictureModel;
import com.shoppament.data.models.ShopKeeperDataModel;
import com.shoppament.data.models.SlotTimingModel;
import com.shoppament.data.remote.model.response.BaseResponse;
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

    private StringBuilder submitWarringErrors;

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
            //Calculate total capacity
            int totalCapacity = registrationViewModel.getTotalCapacity(
                    registrationBinding.insideCapacityEt.getText().toString(),
                    registrationBinding.outsideCapacityEt.getText().toString());

            //Update total capacity view
            registrationBinding.totalCapacityTxt.setText(String.valueOf(totalCapacity));

            //Calculate per slot timing and update view
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
                                registrationViewModel.deleteSlot(position);
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
        //Add Text Watcher to update total capacity and per slots timing with.
        registrationBinding.insideCapacityEt.addTextChangedListener(totalCapacityWatcher);
        registrationBinding.outsideCapacityEt.addTextChangedListener(totalCapacityWatcher);
        registrationBinding.averageTimeHhEt.addTextChangedListener(totalCapacityWatcher);
        registrationBinding.averageTimeMmEt.addTextChangedListener(totalCapacityWatcher);

        //Set OnClick to actions
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

    /**
     * this method to handle the data result of uploading pictures with two options
     * Camera and local files.
     * @param optionId
     * @param data
     */
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

    void clickToUploadPics() {
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
        updateAddressLocation();
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

    private void updateAddressLocation() {
        if(addressLocationModel == null) {
            addressLocationModel = new AddressLocationModel();
        }
        addressLocationModel.setCountry(registrationBinding.countryTxt.getText().toString());
        addressLocationModel.setCity(registrationBinding.cityTxt.getText().toString());
        addressLocationModel.setState(registrationBinding.stateTxt.getText().toString());
        addressLocationModel.setPostalCode(registrationBinding.pinCodeEt.getText().toString());
        addressLocationModel.setAddressLine(registrationBinding.apartmentStreetNameEt.getText().toString());
    }

    private void showLocationErrors(String message) {
        showErrorToast(message);
    }

    /**
     * show time picker dialog for average time by hh and mm format
     *
     */
    private void getTime() {
        int hour = 0;
        int minute = 0;
        if(averageTimeCalendar == null) {
            averageTimeCalendar = Calendar.getInstance();
        }else{
            hour = averageTimeCalendar.get(Calendar.HOUR_OF_DAY);
            minute = averageTimeCalendar.get(Calendar.MINUTE);
        }

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

    /**
     * reset slots timing when click the reset button
     *
     */
    private void restSlotTimings() {
        if(startingTimeCalendar == null) {
            showErrorToast(getResources().getString(R.string.error_empty_starting_time));
            return;
        }
        if(endingTimeCalendar == null) {
            showErrorToast(getResources().getString(R.string.error_empty_ending_time));
            return;
        }
        if(averageTimeCalendar == null) {
            showErrorToast(getResources().getString(R.string.error_empty_average_time));
            return;
        }
        if(registrationBinding.insideCapacityEt.getText().toString().isEmpty()) {
            showErrorToast(getResources().getString(R.string.error_empty_inside_capacity));
            return;
        }
        if(registrationBinding.outsideCapacityEt.getText().toString().isEmpty()) {
            showErrorToast(getResources().getString(R.string.error_empty_outside_capacity));
            return;
        }
        if(registrationViewModel.getPerSlotTimeValue()==0) {
            return;
        }

        registrationViewModel.setSlotsAndTimings(
                TimeFormatManager.getInstance().getMinutes(startingTimeCalendar),
                TimeFormatManager.getInstance().getMinutes(endingTimeCalendar));
    }

    /**
     * show shop list dialog
     *
     */
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

    /**
     * show cities list popup below city text view
     *
     * @param view
     */
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

    /**
     * show countries list popup below country text view
     *
     * @param view
     */
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

    /**
     * show sates list popup below state text view
     *
     * @param view
     */
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

    /**
     * show time picker dialog to select starting time
     *
     * this time should be less than ending time
     */
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

    /**
     * show time picker dialog to select ending time
     *
     * this time should be grater than starting time
     */
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

    /**
     * check validation of every mandatory field to create shop
     *
     * show error warring message for any missing data or wrong
     *
     * you can add custom message for any error separate just create string error msg
     * then place it in validation check.
     *
     * finally generate json object for shop keeper details
     *
     */
    private void submitTheRegistration() {
        ShopKeeperDataModel shopKeeperDataModel = new ShopKeeperDataModel();

        String shopName = registrationBinding.shopNameEt.getText().toString();
        resetSubmitWarringErrors();

        if(shopName.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setShopName(shopName);

        if(registrationViewModel.getShopTypesList().isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setShopTypeList(registrationViewModel.getShopTypesList());

        String shopDescription = registrationBinding.shopDescEt.getText().toString();
        if(shopDescription.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setShopDescription(shopDescription);

        List<PictureModel> pictureModels = registrationViewModel.getPictureModels();
        if(pictureModels.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_pictures));
        }
        shopKeeperDataModel.setPictureModels(pictureModels);

        String startingTime = registrationBinding.startingTimeEt.getText().toString();
        if(startingTime.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setStartingOperationalTime(startingTime);

        String endingTime = registrationBinding.endingTimeEt.getText().toString();
        if(endingTime.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setEndingOperationalTime(endingTime);

        String shopDoorNumber = registrationBinding.shopDoorNumberEt.getText().toString();
        if(shopDoorNumber.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setShopDoorNumber(shopDoorNumber);

        String apartmentStreetName = registrationBinding.apartmentStreetNameEt.getText().toString();
        if(apartmentStreetName.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setApartmentStreetName(apartmentStreetName);

        String pinCode = registrationBinding.pinCodeEt.getText().toString();
        if(pinCode.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setPinCode(pinCode);

        String country = registrationBinding.countryTxt.getText().toString();
        if(country.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setCountry(country);

        String state = registrationBinding.stateTxt.getText().toString();
        if(state.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setState(state);

        String city = registrationBinding.cityTxt.getText().toString();
        if(city.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setCity(city);
        //Location
        updateAddressLocation();
        LatLng latLng = addressLocationModel.getLocation(activity);
        if(latLng != null){
            shopKeeperDataModel.setLatitude(latLng.latitude);
            shopKeeperDataModel.setLongitude(latLng.longitude);
        }else{
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_location));
        }
        String averageHHTime = registrationBinding.averageTimeHhEt.getText().toString();
        if(averageHHTime.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setAverageTime(averageHHTime + " : "
                + registrationBinding.averageTimeMmEt.getText().toString());

        String insideCapacity = registrationBinding.insideCapacityEt.getText().toString();
        if(insideCapacity.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setInsideCapacity(insideCapacity);

        String outsideCapacity = registrationBinding.outsideCapacityEt.getText().toString();
        if(outsideCapacity.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setOutsideCapacity(outsideCapacity);
        shopKeeperDataModel.setTotalCapacity(registrationBinding.totalCapacityTxt.getText().toString());
        shopKeeperDataModel.setPerSlotTime(registrationBinding.perSlotTimeTxt.getText().toString());

        String phone = registrationBinding.phoneNumberEt.getText().toString();
        if(phone.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        if(!Patterns.PHONE.matcher(phone).matches()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_wrong_phone_number));
        }
        shopKeeperDataModel.setPhoneNumber("+91"+phone);

        String otp = registrationBinding.otpEt.getText().toString();
        if(otp.isEmpty()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_empty_fields));
        }
        shopKeeperDataModel.setOTP(otp);

        String email = registrationBinding.emailIdEt.getText().toString();
        if(!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            updateSubmitWarringErrors(getResources().getString(R.string.error_wrong_email));
        }
        shopKeeperDataModel.setEmailID(email);
        shopKeeperDataModel.setGSTNumber(registrationBinding.gstNumberEt.getText().toString());

        if(submitWarringErrors != null && !submitWarringErrors.toString().isEmpty()) {
            showSubmitWarringError();
            return;
        }
        String shopKeeperJson = new Gson().toJson(shopKeeperDataModel);

        registrationBinding.warningVerifiedMessage2Txt.setText(shopKeeperJson);
        registrationBinding.warningVerifiedMessage2Txt.setTextColor(getResources().getColor(R.color.colorBlack));
        registrationBinding.warningVerifiedMessage2Txt.setVisibility(View.VISIBLE);

        registrationViewModel.submitTheRegistration(shopKeeperJson).observe(this, new Observer<BaseResponse>() {
            @Override
            public void onChanged(BaseResponse baseResponse) {

            }
        });
    }

    private void resetSubmitWarringErrors(){
        submitWarringErrors = new StringBuilder();
    }

    private void updateSubmitWarringErrors(String error){
        submitWarringErrors.append(error);
        submitWarringErrors.append("\n");
    }

    private void showSubmitWarringError(){
        registrationBinding.warningVerifiedMessage2Txt.setText(submitWarringErrors.toString());
        registrationBinding.warningVerifiedMessage2Txt.setVisibility(View.VISIBLE);
    }
}
