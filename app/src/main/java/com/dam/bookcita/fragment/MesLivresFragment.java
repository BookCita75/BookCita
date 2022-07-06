package com.dam.bookcita.fragment;



import static com.dam.bookcita.common.Constantes.*;

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
import android.widget.ImageView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MesLivresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MesLivresFragment extends Fragment {

    private static final String TAG = "AfficherListeLivres";
    private RecyclerView rvLivres;
    private EditText et_search_livre ;

    private ArrayList<ModelDetailsLivre> bookArrayList;
    private AdapterDetailsBook adapterBook;
    //private ImageView coverLivreImage;
    private RequestQueue requestQueue;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection("livres");
    private FirebaseAuth auth;
    ProgressDialog progressDialog;
    private ImageView btn_search;
    private FirestoreRecyclerOptions displayedList;

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

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();



    }
    private void init(View view) {
        Log.i(TAG, "init: View");
        requestQueue = Volley.newRequestQueue(getContext());
        rvLivres = view.findViewById(R.id.rv_listesDesLivres);
        rvLivres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        et_search_livre = view.findViewById(R.id.et_search);

        auth = FirebaseAuth.getInstance();
        bookArrayList = new ArrayList<ModelDetailsLivre>();
    }
    public void getBooksFromDB(){
        FirebaseUser firebaseUser = auth.getCurrentUser();

        String id_user = firebaseUser.getUid();
        Log.i(TAG, "getBooksFromDB: id_user : " + id_user);
        try {
            Query query = livresRef
                    //On ne charge que les livres presents dans la BD de l'utilisateur connecté
                    .whereEqualTo("id_user", id_user)
                    .orderBy("title_livre");
            Log.i(TAG, "Query : "+query);
            FirestoreRecyclerOptions<ModelDetailsLivre> livres = new FirestoreRecyclerOptions.Builder<ModelDetailsLivre>()
                    .setQuery(query, ModelDetailsLivre.class)
                    .build();


            adapterBook = new AdapterDetailsBook(livres);
            rvLivres.setAdapter(adapterBook);
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "getBooksFromDB: erreur dans le query : " + e.getMessage());

        }
        adapterBookSetItemClickListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterBook.startListening();

    }

    public void adapterBookSetItemClickListener(){
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


        et_search_livre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchLivre = et_search_livre.getText().toString();
                Log.i(TAG, "onTextChanged: "+s.toString());
                if(!searchLivre.equals("")){
                    Log.i(TAG, "SearchLivre: "+ s.toString());
                    Query querySearch = livresRef
                            .whereEqualTo("auteur_livre", s.toString());
                    //.whereEqualTo("id_user",id_user)
                    Log.i(TAG, "onClick: "+querySearch.toString());
                    FirestoreRecyclerOptions<ModelDetailsLivre> livres = new FirestoreRecyclerOptions.Builder<ModelDetailsLivre>()
                            .setQuery(querySearch, ModelDetailsLivre.class)
                            .build();

                    adapterBook = new AdapterDetailsBook(livres);
                    Log.i(TAG, "livres from query search: "+adapterBook.toString());
                    rvLivres.setAdapter(adapterBook);
                    adapterBook.startListening();

                    adapterBookSetItemClickListener();

                }
            }


            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    getBooksFromDB();
                    adapterBook.startListening();
                    adapterBookSetItemClickListener();
                }
            }
        });

        getBooksFromDB();




        return view;
    }



}