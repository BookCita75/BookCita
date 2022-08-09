package com.dam.bookcita.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dam.bookcita.R;

public class ImportCitationsAnnotationsJSON extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_citations_annotations_json);
    }

    int requestCode = 1 ;

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode, data);
        Context context = getApplicationContext();
        if(requestCode == requestCode && resultCode == Activity.RESULT_OK){
            if(data == null){
                return;
            }
            Uri uri = data.getData();
//            Toast.makeText(context, uri.getPath(), Toast.LENGTH_SHORT).show();
           // String path
           // Log.i("tag", "URI  : ",  );

        }
    }

    public void openFileChooser(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, requestCode);
    }
}