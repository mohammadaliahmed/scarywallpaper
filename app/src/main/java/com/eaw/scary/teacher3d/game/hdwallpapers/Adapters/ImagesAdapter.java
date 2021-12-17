package com.eaw.scary.teacher3d.game.hdwallpapers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eaw.scary.teacher3d.game.hdwallpapers.Activities.ViewImage;
import com.eaw.scary.teacher3d.game.hdwallpapers.Models.WallpaperModel;
import com.eaw.wallpaper.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    Context context;
    ArrayList<WallpaperModel> itemList;
    public static final int GOOGLE_AD_LAYOUT = 1;
    public static final int AD_LAYOUT = 0;

    public ImagesAdapter(Context context, ArrayList<WallpaperModel> itemList) {
        this.context = context;
        this.itemList = itemList;

    }

    public void setItemList(ArrayList<WallpaperModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_item_layout, parent, false);
//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;

        if (viewType == GOOGLE_AD_LAYOUT) {
            View view = LayoutInflater.from(context).inflate(R.layout.ad_in_list_layout, parent, false);
            ImagesAdapter.ViewHolder viewHolder = new ImagesAdapter.ViewHolder(view);
            return viewHolder;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_item_layout, parent, false);
            ImagesAdapter.ViewHolder viewHolder = new ImagesAdapter.ViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case AD_LAYOUT:

                WallpaperModel model = itemList.get(position);
                Glide.with(context).load(model.getPicUrl()).into(holder.image);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ViewImage.class);
                        i.putExtra("picUrl", model.getPicUrl());
                        i.putExtra("mylist", itemList);
                        i.putExtra("position", position);

                        context.startActivity(i);
                    }
                });
            case GOOGLE_AD_LAYOUT:
                AdRequest adRequest = new AdRequest.Builder().build();
                if(holder.adView!=null) {
                    holder.adView.loadAd(adRequest);
                }
                break;
            default:
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position + 1) % 5 == 0 ? GOOGLE_AD_LAYOUT : AD_LAYOUT;

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        AdView adView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            adView = itemView.findViewById(R.id.adView);

        }
    }

}
