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
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.IntentHelper;
import com.adrastel.niviel.dialogs.EditProfileFollowDialog;
import com.adrastel.niviel.fragments.html.HistoryFragment;
import com.adrastel.niviel.fragments.html.RecordFragment;
import com.adrastel.niviel.services.CheckRecordService;
import com.adrastel.niviel.services.EditRecordService;

public class ProfileFragment extends BaseFragment{

    public static final int RECORD_TAB = 0;
    public static final int HISTORY_TAB = 1;
    public static final String FRAGMENT_TAG = "profileFragment";

    private MainActivity activity;
    private TabLayout tabLayout;

    private String wca_id = null;
    private String username = null;
    private int tab = RECORD_TAB;

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

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

            wca_id = args.getString(Constants.EXTRAS.WCA_ID, null);
            username = args.getString(Constants.EXTRAS.USERNAME, null);
            tab = args.getInt(Constants.EXTRAS.TAB, RECORD_TAB);
        }

        /*Intent service = new Intent(getContext(), CheckRecordsReceiver.class);
        getContext().sendBroadcast(service);*/

        getContext().startService(new Intent(getContext(), CheckRecordService.class));
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
        getProfileInfos();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!Assets.isPersonal(activity, wca_id) && !Assets.isFollowing(activity, wca_id)) {
            activity.showFab();

            activity.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EditProfileFollowDialog dialog = EditProfileFollowDialog.getInstance(username);
                    dialog.setTargetFragment(ProfileFragment.this, 0);
                    dialog.show(getFragmentManager(), "setProfileDialog");

                    dialog.setOnDialogClickListener(new EditProfileFollowDialog.DialogProfileListener() {
                        @Override
                        public void onFollow() {

                            Intent follow = new Intent(getContext(), EditRecordService.class);

                            follow.putExtra(EditRecordService.ACTION, EditRecordService.ADD_RECORD);
                            follow.putExtra(EditRecordService.USERNAME, username);
                            follow.putExtra(EditRecordService.WCA_ID, wca_id);

                            getContext().startService(follow);

                        }

                        @Override
                        public void onEdit() {

                            boolean saved = preferences
                                    .edit()
                                    .putString(getString(R.string.pref_wca_id), wca_id)
                                    .putString(getString(R.string.pref_wca_username), username)
                                    .commit();

                            if (saved) {
                                activity.hideFab();
                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(IntentHelper.sendBroadcast(MainActivity.UPDATE_WCA_PROFILE));
                                Toast.makeText(activity, R.string.toast_edit_profile_success, Toast.LENGTH_LONG).show();
                            }

                            else {
                                Toast.makeText(activity, R.string.toast_edit_profile_failure, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            });
        }


    }

    @SuppressWarnings("ConstantConditions")
    private void setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(tab);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_star);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_history);

    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Profile;
    }


    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return RecordFragment.newInstance(wca_id);

                case 1:
                    return HistoryFragment.newInstance(wca_id, username);

                default:
                    return RecordFragment.newInstance(wca_id);
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.records);

                case 1:
                    return getString(R.string.history);

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }


    }

    private void getProfileInfos() {

        activity.setTitle(username);
        activity.setSubtitle(wca_id);
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
