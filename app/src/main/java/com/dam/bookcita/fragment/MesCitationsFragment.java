package com.dam.bookcita.fragment;

import static com.dam.bookcita.common.Constantes.ID_BD;
import static com.google.firebase.firestore.FieldPath.documentId;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dam.bookcita.R;
import com.dam.bookcita.activity.DetailsLivreBD;
import com.dam.bookcita.adapter.AdapterBookNbrCitations;
import com.dam.bookcita.adapter.AdapterDetailsBook;
import com.dam.bookcita.model.ModelDetailsLivre;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MesCitationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MesCitationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "AfficherListeCitations";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RequestQueue requestQueue;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection("livres");
    private CollectionReference citationsRef = db.collection("citations");

    private RecyclerView rvLivres;
    private ArrayList<ModelDetailsLivre> bookArrayList;
    private AdapterBookNbrCitations adapterBook;
    ProgressDialog progressDialog;

    private FirebaseAuth auth;

    private void init(View view) {
        Log.i(TAG, "init: View");
        requestQueue = Volley.newRequestQueue(getContext());
        rvLivres = view.findViewById(R.id.rv_listesDesLivres);
        rvLivres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));


        auth = FirebaseAuth.getInstance();
        bookArrayList = new ArrayList<ModelDetailsLivre>();
    }

    public void getBooksFromDB(){
        Query queryListeDesCitations = citationsRef;
        queryListeDesCitations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> listeDesLivres=new ArrayList<>();
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        Log.i(TAG, documentSnapshot.getString("id_BD_livre"));
                        listeDesLivres.add(documentSnapshot.getString("id_BD_livre"));

                    }
                    Log.i(TAG, "Liste des Livres" + listeDesLivres);
                    Set<String> mySet = new HashSet<String>(listeDesLivres);
                    ArrayList<String> listeLivreSansDoublons = new ArrayList<String>(mySet);
                    Log.i(TAG, "liste Sans Doublons: "+listeLivreSansDoublons);

                    try {
                        if (!listeLivreSansDoublons.isEmpty()) {
                            Query queryListeLivreSansDoublons = livresRef.whereIn(documentId(), listeLivreSansDoublons);

                            FirestoreRecyclerOptions<ModelDetailsLivre> livres = new FirestoreRecyclerOptions.Builder<ModelDetailsLivre>()
                                    .setQuery(queryListeLivreSansDoublons, ModelDetailsLivre.class)
                                    .build();

                            adapterBook = new AdapterBookNbrCitations(livres);
                            rvLivres.setAdapter(adapterBook);
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            adapterBook.startListening();
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }catch (Exception e){
                        Log.e(TAG, "Error queryListeLivreSansDoublons "+e.getMessage());
                    }

                }
            }
        });


        //Query queryLivre = livresRef.whereEqualTo(documentId(),query2);




    }

    @Override
    public void onStart() {
        super.onStart();
        getBooksFromDB();

    }

    public MesCitationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MesCitationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MesCitationsFragment newInstance(String param1, String param2) {
        MesCitationsFragment fragment = new MesCitationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mes_livres, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        init(view);





        return view;
    }
}