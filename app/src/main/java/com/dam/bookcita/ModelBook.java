package com.dam.bookcita;

public class ModelBook {

    private String coverUrl;
    private String titre;
    private String auteur;
    private String isbn;

    public ModelBook() {
    }

    public ModelBook(String coverUrl, String titre, String auteur, String isbn) {
        this.coverUrl = coverUrl;
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
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
}
