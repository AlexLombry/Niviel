package com.adrastel.niviel.database;

import android.support.annotation.NonNull;

import com.adrastel.niviel.FollowerModel;
import com.adrastel.niviel.models.BaseModel;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Follower extends BaseModel implements FollowerModel {

    public static final Factory<Follower> FACTORY = new Factory<>(new Creator<Follower>() {
        @Override
        public Follower create(long _id, @NonNull String name, @NonNull String wca_id, long created_at) {
            return new AutoValue_Follower(_id, name, wca_id, created_at);
        }
    });

    public static final RowMapper<Follower> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
