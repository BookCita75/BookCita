package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;
import static com.dam.bookcita.common.Util.*;
import static com.google.firebase.firestore.FieldPath.documentId;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.dam.bookcita.R;

import java.text.ParseException;

public class ModifierLivreBD extends AppCompatActivity {

    private String title_livre, auteur_livre, editeur_livre, parution_livre, resume_livre, isbn_livre, couvertureImage, langue;
    private long nombres_pages_livres;
    private TextView tv_title_livre;
    private TextView tv_auteur_livre;
    private TextView tv_editeur_livre;
    private TextView tv_parution_livre;
    private TextView tv_resume_livre;
    private TextView tv_isbn_livre;
    private TextView tv_nombres_pages_livres;
    private ImageView iv_couverture_livre_bd;
    private EditText etLangueMLBD;
    private static final String TAG = "ModifierLivreBD";
    private RequestQueue requestQueue;

    private StorageReference fileStorage;

    private Uri serverFileUri;

    private Uri localFileUri;

    private String tokenStorage = "bd2fee53-f293-469c-8dcd-14ae4b5562ec";

    private FirebaseAuth auth;

    String id_BD;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection(LIVRES_COLLECTION_BD);
    private Button btn_modifier;
    private String uriPhoto;


    public void initUI() {
        tv_title_livre = findViewById(R.id.et_titre_texte);
        tv_auteur_livre = findViewById(R.id.et_auteur_Livres);
        tv_editeur_livre = findViewById(R.id.et_editeur_Livre);
        tv_parution_livre = findViewById(R.id.et_date_parution_livre);
        tv_resume_livre = findViewById(R.id.tv_updat_resume_livre_bd);
        tv_isbn_livre = findViewById(R.id.et_isbnLivre);
        tv_nombres_pages_livres = findViewById(R.id.et_nombre_pages);
        iv_couverture_livre_bd = findViewById(R.id.iv_couverture_livre_bd);
        tv_resume_livre.setMovementMethod(new ScrollingMovementMethod());
        etLangueMLBD = findViewById(R.id.etLangueMLBD);

        btn_modifier = findViewById(R.id.btn_save_livre);

        requestQueue = Volley.newRequestQueue(this);


        auth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_livre_bd);
        initUI();
        Intent intent = getIntent();
        id_BD = intent.getStringExtra(ID_BD);
        Log.i(TAG, "onCreate: id_BD UPDATE : " + id_BD);

        fileStorage = FirebaseStorage.getInstance().getReference();

        getListBooksDB(id_BD);

        btn_modifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Log.i(TAG, "onClick: " + tv_auteur_livre.getText().toString());

                    int nombredespages = Integer.parseInt(tv_nombres_pages_livres.getText().toString());
                    Log.i(TAG, "nombredespages: " + nombredespages);
                    if (tv_title_livre.getText().toString().equals("")) {
                        Toast.makeText(ModifierLivreBD.this, getString(R.string.t_please_enter_title), Toast.LENGTH_LONG).show();
                        return;
                    }
                    String langue = etLangueMLBD.getText().toString();
                    if (!(langue.equals("fr") || langue.equals("en") || langue.equals("de"))) {
                        Toast.makeText(ModifierLivreBD.this, getString(R.string.t_please_choose_language), Toast.LENGTH_LONG).show();
                        return;
                    }

                    String date_parution_livre = tv_parution_livre.getText().toString();

                    livresRef.document(id_BD).update(
                            TITRE_LIVRE_BD, tv_title_livre.getText().toString(),
                            AUTEUR_LIVRE_BD, tv_auteur_livre.getText().toString(),
                            EDITEUR_LIVRE_BD, tv_editeur_livre.getText().toString(),
                            DATE_PARUTION_LIVRE_BD, convertDateToFormatEn(date_parution_livre),
                            RESUME_LIVRE_BD, tv_resume_livre.getText().toString(),
                            ISBN_LIVRE_BD, tv_isbn_livre.getText().toString(),
                            NB_PAGES_LIVRE_BD, nombredespages,
                            LANGUE_LIVRE_BD, langue,
                            URL_COVER_LIVRE_BD, uriPhoto

                    );

                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(ModifierLivreBD.this, getString(R.string.t_please_enter_date), Toast.LENGTH_LONG).show();
                }

                catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onClick: e.getMessage() : " + e.getMessage());
                    Toast.makeText(ModifierLivreBD.this, getString(R.string.t_error) + e.getMessage(), Toast.LENGTH_LONG).show();
                }


                Toast.makeText(ModifierLivreBD.this, getString(R.string.t_book_updated_successfully), Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(ModifierLivreBD.this, MainActivity.class);
                mainIntent.putExtra(FRAG_TO_LOAD, MES_LIVRES_FRAGMENT);

                startActivity(mainIntent);
            }
        });


    }

    public void loadImage(View v) {

        Log.i(TAG, "updateImage: imdn");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 101);

    }

    public void uploadImage() {

        long time = System.currentTimeMillis();
        String imageName = time + ".jpg";
        Log.i(TAG, "uploadImage: imageName" + imageName);
        final StorageReference fileRef = fileStorage.child("couvertures_livres/" + imageName);

        String fileStorageStr = fileStorage.toString();

        Log.i(TAG, "uploadImage: fileStorageStr : " + fileStorageStr);
        // fileStorageStr => gs://bookcita...
        String fileStorageWithoutGs = fileStorageStr.substring(5);
        uriPhoto = "https://firebasestorage.googleapis.com/v0/b/" + fileStorageWithoutGs + "o/couvertures_livres%2F" + imageName + "?alt=media&token=" + tokenStorage;
        Log.i(TAG, "uploadImage: Uri photo : " + uriPhoto);


        try {

            fileRef.putFile(localFileUri)
                    .addOnCompleteListener(ModifierLivreBD.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                fileRef.getDownloadUrl().addOnSuccessListener(ModifierLivreBD.this,
                                        new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                serverFileUri = uri;
                                                Log.i(TAG, "onSuccess: On success" + uri.getPath());
                                                //serverFileUri = uri.getPath();
                                                iv_couverture_livre_bd.setImageURI(localFileUri);
                                            }
                                        });
                            } else {
                                Log.i(TAG, "onComplete: erreur dans le putFile ");
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "uploadImage: e.getMessage() : " + e.getMessage());
            Toast.makeText(this, getString(R.string.t_failed_to_load_picture) + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.i(TAG, "onActivityResult: after upload");
        if (requestCode == 101) {
            // Il y'a bien eu sélection d'image (sinon resultcode = RESULT_CANCELED)
            if (resultCode == RESULT_OK) {
                // Ajout des variables globales des uri cf 7.5
                localFileUri = data.getData();
//                data.setData(localFileUri);
//                data.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                data.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                uploadImage();
                Log.i(TAG, "onActivityResult: " + localFileUri);
                // Affectation de l'image sélectionnée à l'avatar (pour la variable globale cf 7.5)

            }
        }


    }

    public void getListBooksDB(String id_BD) {
        livresRef.whereEqualTo(documentId(), id_BD).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(TAG, document.getId() + " => " + document.getData());
                                title_livre = document.getString(TITRE_LIVRE_BD);
                                auteur_livre = document.getString(AUTEUR_LIVRE_BD);
                                couvertureImage = document.getString(URL_COVER_LIVRE_BD);
                                editeur_livre = document.getString(EDITEUR_LIVRE_BD);
                                parution_livre = document.getString(DATE_PARUTION_LIVRE_BD);
                                resume_livre = document.getString(RESUME_LIVRE_BD);
                                isbn_livre = document.getString(ISBN_LIVRE_BD);
                                nombres_pages_livres = document.getLong(NB_PAGES_LIVRE_BD);
                                langue = document.getString(LANGUE_LIVRE_BD);
                                String nbr_pages = String.valueOf(nombres_pages_livres);


                                Log.i(TAG, "onComplete: titre : " + title_livre);
                                //  Log.i(TAG, "nbr_pages : " + nbr_pages);

                                tv_title_livre.setText(title_livre);
                                tv_auteur_livre.setText(auteur_livre);
                                tv_editeur_livre.setText(editeur_livre);
                                tv_parution_livre.setText(convertDateToFormatFr(parution_livre));
                                tv_resume_livre.setText(resume_livre);
                                tv_isbn_livre.setText(isbn_livre);
                                tv_nombres_pages_livres.setText(nbr_pages);
                                etLangueMLBD.setText(langue);


                                //Gestion de l'image avec Glide
                                Context context = ModifierLivreBD.this;

                                RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .error(R.drawable.ic_couverture_livre_150)
                                        .placeholder(R.drawable.ic_couverture_livre_150);

                                // methode normale
                                Glide.with(context)
                                        .load(couvertureImage)
                                        .apply(options)
                                        .fitCenter()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(iv_couverture_livre_bd);
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}