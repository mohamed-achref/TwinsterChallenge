package com.khattech.twinsterchallenge.net.services;

import android.annotation.SuppressLint;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.khattech.twinsterchallenge.models.Movie;
import com.khattech.twinsterchallenge.net.Constant;
import com.khattech.twinsterchallenge.net.callback.OnCallback;
import com.khattech.twinsterchallenge.net.utils.ArtistApp;
import com.khattech.twinsterchallenge.net.utils.GsonUtils;
import com.khattech.twinsterchallenge.net.utils.VolleyMultipartRequest;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mohamed Achref on 3/6/20.
 */
@SuppressLint("LongLogTag")
public class MovieService {

    public void getMoviesNowPlaying(int page, final OnCallback callback) {
        final String TAG = "getMoviesNowPlaying";
        String url;

        String lang = Hawk.get(Constant.PREFS_LANGUAGE);
        if (lang != null)
            url = Constant.BASE_URL + "now_playing?api_key=" + Constant.API_TOKEN + "&language=" + lang + "&page=" + page;
        else if (Locale.getDefault().toString().equals("de_DE"))
            url = Constant.BASE_URL + "now_playing?api_key=" + Constant.API_TOKEN + "&language=de_DE&page=" + page;
        else
            url = Constant.BASE_URL + "now_playing?api_key=" + Constant.API_TOKEN + "&language=en-US&page=" + page;

        Log.d(TAG, "getMovies: " + url);


        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.d(TAG, "onResponse:resultResponse " + resultResponse);
                try {
                    JSONObject result1 = new JSONObject(resultResponse);
                    JSONArray result = result1.getJSONArray("results");

                    int totalPage = result1.getInt("total_pages");
                    Hawk.put(Constant.PREFS_TOTAL_PAGE_NP, totalPage);

                    Log.d(TAG, "onResponse:totalPage " + totalPage);
                    List<Movie> movies = new ArrayList<>();
                    for (int i = 0; i < result.length(); i++) {
                        Movie movie = GsonUtils.fromJSON(result.getJSONObject(i), Movie.class);
                        if (!result.getJSONObject(i).getString("poster_path").equals("null"))
                            movies.add(movie);
                    }
                    callback.onSuccess(movies);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: ");
            }
        });

        multipartRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ArtistApp.getInstance().addToRequestQueue(multipartRequest);
    }

    public void getMoviesUpcoming(int page, final OnCallback callback) {
        final String TAG = "getMoviesUpcoming";
        String url;
        String lang = Hawk.get(Constant.PREFS_LANGUAGE);
        if (lang != null)
            url = Constant.BASE_URL + "upcoming?api_key=" + Constant.API_TOKEN + "&language=" + lang + "&page=" + page;
        else if (Locale.getDefault().toString().equals("de_DE"))
            url = Constant.BASE_URL + "upcoming?api_key=" + Constant.API_TOKEN + "&language=de_DE&page=" + page;
        else
            url = Constant.BASE_URL + "upcoming?api_key=" + Constant.API_TOKEN + "&language=en-US&page=" + page;
        Log.d(TAG, "getMovies: " + url);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.d(TAG, "onResponse:resultResponse " + resultResponse);
                try {
                    JSONObject result1 = new JSONObject(resultResponse);
                    JSONArray result = result1.getJSONArray("results");
                    int totalPage = result1.getInt("total_pages");
                    Log.d(TAG, "onResponse:totalPage " + totalPage);
                    Hawk.put(Constant.PREFS_TOTAL_PAGE_U, totalPage);
                    List<Movie> movies = new ArrayList<>();
                    for (int i = 0; i < result.length(); i++) {
                        Movie movie = GsonUtils.fromJSON(result.getJSONObject(i), Movie.class);
                        if (!result.getJSONObject(i).getString("poster_path").equals("null"))
                            movies.add(movie);
                    }
                    callback.onSuccess(movies);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: ");
            }
        });

        multipartRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ArtistApp.getInstance().addToRequestQueue(multipartRequest);
    }

    public void getDetailsMovies(String id, final OnCallback callback) {
        final String TAG = "getDetailsMovies";

        String url;
        String lang = Hawk.get(Constant.PREFS_LANGUAGE);
        if (lang != null)
            url = Constant.BASE_URL + id + "?api_key=" + Constant.API_TOKEN + "&language="+lang+"&page=1";
        else if (Locale.getDefault().toString().equals("de_DE"))
            url = Constant.BASE_URL + id + "?api_key=" + Constant.API_TOKEN + "&language=de_DE&page=1";
        else
            url = Constant.BASE_URL + id + "?api_key=" + Constant.API_TOKEN + "&language=en-US&page=1";

        Log.d(TAG, "getMovies: " + url);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.d(TAG, "onResponse:resultResponse " + resultResponse);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    Movie movie = GsonUtils.fromJSON(result, Movie.class);
                    callback.onSuccess(movie);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: ");
            }
        });

        multipartRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ArtistApp.getInstance().addToRequestQueue(multipartRequest);
    }

}
