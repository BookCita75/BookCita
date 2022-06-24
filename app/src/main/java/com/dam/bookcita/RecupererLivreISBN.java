package com.dam.bookcita;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class RecupererLivreISBN extends AppCompatActivity{

    private TextView tv_title_livre;
    private TextView tv_auteur_livre;
    private TextView tv_editeur_livre;
    private TextView tv_parution_livre;
    private TextView tv_resume_livre;
    private TextView tv_isbn_livre;
    private TextView tv_nombres_pages_livres;
    private ImageView iv_couverture_livre;
    private static final String TAG = "tag";

    private static final String ISBN = "isbn";
    private static final String ID = "id";

    private RequestQueue requestQueue;


    public void initUI(){
        tv_title_livre = findViewById(R.id.tv_title_livre);
        tv_auteur_livre = findViewById(R.id.tv_auteur_livre);
        tv_editeur_livre = findViewById(R.id.tv_editeur_livre);
        tv_parution_livre = findViewById(R.id.tv_parution_livre);
        tv_resume_livre = findViewById(R.id.tv_resume_livre);
        tv_isbn_livre = findViewById(R.id.tv_isbn_livre);
        tv_nombres_pages_livres = findViewById(R.id.tv_nombres_pages_livres);
        iv_couverture_livre = (ImageView) findViewById(R.id.iv_couverture_livre);
        tv_resume_livre.setMovementMethod(new ScrollingMovementMethod());

        requestQueue = Volley.newRequestQueue(this);


    }
    public String convertirLienEnHttps(String lien){
        try {
            URL url_lien = new URL(lien);
            URL url_lienHttps = new URL("https", url_lien.getHost(),url_lien.getPort(),url_lien.getFile());

            String lienHttps = url_lienHttps.toString();
            return lienHttps;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    public void getBooksFromApi(String isbn, String id) throws IOException {

        String urlJSONFile = "";
        if (isbn.equals("")) {

            //Toast.makeText(this, "Pas d'isbn", Toast.LENGTH_SHORT).show();
            //https://www.googleapis.com/books/v1/volumes/0W-S9jGe51cC?key=AIzaSyARotakRwdwvBqUpRRHwZ3X7URwamy86G0
            urlJSONFile = "https://www.googleapis.com/books/v1/volumes/" + id + "?key=AIzaSyARotakRwdwvBqUpRRHwZ3X7URwamy86G0";

        } else {
            urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q=%22%22+isbn:" + isbn;
            //Log.i(TAG, "parseJSON: " + urlJSONFile);
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlJSONFile, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject volumeInfo;

                    if (!isbn.equals("")) {
                        JSONArray jsonArray = response.getJSONArray("items");
                        JSONObject item = jsonArray.getJSONObject(0);

                        volumeInfo = item.getJSONObject("volumeInfo");
                    }
                    else {
                        volumeInfo = response.getJSONObject("volumeInfo");
                    }

                    String titre = "";
                    if (volumeInfo.has("title")) {
                        titre = volumeInfo.getString("title");
                    }
                    Log.i(TAG, "Titre : " + titre);

                    String auteur = "";
                    if (volumeInfo.has("authors")) {
                        JSONArray jsonArrayAuthors = volumeInfo.getJSONArray("authors");
                        auteur = jsonArrayAuthors.get(0).toString();
                    }
                    Log.i(TAG, "Auteur : " + auteur);

                    String editeur="";
                    if (volumeInfo.has("publisher")) {
                        editeur = volumeInfo.getString("publisher");
                    }
                    Log.i(TAG, "Editeur : " + editeur);

                    String description="";
                    if (volumeInfo.has("description")) {
                        description = volumeInfo.getString("description");
                    }
                    Log.i(TAG, "Description : " + description);


                    String dateApparition="";
                    if (volumeInfo.has("publishedDate")) {
                        dateApparition = volumeInfo.getString("publishedDate");
                    }
                    Log.i(TAG, "Date d'apparition : " + dateApparition);


                    String coverUrl = "";
                    // recuperation de l'url de 'image de couverture
                    if (volumeInfo.has("imageLinks")) {
                        JSONObject jsonObjectImageLinks = volumeInfo.getJSONObject("imageLinks");
                        if (jsonObjectImageLinks.has("thumbnail")) {
                            coverUrl = jsonObjectImageLinks.getString("thumbnail");
                            coverUrl = convertirLienEnHttps(coverUrl);
                        }
                    }
                    Log.i(TAG, "CoverUrl : " + coverUrl);


                    int pageCount=0;
                    if (volumeInfo.has("pageCount")) {
                        pageCount = volumeInfo.getInt("pageCount");
                    }
                    Log.i(TAG, "pageCount : " + pageCount);

                    String isbn = "";
                    if (volumeInfo.has("industryIdentifiers")) {
                        JSONArray jsonArrayIndustryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                        // l'ordre des isbn peut varier
                        for (int j = 0; j < jsonArrayIndustryIdentifiers.length(); j++) {
                            JSONObject jsonObjectIsbn = jsonArrayIndustryIdentifiers.getJSONObject(j);
                            if (jsonObjectIsbn.has("type")) {
                                if (jsonObjectIsbn.getString("type").equals("ISBN_13")) {
                                    if (jsonObjectIsbn.has("identifier")) {
                                        isbn = "ISBN:"+jsonObjectIsbn.getString("identifier");
                                    }
                                }

                            }
                        }
                    }


                   ModelDetailsLivre detailsLivre = new ModelDetailsLivre(titre, auteur, editeur, dateApparition, description, coverUrl,isbn,pageCount);

                    Log.i(TAG, "Details Livre  : " + detailsLivre.getTitle_livre() + " || " + detailsLivre.getAuteur_livre() +" || " + detailsLivre.getEditeur_livre()+" || "+detailsLivre.getDate_parution_livre());

                    tv_title_livre.setText(titre);
                    tv_auteur_livre.setText(auteur);
                    tv_parution_livre.setText(dateApparition);
                    tv_resume_livre.setText(description);
                    tv_editeur_livre.setText(editeur);
                    tv_isbn_livre.setText(isbn);
                    tv_nombres_pages_livres.setText(String.valueOf(pageCount)+"p.");

//                    Uri myUri = Uri.parse(coverUrl);
//                    iv_couverture_livre.setImageURI(myUri);
//                    Log.i(TAG, "myUri : " + myUri);
//
//                    URL url = new URL(coverUrl);
//                    try {
//                        HttpURLConnection http = (HttpURLConnection) url.openConnection();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

//                    URL url = new URL(coverUrl);
//                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                    iv_couverture_livre.setImageBitmap(bmp);



                    // Utilisation de Glide pour la gestion des images
                    Context context = RecupererLivreISBN.this ;

                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .error(R.drawable.ic_couverture_livre_150)
                            .placeholder(R.drawable.ic_couverture_livre_150);

                    // methode normale
                    Glide.with(context)
                            .load(coverUrl)
                            .apply(options)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(iv_couverture_livre);


                }catch (JSONException  e) {
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
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        toolbar.setTitle("Details d'un Livre");
        toolbar.showOverflowMenu();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_recuperer_livre_isbn);
        initUI();

        /** #1 Récupération de l'intent **/
        Intent intent = getIntent();
        String isbn = intent.getStringExtra(ISBN);
        String id = intent.getStringExtra(ID);



        try {
            getBooksFromApi(isbn, id);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}