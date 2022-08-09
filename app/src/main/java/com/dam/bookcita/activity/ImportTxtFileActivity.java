package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dam.bookcita.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.dam.bookcita.model.ModelCitation;

public class ImportTxtFileActivity extends AppCompatActivity {

    private static final String TAG = "ImportTxtFileActivity";

    private JSONObject jsonObjectGeneratedFromTxt;

    public static final int PERMISSION_READ = 0;
    public static final int PERMISSION_WRITE = 1;
    public static final int PERMISSION_MANAGE = 2;
    private static final int PICK_TXT_FILE = 2;
    private Context context;

    private Uri uri;
    String uri_path;
    File file;

    private TextView tvTitreIC;
    private TextView tvAuteurIC;
    private ImageView ivCoverIC;
    private TextView tvNbCitationsIC;

    private String id_BD;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference citationsRef = db.collection(CITATIONS_COLLECTION_BD);
    private FirebaseAuth auth;

    private boolean existeDeja = false;
    private int cptCitationsImportees = 0;
    private int cptCitationsDejaExistantesEnBD = 0;
    private int nbTotalCitationAImporter = 0;

    private String id_user;


    private void init(){
        tvTitreIC = findViewById(R.id.tvTitreIC);
        tvAuteurIC = findViewById(R.id.tvAuteurIC);
        ivCoverIC = findViewById(R.id.ivCoverIC);

        tvNbCitationsIC = findViewById(R.id.tvNbCitationsIC);
    }

    // Methode pour verifier les permissions de l'application
    public boolean checkPermissionReadExternalStorage() {
        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            Log.i(TAG, "checkPermissionReadExternalStorage: " + "Pas de permission READ_EXTERNAL_STORAGE");
            return false;
        }
        Log.i(TAG, "checkPermissionReadExternalStorage: " + "Permission READ_EXTERNAL_STORAGE");
        return true;
    }

    public boolean checkPermissionWriteExternalStorage() {
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            Log.i(TAG, "checkPermissionWriteExternalStorage: " + "Pas de permission WRITE_EXTERNAL_STORAGE");
            return false;
        }
        Log.i(TAG, "checkPermissionWriteExternalStorage: " + "Permission WRITE_EXTERNAL_STORAGE");
        return true;
    }

    public boolean checkPermissionManageExternalStorage() {
        int MANAGE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        if (MANAGE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE}, PERMISSION_MANAGE);
            Log.i(TAG, "checkPermissionManageExternalStorage: " + "Pas de permission MANAGE_EXTERNAL_STORAGE");
            return false;
        }
        Log.i(TAG, "checkPermissionManageExternalStorage: " + "Permission MANAGE_EXTERNAL_STORAGE");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, getString(R.string.t_please_grant_access_read_files), Toast.LENGTH_LONG).show();
                    } else {
                        // Lancement de l'app

                    }
                }
                break;
            }
            case PERMISSION_WRITE: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, getString(R.string.t_please_grant_access_write_files), Toast.LENGTH_LONG).show();
                    } else {
                        // Lancement de l'app

                    }
                }
                break;
            }
//            case PERMISSION_MANAGE: {
//                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
//                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                        Toast.makeText(this, "Veuillez autoriser la gestion de tous les fichiers.", Toast.LENGTH_LONG).show();
//                    } else {
//                        // Lancement de l'app
//
//                    }
//                }
//                break;
//            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_txt_file);

        checkPermissionReadExternalStorage();
        checkPermissionWriteExternalStorage();
        checkPermissionManageExternalStorage();

        Intent intent = getIntent();
        id_BD = intent.getStringExtra(ID_BD);
        Log.i(TAG, "onCreate: id_BD reçu : " + id_BD);

        init();
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();

        getFicheBookFromDB();
        remplirNbCitationsFOBFromDB();



    }

    private void getFicheBookFromDB() {


//        Query query = livresRef.whereEqualTo("id", id_BD);

        db.collection(LIVRES_COLLECTION_BD)
                .whereEqualTo(documentId(), id_BD)
//                .whereEqualTo("auteur_livre", "Luc Lang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(TAG, document.getId() + " => " + document.getData());
                                String titre = document.getString(TITRE_LIVRE_BD);
                                String auteur = document.getString(AUTEUR_LIVRE_BD);
                                String coverUrl = document.getString(URL_COVER_LIVRE_BD);
                                Log.i(TAG, "onComplete: titre : " + titre);
                                Log.i(TAG, "onComplete: auteur : " + auteur);
                                Log.i(TAG, "onComplete: coverUrl : " + coverUrl);
                                tvTitreIC.setText(titre);
                                tvAuteurIC.setText(auteur);
                                //Gestion de l'image avec Glide
                                Context context = ImportTxtFileActivity.this;

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
                                        .into(ivCoverIC);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }

    private void remplirNbCitationsFOBFromDB() {

        db.collection(CITATIONS_COLLECTION_BD)
                .whereEqualTo(ID_USER_CITATION_BD,id_user)
                .whereEqualTo(ID_BD_LIVRE_CITATION_BD, id_BD)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int nbCitationFOB = querySnapshot.size();
                            Log.i(TAG, "onComplete: nbCitationFOB : " + nbCitationFOB);
                            tvNbCitationsIC.setText(String.valueOf(nbCitationFOB));
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }

    private String readFileTxt(String uri_pathComplet) throws FileNotFoundException {
        String myData = "";


        String uriPathCompletAndroid = uri_pathComplet;

        // uriPathCompletAndroid : utilisable pour le FileInputStream
        if (uri_pathComplet.contains("external_files")) {
            uriPathCompletAndroid = uri_pathComplet.replace("external_files", "storage/emulated/0");
        } else if (uri_pathComplet.contains("root_files")) {
            uriPathCompletAndroid = uri_pathComplet.replace("root_files/", "");
        }
        Log.i(TAG, "readFileTxt: uriPathCompletAndroid : " + uriPathCompletAndroid);


        try {

            // ancienne version
//        InputStream ips = getResources().openRawResource(R.raw.bible_ca);

//        InputStream ips = new FileInputStream("/storage/9C33-6BBD/Download/bookCitaTest3/ts_ca.txt");
            InputStream ips = new FileInputStream(uriPathCompletAndroid);
            Log.i(TAG, "try IN : ");

            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);

            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
                Log.i(TAG, "MYDATA: " + myData);
            }

            br.close();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.t_please_check_app_permissions) + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return myData;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        context = getApplicationContext();
        if (requestCode == requestCode && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(context, getString(R.string.t_choose_txt_file), Toast.LENGTH_SHORT).show();
            }

            uri = data.getData();


            String uri_pathComplet = uri.getPath();

            try {
                String strTxt = readFileTxt(uri_pathComplet);
                Log.i(TAG, "onActivityResult: StrTXT : " + strTxt);
                if (!strTxt.equals("")) {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode rootNode = mapper.createObjectNode();


                    ArrayList<ObjectNode> arrayList = new ArrayList<>();


                    String[] tabStrTxt = strTxt.split("-------------------");
                    // -1 car apres le dernier ------------------- il y a un \n
                    int nbCitations = tabStrTxt.length - 1;
                    Log.i(TAG, "generateJSONfromTxt: nbCitations : " + nbCitations);
                    for (int i = 0; i < nbCitations; i++) {

                        ObjectNode item = mapper.createObjectNode();

                        String[] tabItemCitationAnnotation = tabStrTxt[i].split("  |  ");
                        Log.i(TAG, "generateJSONfromTxt: tabItemCitationAnnotation[0] : " + tabItemCitationAnnotation[0]);
                        String dateHeure;

                        //Le premier item de citation contient aussi les infos du livre
                        if (i == 0) {
                            int indexPremierRetourChariot = tabItemCitationAnnotation[0].indexOf('\n');
                            Log.i(TAG, "generateJSONfromTxt: indexPremierRetourChariot : " + indexPremierRetourChariot);
                            dateHeure = tabItemCitationAnnotation[0].substring(indexPremierRetourChariot);
                        } else {
                            dateHeure = tabItemCitationAnnotation[0];
                        }
                        //le premier caractère étant un \n
                        dateHeure = dateHeure.substring(1);

                        // dans le cas ou il y a un titre avant la date et l'heure : il y a aussi un retour chariot
                        int indexRetourChariotAvDateHeure = dateHeure.indexOf('\n');
                        if (indexRetourChariotAvDateHeure != -1) {
                            // cas ou il y a un titre avant la date et l'heure
                            dateHeure = dateHeure.substring(indexRetourChariotAvDateHeure + 1);
                        }

                        String[] tabDateHeure = dateHeure.split(" ");

                        String date = tabDateHeure[0];
                        Log.i(TAG, "generateJSONfromTxt: date : " + date);
                        String heure = tabDateHeure[1];
                        Log.i(TAG, "generateJSONfromTxt: heure : " + heure);


                        Log.i(TAG, "generateJSONfromTxt: tabItemCitationAnnotation[2] : " + tabItemCitationAnnotation[2]);
                        int indexPremierRetourChariotApNumPage = tabItemCitationAnnotation[2].indexOf('\n');
                        Log.i(TAG, "generateJSONfromTxt: indexPremierRetourChariotApNumPage" + indexPremierRetourChariotApNumPage);
                        //Page No. : => fait 11 caractères
                        String numeroPage = tabItemCitationAnnotation[2].substring(10, indexPremierRetourChariotApNumPage);
                        Log.i(TAG, "generateJSONfromTxt: numeroPage : " + numeroPage);

                        int indexDebutNote = tabItemCitationAnnotation[2].indexOf('【');
                        Log.i(TAG, "generateJSONfromTxt: indexDebutNote : " + indexDebutNote);

                        String citation;
                        String annotation = "";
                        //Certains items n'ont pas de note
                        if (indexDebutNote == -1) {
                            // item sans note :
                            citation = tabItemCitationAnnotation[2].substring(indexPremierRetourChariotApNumPage + 1, tabItemCitationAnnotation[2].length() - 1);
                            Log.i(TAG, "generateJSONfromTxt: citation (item sans note) : " + citation);
                        } else {
                            citation = tabItemCitationAnnotation[2].substring(indexPremierRetourChariotApNumPage + 1, indexDebutNote - 1);
                            Log.i(TAG, "generateJSONfromTxt: citation : " + citation);
                            // L'annotation commence à 6 caractères après 【 car : 【Note】 fait 6 caractères
                            annotation = tabItemCitationAnnotation[2].substring(indexDebutNote + 6, tabItemCitationAnnotation[2].length() - 1);
                            Log.i(TAG, "generateJSONfromTxt: annotation : " + annotation);
                        }


                        item.put(DATE_CITATION_BD, date);
                        item.put(HEURE_CITATION_BD, heure);
                        item.put("page", numeroPage);
                        item.put(CITATION_CITATION_BD, citation);
                        if (indexDebutNote != -1) {
                            item.put(ANNOTATION_CITATION_BD, annotation);
                        }
                        arrayList.add(item);

                    }


                    ArrayNode arrayNodeItems = mapper.valueToTree(arrayList);

                    rootNode.putArray("items").addAll(arrayNodeItems);

                    String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                    try {
                        jsonObjectGeneratedFromTxt = new JSONObject(jsonString);
                        Log.i(TAG, "generateJSONfromTxt: jsonObjectGeneratedFromTxt : " + jsonObjectGeneratedFromTxt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "generateJSONfromTxt: jsonString " + jsonString);

                    parseGeneratedJson();
                }

            } catch (FileNotFoundException | JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateJSONfromTxt(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("text/plain");
        startActivityForResult(intent, PICK_TXT_FILE);

        Log.i(TAG, "generateJSONfromTxt: test");

//        String strTxt = readFileTxt();


    }

    public void parseJson(View view) {
        parseGeneratedJson();
    }

    private void parseGeneratedJson() {
        try {
            cptCitationsImportees = 0;
            cptCitationsDejaExistantesEnBD = 0;

            String parsedJSON = "";
            JSONArray jsonArray = jsonObjectGeneratedFromTxt.getJSONArray("items");
            nbTotalCitationAImporter = jsonArray.length();
            for (int i = 0; i < jsonArray.length(); i++) {


                parsedJSON += "item n° : " + i + '\n';
                JSONObject item = jsonArray.getJSONObject(i);

                String date = item.getString(DATE_CITATION_BD);
                Log.i(TAG, "parseGeneratedJson: date : " + date);
                parsedJSON += "date : " + date + '\n';

                String heure = item.getString(HEURE_CITATION_BD);
                Log.i(TAG, "parseGeneratedJson: heure : " + heure);
                parsedJSON += "heure : " + heure + '\n';

                String page = item.getString("page");
                Log.i(TAG, "parseGeneratedJson: page : " + page);
                int numPage = Integer.valueOf(page);

                parsedJSON += "page : " + page + '\n';

                String citation = item.getString(CITATION_CITATION_BD);
                Log.i(TAG, "parseGeneratedJson: citation : " + citation);
                parsedJSON += "citation : " + citation + '\n';

                String annotation = "";
                if (item.has(ANNOTATION_CITATION_BD)) {
                    annotation = item.getString(ANNOTATION_CITATION_BD);
                    Log.i(TAG, "parseGeneratedJson: annotation : " + annotation);
                    parsedJSON += "annotation : " + annotation + '\n';
                }

                parsedJSON += '\n';
                verifieSiCitationExisteDsBD(date, heure, citation, annotation, numPage);
                //enregistrement dans la BD de la citation importee (si la date de la citation et l'heure de la citation n'existe pas deja dans la base pour ce livre)


            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.t_error_import_quotes_from_txt_file) + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void afterOnCompleteVerifieSiCitationExisteDsBD(boolean existeDeja, String citation, String annotation, int numPage, String date, String heure) {
        if (!existeDeja) {
            ModelCitation citationImportee = new ModelCitation(id_BD, citation, annotation, numPage, date, heure, id_user);
            citationsRef.add(citationImportee);
            cptCitationsImportees++;
        } else {
            cptCitationsDejaExistantesEnBD++;
        }
        if ((cptCitationsImportees + cptCitationsDejaExistantesEnBD) == nbTotalCitationAImporter) {
            //dernière citation à importer a été traitée
            Toast.makeText(this, String.valueOf(cptCitationsImportees) + getString(R.string.t_quotes_and_notes_imported_successfully) + String.valueOf(cptCitationsDejaExistantesEnBD) + " citation(s) et annotation(s) non importée(s) car déjà existante(s) dans la base de données.", Toast.LENGTH_LONG).show();
            Intent mainIntent = new Intent(ImportTxtFileActivity.this, MainActivity.class);
            mainIntent.putExtra(FRAG_TO_LOAD, MES_CITATIONS_FRAGMENT);
            startActivity(mainIntent);
        }
    }

    private void verifieSiCitationExisteDsBD(String date, String heure, String citation, String annotation, int numPage) {
        existeDeja = false;

        db.collection(CITATIONS_COLLECTION_BD)
                .whereEqualTo(ID_BD_LIVRE_CITATION_BD, id_BD)
                .whereEqualTo(DATE_CITATION_BD, date)
                .whereEqualTo(HEURE_CITATION_BD, heure)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.size() != 0) {
                                existeDeja = true;

                                // normalement, il n'y a qu'un seul enregistrement en base avec la même date et la même heure
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    String annotationFromDB = document.getString(ANNOTATION_CITATION_BD);
                                    String citationFromDB = document.getString(CITATION_CITATION_BD);
                                    String dateFromDB = document.getString(DATE_CITATION_BD);
                                    String heureFromDB = document.getString(HEURE_CITATION_BD);

                                    Log.i(TAG, "onComplete: annotationFromDB : " + annotationFromDB);
                                    Log.i(TAG, "onComplete: citationFromDB : " + citationFromDB);
                                    Log.i(TAG, "onComplete: dateFromDB : " + dateFromDB);
                                    Log.i(TAG, "onComplete: heureFromDB : " + heureFromDB);

                                }
                                afterOnCompleteVerifieSiCitationExisteDsBD(existeDeja, citation, annotation, numPage, date, heure);
                            } else {
                                existeDeja = false;
                                afterOnCompleteVerifieSiCitationExisteDsBD(existeDeja, citation, annotation, numPage, date, heure);
                            }

                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
}