package com.dam.bookcita.fragment;

import static com.dam.bookcita.common.Constantes.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dam.bookcita.R;
import com.dam.bookcita.activity.DetailsCitationActivity;
import com.dam.bookcita.activity.ListeCitationsFromOneBookActivity;
import com.dam.bookcita.adapter.AdapterBookNbrCitations;
import com.dam.bookcita.adapter.AdapterCitationAvecTitreLivre;
import com.dam.bookcita.model.ModelCitation;
import com.dam.bookcita.model.ModelDetailsLivre;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.Settings;
import com.meilisearch.sdk.exceptions.MeiliSearchApiException;
import com.meilisearch.sdk.model.SearchResult;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MesCitationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MesCitationsFragment extends Fragment  implements AdapterBookNbrCitations.OnItemClickListener, AdapterCitationAvecTitreLivre.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MesCitationsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RequestQueue requestQueue;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection(LIVRES_COLLECTION_BD);
    private CollectionReference citationsRef = db.collection(CITATIONS_COLLECTION_BD);
    private RecyclerView rvListeResultSearchCitation;
    private RecyclerView rv_listesDesCitations;
    private EditText etSearchMesCitations;
    private ArrayList<ModelDetailsLivre> bookArrayList;
    private ArrayList<ModelCitation> citationArrayList;

    private AdapterBookNbrCitations adapterBook;
    private AdapterCitationAvecTitreLivre adapterCitationAvecTitreLivre;

    ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private String id_user;

    private void init(View view) {
        Log.i(TAG, "init: View");
        requestQueue = Volley.newRequestQueue(getContext());

        etSearchMesCitations = view.findViewById(R.id.etSearchMesCitations);
        rv_listesDesCitations = view.findViewById(R.id.rv_listesDesLivresAyantCitations);
        rv_listesDesCitations.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));

        rvListeResultSearchCitation = view.findViewById(R.id.rvListeResultSearchCitation);
        rvListeResultSearchCitation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));


        auth = FirebaseAuth.getInstance();
        bookArrayList = new ArrayList<ModelDetailsLivre>();
        citationArrayList = new ArrayList<ModelCitation>();
    }

    //affiche la liste des livres avec des citations
    public void getBooksWithCitationFromDB(){
        Query queryListeDesCitations = citationsRef.whereEqualTo(ID_USER_CITATION_BD, id_user);
        queryListeDesCitations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> listeDesLivres=new ArrayList<>();
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        Log.i(TAG, documentSnapshot.getString(ID_BD_LIVRE_CITATION_BD));
                        listeDesLivres.add(documentSnapshot.getString(ID_BD_LIVRE_CITATION_BD));

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
                            rv_listesDesCitations.setAdapter(adapterBook);
                            adapterBook.setOnItemClickListener(MesCitationsFragment.this);
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
        View view = inflater.inflate(R.layout.fragment_mes_citations, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        init(view);

        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();
        Log.i(TAG, "onCreateView: id_user : " + id_user);

        etSearchMesCitations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                citationArrayList.clear();

                Log.i(TAG, "beforeTextChanged: s : " + s);
//                String hostUrl = "https://bc_meilysearch_dam.com";
//                String hostUrl = "localhost:7700";
//                String hostUrl = "http://localhost:7700";
                String hostUrl = "http://127.0.0.1:7700";


                Client client = new Client(new Config(hostUrl, "iIqRYifw83a6b3ffc067335ea7cd19fde2cd0ea6383535843a67cd22c1b7df53dfcd4360"));
                try {
                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }




                    Index index = client.index(CITATIONS_COLLECTION_BD);

//                    String docs = index.getDocuments();
//                    Log.i(TAG, "onTextChanged: docs : " + docs);
//                    Log.i(TAG, "onTextChanged: docs : " + docs);


                    String[] strAttrToRetrieve = {"*"};

                    Settings settings = new Settings();
                    settings.setFilterableAttributes(new String[] {ID_USER_CITATION_BD});
//                    index.updateSettings(settings);


                    String[] filterUser = {ID_USER_CITATION_BD + " = " + id_user};
                    SearchRequest searchRequest = new SearchRequest()
                            .setQ(s.toString().trim())
                            .setAttributesToRetrieve(null)
                            .setLimit(100)
//                            .setFilter(filterUser)
                            ;


//                    String rsResult = index.rawSearch(searchRequest);
//                    Log.i(TAG, "onTextChanged: rsResult : " + rsResult);
                    SearchResult results = index.search(searchRequest);
                    ArrayList<HashMap<String,Object>> arrayListResults = results.getHits();
                    for (HashMap<String,Object> r:arrayListResults
                         ) {

                        Log.i(TAG, "onTextChanged: r.toString() : " + r.toString());
                        String date = (String) r.get(DATE_CITATION_BD);
                        String heure = (String) r.get(HEURE_CITATION_BD);
                        Double page = (Double) r.get(NUMERO_PAGE_CITATION_BD);
                        String citation = (String) r.get(CITATION_CITATION_BD);
                        Log.i(TAG, "onTextChanged: citation : " + citation);
                        String annotation = (String) r.get(ANNOTATION_CITATION_BD);
                        String id_BD_livre = (String) r.get(ID_BD_LIVRE_CITATION_BD);
                        String id_userCitation = (String) r.get(ID_USER_CITATION_BD);
                        String id_citation = (String) r.get(_FIRESTORE_ID_CITATION_BD);
                        Log.i(TAG, "onTextChanged: id_citation : " + id_citation);
                        citationArrayList.add(new ModelCitation(id_citation, id_BD_livre, citation, annotation, page.intValue(), date, heure, id_userCitation));

                    }
                    adapterCitationAvecTitreLivre = new AdapterCitationAvecTitreLivre(getContext(), citationArrayList);
                    rvListeResultSearchCitation.setAdapter(adapterCitationAvecTitreLivre);
                    adapterCitationAvecTitreLivre.setOnItemClickListener(MesCitationsFragment.this);
                    Log.i(TAG, "onTextChanged: results : " + results.toString());

                } catch (ConnectException e) {
                    e.printStackTrace();
                    Log.i(TAG, "onTextChanged: e.getMessage() : " + e.getMessage());
                    Toast.makeText(getContext(), "Erreur connection : " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (MeiliSearchApiException e) {
                    e.printStackTrace();
                    Log.i(TAG, "onTextChanged: MeiliSearchApiException e.getMessage() :" + e.getMessage());
                    Toast.makeText(getContext(), "Erreur meilisearch : " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onTextChanged: e.getMessage() : " + e.getMessage());
                    Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getBooksWithCitationFromDB();


        return view;
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
        Intent listeCitationFOBIntent = new Intent(getContext(), ListeCitationsFromOneBookActivity.class);
        String id_BD_livreSelectionne = documentSnapshot.getId();
        Log.i(TAG, "onItemClick: id_BD_livreSelectionne : " + id_BD_livreSelectionne);
        listeCitationFOBIntent.putExtra(ID_BD, id_BD_livreSelectionne);

        startActivity(listeCitationFOBIntent);
    }

    @Override
    public void onItemClick(int position, View view) {
//        Intent listeCitationFOBIntent = new Intent(getContext(), ListeCitationsFromOneBookActivity.class);
//        String id_BD_livreSelectionne = citationArrayList.get(position).getId_BD_livre();
//        Log.i(TAG, "onItemClick: id_BD_livreSelectionne : " + id_BD_livreSelectionne);
//        listeCitationFOBIntent.putExtra(ID_BD, id_BD_livreSelectionne);
//
//        startActivity(listeCitationFOBIntent);

        Intent detailsCitationFOBIntent = new Intent(getContext(), DetailsCitationActivity.class);
        String id_BD_livreSelectionne = citationArrayList.get(position).getId_BD_livre();
        String id_citationSelectionnee = citationArrayList.get(position).getId();
        Log.i(TAG, "onItemClick: id_citationSelectionnee : " + id_citationSelectionnee);
        detailsCitationFOBIntent.putExtra(ID_BD, id_BD_livreSelectionne);
        detailsCitationFOBIntent.putExtra(ID_BD_CITATION, id_citationSelectionnee);
        startActivity(detailsCitationFOBIntent);
    }
}