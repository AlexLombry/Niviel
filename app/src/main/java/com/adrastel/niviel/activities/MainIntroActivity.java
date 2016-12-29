package com.adrastel.niviel.activities;

import android.os.Bundle;

import com.adrastel.niviel.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.intro_slide_1)
                .image(R.mipmap.ic_launcher)
                .background(R.color.amber_200)
                .build()
        );

        addSlide(new SimpleSlide.Builder()
            .title(R.string.profile)
            .description(R.string.intro_slide_2)
            .background(R.color.purple_200)
            .image(R.drawable.ic_profile)
            .build());

        addSlide(new SimpleSlide.Builder()
            .title(R.string.followers)
            .description(R.string.intro_slide_3)
            .background(R.color.blue_grey_200)
            .image(R.drawable.ic_followers)
            .build());

        addSlide(new SimpleSlide.Builder()
            .title(R.string.begin)
            .description(R.string.intro_slide_4)
            .image(R.drawable.ic_add)
            .background(R.color.indigo_200)
            .build());
    }
}
