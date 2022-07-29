package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dam.bookcita.R;
import com.dam.bookcita.model.ModelDetailsLivre;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaisieManuelleLivreActivity extends AppCompatActivity {

    private static final String TAG = "SaisieManuelleLivreActi";

    private EditText etTitreSML;
    private EditText etAuteurSML;
    private EditText etDateSML;
    private EditText etEditeurSML;
    private EditText etNbPagesSML;
    private EditText etIsbnSML;
    private AutoCompleteTextView actvLangueSML;
    private Button btnChooseFileSML;
    private ImageView ivCoverSML;
    private EditText etmlResumeSML;
    private Button btValiderSML;


    private String isbn;

    private boolean existeDeja;

    private String id_user;

    private Uri localFileUri;

    private StorageReference fileStorage;

    private String uriPhoto="";

    private Uri serverFileUri;

    private String tokenStorage = "bd2fee53-f293-469c-8dcd-14ae4b5562ec";

    private FirebaseAuth auth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference livresRef = db.collection(LIVRES_COLLECTION_BD);

    private static final String[] LANGUES_VAL = new String[]{"fr", "en", "de"};


    private void init() {
        etTitreSML = findViewById(R.id.etTitreSML);
        etAuteurSML = findViewById(R.id.etAuteurSML);
        etDateSML = findViewById(R.id.etDateSML);
        etEditeurSML = findViewById(R.id.etEditeurSML);
        etNbPagesSML = findViewById(R.id.etNbPagesSML);
        etIsbnSML = findViewById(R.id.etIsbnSML);
        actvLangueSML = findViewById(R.id.actvLangueSML);
        btnChooseFileSML = findViewById(R.id.btnChooseFileSML);
        ivCoverSML = findViewById(R.id.ivCoverSML);
        etmlResumeSML = findViewById(R.id.etmlResumeSML);
        btValiderSML = findViewById(R.id.btValiderSML);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisie_manuelle_livre);

        init();

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        id_user = firebaseUser.getUid();
        fileStorage = FirebaseStorage.getInstance().getReference();

        final ArrayAdapter<String> adapterLangues = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                LANGUES_VAL
        );
        //adapterLangues.setDropDownViewTheme();
        actvLangueSML.setAdapter(adapterLangues);

        actvLangueSML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvLangueSML.showDropDown();
            }
        });

        btnChooseFileSML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
        });



        Log.i(TAG, "onCreate: av btValider");
        btValiderSML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titre = etTitreSML.getText().toString();
                Log.i(TAG, "onClick: titre : " + titre);

                if (titre.equals("")) {
                    Toast.makeText(SaisieManuelleLivreActivity.this, "Veuillez saisir un titre.", Toast.LENGTH_LONG).show();
                    return;
                }

                String auteur = etAuteurSML.getText().toString();
                Log.i(TAG, "onClick: auteur : " + auteur);
                String date = etDateSML.getText().toString();
                Log.i(TAG, "onClick: date : " + date);
                String editeur = etEditeurSML.getText().toString();
                Log.i(TAG, "onClick: editeur : " + editeur);
                String nbPagesStr = etNbPagesSML.getText().toString();
                Log.i(TAG, "onClick: nbPagesStr : " + nbPagesStr);
                isbn = etIsbnSML.getText().toString();
                Log.i(TAG, "onClick: isbn : " + isbn);
                String langue = actvLangueSML.getText().toString();
                Log.i(TAG, "onClick: langue : " + langue);
                if (!(langue.equals("fr") || langue.equals("en") || langue.equals("de"))) {
                    Toast.makeText(SaisieManuelleLivreActivity.this, "Veuillez choisir une langue entre fr, en ou de.", Toast.LENGTH_LONG).show();
                    return;
                }
                //ivCoverSML
                String resume = etmlResumeSML.getText().toString();
                Log.i(TAG, "onClick: resume : " + resume);


                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                String id_user = firebaseUser.getUid();


                int nbPages = 0;
                if (!nbPagesStr.equals("")) {
                    nbPages = Integer.valueOf(nbPagesStr);
                }
                String idGoogleBooks = "";

                Date dateToday = new Date();
                Log.i(TAG, "onClick: " + dateToday.toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm");
                //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                String strDateToday = dateFormat.format(dateToday);
                String strHeureNow = heureFormat.format(dateToday);

                ModelDetailsLivre livre = new ModelDetailsLivre(titre, auteur, editeur, date, resume, uriPhoto, isbn, nbPages, langue, idGoogleBooks, strDateToday, strHeureNow, id_user);

                try {
                    verifieExistsBookInDB(livre);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onClick: erreur : e.getMessage() : " + e.getMessage());

                }

            }
        });
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
                    .addOnCompleteListener(SaisieManuelleLivreActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                fileRef.getDownloadUrl().addOnSuccessListener(SaisieManuelleLivreActivity.this,
                                        new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                serverFileUri = uri;
                                                Log.i(TAG, "onSuccess: On success" + uri.getPath());
                                                //serverFileUri = uri.getPath();
                                                ivCoverSML.setImageURI(localFileUri);
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
            Toast.makeText(this, "Erreur lors du chargement de l'image : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void verifieExistsBookInDB(ModelDetailsLivre livre) {
        existeDeja = false;

        if(!isbn.equals("")) {
            //  ous n'avons que l'ISBN : verifie si ISBN existe dans la BD
            db.collection(LIVRES_COLLECTION_BD)
                    .whereEqualTo(ID_USER_LIVRE_BD, id_user)
                    .whereEqualTo(ISBN_LIVRE_BD, isbn)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                //comme on filtre par id, on devrait avoir ici qu'un seul resultat
                                if (querySnapshot.size() != 0) {
                                    existeDeja = true;
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        // Le livre existe dans la BD
                                        Log.i(TAG, "onComplete: document.getId() :" + document.getId());

                                    }
                                    afterOnCompleteVerifieExistsBookInDB(existeDeja, livre);
                                } else {
                                    existeDeja = false;
                                    afterOnCompleteVerifieExistsBookInDB(existeDeja, livre);
                                }
                            } else {
                                Log.i(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            ajouterLivreBD(livre);
        }


    }

    private void afterOnCompleteVerifieExistsBookInDB(boolean existeDeja, ModelDetailsLivre livre) {
        if(existeDeja) {
            Toast.makeText(this, "Ce livre existe déjà dans la BD. Veuillez saisir un autre ISBN.", Toast.LENGTH_LONG).show();
        } else {
            // le livre n'existe pas encore dans la BD (selon l'ISBN)
            ajouterLivreBD(livre);
        }
    }

    private void ajouterLivreBD(ModelDetailsLivre livre) {
        livresRef.add(livre);
        Toast.makeText(SaisieManuelleLivreActivity.this, "Livre ajouté avec succès !", Toast.LENGTH_LONG).show();

        Intent mainIntent = new Intent(SaisieManuelleLivreActivity.this, MainActivity.class);
        mainIntent.putExtra(FRAG_TO_LOAD, MES_LIVRES_FRAGMENT);

        startActivity(mainIntent);
    }
}