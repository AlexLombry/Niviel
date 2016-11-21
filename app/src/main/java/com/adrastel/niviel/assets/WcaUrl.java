package com.adrastel.niviel.assets;

import android.net.Uri;

import com.adrastel.niviel.R;

import java.util.ArrayList;

import okhttp3.HttpUrl;

public class WcaUrl {

    private HttpUrl.Builder url;

    public WcaUrl() {
        url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.worldcubeassociation.org");
    }

    public WcaUrl profile(String wca_id) {
        url.addPathSegments("results/p.php").addEncodedQueryParameter("i", wca_id);
        return this;
    }

    public WcaUrl competition() {
        url.addEncodedPathSegment("competitions");
        return this;
    }

    public WcaUrl competition(String segment) {
        url.addEncodedPathSegments(segment);
        return this;
    }

    public WcaUrl competition(ArrayList<String> eventIds, String regionId) {

        url.addEncodedPathSegment("competitions")
                .addEncodedQueryParameter("region", regionId);

        for(String eventId : eventIds) {
            url.addEncodedQueryParameter("event_ids[]", eventId);
        }
        return this;
    }

    public WcaUrl ranking(String eventId, String regionId, boolean isSingle) {

        url.addEncodedPathSegments("results/events.php")
                .addEncodedQueryParameter("eventId", eventId)
                .addEncodedQueryParameter("regionId", regionId);

        if(!isSingle)
            url.addEncodedQueryParameter("average", "Average");

        else
            url.addEncodedQueryParameter("single", "Single");

        return this;
    }

    public WcaUrl apiSearch(String query) {
        url.addEncodedPathSegments("api/v0/search").addEncodedQueryParameter("q", query);
        return this;
    }

    public HttpUrl build() {
        return url.build();
    }

    public String toString() {
        return url.toString();
    }

    public Uri toUri() {
        return Uri.parse(toString());
    }

}
