package com.adrastel.niviel.assets;

import com.google.android.gms.analytics.HitBuilders;

import java.util.Map;

public class Analytics {

    /**
     * Quel thème utilise l'utilisateur
     */
    public static Map<String, String> trackTheme(boolean isDark) {
        return new HitBuilders.EventBuilder()
                .setCategory("Réglages")
                .setAction("Thème")
                .setLabel("Clair = 0, Sombre = 1")
                .setValue(isDark ? 1 : 0)
                .build();
    }

    /**
     * Combien de followers a-t-il ?
     */
    public static Map<String, String> trackFollower(int size) {
        return new HitBuilders.EventBuilder()
                .setCategory("Fragments - Follower")
                .setAction("Followers")
                .setLabel(String.valueOf(size))
                .build();
    }

    /**
     * Quels profil regarde-t-il ?
     */
    public static Map<String, String> trackProfileView(String wca_id) {
        return new HitBuilders.EventBuilder()
                .setCategory("Fragments - Profil")
                .setAction("Profil affiché")
                .setLabel(wca_id)
                .build();
    }

    /**
     * Quel est son profil WCA ?
     */
    public static Map<String, String> trackPersonalId(String wca_id) {
        return new HitBuilders.EventBuilder()
                .setCategory("Fragment - Profil")
                .setAction("Identifiant personnel")
                .setLabel(wca_id)
                .build();
    }

    /**
     * Il follow une nouvelle personne
     */
    public static Map<String, String> trackNewFollower(String wca_id) {
        return new HitBuilders.EventBuilder()
                .setCategory("Fragment - Profil")
                .setAction("Nouveau follower")
                .setLabel(wca_id)
                .build();
    }

}
