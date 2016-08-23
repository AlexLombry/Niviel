package com.adrastel.niviel.assets;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.Spanned;

public class DetailsMaker {

    private Context context;
    private String message = "";

    public DetailsMaker(Context context) {
        this.context = context;
    }

    public DetailsMaker add(@StringRes int titleRes, String body) {

        String title = context.getString(titleRes);

        String message = "<strong>" + title + " : </strong>" + body + "<br/>";

        this.message += message;

        return this;
    }

    public Spanned build() {
        return Assets.fromHtml(message);
    }

    public Spanned build(String color) {

        this.message = "<font color=\"" + color + "\">" + this.message + "</font>";

        return build();
    }
}