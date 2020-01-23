/*
 AdMobAdsListener.java
 Copyright 2015 AppFeel. All rights reserved.
 http://www.appfeel.com
 
 AdMobAds Cordova Plugin (cordova-admob)
 
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to
 deal in the Software without restriction, including without limitation the
 rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 sell copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package com.appfeel.ionic.plugin.capacitor.admob;

import android.annotation.SuppressLint;
import android.util.Log;
import android.app.Activity;

import com.getcapacitor.JSObject;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

@SuppressLint("DefaultLocale")
public class AdMobAdsAdListener extends AdListener {
    private String adType = "";
    private AdMobAds admobAds;
    private boolean isBackFill = false;

    public AdMobAdsAdListener(String adType, AdMobAds admobAds, boolean isBackFill) {
        this.adType = adType;
        this.admobAds = admobAds;
        this.isBackFill = isBackFill;
    }

    @Override
    public void onAdLoaded() {
        admobAds.handleOnAdLoaded(adType);
        Activity admobAdsActivity = (Activity) admobAds.getBridge().getActivity();
        admobAdsActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(AdMobAds.ADMOBADS_LOGTAG, adType + ": ad loaded");
                JSObject data = new JSObject();
                data.put("adType", adType);
                admobAds.notifyListeners("onAdLoaded", data);
            }
        });
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        if (this.isBackFill) {
            final int code = errorCode;
            Activity admobAdsActivity = (Activity) admobAds.getBridge().getActivity();
            admobAdsActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String reason = getErrorReason(code);
                    Log.d(AdMobAds.ADMOBADS_LOGTAG, adType + ": failed to load ad (" + reason + ")");
                    JSObject data = new JSObject();
                    data.put("adType", adType);
                    data.put("error", code);
                    data.put("reason", reason);
                    admobAds.notifyListeners("onAdFailedToLoad", data);
                }
            });
        } else {
            admobAds.tryBackfill(adType);
        }
    }

    /**
     * Gets a string error reason from an error code.
     */
    public String getErrorReason(int errorCode) {
        String errorReason = "Unknown";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }

    @Override
    public void onAdOpened() {
        admobAds.handleOnAdOpened(adType);
        Activity admobAdsActivity = (Activity) admobAds.getBridge().getActivity();
        admobAdsActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(AdMobAds.ADMOBADS_LOGTAG, adType + ": ad opened");
                JSObject data = new JSObject();
                data.put("adType", adType);
                admobAds.notifyListeners("onAdOpened", data);
            }
        });
    }

    @Override
    public void onAdLeftApplication() {
        Activity admobAdsActivity = (Activity) admobAds.getBridge().getActivity();
        admobAdsActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(AdMobAds.ADMOBADS_LOGTAG, adType + ": left application");
                JSObject data = new JSObject();
                data.put("adType", adType);
                admobAds.notifyListeners("onAdLeftApplication", data);
            }
        });
    }

    @Override
    public void onAdClosed() {
        Activity admobAdsActivity = (Activity) admobAds.getBridge().getActivity();
        admobAdsActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(AdMobAds.ADMOBADS_LOGTAG, adType + ": ad closed after clicking on it");
                JSObject data = new JSObject();
                data.put("adType", adType);
                admobAds.notifyListeners("onAdClosed", data);
            }
        });
    }
}
