package com.adrastel.niviel.Http;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ErrorListener implements Response.ErrorListener {

    private Context context;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;

    // Constructeurs

    public ErrorListener(Context context) {
        this.context = context;
    }



    public ErrorListener(Context context, SwipeRefreshLayout refreshLayout) {
        this.context = context;
        this.refreshLayout = refreshLayout;
    }


    /**
     * Lancée lors d'une erreur de requete HTTP
     *
     * Supprime le loader si il y en a un et lance un toast
     *
     * @param error Erreur
     */
    @Override
    public void onErrorResponse(VolleyError error) {



        if(refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }

        Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_LONG).show();

        Log.e(error.getMessage());

        if(error.networkResponse != null) {
            Log.e("HTTP Error : " + error.networkResponse.statusCode);
        }
    }
}
