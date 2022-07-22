package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.dam.bookcita.dialogFragment.SupprimerCitationDialogFragment;
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

    private Button btnModifierCitationDC;
    private Button btnSupprimerCitationDC;

    private String id_BD;
    // static pour que SupprimerCitationDialogFragment puisse appeler supprimerCitation
    private static String id_BD_citation;

    // static pour que SupprimerCitationDialogFragment puisse appeler supprimerCitation
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    // static pour que SupprimerCitationDialogFragment puisse appeler supprimerCitation
    private static CollectionReference citationsRef = db.collection(CITATIONS_COLLECTION_BD);
    private FirebaseAuth auth;

    private String id_user;

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

        btnModifierCitationDC = findViewById(R.id.btnModifierCitationDC);
        btnSupprimerCitationDC = findViewById(R.id.btnSupprimerCitationDC);

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

        btnModifierCitationDC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modifierCitationIntent = new Intent(DetailsCitationActivity.this, ModifierCitationActivity.class);
                modifierCitationIntent.putExtra(ID_BD, id_BD);
                modifierCitationIntent.putExtra(ID_BD_CITATION, id_BD_citation);

                startActivity(modifierCitationIntent);
            }
        });

        btnSupprimerCitationDC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupprimerCitationDialogFragment supprimerCitationDialogFragment = new SupprimerCitationDialogFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                supprimerCitationDialogFragment.show(fragmentManager, "");
            }
        });

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();

        getFicheBookFromDB();
        remplirNbCitationsFOBFromDB();
        remplirDetailsCitationFromDB();
    }

    // static pour que SupprimerCitationDialogFragment puisse appeler supprimerCitation
    public static void supprimerCitation(){

        try {
            Log.i(TAG, "supprimerCitation: id_BD_citation : " + id_BD_citation);
            citationsRef.document(id_BD_citation).delete();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "supprimerCitation: e.getMessage() : " + e.getMessage());
        }

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
                            tvNbCitationsDC.setText(String.valueOf(nbCitationFOB));


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