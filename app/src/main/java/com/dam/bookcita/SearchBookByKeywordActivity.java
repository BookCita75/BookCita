package com.dam.bookcita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SearchBookByKeywordActivity extends AppCompatActivity implements AdapterBook.OnItemClickListener{

    private static final String TAG = "SearchBookByKeywordActi";

    private static final int MAX = 20;
    private static final String MAX_RESULTS = "40";
    private static final String ISBN = "isbn";
    private static final String ID = "id";

    private EditText etKeyword;
    private String keyword = "";
    private String totalItems;
    private RecyclerView rvBookByKeyword;

    private ArrayList<ModelBook> bookArrayList;

    private AdapterBook adapterBook;

    public static final int PERMISSION_INTERNET = 0;

    private RequestQueue requestQueue;  //Pour volley

//    private void remplissageArrayListeEnDur() {
//        ModelBook modelBook1 = new ModelBook("https://books.google.com/books/content?id=StXltAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "Aucun Autre", "John Mac Arthur", "9782890823136");
//        ModelBook modelBook2 = new ModelBook("", "La force d'aimer", "Martin Luther King", "9782356140630");
//
//        bookArrayList.add(modelBook1);
//        bookArrayList.add(modelBook2);
//
//        adapterBook = new AdapterBook(this, bookArrayList);
//
//        rvBookByKeyword.setAdapter(adapterBook);
//
//    }

    // Methode pour verifier les permissions de l'application
    public boolean checkPermission() {
        int INTERNET_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (INTERNET_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET}, INTERNET_PERMISSION);
            Log.i(TAG, "checkPermission: " + "Pas de permission INTERNET");
            return false;
        }
        Log.i(TAG, "checkPermission: " + "Permission INTERNET");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_INTERNET: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.INTERNET)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Please allow internet permission", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lancement de l'app

                    }
                }
            }
        }
    }

    private void init() {
        etKeyword = findViewById(R.id.etKeyword);
        rvBookByKeyword = findViewById(R.id.rvBookByKeyword);

        bookArrayList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        rvBookByKeyword.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        rvBookByKeyword.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book_by_keyword);

        init();

        if (checkPermission()) {
            //remplissageArrayListeEnDur();
        }
    }

    public void searchBook(View view) throws UnsupportedEncodingException {
        bookArrayList.clear();
        keyword = etKeyword.getText().toString().trim();
        Toast.makeText(this, "motCle : " + keyword, Toast.LENGTH_SHORT).show();
        parseJSON();
    }

    public String convertirLienEnHttps(String lien) {
        try {
            URL url_lien = new URL(lien);
            URL url_lienHttps = new URL("https", url_lien.getHost(), url_lien.getPort(), url_lien.getFile());

            String lienHttps = url_lienHttps.toString();
            return lienHttps;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }


    private void parseJSON() throws UnsupportedEncodingException {
        //https://www.googleapis.com/books/v1/volumes?q=souris
        String urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                + URLEncoder.encode(keyword, String.valueOf(StandardCharsets.UTF_8))
                + "&maxResults="
                + MAX_RESULTS
                + "&key="
                + "AIzaSyARotakRwdwvBqUpRRHwZ3X7URwamy86G0";

        Log.i(TAG, "parseJSON: urlJSONFile : " + urlJSONFile);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlJSONFile, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    totalItems = response.getString("totalItems");
                    Log.i(TAG, "onResponse: totalItems : " + totalItems);


                    JSONArray jsonArray = response.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        String id = item.getString("id");
                        Log.i(TAG, "onResponse: id : " + id);

                        JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                        String titre = "";
                        if (volumeInfo.has("title")) {
                            titre = volumeInfo.getString("title");
                        }
                        Log.i(TAG, "onResponse: titre : " + titre);
                        String auteur = "";
                        if (volumeInfo.has("authors")) {
                            JSONArray jsonArrayAuthors = volumeInfo.getJSONArray("authors");
                            // Pour l'instant on ne récupère que l'auteur principal (le 1er)
                            auteur = jsonArrayAuthors.get(0).toString();
                        }
                        Log.i(TAG, "onResponse: auteur : " + auteur);

                        String isbn = "";
                        if (volumeInfo.has("industryIdentifiers")) {
                            JSONArray jsonArrayIndustryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                            // l'ordre des isbn peut varier
                            for (int j = 0; j < jsonArrayIndustryIdentifiers.length(); j++) {
                                JSONObject jsonObjectIsbn = jsonArrayIndustryIdentifiers.getJSONObject(j);
                                if (jsonObjectIsbn.has("type")) {
                                    if (jsonObjectIsbn.getString("type").equals("ISBN_13")) {
                                        if (jsonObjectIsbn.has("identifier")) {
                                            isbn = jsonObjectIsbn.getString("identifier");
                                        }
                                    }

                                }
                            }
                        }


                        Log.i(TAG, "onResponse: isbn : " + isbn);

                        String coverUrl = "";
                        // recuperation de l'url de 'image de couverture
                        if (volumeInfo.has("imageLinks")) {
                            JSONObject jsonObjectImageLinks = volumeInfo.getJSONObject("imageLinks");
                            if (jsonObjectImageLinks.has("thumbnail")) {
                                coverUrl = jsonObjectImageLinks.getString("thumbnail");
                                coverUrl = convertirLienEnHttps(coverUrl);
                            }
                        }

                        Log.i(TAG, "onResponse: coverUrl : " + coverUrl);

                        bookArrayList.add(new ModelBook(coverUrl, titre, auteur, isbn, id));

                    }

                    adapterBook = new AdapterBook(SearchBookByKeywordActivity.this, bookArrayList);
                    rvBookByKeyword.setAdapter(adapterBook);
                    adapterBook.setOnItemClickListener(SearchBookByKeywordActivity.this);

                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }


    @Override
    public void onItemClick(int position, View view) {
        Intent detailIntent = new Intent(this, RecupererLivreISBN.class);
        ModelBook clickItemBook = bookArrayList.get(position);
        detailIntent.putExtra(ISBN, clickItemBook.getIsbn());
        detailIntent.putExtra(ID, clickItemBook.getId());

        startActivity(detailIntent);
    }
}