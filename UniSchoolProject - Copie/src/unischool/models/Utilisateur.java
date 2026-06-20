package unischool.models;

import java.time.LocalDateTime;

public class Utilisateur {
    private int id;
    private String email;
    private String motDePasse;
    private String nom;
    private String prenom;
    private String role; // ADMIN, ENSEIGNANT, ETUDIANT
    private boolean actif;
    private LocalDateTime derniereConnexion;
    private LocalDateTime dateCreation;

    public Utilisateur() {}

    public Utilisateur(String email, String motDePasse, String nom, String prenom, String role) {
        this.email = email;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public LocalDateTime getDerniereConnexion() { return derniereConnexion; }
    public void setDerniereConnexion(LocalDateTime derniereConnexion) { this.derniereConnexion = derniereConnexion; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getNomComplet() { return nom + " " + prenom; }
    public boolean isAdmin() { return "ADMIN".equals(role); }
    public boolean isEnseignant() { return "ENSEIGNANT".equals(role); }
    public boolean isEtudiant() { return "ETUDIANT".equals(role); }

    @Override
    public String toString() { return getNomComplet() + " (" + role + ")"; }
}