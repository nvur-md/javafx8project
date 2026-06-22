package unischool.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Etudiant {
    private int id;
    private int utilisateurId;
    private LocalDate dateNaissance;
    private String telephone;
    private String adresse;
    private String filiere;
    private String niveau;
    private int anneeEtude;
    private String matricule;
    private String groupe;
    private LocalDate dateInscription;
    private String statut; // ACTIF, INACTIF, EXCLU, DIPLOME

    // Informations de l'utilisateur associé
    private String nom;
    private String prenom;
    private String email;
    private boolean actif;

    private transient List<Note> notes;

    public Etudiant() {
        this.notes = new ArrayList<>();
    }

    public Etudiant(int utilisateurId, String nom, String prenom, String email,
                    LocalDate dateNaissance, String telephone, String filiere,
                    String niveau, int anneeEtude, String matricule, String groupe) {
        this();
        this.utilisateurId = utilisateurId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.filiere = filiere != null ? filiere : "Génie Informatique";
        this.niveau = niveau != null ? niveau : "Licence";
        this.anneeEtude = anneeEtude;
        this.matricule = matricule;
        this.groupe = groupe;
        this.dateInscription = LocalDate.now();
        this.statut = "ACTIF";
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
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public int getAnneeEtude() { return anneeEtude; }
    public void setAnneeEtude(int anneeEtude) { this.anneeEtude = anneeEtude; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getGroupe() { return groupe; }
    public void setGroupe(String groupe) { this.groupe = groupe; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }

    public String getNomComplet() { return prenom + " " + nom; }

    public double calculerMoyenne() {
        if (notes == null || notes.isEmpty()) return 0.0;
        double totalPoints = 0.0, totalCoeff = 0.0;
        for (Note note : notes) {
            if (note.isValidee()) {
                totalPoints += note.getValeur() * note.getCoefficient();
                totalCoeff += note.getCoefficient();
            }
        }
        return totalCoeff > 0 ? totalPoints / totalCoeff : 0.0;
    }

    public String getMention() {
        double moyenne = calculerMoyenne();
        if (moyenne >= 16) return "Très Bien";
        else if (moyenne >= 14) return "Bien";
        else if (moyenne >= 12) return "Assez Bien";
        else if (moyenne >= 10) return "Passable";
        else return "Insuffisant";
    }

    @Override
    public String toString() { return getNomComplet() + " (" + matricule + ")"; }
}