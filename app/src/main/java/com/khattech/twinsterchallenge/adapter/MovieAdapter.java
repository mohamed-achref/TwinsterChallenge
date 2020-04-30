package com.khattech.twinsterchallenge.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.khattech.twinsterchallenge.R;
import com.khattech.twinsterchallenge.activities.DetailsMovieActivity;
import com.khattech.twinsterchallenge.models.Movie;
import com.khattech.twinsterchallenge.net.Constant;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

/**
 * Created by Mohamed Achref on 2020-01-09.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.SingleItemRowHolder> {
    private String TAG = "MovieAdapter";

    private ArrayList<Movie> itemsList;
    private Context mContext;
    private String parentFrag;
    private int mLowestPosition = -1;

    private ArrayList<Movie> moviesFav;

    public MovieAdapter(Context context, ArrayList<Movie> itemsList, String parentFrag) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.parentFrag = parentFrag;
    }

    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie_list, null);
        return new SingleItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SingleItemRowHolder holder, final int i) {
        Log.d(TAG, "onBindViewHolder: ");
        final Movie movie = itemsList.get(i);

//        if (i > mLowestPosition) {
//            if (movie != null) {
        moviesFav = Hawk.get(Constant.PREFS_FAVORITE_MOVIE);

        Log.d(TAG, "onBindViewHolder: " + moviesFav);
        Log.d(TAG, "onBindViewHolder: " + moviesFav.contains(movie));

        if (moviesFav != null && moviesFav.contains(movie)) {
            movie.setFavourite(true);
            Log.d(TAG, "onBindViewHolder: != null ");
            holder.btnLike.setBackgroundResource(R.drawable.ic_liked);
        }
        Glide.with(mContext)
                .load(Constant.URL_IMAGE + movie.getPoster_path())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.itemImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: prod " + movie);

                Hawk.put(Constant.PREFS_MOVIE, movie);
                Intent intent = new Intent(mContext, DetailsMovieActivity.class);

                mContext.startActivity(intent);
            }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movie.setFavourite(!movie.isFavourite());

                if (movie.isFavourite()) {
                    if (Hawk.get(Constant.PREFS_FAVORITE_MOVIE) == null) {
                        moviesFav = new ArrayList<>();
                    } else {
                        moviesFav = Hawk.get(Constant.PREFS_FAVORITE_MOVIE);
                    }

                    moviesFav.add(movie);
                    Hawk.put(Constant.PREFS_FAVORITE_MOVIE, moviesFav);

                    holder.btnLike.setBackgroundResource(R.drawable.ic_liked);
                } else {
                    moviesFav = Hawk.get(Constant.PREFS_FAVORITE_MOVIE);
                    moviesFav.remove(movie);
                    Hawk.put(Constant.PREFS_FAVORITE_MOVIE, moviesFav);

                    holder.btnLike.setBackgroundResource(R.drawable.ic_like);

                    if (parentFrag.equals("FavoriteFragment")){
                        itemsList.remove(i);
                        notifyItemRemoved(i);
                    }
                }

            }
        });

//            }

//            mLowestPosition = i;
//        }
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private ImageView itemImage;
        private Button btnLike;

        SingleItemRowHolder(View view) {
            super(view);

            this.itemImage = view.findViewById(R.id.itemImage);
            this.btnLike = view.findViewById(R.id.btnLike);

        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


}