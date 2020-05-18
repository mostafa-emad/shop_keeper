package com.shoppament.data.remote.retrofit;

import com.shoppament.data.remote.model.response.BaseResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

;

public interface ApiServices {

    @POST(ApiConfig.Services.REGISTER_REQUEST)
    @Headers("Content-Type: application/json")
    Call<BaseResponse> register();

    @GET
    Call<ResponseBody> downloadImage(@Url String url);

}
