package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.FRAG_TO_LOAD;
import static com.dam.bookcita.common.Constantes.MES_LIVRES_FRAGMENT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dam.bookcita.R;
import com.dam.bookcita.model.ModelDetailsLivre;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SaisieManuelleLivreActivity extends AppCompatActivity {

    private static final String TAG = "SaisieManuelleLivreActi";

    private EditText etTitreSML;
    private EditText etAuteurSML;
    private EditText etDateSML;
    private EditText etEditeurSML;
    private EditText etNbPagesSML;
    private EditText etIsbnSML;
    private EditText etLangueSML;
    private Button btChooseFileSML;
    private ImageView ivCoverSML;
    private EditText etmlResumeSML;
    private Button btValiderSML;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection("livres");


    private void init(){
        etTitreSML = findViewById(R.id.etTitreSML);
        etAuteurSML = findViewById(R.id.etAuteurSML);
        etDateSML = findViewById(R.id.etDateSML);
        etEditeurSML = findViewById(R.id.etEditeurSML);
        etNbPagesSML = findViewById(R.id.etNbPagesSML);
        etIsbnSML = findViewById(R.id.etIsbnSML);
        etLangueSML = findViewById(R.id.etLangueSML);
        btChooseFileSML = findViewById(R.id.btChooseFileSML);
        ivCoverSML = findViewById(R.id.ivCoverSML);
        etmlResumeSML = findViewById(R.id.etmlResumeSML);
        btValiderSML = findViewById(R.id.btValiderSML);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisie_manuelle_livre);

        init();
        Log.i(TAG, "onCreate: av btValider");
        btValiderSML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titre = etTitreSML.getText().toString();
                Log.i(TAG, "onClick: titre : " + titre);
                String auteur = etAuteurSML.getText().toString();
                Log.i(TAG, "onClick: auteur : " + auteur);
                String date = etDateSML.getText().toString();
                Log.i(TAG, "onClick: date : " + date);
                String editeur = etEditeurSML.getText().toString();
                Log.i(TAG, "onClick: editeur : " + editeur);
                String nbPagesStr = etNbPagesSML.getText().toString();
                Log.i(TAG, "onClick: nbPagesStr : " + nbPagesStr);
                String isbn = etIsbnSML.getText().toString();
                Log.i(TAG, "onClick: isbn : " + isbn);
                String langue = etLangueSML.getText().toString();
                Log.i(TAG, "onClick: langue : " + langue);
                //ivCoverSML
                String resume = etmlResumeSML.getText().toString();
                Log.i(TAG, "onClick: resume : " + resume);


                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                String id_user = firebaseUser.getUid();

                String couvertureImage = "";
                int nbPages = 0;
                if (!nbPagesStr.equals("")) {
                    nbPages = Integer.valueOf(nbPagesStr);
                }

                ModelDetailsLivre livre = new ModelDetailsLivre(titre, auteur, editeur, date, resume,couvertureImage,isbn, nbPages, id_user);

                try {
                    livresRef.add(livre);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onClick: erreur : e.getMessage() : " + e.getMessage());

                }
                Toast.makeText(SaisieManuelleLivreActivity.this, "Livre ajouté avec succès !", Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(SaisieManuelleLivreActivity.this, MainActivity.class);
                mainIntent.putExtra(FRAG_TO_LOAD, MES_LIVRES_FRAGMENT);

                startActivity(mainIntent);

            }
        });
    }
}