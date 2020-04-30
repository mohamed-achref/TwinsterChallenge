package com.khattech.twinsterchallenge.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.khattech.twinsterchallenge.R;
import com.khattech.twinsterchallenge.fragment.FavoriteFragment;
import com.khattech.twinsterchallenge.fragment.HomeFragment;
import com.khattech.twinsterchallenge.fragment.ProfileFragment;
import com.khattech.twinsterchallenge.models.User;
import com.khattech.twinsterchallenge.net.Constant;
import com.orhanobut.hawk.Hawk;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "";
    private AppBarConfiguration mAppBarConfiguration;

    private DrawerLayout drawer;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Hawk.get(Constant.PREFS_USER_CONNECTED) == null) {
            Log.d(TAG, "onCreate: if");
            user = new User("ic_user", "Mohamed Achref KHATTECHE", "Mohamedachref.khatteche@gmail.com");
            Hawk.put(Constant.PREFS_USER_CONNECTED, user);
        }else{
            Log.d(TAG, "onCreate: else");
            Hawk.get(Constant.PREFS_USER_CONNECTED);
        }


        configureNavigationDrawer();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.const_content_main, new HomeFragment())
                .commit();
    }

    private void configureNavigationDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
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
                ImageView imageView = hView.findViewById(R.id.imageView);
                TextView tvUserName = hView.findViewById(R.id.tvUserName);
                TextView tvUserEmail = hView.findViewById(R.id.tvUserEmail);

                Bitmap imageUser = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
                Bitmap imageUserRounded = Constant.getRoundedCornerBitmap(imageUser, Constant.dpToPx(100, MainActivity.this));

                imageView.setImageBitmap(imageUserRounded);

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

                    default:
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


//        NavigationView navigationView = findViewById(R.id.nav_view);
//        View hView = navigationView.getHeaderView(0);
//        ImageView imageView = hView.findViewById(R.id.imageView);
//        TextView tvUserName = hView.findViewById(R.id.tvUserName);
//        TextView tvUserEmail = hView.findViewById(R.id.tvUserEmail);
//
//        Bitmap iconPlus = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user);
////        Bitmap imagePlus = Utils.getRoundedCornerBitmap(iconPlus, Utils.dpToPx(20, this));
//        imageView.setImageBitmap(iconPlus);
//
//        tvUserName.setText(user.getName());
//        tvUserEmail.setText(user.getEmail());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


}
