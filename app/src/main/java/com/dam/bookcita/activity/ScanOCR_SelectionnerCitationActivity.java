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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ScanOCR_SelectionnerCitationActivity extends AppCompatActivity {

    private static final String TAG = "ScanOCR_SelectionnerCit";

    private TextView tvTitreSOS;
    private TextView tvAuteurSOS;
    private ImageView ivCoverSOS;


    private EditText etmlTexteOCR;
    private EditText etmlTexteExtrait;


    private Button btnValiderSelection;

    private String id_BD;
    private String result_text_OCR;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference citationsRef = db.collection(CITATIONS_COLLECTION_BD);
    private FirebaseAuth auth;

    private void init() {
        tvTitreSOS = findViewById(R.id.tvTitreSOS);
        tvAuteurSOS = findViewById(R.id.tvAuteurSOS);
        ivCoverSOS = findViewById(R.id.ivCoverSOS);

        etmlTexteOCR = findViewById(R.id.etmlTexteOCR);
        btnValiderSelection = findViewById(R.id.btnValiderSelection);
        etmlTexteExtrait = findViewById(R.id.etmlTexteExtrait);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ocr_selectionner_citation);

        Intent intent = getIntent();
        id_BD = intent.getStringExtra(ID_BD);
        result_text_OCR = intent.getStringExtra(RESULT_TEXT_OCR);
        Log.i(TAG, "onCreate: id_BD reçu : " + id_BD);

        init();

        getFicheBookFromDB();

        etmlTexteOCR.setText(result_text_OCR);

        btnValiderSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etmlTexteExtrait.getText().toString().isEmpty()) {
                    Toast.makeText(ScanOCR_SelectionnerCitationActivity.this, "Veuillez coller une citation dans le champ texte prévu pour.", Toast.LENGTH_SHORT).show();
                } else {
                    String texteExtrait = etmlTexteExtrait.getText().toString();
                    Log.i(TAG, "onClick: texteExtrait : " + texteExtrait);
                    Intent saisieIntent = new Intent(ScanOCR_SelectionnerCitationActivity.this, SaisieManuelleCitationActivity.class);
                    saisieIntent.putExtra(TYPE_SAISIE_MANUELLE_OR_OCR, SAISIE_OCR);
                    saisieIntent.putExtra(ID_BD, id_BD);
                    saisieIntent.putExtra(TEXTE_EXTRAIT, texteExtrait);
                    startActivity(saisieIntent);
                }

            }
        });
    }

    private void getFicheBookFromDB() {


//        Query query = livresRef.whereEqualTo("id", id_BD);

        db.collection(LIVRES_COLLECTION_BD)
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
                                String titre = document.getString(TITRE_LIVRE_BD);
                                String auteur = document.getString(AUTEUR_LIVRE_BD);
                                String coverUrl = document.getString(URL_COVER_LIVRE_BD);
                                Log.i(TAG, "onComplete: titre : " + titre);
                                Log.i(TAG, "onComplete: auteur : " + auteur);
                                Log.i(TAG, "onComplete: coverUrl : " + coverUrl);
                                tvTitreSOS.setText(titre);
                                tvAuteurSOS.setText(auteur);
                                //Gestion de l'image avec Glide
                                Context context = ScanOCR_SelectionnerCitationActivity.this;

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
                                        .into(ivCoverSOS);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }
}