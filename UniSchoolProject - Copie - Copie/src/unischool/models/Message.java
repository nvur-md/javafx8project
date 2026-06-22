package unischool.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private int id;
    private int expediteurId;
    private String expediteurRole;
    private Integer destinataireId;
    private String destinataireType;
    private String objet;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean lu;
    private LocalDateTime dateLecture;
    private String filiere;
    private Integer annee;

    // Informations associées
    private String nomExpediteur;
    private String nomDestinataire;

    public Message() {}

    public Message(int expediteurId, String expediteurRole, String destinataireType,
                   String objet, String contenu) {
        this.expediteurId = expediteurId;
        this.expediteurRole = expediteurRole;
        this.destinataireType = destinataireType;
        this.objet = objet;
        this.contenu = contenu;
        this.dateEnvoi = LocalDateTime.now();
        this.lu = false;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getExpediteurId() { return expediteurId; }
    public void setExpediteurId(int expediteurId) { this.expediteurId = expediteurId; }
    public String getExpediteurRole() { return expediteurRole; }
    public void setExpediteurRole(String expediteurRole) { this.expediteurRole = expediteurRole; }
    public Integer getDestinataireId() { return destinataireId; }
    public void setDestinataireId(Integer destinataireId) { this.destinataireId = destinataireId; }
    public String getDestinataireType() { return destinataireType; }
    public void setDestinataireType(String destinataireType) { this.destinataireType = destinataireType; }
    public String getObjet() { return objet; }
    public void setObjet(String objet) { this.objet = objet; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }
    public LocalDateTime getDateLecture() { return dateLecture; }
    public void setDateLecture(LocalDateTime dateLecture) { this.dateLecture = dateLecture; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }
    public String getNomExpediteur() { return nomExpediteur; }
    public void setNomExpediteur(String nomExpediteur) { this.nomExpediteur = nomExpediteur; }
    public String getNomDestinataire() { return nomDestinataire; }
    public void setNomDestinataire(String nomDestinataire) { this.nomDestinataire = nomDestinataire; }

    public String getDateFormatee() {
        return dateEnvoi != null ?
                dateEnvoi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public String getResume() {
        return objet.length() > 30 ? objet.substring(0, 27) + "..." : objet;
    }

    @Override
    public String toString() {
        return "📧 " + getDateFormatee() + " - " + objet;
    }
}