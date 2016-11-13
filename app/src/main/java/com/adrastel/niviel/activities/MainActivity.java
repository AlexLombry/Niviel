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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.adrastel.niviel.BuildConfig;
import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.fragments.CompetitionFragment;
import com.adrastel.niviel.fragments.FollowerFragment;
import com.adrastel.niviel.fragments.ProfileFragment;
import com.adrastel.niviel.fragments.RankingFragment;
import com.adrastel.niviel.services.EditRecordService;
import com.google.android.gms.analytics.HitBuilders;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.cketti.mailto.EmailIntentBuilder;


/**
 * Activité principale
 * <p>
 * Est composée d'un DrawerLayout qui gère plusieurs fragments
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {


    // L'indentifiant du receiver
    public static final String ACTIVITY_RECEIVER = "activity_receiver";
    public static final String ACTIVITY_RECEIVER_ACTION = "activity_receiver_action";

    // Code de requête pour redemarrer l'activité
    public static final int RESTART_ACTIVITY = 0;


    // Les vues
    public @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    private MenuItem searchMenuItem;


    // Les core-objects
    private FragmentManager fragmentManager;
    private BaseFragment fragment;
    private SharedPreferences preferences;

    // L'identifiant du profil
    private long prefId = -1;
    private boolean isDark = false;

    // Le runnable qui est executé après que le drawer soit fermé
    private Runnable fragmentToRun;


    // Le reciever
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getIntExtra(EditRecordService.ACTION, EditRecordService.ADD_RECORD_FAILURE)) {

                case EditRecordService.ADD_RECORD_SUCCESS:
                    updateUiOnProfileChange();
                    break;
            }

        }
    };


    /**
     * Lors de la creation de l'activité
     * <p>
     * Initialise le drawer layout, les preferences et l'identification du profil
     *
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fragmentManager = getSupportFragmentManager();

        // Initialisation des objets
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Si c'est le premier lancement de l'application, lance intro
        if(preferences.getBoolean(getString(R.string.pref_first_launch), true)) {

            preferences.edit().putBoolean(getString(R.string.pref_first_launch), false).apply();

            startActivity(new Intent(this, MainIntroActivity.class));
            return;
        }

        // Paramètre le thème
        isDark = Assets.isDark(preferences.getString(getString(R.string.pref_isdark), "0"));
        setDayNightTheme(isDark);


        // Actualise l'ID WCA
        updateUiOnProfileChange();

        /*
        Si il y a un fragment en mémiore, l'execute
        Sinon lance profileFragment
         */
        if (this.fragment == null) {
            if (savedInstanceState != null && fragmentManager.getFragment(savedInstanceState, Constants.STORAGE.FRAGMENT) != null) {
                this.fragment = (BaseFragment) fragmentManager.getFragment(savedInstanceState, Constants.STORAGE.FRAGMENT);
            } else {
                this.fragment = ProfileFragment.newInstance(prefId, true);
            }
        }

        switchFragment(fragment);

        // Gere l'intent (si il s'agit d'une requete...)
        Intent intent = getIntent();
        handleIntent(intent);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {

                item.setChecked(true);
                closeDrawer();

                // On choisit le fragment
                final BaseFragment fragment = selectDrawerItem(item);

                if (fragment != null) {


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

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        Log.d(metrics.widthPixels + "x" + metrics.heightPixels);

    }



    /**
     * Sauvegarde le fragment actuel
     *
     * @param outState bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            if (fragment != null) {
                fragmentManager.putFragment(outState, Constants.STORAGE.FRAGMENT, fragment);
            }
        } catch (Exception e) {
            e.printStackTrace();

            getDefaultTracker().send(new HitBuilders.ExceptionBuilder()
                .setDescription(e.getMessage())
                .setFatal(false)
                .build());
        }
    }

    /**
     * Inflate le menu et définit le composant de recherche
     *
     * @param menu menu
     * @return true pour l'instancier
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflation
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Définition du moteur de rechervche
        searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    /**
     * Lors d'un clique de la toolbar
     * <p>
     * Ouvre le drawer ou les réglages
     *
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
                startActivityForResult(settings, 0);
                return true;

            case R.id.day_night:

                isDark = !isDark;
                preferences
                        .edit()
                        .putString(getString(R.string.pref_isdark), Assets.dayNightBooleanToString(isDark))
                        .apply();

                finish();
                startActivity(new Intent(this, MainActivity.class));

                return true;

            case R.id.contact:

                getDefaultTracker().send(new HitBuilders.EventBuilder()
                    .setCategory("Menu")
                    .setAction("Press Button")
                    .setLabel("Contact")
                    .build());

                if(!BuildConfig.DEBUG) {
                    EmailIntentBuilder
                            .from(this)
                            .to(getString(R.string.email))
                            .subject(getString(R.string.mail_subject))
                            .body(String.format(getString(R.string.mail_body), Build.MODEL, Build.VERSION.RELEASE, Build.VERSION.SDK_INT, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
                            .start();
                }

        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Lors de l'appui du bouton arrière
     * <p>
     * Ferme le menu et la barre de recherche
     */
    @Override
    public void onBackPressed() {
        closeSearchview();

        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }

    }

    /**
     * Lors de la pause de l'activité, enlève les listeners du drawerLayout et des receivers
     */
    @Override
    protected void onPause() {
        super.onPause();
        drawerLayout.removeDrawerListener(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityReceiver);
    }

    /**
     * Lors de la reprise de l'activité, ré établie les listeners et les receivers
     */
    @Override
    protected void onResume() {
        super.onResume();
        drawerLayout.addDrawerListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(activityReceiver, new IntentFilter(EditRecordService.INTENT_FILTER));
    }

    /**
     * Redemarre l'activité si un réglage a été changé
     *
     * @param requestCode requête
     * @param resultCode  réponse
     * @param data        données
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESTART_ACTIVITY) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * Appelé en cas de recherche
     *
     * @param intent recherche
     */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    //<editor-fold desc="Drawer events">
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
    //</editor-fold>

    /**
     * Demarre le runnable (si n'a pas été lancé) lors de la fermeture du Drawer
     *
     * @param drawerView drawer
     */
    @Override
    public void onDrawerClosed(View drawerView) {
        if (fragmentToRun != null) {
            fragmentToRun.run();
            fragmentToRun = null;
        }
    }

    /**
     * Choisit le fragment à prendre
     *
     * @param item item
     */
    @Nullable
    private BaseFragment selectDrawerItem(MenuItem item) {

        tabLayout.setVisibility(View.GONE);

        switch (item.getItemId()) {
            case R.id.profile:
                return ProfileFragment.newInstance(prefId, false);

            case R.id.ranking:
                return RankingFragment.newInstance();

            case R.id.competitions:
                return CompetitionFragment.newInstance();

            case R.id.explore:
                return ProfileFragment.newInstance(null, null, true);

            case R.id.followers:
                return new FollowerFragment();

            case R.id.settings:

                runWhenDrawerClose(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivityForResult(intent, 0);
                    }
                });
                return null;

            default:
                return ProfileFragment.newInstance(prefId, false);

        }
    }

    /**
     * Affiche le fab
     */
    public void showFab() {
        fab.show();
    }

    /**
     * Cache le fab
     */
    public void hideFab() {
        fab.hide();
    }

    /**
     * Change de fragment
     *
     * @param fragment fragment
     */
    public void switchFragment(BaseFragment fragment) {

        // Remplace le fragment
        fragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        updateUiOnFragmentChange(fragment);
        this.fragment = fragment;


    }

    /**
     * Ouvre le menu
     */
    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Ferme le menu
     */
    private void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
    }

    /**
     * Regarde si le menu est ouvert
     *
     * @return true si il est ouver
     */
    private boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    /**
     * Change la couleur de l'action bar
     *
     * @param color couleur
     */
    private void setToolbarColor(int color) {
        if (toolbar != null && color != 0) {
            toolbar.setBackgroundColor(color);
        }
    }



    /**
     * Change la couleur de la bar de status
     * Prendre des couleurs MD 700 si possible
     *
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
     *
     * @param subtitle sous titre
     */
    public void setSubtitle(String subtitle) {

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    /**
     * Retourne le coordinatorLayout
     *
     * @return coordinatorLayout
     */
    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }

    /**
     * Actualise les éléments graphiques lors d'un changement de profil
     */
    private void updateUiOnProfileChange() {
        prefId = preferences.getLong(getString(R.string.pref_personal_id), -1);
        View headerView = navigationView.getHeaderView(0);

        TextView nameView = (TextView) headerView.findViewById(R.id.name);
        TextView wca_idView = (TextView) headerView.findViewById(R.id.wca_id);

        // Recupere le menuItem du profil
        MenuItem profileItem = navigationView.getMenu().findItem(R.id.profile);

        if (prefId != -1) {

            // Essaie de recuperer le nom et prénom. Si ne peut pas, supprime le profil
            try {
                DatabaseHelper database = DatabaseHelper.getInstance(this);
                Follower follower = database.selectFollowerFromId(prefId);

                String prefWcaId = follower.wca_id();
                String prefWcaName = follower.name();

                profileItem.setVisible(true);

                nameView.setText(prefWcaName);
                wca_idView.setText(prefWcaId);

            } catch (Exception e) {
                e.printStackTrace();

                preferences
                        .edit()
                        .putLong(getString(R.string.pref_personal_id), -1)
                        .apply();
            }
        } else {
            profileItem.setVisible(false);

            nameView.setText(R.string.app_name);
            wca_idView.setText(R.string.email);
        }


    }

    /**
     * Rafrechie l'UI lors des changements des fragments
     *
     * @param fragment BaseFragement
     */
    @SuppressWarnings("ResourceType")
    private void updateUiOnFragmentChange(BaseFragment fragment) {

        setSubtitle(null);

        int style = fragment.getStyle();

        int[] attrs = {R.attr.colorPrimary, R.attr.colorPrimaryDark, R.attr.toolbarTitle, R.attr.fabVisible, R.attr.fabIcon};

        TypedArray typedArray = obtainStyledAttributes(style, attrs);


        String title = typedArray.getString(2);
        setTitle(title);

        int primaryColor = typedArray.getColor(0, Color.WHITE);
        setToolbarColor(primaryColor);

        int primaryColorDark = typedArray.getColor(1, Color.WHITE);
        setStatusBar(primaryColorDark);


        boolean fabVisible = typedArray.getBoolean(3, false);

        if (fabVisible) {
            showFab();
        } else {
            hideFab();
        }

        Drawable fabIcon = typedArray.getDrawable(4);
        fab.setImageDrawable(fabIcon);

        typedArray.recycle();
    }

    /**
     * Gere les intent lors du démarrage de l'activité
     *
     * @param intent intent
     */
    private void handleIntent(Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {

            Uri query = intent.getData();

            if (query.getAuthority().equals("com.adrastel.search")) {

                String wca_id = query.getLastPathSegment();
                String name = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY);
                searchUser(wca_id, name);

                closeSearchview();
            }
        } else {

            String fragment_tag = intent.getStringExtra(Constants.EXTRAS.FRAGMENT);

            if (fragment_tag != null) {

                switch (fragment_tag) {

                    case ProfileFragment.FRAGMENT_TAG:
                        String wca_id = intent.getStringExtra(Constants.EXTRAS.WCA_ID);
                        String username = intent.getStringExtra(Constants.EXTRAS.USERNAME);

                        BaseFragment fragment = ProfileFragment.newInstance(wca_id, username, false);
                        switchFragment(fragment);
                        break;
                }

            }
        }
    }

    /**
     * Ferme la zone de recherche
     */
    private void closeSearchview() {
        if (searchMenuItem != null) {
            searchMenuItem.collapseActionView();
        }
    }

    /**
     * Lance la page d'un utilisateur
     *
     * @param wca_id wca
     * @param name   competition
     */
    private void searchUser(String wca_id, String name) {

        ProfileFragment profileFragment = ProfileFragment.newInstance(wca_id, name, false);
        switchFragment(profileFragment);

    }

    /**
     * Change le runner fragmentToRun local en global. Le global est lancé lors de la fermuture du Navigation Drawer
     *
     * @param runnable runnable
     */
    private void runWhenDrawerClose(Runnable runnable) {
        this.fragmentToRun = runnable;
    }
}
