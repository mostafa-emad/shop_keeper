package com.shoppament.data.remote.retrofit;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shoppament.R;
import com.shoppament.data.remote.model.response.BaseResponse;
import com.shoppament.utils.view.ViewController;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ApiClient apiClient;
    private static Retrofit retrofit = null;
    private static Retrofit lenientRetrofit = null;
    private Dialog mProgressDialog;
    private static String baseUrl= ApiConfig.BASE_URL;
    private HashMap<String, String> params;
    private HashMap<String, String> appDetailParams;

    public static ApiClient getInstance() {
        if (apiClient == null) {
            synchronized (ApiClient.class) {
                ApiClient manager = apiClient;
                if (manager == null) {
                    synchronized (ApiClient.class) {
                        apiClient = new ApiClient();
                    }
                }
            }
        }
        return apiClient;
    }

    public void showConnErrorDialog(Context context, String msg) {
        hideDialog();
        ViewController.getInstance().showDialog(context,msg);
    }

    private Retrofit getClient() {
        OkHttpClient.Builder builder = getUnsafeOkHttpClient();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = null;
                newRequest = chain.request().newBuilder()
//                        .addHeader(" Content-Type", "application/json")
                        .build();
                return chain.proceed(newRequest);
            }
        });

        OkHttpClient client=builder.build();
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    private Retrofit getLenientClient() {
        OkHttpClient.Builder builder = getUnsafeOkHttpClient();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
//                        .addHeader("cache-control", "no-cache")
//                        .addHeader("postman-token", "643a3101-bd93-7e7b-97cc-5210add654c0")
                        .build();
                return chain.proceed(newRequest);
            }
        });
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient client=builder.build();
        if (lenientRetrofit==null) {
//            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
//                    .client(client)
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create(gson));
//            lenientRetrofit = CFMobile.createRetrofit(retrofitBuilder);

            lenientRetrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return lenientRetrofit;
    }

    private ApiServices getApiService1(Context context) {
        if(isNetworkAvailable(context)){
            return getInstance().getClient().create(ApiServices.class);
        }else{
            showConnErrorDialog(context,context.getResources().getString(R.string.msg_network_error));
        }
        return null;
    }

    private ApiServices getApiLenientService(Context context) {
        if(isNetworkAvailable(context)){
            return getInstance().getLenientClient().create(ApiServices.class);
        }else{
            showConnErrorDialog(context,"No Internet network");
        }
        return null;
    }

    private OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.writeTimeout(60,TimeUnit.SECONDS);
            builder.readTimeout(100, TimeUnit.SECONDS);
            builder.connectTimeout(100, TimeUnit.SECONDS);
//            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

//            OkHttpClient okHttpClient = builder.build();
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void showDialog(Context context) {
        try {
            if(mProgressDialog==null){
                mProgressDialog=new Dialog(context);
                mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mProgressDialog.getWindow().setDimAmount(0f);
                mProgressDialog.setContentView(R.layout.layout_loading);
//            ((ProgressBar)mProgressDialog.findViewById(R.id.progressBar)).setProgress(10);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
            }

            if(mProgressDialog != null && !mProgressDialog.isShowing())
                mProgressDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleFailure(Context context, @NonNull Throwable t) {
        if(t instanceof SocketTimeoutException){
            ViewController.getInstance().showDialog(context,"Socket Time out. Please try again.");
        }
        t.printStackTrace();
        hideDialog();
    }

    public void register(final Context context, final ApiResponseListener apiResponseListener) {
        showDialog(context);
        ApiServices apiService = getApiService1(context);
        if(apiService==null)
            return;
        Call<BaseResponse> call=apiService.register();
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse>call, @NonNull retrofit2.Response<BaseResponse> response) {
                response(context,response.body(), ApiConfig.Services.REGISTER_REQUEST,apiResponseListener,response.message());
                hideDialog();
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse>call, @NonNull Throwable t) {
                handleFailure(context,t);
            }
        });
    }


    private void response(Context context , BaseResponse response, String serviceName,
                          ApiResponseListener apiResponseListener, String errorMsg) {
        if(response!=null){
            if(!response.getResponseID().equals(ApiConfig.ResponseIDs.SESSION_TIMEOUT_ID)) {
                if(response.isResponseStatus()) {
                    apiResponseListener.onSuccess(serviceName, response, null);
                }else{
                    apiResponseListener.onFailure(serviceName,response.getResponseMessage());
                }
            }else{
                //show message or do action
            }
        }else{
            apiResponseListener.onFailure(serviceName,errorMsg);
        }
    }

    public void downloadImage(final Context context, String url, final ResponseBodyListener responseBodyListener) {
        ApiServices apiService = getApiService1(context);
        if(apiService==null)
            return;
        Call<ResponseBody> call=apiService.downloadImage(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody>call, @NonNull retrofit2.Response<ResponseBody> response) {
                if(response.body()!=null){
                    responseBodyListener.onSuccess("",response.body());
                }else{
                    responseBodyListener.onFailure("",response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody>call, @NonNull Throwable t) {
                handleFailure(context,t);
            }
        });
    }

    public void hideDialog() {
        try{
            if(mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mProgressDialog=null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
            NetworkInfo activeNetworkInfo=connectivity.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
}
