package com.dam.bookcita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import models.ModelDetailsLivre;

public class ListeDesLivresBD extends AppCompatActivity implements AdapterDetailsBook.OnItemClickListener {
    private static final String TAG = "afficherListedesLivres";
    private RecyclerView rvLivres;
    private ArrayList<ModelDetailsLivre> bookArrayList;
    private AdapterDetailsBook adapterBook;
    //private ImageView coverLivreImage;
    private RequestQueue requestQueue;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection("livres");

    private void intUI(){

        requestQueue = Volley.newRequestQueue(this);
        //coverLivreImage = (ImageView) findViewById(R.id.ivCover);
        rvLivres = findViewById(R.id.rv_listesDesLivres);
        rvLivres.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

    }

    public void loadLivres(){

        livresRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String livres= "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            ModelDetailsLivre modelLivre = documentSnapshot.toObject(ModelDetailsLivre.class);
                            //modelLivre.setId(documentSnapshot.getId());

                            String id = modelLivre.getId();
                            String titreLivre = modelLivre.getTitle_livre();
                            String auteurLivre = modelLivre.getAuteur_livre();
                            String isbnLivre = modelLivre.getIsbn_livre();
                            String coverLivre = modelLivre.getUrl_cover_livre();

                            ModelDetailsLivre modelBook = new ModelDetailsLivre(id, titreLivre,auteurLivre, coverLivre, isbnLivre);
                            Log.i(TAG, "onSuccess: Id "+modelBook.getAuteur_livre());

                            bookArrayList = new ArrayList<>();
                            bookArrayList.add(modelBook);
                            adapterBook = new AdapterDetailsBook(getApplicationContext(), bookArrayList);

                            Log.i(TAG, "onCreate: titre : "+livres);

                            rvLivres.setAdapter(adapterBook);




                        }

                    }
                });

//        livresRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    for (DocumentSnapshot documentSnapshot : documentSnapshots){
//
//                    }
//                    List <> DocumentSnapshot modelLivre = task.getResult().getDocuments();
//                    String id = modelLivre.getId();
//                    String titreLivre = modelLivre.getTitle_livre();
//                    String auteurLivre = modelLivre.getAuteur_livre();
//                    String isbnLivre = modelLivre.getIsbn_livre();
//                    String coverLivre = modelLivre.getUrl_cover_livre();
//
//                    ModelDetailsLivre modelBook = new ModelDetailsLivre(id, titreLivre,auteurLivre, coverLivre, isbnLivre);
//                    Log.i(TAG, "onSuccess: Id "+modelBook.getAuteur_livre());
//
//                    bookArrayList = new ArrayList<>();
//                    bookArrayList.add(modelBook);
//                    adapterBook = new AdapterDetailsBook(getApplicationContext(), bookArrayList);
//
//                    Log.i(TAG, "onCreate: titre : "+livres);
//
//                    rvLivres.setAdapter(adapterBook);
//                }
//            }
//        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_liste_des_livres_bd);


        loadLivres();
        intUI();

    }

    @Override
    public void onItemClick(int position, View view) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}