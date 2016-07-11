package com.adrastel.niviel.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.fragments.HistoryFragment;
import com.adrastel.niviel.fragments.RankingFragment;
import com.adrastel.niviel.fragments.RecordFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Context context;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }




        fragmentManager = getSupportFragmentManager();

        context = getApplicationContext();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        if(savedInstanceState != null && fragmentManager.getFragment(savedInstanceState, Constants.STORAGE.FRAGMENT) != null) {
            this.fragment = (BaseFragment) fragmentManager.getFragment(savedInstanceState, Constants.STORAGE.FRAGMENT);

        }
        else {
            this.fragment = new RecordFragment();

        }

        switchFragment(fragment);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                // On choisit le fragment
                BaseFragment fragment = selectDrawerItem(item);
                switchFragment(fragment);

                item.setChecked(true);
                closeDrawer();
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

            case R.id.action_personal_records:

                Intent intent = new Intent(this, RecordActivity.class);
                startActivity(intent);
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

        if(fragment != null) {
            fragmentManager.putFragment(outState, Constants.STORAGE.FRAGMENT, fragment);
        }
    }

    /**
     * Choisit le fragment à prendre
     * @param item item
     */
    private BaseFragment selectDrawerItem(MenuItem item) {
        BaseFragment fragment;

        switch(item.getItemId()) {
            case R.id.nav_profile:
                fragment = new RecordFragment();
                break;

            case R.id.nav_history:
                fragment = new HistoryFragment();
                break;

            case R.id.nav_ranking:
                fragment = new RankingFragment();
                break;

            default:
                fragment = new RecordFragment();
        }

        this.fragment = fragment;
        return fragment;
    }

    /**
     * Change de fragment
     * @param fragment fragment
     */
    private void switchFragment(BaseFragment fragment) {

        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        setTitle(getString(fragment.getTitle()));
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
}
