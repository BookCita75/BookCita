package com.dam.bookcita.dialogFragment;

import static com.dam.bookcita.common.Constantes.FRAG_TO_LOAD;
import static com.dam.bookcita.common.Constantes.MES_CITATIONS_FRAGMENT;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.dam.bookcita.R;
import com.dam.bookcita.activity.DetailsCitationActivity;
import com.dam.bookcita.activity.MainActivity;

public class SupprimerCitationDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_supprimer_citation)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // suppression de la citation
                        DetailsCitationActivity.supprimerCitation();
                        Toast.makeText(getContext(), getString(R.string.t_quote_deleted_successfully), Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(getContext(), MainActivity.class);
                        mainIntent.putExtra(FRAG_TO_LOAD, MES_CITATIONS_FRAGMENT);

                        startActivity(mainIntent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(), getString(R.string.t_quote_deletion_canceled), Toast.LENGTH_LONG).show();
                        return;
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
