package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.dam.bookcita.adapter.FragmentAdapter;
import com.dam.bookcita.R;
import com.dam.bookcita.fragment.AccueilFragment;
import com.dam.bookcita.fragment.MesCitationsFragment;
import com.dam.bookcita.fragment.MesLivresFragment;
import com.dam.bookcita.fragment.RechercherFragment;
import com.dam.bookcita.profile.ProfileActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Variables globales
     **/
    Toolbar toolbar;
    DrawerLayout drawer_layout;

    // La gestion des fragments
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    // Gestion de la NavigationView
    private NavigationView navigationView;

    // Variable emplacement
    private static final String emplacement
            = MainActivity.class.getSimpleName();

    /**
     * Faire le lien entre les widgets et le design
     **/
    public void initUI() {
        toolbar = findViewById(R.id.toolbar);
        drawer_layout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_navigationView);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);
    }



    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private FragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Appel de la méthode d'initialisation de l'UI
        initUI();
        // Ajout du support pour la gestion de la Toolbar
        setSupportActionBar(toolbar);

        // Ajout de la gestion des options d'accessibilité
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, // Le context de l'activité
                drawer_layout, // Le layout du MainActivity
                toolbar, // La toolbar
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        // Ajout d'un listener sur le bouton hamburger
        drawer_layout.addDrawerListener(toggle);
        // Synchro entre le bouton hamburger et le menu
        toggle.syncState();

        // Gestion du clic sur un des item du menu
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            addFragment();
            // Force l'affichage du 1er fragment au démarrage
            navigationView.setCheckedItem(R.id.nav_fragment_accueil);
        }



//        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);

        TabLayout.Tab tabAccueil = tabLayout.newTab().setText("Accueil");
        tabAccueil.setIcon(R.drawable.ic_accueil_24);

        TabLayout.Tab tabMesLivres = tabLayout.newTab().setText("Mes livres");
        tabMesLivres.setIcon(R.drawable.ic_books_bottom_bar_24);

        TabLayout.Tab tabMesCitations = tabLayout.newTab().setText("Citations");
        tabMesCitations.setIcon(R.drawable.ic_baseline_format_quote_24);

        TabLayout.Tab tabChercher = tabLayout.newTab().setText("Chercher");
        tabChercher.setIcon(R.drawable.ic_search_barre_bottom_24);


        tabLayout.addTab(tabAccueil);
        tabLayout.addTab(tabMesLivres);
        tabLayout.addTab(tabMesCitations);
        tabLayout.addTab(tabChercher);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        Intent intent = getIntent();
        int intentFragment = intent.getIntExtra(FRAG_TO_LOAD, ACCUEIL_FRAGMENT);

        viewPager2.setCurrentItem(intentFragment);
        /*
        switch (intentFragment){
            case ACCUEIL_FRAGMENT:
                viewPager2.setCurrentItem(intentFragment);
                break;

            case MES_LIVRES_FRAGMENT:
                viewPager2.setCurrentItem(intentFragment);
                break;

            case MES_CITATIONS_FRAGMENT:
                viewPager2.setCurrentItem(intentFragment);
                break;
        }*/

    }

    private void addFragment() {
        fragmentManager = getSupportFragmentManager();
        // Commencer la discussion
//        fragmentTransaction = fragmentManager.beginTransaction();
//        // Appel du nouveau fragment
//        AccueilFragment accueilFragment = new AccueilFragment();
//        // Ajouter au container de fragment
//        fragmentTransaction.add(R.id.fragment_container, accueilFragment);
//        // Finalisation de la création du fragment
//        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_fragment_accueil:
//                getSupportFragmentManager().
//                        beginTransaction().
//                        replace(R.id.fragment_container, new AccueilFragment()).
//                        commit();
                Intent accueilIntent = new Intent(getApplicationContext(), MainActivity.class);
                accueilIntent.putExtra(FRAG_TO_LOAD, ACCUEIL_FRAGMENT);
                startActivity(accueilIntent);
                break;
            case R.id.nav_fragment_mes_livres:
//                getSupportFragmentManager().
//                        beginTransaction().
//                        replace(R.id.fragment_container, new MesLivresFragment()).
//                        commit();
                Intent mesLivresIntent = new Intent(getApplicationContext(), MainActivity.class);
                mesLivresIntent.putExtra(FRAG_TO_LOAD, MES_LIVRES_FRAGMENT);
                startActivity(mesLivresIntent);
                break;
            case R.id.nav_fragment_mes_citations:
//                getSupportFragmentManager().
//                        beginTransaction().
//                        replace(R.id.fragment_container, new MesCitationsFragment()).
//                        commit();
                Intent mesCitationsIntent = new Intent(getApplicationContext(), MainActivity.class);
                mesCitationsIntent.putExtra(FRAG_TO_LOAD, MES_CITATIONS_FRAGMENT);
                startActivity(mesCitationsIntent);
                break;
            case R.id.nav_fragment_rechercher:
//                getSupportFragmentManager().
//                        beginTransaction().
//                        replace(R.id.fragment_container, new RechercherFragment()).
//                        commit();
                Intent rechercherIntent = new Intent(getApplicationContext(), MainActivity.class);
                rechercherIntent.putExtra(FRAG_TO_LOAD, RECHERCHER_FRAGMENT);
                startActivity(rechercherIntent);
                break;
            case R.id.nav_fragment_profil:
//                getSupportFragmentManager().
//                        beginTransaction().
//                        replace(R.id.fragment_container, new Fragment_05()).
//                        commit();
//                break;
                Intent profilIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profilIntent);
        }

        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }
}