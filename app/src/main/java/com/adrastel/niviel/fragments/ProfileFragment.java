package com.adrastel.niviel.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.dialogs.EditProfileFollowDialog;
import com.adrastel.niviel.services.EditRecordService;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ProfileFragment extends BaseFragment {

    public static final int RECORD_TAB = 0;
    public static final int HISTORY_TAB = 1;

    public static final String FRAGMENT_TAG = "profileFragment";

    private MainActivity activity;
    private TabLayout tabLayout;

    private long follower_id = -1;
    private String wca_id = null;
    private String username = null;
    private int tab = RECORD_TAB;

    private ViewPager viewPager;

    public static ProfileFragment newInstance(long id) {
        ProfileFragment instance = new ProfileFragment();

        Bundle args = new Bundle();

        args.putInt(Constants.EXTRAS.TAB, RECORD_TAB);
        args.putLong(Constants.EXTRAS.ID, id);

        instance.setArguments(args);

        return instance;
    }

    public static ProfileFragment newInstance(int tab, String wca_id, String name) {
        ProfileFragment instance = new ProfileFragment();

        Bundle args = new Bundle();

        args.putInt(Constants.EXTRAS.TAB, tab);
        args.putString(Constants.EXTRAS.WCA_ID, wca_id);
        args.putString(Constants.EXTRAS.USERNAME, name);

        instance.setArguments(args);

        return instance;
    }

    public static ProfileFragment newInstance(String wca_id, String name) {
        return newInstance(0, wca_id, name);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(EditRecordService.ACTION, EditRecordService.ADD_RECORD_FAILURE)) {

                case EditRecordService.ADD_RECORD_SUCCESS:
                    activity.hideFab();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            activity = (MainActivity) getActivity();
        }

        catch (ClassCastException e) {
            e.printStackTrace();
        }

        Bundle args = getArguments();
        if(args != null) {

            tab = args.getInt(Constants.EXTRAS.TAB, RECORD_TAB);

            follower_id = args.getLong(Constants.EXTRAS.ID, -1);

            if(follower_id != -1) {
                DatabaseHelper database = DatabaseHelper.getInstance(getContext());

                Follower follower = database.selectFollowerFromId(follower_id);
                username = follower.name();
                wca_id = follower.wca_id();
            }
            else {
                wca_id = args.getString(Constants.EXTRAS.WCA_ID, null);
                username = args.getString(Constants.EXTRAS.USERNAME, null);

                if(wca_id == null) {

                    String[] usernames = getResources().getStringArray(R.array.random_name);
                    String[] wca_ids = getResources().getStringArray(R.array.random_wca_id);

                    int random = (int) (Math.random() * wca_ids.length);

                    username = usernames[random];
                    wca_id = wca_ids[random];


                }
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        tabLayout = (TabLayout) activity.findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        updateProfileInfos();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!Assets.isFollowing(activity, wca_id)) {
            activity.showFab();

            activity.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EditProfileFollowDialog dialog = EditProfileFollowDialog.newInstance(username);
                    dialog.show(getFragmentManager(), "setProfileDialog");

                    dialog.setOnDialogClickListener(new EditProfileFollowDialog.DialogProfileListener() {
                        @Override
                        public void onFollow() {

                            Intent follow = new Intent(getContext(), EditRecordService.class);

                            follow.putExtra(EditRecordService.ACTION, EditRecordService.ADD_FOLLOWER);
                            follow.putExtra(EditRecordService.USERNAME, username);
                            follow.putExtra(EditRecordService.WCA_ID, wca_id);

                            getContext().startService(follow);

                        }

                        @Override
                        public void onEdit() {

                            Intent delete = new Intent(getContext(), EditRecordService.class);
                            delete.putExtra(EditRecordService.ACTION, EditRecordService.DELETE_FOLLOWER);
                            delete.putExtra(EditRecordService.ID, follower_id);
                            delete.putExtra(EditRecordService.FOLLOWS, false);

                            getContext().startService(delete);


                            Intent add = new Intent(getContext(), EditRecordService.class);

                            add.putExtra(EditRecordService.ACTION, EditRecordService.ADD_FOLLOWER);
                            add.putExtra(EditRecordService.USERNAME, username);
                            add.putExtra(EditRecordService.WCA_ID, wca_id);
                            add.putExtra(EditRecordService.IS_PERSONAL, true);
                            add.putExtra(EditRecordService.FOLLOWS, false);

                            getContext().startService(add);
                        }
                    });

                }
            });
        }

        /*
        Creation du Tracker de Google Analytics
        Les dimensions et stats globales s'appliquent ici
         */
        Tracker tracker = activity.getDefaultTracker();
        tracker.setScreenName(getString(R.string.profile_fragment));

        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();

        // WCA ID
        if(follower_id != -1 && Assets.isPersonal(getContext(), follower_id)) {
            builder.setCustomDimension(1, wca_id);
        }

        // idDark
        boolean isDark = Assets.isDark(preferences.getString(getString(R.string.pref_isdark), "0"));
        builder.setCustomDimension(2, isDark ? "Sombre" : "Clair");

        tracker.send(builder.build());

    }

    @SuppressWarnings("ConstantConditions")
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(tab);


        tabLayout.getTabAt(0).setIcon(R.drawable.ic_star_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_history);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_pages);

    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Profile;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewPager.clearOnPageChangeListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTargetFragment(null, -1);
        super.onSaveInstanceState(outState);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return follower_id != -1 ? RecordFragment.newInstance(follower_id) : RecordFragment.newInstance(wca_id);

                case 1:
                    return follower_id != -1 ? HistoryFragment.newInstance(follower_id, true) : HistoryFragment.newInstance(wca_id, true);

                case 2:
                    return follower_id != -1 ? HistoryFragment.newInstance(follower_id, false) : HistoryFragment.newInstance(wca_id, false);

                default:
                    return follower_id != -1 ? RecordFragment.newInstance(follower_id) : RecordFragment.newInstance(wca_id);
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.records);

                case 1:
                    return getString(R.string.history);

                case 2:
                    return getString(R.string.competitions);

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }


    }

    private void updateProfileInfos() {

        //activity.setTitle(username);
        activity.setSubtitle(username);
    }

    @Override
    public void onDestroyView() {
        if(tabLayout != null) {
            tabLayout.setVisibility(View.GONE);
        }
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(EditRecordService.INTENT_FILTER));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}
