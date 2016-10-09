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

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.IntentHelper;
import com.adrastel.niviel.dialogs.HistoryDialog;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.views.CircleView;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * HistoryAdapter organise les donnes données par un objet histor
 */
public class HistoryAdapter extends BaseAdapter<HistoryAdapter.ViewHolder> implements ExpandableItemAdapter {

    private ArrayList<History> datas = new ArrayList<>();


    public void refreshData(ArrayList<History> datas) {

        this.datas.clear();
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    public ArrayList<History> getDatas() {
        return datas;
    }

    public HistoryAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildCount(int groupPosition) {
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindGroupViewHolder(RecyclerView.ViewHolder holder, int groupPosition, int viewType) {

    }

    @Override
    public void onBindChildViewHolder(RecyclerView.ViewHolder holder, int groupPosition, int childPosition, int viewType) {

    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(RecyclerView.ViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return false;
    }

    @Override
    public boolean onHookGroupExpand(int groupPosition, boolean fromUser) {
        return false;
    }

    @Override
    public boolean onHookGroupCollapse(int groupPosition, boolean fromUser) {
        return false;
    }

    @Override
    public void onBindChildViewHolder(RecyclerView.ViewHolder holder, int groupPosition, int childPosition, int viewType, List payloads) {

    }

    @Override
    public void onBindGroupViewHolder(RecyclerView.ViewHolder holder, int groupPosition, int viewType, List payloads) {

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

        String event = history.getEvent();
        String competition = history.getCompetition();
        String place = history.getPlace();
        String average = history.getAverage();
        String result_details = history.getResult_details();

        holder.competition.setText(Assets.formatHtmltitle(event, competition));
        holder.place.setText(place);
        holder.results.setText(Assets.formatHtmlAverageDetails(average, result_details));

        holder.place.setBackground(Assets.getColor(getActivity(), R.color.indigo_300));

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
                        }

                        return false;
                    }
                });
                popupMenu.show();

            }
        });
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