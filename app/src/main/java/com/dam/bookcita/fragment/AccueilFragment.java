package com.dam.bookcita.fragment;

import static com.dam.bookcita.common.Constantes.AUTEUR_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.DATE_AJOUT_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.HEURE_AJOUT_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.ID_USER_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.LIVRES_COLLECTION_BD;
import static com.dam.bookcita.common.Constantes.TITRE_LIVRE_BD;
import static com.dam.bookcita.common.Constantes.URL_COVER_LIVRE_BD;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.bookcita.R;
import com.dam.bookcita.activity.SaisieManuelleCitationActivity;
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

    private String id_user;

    private FirebaseAuth auth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection(LIVRES_COLLECTION_BD);

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
                                tvDateAjoutLivreAcc.setText(dateAjoutLivre);
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
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }
}