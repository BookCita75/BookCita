package com.dam.bookcita;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ScannerISBNActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_isbn);
    }

    public void scanISBN(View view) {
        Intent cameraXIntent = new Intent(this, CameraXActivity.class);
        startActivity(cameraXIntent);
    }
}