package com.adrastel.niviel.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.models.readable.competition.Competition;
import com.adrastel.niviel.models.readable.competition.Title;
import com.adrastel.niviel.views.CircleView;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import java.util.ArrayList;

public class CompetitionAdapter extends BaseExpandableAdapter<Title, Competition, CompetitionAdapter.TitleViewHolder, CompetitionAdapter.CompetitionViewHolder> {

    private LayoutInflater inflater;

    public CompetitionAdapter(FragmentActivity activity, ArrayList<Title> datas) {
        super(activity, datas);

        inflater = activity.getLayoutInflater();
    }

    static class TitleViewHolder extends ParentViewHolder<Title, Competition> {

        public CircleView place;
        public TextView title;
        public TextView results;
        public ImageButton more;
        public ImageView cube;
        public LinearLayout root;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.list_place);
            title = (TextView) itemView.findViewById(R.id.first_line);
            results = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
            cube = (ImageView) itemView.findViewById(R.id.cube_image);
            root = (LinearLayout) itemView.findViewById(R.id.root_layout);
        }
    }

    static class CompetitionViewHolder extends ChildViewHolder<Competition> {

        public CircleView place;
        public TextView competition;
        public TextView location;
        public ImageButton more;
        public LinearLayout root;

        public CompetitionViewHolder(@NonNull View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.list_place);
            competition = (TextView) itemView.findViewById(R.id.first_line);
            location = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
            root = (LinearLayout) itemView.findViewById(R.id.root_layout);
        }
    }

    @NonNull
    @Override
    public TitleViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View parentView = inflater.inflate(R.layout.adapter_list_avatar, parentViewGroup, false);

        return new TitleViewHolder(parentView);
    }

    @NonNull
    @Override
    public CompetitionViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View childView = inflater.inflate(R.layout.adapter_list_avatar, childViewGroup, false);

        return new CompetitionViewHolder(childView);
    }

    @SuppressLint("PrivateResource")
    @SuppressWarnings("deprecation")
    @Override
    public void onBindParentViewHolder(@NonNull TitleViewHolder parentViewHolder, int parentPosition, @NonNull Title parent) {

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

        cube.setImageResource(R.drawable.ic_competition);
        cube.setColorFilter(0x00000000);
    }

    @Override
    public void onBindChildViewHolder(@NonNull CompetitionViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Competition child) {

        childViewHolder.competition.setText(child.getCompetition());
        childViewHolder.location.setText(child.getCountry());

    }
}
