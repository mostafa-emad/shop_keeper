package com.shoppament.data.repo;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.shoppament.R;
import com.shoppament.data.room.database.AppDatabase;
import com.shoppament.data.room.entity.DataEntity;

import java.util.ArrayList;

public class RegistrationRepository {
    private Context context;
    private AppDatabase appDatabase;

    public RegistrationRepository(Context context) {
        this.context = context;
        appDatabase = AppDatabase.getAppDatabase(context);
    }

    public MutableLiveData<String> fetchData(){
        final MutableLiveData<String> dataMutableLiveData = new MutableLiveData<>();

        //Get data from db
        DataEntity dataEntity = appDatabase.userDao().getData();
        if(dataEntity == null || dataEntity.getContent() ==null || dataEntity.getContent().isEmpty()){
            //get data from remote db
            String apiData = context.getResources().getString(R.string.large_text);
            dataMutableLiveData.setValue(apiData);

            dataEntity = new DataEntity();
            dataEntity.setId("123");
            dataEntity.setContent(apiData);
            appDatabase.userDao().inset(dataEntity);
        }else{
            dataMutableLiveData.setValue(dataEntity.getContent());
        }

        return dataMutableLiveData;
    }

    public ArrayList<String> getShopTypes() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Vegetable and Fruits");
        data.add("Grocery/Provisions");
        data.add("Supermarket");
        data.add("Restaurant");
        data.add("Café");

        return data;
    }

    public ArrayList<String> getCities() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Coimbatore");

        return data;
    }

    public ArrayList<String> getCountries() {
        ArrayList<String> data = new ArrayList<>();
        data.add("India");

        return data;
    }

    public ArrayList<String> getStates() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Tamil Nadu");

        return data;
    }
}
