package com.adrastel.niviel.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrastel.niviel.Models.History;
import com.adrastel.niviel.Models.Record;
import com.adrastel.niviel.R;
import com.adrastel.niviel.WCA.RecordProvider;
import com.adrastel.niviel.assets.DividerDecoration;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private ArrayList<Record> records;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView event;
        public TextView single;
        public TextView average;
        public ImageView image;
        public Button more_info;
        public Button history;


        // view holder
        public ViewHolder(View itemView) {
            super(itemView);

            event = (TextView) itemView.findViewById(R.id.adapter_profile_event);
            single = (TextView) itemView.findViewById(R.id.adapter_profile_single);
            average = (TextView) itemView.findViewById(R.id.adapter_profile_average);
            image = (ImageView) itemView.findViewById(R.id.adapter_profile_image);
            more_info = (Button) itemView.findViewById(R.id.adapter_profile_more_info);
            history = (Button) itemView.findViewById(R.id.adapter_profile_history);

        }
    }

    // constructeur
    public ProfileAdapter(ArrayList<Record> records) {
        this.records = records;
    }

    // lors de la creation de la vue
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_profile, parent, false);

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

        holder.more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreInfoDialog(view.getContext(), record);
            }
        });

        if(record.getCompetitions().size() >= 0) {
            holder.history.setVisibility(View.VISIBLE);
        }

        holder.history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistoryDialog(view.getContext(), record);
            }
        });

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    private void showMoreInfoDialog(Context context, Record record) {


        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.dialog_personal_record_info, null);

        TextView single = (TextView) view.findViewById(R.id.dialog_personal_record_info_single);
        TextView nr_single = (TextView) view.findViewById(R.id.dialog_personal_record_info_single_nr);
        TextView cr_single = (TextView) view.findViewById(R.id.dialog_personal_record_info_single_cr);
        TextView wr_single = (TextView) view.findViewById(R.id.dialog_personal_record_info_single_wr);

        TextView average = (TextView) view.findViewById(R.id.dialog_personal_record_info_average);
        TextView nr_average = (TextView) view.findViewById(R.id.dialog_personal_record_info_average_nr);
        TextView cr_average = (TextView) view.findViewById(R.id.dialog_personal_record_info_average_cr);
        TextView wr_average = (TextView) view.findViewById(R.id.dialog_personal_record_info_average_wr);

        single.setText(record.getSingle());
        nr_single.setText(record.getNr_single());
        cr_single.setText(record.getCr_single());
        wr_single.setText(record.getWr_single());

        average.setText(record.getAverage());
        nr_average.setText(record.getNr_average());
        cr_average.setText(record.getCr_average());
        wr_average.setText(record.getWr_average());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(record.getEvent());

        builder.setView(view);

        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void showHistoryDialog(Context context, Record record) {

        ArrayList<History> histories = record.getCompetitions();

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.dialog_history, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dialog_history_recycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.addItemDecoration(new DividerDecoration(context));

        HistoryAdapter adapter = new HistoryAdapter(histories);
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(record.getEvent());

        // todo: resoudre le pb de l'adapter
        builder.setView(view);

        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }


}
