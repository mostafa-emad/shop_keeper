package com.shoppament.utils.callbacks;

public interface OnTaskCompletedListener {
    int DEFAULT_MESSAGE_DURATION = 10 * 1000;

    void onCompleted(Object result);
    void onError(int duration, String message);
}
