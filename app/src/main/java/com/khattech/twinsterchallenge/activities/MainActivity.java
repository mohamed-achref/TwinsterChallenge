package com.khattech.twinsterchallenge.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.khattech.twinsterchallenge.R;
import com.khattech.twinsterchallenge.fragment.FavoriteFragment;
import com.khattech.twinsterchallenge.fragment.HomeFragment;
import com.khattech.twinsterchallenge.fragment.ProfileFragment;
import com.khattech.twinsterchallenge.models.User;
import com.khattech.twinsterchallenge.net.Constant;
import com.orhanobut.hawk.Hawk;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private DrawerLayout drawer;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private User user;

    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_GALLERY_IMAGE = 1;

    private static String fileName;
    private ImageView imageView;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Log.d(TAG, "onCreate: " + Locale.getDefault());
        setSupportActionBar(toolbar);
        if (Hawk.get(Constant.PREFS_USER_CONNECTED) == null) {
            Log.d(TAG, "onCreate: if");
            user = new User("ic_user", "Mohamed Achref KHATTECHE", "Mohamedachref.khatteche@gmail.com");
            Hawk.put(Constant.PREFS_USER_CONNECTED, user);
        } else {
            Log.d(TAG, "onCreate: else");
            Hawk.get(Constant.PREFS_USER_CONNECTED);
        }

        configureNavigationDrawer();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.const_content_main, new HomeFragment())
                .commit();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().setStatusBarColor(Color.TRANSPARENT);

        lang = Hawk.get(Constant.PREFS_LANGUAGE);

        if (lang != null)
            if (lang.equals("de_DE"))
                Constant.setLocale(MainActivity.this, "de");
            else
                Constant.setLocale(MainActivity.this, "en");
    }


    private void configureNavigationDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_favorite, R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                user = Hawk.get(Constant.PREFS_USER_CONNECTED);

                NavigationView navigationView = findViewById(R.id.nav_view);

                View hView = navigationView.getHeaderView(0);
                imageView = hView.findViewById(R.id.imageView);
                TextView tvUserName = hView.findViewById(R.id.tvUserName);
                TextView tvUserEmail = hView.findViewById(R.id.tvUserEmail);

                Bitmap imageUser = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);

                if (!user.getImage().equals("ic_user"))
                    Glide.with(MainActivity.this)
                            .load(new File(user.getImage())) // Uri of the picture
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageView);
                else
                    Glide.with(MainActivity.this)
                            .load(imageUser) // Uri of the picture
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImagePickerOptions(MainActivity.this);
                    }
                });

                tvUserName.setText(user.getName());
                tvUserEmail.setText(user.getEmail());

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.const_content_main, new HomeFragment())
                                .commit();
                        break;
                    case R.id.nav_favorite:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.const_content_main, new FavoriteFragment())
                                .commit();
                        break;
                    case R.id.nav_profile:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.const_content_main, new ProfileFragment())
                                .commit();
                        break;
                    case R.id.nav_language:
                        selectLanguage();
                        break;

                    default:
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
        alertBuilder
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.exit_confirm))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void showImagePickerOptions(final Context context) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // add a list
        String[] items = {context.getString(R.string.take_picture), context.getString(R.string.choose_galery)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
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
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            fileName = System.currentTimeMillis() + ".jpg";
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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
        Dexter.withActivity(this)
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
        Uri destinationUri = Uri.fromFile(new File(this.getCacheDir(), queryName(this.getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        int IMAGE_COMPRESSION = 80;
        options.setCompressionQuality(IMAGE_COMPRESSION);

        try {
            options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        } catch (Exception E) {
            E.printStackTrace();
            options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
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
                .start(this);

        user.setImage(destinationUri.getPath());

        File res = new File(destinationUri.getPath());
        Glide.with(this)
                .load(res) // Uri of the picture
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);

        Hawk.put(Constant.PREFS_USER_CONNECTED, user);
        int nbFrag = MainActivity.this.getSupportFragmentManager().getFragments().size() - 1;

        Log.d(TAG, "cropImage:& " + (MainActivity.this.getSupportFragmentManager().getFragments().get(nbFrag).equals(ProfileFragment.class)));
        Log.d(TAG, "cropImage:é " + (MainActivity.this.getSupportFragmentManager().getFragments().get(nbFrag) instanceof ProfileFragment));

        if (MainActivity.this.getSupportFragmentManager().getFragments().get(nbFrag) instanceof ProfileFragment)
            Glide.with(this)
                    .load(res) // Uri of the picture
                    .apply(RequestOptions.circleCropTransform())
                    .into(((ProfileFragment) MainActivity.this.getSupportFragmentManager().getFragments().get(nbFrag)).imageView);

    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        this.setResult(Activity.RESULT_CANCELED, intent);
//        this.finish();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(this.getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return FileProvider.getUriForFile(this, this.getPackageName() + ".provider", image);
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

    private void selectLanguage() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_language);
        // there are a lot of settings, for dialog, check them all out!
        // set up radiobutton
        final RadioButton rd1 = dialog.findViewById(R.id.rdLang1);
        final RadioButton rd2 = dialog.findViewById(R.id.rdLang2);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        if (lang != null) {
            if (lang.equals("en-US"))
                rd1.setChecked(true);
            else
                rd2.setChecked(true);
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: OK ");
                if (rd1.isChecked()) {
                    Log.d(TAG, "onClick: rd1");
                    Hawk.put(Constant.PREFS_LANGUAGE, "en_US");
                    Constant.setLocale(MainActivity.this, "en");
                } else if (rd2.isChecked()) {
                    Log.d(TAG, "onClick: rd2");
                    Hawk.put(Constant.PREFS_LANGUAGE, "de_DE");
                    Constant.setLocale(MainActivity.this, "de");
                }

                recreate();
                dialog.dismiss();
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        // now that the dialog is set up, it's time to show it
        dialog.show();


    }
}
