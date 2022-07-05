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


    private String id_BD;

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
                                String titre = document.getString("title_livre");
                                String auteur = document.getString("auteur_livre");
                                String coverUrl = document.getString("url_cover_livre");
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

        getFicheBookFromDB();

        btnSaisieManuelleCitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saisieManuelleCitationIntent = new Intent(AjoutCitationActivity.this, SaisieManuelleCitationActivity.class);
                saisieManuelleCitationIntent.putExtra(ID_BD, id_BD);
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
}