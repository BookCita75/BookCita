package com.dam.bookcita;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AjoutCitationActivity extends AppCompatActivity {

    private static final String TYPE_ISBN_OR_OCR = "type_ISBN_or_OCR";
    private Button btnScanOCR;
    private Button btnImportFichierTxt;

    private void initUI() {
        btnScanOCR = findViewById(R.id.btnScanOCR);
        btnImportFichierTxt = findViewById(R.id.btnImportFichierTxt);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_citation);

        initUI();

        btnScanOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraXIntent = new Intent(AjoutCitationActivity.this, CameraXActivity.class);
                cameraXIntent.putExtra(TYPE_ISBN_OR_OCR, "OCR");
                startActivity(cameraXIntent);
            }
        });

        btnImportFichierTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent importTxtFileIntent = new Intent(AjoutCitationActivity.this, ImportTxtFileActivity.class);
                startActivity(importTxtFileIntent);
            }
        });
    }
}