package com.dam.bookcita.fragment;

import static com.dam.bookcita.common.Constantes.*;
import static com.dam.bookcita.common.Util.convertirLienEnHttps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.dam.bookcita.activity.RechercheAvanceeLivreActivity;
import com.dam.bookcita.activity.SaisieManuelleLivreActivity;
import com.dam.bookcita.adapter.AdapterBook;
import com.dam.bookcita.R;
import com.dam.bookcita.activity.CameraXActivity;
import com.dam.bookcita.activity.DetailsLivreISBN;
import com.dam.bookcita.model.ModelBook;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RechercherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RechercherFragment extends Fragment implements AdapterBook.OnItemClickListener {

    private static final String TAG = "RechercherFragment";

    private static final int MAX = 20;
    private static final String MAX_RESULTS = "40";


    // test pour rebase
    private EditText etKeyword;
    private String keyword = "";
    private String totalItems;
    private RecyclerView rvBookByKeyword;

    private Button btnChercher;
    private Button btnClearText;
    private Button btnRechScanISBN;
    private Button btnAjoutManuelLivre;
    private Button btnRechAvancee;


    private ArrayList<ModelBook> bookArrayList;

    private AdapterBook adapterBook;

    public static final int PERMISSION_INTERNET = 0;

    private RequestQueue requestQueue;  //Pour volley

    /** Variables propres au fragment */

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


//    private void remplissageArrayListeEnDur() {
//        ModelBook modelBook1 = new ModelBook("https://books.google.com/books/content?id=StXltAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "Aucun Autre", "John Mac Arthur", "9782890823136");
//        ModelBook modelBook2 = new ModelBook("", "La force d'aimer", "Martin Luther King", "9782356140630");
//
//        bookArrayList.add(modelBook1);
//        bookArrayList.add(modelBook2);
//
//        adapterBook = new AdapterBook(this, bookArrayList);
//
//        rvBookByKeyword.setAdapter(adapterBook);
//
//    }

    // Methode pour verifier les permissions de l'application
    public boolean checkPermission() {
        int INTERNET_PERMISSION = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET);
        if (INTERNET_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.INTERNET}, INTERNET_PERMISSION);
            Log.i(TAG, "checkPermission: " + "Pas de permission INTERNET");
            return false;
        }
        Log.i(TAG, "checkPermission: " + "Permission INTERNET");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_INTERNET: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.INTERNET)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getContext(), getString(R.string.t_please_allow_internet_permission), Toast.LENGTH_SHORT).show();
                    } else {
                        // Lancement de l'app

                    }
                }
            }
        }
    }

    private void init(View view) {
        etKeyword = view.findViewById(R.id.etKeyword);
        rvBookByKeyword = view.findViewById(R.id.rvBookByKeyword);
        btnClearText = view.findViewById(R.id.btnClearText);
        btnAjoutManuelLivre = view.findViewById(R.id.btnAjoutManuelLivre);
        btnRechAvancee = view.findViewById(R.id.btnRechAvancee);

        btnRechScanISBN = view.findViewById(R.id.btnRechScanISBN);
        bookArrayList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());

        rvBookByKeyword.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        rvBookByKeyword.setItemAnimator(new DefaultItemAnimator());
    }









    private void parseJSON() throws UnsupportedEncodingException {
        String urlJSONFile;
        // il faut q soit egal a quelque chose sinon erreur => ici il faut que q soit "" et non pas rien
        if (keyword.equals("")) {
            //https://www.googleapis.com/books/v1/volumes?q=souris
            urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                    + "%22%22"
                    + "&maxResults="
                    + MAX_RESULTS
                    + "&key="
                    + "AIzaSyARotakRwdwvBqUpRRHwZ3X7URwamy86G0";
        } else {
            //https://www.googleapis.com/books/v1/volumes?q=souris
            urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                    + URLEncoder.encode(keyword, String.valueOf(StandardCharsets.UTF_8))
                    + "&maxResults="
                    + MAX_RESULTS
                    + "&key="
                    + "AIzaSyARotakRwdwvBqUpRRHwZ3X7URwamy86G0";
        }


        Log.i(TAG, "parseJSON: urlJSONFile : " + urlJSONFile);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlJSONFile, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    totalItems = response.getString("totalItems");
                    Log.i(TAG, "onResponse: totalItems : " + totalItems);


                    JSONArray jsonArray = response.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        String idGoogleBooks = item.getString("id");
                        Log.i(TAG, "onResponse: idGoogleBooks : " + idGoogleBooks);

                        JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                        String titre = "";
                        if (volumeInfo.has("title")) {
                            titre = volumeInfo.getString("title");
                        }
                        Log.i(TAG, "onResponse: titre : " + titre);
                        String auteur = "";
                        if (volumeInfo.has("authors")) {
                            JSONArray jsonArrayAuthors = volumeInfo.getJSONArray("authors");
                            // Pour l'instant on ne récupère que l'auteur principal (le 1er)
                            auteur = jsonArrayAuthors.get(0).toString();
                        }
                        Log.i(TAG, "onResponse: auteur : " + auteur);

                        String isbn = "";
                        if (volumeInfo.has("industryIdentifiers")) {
                            JSONArray jsonArrayIndustryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                            // l'ordre des isbn peut varier
                            for (int j = 0; j < jsonArrayIndustryIdentifiers.length(); j++) {
                                JSONObject jsonObjectIsbn = jsonArrayIndustryIdentifiers.getJSONObject(j);
                                if (jsonObjectIsbn.has("type")) {
                                    if (jsonObjectIsbn.getString("type").equals("ISBN_13")) {
                                        if (jsonObjectIsbn.has("identifier")) {
                                            isbn = jsonObjectIsbn.getString("identifier");
                                        }
                                    }

                                }
                            }
                        }


                        Log.i(TAG, "onResponse: isbn : " + isbn);

                        String coverUrl = "";
                        // recuperation de l'url de 'image de couverture
                        if (volumeInfo.has("imageLinks")) {
                            JSONObject jsonObjectImageLinks = volumeInfo.getJSONObject("imageLinks");
                            if (jsonObjectImageLinks.has("thumbnail")) {
                                coverUrl = jsonObjectImageLinks.getString("thumbnail");
                                coverUrl = convertirLienEnHttps(coverUrl);
                            }
                        }

                        Log.i(TAG, "onResponse: coverUrl : " + coverUrl);

                        bookArrayList.add(new ModelBook(coverUrl, titre, auteur, isbn, idGoogleBooks));

                    }

                    adapterBook = new AdapterBook(getContext(), bookArrayList);
                    rvBookByKeyword.setAdapter(adapterBook);
                    adapterBook.setOnItemClickListener(RechercherFragment.this);

                } catch (
                        JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "onResponse: JSONException : " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.i(TAG, "onErrorResponse: " + error.getMessage());
                Toast.makeText(getContext(), getString(R.string.t_please_check_internet_connection) + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(request);
    }


    @Override
    public void onItemClick(int position, View view) {
        Intent detailIntent = new Intent(getContext(), DetailsLivreISBN.class);
        ModelBook clickItemBook = bookArrayList.get(position);
        detailIntent.putExtra(ISBN, clickItemBook.getIsbn());
        detailIntent.putExtra(ID_GOOGLE_BOOKS, clickItemBook.getIdGoogleBooks());

        startActivity(detailIntent);
    }





    public RechercherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RechercherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RechercherFragment newInstance(String param1, String param2) {
        RechercherFragment fragment = new RechercherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rechercher, container, false);

        init(view);

//        btnChercher.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bookArrayList.clear();
//                keyword = etKeyword.getText().toString().trim();
//                //Toast.makeText(this, "motCle : " + keyword, Toast.LENGTH_SHORT).show();
//                try {
//                    parseJSON();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        btnClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etKeyword.setText("");
                btnClearText.setVisibility(View.GONE);
            }
        });

        btnRechScanISBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraXIntent = new Intent(getContext(), CameraXActivity.class);
                cameraXIntent.putExtra(TYPE_ISBN_OR_OCR, "ISBN");

                startActivity(cameraXIntent);
            }
        });

        if (checkPermission()) {
            //remplissageArrayListeEnDur();
        }
        btnClearText.setVisibility(View.GONE);

        btnAjoutManuelLivre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saisieManuelleIntent = new Intent(getContext(), SaisieManuelleLivreActivity.class);
                startActivity(saisieManuelleIntent);
            }
        });

        btnRechAvancee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rechAvanceeIntent = new Intent(getContext(), RechercheAvanceeLivreActivity.class);
                startActivity(rechAvanceeIntent);
            }
        });

        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bookArrayList.clear();
                keyword = etKeyword.getText().toString().trim();
                //Toast.makeText(this, "motCle : " + keyword, Toast.LENGTH_SHORT).show();
                try {
                    parseJSON();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    btnClearText.setVisibility(View.VISIBLE);
                } else {
                    btnClearText.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }
}