package com.dam.bookcita;

import static com.dam.bookcita.common.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class DetailsLivreBD extends AppCompatActivity {

    private Button btnAjouterCitation;

    private static final String TAG = "DetailsLivreBD";

    private void initUI() {
        btnAjouterCitation = findViewById(R.id.btnAjouterCitation);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_livre_bd);

        initUI();

        Intent intent = getIntent();
        String id_BD = intent.getStringExtra(ID_BD);
        Log.i(TAG, "onCreate: id_BD : " + id_BD);

        btnAjouterCitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ajoutCitationIntent = new Intent(DetailsLivreBD.this, AjoutCitationActivity.class);
                ajoutCitationIntent.putExtra(ID_BD, id_BD);

                startActivity(ajoutCitationIntent);
            }
        });
    }
}