package com.eaw.scary.teacher3d.game.hdwallpapers.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.eaw.scary.teacher3d.game.hdwallpapers.Fragment.FeaturedFragment;
import com.eaw.scary.teacher3d.game.hdwallpapers.Fragment.NewFragment;
import com.eaw.scary.teacher3d.game.hdwallpapers.Fragment.PopularFragemnt;
import com.eaw.scary.teacher3d.game.hdwallpapers.Fragment.RandomFragment;


/**
 * Created by AliAh on 02/03/2018.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new NewFragment();
        } else if (position == 1) {
            return new FeaturedFragment();
        } else if (position == 2) {
            return new PopularFragemnt();
        } else if (position == 3) {
            return new RandomFragment();
        } else {
            return null;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 4;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "New";
            case 1:
                return "Featured";
            case 2:
                return "Popular";
            case 3:
                return "Random";

            default:
                return null;
        }
    }

}
