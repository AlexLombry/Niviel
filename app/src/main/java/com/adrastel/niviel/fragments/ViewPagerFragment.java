package com.adrastel.niviel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.html.HistoryFragment;
import com.adrastel.niviel.fragments.html.RecordFragment;

public class ViewPagerFragment extends BaseFragment {

    private MainActivity activity;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            activity = (MainActivity) getActivity();
        }

        catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

        if(activity != null) {

            try {
                ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);

                tabLayout = (TabLayout) activity.findViewById(R.id.tab_layout);
                tabLayout.setupWithViewPager(viewPager);
                setupViewPager(viewPager);

            }

            catch (Exception e) {
                e.printStackTrace();
            }

        }

        return view;
    }

    @SuppressWarnings("ConstantConditions")
    private void setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_profile);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_star);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_history);

    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Records;
    }

    // todo: utiliser les onrestorinstancestate
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new ProfileFragment();

                case 1:
                    return new RecordFragment();

                case 2:
                    return new HistoryFragment();

                default:
                    return new ProfileFragment();
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            /*switch (position) {
                case 0:
                    return getString(R.string.profile);

                case 1:
                    return getString(R.string.records);

                case 2:
                    return getString(R.string.history);

                default:
                    return null;
            }*/
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
