package com.adrastel.niviel.adapters;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.adrastel.niviel.assets.DetailsMaker;
import com.adrastel.niviel.assets.IntentHelper;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.html.HistoryFragment;
import com.adrastel.niviel.providers.RecordProvider;
import com.adrastel.niviel.assets.BackgroundCards;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.dialogs.RecordDialog;
import com.adrastel.niviel.models.readable.Record;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

public class RecordAdapter extends BaseAdapter<RecordAdapter.ViewHolder> {

    private ArrayList<Record> records;
    private BackgroundCards backgroundCards = new BackgroundCards(4865);
    private ArrayList<Integer> colors = backgroundCards.shuffle();
    private String wca_id = null;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView event;
        public TextView single;
        public ImageView image;
        public Button more_info;
        public Button history;
        public CardView card;


        // view holder
        public ViewHolder(View itemView) {
            super(itemView);

            event = (TextView) itemView.findViewById(R.id.event);
            single = (TextView) itemView.findViewById(R.id.record);
            image = (ImageView) itemView.findViewById(R.id.cube_image);
            more_info = (Button) itemView.findViewById(R.id.more_info);
            history = (Button) itemView.findViewById(R.id.goto_history);
            card = (CardView) itemView.findViewById(R.id.card);

        }
    }

    // constructeur
    public RecordAdapter(ArrayList<Record> records) {
        this.records = records;
    }

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

        final Record record = records.get(position);



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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final RequestCreator image = Picasso.with(holder.image.getContext())
                            .load(image_resource)
                            .fit()
                            .centerInside();

                    holder.image.post(new Runnable() {
                        @Override
                        public void run() {
                            image.into(holder.image);
                        }
                    });
                }
                catch (Exception e) {
                    Log.e(e.getMessage());
                }
            }
        }).start();
//        Picasso.with(holder.image.getContext())
//                .load(image_resource)
//                .fit()
//                .centerInside()
//                .into(holder.image);


        // background
        int color_position = backgroundCards.get(position);
        int color = colors.get(color_position);
        holder.card.setCardBackgroundColor(color);


        // listener
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
        return records.size();
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
