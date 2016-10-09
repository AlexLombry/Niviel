package com.adrastel.niviel.fragments.html;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.RecordAdapter;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.managers.HttpManager;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.providers.html.RecordProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class RecordFragment extends BaseFragment {


    public static final String RECORDS = "records";

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private Unbinder unbinder;

    private HttpManager httpManager;

    private RecordAdapter adapter;

    private String wca_id;
    private long follower_id = -1;

    public static RecordFragment newInstance(long id) {

        RecordFragment instance = new RecordFragment();

        Bundle args = new Bundle();
        args.putLong(Constants.EXTRAS.ID , id);

        instance.setArguments(args);

        return instance;

    }

    public static RecordFragment newInstance(String wca_id) {

        RecordFragment instance = new RecordFragment();

        Bundle args = new Bundle();
        args.putString(Constants.EXTRAS.WCA_ID ,wca_id);

        instance.setArguments(args);

        return instance;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new RecordAdapter(getActivity());

        wca_id = null;

        // On recupere l'follower_id wca

        Bundle arguments = getArguments();

        if(arguments != null) {
            follower_id = arguments.getLong(Constants.EXTRAS.ID, -1);

            wca_id = follower_id == -1 ? arguments.getString(Constants.EXTRAS.WCA_ID, null) : null;
        }
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

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);


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

        httpManager = new HttpManager(getActivity(), swipeRefresh, progressBar);

        if (savedInstanceState != null) {
            adapter.refreshData(loadLocalData(savedInstanceState));
            httpManager.stopLoaders();
        }
        // Si on est connecté, on fait une requete HTTP, sinon on lit les données locales

        else if(follower_id != -1) {

            DatabaseHelper database = DatabaseHelper.getInstance(getContext());

            Follower follower = database.selectFollowerFromId(follower_id);

            this.wca_id = follower.wca_id();

            ArrayList<com.adrastel.niviel.database.Record> localRecords = database.selectRecordsFromFollower(follower._id());
            ArrayList<Record> records = new ArrayList<>();

            for(com.adrastel.niviel.database.Record record : localRecords) {
                records.add(record.toRecordModel());
            }

            adapter.refreshData(records);
            httpManager.stopLoaders();


        }
        else if (isConnected()) {
            callData();
        }

        else {
            Toast.makeText(getContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
            httpManager.stopLoaders();
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callData();
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

        outState.putParcelableArrayList(RECORDS, adapter.getDatas());
        super.onSaveInstanceState(outState);
    }

    protected ArrayList<Record> loadLocalData(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            return savedInstanceState.getParcelableArrayList(RECORDS);
        }

        return new ArrayList<>();
    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Records;
    }

    public void callData() {
        HttpUrl url = new HttpUrl.Builder()

                // https://www.worldcubeassociation.org/results/p.php?i=
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addPathSegments("results/p.php")
                .addEncodedQueryParameter("i", wca_id)
                .build();
        httpManager.callData(url, new HttpManager.SuccessCallback() {
            @Override
            public void onSuccess(String response) {

                Document document = Jsoup.parse(response);

                final ArrayList<Record> records = RecordProvider.getRecord(getActivity(), document);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(records);
                    }
                });
            }
        });
    }

}
