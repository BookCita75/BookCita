package com.dam.bookcita;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ParseTxtToJSONActivity extends AppCompatActivity {

    private static final String TAG = "ParseTxtToJSONActivity";

    private EditText etResultJSON;

    private JSONObject jsonObjectGeneratedFromTxt;

    private void initUI() {
        etResultJSON = findViewById(R.id.etResultJSON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_txt_to_json);

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


    public void parseTxtToJson(View view) throws JsonProcessingException {

        String strTxt = readFileTxt();


        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();


        ArrayList<ObjectNode> arrayList = new ArrayList<>();


        String[] tabStrTxt = strTxt.split("-------------------");
        // -1 car apres le dernier ------------------- il y a un \n
        int nbCitations = tabStrTxt.length - 1;
        Log.i(TAG, "parseTxtToJson: nbCitations : " + nbCitations);
        for (int i = 0; i < nbCitations; i++) {

            ObjectNode item = mapper.createObjectNode();
            //Log.i(TAG, "parseTxtToJson: tabStrTxt[1]: " + tabStrTxt[1]);

            String[] tabItemCitationAnnotation = tabStrTxt[i].split("  |  ");
            Log.i(TAG, "parseTxtToJson: tabItemCitationAnnotation[0] : " + tabItemCitationAnnotation[0]);
            String dateHeure;

            //Le premier item de citation contient aussi les infos du livre
            if (i == 0) {
                int indexPremierRetourChariot = tabItemCitationAnnotation[0].indexOf('\n');
                Log.i(TAG, "parseTxtToJson: indexPremierRetourChariot : " + indexPremierRetourChariot);
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
                dateHeure = dateHeure.substring(indexRetourChariotAvDateHeure+1);
            }

            String[] tabDateHeure = dateHeure.split(" ");

            String date = tabDateHeure[0];
            Log.i(TAG, "parseTxtToJson: date : " + date);
            String heure = tabDateHeure[1];
            Log.i(TAG, "parseTxtToJson: heure : " + heure);


            Log.i(TAG, "parseTxtToJson: tabItemCitationAnnotation[2] : " + tabItemCitationAnnotation[2]);
            int indexPremierRetourChariotApNumPage = tabItemCitationAnnotation[2].indexOf('\n');
            Log.i(TAG, "parseTxtToJson: indexPremierRetourChariotApNumPage" + indexPremierRetourChariotApNumPage);
            //Page No. : => fait 11 caractères
            String numeroPage = tabItemCitationAnnotation[2].substring(10, indexPremierRetourChariotApNumPage);
            Log.i(TAG, "parseTxtToJson: numeroPage : " + numeroPage);

            int indexDebutNote = tabItemCitationAnnotation[2].indexOf('【');
            Log.i(TAG, "parseTxtToJson: indexDebutNote : " + indexDebutNote);

            String citation;
            String annotation="";
            //Certains items n'ont pas de note
            if(indexDebutNote == -1) {
                // item sans note :
                citation = tabItemCitationAnnotation[2].substring(indexPremierRetourChariotApNumPage + 1, tabItemCitationAnnotation[2].length() - 1);
                Log.i(TAG, "parseTxtToJson: citation (item sans note) : " + citation);
            } else {
                citation = tabItemCitationAnnotation[2].substring(indexPremierRetourChariotApNumPage + 1, indexDebutNote - 1);
                Log.i(TAG, "parseTxtToJson: citation : " + citation);
                // L'annotation commence à 6 caractères après 【 car : 【Note】 fait 6 caractères
                annotation = tabItemCitationAnnotation[2].substring(indexDebutNote + 6, tabItemCitationAnnotation[2].length() - 1);
                Log.i(TAG, "parseTxtToJson: annotation : " + annotation);
            }



            item.put("date", date);
            item.put("heure", heure);
            item.put("page", numeroPage);
            item.put("citation", citation);
            if (indexDebutNote != -1){
                item.put("annotation", annotation);
            }
            arrayList.add(item);

        }


        ArrayNode arrayNodeItems = mapper.valueToTree(arrayList);

        rootNode.putArray("items").addAll(arrayNodeItems);

        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        try {
            jsonObjectGeneratedFromTxt= new JSONObject(jsonString);
            Log.i(TAG, "parseTxtToJson: jsonObjectGeneratedFromTxt : " + jsonObjectGeneratedFromTxt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        etResultJSON.setText(strTxt + jsonString);

        Log.i(TAG, "parseTxtToJson: jsonString " + jsonString);

    }
}