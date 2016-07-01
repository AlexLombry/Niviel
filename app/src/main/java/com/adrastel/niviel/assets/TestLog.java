package com.adrastel.niviel.assets;

public class TestLog extends Log {

    private static int count = 0;

    public static void test(Object message) {

        if(message != null) {
            Log.d(String.valueOf(count), "Le message n'est pas vide");
        }

        else {
            Log.d(String.valueOf(count), "Le message est vide");
        }

        count++;
    }

}
