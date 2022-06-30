package com.dam.bookcita;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AjoutCitationActivity extends AppCompatActivity {

    private static final String TYPE_ISBN_OR_OCR = "type_ISBN_or_OCR";
    private Button btnScanOCR;

    private void initUI() {
        btnScanOCR = findViewById(R.id.btnScanOCR);
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
    }
}