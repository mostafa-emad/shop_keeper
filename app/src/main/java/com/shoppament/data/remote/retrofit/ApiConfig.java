package com.shoppament.data.remote.retrofit;

public class ApiConfig {
    private static final String BASE_URL_PRODUCTION = "https://live.com/ws/";
    private static final String BASE_URL_TEST = "https://test.com/ws/";

    public static final String BASE_URL = BASE_URL_PRODUCTION;
    public static final String AUTH_KEY = "WJzqxb3h4mXSi5uCCGSUrzB1Kc3lq5ec==";

    public interface ResponseIDs {
        String SESSION_TIMEOUT_ID = "6";
    }

    public interface Services {
        String REGISTER_REQUEST = "register.php";
    }

    public interface ApiQueryKeys {
        String QUERY_AUTH_KEY ="AuthKey";
    }

}
