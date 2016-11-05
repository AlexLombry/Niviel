package com.adrastel.niviel.fragments.html;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.RankingAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.dialogs.RankingSwitchCountryDialog;
import com.adrastel.niviel.dialogs.RankingSwitchCubeDialog;
import com.adrastel.niviel.managers.HttpManager;
import com.adrastel.niviel.models.readable.Ranking;
import com.adrastel.niviel.providers.html.RankingProvider;
import com.adrastel.niviel.views.RecyclerViewCompat;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class RankingFragment extends HtmlFragment<Ranking> implements RankingSwitchCubeDialog.RankingSwitchCubeListener {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view) RecyclerViewCompat recyclerView;
    @BindView(R.id.empty_view) View emptyView;

    private Unbinder unbinder;
    private RankingAdapter adapter;
    private HttpManager httpManager;

    // cubePosition est une variable qui permet d'identifier sur quelle rubrique on est
    private int cubePosition = 0;
    private int countryPosition = 0;

    // issingle permet de savoir si on veut les single ou average
    private boolean isSingle = true;
    private String subtitle = null;

    /**
     * Lors de la creation de l'app
     * @param savedInstanceState bundle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        adapter = new RankingAdapter(getActivity());

    }

    /**
     * Lors de la creation de la vue
     * @param inflater inflater
     * @param container parent
     * @param savedInstanceState bindle
     * @return vue
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);

        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);


        return view;
    }

    /**
     * Quand l'activité est créee
     * @param savedInstanceState bundle
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        httpManager = new HttpManager(activity, swipeRefreshLayout, progressBar);

        if(savedInstanceState != null) {
            cubePosition = savedInstanceState.getInt(Constants.EXTRAS.CUBE_POSITION, 0);
            isSingle = savedInstanceState.getBoolean(Constants.EXTRAS.ISSINGLE, true);

            subtitle = savedInstanceState.getString(Constants.EXTRAS.SUBTITLE, null);
            activity.setSubtitle(subtitle);

            ArrayList<Ranking> rankings = savedInstanceState.getParcelableArrayList(Constants.EXTRAS.RANKING);

            adapter.setSingle(isSingle);

            adapter.refreshData(rankings);

            stopLoaders();

        }


        else if(isConnected()) {
            callData();
        }

        else {
            stopLoaders();
            makeSnackbar(R.string.error_connection, Snackbar.LENGTH_INDEFINITE).show();
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callData();
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        adapter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(Constants.EXTRAS.CUBE_POSITION, cubePosition);
        outState.putBoolean(Constants.EXTRAS.ISSINGLE, isSingle);
        outState.putString(Constants.EXTRAS.SUBTITLE, subtitle);
        outState.putParcelableArrayList(Constants.EXTRAS.RANKING, adapter.getDatas());

        super.onSaveInstanceState(outState);
    }

    /**
     * Ajoute le menu du fragment à celui de l'activité
     * @param menu menu
     * @param inflater inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ranking, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.switch_cube:


                DialogFragment cubeSwitch = RankingSwitchCubeDialog.newInstance(cubePosition);
                cubeSwitch.setTargetFragment(this, 0);
                cubeSwitch.show(getFragmentManager(), "cubeSwitch");

                return true;

            case R.id.switch_country:

                RankingSwitchCountryDialog countrySwitch = RankingSwitchCountryDialog.newInstance(countryPosition);
                countrySwitch.show(getFragmentManager(), "countrySwitch");

                countrySwitch.setListener(new RankingSwitchCountryDialog.RankingSwitchCountryListener() {
                    @Override
                    public void onClick(int position) {
                        countryPosition = position;
                        callData();
                    }
                });

                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Retourne l'emplacement de stockage utilisé
     * @return stockage
     */
    @Override
    public String getStorageLocation() {
        return Constants.STORAGE.RANKING;
    }

    @Override
    public Type getStorageType() {
        return new TypeToken<ArrayList<Ranking>>(){}.getType();
    }

    @Override
    public void callData() {

        swipeRefreshLayout.setRefreshing(true);

        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addEncodedPathSegments("results/events.php")
                .addEncodedQueryParameter("eventId", Assets.getCubeId(cubePosition))
                .addEncodedQueryParameter("regionId", getResources().getStringArray(R.array.countries_id)[countryPosition]);

        if(!isSingle) {
            builder.addEncodedQueryParameter("average", "Average");
        }

        else {
            builder.addEncodedQueryParameter("single", "Single");
        }

        adapter.setSingle(isSingle);

        String[] cubes = getResources().getStringArray(R.array.cubes);
        String mode = isSingle ? getString(R.string.single) : getString(R.string.average);
        subtitle = String.format(getString(R.string.two_infos), cubes[cubePosition], mode);

        activity.setSubtitle(subtitle);

        httpManager.callData(builder.build(), new HttpManager.SuccessCallback() {
            @Override
            public void onSuccess(String response) {
                Document document = Jsoup.parse(response);
                final ArrayList<Ranking> rankings = RankingProvider.getRanking(getActivity(), document);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(rankings);
                        httpManager.stopLoaders();
                    }
                });
            }
        });
    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Ranking;
    }

    @Override
    public void onClick(int cubePosition, boolean isSingle) {

        this.cubePosition = cubePosition;
        this.isSingle = isSingle;

        callData();

    }
    private void stopLoaders() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

}