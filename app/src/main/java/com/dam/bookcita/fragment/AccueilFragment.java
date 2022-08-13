package com.dam.bookcita.fragment;

import static com.dam.bookcita.common.Constantes.ANNOTATION_CITATION_BD;
import static com.dam.bookcita.common.Constantes.AUTEUR_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.CITATIONS_COLLECTION_BD;
import static com.dam.bookcita.common.Constantes.CITATION_CITATION_BD;
import static com.dam.bookcita.common.Constantes.DATE_AJOUT_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.DATE_CITATION_BD;
import static com.dam.bookcita.common.Constantes.HEURE_AJOUT_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.HEURE_CITATION_BD;
import static com.dam.bookcita.common.Constantes.ID_BD_LIVRE_CITATION_BD;
import static com.dam.bookcita.common.Constantes.ID_USER_CITATION_BD;
import static com.dam.bookcita.common.Constantes.ID_USER_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.LIVRES_COLLECTION_BD;
import static com.dam.bookcita.common.Constantes.TITRE_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.URL_COVER_LIVRE_BD;
import static com.dam.bookcita.common.Util.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.bookcita.R;
import com.dam.bookcita.activity.SaisieManuelleCitationActivity;
import com.dam.bookcita.adapter.AdapterCitationAvecTitreLivre;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccueilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccueilFragment extends Fragment {

    private static final String TAG = "AccueilFragment";

    private TextView tvTitreAcc;
    private TextView tvAuteurAcc;
    private TextView tvDateAjoutLivreAcc;
    private TextView tvPrenomUserAcc;

    private ImageView ivCoverBookAcc;


    private TextView tvCitationAcc;
    private TextView tvAnnotationAcc;
    private TextView tvDateAjoutCitationAcc;
    private TextView tvHeureAjoutCitationAcc;
    private TextView tvCitationTitreLivreAcc;
    private TextView tvCitationAuteurLivreAcc;

    private ImageView ivCoverLivreCitationAcc;

    private LinearLayout llLastAddedQuoteFilled;
    private LinearLayout llLastAddedQuoteDefault;

    private LinearLayout llLastAddedBookFilled;
    private LinearLayout llLastAddedBookDefault;



    private String id_user;

    private FirebaseAuth auth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection(LIVRES_COLLECTION_BD);
    private CollectionReference citationsRef = db.collection(CITATIONS_COLLECTION_BD);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccueilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccueilFragment newInstance(String param1, String param2) {
        AccueilFragment fragment = new AccueilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AccueilFragment() {
        // Required empty public constructor
    }

    private void init(View view) {
        tvTitreAcc = view.findViewById(R.id.tvTitreAcc);
        tvAuteurAcc = view.findViewById(R.id.tvAuteurAcc);
        tvDateAjoutLivreAcc = view.findViewById(R.id.tvDateAjoutLivreAcc);
        tvPrenomUserAcc = view.findViewById(R.id.tvPrenomUserAcc);

        ivCoverBookAcc = view.findViewById(R.id.ivCoverBookAcc);

        tvCitationAcc = view.findViewById(R.id.tvCitationAcc);
        tvAnnotationAcc = view.findViewById(R.id.tvAnnotationAcc);
        tvDateAjoutCitationAcc = view.findViewById(R.id.tvDateAjoutCitationAcc);
        tvHeureAjoutCitationAcc = view.findViewById(R.id.tvHeureAjoutCitationAcc);
        tvCitationTitreLivreAcc = view.findViewById(R.id.tvCitationTitreLivreAcc);
        tvCitationAuteurLivreAcc = view.findViewById(R.id.tvCitationAuteurLivreAcc);

        ivCoverLivreCitationAcc = view.findViewById(R.id.ivCoverLivreCitationAcc);

        llLastAddedQuoteFilled = view.findViewById(R.id.llLastAddedQuoteFilled);
        llLastAddedQuoteDefault = view.findViewById(R.id.llLastAddedQuoteDefault);

        llLastAddedBookFilled = view.findViewById(R.id.llLastAddedBookFilled);
        llLastAddedBookDefault = view.findViewById(R.id.llLastAddedBookDefault);

        auth = FirebaseAuth.getInstance();

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
        View view = inflater.inflate(R.layout.fragment_accueil, container, false);

        init(view);
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();
        String prenomUser = firebaseUser.getDisplayName();
        tvPrenomUserAcc.setText(prenomUser);

        getLastAddedBookFromDB();
        getLastAddedQuoteFromDB();

        return view;
    }

    private void getLastAddedBookFromDB() {
        Query query = livresRef
                .whereEqualTo(ID_USER_LIVRE_BD, id_user)
                .orderBy(DATE_AJOUT_LIVRE_BD, Query.Direction.DESCENDING)
                .orderBy(HEURE_AJOUT_LIVRE_BD, Query.Direction.DESCENDING)
                .limit(1);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.size() == 0) {
                                // il n'y a pas de livre dans la BD
                                llLastAddedBookFilled.setVisibility(View.GONE);
                                llLastAddedBookDefault.setVisibility(View.VISIBLE);
                            } else {
                                llLastAddedBookDefault.setVisibility(View.GONE);
                                llLastAddedBookFilled.setVisibility(View.VISIBLE);

                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    Log.i(TAG, document.getId() + " => " + document.getData());
                                    String titre = document.getString(TITRE_LIVRE_BD);
                                    String auteur = document.getString(AUTEUR_LIVRE_BD);
                                    String coverUrl = document.getString(URL_COVER_LIVRE_BD);
                                    String dateAjoutLivre = document.getString(DATE_AJOUT_LIVRE_BD);
                                    Log.i(TAG, "onComplete: titre : " + titre);
                                    Log.i(TAG, "onComplete: auteur : " + auteur);
                                    Log.i(TAG, "onComplete: coverUrl : " + coverUrl);
                                    tvTitreAcc.setText(titre);
                                    tvAuteurAcc.setText(auteur);
                                    tvDateAjoutLivreAcc.setText(convertDateToFormatFr(dateAjoutLivre));
                                    //Gestion de l'image avec Glide
                                    Context context = getContext();

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
                                            .into(ivCoverBookAcc);
                                }
                            }

                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    private void getLastAddedQuoteFromDB() {
        Query query = citationsRef
                .whereEqualTo(ID_USER_CITATION_BD, id_user)
                .orderBy(DATE_CITATION_BD, Query.Direction.DESCENDING)
                .orderBy(HEURE_CITATION_BD, Query.Direction.DESCENDING)
                .limit(1);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.size() == 0) {
                               // il n'y a pas de citation dans la BD
                                llLastAddedQuoteFilled.setVisibility(View.GONE);
                                llLastAddedQuoteDefault.setVisibility(View.VISIBLE);
                            } else {
                                llLastAddedQuoteDefault.setVisibility(View.GONE);
                                llLastAddedQuoteFilled.setVisibility(View.VISIBLE);
                                // limit(1) => un seul document
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    Log.i(TAG, document.getId() + " => " + document.getData());
                                    String citation = document.getString(CITATION_CITATION_BD);
                                    String annotation = document.getString(ANNOTATION_CITATION_BD);
                                    String date_citation = document.getString(DATE_CITATION_BD);
                                    String heure_citation = document.getString(HEURE_CITATION_BD);
                                    String id_bd_livre = document.getString(ID_BD_LIVRE_CITATION_BD);


                                    tvCitationAcc.setText(citation);
                                    tvAnnotationAcc.setText(annotation);
                                    tvDateAjoutCitationAcc.setText(convertDateToFormatFr(date_citation));
                                    tvHeureAjoutCitationAcc.setText(heure_citation);

                                    getThenSetTitreAuteurFromId_BD_livre(id_bd_livre);
                                }
                            }

                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }


    private void getThenSetTitreAuteurFromId_BD_livre(String id_BD_livre) {
        db.collection(LIVRES_COLLECTION_BD)
                .whereEqualTo(documentId(), id_BD_livre)
//                .whereEqualTo("auteur_livre", "Luc Lang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Log.i(TAG, document.getId() + " => " + document.getData());
                                String titre = document.getString(TITRE_LIVRE_BD);
                                String auteur = document.getString(AUTEUR_LIVRE_BD);
                                String coverUrl = document.getString(URL_COVER_LIVRE_BD);

                                tvCitationTitreLivreAcc.setText(titre);
                                tvCitationAuteurLivreAcc.setText(auteur);

                                //Gestion de l'image avec Glide
                                Context context = getContext();

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
                                        .into(ivCoverLivreCitationAcc);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }
}