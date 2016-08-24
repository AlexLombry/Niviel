package com.adrastel.niviel.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.adrastel.niviel.fragments.html.HistoryFragment;
import com.adrastel.niviel.fragments.html.ProfileFragment;
import com.adrastel.niviel.fragments.html.RankingFragment;
import com.adrastel.niviel.fragments.html.RecordFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ActivityTunnelInterface {

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.navigation_view) NavigationView navigationView;

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



}
