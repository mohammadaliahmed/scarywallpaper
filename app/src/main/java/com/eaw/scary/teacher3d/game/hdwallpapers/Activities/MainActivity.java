package com.eaw.scary.teacher3d.game.hdwallpapers.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.eaw.scary.teacher3d.game.hdwallpapers.Adapters.SimpleFragmentPagerAdapter;
import com.eaw.scary.teacher3d.game.hdwallpapers.Fragment.CollectionsFragment;
import com.eaw.scary.teacher3d.game.hdwallpapers.Fragment.FavoritesFragment;
import com.eaw.scary.teacher3d.game.hdwallpapers.Fragment.HomeFragment;
import com.eaw.scary.teacher3d.game.hdwallpapers.Utils.CommonUtils;
import com.eaw.wallpaper.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;


    private Fragment fragment;
    public static int openCounter = 0;
    private ReviewManager reviewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragment = new HomeFragment();
        loadFragment(fragment);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        reviewManager = ReviewManagerFactory.create(this);


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    toolbar.setTitle("Shop");


                    fragment = new HomeFragment();
                    loadFragment(fragment);

                    return true;
                case R.id.navigation_collections:
                    fragment = new CollectionsFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_favorites:
                    fragment = new FavoritesFragment();
                    loadFragment(fragment);
                    return true;

            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    private void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
//                showRateAppFallbackDialog();
            }
        });

    }

    private void showRateAppFallbackDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Rate App")
                .setMessage("Please rate our app")
                .setPositiveButton("Rate Now", (dialog, which) -> {
                    CommonUtils.showToast("done");
                })
                .setNegativeButton("Remind me later",
                        (dialog, which) -> {
                            CommonUtils.showToast("later");
                        })
                .setNeutralButton("No, Thanks",
                        (dialog, which) -> {
                            CommonUtils.showToast("canceled");
                        })
                .setOnDismissListener(dialog -> {
                    CommonUtils.showToast("dismiss");
                })
                .show();
    }



}
