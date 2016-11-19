package com.adrastel.niviel.assets;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.adrastel.niviel.R;

import java.util.ArrayList;

public class InboxStyle extends NotificationCompat.InboxStyle {

    private Context context;
    private ArrayList<CharSequence> lines = new ArrayList<>();
    private CharSequence title = null;
    private CharSequence summary = null;

    public InboxStyle(Context context) {
        super();

        this.context = context;
    }

    @Override
    public NotificationCompat.InboxStyle setBigContentTitle(CharSequence title) {
        this.title = title;
        return super.setBigContentTitle(title);
    }

    @Override
    public NotificationCompat.InboxStyle setSummaryText(CharSequence cs) {
        this.summary = cs;
        return super.setSummaryText(cs);
    }

    @Override
    public NotificationCompat.InboxStyle addLine(CharSequence cs) {
        lines.add(cs);
        return super.addLine(cs);
    }

    @Override
    public String toString() {
        String inbox = context.getString(R.string.string_details_string, title, summary) + "\n";

        for(CharSequence line : lines) {
            inbox += (line + "\n");
        }

        return inbox;
    }
}
