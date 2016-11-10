package com.adrastel.niviel.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Cubes;
import com.adrastel.niviel.dialogs.HistoryDialog;
import com.adrastel.niviel.models.readable.Event;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.views.CircleView;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Cet adapter gère l'historique du joueur. Il se base sur une liste etendable en fonction de l'event
 */

public class HistoryAdapter extends BaseExpandableAdapter<Event, History, HistoryAdapter.EventViewHolder, HistoryAdapter.HistoryViewHolder> {

    private LayoutInflater inflater;
    private boolean sortByEvent = true;

    public HistoryAdapter(FragmentActivity activity, ArrayList<Event> events) {
        super(activity, events);

        inflater = LayoutInflater.from(activity);
    }

    /**
     * Classe parent qui represente la catégorie du cube
     */
    @SuppressWarnings("WeakerAccess")
    static class EventViewHolder extends ParentViewHolder<Event, History> {

        public CircleView place;
        public TextView title;
        public TextView results;
        public ImageButton more;
        public ImageView cube;
        public LinearLayout root;

        public EventViewHolder(View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.list_place);
            title = (TextView) itemView.findViewById(R.id.first_line);
            results = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
            cube = (ImageView) itemView.findViewById(R.id.cube_image);
            root = (LinearLayout) itemView.findViewById(R.id.root_layout);
        }
    }

    /**
     * Classe enfant qui représente l'historique
     */
    @SuppressWarnings("WeakerAccess")
    static class HistoryViewHolder extends ChildViewHolder<History> {

        public CircleView place;
        public TextView competition;
        public TextView results;
        public ImageButton more;
        public LinearLayout root;

        public HistoryViewHolder(View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.list_place);
            competition = (TextView) itemView.findViewById(R.id.first_line);
            results = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
            root = (LinearLayout) itemView.findViewById(R.id.root_layout);
        }
    }

    /**
     * Inflate le layout parent
     * @param parentViewGroup Vue parente
     * @param viewType Type de la vue
     * @return vue enfant
     */
    @NonNull
    @Override
    public EventViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View parentView = inflater.inflate(R.layout.adapter_list_avatar, parentViewGroup, false);

        return new EventViewHolder(parentView);
    }

    /**
     * Inflate le layout enfant
     * @param childViewGroup Vue parent
     * @param viewType Type de la vue
     * @return Vue enfant
     */
    @NonNull
    @Override
    public HistoryViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View childView = inflater.inflate(R.layout.adapter_list_avatar, childViewGroup, false);

        return new HistoryViewHolder(childView);
    }

    /**
     * Lors de l'attachement de la vue parent
     *
     * Cache plusieurs vues
     * Ajoute un padding pour reiquilibre
     * Change la taille du titre
     * Charge la miniature
     *
     * @param parentViewHolder vue parent
     * @param parentPosition position du parent
     * @param parent objet parent
     */

    @Override
    @SuppressLint("PrivateResource")
    @SuppressWarnings("deprecation")
    public void onBindParentViewHolder(@NonNull final EventViewHolder parentViewHolder, int parentPosition, @NonNull Event parent) {

        // Comme tous les parents possède le meme sortByEvent, on peut mettre cette variable en global
        sortByEvent = parent.isSortByEvent();

        parentViewHolder.results.setVisibility(View.GONE);
        parentViewHolder.place.setVisibility(View.GONE);
        parentViewHolder.more.setVisibility(View.GONE);

        TextView title = parentViewHolder.title;
        title.setText(parent.getTitle());
        title.setPadding(Assets.dpToPx(getActivity(), 20), 0, 0, 0);
        title.setTextSize(Assets.spToPx(getActivity(), 12));

        title.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Large);

        ImageView cube = parentViewHolder.cube;
        cube.setVisibility(View.VISIBLE);


        if(sortByEvent) {
            Picasso.with(getActivity())
                    .load(Cubes.getImage(parent.getTitle()))
                    .fit()
                    .centerInside()
                    .into(cube);
        } else {
            cube.setImageResource(R.drawable.ic_star_black);
        }


    }

    /**
     * Lors de l'attachement de la vue enfant
     *
     * Change le background
     * Inflate les differents elements comme le titre, la place
     *
     * Charge le menu contextuel
     *
     * @param childViewHolder vue enfant
     * @param parentPosition position du parent
     * @param childPosition position de l'enfant
     * @param child object enfant
     */
    @Override
    public void onBindChildViewHolder(@NonNull HistoryViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull final History child) {

        String event = child.getEvent();
        String competition = child.getCompetition();
        String place = child.getPlace();
        String average = child.getAverage();
        String result_details = child.getResult_details();

        childViewHolder.more.setVisibility(View.GONE);

        childViewHolder.root.setBackgroundColor(Assets.getColor(getActivity(), R.color.background_child_expanded_recycler_view));

        childViewHolder.competition.setText(sortByEvent ? Assets.formatHtmltitle(event, competition) : event);

        childViewHolder.place.setText(place);
        childViewHolder.results.setText(Assets.formatHtmlAverageDetails(average, result_details));

        childViewHolder.place.setBackground(Assets.getColor(getActivity(), R.color.indigo_300));


        childViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDetails(getActivity().getSupportFragmentManager(), child);
            }
        });
    }

    /**
     * Charge un dialogue contenant les détails de l'historique
     * @param fragmentManager manager
     * @param history objet enfant
     */
    private void onDetails(FragmentManager fragmentManager, History history) {

        if(fragmentManager != null) {

            DialogFragment historyDialog = HistoryDialog.newInstance(history);

            historyDialog.show(fragmentManager, "historyDetailsDialog");

        }

    }

    /**
     * Appelé pour raffrechir les donnéees
     *
     * Trie les données avant de les raffrechir
     * @param datas données
     */
    @Override
    public void refreshData(ArrayList<Event> datas) {
        Collections.sort(datas, new Event.ComparatorByEvent());

        super.refreshData(datas);
    }
}