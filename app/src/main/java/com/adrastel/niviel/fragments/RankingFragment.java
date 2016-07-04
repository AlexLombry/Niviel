package com.adrastel.niviel.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adrastel.niviel.Models.BaseModel;
import com.adrastel.niviel.Models.Ranking;
import com.adrastel.niviel.R;
import com.adrastel.niviel.WCA.RankingProvider;
import com.adrastel.niviel.adapters.RankingAdapter;
import com.adrastel.niviel.assets.Constants;
import com.android.volley.VolleyError;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class RankingFragment extends GenericFragment<Ranking, RankingAdapter> {

    private Activity activity;
    private RankingAdapter adapter = new RankingAdapter();

    /**
     * Lors de la creation de l'app
     * @param savedInstanceState bundle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
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


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_list_recycler);

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

        requestData();

    }

    /**
     * Retourne l'url de requete
     * @return url
     */
    @Override
    protected String getUrl() {
        return "https://www.worldcubeassociation.org/results/events.php?eventId=333&regionId=&years=&show=100%2BPersons&single=Single";
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
    public String getTitle() {
        return activity != null ? activity.getString(R.string.ranking) : null;
    }

    /**
     * Fait une requete HTTP
     */
    private void requestData() {

        super.requestData(activity, new requestDataCallback() {
            @Override
            public ArrayList<? extends BaseModel> parseDatas(Document document) {
                return RankingProvider.getRanking(document);
            }

            @Override
            public void onSuccess(ArrayList<? extends BaseModel> datas) {

            }

            @Override
            public void onError(VolleyError error) {

            }

            @Override
            public void postRequest() {

            }
        });
    }

}
