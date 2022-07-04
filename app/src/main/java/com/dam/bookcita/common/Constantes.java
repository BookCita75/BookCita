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
    String FRAG_TO_LOAD = "frag_to_load";

    int ACCUEIL_FRAGMENT = 0;
    int MES_LIVRES_FRAGMENT = 1;

    String ISBN = "isbn";
    String ID = "id";
    String TYPE_ISBN_OR_OCR = "type_ISBN_or_OCR";
}
