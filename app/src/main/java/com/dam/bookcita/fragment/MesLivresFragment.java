package com.dam.bookcita.fragment;


import static com.dam.bookcita.common.Constantes.*;
import static com.dam.bookcita.common.Util.firstLetterToUpperCase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dam.bookcita.adapter.AdapterDetailsBook;
import com.dam.bookcita.R;
import com.dam.bookcita.activity.DetailsLivreBD;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import com.dam.bookcita.model.ModelDetailsLivre;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MesLivresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MesLivresFragment extends Fragment {

    private static final String TAG = "AfficherListeLivres";
    private RecyclerView rvLivres;
    private EditText et_search_livre;
    private RadioButton rBtnTous;
    private RadioButton rBtnEnCours;
    private RadioButton rBtnLus;
    private RadioButton rBtnALire;
    private RadioButton rBtnALire2eTps;


    private ArrayList<ModelDetailsLivre> bookArrayList;
    private AdapterDetailsBook adapterBook;
    //private ImageView coverLivreImage;
    private RequestQueue requestQueue;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection(LIVRES_COLLECTION_BD);
    private FirebaseAuth auth;
    ProgressDialog progressDialog;

    private FirestoreRecyclerOptions displayedList;

    private StorageReference fileStorage;

    String id_user;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MesLivresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MesLivresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MesLivresFragment newInstance(String param1, String param2) {
        MesLivresFragment fragment = new MesLivresFragment();
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

    private void init(View view) {
        Log.i(TAG, "init: View");
        requestQueue = Volley.newRequestQueue(getContext());
        rvLivres = view.findViewById(R.id.rv_listesDesLivres);
        rvLivres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        et_search_livre = view.findViewById(R.id.et_search);

        rBtnTous = view.findViewById(R.id.rBtnTous);
        rBtnEnCours = view.findViewById(R.id.rBtnEnCours);
        rBtnLus = view.findViewById(R.id.rBtnLus);
        rBtnALire = view.findViewById(R.id.rBtnALire);
        rBtnALire2eTps = view.findViewById(R.id.rBtnALire2eTps);


        auth = FirebaseAuth.getInstance();
        bookArrayList = new ArrayList<ModelDetailsLivre>();
    }

    public void getBooksFromDB(String etiquette) {
        try {
            Query query = null;

            if (etiquette.equals(VALUE_ETIQUETTE_ML_TOUS)) {
                query = livresRef
                        //On ne charge que les livres presents dans la BD de l'utilisateur connecté
                        .whereEqualTo(ID_USER_LIVRE_BD, id_user)
                        .orderBy(TITRE_LIVRE_BD);
            } else {
                query = livresRef
                        //On ne charge que les livres presents dans la BD de l'utilisateur connecté
                        .whereEqualTo(ID_USER_LIVRE_BD, id_user)
                        .whereEqualTo(ETIQUETTE_LIVRE_BD, etiquette)
                        .orderBy(TITRE_LIVRE_BD);
            }

            Log.i(TAG, "Query : " + query);
            FirestoreRecyclerOptions<ModelDetailsLivre> livres = new FirestoreRecyclerOptions.Builder<ModelDetailsLivre>()
                    .setQuery(query, ModelDetailsLivre.class)
                    .build();


            adapterBook = new AdapterDetailsBook(livres);
            rvLivres.setAdapter(adapterBook);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "getBooksFromDB: erreur dans le query : " + e.getMessage());

        }
        adapterBook.startListening();
        adapterBookSetOnItemClickListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterBook.startListening();
        adapterBookSetOnItemClickListener();

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
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();
        fileStorage = FirebaseStorage.getInstance().getReference();

        rBtnTous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBooksFromDB(VALUE_ETIQUETTE_ML_TOUS);
            }
        });

        rBtnEnCours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBooksFromDB(VALUE_ETIQUETTE_EN_COURS);
            }
        });

        rBtnLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBooksFromDB(VALUE_ETIQUETTE_LU);
            }
        });

        rBtnALire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBooksFromDB(VALUE_ETIQUETTE_A_LIRE);
            }
        });

        rBtnALire2eTps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBooksFromDB(VALUE_ETIQUETTE_2EME_TEMPS);
            }
        });





        et_search_livre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sFirstLetterUpperCase = firstLetterToUpperCase(s);
                Log.i(TAG, "SearchLivre: " + s.toString());
                Query querySearch = livresRef
                        .orderBy(AUTEUR_LIVRE_BD)
                        .startAt(sFirstLetterUpperCase)
                        .endAt(sFirstLetterUpperCase + "\uf8ff");
//                            .whereArrayContains(AUTEUR_LIVRE_BD, s.toString());
//                            .whereEqualTo(AUTEUR_LIVRE_BD, s.toString());
                //.whereEqualTo("id_user",id_user)
                Log.i(TAG, "onClick: " + querySearch.toString());
                FirestoreRecyclerOptions<ModelDetailsLivre> livres = new FirestoreRecyclerOptions.Builder<ModelDetailsLivre>()
                        .setQuery(querySearch, ModelDetailsLivre.class)
                        .build();

                adapterBook = new AdapterDetailsBook(livres);
                Log.i(TAG, "livres from query search: " + adapterBook.toString());
                rvLivres.setAdapter(adapterBook);
                adapterBook.startListening();
                adapterBookSetOnItemClickListener();


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    getBooksFromDB(VALUE_ETIQUETTE_ML_TOUS);
                    adapterBook.startListening();
                }
            }
        });

        getBooksFromDB(VALUE_ETIQUETTE_ML_TOUS);


        return view;
    }


    public void adapterBookSetOnItemClickListener() {
        adapterBook.setOnItemClickListener(new AdapterDetailsBook.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent detailsLivresBDIntent = new Intent(getContext(), DetailsLivreBD.class);
                String id_BD = documentSnapshot.getId();
                Log.i(TAG, "onItemClick: id_BD : " + id_BD);
                detailsLivresBDIntent.putExtra(ID_BD, id_BD);
                startActivity(detailsLivresBDIntent);

//                startActivity(new Intent(getContext(), DetailsLivreBD.class));
            }
        });
    }

}