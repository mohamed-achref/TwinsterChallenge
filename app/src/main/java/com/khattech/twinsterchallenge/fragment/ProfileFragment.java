package com.khattech.twinsterchallenge.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.khattech.twinsterchallenge.R;
import com.khattech.twinsterchallenge.models.User;
import com.khattech.twinsterchallenge.net.Constant;
import com.orhanobut.hawk.Hawk;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private User user;
    public ImageView imageView;
    private EditText edName, edEmail;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_GALLERY_IMAGE = 1;

    private static String fileName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);

        user = Hawk.get(Constant.PREFS_USER_CONNECTED);

        Button btnUpdate = v.findViewById(R.id.btnUpdate);
        imageView = v.findViewById(R.id.ivImage);
        edName = v.findViewById(R.id.edName);
        edEmail = v.findViewById(R.id.edEmail);

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

        edName.setText(user.getName());
        edEmail.setText(user.getEmail());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setEmail(edEmail.getText().toString());
                user.setName(edName.getText().toString());

                Hawk.put(Constant.PREFS_USER_CONNECTED, user);

                String message = getActivity().getResources().getString(R.string.message_profile);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerOptions(getContext());
            }
        });

        return v;
    }

    private void showImagePickerOptions(final Context context) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // add a list
        String[] animals = {context.getString(R.string.take_picture), context.getString(R.string.choose_galery)};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takeCameraImage();
                        break;
                    case 1:
                        chooseImageFromGallery();
                        break;
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void takeCameraImage() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            fileName = System.currentTimeMillis() + ".jpg";
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void chooseImageFromGallery() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    cropImage(getCacheImagePath(fileName));
                } else {
                    setResultCancelled();
                }
                break;
            case REQUEST_GALLERY_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    cropImage(imageUri);
                } else {
                    setResultCancelled();
                }
                break;

            default:
                setResultCancelled();
        }
    }

    private void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getActivity().getCacheDir(), queryName(getActivity().getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        int IMAGE_COMPRESSION = 80;
        options.setCompressionQuality(IMAGE_COMPRESSION);


        try {
            options.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            options.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        } catch (Exception E) {
            E.printStackTrace();
            options.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            options.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        }

        boolean lockAspectRatio = false;
        int ASPECT_RATIO_X = 16;
        int ASPECT_RATIO_Y = 9;
        if (lockAspectRatio)
            options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);

        boolean setBitmapMaxWidthHeight = false;
        int bitmapMaxHeight = 1000;
        int bitmapMaxWidth = 1000;
        if (setBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(getActivity());

        user.setImage(destinationUri.getPath());

        File res = new File(destinationUri.getPath());
        Glide.with(getActivity())
                .load(res) // Uri of the picture
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        getActivity().setResult(Activity.RESULT_CANCELED, intent);
//        getActivity().finish();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getActivity().getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", image);
    }

    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    public static void clearCache(Context context) {
        File path = new File(context.getExternalCacheDir(), "camera");
        if (path.exists() && path.isDirectory()) {
            for (File child : path.listFiles()) {
                child.delete();
            }
        }
    }
}
