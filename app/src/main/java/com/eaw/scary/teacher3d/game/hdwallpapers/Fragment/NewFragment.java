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
import java.util.Collections;
import java.util.Comparator;

public class NewFragment extends Fragment {
    private View rootView;
    RecyclerView recyclerView;

    ProgressBar progress;
    ImagesAdapter adapter;
    private ArrayList<WallpaperModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new, container, false);
        progress = rootView.findViewById(R.id.progress);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        GridLayoutManager gridLayout = new GridLayoutManager(getContext(), 2);
        gridLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                return (position + 1) % 5 == 0 ? 2 : 1;

            }
        });
        recyclerView.setLayoutManager(gridLayout);
        adapter = new ImagesAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);
        getDataFromDb();
        return rootView;

    }

    private void getDataFromDb() {
        progress.setVisibility(View.VISIBLE);
        mDatabase.child("Wallpapers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        WallpaperModel model = snapshot1.getValue(WallpaperModel.class);
                        if (model != null && model.getPicUrl() != null) {
                            itemList.add(model);
                        }
                    }

                    Collections.sort(itemList, new Comparator<WallpaperModel>() {
                        @Override
                        public int compare(WallpaperModel listData, WallpaperModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();
                            return ob2.compareTo(ob1);

                        }
                    });
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
