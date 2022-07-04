package com.dam.bookcita;

import static com.dam.bookcita.common.Constantes.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private FragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);

        FragmentManager fragmentManager = getSupportFragmentManager();
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


        switch (intentFragment){
            case ACCUEIL_FRAGMENT:
                viewPager2.setCurrentItem(intentFragment);
                break;

            case MES_LIVRES_FRAGMENT:
                viewPager2.setCurrentItem(intentFragment);
                break;
        }

    }


}