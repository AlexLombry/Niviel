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
                .title("Niviel")
                .description("Bienvenue sur Niviel, une application simple affichant vos records provenant de la WCA")
                .image(R.mipmap.ic_launcher)
                .background(R.color.amber_200)
                .build()
        );

        addSlide(new SimpleSlide.Builder()
            .title("Profil")
            .description("Retrouvez ainsi vos records, historiques, competitions avec une interface légère et rapide")
            .background(R.color.purple_200)
            .image(R.drawable.ic_profile)
            .build());

        addSlide(new SimpleSlide.Builder()
            .title("Followers")
            .description("Vous avez la possibilité de suivre des personnes afin de garder leur résultats hors lignes et être informés quand ils battent leur records")
            .background(R.color.blue_grey_200)
            .image(R.drawable.ic_followers)
            .build());

        addSlide(new SimpleSlide.Builder()
            .title("Pour commencer")
            .description("Identifiez-vous pour acceder rapidement à votre profil.\n\nPour vous identifer, accédez à votre profil via une recherche, appuyez sur le bouton + et cliquez sur \"Changer de profil")
            .image(R.drawable.ic_add)
            .background(R.color.indigo_200)
            .build());

    }
}
