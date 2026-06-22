package unischool.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Absence {
    private int id;
    private int etudiantId;
    private LocalDate dateAbsence;
    private LocalDate dateRetour;
    private String motif;
    private boolean justifiee;
    private Integer enseignantId;
    private LocalDate dateSaisie;

    // Informations associées
    private String nomEtudiant;
    private String prenomEtudiant;
    private String nomEnseignant;

    public Absence() {}

    public Absence(int etudiantId, LocalDate dateAbsence, String motif, boolean justifiee) {
        this.etudiantId = etudiantId;
        this.dateAbsence = dateAbsence;
        this.motif = motif;
        this.justifiee = justifiee;
        this.dateSaisie = LocalDate.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }
    public LocalDate getDateAbsence() { return dateAbsence; }
    public void setDateAbsence(LocalDate dateAbsence) { this.dateAbsence = dateAbsence; }
    public LocalDate getDateRetour() { return dateRetour; }
    public void setDateRetour(LocalDate dateRetour) { this.dateRetour = dateRetour; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public boolean isJustifiee() { return justifiee; }
    public void setJustifiee(boolean justifiee) { this.justifiee = justifiee; }
    public Integer getEnseignantId() { return enseignantId; }
    public void setEnseignantId(Integer enseignantId) { this.enseignantId = enseignantId; }
    public LocalDate getDateSaisie() { return dateSaisie; }
    public void setDateSaisie(LocalDate dateSaisie) { this.dateSaisie = dateSaisie; }
    public String getNomEtudiant() { return nomEtudiant; }
    public void setNomEtudiant(String nomEtudiant) { this.nomEtudiant = nomEtudiant; }
    public String getPrenomEtudiant() { return prenomEtudiant; }
    public void setPrenomEtudiant(String prenomEtudiant) { this.prenomEtudiant = prenomEtudiant; }
    public String getNomEnseignant() { return nomEnseignant; }
    public void setNomEnseignant(String nomEnseignant) { this.nomEnseignant = nomEnseignant; }

    public String getNomEtudiantComplet() {
        return (prenomEtudiant != null ? prenomEtudiant : "") +
                " " + (nomEtudiant != null ? nomEtudiant : "");
    }

    public String getDateFormatee() {
        return dateAbsence != null ?
                dateAbsence.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public String getStatut() { return justifiee ? "✅ Justifiée" : "❌ Non justifiée"; }

    @Override
    public String toString() { return getDateFormatee() + " - " + getStatut() + " : " + motif; }
}