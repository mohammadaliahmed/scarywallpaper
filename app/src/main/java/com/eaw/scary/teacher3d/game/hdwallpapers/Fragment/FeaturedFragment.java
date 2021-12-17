package com.eaw.scary.teacher3d.game.hdwallpapers.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eaw.scary.teacher3d.game.hdwallpapers.Adapters.ImagesAdapter;
import com.eaw.scary.teacher3d.game.hdwallpapers.Models.WallpaperModel;
import com.eaw.wallpaper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FeaturedFragment extends Fragment {
    private View rootView;
    RecyclerView recyclerView;

    ImagesAdapter adapter;
    private ArrayList<WallpaperModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;
    ProgressBar progress;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new, container, false);
        progress = rootView.findViewById(R.id.progress);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ImagesAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);
        getDataFromDb();
        return rootView;
    }

    private void getDataFromDb() {
        mDatabase.child("Wallpapers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    itemList.clear();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        WallpaperModel model = snapshot1.getValue(WallpaperModel.class);
                        if (model != null && model.getPicUrl() != null) {
                            if (model.isFeatured()) {
                                itemList.add(model);
                            }
                        }
                    }
                    progress.setVisibility(View.GONE);
                    adapter.setItemList(itemList);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
