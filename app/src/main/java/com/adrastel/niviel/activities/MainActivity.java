package com.adrastel.niviel.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.dialogs.InfoDialog;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.fragments.CompetitionFragment;
import com.adrastel.niviel.fragments.FollowerFragment;
import com.adrastel.niviel.fragments.ProfileFragment;
import com.adrastel.niviel.fragments.RankingFragment;
import com.adrastel.niviel.models.readable.SuggestionUser;
import com.adrastel.niviel.services.CheckRecordService;
import com.adrastel.niviel.services.EditRecordService;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.cketti.mailto.EmailIntentBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Activité principale
 * <p>
 * Est composée d'un DrawerLayout qui gère plusieurs fragments
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {


    // Code de requête pour redemarrer l'activité
    public static final int RESTART_ACTIVITY = 0;
    public static final String FRAGMENT = "fragment";
    public static final String FRAGMENT_DESTINATION = "fragment";


    // Les vues
    public @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.navigation_view) NavigationView navigationView;
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

    private int adViewed = 0;
    private long adViewedTime = 0;

    private OkHttpClient httpClient = new OkHttpClient();


    // Le reciever
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getIntExtra(EditRecordService.ACTION, EditRecordService.ADD_RECORD_FAILURE)) {

                case EditRecordService.ADD_RECORD_SUCCESS:
                    updateUiOnProfileChange();
                    break;

                case EditRecordService.ADD_RECORD_FAILURE:
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

            preferences
                    .edit()
                    .putBoolean(getString(R.string.pref_first_launch), false)
                    .putLong(getString(R.string.pref_time_first_launch), System.currentTimeMillis())
                    .apply();

            InfoDialog introDialog = InfoDialog.newInstance(R.string.information, R.string.info_no_connection);
            introDialog.show(getSupportFragmentManager(), "IntroDialog");

            startActivity(new Intent(this, MainIntroActivity.class));
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
            if (savedInstanceState != null && fragmentManager.getFragment(savedInstanceState, FRAGMENT) != null) {
                this.fragment = (BaseFragment) fragmentManager.getFragment(savedInstanceState, FRAGMENT);
            } else {
                this.fragment = ProfileFragment.newInstance(prefId);
            }
        }

        switchFragment(fragment);

        final Handler handler = new Handler(Looper.getMainLooper());

        // Lance un fragment lors d'un clique du menu
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
                            /**
                             * Si une pub est affiché, freeze l'app pour une seconde, sinon switch de fragment
                             * Cela a pour but d'eviter les cliques non voulus de l'utilisateur
                             */
                            if (runAdd())
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        switchFragment(fragment);

                                    }
                                }, 1000);

                            else
                                switchFragment(fragment);
                        }
                    });
                }

                return true;
            }
        });

        // Initialisation des annonces
        AppLovinSdk.initializeSdk(this);

        int oldDayOfMonth = preferences.getInt("dayOfMonth", 0);
        int currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Si l'application a été ouverte aujourd'hui, retarde, l'affichage de l'annonce
        if(oldDayOfMonth == currentDayOfMonth)
            adViewedTime = System.currentTimeMillis();

        else
            preferences.edit().putInt("dayOfMonth", currentDayOfMonth).apply();


    }


    /**
     * Affiche une annonce
     */
    public boolean showAdd() {
        if(AppLovinInterstitialAd.isAdReadyToDisplay(this)) {
           adViewed++;
           adViewedTime = System.currentTimeMillis();
           AppLovinInterstitialAd.show(this);

           return true;
        }

        return false;
    }

    public boolean runAdd() {

        // Ne montre pas d'annonce avant les 15 premières minutes du premier lancement de l'app
        if(System.currentTimeMillis() - preferences.getLong(getString(R.string.pref_time_first_launch), 0) >= 15 * 60 * 1000) {

            // Ne montre pas d'annonce à 3 minutes d'intervalles ou à 6 minutes d'intervalles si 5 annonces ont étés montréees
            if ((adViewed >= 5 && System.currentTimeMillis() >= 6 * 60 * 1000) || (System.currentTimeMillis() - adViewedTime >= 3 * 60 * 1000)) {
                return showAdd();
            }
        }

        return false;

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
                fragmentManager.putFragment(outState, FRAGMENT, fragment);
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
    @SuppressLint("StringFormatInvalid")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflation
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Définition du moteur de rechervche
        searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryRefinementEnabled(true);


        final CursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.adapter_suggestion, null,
                new String[] {SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[] {android.R.id.text1},
                0
        );

        final ArrayList<SuggestionUser> suggestions = new ArrayList<>();

        searchView.setSuggestionsAdapter(adapter);



        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                SuggestionUser user = suggestions.get(position);
                searchView.setQuery(user.getName(), false);
                searchView.clearFocus();

                searchUser(user.getWca_id(), user.getName());
                closeSearchview();
                return true;
            }
        });

        /**
         * Envoie des requetes espacées de 500ms pour gagner en fluidité
         * Stocke le temps en ms de la derniere requête
         */
        final AtomicLong lastRequest = new AtomicLong();


        lastRequest.set(0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if(System.currentTimeMillis() - lastRequest.get() >= 500) {

                    HttpUrl url = new WcaUrl()
                            .apiSearch(query)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            try {
                                if (response.isSuccessful()) {
                                    String data = response.body().string();
                                    response.close();

                                    JsonParser jsonParser = new JsonParser();
                                    JsonElement jsonTree = jsonParser.parse(data);

                                    JsonObject jsonObject = jsonTree.getAsJsonObject();

                                    JsonArray result = jsonObject.getAsJsonArray("result");

                                    Gson gson = new Gson();
                                    ArrayList<SuggestionUser> suggestionUsers = gson.fromJson(result, new TypeToken<ArrayList<SuggestionUser>>() {
                                    }.getType());

                                    suggestions.clear();
                                    suggestions.addAll(suggestionUsers);


                                    String[] COLUMNS = {
                                            BaseColumns._ID,
                                            SearchManager.SUGGEST_COLUMN_TEXT_1,
                                            SearchManager.SUGGEST_COLUMN_INTENT_DATA
                                    };
                                    final MatrixCursor cursor = new MatrixCursor(COLUMNS);


                                    int limit = 10;

                                    for (int i = 0; i < suggestions.size() && cursor.getCount() < limit; i++) {

                                        SuggestionUser suggestionUser = suggestions.get(i);

                                        // verifie que l'utilisateur a un id wca
                                        if ((suggestionUser.getType().equals("person") || suggestionUser.getType().equals("suggestionUser")) && suggestionUser.getWca_id() != null) {

                                            String field = getString(R.string.string_details_string, suggestionUser.getName(), suggestionUser.getWca_id());
                                            cursor.addRow(new Object[]{i, field, suggestionUser.getName()});
                                            i++;
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.swapCursor(cursor);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    lastRequest.set(System.currentTimeMillis());
                }

                return false;

            }


        });
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
                if(BuildConfig.DEBUG) {
                    startService(new Intent(this, CheckRecordService.class));
                    return true;
                }
                else {
                    Intent settings = new Intent(this, SettingsActivity.class);
                    startActivityForResult(settings, 0);
                    return true;
                }

            case R.id.dark_light:

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

                EmailIntentBuilder
                        .from(this)
                        .to(getString(R.string.email))
                        .subject(getString(R.string.mail_subject))
                        .body(getString(R.string.mail_body, Build.MODEL, Build.VERSION.RELEASE, Build.VERSION.SDK_INT, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE))
                        .start();

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
        drawerLayout.removeDrawerListener(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityReceiver);

        super.onPause();
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
                return ProfileFragment.newInstance(prefId);

            case R.id.ranking:
                return RankingFragment.newInstance();

            case R.id.competitions:
                return CompetitionFragment.newInstance();

            case R.id.explore:
                return ProfileFragment.newInstance(null, null);

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
                return ProfileFragment.newInstance(prefId);

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
            wca_idView.setText("");
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

            String fragment_tag = intent.getStringExtra(FRAGMENT_DESTINATION);

            if (fragment_tag != null) {

                switch (fragment_tag) {

                    case ProfileFragment.FRAGMENT_TAG:
                        String wca_id = intent.getStringExtra(BaseActivity.WCA_ID);
                        String username = intent.getStringExtra(BaseActivity.USERNAME);

                        BaseFragment fragment = ProfileFragment.newInstance(wca_id, username);
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

        ProfileFragment profileFragment = ProfileFragment.newInstance(wca_id, name);
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
