package com.dam.bookcita.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.bookcita.R;
import com.dam.bookcita.model.ModelCitation;

import java.util.ArrayList;

public class AdapterCitationAvecTitreLivre extends RecyclerView.Adapter<AdapterCitationAvecTitreLivre.MyViewHolder> {
    private Context context;
    private ArrayList<ModelCitation> citationArrayList;

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
        holder.tvTitreATL.setText(id_BD_livre);
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

        }
    }
}
