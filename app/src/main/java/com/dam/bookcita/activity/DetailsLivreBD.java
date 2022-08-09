package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;
import static com.dam.bookcita.common.Util.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.bookcita.R;
import com.dam.bookcita.dialogFragment.SupprimerLivreDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailsLivreBD extends AppCompatActivity {

    private Button btnAjouterCitation, btnModifierLivreBD, btnSupprimerLivreBD;
    private String title_livre, auteur_livre, editeur_livre, parution_livre, resume_livre, isbn_livre, couvertureImage, langue, etiquette;
    long nombres_pages_livres;
    private TextView tv_title_livre;
    private TextView tv_auteur_livre;
    private TextView tv_editeur_livre;
    private TextView tv_parution_livre;
    private TextView tv_resume_livre;
    private TextView tv_isbn_livre;
    private TextView tv_nombres_pages_livres;
    private TextView tvLangueDLBD;
    private ImageView iv_couverture_livre;

    private RadioButton rBtnAucuneDL;
    private RadioButton rBtnEnCoursDL;
    private RadioButton rBtnLuDL;
    private RadioButton rBtnALireDL;
    private RadioButton rBtnALire2eTpsDL;


    private static final String TAG = "DetailsLivreBD";
    private RequestQueue requestQueue;

    private static String id_BD;
    private static String id_user;


    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference livresRef = db.collection(LIVRES_COLLECTION_BD);
    private static CollectionReference citationsRef = db.collection(CITATIONS_COLLECTION_BD);

    private FirebaseAuth auth;
    ProgressDialog progressDialog;


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
        tvLangueDLBD = findViewById(R.id.tvLangueDLBD);

        rBtnAucuneDL = findViewById(R.id.rBtnAucuneDL);
        rBtnEnCoursDL = findViewById(R.id.rBtnEnCoursDL);
        rBtnLuDL = findViewById(R.id.rBtnLuDL);
        rBtnALireDL = findViewById(R.id.rBtnALireDL);
        rBtnALire2eTpsDL = findViewById(R.id.rBtnALire2eTpsDL);


        btnAjouterCitation = findViewById(R.id.btnAjouterCitation);
        btnModifierLivreBD = findViewById(R.id.btn_modifier_livre_bd);
        btnSupprimerLivreBD = findViewById(R.id.btnSupprimerLivreBD);

        requestQueue = Volley.newRequestQueue(this);


    }


    public void getListBooksDB(String id_BD) {
        livresRef.whereEqualTo(documentId(), id_BD).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(TAG, document.getId() + " => " + document.getData());
                                try {
                                    title_livre = document.getString(TITRE_LIVRE_BD);
                                    auteur_livre = document.getString(AUTEUR_LIVRE_BD);
                                    couvertureImage = document.getString(URL_COVER_LIVRE_BD);
                                    editeur_livre = document.getString(EDITEUR_LIVRE_BD);
                                    parution_livre = document.getString(DATE_PARUTION_LIVRE_BD);
                                    resume_livre = document.getString(RESUME_LIVRE_BD);
                                    isbn_livre = document.getString(ISBN_LIVRE_BD);
                                    nombres_pages_livres = document.getLong(NB_PAGES_LIVRE_BD);
                                    langue = document.getString(LANGUE_LIVRE_BD);
                                    etiquette = document.getString(ETIQUETTE_LIVRE_BD);

                                    switch(etiquette) {
                                        case VALUE_ETIQUETTE_AUCUNE:
                                            rBtnAucuneDL.setChecked(true);
                                            break;
                                        case VALUE_ETIQUETTE_EN_COURS:
                                            rBtnEnCoursDL.setChecked(true);
                                            break;
                                        case VALUE_ETIQUETTE_LU:
                                            rBtnLuDL.setChecked(true);
                                            break;
                                        case VALUE_ETIQUETTE_A_LIRE:
                                            rBtnALireDL.setChecked(true);
                                            break;
                                        case VALUE_ETIQUETTE_2EME_TEMPS:
                                            rBtnALire2eTpsDL.setChecked(true);
                                            break;
                                    }

                                    String nbr_pages = String.valueOf(nombres_pages_livres);

                                    Log.i(TAG, "onComplete: titre : " + title_livre);
                                    Log.i(TAG, "onComplete: nombres_pages_livres : " + nombres_pages_livres);
                                    Log.i(TAG, "onComplete: couvertureImage : " + couvertureImage);
                                    tv_title_livre.setText(title_livre);
                                    tv_auteur_livre.setText(auteur_livre);
                                    tv_editeur_livre.setText(editeur_livre);
                                    tv_parution_livre.setText(convertDateToFormatFr(parution_livre));
                                    tv_resume_livre.setText(resume_livre);
                                    tv_isbn_livre.setText(isbn_livre);
                                    tv_nombres_pages_livres.setText(nbr_pages + "p.");
                                    tvLangueDLBD.setText(langue);


                                    //Gestion de l'image avec Glide
                                    Context context = DetailsLivreBD.this;

                                    RequestOptions options = new RequestOptions()
                                            .centerCrop()
                                            .error(R.drawable.ic_couverture_livre_150)
                                            .placeholder(R.drawable.ic_couverture_livre_150);

                                    // methode normale
                                    Glide.with(context)
                                            .load(couvertureImage)
                                            .apply(options)
                                            .fitCenter()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(iv_couverture_livre);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "onComplete: erreur : e.getMessage() : " + e.getMessage());
                                    Toast.makeText(DetailsLivreBD.this, getString(R.string.t_error) + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

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
        setContentView(R.layout.activity_details_livre_bd);

        initUI();

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();

        Intent intent = getIntent();
        id_BD = intent.getStringExtra(ID_BD);
        Log.i(TAG, "onCreate: id_BD : " + id_BD);
        getListBooksDB(id_BD);


        btnAjouterCitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ajoutCitationIntent = new Intent(DetailsLivreBD.this, AjoutCitationActivity.class);
                ajoutCitationIntent.putExtra(ID_BD, id_BD);

                startActivity(ajoutCitationIntent);
            }
        });


        btnModifierLivreBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent modifierCitationIntent = new Intent(DetailsLivreBD.this, ModifierLivreBD.class);
                modifierCitationIntent.putExtra(ID_BD, id_BD);
                startActivity(modifierCitationIntent);
            }
        });

        btnSupprimerLivreBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupprimerLivreDialogFragment supprimerLivreDialogFragment = new SupprimerLivreDialogFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                supprimerLivreDialogFragment.show(fragmentManager, "");
            }
        });

        rBtnAucuneDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {

                    // update etiquette
                    try {
                        livresRef.document(id_BD).update(
                                ETIQUETTE_LIVRE_BD, VALUE_ETIQUETTE_AUCUNE
                        );
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_flag_updated_successfully), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "onClick: e.getMessage() :" + e.getMessage());
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_failed_to_update_flag) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        rBtnEnCoursDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    // update etiquette
                    try {
                        livresRef.document(id_BD).update(
                                ETIQUETTE_LIVRE_BD, VALUE_ETIQUETTE_EN_COURS
                        );
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_flag_updated_successfully), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "onClick: e.getMessage() :" + e.getMessage());
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_failed_to_update_flag) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        rBtnLuDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    try {
                        livresRef.document(id_BD).update(
                                ETIQUETTE_LIVRE_BD, VALUE_ETIQUETTE_LU
                        );
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_flag_updated_successfully), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "onClick: e.getMessage() :" + e.getMessage());
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_failed_to_update_flag) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        rBtnALireDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    try {
                        livresRef.document(id_BD).update(
                                ETIQUETTE_LIVRE_BD, VALUE_ETIQUETTE_A_LIRE
                        );
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_flag_updated_successfully), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "onClick: e.getMessage() :" + e.getMessage());
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_failed_to_update_flag) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        rBtnALire2eTpsDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked) {
                    try {
                        livresRef.document(id_BD).update(
                                ETIQUETTE_LIVRE_BD, VALUE_ETIQUETTE_2EME_TEMPS
                        );
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_flag_updated_successfully), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "onClick: e.getMessage() :" + e.getMessage());
                        Toast.makeText(DetailsLivreBD.this, getString(R.string.t_failed_to_update_flag) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    public static void supprimerLivreBD() {
        try {
            // suppression en cascade des citations du livre en question
            citationsRef
                    .whereEqualTo(ID_USER_CITATION_BD, id_user)
                    .whereEqualTo(ID_BD_LIVRE_CITATION_BD, id_BD)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                try {
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        String id_citationASupprimer = document.getId();
                                        Log.i(TAG, "onComplete: id_citationASupprimer : " + id_citationASupprimer);

                                        citationsRef.document(id_citationASupprimer).delete();
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                    Log.i(TAG, "onComplete: e.getMessage() : " + e.getMessage());
                                }

                            } else {
                                Log.i(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

            Log.i(TAG, "supprimerLivreBD: id_BD : " + id_BD);
            livresRef.document(id_BD).delete();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "supprimerLivreBD: e.getMessage() : " + e.getMessage());
        }
    }
}