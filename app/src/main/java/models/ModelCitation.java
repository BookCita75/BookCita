package models;

import com.google.firebase.firestore.Exclude;

public class ModelCitation {
    private String id;
    private String id_BD_livre;
    private String citation;
    private String annotation;
    private int numeroPage;
    private String date;
    private String heure;

    public ModelCitation() {
    }

    public ModelCitation(String id_BD_livre, String citation, String annotation, int numeroPage, String date, String heure) {
        this.id_BD_livre = id_BD_livre;
        this.citation = citation;
        this.annotation = annotation;
        this.numeroPage = numeroPage;
        this.date = date;
        this.heure = heure;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_BD_livre() {
        return id_BD_livre;
    }

    public void setId_BD_livre(String id_BD_livre) {
        this.id_BD_livre = id_BD_livre;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public int getNumeroPage() {
        return numeroPage;
    }

    public void setNumeroPage(int numeroPage) {
        this.numeroPage = numeroPage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }
}
