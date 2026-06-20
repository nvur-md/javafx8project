package unischool.dao;

import unischool.models.EmploiDuTemps;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EmploiDuTempsDAO {

    public void create(EmploiDuTemps edt) throws SQLException {
        String sql = "INSERT INTO emploi_du_temps (matiere_id, enseignant_id, filiere, annee, jour, heure_debut, heure_fin, salle, type_cours, semestre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, edt.getMatiereId());
            stmt.setInt(2, edt.getEnseignantId());
            stmt.setString(3, edt.getFiliere());
            stmt.setInt(4, edt.getAnnee());
            stmt.setString(5, edt.getJour());
            stmt.setTime(6, Time.valueOf(edt.getHeureDebut()));
            stmt.setTime(7, Time.valueOf(edt.getHeureFin()));
            stmt.setString(8, edt.getSalle());
            stmt.setString(9, edt.getTypeCours());
            stmt.setInt(10, edt.getSemestre());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    edt.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<EmploiDuTemps> getEmploiDuTemps(String filiere, int annee, int semestre) throws SQLException {
        List<EmploiDuTemps> edtList = new ArrayList<>();
        String sql = "SELECT e.*, m.nom AS matiere_nom, m.code AS matiere_code, u.nom AS enseignant_nom, u.prenom AS enseignant_prenom FROM emploi_du_temps e JOIN matiere m ON e.matiere_id = m.id JOIN enseignant ens ON e.enseignant_id = ens.id JOIN utilisateur u ON ens.utilisateur_id = u.id WHERE e.filiere = ? AND e.annee = ? AND e.semestre = ? ORDER BY FIELD(e.jour, 'LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI'), e.heure_debut";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filiere);
            stmt.setInt(2, annee);
            stmt.setInt(3, semestre);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    edtList.add(mapResultSetToEmploiDuTemps(rs));
                }
            }
        }
        return edtList;
    }

    public List<EmploiDuTemps> getEmploiDuTempsByJour(String filiere, int annee, String jour) throws SQLException {
        List<EmploiDuTemps> edtList = new ArrayList<>();
        String sql = "SELECT e.*, m.nom AS matiere_nom, m.code AS matiere_code, u.nom AS enseignant_nom, u.prenom AS enseignant_prenom FROM emploi_du_temps e JOIN matiere m ON e.matiere_id = m.id JOIN enseignant ens ON e.enseignant_id = ens.id JOIN utilisateur u ON ens.utilisateur_id = u.id WHERE e.filiere = ? AND e.annee = ? AND e.jour = ? ORDER BY e.heure_debut";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filiere);
            stmt.setInt(2, annee);
            stmt.setString(3, jour);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    edtList.add(mapResultSetToEmploiDuTemps(rs));
                }
            }
        }
        return edtList;
    }

    public List<EmploiDuTemps> getEmploiDuTempsByEnseignant(int enseignantId, int semestre) throws SQLException {
        List<EmploiDuTemps> edtList = new ArrayList<>();
        String sql = "SELECT e.*, m.nom AS matiere_nom, m.code AS matiere_code, u.nom AS enseignant_nom, u.prenom AS enseignant_prenom FROM emploi_du_temps e JOIN matiere m ON e.matiere_id = m.id JOIN enseignant ens ON e.enseignant_id = ens.id JOIN utilisateur u ON ens.utilisateur_id = u.id WHERE e.enseignant_id = ? AND e.semestre = ? ORDER BY FIELD(e.jour, 'LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI'), e.heure_debut";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enseignantId);
            stmt.setInt(2, semestre);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    edtList.add(mapResultSetToEmploiDuTemps(rs));
                }
            }
        }
        return edtList;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM emploi_du_temps WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void deleteByFiliereAndAnnee(String filiere, int annee, int semestre) throws SQLException {
        String sql = "DELETE FROM emploi_du_temps WHERE filiere = ? AND annee = ? AND semestre = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filiere);
            stmt.setInt(2, annee);
            stmt.setInt(3, semestre);
            stmt.executeUpdate();
        }
    }

    private EmploiDuTemps mapResultSetToEmploiDuTemps(ResultSet rs) throws SQLException {
        EmploiDuTemps e = new EmploiDuTemps();
        e.setId(rs.getInt("id"));
        e.setMatiereId(rs.getInt("matiere_id"));
        e.setEnseignantId(rs.getInt("enseignant_id"));
        e.setFiliere(rs.getString("filiere"));
        e.setAnnee(rs.getInt("annee"));
        e.setJour(rs.getString("jour"));
        e.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
        e.setHeureFin(rs.getTime("heure_fin").toLocalTime());
        e.setSalle(rs.getString("salle"));
        e.setTypeCours(rs.getString("type_cours"));
        e.setSemestre(rs.getInt("semestre"));

        // Informations associées
        e.setNomMatiere(rs.getString("matiere_nom"));
        e.setCodeMatiere(rs.getString("matiere_code"));
        e.setNomEnseignant(rs.getString("enseignant_nom"));
        e.setPrenomEnseignant(rs.getString("enseignant_prenom"));

        return e;
    }
}