package com.adrastel.niviel.WCA;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile {

    private int id;
    private String wca_id;
    private String name;
    private String gender;
    private String country;
    private String created_at;
    private String avatar_url;
    private boolean is_default;

    public Profile(int id, String wca_id, String name, String gender, String country, String created_at, String avatar_url, boolean is_default) {
        this.id = id;
        this.wca_id = wca_id;
        this.name = name;
        this.gender = gender;
        this.country = country;
        this.created_at = created_at;
        this.avatar_url = avatar_url;
        this.is_default = is_default;
    }

    public int getId() {
        return id;
    }

    public String getWca_id() {
        return wca_id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public boolean is_default() {
        return is_default;
    }

    public static Profile deserialize(String json) throws JSONException {

        JSONObject root = new JSONObject(json);

        JSONObject user = root.getJSONObject("user");

        JSONObject avatarObject = user.getJSONObject("avatar");

        int id = user.getInt("id");
        String wca_id = user.getString("wca_id");
        String name = user.getString("name");
        String gender = user.getString("gender");
        String country = user.getString("country_iso2");
        String created_at = user.getString("created_at");
        String avatar = avatarObject.getString("url");
        boolean is_default = avatarObject.getBoolean("is_default");


        /*
        {
            "user": {
                "class":"user",
                "url":"\/results\/p.php?i=2016DERO01",
                "id":14874,
                "wca_id":"2016DERO01",
                "name":"Mathias Deroubaix",
                "gender":"m",
                "country_iso2":"FR",
                "delegate_status":null,
                "created_at":"2016-03-20T15:31:02.000Z",
                "updated_at":"2016-06-05T19:38:20.000Z",
                "teams":[],
                "avatar":{
                    "url":"https:\/\/d1qsrrpnlo9sni.cloudfront.net\/assets\/missing_avatar_thumb-f0ea801c804765a22892b57636af829edbef25260a65d90aaffbd7873bde74fc.png",
                    "thumb_url":"https:\/\/d1qsrrpnlo9sni.cloudfront.net\/assets\/missing_avatar_thumb-f0ea801c804765a22892b57636af829edbef25260a65d90aaffbd7873bde74fc.png",
                    "is_default":true
                    }
                }
           }*/

        return new Profile(id, wca_id, name, gender, country, created_at, avatar, is_default);
    }
}
