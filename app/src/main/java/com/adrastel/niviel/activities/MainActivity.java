package com.adrastel.niviel.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.fragments.FollowerFragment;
import com.adrastel.niviel.fragments.html.ProfileFragment;
import com.adrastel.niviel.fragments.html.RankingFragment;
import com.adrastel.niviel.fragments.html.account.HistoryFragment;
import com.adrastel.niviel.fragments.html.account.RecordFragment;
import com.adrastel.niviel.http.HttpCallback;
import com.adrastel.niviel.interfaces.ActivityTunnelInterface;
import com.adrastel.niviel.models.readable.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity implements ActivityTunnelInterface {

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;

    private FragmentManager fragmentManager;
    private BaseFragment fragment;



    /**
     * Lors de la creation de l'activité
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        handleIntent(intent);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragment != null) {
                    fragment.onFabClick(view);
                }
            }
        });

        final ActionBar actionBar = getSupportActionBar();


        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fragmentManager = getSupportFragmentManager();


        if(savedInstanceState != null && fragmentManager.getFragment(savedInstanceState, Constants.STORAGE.FRAGMENT) != null) {
            this.fragment = (BaseFragment) fragmentManager.getFragment(savedInstanceState, Constants.STORAGE.FRAGMENT);
        }
        else {
            this.fragment = new RecordFragment();
        }

        switchFragment(fragment);

        // todo: regarder le probleme back pas actualisation toolbar

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);
                closeDrawer();

                // On choisit le fragment
                BaseFragment fragment = selectDrawerItem(item);

                if(fragment != null) {
                    switchFragment(fragment);
                }

                return true;
            }
        });

        // todo: gere id wca incorrecte



    }

    /**
     * On inflate le menu
     * @param menu menu
     * @return true pour l'instancier
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        try {
            MenuItem itemSearch = menu.findItem(R.id.search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);

            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Lors d'un clique de la toolbar
     * @param item item
     * @return true pour arreter l'execution
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;

            case R.id.settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Si on presse back, on ferme le menu
     */
    @Override
    public void onBackPressed() {

        if(isDrawerOpen()) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * On sauvegarde le fragment actuel
     * @param outState bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            if (fragment != null) {
                fragmentManager.putFragment(outState, Constants.STORAGE.FRAGMENT, fragment);
            }
        }

        catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    //todo: refaire les couleurs des toolbars + couleur circle view ranking faire correspondre avec toolbar foollowers

    /**
     * Choisit le fragment à prendre
     * @param item item
     */
    @Nullable
    private BaseFragment selectDrawerItem(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.profile:
                return new ProfileFragment();

            case R.id.records:
                return new RecordFragment();

            case R.id.history:
                return new HistoryFragment();

            case R.id.ranking:
                return new RankingFragment();

            case R.id.followers:
                return new FollowerFragment();

            case R.id.settings:

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return null;

            default:
                return new RecordFragment();

        }
    }

    /**
     * Change de fragment
     * @param fragment fragment
     */
    public void switchFragment(BaseFragment fragment) {

        try {
            // Remplace le fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();


            // Modifie le titre et les couleurs des barres
            String fragmentTitle = getString(fragment.getTitle());
            setTitle(fragmentTitle);
            setSubtitle(null);
            // colors
            int primaryColor = Assets.getColor(this, fragment.getPrimaryColor());
            setToolbarColor(primaryColor);


            int primaryColorDark = Assets.getColor(this, fragment.getPrimaryDarkColor());
            setStatusBar(primaryColorDark);

            int[] attrs = { R.attr.colorPrimary, R.attr.colorPrimaryDark};

            TypedArray typedArray = obtainStyledAttributes(R.style.AppTheme_Preferences, attrs);
            Log.d("Primary ", Integer.toHexString(typedArray.getColor(0, Color.BLACK)));
            Log.d("primary dark ", Integer.toHexString(typedArray.getColor(1, Color.BLACK)));


            typedArray.recycle();
            // fab
            if(fragment.getFabVisibility() == View.VISIBLE) {
                fab.show();
            }

            else {
                fab.hide();
            }

            fab.setImageResource(fragment.getFabIcon());

            this.fragment = fragment;

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Ouvre le menu
     */
    private void openDrawer() {
        if(drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Ferme le menu
     */
    private void closeDrawer() {
        if(drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
    }

    /**
     * Regarde si le menu est ouvert
     * @return true si il est ouver
     */
    private boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    /**
     * Change la couleur de l'action bar
     * @param color couleur
     */
    private void setToolbarColor(int color) {
        if(toolbar != null && color != 0) {
            toolbar.setBackgroundColor(color);
        }
    }

    /**
     * Change la couleur de la bar de status
     * Prendre des couleurs MD 700 si possible
     * @param color couleur
     */
    private void setStatusBar(int color) {
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && color != 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }

    }

    /**
     * Edite le sous titre de l'action bar
     * Disponible dans les fragments
     * @param subtitle sous titre
     */
    @Override
    public void setSubtitle(String subtitle) {

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    @Override
    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }

    private void handleIntent(Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchUser(query);
        }
    }

    private void searchUser(String query) {

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addEncodedPathSegments("api/v0/search/users")
                .addEncodedQueryParameter("q", "Mathias Deroubaix")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);


        call.enqueue(new HttpCallback(this) {
            @Override
            public void onResponse(String response){


                JsonParser jsonParser = new JsonParser();
                JsonElement jsonTree = jsonParser.parse(response);

                JsonObject jsonObject = jsonTree.getAsJsonObject();

                JsonArray result = jsonObject.getAsJsonArray("result");

                Gson gson = new Gson();
                ArrayList<User> users = gson.fromJson(result, new TypeToken<ArrayList<User>>() {
                }.getType());

                for (User user : users) {
                    Log.d("MainActivity: " + user.getWca_id());

                }
            }

            @Override
            public void onFailure() {

            }

        });
    }
}
