package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.ID_GOOGLE_BOOKS;
import static com.dam.bookcita.common.Constantes.ISBN;
import static com.dam.bookcita.common.Util.convertirLienEnHttps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dam.bookcita.R;
import com.dam.bookcita.adapter.AdapterBook;
import com.dam.bookcita.fragment.RechercherFragment;
import com.dam.bookcita.model.ModelBook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RechercheAvanceeLivreActivity extends AppCompatActivity implements AdapterBook.OnItemClickListener {

    private static final String TAG = "RechercheAvanceeLivreAc";
    private String totalItems;

    private EditText etKeywordRA;
    private EditText etAuteurRA;
    private EditText etTitreRA;
    private EditText etIsbnRA;
    private EditText etLangueRA;

    private Button btnClearTextRA;
    private Button btnClearTextAuteurRA;
    private Button btnClearTextTitreRA;
    private Button btnClearTextIsbnRA;
    private Button btnClearTextLangueRA;

    private Button btnRechercherRA;

    private RecyclerView rvBookRA;

    private String keywordRA = "";
    private String auteurRA = "";
    private String titreRA = "";
    private String isbnRA = "";
    private String langueRA = "";

    private ArrayList<ModelBook> bookArrayList;
    private AdapterBook adapterBook;
    private RequestQueue requestQueue;

    private static final String MAX_RESULTS = "40";
    private static final String KEY_VALUE = "AIzaSyARotakRwdwvBqUpRRHwZ3X7URwamy86G0";

    private void init(){
        etKeywordRA = findViewById(R.id.etKeywordRA);
        etAuteurRA = findViewById(R.id.etAuteurRA);
        etTitreRA = findViewById(R.id.etTitreRA);
        etIsbnRA = findViewById(R.id.etIsbnRA);
        etLangueRA = findViewById(R.id.etLangueRA);

        btnClearTextRA = findViewById(R.id.btnClearTextRA);
        btnClearTextAuteurRA = findViewById(R.id.btnClearTextAuteurRA);
        btnClearTextTitreRA = findViewById(R.id.btnClearTextTitreRA);
        btnClearTextIsbnRA = findViewById(R.id.btnClearTextIsbnRA);
        btnClearTextLangueRA = findViewById(R.id.btnClearTextLangueRA);
        btnRechercherRA = findViewById(R.id.btnRechercherRA);

        rvBookRA = findViewById(R.id.rvBookRA);

        bookArrayList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(RechercheAvanceeLivreActivity.this);

        rvBookRA.setLayoutManager(new LinearLayoutManager(RechercheAvanceeLivreActivity.this,
                LinearLayoutManager.VERTICAL, false));
        rvBookRA.setItemAnimator(new DefaultItemAnimator());

    }

    private void parseJSON() throws UnsupportedEncodingException {
        String urlJSONFile;
        // il faut q soit egal a quelque chose sinon erreur => ici il faut que q soit "" et non pas rien

        String keywordRAEncoded;
        String auteurRAEncoded;
        String titreRAEncoded;
        String isbnRAEncoded;

        if (langueRA.equals("")) {
            if (keywordRA.equals("")) {
                keywordRAEncoded = "%22%22";
            } else {
                keywordRAEncoded = URLEncoder.encode(keywordRA, String.valueOf(StandardCharsets.UTF_8));
            }

            urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                    + keywordRAEncoded
                    + "&maxResults="
                    + MAX_RESULTS
                    + "&key="
                    + KEY_VALUE;



            if (!isbnRA.equals("")) {
                isbnRAEncoded = URLEncoder.encode(isbnRA, String.valueOf(StandardCharsets.UTF_8));
                urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                        + keywordRAEncoded
                        + "+isbn:"
                        + isbnRAEncoded
                        + "&maxResults="
                        + MAX_RESULTS
                        + "&key="
                        + KEY_VALUE;
            }

            if (!auteurRA.equals("")) {
                auteurRAEncoded = URLEncoder.encode(auteurRA, String.valueOf(StandardCharsets.UTF_8));
                // https://www.googleapis.com/books/v1/volumes?q=flowers+inauthor:keyes&key=yourAPIKey
                urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                        + keywordRAEncoded
                        + "+inauthor:"
                        + auteurRAEncoded
                        + "&maxResults="
                        + MAX_RESULTS
                        + "&key="
                        + KEY_VALUE;

                if (!titreRA.equals("")) {
                    titreRAEncoded = URLEncoder.encode(titreRA, String.valueOf(StandardCharsets.UTF_8));
                    urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                            + keywordRAEncoded
                            + "+inauthor:"
                            + auteurRAEncoded
                            + "+intitle:"
                            + titreRAEncoded
                            + "&maxResults="
                            + MAX_RESULTS
                            + "&key="
                            + KEY_VALUE;
                }
            }
            if (auteurRA.equals("")) {
                if (!titreRA.equals("")) {
                    titreRAEncoded = URLEncoder.encode(titreRA, String.valueOf(StandardCharsets.UTF_8));
                    urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                            + keywordRAEncoded
                            + "+intitle:"
                            + titreRAEncoded
                            + "&maxResults="
                            + MAX_RESULTS
                            + "&key="
                            + KEY_VALUE;
                }
            }
        }
        else {
            if (! (
                    (langueRA.equals("fr"))
                    || (langueRA.equals("en"))
                    || (langueRA.equals("de"))
                  ) ) {
                Toast.makeText(this, "Veuillez saisir comme langue fr, en ou de", Toast.LENGTH_LONG).show();
                return;
            } else {
                // &langRestrict=
                if (keywordRA.equals("")) {
                    keywordRAEncoded = "%22%22";
                } else {
                    keywordRAEncoded = URLEncoder.encode(keywordRA, String.valueOf(StandardCharsets.UTF_8));
                }

                urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                        + keywordRAEncoded
                        + "&langRestrict="
                        + langueRA
                        + "&maxResults="
                        + MAX_RESULTS
                        + "&key="
                        + KEY_VALUE;

                if (!isbnRA.equals("")) {
                    isbnRAEncoded = URLEncoder.encode(isbnRA, String.valueOf(StandardCharsets.UTF_8));
                    urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                            + keywordRAEncoded
                            + "+isbn:"
                            + isbnRAEncoded
                            + "&langRestrict="
                            + langueRA
                            + "&maxResults="
                            + MAX_RESULTS
                            + "&key="
                            + KEY_VALUE;
                }

                if (!auteurRA.equals("")) {
                    auteurRAEncoded = URLEncoder.encode(auteurRA, String.valueOf(StandardCharsets.UTF_8));
                    // https://www.googleapis.com/books/v1/volumes?q=flowers+inauthor:keyes&key=yourAPIKey
                    urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                            + keywordRAEncoded
                            + "+inauthor:"
                            + auteurRAEncoded
                            + "&langRestrict="
                            + langueRA
                            + "&maxResults="
                            + MAX_RESULTS
                            + "&key="
                            + KEY_VALUE;

                    if (!titreRA.equals("")) {
                        titreRAEncoded = URLEncoder.encode(titreRA, String.valueOf(StandardCharsets.UTF_8));
                        urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                                + keywordRAEncoded
                                + "+inauthor:"
                                + auteurRAEncoded
                                + "+intitle:"
                                + titreRAEncoded
                                + "&langRestrict="
                                + langueRA
                                + "&maxResults="
                                + MAX_RESULTS
                                + "&key="
                                + KEY_VALUE;
                    }
                }
                if (auteurRA.equals("")) {
                    if (!titreRA.equals("")) {
                        titreRAEncoded = URLEncoder.encode(titreRA, String.valueOf(StandardCharsets.UTF_8));
                        urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                                + keywordRAEncoded
                                + "+intitle:"
                                + titreRAEncoded
                                + "&langRestrict="
                                + langueRA
                                + "&maxResults="
                                + MAX_RESULTS
                                + "&key="
                                + KEY_VALUE;
                    }
                }
            }


        }






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

                        String idGoogleBooks = item.getString("id");
                        Log.i(TAG, "onResponse: idGoogleBooks : " + idGoogleBooks);

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

                        bookArrayList.add(new ModelBook(coverUrl, titre, auteur, isbn, idGoogleBooks));

                    }

                    adapterBook = new AdapterBook(RechercheAvanceeLivreActivity.this, bookArrayList);
                    rvBookRA.setAdapter(adapterBook);
                    adapterBook.setOnItemClickListener(RechercheAvanceeLivreActivity.this);

                } catch (
                        JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "onResponse: JSONException : " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i(TAG, "onErrorResponse: " + error.getMessage());
                Toast.makeText(RechercheAvanceeLivreActivity.this, "Veuillez vérifier votre connexion à Internet\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_avancee_livre);

        init();

        btnRechercherRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookArrayList.clear();

                keywordRA = etKeywordRA.getText().toString();
                auteurRA = etAuteurRA.getText().toString();
                titreRA = etTitreRA.getText().toString();
                isbnRA = etIsbnRA.getText().toString();
                langueRA = etLangueRA.getText().toString();

                try {
                    parseJSON();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        btnClearTextRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etKeywordRA.setText("");
                btnClearTextRA.setVisibility(View.GONE);
            }
        });

        btnClearTextAuteurRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAuteurRA.setText("");
                btnClearTextAuteurRA.setVisibility(View.GONE);
            }
        });

        btnClearTextTitreRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTitreRA.setText("");
                btnClearTextTitreRA.setVisibility(View.GONE);
            }
        });

        btnClearTextIsbnRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etIsbnRA.setText("");
                btnClearTextIsbnRA.setVisibility(View.GONE);
            }
        });

        btnClearTextLangueRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLangueRA.setText("");
                btnClearTextLangueRA.setVisibility(View.GONE);
            }
        });



        etKeywordRA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    btnClearTextRA.setVisibility(View.VISIBLE);
                } else {
                    btnClearTextRA.setVisibility(View.GONE);
                }
            }
        });

        etAuteurRA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    btnClearTextAuteurRA.setVisibility(View.VISIBLE);
                } else {
                    btnClearTextAuteurRA.setVisibility(View.GONE);
                }
            }
        });

        etTitreRA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    btnClearTextTitreRA.setVisibility(View.VISIBLE);
                } else {
                    btnClearTextTitreRA.setVisibility(View.GONE);
                }
            }
        });

        etIsbnRA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    btnClearTextIsbnRA.setVisibility(View.VISIBLE);
                } else {
                    btnClearTextIsbnRA.setVisibility(View.GONE);
                }
            }
        });

        etLangueRA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    btnClearTextLangueRA.setVisibility(View.VISIBLE);
                } else {
                    btnClearTextLangueRA.setVisibility(View.GONE);
                }
            }
        });
    }




    @Override
    public void onItemClick(int position, View view) {
        Intent detailIntent = new Intent(this, DetailsLivreISBN.class);
        ModelBook clickItemBook = bookArrayList.get(position);
        detailIntent.putExtra(ISBN, clickItemBook.getIsbn());
        detailIntent.putExtra(ID_GOOGLE_BOOKS, clickItemBook.getIdGoogleBooks());

        startActivity(detailIntent);
    }
}