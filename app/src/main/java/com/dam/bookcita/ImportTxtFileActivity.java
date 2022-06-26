package com.dam.bookcita;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ImportTxtFileActivity extends AppCompatActivity {

    private static final String TAG = "ImportTxtFileActivity";

    private EditText etTxtFile, etGeneratedJSON, etParsedJSON;

    private Button btnParserJSON;

    private JSONObject jsonObjectGeneratedFromTxt;

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


    private String readFileTxt() {
        String myData = "";
        InputStream ips = getResources().openRawResource(R.raw.bible_ca);

        try {
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);

            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            br.close();
            ipsr.close();
            ips.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myData;
    }


    public void generateJSONfromTxt(View view) throws JsonProcessingException {

        String strTxt = readFileTxt();


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


            item.put("date", date);
            item.put("heure", heure);
            item.put("page", numeroPage);
            item.put("citation", citation);
            if (indexDebutNote != -1) {
                item.put("annotation", annotation);
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

        etTxtFile.setText(strTxt);
        etGeneratedJSON.setText(jsonString);
        Log.i(TAG, "generateJSONfromTxt: jsonString " + jsonString);

        btnParserJSON.setEnabled(true);

    }

    public void parseJson (View view){
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