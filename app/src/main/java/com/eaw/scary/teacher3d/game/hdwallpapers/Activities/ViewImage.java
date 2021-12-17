package com.eaw.scary.teacher3d.game.hdwallpapers.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        imgList = (ArrayList<WallpaperModel>) getIntent().getSerializableExtra("mylist");
        position = getIntent().getIntExtra("position", 0);

        googleAd = findViewById(R.id.googleAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        googleAd.loadAd(adRequest);



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

    //runtime storage permission
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //do somethings
        }
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
