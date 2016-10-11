package com.adrastel.niviel.adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Cubes;
import com.adrastel.niviel.assets.IntentHelper;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.dialogs.HistoryDialog;
import com.adrastel.niviel.models.readable.Event;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.providers.html.RecordProvider;
import com.adrastel.niviel.views.CircleView;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * HistoryAdapter organise les donnes données par un objet histor
 */
public class HistoryAdapter extends BaseExpandableAdapter<Event, History, HistoryAdapter.EventViewHolder, HistoryAdapter.HistoryViewHolder> {

    private LayoutInflater inflater;

    public HistoryAdapter(FragmentActivity activity, ArrayList<Event> events) {
        super(activity, events);

        inflater = LayoutInflater.from(activity);
    }

    static class EventViewHolder extends ParentViewHolder<Event, History> {

        public CircleView place;
        public TextView title;
        public TextView results;
        public ImageButton more;
        public ImageView cube;

        public EventViewHolder(View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.list_place);
            title = (TextView) itemView.findViewById(R.id.first_line);
            results = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
            cube = (ImageView) itemView.findViewById(R.id.cube_image);
        }
    }

    static class HistoryViewHolder extends ChildViewHolder<History> {

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

    @NonNull
    @Override
    public EventViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View parentView = inflater.inflate(R.layout.adapter_list_avatar, parentViewGroup, false);

        return new EventViewHolder(parentView);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View childView = inflater.inflate(R.layout.adapter_list_avatar, childViewGroup, false);

        return new HistoryViewHolder(childView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull final EventViewHolder parentViewHolder, int parentPosition, @NonNull Event parent) {
        parentViewHolder.results.setVisibility(View.GONE);
        parentViewHolder.place.setVisibility(View.GONE);

        TextView title = parentViewHolder.title;
        title.setText(parent.getTitle());
        title.setPadding(Assets.dpToPx(getActivity(), 20), 0, 0, 0);
        title.setTextSize(Assets.dpToPx(getActivity(), 16));
        title.setTextColor(0xFF444444);

        ImageView cube = parentViewHolder.cube;
        cube.setVisibility(View.VISIBLE);

        Picasso.with(getActivity())
                .load(Cubes.getImage(parent.getTitle()))
                .fit()
                .centerInside()
                .into(cube);

        parentViewHolder.more.setImageResource(R.drawable.ic_up);
        parentViewHolder.more.setClickable(false);
        parentViewHolder.more.setBackgroundColor(0x00000000);

        setExpandCollapseListener(new ExpandCollapseListener() {
            @Override
            public void onParentExpanded(int parentPosition) {
                Log.d("expand");
                parentViewHolder.more.setImageResource(R.drawable.ic_down);
            }

            @Override
            public void onParentCollapsed(int parentPosition) {
                Log.d("collapse");
                parentViewHolder.more.setImageResource(R.drawable.ic_up);
            }
        });
    }

    @Override
    public void onBindChildViewHolder(@NonNull HistoryViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull History child) {

        String event = child.getEvent();
        String competition = child.getCompetition();
        String place = child.getPlace();
        String average = child.getAverage();
        String result_details = child.getResult_details();

        childViewHolder.competition.setText(Assets.formatHtmltitle(event, competition));
        childViewHolder.place.setText(place);
        childViewHolder.results.setText(Assets.formatHtmlAverageDetails(average, result_details));

        childViewHolder.place.setBackground(Assets.getColor(getActivity(), R.color.indigo_300));

        loadMenu(childViewHolder, child);
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
    private void loadMenu(HistoryViewHolder holder, final History history) {
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

    @Override
    public void collapseParent(@NonNull Event parent) {
        super.collapseParent(parent);
    }
}