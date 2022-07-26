package com.dam.bookcita.model;

import com.google.firebase.firestore.Exclude;

public class ModelBook {

    private String coverUrl;
    private String titre;
    private String auteur;
    private String isbn;
    private String idGoogleBooks;

    public ModelBook() {
    }

    public ModelBook(String coverUrl, String titre, String auteur, String isbn, String idGoogleBooks) {
        this.coverUrl = coverUrl;
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.idGoogleBooks = idGoogleBooks;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIdGoogleBooks() {
        return idGoogleBooks;
    }

    public void setIdGoogleBooks(String idGoogleBooks) {
        this.idGoogleBooks = idGoogleBooks;
    }
}
