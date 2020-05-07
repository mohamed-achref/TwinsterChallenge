package com.khattech.twinsterchallenge.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.khattech.twinsterchallenge.R;
import com.khattech.twinsterchallenge.models.User;
import com.khattech.twinsterchallenge.net.Constant;
import com.orhanobut.hawk.Hawk;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);

        User user = Hawk.get(Constant.PREFS_USER_CONNECTED);

        imageView = v.findViewById(R.id.ivImage);
        TextView tvName = v.findViewById(R.id.tvName);
        TextView tvEmail = v.findViewById(R.id.tvEmail);

        Bitmap iconPlus = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
        Log.d(TAG, "onCreateView: " + user);
        if (!user.getImage().equals("ic_user"))
            Glide.with(getActivity())
                    .load(new File(user.getImage())) // Uri of the picture
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        else
            Glide.with(getActivity())
                    .load(iconPlus) // Uri of the picture
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        setHasOptionsMenu(true);

        return v;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_profile, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_edit) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.const_content_main, new ProfileUpdateFragment())
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
