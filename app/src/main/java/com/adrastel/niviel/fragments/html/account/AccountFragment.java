package com.adrastel.niviel.fragments.html.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.SettingsActivity;
import com.adrastel.niviel.adapters.BaseAdapter;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.fragments.html.HtmlFragment;
import com.adrastel.niviel.models.BaseModel;

public abstract class AccountFragment<M extends BaseModel, A extends BaseAdapter> extends HtmlFragment<M, A> {

    protected String url;

    // utilisé en cas d'erreur
    protected boolean needToRefresh = false;

    protected String wca_id = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle arguments = getArguments();

        // Recupere l'id depuis la RAM
        if(arguments != null) {
            wca_id = arguments.getString(Constants.EXTRAS.WCA_ID, null);
        }



    }

    @Override
    public void onResume() {
        super.onResume();

        // Si ça ne marche pas, recupere depuis les preferences

        if(wca_id == null) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            wca_id = preferences.getString(getString(R.string.pref_wca_id), null);

            // Si ça ne marche toujours pas, affiche une erreur
            if (wca_id == null || wca_id.equals("")) {

                needToRefresh = true;

                makeSnackbar(R.string.wrong_wca_id, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), SettingsActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();

            }

        }
        setUrl(wca_id);
    }

    /**
     * Retourne l'url de requete
     * @return url
     */
    @Override
    protected String getUrl() {
        return url;
    }

    protected void setUrl(String wca_id) {
        url = "https://www.worldcubeassociation.org/results/p.php?i=" + wca_id;
    }
}
