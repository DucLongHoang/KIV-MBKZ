package com.example.fitnessapptabbed.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.fitnessapptabbed.R;
import com.example.fitnessapptabbed.ui.main.plans.PlansFragment;
import com.example.fitnessapptabbed.ui.main.plans.PlansHolderFragment;
import com.example.fitnessapptabbed.ui.main.stats.StatsFragment;
import com.example.fitnessapptabbed.ui.main.train.TrainFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{
            R.string.tab_plans, R.string.tab_train, R.string.tab_stats
    };
    private final Context mContext;
    private Fragment planHolderFragment;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return PlansHolderFragment.newInstance();
            case 1: return TrainFragment.newInstance();
            case 2: return StatsFragment.newInstance();
            default: throw new RuntimeException("This shouldn't happen");
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}