package com.dam.bookcita;

import static com.dam.bookcita.common.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DetailsLivreBD extends AppCompatActivity {

    private static final String TAG = "DetailsLivreBD";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_livre_bd);

        Intent intent = getIntent();
        String id_BD = intent.getStringExtra(ID_BD);
        Log.i(TAG, "onCreate: id_BD : " + id_BD);

    }
}