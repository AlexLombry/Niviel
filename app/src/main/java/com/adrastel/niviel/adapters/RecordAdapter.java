package com.adrastel.niviel.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.adrastel.niviel.WCA.RecordProvider;
import com.adrastel.niviel.activities.FragmentActivity;
import com.adrastel.niviel.activities.HistoryActivity;
import com.adrastel.niviel.assets.BackgroundCards;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.dialogs.RecordDialog;
import com.adrastel.niviel.models.Record;

import java.util.ArrayList;

public class RecordAdapter extends BaseAdapter<RecordAdapter.ViewHolder> {

    private ArrayList<Record> records;
    private BackgroundCards backgroundCards = new BackgroundCards(4865);
    private ArrayList<Integer> colors = backgroundCards.shuffle();
    private FragmentManager manager;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView event;
        public TextView single;
        public TextView average;
        public ImageView image;
        public Button more_info;
        public Button history;
        public CardView card;


        // view holder
        public ViewHolder(View itemView) {
            super(itemView);

            event = (TextView) itemView.findViewById(R.id.adapter_profile_event);
            single = (TextView) itemView.findViewById(R.id.adapter_profile_single);
            average = (TextView) itemView.findViewById(R.id.adapter_profile_average);
            image = (ImageView) itemView.findViewById(R.id.adapter_profile_image);
            more_info = (Button) itemView.findViewById(R.id.adapter_profile_more_info);
            history = (Button) itemView.findViewById(R.id.adapter_profile_history);
            card = (CardView) itemView.findViewById(R.id.adapter_profile_card);

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
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Record record = records.get(position);



        String event = record.getEvent();
        String single = record.getSingle();
        String average = record.getAverage();
        int image_resource = RecordProvider.getImage(event);

        holder.event.setText(event);
        holder.single.setText(single);
        holder.average.setText(average);
        holder.image.setImageResource(image_resource);


        // background
        int color_position = backgroundCards.get(position);
        int color = colors.get(color_position);
        holder.card.setCardBackgroundColor(color);


        // listener
        holder.more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreInfoDialog(manager, record);
            }
        });

        if(record.getCompetitions() != null && record.getCompetitions().size() >= 0) {
            holder.history.setVisibility(View.VISIBLE);
        }

        holder.history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistory(view.getContext(), record);
            }
        });

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }

    private void showHistory(Context context, Record record) {

        // Les parametres du fragment
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.EXTRAS.RECORDS, record.getCompetitions());

        // Les parametres de l'activity
        Intent intent = new Intent(context, FragmentActivity.class);
        intent.putExtra(Constants.EXTRAS.ID, R.id.nav_history);
        intent.putExtra(Constants.EXTRAS.BUNDLE, bundle);

        context.startActivity(intent);
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
