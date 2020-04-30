package com.khattech.twinsterchallenge.net.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.orhanobut.hawk.Hawk;

@SuppressLint("Registered")
public class ArtistApp extends Application {

    public static final String TAG = ArtistApp.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static ArtistApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Hawk.init(getApplicationContext()).build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static synchronized ArtistApp getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private boolean grid_mode = true;

    public boolean isGrid_mode() {
        return grid_mode;
    }

    public void setGrid_mode(boolean grid_mode) {
        this.grid_mode = grid_mode;
    }

}
