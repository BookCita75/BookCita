package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.ANNOTATION_CITATION_BD;
import static com.dam.bookcita.common.Constantes.AUTEUR_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.CITATIONS_COLLECTION_BD;
import static com.dam.bookcita.common.Constantes.CITATION_CITATION_BD;
import static com.dam.bookcita.common.Constantes.DATE_PARUTION_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.EDITEUR_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.FRAG_TO_LOAD;
import static com.dam.bookcita.common.Constantes.ID_BD;
import static com.dam.bookcita.common.Constantes.ID_BD_CITATION;
import static com.dam.bookcita.common.Constantes.ID_BD_LIVRE_CITATION_BD;
import static com.dam.bookcita.common.Constantes.ID_USER_CITATION_BD;
import static com.dam.bookcita.common.Constantes.ISBN_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.LANGUE_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.LIVRES_COLLECTION_BD;
import static com.dam.bookcita.common.Constantes.MES_CITATIONS_FRAGMENT;
import static com.dam.bookcita.common.Constantes.MES_LIVRES_FRAGMENT;
import static com.dam.bookcita.common.Constantes.NB_PAGES_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.NUMERO_PAGE_CITATION_BD;
import static com.dam.bookcita.common.Constantes.RESUME_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.TITRE_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.URL_COVER_LIVRE_BD;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ModifierCitationActivity extends AppCompatActivity {

    private static final String TAG = "ModifierCitationActivit";
    private TextView tvTitreMC;
    private TextView tvAuteurMC;
    private ImageView ivCoverMC;

    private TextView tvNbCitationsMC;

    private EditText etPageCitationMC;
    private EditText etmlCitationMC;
    private EditText etmlAnnotationMC;

    private Button btnEnregistrerMC;

    private String id_BD;
    private String id_BD_citation;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference citationsRef = db.collection(CITATIONS_COLLECTION_BD);
    private FirebaseAuth auth;

    private String id_user;

    private void init() {
        //init UI
        tvTitreMC = findViewById(R.id.tvTitreMC);
        tvAuteurMC = findViewById(R.id.tvAuteurMC);
        ivCoverMC = findViewById(R.id.ivCoverMC);
        tvNbCitationsMC = findViewById(R.id.tvNbCitationsMC);

        etPageCitationMC = findViewById(R.id.etPageCitationMC);
        etmlCitationMC = findViewById(R.id.etmlCitationMC);
        etmlAnnotationMC = findViewById(R.id.etmlAnnotationMC);

        btnEnregistrerMC = findViewById(R.id.btnEnregistrerMC);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_citation);

        Intent intent = getIntent();
        id_BD = intent.getStringExtra(ID_BD);
        id_BD_citation = intent.getStringExtra(ID_BD_CITATION);
        Log.i(TAG, "onCreate: id_BD reçu : " + id_BD);
        Log.i(TAG, "onCreate: id_BD_citation : " + id_BD_citation);

        init();

        btnEnregistrerMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String numeroPageStr = etPageCitationMC.getText().toString();
                    int numeroPage = 0;
                    if(numeroPageStr.equals("")) {
                        Toast.makeText(ModifierCitationActivity.this, "Veuillez saisir un numéro de page.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    numeroPage = Integer.parseInt(numeroPageStr);
                    Log.i(TAG, "numeroPage: "+ numeroPage);

                    String citation = etmlCitationMC.getText().toString();
                    if (citation.equals("")) {
                        Toast.makeText(ModifierCitationActivity.this, "Veuillez saisir une citation.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String annotation = etmlAnnotationMC.getText().toString();

                    citationsRef.document(id_BD_citation).update(
                            NUMERO_PAGE_CITATION_BD, numeroPage,
                            CITATION_CITATION_BD, citation,
                            ANNOTATION_CITATION_BD, annotation
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onClick: e.getMessage() : " + e.getMessage());
                    Toast.makeText(ModifierCitationActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


                Toast.makeText(ModifierCitationActivity.this, "Modification enregistrée avec succès.", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(ModifierCitationActivity.this, MainActivity.class);
                mainIntent.putExtra(FRAG_TO_LOAD, MES_CITATIONS_FRAGMENT);

                startActivity(mainIntent);
            }
        });

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();

        getFicheBookFromDB();
        remplirNbCitationsFOBFromDB();
        remplirDetailsCitationFromDB();

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
                                tvTitreMC.setText(titre);
                                tvAuteurMC.setText(auteur);
                                //Gestion de l'image avec Glide
                                Context context = ModifierCitationActivity.this;

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
                                        .into(ivCoverMC);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }

    private void remplirNbCitationsFOBFromDB() {

        db.collection(CITATIONS_COLLECTION_BD)
                .whereEqualTo(ID_USER_CITATION_BD,id_user)
                .whereEqualTo(ID_BD_LIVRE_CITATION_BD, id_BD)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int nbCitationFOB = querySnapshot.size();
                            Log.i(TAG, "onComplete: nbCitationFOB : " + nbCitationFOB);
                            tvNbCitationsMC.setText(String.valueOf(nbCitationFOB));


                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });

    }

    private void remplirDetailsCitationFromDB () {
        db.collection(CITATIONS_COLLECTION_BD)
                .whereEqualTo(ID_USER_CITATION_BD,id_user)
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
//                                String date = document.getString(DATE_CITATION_BD);
//                                String heure = document.getString(HEURE_CITATION_BD);
                                String numero_page = document.get(NUMERO_PAGE_CITATION_BD).toString();
                                String citation = document.getString(CITATION_CITATION_BD);
                                String annotation = document.getString(ANNOTATION_CITATION_BD);

                                etmlCitationMC.setText(citation);
                                etmlAnnotationMC.setText(annotation);
                                etPageCitationMC.setText(numero_page);
                            }

                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }
}