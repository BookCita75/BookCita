package com.dam.bookcita.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static String firstLetterToUpperCase(CharSequence s) {
        String result = "";
        String str = s.toString();
        if (!str.equals("")) {
            result = str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return result;
    }

    public static String convertDateToFormatFr(String dateInFormatEnStr){
        String dateInFormatFrStr = "";
        if(!dateInFormatEnStr.equals("")) {
            SimpleDateFormat dateFormatEn = new SimpleDateFormat("yyyy-MM-dd");
            Date dateInFormatEn = null;
            try {
                dateInFormatEn = dateFormatEn.parse(dateInFormatEnStr);
                SimpleDateFormat dateFormatFr = new SimpleDateFormat("dd/MM/yyyy");
                dateInFormatFrStr = dateFormatFr.format(dateInFormatEn);
            } catch (ParseException e) {
                e.printStackTrace();
                SimpleDateFormat dateFormat_YYYYMM_En = new SimpleDateFormat("yyyy-MM");
                try{
                    Date dateInFormat_YYYY_MM_En = dateFormat_YYYYMM_En.parse(dateInFormatEnStr);
                    SimpleDateFormat dateFormat_MM_YYYY_Fr = new SimpleDateFormat("MM/yyyy");
                    dateInFormatFrStr = dateFormat_MM_YYYY_Fr.format(dateInFormat_YYYY_MM_En);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                    SimpleDateFormat dateFormat_YYYY_En = new SimpleDateFormat("yyyy");
                    try{
                        Date dateInFormat_YYYY_En = dateFormat_YYYY_En.parse(dateInFormatEnStr);
                        dateInFormatFrStr = dateInFormatEnStr;
                    } catch (ParseException pE) {
                        pE.printStackTrace();
                    }
                }
            }
        }
        return dateInFormatFrStr;
    }

    public static String convertDateToFormatEn(String dateInFormatFrStr) throws ParseException {
        String dateInFormatEnStr = "";
        if (!dateInFormatFrStr.equals("")) {
            SimpleDateFormat dateFormatFr = new SimpleDateFormat("dd/MM/yyyy");
            Date dateInFormatFr = null;
            try {
                dateInFormatFr = dateFormatFr.parse(dateInFormatFrStr);
                SimpleDateFormat dateFormatEn = new SimpleDateFormat("yyyy-MM-dd");
                dateInFormatEnStr = dateFormatEn.format(dateInFormatFr);
            } catch (ParseException e) {
                e.printStackTrace();
                SimpleDateFormat dateFormat_MM_YYYY_Fr = new SimpleDateFormat("MM/yyyy");
                try{
                    Date dateInFormat_MM_YYYY_Fr = dateFormat_MM_YYYY_Fr.parse(dateInFormatFrStr);
                    SimpleDateFormat dateFormat_YYYYMM_En = new SimpleDateFormat("yyyy-MM");
                    dateInFormatEnStr = dateFormat_YYYYMM_En.format(dateInFormat_MM_YYYY_Fr);

                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                    SimpleDateFormat dateFormat_YYYY_Fr = new SimpleDateFormat("yyyy");
                    try{
                        Date dateInFormat_YYYY_Fr = dateFormat_YYYY_Fr.parse(dateInFormatFrStr);
                        dateInFormatEnStr = dateInFormatFrStr;
                    } catch (ParseException pE) {
                        pE.printStackTrace();
                        throw pE;
                    }
                }
            }
        }
        return dateInFormatEnStr;
    }
}
