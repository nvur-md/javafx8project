package unischool.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Note {
    private int id;
    private int etudiantId;
    private int matiereId;
    private Integer enseignantId;
    private double valeur;
    private LocalDate dateEvaluation;
    private double coefficient;
    private String appreciation;
    private String type;
    private int semestre;
    private LocalDate dateSaisie;
    private LocalDate dateModification;
    private boolean validee;

    // Informations associées (pour affichage)
    private String nomEtudiant;
    private String prenomEtudiant;
    private String nomMatiere;
    private String codeMatiere;
    private String nomEnseignant;

    public Note() {}

    public Note(int etudiantId, int matiereId, Integer enseignantId,
                double valeur, LocalDate dateEvaluation, double coefficient,
                String appreciation, String type, int semestre) {
        this.etudiantId = etudiantId;
        this.matiereId = matiereId;
        this.enseignantId = enseignantId;
        this.valeur = Math.max(0, Math.min(20, valeur));
        this.dateEvaluation = dateEvaluation;
        this.coefficient = coefficient;
        this.appreciation = appreciation;
        this.type = type;
        this.semestre = semestre;
        this.dateSaisie = LocalDate.now();
        this.validee = false;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }
    public int getMatiereId() { return matiereId; }
    public void setMatiereId(int matiereId) { this.matiereId = matiereId; }
    public Integer getEnseignantId() { return enseignantId; }
    public void setEnseignantId(Integer enseignantId) { this.enseignantId = enseignantId; }
    public double getValeur() { return valeur; }
    public void setValeur(double valeur) { this.valeur = Math.max(0, Math.min(20, valeur)); }
    public LocalDate getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDate dateEvaluation) { this.dateEvaluation = dateEvaluation; }
    public double getCoefficient() { return coefficient; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
    public String getAppreciation() { return appreciation; }
    public void setAppreciation(String appreciation) { this.appreciation = appreciation; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }
    public LocalDate getDateSaisie() { return dateSaisie; }
    public void setDateSaisie(LocalDate dateSaisie) { this.dateSaisie = dateSaisie; }
    public LocalDate getDateModification() { return dateModification; }
    public void setDateModification(LocalDate dateModification) { this.dateModification = dateModification; }
    public boolean isValidee() { return validee; }
    public void setValidee(boolean validee) { this.validee = validee; }

    // Informations associées
    public String getNomEtudiant() { return nomEtudiant; }
    public void setNomEtudiant(String nomEtudiant) { this.nomEtudiant = nomEtudiant; }
    public String getPrenomEtudiant() { return prenomEtudiant; }
    public void setPrenomEtudiant(String prenomEtudiant) { this.prenomEtudiant = prenomEtudiant; }
    public String getNomMatiere() { return nomMatiere; }
    public void setNomMatiere(String nomMatiere) { this.nomMatiere = nomMatiere; }
    public String getCodeMatiere() { return codeMatiere; }
    public void setCodeMatiere(String codeMatiere) { this.codeMatiere = codeMatiere; }
    public String getNomEnseignant() { return nomEnseignant; }
    public void setNomEnseignant(String nomEnseignant) { this.nomEnseignant = nomEnseignant; }

    public String getNomEtudiantComplet() {
        return (prenomEtudiant != null ? prenomEtudiant : "") +
                " " + (nomEtudiant != null ? nomEtudiant : "");
    }

    public String getDateFormatee() {
        return dateEvaluation != null ?
                dateEvaluation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public double getPoints() { return valeur * coefficient; }
    public boolean estValide() { return valeur >= 0 && valeur <= 20 && coefficient > 0; }

    public String getAppreciationText() {
        if (valeur >= 16) return "Excellent";
        else if (valeur >= 14) return "Très bien";
        else if (valeur >= 12) return "Bien";
        else if (valeur >= 10) return "Passable";
        else return "Insuffisant";
    }

    @Override
    public String toString() { return "Note: " + valeur + "/20 en " + nomMatiere; }
}