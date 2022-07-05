package com.dam.bookcita.activity;

import static com.google.firebase.firestore.FieldPath.documentId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.bookcita.R;
import com.dam.bookcita.adapter.AdapterCitation;
import com.dam.bookcita.adapter.AdapterDetailsBook;
import com.dam.bookcita.model.ModelCitation;
import com.dam.bookcita.model.ModelDetailsLivre;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ListeCitationsFromOneBookActivity extends AppCompatActivity {

    private static final String TAG = "ListeCitationsFromOneBo";

    private TextView tvTitreLC;
    private TextView tvAuteurLC;
    private ImageView ivCoverLC;
    private RecyclerView rvCitationsFOB;

    private AdapterCitation adapterCitation;

    private ProgressDialog progressDialog;

    private String id_BD;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference citationsRef = db.collection("citations");

    private void init(){
        tvTitreLC = findViewById(R.id.tvTitreLC);
        tvAuteurLC = findViewById(R.id.tvAuteurLC);
        ivCoverLC = findViewById(R.id.ivCoverLC);

        rvCitationsFOB = findViewById(R.id.rvCitationsFOB);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_citations_from_one_book);

        /************/
        // en attendant le code d'Ons

        id_BD = "P7RpM6y4e5lh0B4IfG3c";
        // correspond à la force d'aimer
        /************/

        init();

        getFicheBookFromDB();

        rvCitationsFOB.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        getCitationsFOBFromDB();
    }

    public void getCitationsFOBFromDB(){
        Query query = citationsRef.whereEqualTo("id_BD_livre",id_BD);
//        Query query = citationsRef.orderBy("citation");
        Log.i(TAG, "Query : "+query);
        FirestoreRecyclerOptions<ModelCitation> citationsFOB = new FirestoreRecyclerOptions.Builder<ModelCitation>()
                .setQuery(query, ModelCitation.class)
                .build();
        adapterCitation = new AdapterCitation(citationsFOB);
        rvCitationsFOB.setAdapter(adapterCitation);
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterCitation.startListening();
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
                                tvTitreLC.setText(titre);
                                tvAuteurLC.setText(auteur);
                                //Gestion de l'image avec Glide
                                Context context = ListeCitationsFromOneBookActivity.this;

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
                                        .into(ivCoverLC);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }
}