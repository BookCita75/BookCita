package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;
import static com.google.firebase.firestore.FieldPath.documentId;


import com.dam.bookcita.R;
import com.dam.bookcita.model.ModelDetailsLivre;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsLivreISBN extends AppCompatActivity {

    private TextView tv_title_livre;
    private TextView tv_auteur_livre;
    private TextView tv_editeur_livre;
    private TextView tv_parution_livre;
    private TextView tv_resume_livre;
    private TextView tv_isbn_livre;
    private TextView tv_nombres_pages_livres;
    private TextView tvLangueDLI;
    private ImageView iv_couverture_livre;

    private String idGoogleBooks;
    private String isbn;

    private boolean existeDeja;

    private Button btn_ajouter_livre_bd;

    private String id_user;

    private FirebaseAuth auth;

    private static final String TAG = "AjouterLivreBD";


    private RequestQueue requestQueue;
    private ModelDetailsLivre detailsLivre;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection(LIVRES_COLLECTION_BD);


    private String title_livre, auteur_livre, editeur_livre, parution_livre, resume_livre, isbn_livre, couvertureImage, langue;
    private int nombres_pages_livres;

    public void initUI() {
        tv_title_livre = findViewById(R.id.tv_title_updat_livre_bd);
        tv_auteur_livre = findViewById(R.id.tv_auteur_updat_livre_bd);
        tv_editeur_livre = findViewById(R.id.tv_updat_editeur_livre_bd);
        tv_parution_livre = findViewById(R.id.tv_updat_parution_livre_bd);
        tv_resume_livre = findViewById(R.id.tv_updat_resume_livre_bd);
        tv_isbn_livre = findViewById(R.id.tv_updat_isbn_livre_bd);
        tv_nombres_pages_livres = findViewById(R.id.tv_nombres_updat_pages_livres_bd);
        iv_couverture_livre = (ImageView) findViewById(R.id.iv_couverture_livre_bd);
        tv_resume_livre.setMovementMethod(new ScrollingMovementMethod());
        tvLangueDLI = findViewById(R.id.tvLangueDLI);
        btn_ajouter_livre_bd = findViewById(R.id.btn_ajouter_livre_bd);

        requestQueue = Volley.newRequestQueue(this);
    }

    public void ajouterLivreBD(View view) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        String id_user = firebaseUser.getUid();


        title_livre = detailsLivre.getTitle_livre();
        auteur_livre = detailsLivre.getAuteur_livre();
        editeur_livre = detailsLivre.getEditeur_livre();
        parution_livre = detailsLivre.getDate_parution_livre();
        resume_livre = detailsLivre.getResume_livre();
        isbn_livre = detailsLivre.getIsbn_livre();
        nombres_pages_livres = detailsLivre.getNbPages_livre();
        couvertureImage = detailsLivre.getUrl_cover_livre();
        langue = detailsLivre.getLangue();

        Log.i(TAG, "isbn livre : " + isbn_livre);
        // String nbPages = nombres_pages_livres.substring(0, nombres_pages_livres.length()-1);

        Log.i(TAG, "Auteeuuuuuur: " + detailsLivre.getAuteur_livre());

        //Enlever le caractére p le textView nbr des pages il prend 244p du coup je dois supprimer p pour le convertir en int aprés
        //String nombreDesPages = nbPages.substring(0, nbPages.length()-1);
        //Log.i(TAG, "ajouterLivreBD: nombreDesPages"+nombreDesPages);

        //int nbPagesSanP = Integer.parseInt(nombreDesPages);
        //Log.i(TAG, "ajouterLivreBD: nombreDesPagesSansP"+nbPagesSanP);

        Date dateToday = new Date();
        Log.i(TAG, "onClick: " + dateToday.toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String strDateToday = dateFormat.format(dateToday);
        String strHeureNow = heureFormat.format(dateToday);

        ModelDetailsLivre livre = new ModelDetailsLivre(title_livre, auteur_livre, editeur_livre, parution_livre, resume_livre, couvertureImage, isbn_livre, nombres_pages_livres, langue, idGoogleBooks, strDateToday, strHeureNow, id_user);

        livresRef.add(livre);
        Log.i(TAG, "ModelDetailsLivre : ISBN" + livre.getIsbn_livre() + " Titre : " + livre.getTitle_livre() + " Nombre des pages : " + livre.getNbPages_livre());

        Log.i(TAG, "ajouterLivreBD: livresRef.getId : " + livresRef.getId());

        Toast.makeText(DetailsLivreISBN.this, getString(R.string.t_book_added_successfully), Toast.LENGTH_SHORT).show();

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra(FRAG_TO_LOAD, MES_LIVRES_FRAGMENT);

        startActivity(mainIntent);
//        Intent intent = new Intent().setClass(this, ListeDesLivresBD.class);
//        startActivity(intent);


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

    public void getBooksFromApi(String isbn, String idGoogleBooks) throws IOException {

        String urlJSONFile = "";
        if (isbn.equals("")) {

            //Toast.makeText(this, "Pas d'isbn", Toast.LENGTH_SHORT).show();
            //https://www.googleapis.com/books/v1/volumes/0W-S9jGe51cC?key=AIzaSyARotakRwdwvBqUpRRHwZ3X7URwamy86G0
            urlJSONFile = "https://www.googleapis.com/books/v1/volumes/" + idGoogleBooks + "?key=AIzaSyARotakRwdwvBqUpRRHwZ3X7URwamy86G0";

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
                    } else {
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

                    String editeur = "";
                    if (volumeInfo.has("publisher")) {
                        editeur = volumeInfo.getString("publisher");
                    }
                    Log.i(TAG, "Editeur : " + editeur);

                    String description = "";
                    if (volumeInfo.has("description")) {
                        description = volumeInfo.getString("description");
                    }
                    Log.i(TAG, "Description : " + description);


                    String dateApparition = "";
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


                    int pageCount = 0;
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
                                        isbn = jsonObjectIsbn.getString("identifier");
                                    }
                                }

                            }
                        }
                    }

                    String langue = "";
                    if (volumeInfo.has("language")) {
                        langue = volumeInfo.getString("language");
                    }
                    Log.i(TAG, "onResponse: langue : " + langue);

                    detailsLivre = new ModelDetailsLivre(titre, auteur, editeur, dateApparition, description, coverUrl, isbn, pageCount, langue, idGoogleBooks);

                    Log.i(TAG, "Details Livre  : " + detailsLivre.getTitle_livre() + " || " + detailsLivre.getAuteur_livre() + " || " + detailsLivre.getEditeur_livre() + " || " + detailsLivre.getDate_parution_livre());

                    tv_title_livre.setText(titre);
                    tv_auteur_livre.setText(auteur);
                    tv_parution_livre.setText(dateApparition);
                    tv_resume_livre.setText(description);
                    tv_editeur_livre.setText(editeur);
                    tv_isbn_livre.setText(isbn);
                    tv_nombres_pages_livres.setText(String.valueOf(pageCount) + "p.");
                    tvLangueDLI.setText(langue);

                    // Utilisation de Glide pour la gestion des images
                    Context context = DetailsLivreISBN.this;

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


                } catch (JSONException e) {
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
        // getSupportActionBar().hide();
        setContentView(R.layout.activity_details_livre_isbn);
        initUI();

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();

        /** #1 Récupération de l'intent **/
        Intent intent = getIntent();
        isbn = intent.getStringExtra(ISBN);
        idGoogleBooks = intent.getStringExtra(ID_GOOGLE_BOOKS);


        //Log.i(TAG, "ISBN BDD: "+detailsLivre.getId());
//        intent.putExtra("IDBD",isbn);
        try {
            verifieExistsBookInDB();
            getBooksFromApi(isbn, idGoogleBooks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verifieExistsBookInDB() {
        existeDeja = false;
        if (!idGoogleBooks.equals("")) {
            // verifie si idGoogleBooks deja present en BD
            db.collection(LIVRES_COLLECTION_BD)
                    .whereEqualTo(ID_USER_LIVRE_BD, id_user)
                    .whereEqualTo(ID_GOOGLE_BOOKS_LIVRE_BD, idGoogleBooks)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                                if (querySnapshot.size() != 0) {
                                    existeDeja = true;
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        // Le livre existe dans la BD
                                        Log.i(TAG, "onComplete: document.getId() :" + document.getId());

                                    }
                                    afterOnCompleteVerifieExistsBookInDB(existeDeja);
                                } else {
                                    existeDeja = false;
                                    afterOnCompleteVerifieExistsBookInDB(existeDeja);
                                }
                            } else {
                                Log.i(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            // On vient de l'ecran scanISBN  => nous n'avons que l'ISBN : verifie si ISBN existe dans la BD
            db.collection(LIVRES_COLLECTION_BD)
                    .whereEqualTo(ID_USER_LIVRE_BD, id_user)
                    .whereEqualTo(ISBN_LIVRE_BD, isbn)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                                if (querySnapshot.size() != 0) {
                                    existeDeja = true;
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        // Le livre existe dans la BD
                                        Log.i(TAG, "onComplete: document.getId() :" + document.getId());

                                    }
                                    afterOnCompleteVerifieExistsBookInDB(existeDeja);
                                } else {
                                    existeDeja = false;
                                    afterOnCompleteVerifieExistsBookInDB(existeDeja);
                                }
                            } else {
                                Log.i(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void afterOnCompleteVerifieExistsBookInDB(boolean existeDeja) {
        if (!existeDeja) {
            btn_ajouter_livre_bd.setVisibility(View.VISIBLE);
        }
    }

}