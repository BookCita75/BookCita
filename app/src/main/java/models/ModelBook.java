package models;

import com.google.firebase.firestore.Exclude;

public class ModelBook {

    private String coverUrl;
    private String titre;
    private String auteur;
    private String isbn;
    private String idLivre;

    public ModelBook() {
    }

    public ModelBook(String coverUrl, String titre, String auteur, String isbn, String idLivre) {
        this.coverUrl = coverUrl;
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.idLivre = idLivre;
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

    @Exclude
    public String getId() {
        return idLivre;
    }

    public void setId(String idLivre) {
        this.idLivre = idLivre;
    }
}
