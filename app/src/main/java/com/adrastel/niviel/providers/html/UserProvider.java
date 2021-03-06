package com.adrastel.niviel.providers.html;

import com.adrastel.niviel.models.readable.User;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UserProvider {

    public static User getUser(Document document) {

        try {

            //Name
            String name = document.select("#content h1").first().text();

            // Infos
            Element table = document.select("table").first();

            Element tr = table.select("tbody tr").get(3);

            Elements tds = tr.children();

            String country = tds.get(0).text();
            String wca_id = tds.get(1).text();
            String gender = tds.get(2).text();
            String competitions = tds.get(3).text();

            String picture = null;

            try {
                picture = document.select("#content center img").first().attr("src");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return new User(name, country, wca_id, gender, competitions, picture);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
