package unischool.dao;

import unischool.models.Matiere;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereDAO {

    public void create(Matiere matiere) throws SQLException {
        String sql = "INSERT INTO matiere (code, nom, description, credits, semestre, filiere, enseignant_responsable_id, volume_horaire, actif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, matiere.getCode());
            stmt.setString(2, matiere.getNom());
            stmt.setString(3, matiere.getDescription());
            stmt.setInt(4, matiere.getCredits());
            stmt.setInt(5, matiere.getSemestre());
            stmt.setString(6, matiere.getFiliere());

            if (matiere.getEnseignantResponsableId() != null) {
                stmt.setInt(7, matiere.getEnseignantResponsableId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setInt(8, matiere.getVolumeHoraire());
            stmt.setBoolean(9, matiere.isActif());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    matiere.setId(rs.getInt(1));
                }
            }
        }
    }

    public Matiere read(int id) throws SQLException {
        String sql = "SELECT m.*, u.nom AS enseignant_nom, u.prenom AS enseignant_prenom FROM matiere m LEFT JOIN enseignant e ON m.enseignant_responsable_id = e.id LEFT JOIN utilisateur u ON e.utilisateur_id = u.id WHERE m.id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Matiere m = mapResultSetToMatiere(rs);
                    // Ajouter le nom du responsable
                    String nomEns = rs.getString("enseignant_nom");
                    String prenomEns = rs.getString("enseignant_prenom");
                    if (nomEns != null && prenomEns != null) {
                        m.setNomEnseignantResponsable(prenomEns + " " + nomEns);
                    }
                    return m;
                }
            }
        }
        return null;
    }

    public Matiere readByCode(String code) throws SQLException {
        String sql = "SELECT * FROM matiere WHERE code = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMatiere(rs);
                }
            }
        }
        return null;
    }

    public List<Matiere> readAll() throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT m.*, u.nom AS enseignant_nom, u.prenom AS enseignant_prenom FROM matiere m LEFT JOIN enseignant e ON m.enseignant_responsable_id = e.id LEFT JOIN utilisateur u ON e.utilisateur_id = u.id ORDER BY m.semestre, m.code";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Matiere m = mapResultSetToMatiere(rs);
                String nomEns = rs.getString("enseignant_nom");
                String prenomEns = rs.getString("enseignant_prenom");
                if (nomEns != null && prenomEns != null) {
                    m.setNomEnseignantResponsable(prenomEns + " " + nomEns);
                }
                matieres.add(m);
            }
        }
        return matieres;
    }

    public List<Matiere> readBySemestre(int semestre) throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT * FROM matiere WHERE semestre = ? ORDER BY code";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, semestre);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matieres.add(mapResultSetToMatiere(rs));
                }
            }
        }
        return matieres;
    }

    public List<Matiere> readByFiliere(String filiere) throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT * FROM matiere WHERE filiere = ? ORDER BY semestre, code";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filiere);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matieres.add(mapResultSetToMatiere(rs));
                }
            }
        }
        return matieres;
    }

    public void update(Matiere matiere) throws SQLException {
        String sql = "UPDATE matiere SET code=?, nom=?, description=?, credits=?, semestre=?, filiere=?, enseignant_responsable_id=?, volume_horaire=?, actif=? WHERE id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matiere.getCode());
            stmt.setString(2, matiere.getNom());
            stmt.setString(3, matiere.getDescription());
            stmt.setInt(4, matiere.getCredits());
            stmt.setInt(5, matiere.getSemestre());
            stmt.setString(6, matiere.getFiliere());

            if (matiere.getEnseignantResponsableId() != null) {
                stmt.setInt(7, matiere.getEnseignantResponsableId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setInt(8, matiere.getVolumeHoraire());
            stmt.setBoolean(9, matiere.isActif());
            stmt.setInt(10, matiere.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM matiere WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Matiere> search(String keyword) throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT * FROM matiere WHERE code LIKE ? OR nom LIKE ? OR filiere LIKE ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matieres.add(mapResultSetToMatiere(rs));
                }
            }
        }
        return matieres;
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM matiere";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
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