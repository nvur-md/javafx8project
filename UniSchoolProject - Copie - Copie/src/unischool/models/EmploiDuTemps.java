package unischool.models;

import java.time.LocalTime;

public class EmploiDuTemps {
    private int id;
    private int matiereId;
    private int enseignantId;
    private String filiere;
    private int annee;
    private String jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;
    private String typeCours;
    private int semestre;

    // Informations associées
    private String nomMatiere;
    private String codeMatiere;
    private String nomEnseignant;
    private String prenomEnseignant;

    public EmploiDuTemps() {}

    public EmploiDuTemps(int matiereId, int enseignantId, String filiere, int annee,
                         String jour, LocalTime heureDebut, LocalTime heureFin,
                         String salle, String typeCours, int semestre) {
        this.matiereId = matiereId;
        this.enseignantId = enseignantId;
        this.filiere = filiere;
        this.annee = annee;
        this.jour = jour;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.salle = salle;
        this.typeCours = typeCours;
        this.semestre = semestre;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMatiereId() { return matiereId; }
    public void setMatiereId(int matiereId) { this.matiereId = matiereId; }
    public int getEnseignantId() { return enseignantId; }
    public void setEnseignantId(int enseignantId) { this.enseignantId = enseignantId; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }
    public String getTypeCours() { return typeCours; }
    public void setTypeCours(String typeCours) { this.typeCours = typeCours; }
    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }
    public String getNomMatiere() { return nomMatiere; }
    public void setNomMatiere(String nomMatiere) { this.nomMatiere = nomMatiere; }
    public String getCodeMatiere() { return codeMatiere; }
    public void setCodeMatiere(String codeMatiere) { this.codeMatiere = codeMatiere; }
    public String getNomEnseignant() { return nomEnseignant; }
    public void setNomEnseignant(String nomEnseignant) { this.nomEnseignant = nomEnseignant; }
    public String getPrenomEnseignant() { return prenomEnseignant; }
    public void setPrenomEnseignant(String prenomEnseignant) { this.prenomEnseignant = prenomEnseignant; }

    public String getHeureFormatee() {
        return heureDebut + " - " + heureFin;
    }

    public String getNomEnseignantComplet() {
        return (prenomEnseignant != null ? prenomEnseignant : "") +
                " " + (nomEnseignant != null ? nomEnseignant : "");
    }

    @Override
    public String toString() {
        return jour + " " + getHeureFormatee() + " - " + nomMatiere + " (" + typeCours + ")";
    }
}