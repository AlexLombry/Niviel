package com.adrastel.niviel.assets;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;

public abstract class Target implements com.squareup.picasso.Target {

    public abstract void onImageLoaded(Bitmap bitmap, Picasso.LoadedFrom from);

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        onImageLoaded(bitmap, from);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }
}
