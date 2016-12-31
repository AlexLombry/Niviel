package com.adrastel.niviel.activities;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.models.readable.SuggestionUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.listView) ListView listView;

    private OkHttpClient client = new OkHttpClient();
    private ArrayList<HashMap<String, String>> suggestions = new ArrayList<>();
    private SimpleAdapter adapter;
    private Call oldCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        adapter = new SimpleAdapter(this, suggestions, android.R.layout.simple_list_item_2,new String[] {"text1", "text2"}, new int[] {android.R.id.text1, android.R.id.text2 });
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                try {
                    HashMap<String, String> test = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                    Log.d(test.get("text1"));

                }
                catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setIconifiedByDefault(true);
        searchView.setIconified(false);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callData(newText);
                Log.d(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void callData(String query) {


        HttpUrl url = new WcaUrl()
                .apiSearch(query)
                .build();



        Request request = new Request.Builder()
                .url(url)
                .build();

        if(oldCall != null) {
            oldCall.cancel();
        }

        oldCall = client.newCall(request);

        oldCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(!response.isSuccessful()) {
                    Log.e("HTTP Error");
                    return;
                }

                String data = response.body().string();
                response.close();

                JsonParser jsonParser = new JsonParser();
                JsonElement jsonTree = jsonParser.parse(data);

                JsonObject jsonObject = jsonTree.getAsJsonObject();

                JsonArray result = jsonObject.getAsJsonArray("result");

                Gson gson = new Gson();
                ArrayList<SuggestionUser> suggestionUsers = gson.fromJson(result, new TypeToken<ArrayList<SuggestionUser>>() {}.getType());

                suggestions.clear();

                for (int i = 0; i < suggestionUsers.size(); i++) {

                    SuggestionUser suggestionUser = suggestionUsers.get(i);

                    // verifie que l'utilisateur a un id wca
                    if ((suggestionUser.getType().equals("person") || suggestionUser.getType().equals("suggestionUser")) && suggestionUser.getWca_id() != null) {

                        String field = getString(R.string.string_details_string, suggestionUser.getName(), suggestionUser.getWca_id());

                        HashMap<String, String> row = new HashMap<>();

                        row.put("text1", suggestionUser.getName());
                        row.put("text2", suggestionUser.getWca_id());

                        suggestions.add(row);

                        i++;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }


}
