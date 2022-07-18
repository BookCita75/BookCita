package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.bookcita.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailsCitationActivity extends AppCompatActivity {

    private static final String TAG = "DetailsCitationActivity";

    private TextView tvTitreDetailCitation;
    private TextView tvTitreDC;
    private TextView tvAuteurDC;
    private ImageView ivCoverDC;

    private TextView tvNbCitationsDC;

    private EditText etPageCitationDC;
    private EditText etmlCitationDC;
    private EditText etmlAnnotationDC;



    private String id_BD;
    private String id_BD_citation;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference citationsRef = db.collection("citations");
    private FirebaseAuth auth;

    String id_user;

    private void init() {
        //init UI
        tvTitreDetailCitation = findViewById(R.id.tvTitreDetailCitation);
        tvTitreDC = findViewById(R.id.tvTitreDC);
        tvAuteurDC = findViewById(R.id.tvAuteurDC);
        ivCoverDC = findViewById(R.id.ivCoverDC);
        tvNbCitationsDC = findViewById(R.id.tvNbCitationsDC);

        etPageCitationDC = findViewById(R.id.etPageCitationDC);
        etmlCitationDC = findViewById(R.id.etmlCitationDC);
        etmlAnnotationDC = findViewById(R.id.etmlAnnotationDC);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_citation);

        Intent intent = getIntent();
        id_BD = intent.getStringExtra(ID_BD);
        id_BD_citation = intent.getStringExtra(ID_BD_CITATION);
        Log.i(TAG, "onCreate: id_BD reçu : " + id_BD);
        Log.i(TAG, "onCreate: id_BD_citation : " + id_BD_citation);

        init();

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();

        getFicheBookFromDB();
        remplirNbCitationsFOBFromDB();
        remplirDetailsCitationFromDB();
    }

    private void getFicheBookFromDB() {


//        Query query = livresRef.whereEqualTo("id", id_BD);

        db.collection("livres")
                .whereEqualTo(documentId(), id_BD)
//                .whereEqualTo("auteur_livre", "Luc Lang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(TAG, document.getId() + " => " + document.getData());
                                String titre = document.getString(TITRE_LIVRE);
                                String auteur = document.getString(AUTEUR_LIVRE);
                                String coverUrl = document.getString(URL_COVER_LIVRE);
                                Log.i(TAG, "onComplete: titre : " + titre);
                                Log.i(TAG, "onComplete: auteur : " + auteur);
                                Log.i(TAG, "onComplete: coverUrl : " + coverUrl);
                                tvTitreDC.setText(titre);
                                tvAuteurDC.setText(auteur);
                                //Gestion de l'image avec Glide
                                Context context = DetailsCitationActivity.this;

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
                                        .into(ivCoverDC);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }

    private void remplirNbCitationsFOBFromDB() {

        db.collection("citations")
                .whereEqualTo("id_user",id_user)
                .whereEqualTo("id_BD_livre", id_BD)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int nbCitationFOB = querySnapshot.size();
                            Log.i(TAG, "onComplete: nbCitationFOB : " + nbCitationFOB);
                            tvNbCitationsDC.setText(String.valueOf(nbCitationFOB));


                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }

    private void remplirDetailsCitationFromDB () {
        db.collection("citations")
                .whereEqualTo("id_user",id_user)
                .whereEqualTo(documentId(), id_BD_citation)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(TAG, document.getId() + " => " + document.getData());
//                                String date = document.getString("date");
//                                String heure = document.getString("heure");
                                String numero_page = document.get("numeroPage").toString();
                                String citation = document.getString("citation");
                                String annotation = document.getString("annotation");

                                etmlCitationDC.setText(citation);
                                etmlAnnotationDC.setText(annotation);
                                etPageCitationDC.setText(numero_page);
                            }

                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }
}