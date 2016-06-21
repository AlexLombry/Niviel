package com.adrastel.niviel.callbacks;

import com.adrastel.niviel.WCA.Profile;


public interface ProfileCallback {
    void onSuccess(Profile response);
    void onError(int errorCode, String body);
}
