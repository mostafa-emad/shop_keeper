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

            //        ApiClient.getInstance().register(context, new ApiResponseListener() {
//            @Override
//            public void onSuccess(String operation, BaseResponse result, BaseRequest request) {
//                dataMutableLiveData.setValue(context.getResources().getString(R.string.large_text));
//            }
//
//            @Override
//            public void onFailure(String operation, String errorMessage) {
//                dataMutableLiveData.setValue(context.getResources().getString(R.string.msg_error));
//            }
//        });
        }else{
            dataMutableLiveData.setValue(dataEntity.getContent());
        }

        return dataMutableLiveData;
    }

    public ArrayList<String> getShopTypes() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Type 1");
        data.add("Type 2");
        data.add("Type 3");
        data.add("Type 4");
        data.add("Type 5");

        return data;
    }
}
