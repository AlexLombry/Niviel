package com.adrastel.niviel.fragments;

import android.app.SearchManager;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

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
import java.util.concurrent.atomic.AtomicLong;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RedirectFragment extends Fragment implements TextWatcher {

    @BindView(R.id.query) AutoCompleteTextView autoCompleteTextView;

    Unbinder unbinder;

    OkHttpClient client = new OkHttpClient();

    ArrayList<String> suggestions = new ArrayList<>();
    ArrayList<SuggestionUser> suggestionUsers = new ArrayList<>();

    ArrayAdapter<String> adapter;

    /**
     * Envoie des requetes espacées de 500ms pour gagner en fluidité
     * Stocke le temps en ms de la derniere requête
     */
    final AtomicLong lastRequest = new AtomicLong();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_redirect, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        lastRequest.set(0);


        adapter = new ArrayAdapter<>(getContext(), R.layout.adapter_suggestion, android.R.id.text1, suggestions);

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.addTextChangedListener(this);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    Toast.makeText(getContext(), suggestionUsers.get(position).getWca_id(), Toast.LENGTH_LONG).show();
                }

                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(System.currentTimeMillis() - lastRequest.get() >= 500) {

            HttpUrl url = new WcaUrl()
                    .apiSearch(String.valueOf(charSequence).trim())
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    autoCompleteTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (!response.isSuccessful()) {

                        autoCompleteTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                    try {
                        String data = response.body().string();
                        response.close();

                        JsonParser jsonParser = new JsonParser();
                        JsonElement jsonTree = jsonParser.parse(data);

                        JsonObject jsonObject = jsonTree.getAsJsonObject();

                        JsonArray result = jsonObject.getAsJsonArray("result");

                        Gson gson = new Gson();
                        suggestionUsers = gson.fromJson(result, new TypeToken<ArrayList<SuggestionUser>>() {
                        }.getType());


                        suggestions.clear();

                        int limit = 10;


                        for (int i = 0; i < suggestionUsers.size() && suggestions.size() < limit; i++) {

                            SuggestionUser suggestionUser = suggestionUsers.get(i);

                            // verifie que l'utilisateur a un id wca
                            if ((suggestionUser.getType().equals("person") || suggestionUser.getType().equals("suggestionUser")) && suggestionUser.getWca_id() != null) {

                                String field = getString(R.string.string_details_string, suggestionUser.getName(), suggestionUser.getWca_id());
                                suggestions.add(field);
                                Log.d(field);
                            }

                            i++;

                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.clear();
                                adapter.addAll(suggestions);
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            lastRequest.set(System.currentTimeMillis());
        }
    }

    //<editor-fold desc="Unused methods">
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }


    @Override
    public void afterTextChanged(Editable editable) {

    }
    //</editor-fold>
}
