package com.dam.bookcita;

import static com.dam.bookcita.common.Constants.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailsLivreBD extends AppCompatActivity {

    private Button btnAjouterCitation, btnModifierLivreBD;
    String title_livre, auteur_livre, editeur_livre, parution_livre, resume_livre, isbn_livre, couvertureImage;
    long nombres_pages_livres;
    private TextView tv_title_livre;
    private TextView tv_auteur_livre;
    private TextView tv_editeur_livre;
    private TextView tv_parution_livre;
    private TextView tv_resume_livre;
    private TextView tv_isbn_livre;
    private TextView tv_nombres_pages_livres;
    private ImageView iv_couverture_livre;
    private static final String TAG = "DetailsLivreBD";
    private RequestQueue requestQueue;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection("livres");
    private FirebaseAuth auth;
    ProgressDialog progressDialog;


    public void initUI(){
        tv_title_livre = findViewById(R.id.tv_title_livre_bd);
        tv_auteur_livre = findViewById(R.id.tv_auteur_livre_bd);
        tv_editeur_livre = findViewById(R.id.tv_editeur_livre_bd);
        tv_parution_livre = findViewById(R.id.tv_parution_livre_bd);
        tv_resume_livre = findViewById(R.id.tv_resume_livre_bd);
        tv_isbn_livre = findViewById(R.id.tv_isbn_livre_bd);
        tv_nombres_pages_livres = findViewById(R.id.tv_nombres_pages_livres_bd);
        iv_couverture_livre = (ImageView) findViewById(R.id.iv_couverture_livre_bd);
        tv_resume_livre.setMovementMethod(new ScrollingMovementMethod());

        btnAjouterCitation = findViewById(R.id.btnAjouterCitation);
        btnModifierLivreBD = findViewById(R.id.btn_updat_livre_bd);

        requestQueue = Volley.newRequestQueue(this);


        auth = FirebaseAuth.getInstance();

    }

    public void getListBooksDB(String id_BD){
        livresRef.whereEqualTo(documentId(), id_BD).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(TAG, document.getId() + " => " + document.getData());
                                title_livre = document.getString("title_livre");
                                auteur_livre = document.getString("auteur_livre");
                                couvertureImage = document.getString("url_cover_livre");
                                editeur_livre =document.getString("editeur_livre");
                                parution_livre = document.getString("date_parution_livre");
                                resume_livre = document.getString("resume_livre");
                                isbn_livre = document.getString("isbn_livre");
                                nombres_pages_livres = document.getLong("nombre_livres");

                                String nbr_pages = String.valueOf(nombres_pages_livres);

                                Log.i(TAG, "onComplete: titre : " + title_livre);
                                Log.i(TAG, "onComplete: nombres_pages_livres : " + nombres_pages_livres);

                                tv_title_livre.setText(title_livre);
                                tv_auteur_livre.setText(auteur_livre);
                                tv_editeur_livre.setText(editeur_livre);
                                tv_parution_livre.setText(parution_livre);
                                tv_resume_livre.setText(resume_livre);
                                tv_isbn_livre.setText(isbn_livre);
                                tv_nombres_pages_livres.setText(nbr_pages+"p.");


                                //Gestion de l'image avec Glide
                                Context context = DetailsLivreBD.this ;

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

        Intent intent = getIntent();
        String id_BD = intent.getStringExtra(ID_BD);
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
    }
}