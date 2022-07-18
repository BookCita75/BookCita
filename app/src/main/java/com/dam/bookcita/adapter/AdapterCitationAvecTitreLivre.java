package com.dam.bookcita.adapter;

import static com.dam.bookcita.common.Constantes.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.bookcita.R;
import com.dam.bookcita.activity.ListeCitationsFromOneBookActivity;
import com.dam.bookcita.model.ModelCitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdapterCitationAvecTitreLivre extends RecyclerView.Adapter<AdapterCitationAvecTitreLivre.MyViewHolder> {
    private Context context;
    private ArrayList<ModelCitation> citationArrayList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "AdapterCitationAvecTitr";

    // La gestion du click
    public AdapterCitationAvecTitreLivre.OnItemClickListener onItemClickListener;

    public AdapterCitationAvecTitreLivre(Context context, ArrayList<ModelCitation> citationArrayList) {
        this.context = context;
        this.citationArrayList = citationArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_citation_avec_titre_livre, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelCitation currentItem = citationArrayList.get(position);

        String date = currentItem.getDate();
        String heure = currentItem.getHeure();
        int page = currentItem.getNumeroPage();
        String citation = currentItem.getCitation();
        String annotation = currentItem.getAnnotation();
        String id_BD_livre = currentItem.getId_BD_livre();

        holder.tvDateItemATL.setText(date);
        holder.tvHeureItemATL.setText(heure);
        holder.tvPageItemATL.setText(String.valueOf(page));
        holder.tvCitationItemATL.setText(citation);
        holder.tvAnnotationItemATL.setText(annotation);

        //TODO à remplacer par le titre plus tard
        //holder.tvTitreATL.setText(id_BD_livre);
        getThenSetTitreAuteurFromId_BD_livre(holder, id_BD_livre);
    }

    private void getThenSetTitreAuteurFromId_BD_livre(@NonNull MyViewHolder holder, String id_BD_livre) {
        db.collection("livres")
                .whereEqualTo(documentId(), id_BD_livre)
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
                                String titre = document.getString(TITRE_LIVRE);
                                String auteur = document.getString(AUTEUR_LIVRE);
                                Log.i(TAG, "onComplete: titre : " + titre);
                                Log.i(TAG, "onComplete: auteur : " + auteur);

                                holder.tvTitreATL.setText(titre);
                                holder.tvAuteurATL.setText(auteur);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    @Override
    public int getItemCount() {
        return citationArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateItemATL, tvHeureItemATL, tvPageItemATL, tvCitationItemATL,
                tvAnnotationItemATL, tvTitreATL, tvAuteurATL;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateItemATL = itemView.findViewById(R.id.tvDateItemATL);
            tvHeureItemATL = itemView.findViewById(R.id.tvHeureItemATL);
            tvPageItemATL = itemView.findViewById(R.id.tvPageItemATL);
            tvCitationItemATL = itemView.findViewById(R.id.tvCitationItemATL);
            tvAnnotationItemATL = itemView.findViewById(R.id.tvAnnotationItemATL);
            tvTitreATL = itemView.findViewById(R.id.tvTitreATL);
            tvAuteurATL = itemView.findViewById(R.id.tvAuteurATL);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(),v);
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    public void setOnItemClickListener(AdapterCitationAvecTitreLivre.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
