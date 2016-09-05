package com.adrastel.niviel.adapters;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.IntentHelper;
import com.adrastel.niviel.dialogs.HistoryDialog;
import com.adrastel.niviel.fragments.html.RecordFragment;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.views.CircleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * HistoryAdapter organise les donnes données par un objet histor
 */
public class HistoryAdapter extends WebAdapter<HistoryAdapter.ViewHolder, History> {

    private String wca_id = null;

    public HistoryAdapter(FragmentActivity activity) {
        super(activity);
    }

    public HistoryAdapter(FragmentActivity activity, String wca_id) {
        super(activity);
        this.wca_id = wca_id;
    }


    // Le view holder qui contient toutes les infos
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_place) CircleView place;
        @BindView(R.id.first_line) TextView competition;
        @BindView(R.id.second_line) TextView results;
        @BindView(R.id.more) ImageButton more;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Lors de la creation de la vue
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

        return new ViewHolder(view);
    }

    // Lors de l'hydratation de la vue
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final History history = getDatas().get(position);

        String competition = history.getCompetition();
        String place = history.getPlace();
        String average = history.getAverage();
        String result_details = history.getResult_details();

        holder.competition.setText(competition);
        holder.place.setText(place);
        holder.results.setText(Assets.formatHtmlAverageDetails(average, result_details));

        loadMenu(holder, history);


    }

    @Override
    public int getItemCount() {
        return getDatas().size();
    }

    private void loadMenu(ViewHolder holder, final History history) {
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.menu_pop_history);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.details:
                                onDetails(getActivity().getSupportFragmentManager(), history);
                                return true;

                            case R.id.share:
                                onShare(history);
                                return true;

                            case R.id.goto_records:
                                gotoRecords();
                                return true;

                        }

                        return false;
                    }
                });
                popupMenu.show();

            }
        });
    }

    private void gotoRecords() {

        if(wca_id != null) {
            RecordFragment fragment = RecordFragment.newInstance(wca_id);
            IntentHelper.switchFragment(getActivity(), fragment);
        }
    }

    private void onDetails(FragmentManager fragmentManager, History history) {

        if(fragmentManager != null) {



            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.EXTRAS.HISTORY, history);

            DialogFragment historyDialog = new HistoryDialog();

            historyDialog.setArguments(bundle);

            historyDialog.show(fragmentManager, Constants.TAG.HISTORY);

        }

    }

    private void onShare(History history) {


        String shareFormat = getActivity().getString(R.string.share_history);

        String text = String.format(shareFormat, history.getPlace(), history.getCompetition(), history.getAverage());

        String html = String.format(shareFormat, Assets.wrapStrong(history.getPlace()),
                Assets.wrapStrong(history.getCompetition()), Assets.wrapStrong(history.getAverage()));


        IntentHelper.shareIntent(getActivity(), text, html);
    }

}