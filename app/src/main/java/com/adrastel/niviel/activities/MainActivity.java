package com.adrastel.niviel.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        SharedPreferences preferences = context.getSharedPreferences(Constants.SECRETS.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                startActivity(intent);

                /*WcaAPI api = new WcaAPI(view.getContext(), null);
                api.getRecord("2016DERO01", new ProfileCallback() {
                    @Override
                    public void onSuccess(Profile response) {
                        Log.d(response.getWca_id());
                        Log.d(response.getName());
                        Log.d(response.getGender());
                    }

                    @Override
                    public void onError(int errorCode, String body) {
                        Log.e(errorCode, body);
                    }
                });*/
            }
        });

        int expires_in = preferences.getInt(Constants.PREFERENCES.EXPIRES_IN, 0);






        //Intent intent = new Intent(this, LoginActivity.class);

        //startActivityForResult(intent, Constants.CODES.BASIC_REQUEST);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Constants.CODES.BASIC_REQUEST  && resultCode == Activity.RESULT_OK) {
            HashMap<String, String> token = (HashMap<String, String>) data.getSerializableExtra(Constants.EXTRAS.AUTHORIZATION_CODE);

            String access_token = token.get("access_token");
            String token_type = token.get("token_type");
            String scope = token.get("scope");
            int created_at = Integer.parseInt(token.get("created_at"));
            int expires_in = Integer.parseInt(token.get("expires_in"));

            final SharedPreferences preferences = context.getSharedPreferences(Constants.SECRETS.SHARED_PREFERENCES, Context.MODE_PRIVATE);



            /*editor.putString(Constants.PREFERENCES.ACCESS_TOKEN, access_token);
            editor.putString(Constants.PREFERENCES.TOKEN_TYPE, token_type);
            editor.putString(Constants.PREFERENCES.SCOPE, scope);
            editor.putInt(Constants.PREFERENCES.CREATED_AT, created_at);
            editor.putInt(Constants.PREFERENCES.EXPIRES_IN, expires_in);*/

            getUserId(access_token, new VolleyCallback() {
                @Override
                public void onSuccess(String stringResponse) throws JSONException {

                    //JSONObject root = new JSONObject(stringResponse);
                    //JSONObject me = root.getJSONObject("me");

                    //HashMap<String, String> response = JsonStringToMap(me);


                    //SharedPreferences.Editor editor = preferences.edit();



                    //editor.apply();
                }
            });




        }

    }

    private void getUserId(final String access_token, final VolleyCallback callback) {
        //String url = "https://www.worldcubeassociation.org/api/v0/users/2016DERO01";

        String url = Constants.API.ME;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HTTP Error");

                try {
                    String body = new String(error.networkResponse.data, "utf-8");
                    Log.d(body);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Authorization", "Bearer " + access_token);

                return params;
            }

        };

        queue.add(request);

    }

    public interface VolleyCallback {
        void onSuccess(String response) throws JSONException;
    }

    /**
     *
     * A partir d'une String en JSON retourne un HashMap
     *
     * @param request la requete format JSON
     * @return HashMap
     * @throws JSONException si la conversion en JSONObject rate
     */
    public HashMap<String, String> JsonStringToMap(JSONObject request) throws JSONException {

        HashMap<String, String> response = new HashMap<>();
        //JSONObject json = new JSONObject(request);
        Iterator<?> keys = request.keys();

        while(keys.hasNext()) {
            String key = (String) keys.next();
            String value = request.getString(key);
            response.put(key, value);
        }

        return response;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_personal_records:

                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
