package com.eaw.scary.teacher3d.game.hdwallpapers.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eaw.scary.teacher3d.game.hdwallpapers.Adapters.MainSliderAdapter;
import com.eaw.scary.teacher3d.game.hdwallpapers.Models.WallpaperModel;
import com.eaw.scary.teacher3d.game.hdwallpapers.Utils.ApplicationClass;
import com.eaw.scary.teacher3d.game.hdwallpapers.Utils.CommonUtils;
import com.eaw.scary.teacher3d.game.hdwallpapers.Utils.SharedPrefs;
import com.eaw.wallpaper.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewImage extends AppCompatActivity {

    private ArrayList<WallpaperModel> imgList = new ArrayList<>();
    int position;
    private ViewPager viewPager;
    private MainSliderAdapter mViewPagerAdapter;

    LinearLayout apply, likeUnlike, save;
    ImageView likeUnlikeHeart;

    private String optionChosen = "home";
    private HashMap<String, WallpaperModel> likedMap = new HashMap<>();
    public static final int PERMISSION_WRITE = 0;

    private AdView googleAd;
    private InterstitialAd interstitialAd;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    LinearLayout info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        imgList = (ArrayList<WallpaperModel>) getIntent().getSerializableExtra("mylist");
        position = getIntent().getIntExtra("position", 0);

        googleAd = findViewById(R.id.googleAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        googleAd.loadAd(adRequest);
        getPermissions();

        info = findViewById(R.id.info);
        likeUnlikeHeart = findViewById(R.id.likeUnlikeHeart);
        likeUnlike = findViewById(R.id.likeUnlike);
        save = findViewById(R.id.save);
        apply = findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showApplyAlert();
            }
        });
        this.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });

        viewPager = findViewById(R.id.viewpager);
        mViewPagerAdapter = new MainSliderAdapter(this, imgList);
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setCurrentItem(position);
        checkLikeStatus();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ViewImage.this.position = position;
                checkLikeStatus();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        likeUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (likedMap.containsKey(imgList.get(position).getId())) {
                    likedMap.remove(imgList.get(position).getId());
                    SharedPrefs.setLikedMap(likedMap);
                    Glide.with(ViewImage.this).load(R.drawable.heart_empty).into(likeUnlikeHeart);
                } else {
                    likedMap.put(imgList.get(position).getId(), imgList.get(position));
                    SharedPrefs.setLikedMap(likedMap);
                    Glide.with(ViewImage.this).load(R.drawable.heart_fill).into(likeUnlikeHeart);
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(imgList.get(position).getPicUrl());
                DownloadManager downloadManager = (DownloadManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis() + ".jpg");
                downloadManager.enqueue(request);
                CommonUtils.showToast("Saved");
            }
        });
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        ViewImage.this.interstitialAd = interstitialAd;
                        if (MainActivity.openCounter > 0) {
                            if (MainActivity.openCounter % 3 == 0) {
                                showInterstitial();
                            }
                        }
                        MainActivity.openCounter = MainActivity.openCounter + 1;

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        ViewImage.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        ViewImage.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                                ViewImage.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });


    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();

        }
    }


    private void checkLikeStatus() {
        likedMap = SharedPrefs.getLikedMap();
        if (likedMap != null) {
            if (likedMap.containsKey(imgList.get(position).getId())) {
                Glide.with(ViewImage.this).load(R.drawable.heart_fill).into(likeUnlikeHeart);
            } else {
                Glide.with(ViewImage.this).load(R.drawable.heart_empty).into(likeUnlikeHeart);
            }
        } else {
            likedMap = new HashMap<>();
        }
    }

    private void showApplyAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Set as");
        String[] items = {"Home Screen", "Lock Screen", "Home & Lock screen"};
        int checkedItem = 0;
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        optionChosen = "home";
                        break;
                    case 1:
                        optionChosen = "lock";

                        break;
                    case 2:
                        optionChosen = "both";
                        break;
                }
            }
        });
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setWallpaperNow();

            }
        });
        alertDialog.setNegativeButton("Cancel", null);
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void setWallpaperNow() {
        if (optionChosen.equals("home")) {
            setHomeWallpaper();
        } else if (optionChosen.equals("lock")) {
            setLOckScreenWallaper();
        } else if (optionChosen.equals("both")) {
            setHomeWallpaper();
            setLOckScreenWallaper();
        }
    }

    private void setLOckScreenWallaper() {
        Glide.with(this)
                .asBitmap()
                .load(imgList.get(position).getPicUrl())
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(ViewImage.this);
                        try {

                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                            CommonUtils.showToast("Done");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void setHomeWallpaper() {
        Glide.with(this)
                .asBitmap()
                .load(imgList.get(position).getPicUrl())
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(ViewImage.this);
                        try {
                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM);

                            CommonUtils.showToast("Done");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.SET_WALLPAPER,


        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
