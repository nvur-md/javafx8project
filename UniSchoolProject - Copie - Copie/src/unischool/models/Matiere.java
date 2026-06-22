package unischool.models;

public class Matiere {
    private int id;
    private String code;
    private String nom;
    private String description;
    private int credits;
    private int semestre;
    private String filiere;
    private Integer enseignantResponsableId;
    private int volumeHoraire;
    private boolean actif;

    // Informations associées
    private String nomEnseignantResponsable;

    public Matiere() {}

    public Matiere(String code, String nom, String description, int credits,
                   int semestre, String filiere, int volumeHoraire) {
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.credits = credits;
        this.semestre = semestre;
        this.filiere = filiere;
        this.volumeHoraire = volumeHoraire;
        this.actif = true;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public int getSemestre() { return semestre; }
    public void setSemestre(int semestre) { this.semestre = semestre; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public Integer getEnseignantResponsableId() { return enseignantResponsableId; }
    public void setEnseignantResponsableId(Integer enseignantResponsableId) { this.enseignantResponsableId = enseignantResponsableId; }
    public int getVolumeHoraire() { return volumeHoraire; }
    public void setVolumeHoraire(int volumeHoraire) { this.volumeHoraire = volumeHoraire; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public String getNomEnseignantResponsable() { return nomEnseignantResponsable; }
    public void setNomEnseignantResponsable(String nomEnseignantResponsable) { this.nomEnseignantResponsable = nomEnseignantResponsable; }

    public String getNomComplet() { return code + " - " + nom; }

    @Override
    public String toString() { return getNomComplet(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Matiere matiere = (Matiere) obj;
        return id == matiere.id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }
}