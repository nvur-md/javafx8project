package unischool.dao;

import unischool.models.Enseignant;
import unischool.models.Matiere;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnseignantDAO {

    public void create(Enseignant enseignant) throws SQLException {
        String sql = "INSERT INTO enseignant (utilisateur_id, specialite, date_embauche, grade, telephone, bureau, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, enseignant.getUtilisateurId());
            stmt.setString(2, enseignant.getSpecialite());
            stmt.setDate(3, enseignant.getDateEmbauche() != null ? Date.valueOf(enseignant.getDateEmbauche()) : null);
            stmt.setString(4, enseignant.getGrade());
            stmt.setString(5, enseignant.getTelephone());
            stmt.setString(6, enseignant.getBureau());
            stmt.setString(7, enseignant.getStatut() != null ? enseignant.getStatut() : "ACTIF");

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    enseignant.setId(rs.getInt(1));
                }
            }
        }
    }

    public Enseignant read(int id) throws SQLException {
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM enseignant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE e.id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Enseignant enseignant = mapResultSetToEnseignant(rs);
                    enseignant.setMatieres(getMatieresByEnseignant(id));
                    return enseignant;
                }
            }
        }
        return null;
    }

    public Enseignant readByUtilisateurId(int utilisateurId) throws SQLException {
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM enseignant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE e.utilisateur_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, utilisateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Enseignant enseignant = mapResultSetToEnseignant(rs);
                    enseignant.setMatieres(getMatieresByEnseignant(enseignant.getId()));
                    return enseignant;
                }
            }
        }
        return null;
    }

    public List<Enseignant> readAll() throws SQLException {
        List<Enseignant> enseignants = new ArrayList<>();
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM enseignant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE u.role = 'ENSEIGNANT' ORDER BY u.nom, u.prenom";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Enseignant enseignant = mapResultSetToEnseignant(rs);
                // ✅ NE PAS appeler getMatieresByEnseignant() ici !
                // Les matières seront chargées séparément si nécessaire
                enseignants.add(enseignant);
            }
        }
        return enseignants;
    }

    public void update(Enseignant enseignant) throws SQLException {
        String sql = "UPDATE enseignant SET specialite=?, date_embauche=?, grade=?, telephone=?, bureau=?, statut=? WHERE id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, enseignant.getSpecialite());
            stmt.setDate(2, enseignant.getDateEmbauche() != null ? Date.valueOf(enseignant.getDateEmbauche()) : null);
            stmt.setString(3, enseignant.getGrade());
            stmt.setString(4, enseignant.getTelephone());
            stmt.setString(5, enseignant.getBureau());
            stmt.setString(6, enseignant.getStatut());
            stmt.setInt(7, enseignant.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM enseignant WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Matiere> getMatieresByEnseignant(int enseignantId) throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT m.* FROM matiere m JOIN enseignant_matiere em ON m.id = em.matiere_id WHERE em.enseignant_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enseignantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matieres.add(mapResultSetToMatiere(rs));
                }
            }
        }
        return matieres;
    }

    public void assignerMatiere(int enseignantId, int matiereId, String anneeAcademique) throws SQLException {
        String sql = "INSERT INTO enseignant_matiere (enseignant_id, matiere_id, annee_academique) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enseignantId);
            stmt.setInt(2, matiereId);
            stmt.setString(3, anneeAcademique);
            stmt.executeUpdate();
        }
    }

    public void retirerMatiere(int enseignantId, int matiereId, String anneeAcademique) throws SQLException {
        String sql = "DELETE FROM enseignant_matiere WHERE enseignant_id = ? AND matiere_id = ? AND annee_academique = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enseignantId);
            stmt.setInt(2, matiereId);
            stmt.setString(3, anneeAcademique);
            stmt.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM enseignant";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Enseignant mapResultSetToEnseignant(ResultSet rs) throws SQLException {
        Enseignant e = new Enseignant();
        e.setId(rs.getInt("id"));
        e.setUtilisateurId(rs.getInt("utilisateur_id"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setEmail(rs.getString("email"));
        e.setActif(rs.getBoolean("actif"));
        e.setSpecialite(rs.getString("specialite"));

        Date dateEmbauche = rs.getDate("date_embauche");
        if (dateEmbauche != null) {
            e.setDateEmbauche(dateEmbauche.toLocalDate());
        }

        e.setGrade(rs.getString("grade"));
        e.setTelephone(rs.getString("telephone"));
        e.setBureau(rs.getString("bureau"));
        e.setStatut(rs.getString("statut"));

        return e;
    }

    private Matiere mapResultSetToMatiere(ResultSet rs) throws SQLException {
        Matiere m = new Matiere();
        m.setId(rs.getInt("id"));
        m.setCode(rs.getString("code"));
        m.setNom(rs.getString("nom"));
        m.setDescription(rs.getString("description"));
        m.setCredits(rs.getInt("credits"));
        m.setSemestre(rs.getInt("semestre"));
        m.setFiliere(rs.getString("filiere"));
        m.setVolumeHoraire(rs.getInt("volume_horaire"));
        m.setActif(rs.getBoolean("actif"));

        int responsableId = rs.getInt("enseignant_responsable_id");
        if (!rs.wasNull()) {
            m.setEnseignantResponsableId(responsableId);
        }

        return m;
    }
}