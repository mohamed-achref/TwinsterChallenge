package com.khattech.twinsterchallenge.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.khattech.twinsterchallenge.R;
import com.khattech.twinsterchallenge.models.Genre;
import com.khattech.twinsterchallenge.models.Movie;
import com.khattech.twinsterchallenge.net.Constant;
import com.khattech.twinsterchallenge.net.callback.OnCallback;
import com.khattech.twinsterchallenge.net.services.MovieService;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DetailsMovieActivity extends AppCompatActivity {

    private String TAG = "DetailsMovieActivity";

    private TextView tvTitleMovie;
    private TextView tvDetails;
    private TextView tvDescription;

    private ImageView image;
    private FrameLayout icRightButton;
    private Button btnLike;
    private ArrayList<Movie> moviesFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);

        tvTitleMovie = findViewById(R.id.tvTitleMovie);
        tvDetails = findViewById(R.id.tvDetails);
        tvDescription = findViewById(R.id.tvDescription);
        image = findViewById(R.id.image);
        icRightButton = findViewById(R.id.icRightButton);
        btnLike = findViewById(R.id.btnLike);

        final Movie movie = Hawk.get(Constant.PREFS_MOVIE);

        icRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String message = getResources().getString(R.string.message_share);

                sendIntent.putExtra(Intent.EXTRA_TEXT, message + ": " + movie.getTitle());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

//                shareImage(Constant.URL_IMAGE + movie.getPoster_path(),DetailsMovieActivity.this);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
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

                    btnLike.setBackgroundResource(R.drawable.ic_liked);
                } else {
                    moviesFav = Hawk.get(Constant.PREFS_FAVORITE_MOVIE);
                    moviesFav.remove(movie);
                    Hawk.put(Constant.PREFS_FAVORITE_MOVIE, moviesFav);

                    btnLike.setBackgroundResource(R.drawable.ic_like);
                }

            }
        });


        MovieService movieService = new MovieService();
        movieService.getDetailsMovies(movie.getId(), new OnCallback() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, "onSuccess: " + response);

                Movie movieDetails = (Movie) response;

                Glide.with(DetailsMovieActivity.this)
                        .load(Constant.URL_IMAGE + movie.getPoster_path())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(image);

                tvTitleMovie.setText(movieDetails.getTitle());
                StringBuilder genreString = new StringBuilder();

                for (Genre genre : movieDetails.getGenres()) {
                    genreString.append(genre.getName()).append(", ");
                }
                String details = "";
                if (movieDetails.getRelease_date() != null)
                    details = details + movieDetails.getRelease_date().substring(0, 4);

                if (movieDetails.getGenres().size() > 0)
                    details = details + " | " + genreString.substring(0, genreString.length() - 1);

                if (movie.getVote_average() != null)
                    details = details + " | " + movie.getVote_average();


                tvDetails.setText(details);
                tvDescription.setText(movieDetails.getOverview());
            }

            @Override
            public void onError(int errorCode) {
                Log.d(TAG, "onError: " + errorCode);
            }
        });
    }
    static public void shareImage(String url, final Context context) {
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                context.startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public void onBack(View view) {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
