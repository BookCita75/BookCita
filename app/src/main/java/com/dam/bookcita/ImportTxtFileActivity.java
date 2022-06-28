package com.dam.bookcita;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ImportTxtFileActivity extends AppCompatActivity {

    private static final String TAG = "ImportTxtFileActivity";

    private EditText etTxtFile, etGeneratedJSON, etParsedJSON;

    private Button btnParserJSON;

    private JSONObject jsonObjectGeneratedFromTxt;


    private static final int PICK_TXT_FILE = 2;
    private Context context;

    private Uri uri;
    String uri_path;
    File file;

    private void initUI() {
        etTxtFile = findViewById(R.id.etTxtFile);
        etGeneratedJSON = findViewById(R.id.etGeneratedJSON);
        btnParserJSON = findViewById(R.id.btnParserJSON);
        etParsedJSON = findViewById(R.id.etParsedJSON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_txt_file);

        initUI();
    }


    private String readFileTxt(String uri_pathComplet) throws FileNotFoundException {
        String myData = "";
        Log.i(TAG, "URI path : " + uri_path);

        int indexLastSlash = uri_pathComplet.lastIndexOf('/');
        Log.i(TAG, "onActivityResult: indexLastSlash : " + indexLastSlash);
        uri_path = uri_pathComplet.substring(0, indexLastSlash);
        Log.i(TAG, "onActivityResult: uriPathComplet : " + uri_pathComplet);
        Log.i(TAG, "onActivityResult: uri_path from onActivityResult : " + uri_path);

        String fileName = uri_pathComplet.substring(indexLastSlash + 1, uri_pathComplet.length());
        String uri_pathSansSlash = uri_path.substring(1);
        Log.i(TAG, "readFileTxt: fileName : " + fileName);




//        String uriPathCompletTest= getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ "/" + "bible_ca.txt";
//        Log.i(TAG, "readFileTxt: uriPathCompletTest : " + uriPathCompletTest);
//
//        String uriPathCompletTestSansSlash = uriPathCompletTest.substring(1);
//        Log.i(TAG, "readFileTxt: uriPathCompletTestSansSlash : " + uriPathCompletTestSansSlash);

        file=new File(uri_path, fileName);

        InputStream ips = new FileInputStream(file);
//        InputStream ips = getResources().openRawResource(R.raw.bible_ca);
        try {
            Log.i(TAG, "try IN : ");

            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
           // FileInputStream fis = new FileInputStream(file);

            //FileReader fileReader = new FileReader(fileName);

           // BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//            BufferedReader br = new BufferedReader(new FileReader(file));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
                Log.i(TAG, "MYDATA: " + myData);
            }

            br.close();

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
                Toast.makeText(context, "Selectionner un fichier Texte", Toast.LENGTH_SHORT).show();
            }

            uri = data.getData();


            String uri_pathComplet = uri.getPath();

            try {
                String strTxt = readFileTxt(uri_pathComplet);
                Log.i(TAG, "onActivityResult: StrTXT : " + strTxt);
            } catch (FileNotFoundException e) {
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
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode rootNode = mapper.createObjectNode();
//
//
//        ArrayList<ObjectNode> arrayList = new ArrayList<>();
//
//
//        String[] tabStrTxt = strTxt.split("-------------------");
//        // -1 car apres le dernier ------------------- il y a un \n
//        int nbCitations = tabStrTxt.length - 1;
//        Log.i(TAG, "generateJSONfromTxt: nbCitations : " + nbCitations);
//        for (int i = 0; i < nbCitations; i++) {
//
//            ObjectNode item = mapper.createObjectNode();
//
//            String[] tabItemCitationAnnotation = tabStrTxt[i].split("  |  ");
//            Log.i(TAG, "generateJSONfromTxt: tabItemCitationAnnotation[0] : " + tabItemCitationAnnotation[0]);
//            String dateHeure;
//
//            //Le premier item de citation contient aussi les infos du livre
//            if (i == 0) {
//                int indexPremierRetourChariot = tabItemCitationAnnotation[0].indexOf('\n');
//                Log.i(TAG, "generateJSONfromTxt: indexPremierRetourChariot : " + indexPremierRetourChariot);
//                dateHeure = tabItemCitationAnnotation[0].substring(indexPremierRetourChariot);
//            } else {
//                dateHeure = tabItemCitationAnnotation[0];
//            }
//            //le premier caractère étant un \n
//            dateHeure = dateHeure.substring(1);
//
//            // dans le cas ou il y a un titre avant la date et l'heure : il y a aussi un retour chariot
//            int indexRetourChariotAvDateHeure = dateHeure.indexOf('\n');
//            if (indexRetourChariotAvDateHeure != -1) {
//                // cas ou il y a un titre avant la date et l'heure
//                dateHeure = dateHeure.substring(indexRetourChariotAvDateHeure + 1);
//            }
//
//            String[] tabDateHeure = dateHeure.split(" ");
//
//            String date = tabDateHeure[0];
//            Log.i(TAG, "generateJSONfromTxt: date : " + date);
//            String heure = tabDateHeure[1];
//            Log.i(TAG, "generateJSONfromTxt: heure : " + heure);
//
//
//            Log.i(TAG, "generateJSONfromTxt: tabItemCitationAnnotation[2] : " + tabItemCitationAnnotation[2]);
//            int indexPremierRetourChariotApNumPage = tabItemCitationAnnotation[2].indexOf('\n');
//            Log.i(TAG, "generateJSONfromTxt: indexPremierRetourChariotApNumPage" + indexPremierRetourChariotApNumPage);
//            //Page No. : => fait 11 caractères
//            String numeroPage = tabItemCitationAnnotation[2].substring(10, indexPremierRetourChariotApNumPage);
//            Log.i(TAG, "generateJSONfromTxt: numeroPage : " + numeroPage);
//
//            int indexDebutNote = tabItemCitationAnnotation[2].indexOf('【');
//            Log.i(TAG, "generateJSONfromTxt: indexDebutNote : " + indexDebutNote);
//
//            String citation;
//            String annotation = "";
//            //Certains items n'ont pas de note
//            if (indexDebutNote == -1) {
//                // item sans note :
//                citation = tabItemCitationAnnotation[2].substring(indexPremierRetourChariotApNumPage + 1, tabItemCitationAnnotation[2].length() - 1);
//                Log.i(TAG, "generateJSONfromTxt: citation (item sans note) : " + citation);
//            } else {
//                citation = tabItemCitationAnnotation[2].substring(indexPremierRetourChariotApNumPage + 1, indexDebutNote - 1);
//                Log.i(TAG, "generateJSONfromTxt: citation : " + citation);
//                // L'annotation commence à 6 caractères après 【 car : 【Note】 fait 6 caractères
//                annotation = tabItemCitationAnnotation[2].substring(indexDebutNote + 6, tabItemCitationAnnotation[2].length() - 1);
//                Log.i(TAG, "generateJSONfromTxt: annotation : " + annotation);
//            }
//
//
//            item.put("date", date);
//            item.put("heure", heure);
//            item.put("page", numeroPage);
//            item.put("citation", citation);
//            if (indexDebutNote != -1) {
//                item.put("annotation", annotation);
//            }
//            arrayList.add(item);
//
//        }
//
//
//        ArrayNode arrayNodeItems = mapper.valueToTree(arrayList);
//
//        rootNode.putArray("items").addAll(arrayNodeItems);
//
//        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
//        try {
//            jsonObjectGeneratedFromTxt = new JSONObject(jsonString);
//            Log.i(TAG, "generateJSONfromTxt: jsonObjectGeneratedFromTxt : " + jsonObjectGeneratedFromTxt);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        etTxtFile.setText(strTxt);
//        etGeneratedJSON.setText(jsonString);
//        Log.i(TAG, "generateJSONfromTxt: jsonString " + jsonString);
//
//        btnParserJSON.setEnabled(true);

    }

    public void parseJson(View view) {
        parseGeneratedJson();
    }

    private void parseGeneratedJson() {
        try {
            String parsedJSON = "";
            JSONArray jsonArray = jsonObjectGeneratedFromTxt.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                parsedJSON += "item n° : " + i + '\n';
                JSONObject item = jsonArray.getJSONObject(i);

                String date = item.getString("date");
                Log.i(TAG, "parseGeneratedJson: date : " + date);
                parsedJSON += "date : " + date + '\n';

                String heure = item.getString("heure");
                Log.i(TAG, "parseGeneratedJson: heure : " + heure);
                parsedJSON += "heure : " + heure + '\n';

                String page = item.getString("page");
                Log.i(TAG, "parseGeneratedJson: page : " + page);
                parsedJSON += "page : " + page + '\n';

                String citation = item.getString("citation");
                Log.i(TAG, "parseGeneratedJson: citation : " + citation);
                parsedJSON += "citation : " + citation + '\n';

                if (item.has("annotation")) {
                    String annotation = item.getString("annotation");
                    Log.i(TAG, "parseGeneratedJson: annotation : " + annotation);
                    parsedJSON += "annotation : " + annotation + '\n';
                }

                parsedJSON += '\n';

            }
            etParsedJSON.setText(parsedJSON);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }
}