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
import com.eaw.scary.teacher3d.game.hdwallpapers.Utils.SharedPrefs;
import com.eaw.wallpaper.R;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
    private View rootView;
    RecyclerView recyclerView;

    ProgressBar progress;
    ImagesAdapter adapter;
    private ArrayList<WallpaperModel> itemList = new ArrayList<>();

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new, container, false);
        progress = rootView.findViewById(R.id.progress);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        progress.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onResume() {
        itemList = new ArrayList<>(SharedPrefs.getLikedMap().values());
        adapter = new ImagesAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);
        super.onResume();
    }
}
