package com.adrastel.niviel.adapters;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.DetailsMaker;
import com.adrastel.niviel.assets.IntentHelper;
import com.adrastel.niviel.dialogs.RecordDialog;
import com.adrastel.niviel.fragments.html.HistoryFragment;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.providers.html.RecordProvider;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordAdapter extends WebAdapter<RecordAdapter.ViewHolder, Record> {

    private String wca_id = null;

    public RecordAdapter(FragmentActivity activity) {
        super(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.event) TextView event;
        @BindView(R.id.record) TextView single;
        @BindView(R.id.cube_image) ImageView image;
        @BindView(R.id.more_info) Button more_info;
        @BindView(R.id.goto_history) Button history;
        @BindView(R.id.card) CardView card;


        // view holder
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // constructeur


    // lors de la creation de la vue
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_record, parent, false);

        return new ViewHolder(view);
    }

    // Lors de l'hydratation de la vue
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Record record = getDatas().get(position);



        final String event = record.getEvent();
        final String single = record.getSingle();
        final String average = record.getAverage();
        final int image_resource = RecordProvider.getImage(event);

        holder.event.setText(event);

        DetailsMaker detailsMaker = new DetailsMaker(holder.single.getContext());

        detailsMaker.add(R.string.single, single);

        if(!average.equals("")) {
            detailsMaker.add(R.string.average, average);
        }

        holder.single.setText(detailsMaker.build("#404040"), TextView.BufferType.SPANNABLE);

        Picasso.with(holder.image.getContext())
                .load(image_resource)
                .fit()
                .centerInside()
                .into(holder.image);
/*
        int[] colors = getActivity().getResources().getIntArray(R.array.colors_md_100);
        int color_position = position % colors.length;*/



        holder.card.setCardBackgroundColor(0xFFFF8A65);

        //listener
        holder.more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreInfoDialog(getActivity().getSupportFragmentManager(), record);
            }
        });


        if(record.getCompetitions() != null && record.getCompetitions().size() >= 0) {
            holder.history.setVisibility(View.VISIBLE);
        }

        holder.history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistory(record);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getDatas().size();
    }

    public void setWca_id(String wca_id) {
        this.wca_id = wca_id;
    }

    private void showHistory(Record record) {

        // Les parametres du fragment
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.EXTRAS.COMPETITIONS, record.getCompetitions());
        bundle.putString(Constants.EXTRAS.WCA_ID, wca_id);


        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(bundle);
        // todo voir l'histoire des compets
        IntentHelper.switchFragment(getActivity(), fragment);
    }

    private void showMoreInfoDialog(FragmentManager manager, Record record) {

        if(manager != null) {

            Bundle bundle = new Bundle();

            bundle.putParcelable(Constants.EXTRAS.RECORDS, record);

            DialogFragment recordDialog = new RecordDialog();
            recordDialog.setArguments(bundle);

            recordDialog.show(manager, Constants.TAG.RECORDS);

        }

    }


}
