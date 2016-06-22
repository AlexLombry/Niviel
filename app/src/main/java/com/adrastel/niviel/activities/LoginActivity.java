package com.adrastel.niviel.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.callbacks.VolleyCallback;
import com.adrastel.niviel.connectivity.WcaAuth;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {

    /**
     *
     * LoginActivity est la classe gérant l'accès des utilisateurs à l'API de la WCA
     *
     * Schema :
     *
     * Le client est redirigé sur une page de connexion, il se connnecte et accepte
     *
     * Il est redirigé vers http://127.0.0.1/niviel/?code=712fb0e1c0f1ac69052342a55a1ecdbb91029696500ea7c1ce192f53b4b4e92a
     *
     * On recupere le code et on envoie une requete POST vers le serveur pour avoir un access_token
     *
     * {
     *      "access_token": "83253b2867cb242a605006124115df022fbb8cfb8ae93817ab089cc250ff0619",
     *      "token_type": "bearer",
     *      "expires_in": 7200,
     *      "scope": "public",
     *      "created_at": 1465549671
     * }
     *
     * On ferme l'activité et on revoie l'objet sous forme de HashMap
     *
     */

    private Context context;

    /**
     *
     * Se connecte à l'API de la WCA, récupère un code pour recuperer l'access_token et finir l'activité en cas de succès
     *
     * @param savedInstanceState Pas utile pour l'instant.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;

        final WebView webView = (WebView) findViewById(R.id.oauth_web_view);
        assert webView != null;

        final String oauth = "https://www.worldcubeassociation.org/oauth/authorize?client_id=8858b7016129d9494c165449e7454e38c97ad7d36dcd3679076243e7392d1f5e&redirect_uri=http%3A%2F%2F127.0.0.1%2Fniviel%2F&response_type=code&scope=public";
        webView.loadUrl(oauth);

        /**
         * On définit la webview
         */
        webView.setWebViewClient(new WebViewClient() {

            /**
             *
             * Regarde les parametres de l'URL, pour voir si l'utilisateur a accepté ou non
             *
             * @param view la vue
             * @param url l'url
             * @return true si on arrete l'execution et false si on continue
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                /**
                 * Si l'URL est nulle
                 */
                if(url.isEmpty()) {
                    webView.loadUrl(oauth);
                }

                Log.d("niviel", url);
                Uri uri = Uri.parse(url);



                /**
                 * Si le client à autorise, on fait une requete pour recuperer l'access token
                 */
                if(uri.getQueryParameter("code") != null) {

                    Log.d("niviel", "c'est obn");

                    String token = uri.getQueryParameter("code");

                    Log.d("niviel", token);

                    WcaAuth wcaAuth = new WcaAuth(context);

                    wcaAuth.getAccessToken(token, new VolleyCallback() {

                        /**
                         * Retourne la reponse du serveur
                         *
                         *
                         * @param stringResponse la reponse HTTP retournée en String
                         * @throws JSONException En cas d'erreur de conversion
                         */

                        @Override
                        public void onSuccess(String stringResponse) {

                            //HashMap<String, String> response = Conversions.JsonStringToMap(stringResponse);

                            //Log.d("niviel", response.get("access_token"));

                            Intent intent = new Intent();
                            //intent.putExtra(Constants.EXTRAS.AUTHORIZATION_CODE, null);

                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onError(int errorCode, String body) {

                        }
                    });

                    /**
                     * On finit l'application
                     *
                     * @return access_token
                     *
                     */




                    return true;
                }

                /**
                 * Si il y a une erreur
                 */
                else if(uri.getQueryParameter("error") != null) {

                    String error = uri.getQueryParameter("error");

                    /**
                     * Si le client refuse l'autorisation
                     */
                    if(error.equals("access_denied")) {

                        Toast.makeText(getApplicationContext(), "Oups ! Vous avez refusé l'accès.", Toast.LENGTH_LONG).show();
                        webView.loadUrl(oauth);
                    }

                    else {
                        Toast.makeText(getApplicationContext(), "Oups ! Une erreur inconnue s'est produite.", Toast.LENGTH_LONG).show();
                        webView.loadUrl(oauth);
                    }


                }


                return false;
            }

            /**
             * En cas d'erreur
             *
             * NOTE: Comme l'API 23 ne supporte plus onReceivedError, on procede donc à 2 appels
             *
             */

            /**
             * En cas d'erreur, la fonction est executée.
             *
             * Si la version >= API 23, on arrète la fonction
             *
             * @param view Vue
             * @param errorCode code d'erreur
             * @param description description de l'erreur
             * @param failingUrl URL d'arrivée
             */

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                /**
                 * Si on est sur Android M, on arrete la fonction, elle sera executé ci dessous
                 */
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    return;
                }

                onError(errorCode);


            }

            /**
             * En cas d'erreur, la fonction est executée.
             *
             * La fonction n'est execute que si la version >= API 23
             *
             * @param view vue
             * @param request requête exécutée
             * @param error erreur lancée
             */

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                onError(error.getErrorCode());

            }

            /**
             * On regarde les errors possibles
             */
            public void onError(int errorCode) {
                String description;

                switch (errorCode) {
                    case WebViewClient.ERROR_CONNECT:
                        description = "Impossible de se connecter au serveur";
                        break;

                    case WebViewClient.ERROR_PROXY_AUTHENTICATION:
                        description = "Erreur de connexion au proxy";
                        break;

                    case WebViewClient.ERROR_REDIRECT_LOOP:
                        description = "Trop de redirections";
                        break;

                    case WebViewClient.ERROR_TIMEOUT:
                        description = "Connexion expirée";
                        break;

                    case WebViewClient.ERROR_TOO_MANY_REQUESTS:
                        description = "Trop de requetes effectuées";
                        break;

                    default:
                        description = "Erreur inconnue : " + errorCode;
                        break;
                }

                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_LONG).show();
                //webView.loadUrl(oauth);
            }
        });


    }





}
