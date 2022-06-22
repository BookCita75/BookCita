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

public class AdapterBook extends RecyclerView.Adapter<AdapterBook.MyViewHolder> {
    private Context context;
    private ArrayList<ModelBook> bookArrayList;

    public AdapterBook(Context context, ArrayList<ModelBook> bookArrayList) {
        this.context = context;
        this.bookArrayList = bookArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Remplissage du DataBinding
        ModelBook currentItem = bookArrayList.get(position);

        String titre = currentItem.getTitre();
        String auteur = currentItem.getAuteur();

        holder.tvTitre.setText(titre);
        holder.tvAuteur.setText(auteur);

        //Gestion de l'image
        String coverUrl = currentItem.getCoverUrl();

        // Utilisation de Glide pour la gestion des images
        Context context = holder.ivCover.getContext();

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher);

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

        }
    }
}
