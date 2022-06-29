package com.dam.bookcita;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import models.ModelDetailsLivre;

public class AdapterDetailsBook extends RecyclerView.Adapter<AdapterDetailsBook.MyViewHolder> {
    private Context context;
    private ArrayList<ModelDetailsLivre> bookArrayList;

    public AdapterDetailsBook(Context context, ArrayList<ModelDetailsLivre> bookArrayList) {
        this.context = context;
        this.bookArrayList = bookArrayList;
    }

    // La gestion du click
    public OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Remplissage du DataBinding
        ModelDetailsLivre currentItem = bookArrayList.get(position);

        String titre = currentItem.getTitle_livre();
        String auteur = currentItem.getAuteur_livre();

        holder.tvTitre.setText(titre);
        holder.tvAuteur.setText(auteur);

        //Gestion de l'image
        String coverUrl = currentItem.getUrl_cover_livre();

        // Utilisation de Glide pour la gestion des images
        Context context = holder.ivCover.getContext();

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
                .into(holder.ivCover);

    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvAuteur;
        ImageView ivCover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre = itemView.findViewById(R.id.tvTitre);
            tvAuteur = itemView.findViewById(R.id.tvAuteur);
            ivCover = itemView.findViewById(R.id.ivCover);

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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
