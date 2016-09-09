package com.adrastel.niviel.activities;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.fragments.FollowerFragment;
import com.adrastel.niviel.fragments.ProfileFragment;
import com.adrastel.niviel.fragments.html.RankingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {


    // Les vues
    public @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tab_layout) TabLayout tabLayout;

    // Les core-objects
    private FragmentManager fragmentManager;
    private BaseFragment fragment;
    private SharedPreferences preferences;

    // Les preferences
    private String prefWcaId = null;
    private String prefWcaName = null;
    private boolean prefDoubleClickToExit = true;
    private boolean doubleClickToExit = false;

    // Le runnable qui est executé après que le drawer soit fermé
    private Runnable fragmentToRun;

    // Les constantes du receiver
    public static final String ACTIVITY_RECEIVER = "activity_receiver";
    public static final String ACTIVITY_RECEIVER_ACTION = "activity_receiver_action";
    public static final String UPDATE_WCA_PROFILE = "update_wca_profile";

    // Le reciever
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("intent received");
            String action = intent.getStringExtra(ACTIVITY_RECEIVER_ACTION);

            switch (action) {
                case UPDATE_WCA_PROFILE:
                    updateWcaProfile();
                    break;
            }

        }
    };



    /**
     * Lors de la creation de l'activité
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);

        // Initialisation des objets
        ButterKnife.bind(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        fragmentManager = getSupportFragmentManager();

        // Gere l'intent (si il s'agit d'une requete...)
        Intent intent = getIntent();
        handleIntent(intent);

        // Actualise l'ID WCA
        updateWcaProfile();

        // Recupère les près
        prefDoubleClickToExit = preferences.getBoolean(getString(R.string.pref_double_back), true);


        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        /*
        Si il y a un fragment en mémiore, l'execute
        Sinon lance profileFragment
         */
        if(savedInstanceState != null && fragmentManager.getFragment(savedInstanceState, Constants.STORAGE.FRAGMENT) != null) {
            this.fragment = (BaseFragment) fragmentManager.getFragment(savedInstanceState, Constants.STORAGE.FRAGMENT);
        }
        else {
            this.fragment = ProfileFragment.newInstance(ProfileFragment.RECORD_TAB, prefWcaId, prefWcaName);
        }

        switchFragment(fragment);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {

                item.setChecked(true);
                closeDrawer();

                // On choisit le fragment
                final BaseFragment fragment = selectDrawerItem(item);

                if(fragment != null ) {
                    runWhenDrawerClose(new Runnable() {
                        @Override
                        public void run() {

                            switchFragment(fragment);
                        }
                    });
                }

                return true;
            }
        });

        drawerLayout.addDrawerListener(this);

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

            if(prefDoubleClickToExit) {

                if (doubleClickToExit) {
                    super.onBackPressed();
                } else {
                    doubleClickToExit = true;
                    Toast.makeText(this, R.string.toast_click_double_back, Toast.LENGTH_LONG).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleClickToExit = false;
                        }
                    }, 3000);
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(activityReceiver, new IntentFilter(ACTIVITY_RECEIVER));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityReceiver);
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
    // todo: ajouter un bouton "ne pas cliquer"
    /**
     * Choisit le fragment à prendre
     * @param item item
     */
    @Nullable
    private BaseFragment selectDrawerItem(MenuItem item) {

        // todo: changer le drawer
        tabLayout.setVisibility(View.GONE);

        switch(item.getItemId()) {
            case R.id.profile:
                return ProfileFragment.newInstance(prefWcaId, prefWcaName);

            case R.id.ranking:
                return new RankingFragment();

            case R.id.followers:
                return new FollowerFragment();

            case R.id.settings:

                runWhenDrawerClose(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                });
                return null;

            default:
                return ProfileFragment.newInstance(prefWcaId, prefWcaName);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawerLayout.removeDrawerListener(this);
    }

    public void showFab() {
        fab.show();
    }

    public void hideFab() {
        fab.hide();
    }

    /**
     * Change de fragment
     * @param fragment fragment
     */
    @SuppressWarnings("ResourceType")
    public void switchFragment(BaseFragment fragment) {

        try {
            // Remplace le fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();

            updateUi(fragment);
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
    public void setSubtitle(String subtitle) {

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }

    private void updateWcaProfile() {
        prefWcaId = preferences.getString(getString(R.string.pref_wca_id), null);
        prefWcaName = preferences.getString(Constants.EXTRAS.USERNAME, null);
    }

    @SuppressWarnings("ResourceType")
    private void updateUi(BaseFragment fragment) {

        setSubtitle(null);

        int style = fragment.getStyle();

        int[] attrs = { R.attr.colorPrimary, R.attr.colorPrimaryDark, R.attr.toolbarTitle, R.attr.fabVisible, R.attr.fabIcon};

        TypedArray typedArray = obtainStyledAttributes(style, attrs);

        Log.d("title", String.valueOf(typedArray.getString(2)));

        String title = typedArray.getString(2);
        setTitle(title);

        int primaryColor = typedArray.getColor(0, Color.WHITE);
        setToolbarColor(primaryColor);

        int primaryColorDark = typedArray.getColor(1, Color.WHITE);
        setStatusBar(primaryColorDark);


        boolean fabVisible = typedArray.getBoolean(3, false);

        if(fabVisible) {
            showFab();
        }

        else {
            hideFab();
        }

        Drawable fabIcon = typedArray.getDrawable(4);
        fab.setImageDrawable(fabIcon);

        typedArray.recycle();
    }

    private void handleIntent(Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_VIEW)) {

            try {
                Uri query = intent.getData();

                Log.d(query.toString());

                if (query.getAuthority().equals("com.adrastel.search")) {

                    String wca_id = query.getLastPathSegment();
                    String name = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY);
                    searchUser(wca_id, name);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void searchUser(String wca_id, String name) {

        ProfileFragment profileFragment = ProfileFragment.newInstance(wca_id, name);
        switchFragment(profileFragment);

    }

    private void runWhenDrawerClose(Runnable runnable) {
        this.fragmentToRun = runnable;
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {}

    @Override
    public void onDrawerOpened(View drawerView) {}

    @Override
    public void onDrawerClosed(View drawerView) {
        if(fragmentToRun != null) {
            fragmentToRun.run();
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {}
}
