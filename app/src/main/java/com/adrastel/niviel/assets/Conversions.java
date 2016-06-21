package com.adrastel.niviel.assets;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Conversions {

    /**
     *
     * A partir d'une String en JSON retourne un HashMap
     *
     * @param request la requete format JSON
     * @return HashMap
     * @throws JSONException si la conversion en JSONObject rate
     */
    public static HashMap<String, String> JsonStringToMap(String request) throws JSONException {

        HashMap<String, String> response = new HashMap<>();
        JSONObject json = new JSONObject(request);
        Iterator<?> keys = json.keys();

        while(keys.hasNext()) {
            String key = (String) keys.next();
            String value = json.getString(key);
            response.put(key, value);
        }

        return response;
    }
}
