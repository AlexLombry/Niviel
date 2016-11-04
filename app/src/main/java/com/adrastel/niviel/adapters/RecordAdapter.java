package com.adrastel.niviel.adapters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Cubes;
import com.adrastel.niviel.assets.DetailsMaker;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.dialogs.RecordDialog;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.models.readable.SuggestionUser;
import com.adrastel.niviel.models.readable.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordAdapter extends WebAdapter<RecordAdapter.ViewHolder, Record> {

    private int lastPosition = -1;

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
                holder.image.setVisibility(View.GONE);
                holder.more_info.setVisibility(View.GONE);

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
            holder.more_info.setVisibility(View.VISIBLE);

            DetailsMaker detailsMaker = new DetailsMaker(holder.single.getContext());

            detailsMaker.add(R.string.single, single);

            if (average != null && !average.equals("")) {
                detailsMaker.add(R.string.average, average);
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

    public void setHeader(User user) {
        this.user = user;
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return getDatas().size() + 1;
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
