package com.adrastel.niviel.assets;

import com.adrastel.niviel.R;

public class Cubes {

    public static final int CUBE_333 = 0;
    public static final int CUBE_222 = 1;
    public static final int CUBE_444 = 2;
    public static final int CUBE_555 = 3;
    public static final int CUBE_333_BF = 4;
    public static final int CUBE_333_OH = 5;
    public static final int CUBE_333_FM = 6;
    public static final int CUBE_333_WF = 7;
    public static final int CUBE_MEGAMINX = 8;
    public static final int CUBE_PYRAMINX = 9;
    public static final int CUBE_SQUARE_1 = 10;
    public static final int CUBE_CLOCK = 11;
    public static final int CUBE_SKEWB = 12;
    public static final int CUBE_666 = 13;
    public static final int CUBE_777 = 14;
    public static final int CUBE_444_BF = 15;
    public static final int CUBE_555_BF = 16;
    public static final int CUBE_333_MB = 17;

    public static final int SINGLE = 18;
    public static final int AVERAGE = 19;

    public static final int NR = 20;
    public static final int CR = 21;
    public static final int WR = 22;

    public static boolean isAverage(int cube) {
        return !(cube == CUBE_444_BF || cube == CUBE_555_BF || cube == CUBE_333_MB);
    }

    /**
     * Retourne une image en fonction d'un nombre
     * @param i type du cube
     * @return drawable
     */
    public static int getImage(int i) {

        String[] cubes = {"2x2 cube", "4x4 cube", "5x5 cube", "6x6 cube", "megaminx", "pyraminx", "square-1", "skewb"};

        int position = i % cubes.length;
        String event = cubes[position];

        return getImage(event);
    }

    /**
     * Retourne une image en fonction du type du cube
     *
     * @param event type du cube
     * @return drawable
     */
    public static int getImage(String event) {

        switch(event.toLowerCase()) {
            case "2x2 cube":
                return R.drawable.cube_2x2;

            case "4x4 cube":
                return R.drawable.cube_4x4;

            case "5x5 cube":
                return R.drawable.cube_5x5;

            case "6x6 cube":
                return R.drawable.cube_6x6;

            case "7x7 cube":
                return R.drawable.cube_7x7;

            case "megaminx":
                return R.drawable.megaminx;

            case "pyraminx":
                return R.drawable.piraminx;

            case "square-1":
                return R.drawable.square_1;

            case "rubik's clock":
                return R.drawable.cube_clock;

            case "skewb":
                return R.drawable.skewb;

            case "4x4 blindfolded":
                return R.drawable.cube_4x4;

            case "5x5 blindfolded":
                return R.drawable.cube_5x5;

            default:
                return R.drawable.cube_3x3;

        }
    }

}
