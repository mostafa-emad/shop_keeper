package com.shoppament.data.remote.retrofit;

import okhttp3.ResponseBody;

public interface ResponseBodyListener {
    void onSuccess(String operation, ResponseBody result);
    void onFailure(String operation, String errorMessage);
}
