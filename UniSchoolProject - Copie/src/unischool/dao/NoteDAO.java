package unischool.dao;

import unischool.models.Note;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    public void create(Note note) throws SQLException {
        String sql = "INSERT INTO note (etudiant_id, matiere_id, enseignant_id, valeur, date_evaluation, coefficient, appreciation, type, semestre, validee) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, note.getEtudiantId());
            stmt.setInt(2, note.getMatiereId());

            if (note.getEnseignantId() != null) {
                stmt.setInt(3, note.getEnseignantId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setDouble(4, note.getValeur());
            stmt.setDate(5, note.getDateEvaluation() != null ? Date.valueOf(note.getDateEvaluation()) : null);
            stmt.setDouble(6, note.getCoefficient());
            stmt.setString(7, note.getAppreciation());
            stmt.setString(8, note.getType());
            stmt.setInt(9, note.getSemestre());
            stmt.setBoolean(10, note.isValidee());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    note.setId(rs.getInt(1));
                }
            }
        }
    }

    public Note read(int id) throws SQLException {
        String sql = "SELECT n.*, u.nom, u.prenom, m.nom as matiere_nom, m.code FROM note n JOIN etudiant e ON n.etudiant_id = e.id JOIN utilisateur u ON e.utilisateur_id = u.id JOIN matiere m ON n.matiere_id = m.id WHERE n.id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNote(rs);
                }
            }
        }
        return null;
    }

    public List<Note> readAll() throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.*, u.nom, u.prenom, m.nom as matiere_nom, m.code FROM note n JOIN etudiant e ON n.etudiant_id = e.id JOIN utilisateur u ON e.utilisateur_id = u.id JOIN matiere m ON n.matiere_id = m.id ORDER BY n.date_evaluation DESC";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        }
        return notes;
    }

    public List<Note> getNotesByEtudiant(int etudiantId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.*, u.nom, u.prenom, m.nom as matiere_nom, m.code FROM note n JOIN etudiant e ON n.etudiant_id = e.id JOIN utilisateur u ON e.utilisateur_id = u.id JOIN matiere m ON n.matiere_id = m.id WHERE n.etudiant_id = ? ORDER BY n.date_evaluation DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, etudiantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapResultSetToNote(rs));
                }
            }
        }
        return notes;
    }

    public List<Note> getNotesByMatiere(int matiereId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.*, u.nom, u.prenom, m.nom as matiere_nom, m.code FROM note n JOIN etudiant e ON n.etudiant_id = e.id JOIN utilisateur u ON e.utilisateur_id = u.id JOIN matiere m ON n.matiere_id = m.id WHERE n.matiere_id = ? ORDER BY n.date_evaluation DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matiereId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapResultSetToNote(rs));
                }
            }
        }
        return notes;
    }

    public List<Note> getNotesByEnseignant(int enseignantId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.*, u.nom, u.prenom, m.nom as matiere_nom, m.code FROM note n JOIN etudiant e ON n.etudiant_id = e.id JOIN utilisateur u ON e.utilisateur_id = u.id JOIN matiere m ON n.matiere_id = m.id WHERE n.enseignant_id = ? ORDER BY n.date_evaluation DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enseignantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapResultSetToNote(rs));
                }
            }
        }
        return notes;
    }

    public void update(Note note) throws SQLException {
        String sql = "UPDATE note SET valeur=?, date_evaluation=?, coefficient=?, appreciation=?, type=?, semestre=?, validee=?, date_modification=? WHERE id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, note.getValeur());
            stmt.setDate(2, note.getDateEvaluation() != null ? Date.valueOf(note.getDateEvaluation()) : null);
            stmt.setDouble(3, note.getCoefficient());
            stmt.setString(4, note.getAppreciation());
            stmt.setString(5, note.getType());
            stmt.setInt(6, note.getSemestre());
            stmt.setBoolean(7, note.isValidee());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            stmt.setInt(9, note.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM note WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void deleteByEtudiant(int etudiantId) throws SQLException {
        String sql = "DELETE FROM note WHERE etudiant_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, etudiantId);
            stmt.executeUpdate();
        }
    }

    // ============================================================
// MOYENNES - INCLURE TOUTES LES NOTES (PAS SEULEMENT VALIDEES)
// ============================================================

    /**
     * Moyenne générale - Toutes les notes
     */
    public double getMoyenneGenerale() throws SQLException {
        String sql = "SELECT AVG(valeur) FROM note";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    /**
     * Moyenne d'un étudiant - Toutes ses notes
     */
    public double getMoyenneByEtudiant(int etudiantId) throws SQLException {
        String sql = "SELECT AVG(valeur * coefficient) / AVG(coefficient) FROM note WHERE etudiant_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, etudiantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    /**
     * Moyenne d'une matière - Toutes les notes
     */
    public double getMoyenneByMatiere(int matiereId) throws SQLException {
        String sql = "SELECT AVG(valeur) FROM note WHERE matiere_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matiereId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM note";

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
        String sql = "SELECT COUNT(*) FROM note WHERE etudiant_id = ?";

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

    private Note mapResultSetToNote(ResultSet rs) throws SQLException {
        Note n = new Note();
        n.setId(rs.getInt("id"));
        n.setEtudiantId(rs.getInt("etudiant_id"));
        n.setMatiereId(rs.getInt("matiere_id"));

        int enseignantId = rs.getInt("enseignant_id");
        if (!rs.wasNull()) {
            n.setEnseignantId(enseignantId);
        }

        n.setValeur(rs.getDouble("valeur"));

        Date dateEval = rs.getDate("date_evaluation");
        if (dateEval != null) {
            n.setDateEvaluation(dateEval.toLocalDate());
        }

        n.setCoefficient(rs.getDouble("coefficient"));
        n.setAppreciation(rs.getString("appreciation"));
        n.setType(rs.getString("type"));
        n.setSemestre(rs.getInt("semestre"));

        Timestamp dateSaisie = rs.getTimestamp("date_saisie");
        if (dateSaisie != null) {
            n.setDateSaisie(dateSaisie.toLocalDateTime().toLocalDate());
        }

        Timestamp dateModification = rs.getTimestamp("date_modification");
        if (dateModification != null) {
            n.setDateModification(dateModification.toLocalDateTime().toLocalDate());
        }

        n.setValidee(rs.getBoolean("validee"));

        // Informations associées
        n.setNomEtudiant(rs.getString("nom"));
        n.setPrenomEtudiant(rs.getString("prenom"));
        n.setNomMatiere(rs.getString("matiere_nom"));
        n.setCodeMatiere(rs.getString("code"));

        return n;
    }

    /**
     * Compter les notes d'un enseignant
     */
    public int countByEnseignant(int enseignantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM note WHERE enseignant_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enseignantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}