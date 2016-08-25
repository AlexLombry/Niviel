package com.adrastel.niviel.fragments.html;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.RankingAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.dialogs.RankingSwitchCubeDialog;
import com.adrastel.niviel.models.BaseModel;
import com.adrastel.niviel.models.readable.Ranking;
import com.adrastel.niviel.providers.RankingProvider;
import com.android.volley.VolleyError;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RankingFragment extends HtmlFragment<Ranking, RankingAdapter> implements RankingSwitchCubeDialog.RankingSwitchCubeListener {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private Activity activity;
    private ConnectivityManager connectivityManager;
    private Unbinder unbinder;
    private RankingAdapter adapter = new RankingAdapter(getDatas());

    // cubePosition est une variable qui permet d'identifier sur quelle rubrique on est
    private int cubePosition = 0;

    // issingle permet de savoir si on veut les single ou average
    private boolean isSingle = true;

    /**
     * Lors de la creation de l'app
     * @param savedInstanceState bundle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        activity = getActivity();
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
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

        recyclerView.setAdapter(getAdapter());

        return view;
    }

    /**
     * Quand l'activité est créee
     * @param savedInstanceState bundle
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            cubePosition = savedInstanceState.getInt(Constants.EXTRAS.POSITION, 0);
            isSingle = savedInstanceState.getBoolean(Constants.EXTRAS.ISSINGLE, true);

            ArrayList<Ranking> rankings = savedInstanceState.getParcelableArrayList(Constants.EXTRAS.RANKING);

            getAdapter().setSingle(isSingle);

            refreshData(rankings);

            stopLoaders();

        }


        else if(isConnected()) {
            requestData();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(Constants.EXTRAS.POSITION, cubePosition);
        outState.putBoolean(Constants.EXTRAS.ISSINGLE, isSingle);
        outState.putParcelableArrayList(Constants.EXTRAS.RANKING, getDatas());

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


                Bundle bundle = new Bundle();
                bundle.putInt(Constants.EXTRAS.POSITION, cubePosition);

                DialogFragment dialog = new RankingSwitchCubeDialog();
                dialog.setArguments(bundle);
                dialog.setTargetFragment(this, 0);
                dialog.show(getFragmentManager(), Constants.TAG.RANKING);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Retourne l'url de requete
     * @return url
     */
    @Override
    protected String getUrl() {

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https");
        builder.authority("www.worldcubeassociation.org");
        builder.appendEncodedPath("results/events.php");
        builder.appendQueryParameter("eventId", Assets.getCubeId(cubePosition));

        if(!isSingle) {
            builder.appendQueryParameter("average", "Average");
        }

        else {
            builder.appendQueryParameter("single", "Single");
        }

        return builder.build().toString();
    }

    /**
     * Retourne l'adapter utilisé
     * @return adapter
     */
    @Override
    protected RankingAdapter getAdapter() {
        return adapter;
    }

    /**
     * Retourne l'emplacement de stockage utilisé
     * @return stockage
     */
    @Override
    protected String getStorage() {
        return Constants.STORAGE.RANKING;
    }

    /**
     * Retoune le titre du fragment
     * @return titre
     */
    @Override
    public int getTitle() {
        return R.string.ranking;
    }

    @Override
    public int getPrimaryColor() {
        return R.color.red;
    }

    @Override
    public int getPrimaryDarkColor() {
        return R.color.redDark;
    }

    private void setSubtitle(String subtitle) {

        if(activityTunnelInterface != null) {
            activityTunnelInterface.setSubtitle(subtitle);
        }
    }

    @Override
    public void onClick(int cubePosition, boolean isSingle) {

        this.cubePosition = cubePosition;
        this.isSingle = isSingle;

        requestData();

    }


    /**
     * Fait une requete HTTP
     */
    private void requestData() {

        getAdapter().setSingle(isSingle);

        String[] cubes = getResources().getStringArray(R.array.cubes);
        String mode = isSingle ? getString(R.string.single) : getString(R.string.average);
        String subtitle = String.format(getString(R.string.ranking_subtitle), cubes[cubePosition], mode);

        setSubtitle(subtitle);

        super.requestData(activity, new requestDataCallback() {
            @Override
            public ArrayList<? extends BaseModel> parseDatas(Document document) {
                return RankingProvider.getRanking(activity, document);
            }

            @Override
            public void onSuccess(ArrayList<? extends BaseModel> datas) {
                refreshData((ArrayList<Ranking>) datas);
            }

            @Override
            public void onError(VolleyError error) {

            }

            @Override
            public void postRequest() {
                stopLoaders();
            }
        });
    }

    private void stopLoaders() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

}