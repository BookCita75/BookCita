package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;

import static com.google.firebase.firestore.FieldPath.documentId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class AjoutCitationActivity extends AppCompatActivity {

    private static final String TAG = "AjoutCitationActivity";

    private Button btnSaisieManuelleCitation;
    private Button btnScanOCR;
    private Button btnImportFichierTxt;

    private TextView tvTitreAC;
    private TextView tvAuteurAC;
    private ImageView ivCoverAC;

    private TextView tvNbCitations;

    private String id_BD;
    private String id_user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection("livres");
    private FirebaseAuth auth;


    private void init() {
        //init UI
        btnSaisieManuelleCitation = findViewById(R.id.btnSaisieManuelleCitation);
        btnScanOCR = findViewById(R.id.btnScanOCR);
        btnImportFichierTxt = findViewById(R.id.btnImportFichierTxt);

        tvTitreAC = findViewById(R.id.tvTitreAC);
        tvAuteurAC = findViewById(R.id.tvAuteurAC);
        ivCoverAC = findViewById(R.id.ivCoverAC);
        tvNbCitations = findViewById(R.id.tvNbCitations);


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
                                tvTitreAC.setText(titre);
                                tvAuteurAC.setText(auteur);
                                //Gestion de l'image avec Glide
                                Context context = AjoutCitationActivity.this ;

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
                                        .into(ivCoverAC);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_citation);

        init();

        Intent intent = getIntent();
        id_BD = intent.getStringExtra(ID_BD);
        Log.i(TAG, "onCreate: id_BD reçu : " + id_BD);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();

        getFicheBookFromDB();
        remplirNbCitationsFOBFromDB();

        btnSaisieManuelleCitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saisieManuelleCitationIntent = new Intent(AjoutCitationActivity.this, SaisieManuelleCitationActivity.class);
                saisieManuelleCitationIntent.putExtra(ID_BD, id_BD);
                saisieManuelleCitationIntent.putExtra(TYPE_SAISIE_MANUELLE_OR_OCR, SAISIE_MANUELLE);
                startActivity(saisieManuelleCitationIntent);
            }
        });

        btnScanOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraXIntent = new Intent(AjoutCitationActivity.this, CameraXActivity.class);
                cameraXIntent.putExtra(TYPE_ISBN_OR_OCR, "OCR");
                cameraXIntent.putExtra(ID_BD, id_BD);
                startActivity(cameraXIntent);
            }
        });

        btnImportFichierTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent importTxtFileIntent = new Intent(AjoutCitationActivity.this, ImportTxtFileActivity.class);
                importTxtFileIntent.putExtra(ID_BD, id_BD);
                startActivity(importTxtFileIntent);
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
                            tvNbCitations.setText(String.valueOf(nbCitationFOB));
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }
}