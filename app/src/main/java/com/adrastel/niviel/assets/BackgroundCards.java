package com.adrastel.niviel.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BackgroundCards {

    private ArrayList<Integer> colors = new ArrayList<>();
    private Random random;

    public BackgroundCards(long seed) {

        this.random = new Random(seed);

        // 300 rouge
        colors.add(0xFFE57373);

        // 200 rose
        colors.add(0xFFF48FB1);

        // 200 violet
        colors.add(0xFFCE93D8);

        // 200 violet profond
        colors.add(0xFFB39DDB);

        // 200 indigo
        colors.add(0xFF9FA8DA);

        // 400 Bleu
        colors.add(0xFF42A5F5);

        // 500 bleu clair
        colors.add(0xFF03A9F4);

        // 600 cyan
        colors.add(0xFF00ACC1);

        // 400 teal
        colors.add(0xFF26A69A);

        // 500 vert
        colors.add(0xFF4CAF50);

        // 600 vert clair
        colors.add(0xFF7CB342);

        // 500 citron vert
        colors.add(0xFFCDDC39);

        // 500 jaune
        colors.add(0xFFFFEB3B);

        // 500 ambre
        colors.add(0xFFFFC107);

        // 500 orange
        colors.add(0xFFFF9800);

        // 400 orange foncé
        colors.add(0xFFFF7043);

        // 300 bleu gris
        colors.add(0xFF90A4AE);

    }

    public ArrayList<Integer> shuffle() {
        Collections.shuffle(colors, random);
        return colors;
    }

    public int get(int position) {

        while (position >= colors.size()) {
            position -= colors.size();
        }

        return position;
    }
}
