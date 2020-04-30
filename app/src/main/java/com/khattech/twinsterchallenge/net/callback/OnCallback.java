package com.khattech.twinsterchallenge.net.callback;


public interface OnCallback {
    void onSuccess(Object response);
    void onError(int errorCode);
}
