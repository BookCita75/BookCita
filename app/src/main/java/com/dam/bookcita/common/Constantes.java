package com.dam.bookcita.common;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public interface Constantes {
    //************ CONSTANTES FIREBASE ****************/
    // Auth
    FirebaseAuth FIREBASE_AUTH = FirebaseAuth.getInstance();
    // User
    FirebaseUser CURRENT_USER = FIREBASE_AUTH.getCurrentUser();
    // Firestore
    @SuppressLint("StaticFieldLeak")
    FirebaseFirestore FIRESTORE_INSTANCE = FirebaseFirestore.getInstance();
    // Storage
    FirebaseStorage STORAGE_INSTANCE = FirebaseStorage.getInstance();


    //************ CONSTANTS POUR LE DOSSIER DE STORAGE ****************/
    // Lien vers le dossier de stockage des avatars
    String AVATARS_FOLDER = "avatars_user";


    //************ CONSTANTS DES COLLECTIONS ET DE LEURS CHAMPS ****************/
    //-------------- Collection Users
    String USERS = "Users";

    String NAME = "name";
    String EMAIL = "email";
    String ONLINE = "online";
    String AVATAR = "avatar";
    //-------------- end Users

    //-------------- Collection Friend request
    String FRIEND_REQUESTS = "FriendRequests";

    String REQUEST_TYPE = "request_type";
    String REQUEST_STATUS_SENT = "sent";
    String REQUEST_STATUS_RECEIVED = "received";
    //-------------- end Friend Request

    String ID_BD = "id_BD";
    String ID_BD_CITATION = "id_BD_citation";
    String FRAG_TO_LOAD = "frag_to_load";

    int ACCUEIL_FRAGMENT = 0;
    int MES_LIVRES_FRAGMENT = 1;
    int MES_CITATIONS_FRAGMENT = 2;
    int RECHERCHER_FRAGMENT = 3;

    String ISBN = "isbn";
    String ID_GOOGLE_BOOKS = "id_google_books";
    String TYPE_ISBN_OR_OCR = "type_ISBN_or_OCR";
    String RESULT_TEXT_OCR = "result_text_OCR";

    String TYPE_SAISIE_MANUELLE_OR_OCR = "type_saisie_manuelle_or_OCR";
    String SAISIE_OCR = "saisie_OCR";
    String SAISIE_MANUELLE = "saisie_manuelle";
    String TEXTE_EXTRAIT = "texte_extrait";

    /** BD **/
    String LIVRES_COLLECTION_BD = "livres";
    String CITATIONS_COLLECTION_BD = "citations";

    /* Collection livres */
    String TITRE_LIVRE_BD = "title_livre";
    String AUTEUR_LIVRE_BD = "auteur_livre";
    String URL_COVER_LIVRE_BD = "url_cover_livre";
    String EDITEUR_LIVRE_BD = "editeur_livre";
    String DATE_PARUTION_LIVRE_BD = "date_parution_livre";
    String RESUME_LIVRE_BD = "resume_livre";
    String ISBN_LIVRE_BD = "isbn_livre";
    String NB_PAGES_LIVRE_BD = "nbPages_livre";
    String LANGUE_LIVRE_BD = "langue";
    String ID_USER_LIVRE_BD = "id_user";
    String ID_GOOGLE_BOOKS_LIVRE_BD = "idGoogleBooks";
    String ETIQUETTE_LIVRE_BD = "etiquette";

    /* Values */

    String VALUE_ETIQUETTE_AUCUNE = "aucune";
    String VALUE_ETIQUETTE_EN_COURS = "en cours";
    String VALUE_ETIQUETTE_LU = "lu";
    String VALUE_ETIQUETTE_A_LIRE = "a lire";
    String VALUE_ETIQUETTE_2EME_TEMPS = "2eme temps";

    String VALUE_ETIQUETTE_ML_TOUS = "tous";
//    String VALUE_ETIQUETTE_ML_EN_COURS = "en cours";
//    String VALUE_ETIQUETTE_ML_LUS = "lus";
//    String VALUE_ETIQUETTE_ML_A_LIRE = "a lire";
//    String VALUE_ETIQUETTE_ML_2EME_TEMPS = "2eme temps";


    /* Collection citations */
    String ID_USER_CITATION_BD = "id_user";
    String ID_BD_LIVRE_CITATION_BD = "id_BD_livre";
    String DATE_CITATION_BD = "date";
    String HEURE_CITATION_BD = "heure";
    String NUMERO_PAGE_CITATION_BD = "numeroPage";
    String CITATION_CITATION_BD = "citation";
    String ANNOTATION_CITATION_BD = "annotation";
    String _FIRESTORE_ID_CITATION_BD = "_firestore_id";




    /* Meilisearch */
    String INDEX_CITATIONS_MEILISEARCH = "citations";






}
