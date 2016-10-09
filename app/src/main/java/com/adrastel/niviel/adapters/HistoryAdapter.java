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
import com.adrastel.niviel.models.readable.Event;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.views.CircleView;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * HistoryAdapter organise les donnes données par un objet histor
 */
public class HistoryAdapter extends BaseExpandableAdapter<HistoryAdapter.EventViewHolder, HistoryAdapter.HistoryViewHolder> {

    private LayoutInflater inflater;

    public HistoryAdapter(FragmentActivity activity, ArrayList<Event> events) {
        super(activity, events);

        inflater = LayoutInflater.from(activity);
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

    public static class EventViewHolder extends ParentViewHolder {

        public TextView title;

        public EventViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.first_line);
        }
    }

    public static class HistoryViewHolder extends ChildViewHolder {

        public CircleView place;
        public TextView competition;
        public TextView results;
        public ImageButton more;

        public HistoryViewHolder(View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.list_place);
            competition = (TextView) itemView.findViewById(R.id.first_line);
            results = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
        }
    }

    // Lors de la creation de la vue
    /*@Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

        return new ViewHolder(view);
    }*/

    @Override
    public EventViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View parentView = inflater.inflate(R.layout.adapter_list_avatar, parentViewGroup, false);

        return new EventViewHolder(parentView);
    }

    @Override
    public HistoryViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View childView = inflater.inflate(R.layout.adapter_list_avatar, childViewGroup, false);

        return new HistoryViewHolder(childView);
    }

    @Override
    public void onBindParentViewHolder(EventViewHolder parentViewHolder, int position, ParentListItem parentListItem) {

        Event event = (Event) parentListItem;

        parentViewHolder.title.setText(event.getTitle());
    }

    @Override
    public void onBindChildViewHolder(HistoryViewHolder childViewHolder, int position, Object childListItem) {

        History history = (History) childListItem;

        String event = history.getEvent();
        String competition = history.getCompetition();
        String place = history.getPlace();
        String average = history.getAverage();
        String result_details = history.getResult_details();

        childViewHolder.competition.setText(Assets.formatHtmltitle(event, competition));
        childViewHolder.place.setText(place);
        childViewHolder.results.setText(Assets.formatHtmlAverageDetails(average, result_details));

        childViewHolder.place.setBackground(Assets.getColor(getActivity(), R.color.indigo_300));
    }

 /*   // Lors de l'hydratation de la vue
    //@Override
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


    }*/
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

    // todo: changer la phrase shareFormat
    private void onShare(History history) {


        String shareFormat = getActivity().getString(R.string.share_history);

        String text = String.format(shareFormat, history.getPlace(), history.getCompetition(), history.getAverage());

        String html = String.format(shareFormat, Assets.wrapStrong(history.getPlace()),
                Assets.wrapStrong(history.getCompetition()), Assets.wrapStrong(history.getAverage()));


        IntentHelper.shareIntent(getActivity(), text, html);
    }

}