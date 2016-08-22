package com.adrastel.niviel.assets;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.fragments.BaseFragment;

public class IntentHelper {


    public static void shareIntent(Context context, String text, String html) {
        try {
            Intent intent = new Intent();

            intent.setType("text/plain");
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            intent.putExtra(Intent.EXTRA_HTML_TEXT, html);

            Intent chooser = Intent.createChooser(intent, context.getString(R.string.share));

            context.startActivity(chooser);
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void switchFragment(FragmentActivity fragmentActivity, BaseFragment fragment) {

        try {
            MainActivity activity = (MainActivity) fragmentActivity;
            activity.switchFragment(fragment);
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
