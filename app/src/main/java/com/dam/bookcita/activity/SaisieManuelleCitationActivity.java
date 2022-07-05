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

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dam.bookcita.model.ModelCitation;

public class SaisieManuelleCitationActivity extends AppCompatActivity {
    private static final String TAG = "SaisieManuelleCitationA";

    private TextView tvTitreSaisie;
    private TextView tvTitreSMC;
    private TextView tvAuteurSMC;
    private ImageView ivCoverSMC;

    private EditText etPageCitation;
    private EditText etmlCitation;
    private EditText etmlAnnotation;


    private Button btnValiderAjoutCitation;

    private String id_BD;
    private String type_saisie="";
    private String texte_extrait;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference citationsRef = db.collection("citations");
    private FirebaseAuth auth;


    private void init() {
        //init UI
        tvTitreSaisie = findViewById(R.id.tvTitreSaisie);
        tvTitreSMC = findViewById(R.id.tvTitreSMC);
        tvAuteurSMC = findViewById(R.id.tvAuteurSMC);
        ivCoverSMC = findViewById(R.id.ivCoverSMC);
        btnValiderAjoutCitation = findViewById(R.id.btnValiderAjoutCitation);

        etPageCitation = findViewById(R.id.etPageCitation);
        etmlCitation = findViewById(R.id.etmlCitation);
        etmlAnnotation = findViewById(R.id.etmlAnnotation);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisie_manuelle_citation);

        Intent intent = getIntent();
        id_BD = intent.getStringExtra(ID_BD);
        type_saisie = intent.getStringExtra(TYPE_SAISIE_MANUELLE_OR_OCR);

        Log.i(TAG, "onCreate: id_BD reçu : " + id_BD);

        init();

        if(type_saisie.equals(SAISIE_OCR)) {
            texte_extrait = intent.getStringExtra(TEXTE_EXTRAIT);
            tvTitreSaisie.setText("Scan OCR saisie citation");
            etmlCitation.setText(texte_extrait);
        }

        getFicheBookFromDB();

        btnValiderAjoutCitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pageCitationStr = etPageCitation.getText().toString();
                Log.i(TAG, "onClick: pageCitationStr : " + pageCitationStr);
                if (pageCitationStr.equals("")) {
                    Toast.makeText(SaisieManuelleCitationActivity.this, "Veuillez saisir un numéro de page.", Toast.LENGTH_LONG).show();
                    return;
                }
                int pageCitation = 0;
                try {
                    pageCitation = Integer.valueOf(pageCitationStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onClick: " + e.getMessage());
                    return;
                }
                Log.i(TAG, "onClick: pageCitation : " + String.valueOf(pageCitation));
                String citation = etmlCitation.getText().toString();
                if (citation.equals("")) {
                    Toast.makeText(SaisieManuelleCitationActivity.this, "Veuillez saisir une citation.", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i(TAG, "onClick: citation : " + citation);
                String annotation = etmlAnnotation.getText().toString();
                Log.i(TAG, "onClick: annotation : " + annotation);
                Date dateToday = new Date();
                Log.i(TAG, "onClick: " + dateToday.toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm");
                //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                String strDateToday = dateFormat.format(dateToday);
                String strHeureNow = heureFormat.format(dateToday);
                Log.i(TAG, "onClick: strDateToday : " + strDateToday);
                Log.i(TAG, "onClick: strHeureNow : " + strHeureNow);

                ModelCitation citationSaisie = new ModelCitation(id_BD,citation, annotation, pageCitation, strDateToday, strHeureNow);
                try {
                    citationsRef.add(citationSaisie);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SaisieManuelleCitationActivity.this, "Problème dans l'enregistrement de la citation.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onClick: " + e.getMessage());
                    return;
                }
                Toast.makeText(SaisieManuelleCitationActivity.this, "Citation enregistrée avec succès.", Toast.LENGTH_SHORT).show();

            }

        });

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
                                tvTitreSMC.setText(titre);
                                tvAuteurSMC.setText(auteur);
                                //Gestion de l'image avec Glide
                                Context context = SaisieManuelleCitationActivity.this;

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
                                        .into(ivCoverSMC);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }


}