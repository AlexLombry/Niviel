package com.adrastel.niviel.assets;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.Spanned;

public class DetailsDialogMaker {

    private Context context;
    private String message = "";

    public DetailsDialogMaker(Context context) {
        this.context = context;
    }

    public DetailsDialogMaker add(@StringRes int titleRes, String body) {

        String title = context.getString(titleRes);

        String message = "<strong>" + title + " : </strong>" + body + "<br/>";

        this.message += message;

        return this;
    }

    public Spanned build() {

        String finalMessage = "<font color='#757575'>" + message + "</font>";

        return Assets.fromHtml(finalMessage);
    }
}
