package com.dam.bookcita.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.bookcita.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import com.dam.bookcita.model.ModelDetailsLivre;

public class AdapterDetailsBook extends FirestoreRecyclerAdapter <ModelDetailsLivre, AdapterDetailsBook.MyViewHolder> {
    private Context context;
    private ArrayList<ModelDetailsLivre> bookArrayList;

//    public AdapterDetailsBook(Context context, ArrayList<ModelDetailsLivre> bookArrayList) {
//        this.context = context;
//        this.bookArrayList = bookArrayList;
//    }

    // La gestion du click
    public OnItemClickListener onItemClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterDetailsBook(@NonNull FirestoreRecyclerOptions<ModelDetailsLivre> options) {
        super(options);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ModelDetailsLivre modelLivres) {
        String titreLivre = modelLivres.getTitle_livre();
        String auteurLivre = modelLivres.getAuteur_livre();
        String coverImage = modelLivres.getUrl_cover_livre();

        holder.tvTitre.setText(titreLivre);
        holder.tvAuteur.setText(auteurLivre);
        Context context = holder.ivCover.getContext();

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_couverture_livre_150)
                .placeholder(R.drawable.ic_couverture_livre_150);

        // methode normale
        Glide.with(context)
                .load(coverImage)
                .apply(options)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivCover);

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvAuteur;
        ImageView ivCover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre = itemView.findViewById(R.id.tvTitre);
            tvAuteur = itemView.findViewById(R.id.tvAuteur);
            ivCover = itemView.findViewById(R.id.ivCoverLivre);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && onItemClickListener != null){
                        DocumentSnapshot livreSnapshot = getSnapshots().getSnapshot(position);
                        onItemClickListener.onItemClick(livreSnapshot, position);
                    }
                }
            });

        }
    }



    //
//    @Override
//    public int getItemCount() {
//        return bookArrayList.size();
//    }
    /** @Override
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

    }**/

}
