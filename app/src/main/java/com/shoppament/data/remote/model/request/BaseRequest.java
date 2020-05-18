package com.shoppament.data.remote.model.request;

import java.io.Serializable;

public class BaseRequest implements Serializable {
    private String LoginKey;
    private String UserID;
    private String RestaurantID;
    private String AuthKey;

    public String getLoginKey() {
        return LoginKey;
    }

    public void setLoginKey(String loginKey) {
        LoginKey = loginKey;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getRestaurantID() {
        return RestaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        RestaurantID = restaurantID;
    }

    public String getAuthKey() {
        return AuthKey;
    }

    public void setAuthKey(String authKey) {
        AuthKey = authKey;
    }
}
