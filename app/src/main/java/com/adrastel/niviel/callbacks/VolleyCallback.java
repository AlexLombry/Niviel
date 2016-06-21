package com.adrastel.niviel.callbacks;


public interface VolleyCallback {
    void onSuccess(String response);
    void onError(int errorCode, String body);
}
