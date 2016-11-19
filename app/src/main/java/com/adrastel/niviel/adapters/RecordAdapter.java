package com.adrastel.niviel.adapters;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Cubes;
import com.adrastel.niviel.assets.DetailsMaker;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.dialogs.RecordDialog;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.models.readable.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;

public class RecordAdapter extends WebAdapter<RecordAdapter.ViewHolder, Record> {

    private User user;

    public RecordAdapter(FragmentActivity activity) {
        super(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.event) TextView event;
        @BindView(R.id.record) TextView single;
        @BindView(R.id.cube_image) ImageView image;
        @BindView(R.id.more_info) Button more_info;
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

        // Si il s'agit du header ou non
        if(isHeader(position)) {
            if(user != null) {

                Resources resources = getActivity().getResources();

                holder.event.setText(String.format(getActivity().getString(R.string.two_infos), user.getName(), user.getWca_id()));
                holder.event.setTextSize(Assets.spToPx(getActivity(), 9));
                holder.event.setGravity(Gravity.CENTER);
                holder.more_info.setText(R.string.wca);

                holder.more_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            Uri url = new WcaUrl()
                                    .profile(user.getWca_id())
                                    .toUri();

                            Intent viewOnWebSite = new Intent(Intent.ACTION_VIEW);
                            viewOnWebSite.setData(url);

                            Intent chooser = Intent.createChooser(viewOnWebSite, user.getName());

                            getActivity().startActivity(chooser);
                        }
                        catch (ActivityNotFoundException e ) {
                            e.printStackTrace();
                        }

                    }
                });


                DetailsMaker detailsMaker = new DetailsMaker(getActivity());

                try {
                    int competitions = Integer.parseInt(user.getCompetitions());
                    detailsMaker.add(user.getCompetitions() + " " + resources.getQuantityString(R.plurals.competitions, competitions));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                detailsMaker.br();
                detailsMaker.add(R.string.country, user.getCountry());
                detailsMaker.add(R.string.gender, user.getGender());


                holder.single.setText(detailsMaker.build());

                holder.image.setImageResource(R.drawable.ic_share);
                holder.image.setClickable(true);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String url = new WcaUrl()
                                .profile(user.getWca_id())
                                .toString();

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, url);

                        try {
                            getActivity().startActivity(Intent.createChooser(share, getActivity().getString(R.string.share)));
                        }
                        catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.error_activity, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });

            }

        }
        else {
            final int cubePosition = position - 1;
            final Record record = getDatas().get(cubePosition);

            final String event = record.getEvent();
            final String single = record.getSingle();
            final String average = record.getAverage();
            final int image_resource = Cubes.getImage(event);

            holder.event.setText(event);
            holder.event.setGravity(Gravity.LEFT);
            holder.event.setTextSize(Assets.spToPx(getActivity(), 18));
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setClickable(false);
            holder.image.setOnClickListener(null);
            holder.more_info.setText(R.string.more_info);

            DetailsMaker detailsMaker = new DetailsMaker(holder.single.getContext());

            detailsMaker.add(R.string.single, single);

            if (average != null && !average.equals("")) {
                detailsMaker.add(R.string.average, average);
            }

            try {

                int wr = Integer.parseInt(record.getWr_single());

                if(wr <= 300) {
                    detailsMaker.add(String.format(getActivity().getString(R.string.record_wr), ""), record.getWr_single());
                }

                else {

                    int cr = Integer.parseInt(record.getCr_single());

                    if(cr <= 300) {
                        detailsMaker.add(String.format(getActivity().getString(R.string.record_cr), ""), record.getCr_single());
                    }

                    else {
                        detailsMaker.add(String.format(getActivity().getString(R.string.record_nr), ""), record.getNr_single());
                    }
                }

            }

            catch (Exception e) {
                e.printStackTrace();
                detailsMaker.add(String.format(getActivity().getString(R.string.record_nr), ""), record.getNr_single());
            }

            holder.single.setText(detailsMaker.build(), TextView.BufferType.SPANNABLE);

            Picasso.with(getActivity())
                    .load(image_resource)
                    .fit()
                    .centerInside()
                    .into(holder.image);


            holder.more_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMoreInfoDialog(getActivity().getSupportFragmentManager(), record);
                }
            });
        }


    }

    private boolean isHeader(int position) {
        return position == 0;
    }

    /**
     * Si le tableau de possède aucun record, ne cree pas de header donc retourne 0.
     */
    @Override
    public int getItemCount() {
        int size = getDatas().size();

        return size != 0 ? size + 1 : 0;
    }

    public void refreshData(User user, ArrayList<Record> datas) {
        super.refreshData(datas);

        this.user = user;
        notifyItemChanged(0);
    }

    public User getUser() {
        return user;
    }

    private void showMoreInfoDialog(FragmentManager manager, Record record) {

        if(manager != null) {
            DialogFragment recordDialog = RecordDialog.newInstance(record);

            recordDialog.show(manager, Constants.TAG.RECORDS);

        }

    }
}
