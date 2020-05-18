package com.shoppament.data.remote.retrofit;

import com.shoppament.data.remote.model.request.BaseRequest;
import com.shoppament.data.remote.model.response.BaseResponse;

public interface ApiResponseListener {
    void onSuccess(String operation, BaseResponse result, BaseRequest request);
    void onFailure(String operation, String errorMessage);
}
