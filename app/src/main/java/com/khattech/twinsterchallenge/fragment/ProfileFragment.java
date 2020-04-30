package com.khattech.twinsterchallenge.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.khattech.twinsterchallenge.R;
import com.khattech.twinsterchallenge.models.User;
import com.khattech.twinsterchallenge.net.Constant;
import com.orhanobut.hawk.Hawk;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnUpdate = v.findViewById(R.id.btnUpdate);
        final ImageView imageView = v.findViewById(R.id.ivImage);
        final EditText edName = v.findViewById(R.id.edName);
        final EditText edEmail = v.findViewById(R.id.edEmail);

        Bitmap iconPlus = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
        imageView.setImageBitmap(iconPlus);

        user = Hawk.get(Constant.PREFS_USER_CONNECTED);

        edName.setText(user.getName());
        edEmail.setText(user.getEmail());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setEmail(edEmail.getText().toString());
                user.setName(edName.getText().toString());

                Hawk.put(Constant.PREFS_USER_CONNECTED,user);

                String message = getActivity().getResources().getString(R.string.message_profile);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
