package unischool.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Enseignant {
    private int id;
    private int utilisateurId;
    private String specialite;
    private LocalDate dateEmbauche;
    private String grade;
    private String telephone;
    private String bureau;
    private String statut; // ACTIF, CONGE, DEPART

    // Informations de l'utilisateur associé
    private String nom;
    private String prenom;
    private String email;
    private boolean actif;

    private transient List<Matiere> matieres;

    public Enseignant() {
        this.matieres = new ArrayList<>();
    }

    public Enseignant(int utilisateurId, String nom, String prenom, String email,
                      String specialite, LocalDate dateEmbauche, String grade,
                      String telephone, String bureau, String statut) {
        this();
        this.utilisateurId = utilisateurId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.specialite = specialite;
        this.dateEmbauche = dateEmbauche;
        this.grade = grade;
        this.telephone = telephone;
        this.bureau = bureau;
        this.statut = statut != null ? statut : "ACTIF";
        this.actif = true;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getBureau() { return bureau; }
    public void setBureau(String bureau) { this.bureau = bureau; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public List<Matiere> getMatieres() { return matieres; }
    public void setMatieres(List<Matiere> matieres) { this.matieres = matieres; }

    public String getNomComplet() { return prenom + " " + nom; }

    @Override
    public String toString() { return getNomComplet() + " (" + specialite + ")"; }
}