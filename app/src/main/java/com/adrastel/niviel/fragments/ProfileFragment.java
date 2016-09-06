package com.adrastel.niviel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.html.HistoryFragment;
import com.adrastel.niviel.fragments.html.RecordFragment;
import com.adrastel.niviel.http.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ProfileFragment extends BaseFragment {

    public static final int RECORD_TAB = 1;
    public static final int HISTORY_TAB = 2;

    private MainActivity activity;
    private TabLayout tabLayout;

    private String wca_id = null;
    private int tab = RECORD_TAB;

    public static ProfileFragment newInstance(int tab, String wca_id) {
        ProfileFragment instance = new ProfileFragment();

        Bundle args = new Bundle();

        args.putInt(Constants.EXTRAS.TAB, tab);
        args.putString(Constants.EXTRAS.WCA_ID, wca_id);

        instance.setArguments(args);

        return instance;
    }

    public static ProfileFragment newInstance(String wca_id) {
        return newInstance(0, wca_id);
    }

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
            Log.d("WCA 1", wca_id);
            tab = args.getInt(Constants.EXTRAS.TAB, RECORD_TAB);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if(activity != null) {

            try {
                ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);

                tabLayout = (TabLayout) activity.findViewById(R.id.tab_layout);
                tabLayout.setVisibility(View.VISIBLE);
                tabLayout.setupWithViewPager(viewPager);
                setupViewPager(viewPager);
                getProfileInfos();

            }

            catch (Exception e) {
                e.printStackTrace();
            }

        }

        return view;
    }

    @SuppressWarnings("ConstantConditions")
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_star);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_history);

    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Profile;
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
                    return new RecordFragment();

                case 1:
                    return new HistoryFragment();

                default:
                    return new RecordFragment();
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

        if (wca_id != null) {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host(Constants.WCA.HOST)
                    .addEncodedPathSegments("api/v0/users")
                    .addEncodedPathSegment(wca_id)
                    .build();


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new HttpCallback(getActivity()) {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject user = new JSONObject(response).getJSONObject("user");

                        final String name = user.getString("name");
                        Log.d(name);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ProfileFragment.this.activity.setTitle(name);
                                ProfileFragment.this.activity.setSubtitle(wca_id);

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        if(tabLayout != null) {
            tabLayout.setVisibility(View.GONE);
        }
        super.onDestroyView();
    }
}
