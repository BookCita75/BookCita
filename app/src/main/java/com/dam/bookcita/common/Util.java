package com.dam.bookcita.common;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.MalformedURLException;
import java.net.URL;

public class Util {

    /** Méthode pour le check d'internet **/
    public static boolean connectionAvailable(Context context){
        // Pour utiliser cette classe il ne faut oublier d'ajouter la permisson dans le manifest
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null ){
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        } else {
            return false;
        }
    }

    public static String convertirLienEnHttps(String lien) {
        try {
            URL url_lien = new URL(lien);
            URL url_lienHttps = new URL("https", url_lien.getHost(), url_lien.getPort(), url_lien.getFile());

            String lienHttps = url_lienHttps.toString();
            return lienHttps;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }
}
