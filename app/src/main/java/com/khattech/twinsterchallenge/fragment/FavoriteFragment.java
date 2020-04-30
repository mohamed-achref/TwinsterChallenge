package com.khattech.twinsterchallenge.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khattech.twinsterchallenge.R;
import com.khattech.twinsterchallenge.adapter.MovieAdapter;
import com.khattech.twinsterchallenge.models.Movie;
import com.khattech.twinsterchallenge.net.Constant;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {

    private static final String TAG = "FavoriteFragment";
    private ArrayList<Movie> movies;

    private MovieAdapter movieAdapter;
    private RecyclerView rvMovies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        rvMovies = view.findViewById(R.id.rvMovies);
        rvMovies.setHasFixedSize(true);

        rvMovies.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
        movies = Hawk.get(Constant.PREFS_FAVORITE_MOVIE);

        Log.d(TAG, "onCreateView: "+movies);

        movieAdapter = new MovieAdapter(getActivity(), movies,"FavoriteFragment");
        rvMovies.setAdapter(movieAdapter);
        return view;
    }
}
