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
import com.khattech.twinsterchallenge.net.callback.OnCallback;
import com.khattech.twinsterchallenge.net.services.MovieService;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment {

    private static final String TAG = "UpcomingFragment";

    private ArrayList<Movie> movies;

    private MovieAdapter movieAdapter;
    private RecyclerView rvMovies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        rvMovies = view.findViewById(R.id.rvMovies);
        rvMovies.setHasFixedSize(true);

        rvMovies.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));

        fetchMovies();
        return view;
    }


    private void fetchMovies() {

        MovieService service = new MovieService();
        service.getMoviesUpcoming(new OnCallback() {
            @Override
            public void onSuccess(Object response) {
                movies = (ArrayList<Movie>) response;
                Log.d(TAG, "onSuccess: " + movies);
                movieAdapter = new MovieAdapter(getActivity(), movies);
                rvMovies.setAdapter(movieAdapter);
            }

            @Override
            public void onError(int errorCode) {
                Log.d(TAG, "onError: " + errorCode);
            }
        });

    }
}
