package com.dam.bookcita;

import static com.dam.bookcita.common.Constantes.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import models.ModelDetailsLivre;

public class ListeDesLivresBD extends  AppCompatActivity{
    private static final String TAG = "ListeDesLivresBD";
//    private static final String TAG = "afficherListedesLivres";
    private RecyclerView rvLivres;
    private ArrayList<ModelDetailsLivre> bookArrayList;
    private AdapterDetailsBook adapterBook;
    //private ImageView coverLivreImage;
    private RequestQueue requestQueue;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection("livres");
    private FirebaseAuth auth;
    ProgressDialog progressDialog;
    private void intUI(){

        requestQueue = Volley.newRequestQueue(this);
        rvLivres = findViewById(R.id.rv_listesDesLivres);
        rvLivres.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));


        auth = FirebaseAuth.getInstance();
        bookArrayList = new ArrayList<ModelDetailsLivre>();
//        FirebaseUser currentUser = auth.getCurrentUser();
//        Log.i(TAG, "CurrentUser : " + currentUser.getEmail());


    }
    public void getBooksFromDB(){
        Query query = livresRef.orderBy("title_livre");
        Log.i(TAG, "Query : "+query);
        FirestoreRecyclerOptions<ModelDetailsLivre> livres = new FirestoreRecyclerOptions.Builder<ModelDetailsLivre>()
                .setQuery(query, ModelDetailsLivre.class)
                .build();
        adapterBook = new AdapterDetailsBook(livres);
        rvLivres.setAdapter(adapterBook);
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser curentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(curentUser == null){
//            startActivity(new Intent(ListeDesLivresBD.this, MainActivity.class));
//        } else {
//            adapterBook.startListening();
//        }
        adapterBook.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_liste_des_livres_bd);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();
        intUI();
        getBooksFromDB();

        adapterBook.setOnItemClickListener(new AdapterDetailsBook.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

//                startActivity(new Intent(ListeDesLivresBD.this, DetailsLivreBD.class));
            }
        });

    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}