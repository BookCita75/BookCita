package com.dam.bookcita.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.bookcita.R;
import com.dam.bookcita.model.ModelCitation;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class AdapterCitation extends FirestoreRecyclerAdapter<ModelCitation, AdapterCitation.MyViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterCitation.OnItemClickListener onItemClickListener;

    public AdapterCitation(@NonNull FirestoreRecyclerOptions<ModelCitation> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ModelCitation modelCitation) {
        String dateC = modelCitation.getDate();
        String heureC = modelCitation.getHeure();
        int pageC = modelCitation.getNumeroPage();
        String citation = modelCitation.getCitation();
        String annotation = modelCitation.getAnnotation();

        holder.tvDate.setText(dateC);
        holder.tvHeure.setText(heureC);
        holder.tvPage.setText(String.valueOf(pageC));
        holder.tvCitation.setText(citation);
        holder.tvAnnotation.setText(annotation);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_citation, parent, false);
        return new MyViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(AdapterCitation.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvHeure, tvPage, tvCitation, tvAnnotation;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDateItemC);
            tvHeure = itemView.findViewById(R.id.tvHeureItemC);
            tvPage = itemView.findViewById(R.id.tvPageItemIC);
            tvCitation = itemView.findViewById(R.id.tvCitationItemC);
            tvAnnotation = itemView.findViewById(R.id.tvAnnotationItemC);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && onItemClickListener != null){
                        DocumentSnapshot citationSnapshot = getSnapshots().getSnapshot(position);
                        onItemClickListener.onItemClick(citationSnapshot, position);
                    }
                }
            });

        }
    }
}
