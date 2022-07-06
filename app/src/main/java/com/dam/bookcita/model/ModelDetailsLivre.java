package com.dam.bookcita.model;

import com.google.firebase.firestore.Exclude;

public class ModelDetailsLivre {
    private String id;
    private String title_livre;
    private String auteur_livre;
    private String editeur_livre;
    private String date_parution_livre;
    private String resume_livre;
    private String url_cover_livre;
    private String isbn_livre;
    private String id_user;
    private int nombre_livres;



    public ModelDetailsLivre(String title_livre, String auteur_livre, String editeur_livre, String date_parution_livre, String resume_livre, String url_cover_livre, String isbn_livre) {
        this.title_livre = title_livre;
        this.auteur_livre = auteur_livre;
        this.editeur_livre = editeur_livre;
        this.date_parution_livre = date_parution_livre;
        this.resume_livre = resume_livre;
        this.url_cover_livre = url_cover_livre;
        this.isbn_livre = isbn_livre;

    }

    public ModelDetailsLivre(String id, String title_livre, String auteur_livre, String url_cover_livre, String isbn_livre) {
        this.id = id;
        this.title_livre = title_livre;
        this.auteur_livre = auteur_livre;
        this.url_cover_livre = url_cover_livre;
        this.isbn_livre = isbn_livre;
    }

    public ModelDetailsLivre() {
    }

    public ModelDetailsLivre(String id, String title_livre, String auteur_livre, String editeur_livre, String date_parution_livre, String resume_livre, String url_cover_livre, String isbn_livre, int nombre_livres) {
        this.title_livre = title_livre;
        this.auteur_livre = auteur_livre;
        this.editeur_livre = editeur_livre;
        this.date_parution_livre = date_parution_livre;
        this.resume_livre = resume_livre;
        this.url_cover_livre = url_cover_livre;
        this.isbn_livre = isbn_livre;
        this.nombre_livres = nombre_livres;
        this.id= id;
    }

    public ModelDetailsLivre(String title_livre, String auteur_livre, String editeur_livre, String date_parution_livre, String resume_livre, String url_cover_livre, String isbn_livre, int nombre_livres, String id_user) {
        this.title_livre = title_livre;
        this.auteur_livre = auteur_livre;
        this.editeur_livre = editeur_livre;
        this.date_parution_livre = date_parution_livre;
        this.resume_livre = resume_livre;
        this.url_cover_livre = url_cover_livre;
        this.isbn_livre = isbn_livre;
        this.nombre_livres = nombre_livres;
        this.id_user = id_user;
    }

    public ModelDetailsLivre(String title_livre, String auteur_livre, String editeur_livre, String date_parution_livre, String resume_livre, String url_cover_livre, String isbn_livre, int nombre_livres) {
        this.title_livre = title_livre;
        this.auteur_livre = auteur_livre;
        this.editeur_livre = editeur_livre;
        this.date_parution_livre = date_parution_livre;
        this.resume_livre = resume_livre;
        this.url_cover_livre = url_cover_livre;
        this.isbn_livre = isbn_livre;
        this.nombre_livres = nombre_livres;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle_livre() {
        return title_livre;
    }

    public void setTitle_livre(String title_livre) {
        this.title_livre = title_livre;
    }

    public String getAuteur_livre() {
        return auteur_livre;
    }

    public void setAuteur_livre(String auteur_livre) {
        this.auteur_livre = auteur_livre;
    }

    public String getEditeur_livre() {
        return editeur_livre;
    }

    public void setEditeur_livre(String editeur_livre) {
        this.editeur_livre = editeur_livre;
    }

    public String getDate_parution_livre() {
        return date_parution_livre;
    }

    public void setDate_parution_livre(String date_parution_livre) {
        this.date_parution_livre = date_parution_livre;
    }

    public String getResume_livre() {
        return resume_livre;
    }

    public void setResume_livre(String resume_livre) {
        this.resume_livre = resume_livre;
    }

    public String getUrl_cover_livre() {
        return url_cover_livre;
    }

    public void setUrl_cover_livre(String url_cover_livre) {
        this.url_cover_livre = url_cover_livre;
    }

    public String getIsbn_livre() {
        return isbn_livre;
    }

    public void setIsbn_livre(String isbn_livre) {
        this.isbn_livre = isbn_livre;
    }

    public int getNombre_livres() {
        return nombre_livres;
    }

    public void setNombre_livres(int nombre_livres) {
        this.nombre_livres = nombre_livres;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
}
