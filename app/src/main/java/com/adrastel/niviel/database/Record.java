package com.adrastel.niviel.database;

import android.support.annotation.NonNull;

import com.adrastel.niviel.FollowerModel;
import com.adrastel.niviel.RecordModel;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Record implements RecordModel {

    public static final Factory<Record> FACTORY = new Factory<>(new Creator<Record>() {
        @Override
        public Record create(long _id, long follower, @NonNull String event, long single, long nr_single, long cr_single, long wr_single, long average, long nr_average, long cr_average, long wr_average) {
            return new AutoValue_Record(_id, follower, event, single, nr_single, cr_single, wr_single, average, nr_average, cr_average, wr_average);
        }
    });

    public static final RowMapper<Record> SELECT_FROM_FOLLOWER_MAPPER = FACTORY.select_from_followerMapper();

}
