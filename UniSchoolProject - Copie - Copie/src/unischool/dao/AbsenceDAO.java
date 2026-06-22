package unischool.dao;

import unischool.models.Absence;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbsenceDAO {

    public void create(Absence absence) throws SQLException {
        String sql = "INSERT INTO absence (etudiant_id, date_absence, date_retour, motif, justifiee, enseignant_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, absence.getEtudiantId());
            stmt.setDate(2, Date.valueOf(absence.getDateAbsence()));

            if (absence.getDateRetour() != null) {
                stmt.setDate(3, Date.valueOf(absence.getDateRetour()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, absence.getMotif());
            stmt.setBoolean(5, absence.isJustifiee());

            if (absence.getEnseignantId() != null) {
                stmt.setInt(6, absence.getEnseignantId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    absence.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Absence> readAll() throws SQLException {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT a.*, u.nom, u.prenom FROM absence a JOIN etudiant e ON a.etudiant_id = e.id JOIN utilisateur u ON e.utilisateur_id = u.id ORDER BY a.date_absence DESC";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                absences.add(mapResultSetToAbsence(rs));
            }
        }
        return absences;
    }

    public List<Absence> getAbsencesByEtudiant(int etudiantId) throws SQLException {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT a.*, u.nom, u.prenom FROM absence a JOIN etudiant e ON a.etudiant_id = e.id JOIN utilisateur u ON e.utilisateur_id = u.id WHERE a.etudiant_id = ? ORDER BY a.date_absence DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, etudiantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    absences.add(mapResultSetToAbsence(rs));
                }
            }
        }
        return absences;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM absence WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void update(Absence absence) throws SQLException {
        String sql = "UPDATE absence SET date_absence=?, date_retour=?, motif=?, justifiee=?, enseignant_id=? WHERE id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(absence.getDateAbsence()));

            if (absence.getDateRetour() != null) {
                stmt.setDate(2, Date.valueOf(absence.getDateRetour()));
            } else {
                stmt.setNull(2, Types.DATE);
            }

            stmt.setString(3, absence.getMotif());
            stmt.setBoolean(4, absence.isJustifiee());

            if (absence.getEnseignantId() != null) {
                stmt.setInt(5, absence.getEnseignantId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setInt(6, absence.getId());
            stmt.executeUpdate();
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM absence";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countByEtudiant(int etudiantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM absence WHERE etudiant_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, etudiantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countJustifiees() throws SQLException {
        String sql = "SELECT COUNT(*) FROM absence WHERE justifiee = TRUE";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countNonJustifiees() throws SQLException {
        String sql = "SELECT COUNT(*) FROM absence WHERE justifiee = FALSE";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Absence mapResultSetToAbsence(ResultSet rs) throws SQLException {
        Absence a = new Absence();
        a.setId(rs.getInt("id"));
        a.setEtudiantId(rs.getInt("etudiant_id"));
        a.setDateAbsence(rs.getDate("date_absence").toLocalDate());

        Date dateRetour = rs.getDate("date_retour");
        if (dateRetour != null) {
            a.setDateRetour(dateRetour.toLocalDate());
        }

        a.setMotif(rs.getString("motif"));
        a.setJustifiee(rs.getBoolean("justifiee"));

        int enseignantId = rs.getInt("enseignant_id");
        if (!rs.wasNull()) {
            a.setEnseignantId(enseignantId);
        }

        a.setNomEtudiant(rs.getString("nom"));
        a.setPrenomEtudiant(rs.getString("prenom"));

        return a;
    }
}