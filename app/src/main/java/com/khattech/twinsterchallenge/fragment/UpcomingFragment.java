package com.khattech.twinsterchallenge.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.khattech.twinsterchallenge.R;
import com.khattech.twinsterchallenge.adapter.MovieAdapter;
import com.khattech.twinsterchallenge.models.Movie;
import com.khattech.twinsterchallenge.net.Constant;
import com.khattech.twinsterchallenge.net.callback.OnCallback;
import com.khattech.twinsterchallenge.net.services.MovieService;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment {
    private static final String TAG = "UpcomingFragment";

    private ArrayList<Movie> movies;
    private ArrayList<Movie> moviesAll;

    private MovieAdapter movieAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int visibleitemcount, totalitemcount, pastvisibleitems;
    private boolean loading = true;
    private int pagenumber = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        RecyclerView rvMovies = view.findViewById(R.id.rvMovies);
        rvMovies.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.setNestedScrollingEnabled(true);
        moviesAll = new ArrayList<>();
        movieAdapter = new MovieAdapter(getActivity(), moviesAll, "UpcomingFragment");
        rvMovies.setAdapter(movieAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading = true;
                fetchMovies(pagenumber);
            }
        });

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchMovies(pagenumber);
            }
        });

        rvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: " + dx + " " + dy);
                if (dy > 0) {
                    visibleitemcount = layoutManager.getChildCount();
                    totalitemcount = layoutManager.getItemCount();
                    pastvisibleitems = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    //if loading is true which means there is data to be fetched from the database
                    if (loading) {
                        int totalPage = Hawk.get(Constant.PREFS_TOTAL_PAGE_U);
                        Log.d(TAG, "onScrolled:totalpage " + totalPage);
                        if ((visibleitemcount + pastvisibleitems) >= totalitemcount && pagenumber < totalPage) {
                            swipeRefreshLayout.setRefreshing(true);
                            loading = false;
                            pagenumber += 1;
                            fetchMovies(pagenumber);
                        }
                    }
                }
            }
        });

        return view;
    }

    private void fetchMovies(int pagenumber) {
        MovieService service = new MovieService();
        service.getMoviesUpcoming(pagenumber, new OnCallback() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, "onSuccess: " + movies);
                movies = (ArrayList<Movie>) response;
                moviesAll.addAll(movies);
                movieAdapter.notifyDataSetChanged();
                loading = true;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int errorCode) {
                Log.d(TAG, "onError: " + errorCode);
                Toast.makeText(getContext(), "ERROR " + errorCode, Toast.LENGTH_LONG);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        movieAdapter.notifyDataSetChanged();
        Log.d(TAG, "onResume: ");
    }
}
