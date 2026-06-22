package unischool.dao;

import unischool.models.Etudiant;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {

    public void create(Etudiant etudiant) throws SQLException {
        String sql = "INSERT INTO etudiant (utilisateur_id, date_naissance, telephone, adresse, filiere, niveau, annee_etude, matricule, groupe, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, etudiant.getUtilisateurId());
            stmt.setDate(2, etudiant.getDateNaissance() != null ? Date.valueOf(etudiant.getDateNaissance()) : null);
            stmt.setString(3, etudiant.getTelephone());
            stmt.setString(4, etudiant.getAdresse());
            stmt.setString(5, etudiant.getFiliere());
            stmt.setString(6, etudiant.getNiveau());
            stmt.setInt(7, etudiant.getAnneeEtude());
            stmt.setString(8, etudiant.getMatricule());
            stmt.setString(9, etudiant.getGroupe());
            stmt.setString(10, etudiant.getStatut() != null ? etudiant.getStatut() : "ACTIF");

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    etudiant.setId(rs.getInt(1));
                }
            }
        }
    }

    public Etudiant read(int id) throws SQLException {
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM etudiant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE e.id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEtudiant(rs);
                }
            }
        }
        return null;
    }

    public Etudiant readByUtilisateurId(int utilisateurId) throws SQLException {
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM etudiant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE e.utilisateur_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, utilisateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEtudiant(rs);
                }
            }
        }
        return null;
    }

    public Etudiant readByMatricule(String matricule) throws SQLException {
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM etudiant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE e.matricule = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matricule);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEtudiant(rs);
                }
            }
        }
        return null;
    }

    public List<Etudiant> readAll() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM etudiant e JOIN utilisateur u ON e.utilisateur_id = u.id ORDER BY u.nom, u.prenom";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                etudiants.add(mapResultSetToEtudiant(rs));
            }
        }
        return etudiants;
    }

    public List<Etudiant> readByFiliere(String filiere) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM etudiant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE e.filiere = ? ORDER BY u.nom, u.prenom";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filiere);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    etudiants.add(mapResultSetToEtudiant(rs));
                }
            }
        }
        return etudiants;
    }

    public List<Etudiant> readByAnnee(int annee) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM etudiant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE e.annee_etude = ? ORDER BY u.nom, u.prenom";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, annee);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    etudiants.add(mapResultSetToEtudiant(rs));
                }
            }
        }
        return etudiants;
    }

    public List<Etudiant> readByFiliereAndAnnee(String filiere, int annee) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM etudiant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE e.filiere = ? AND e.annee_etude = ? ORDER BY u.nom, u.prenom";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filiere);
            stmt.setInt(2, annee);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    etudiants.add(mapResultSetToEtudiant(rs));
                }
            }
        }
        return etudiants;
    }

    public void update(Etudiant etudiant) throws SQLException {
        String sql = "UPDATE etudiant SET date_naissance=?, telephone=?, adresse=?, filiere=?, niveau=?, annee_etude=?, matricule=?, groupe=?, statut=? WHERE id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, etudiant.getDateNaissance() != null ? Date.valueOf(etudiant.getDateNaissance()) : null);
            stmt.setString(2, etudiant.getTelephone());
            stmt.setString(3, etudiant.getAdresse());
            stmt.setString(4, etudiant.getFiliere());
            stmt.setString(5, etudiant.getNiveau());
            stmt.setInt(6, etudiant.getAnneeEtude());
            stmt.setString(7, etudiant.getMatricule());
            stmt.setString(8, etudiant.getGroupe());
            stmt.setString(9, etudiant.getStatut());
            stmt.setInt(10, etudiant.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM etudiant WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Etudiant> search(String keyword) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT e.*, u.nom, u.prenom, u.email, u.actif FROM etudiant e JOIN utilisateur u ON e.utilisateur_id = u.id WHERE u.nom LIKE ? OR u.prenom LIKE ? OR u.email LIKE ? OR e.matricule LIKE ? OR e.filiere LIKE ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    etudiants.add(mapResultSetToEtudiant(rs));
                }
            }
        }
        return etudiants;
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM etudiant";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countByFiliere(String filiere) throws SQLException {
        String sql = "SELECT COUNT(*) FROM etudiant WHERE filiere = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filiere);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Etudiant mapResultSetToEtudiant(ResultSet rs) throws SQLException {
        Etudiant e = new Etudiant();
        e.setId(rs.getInt("id"));
        e.setUtilisateurId(rs.getInt("utilisateur_id"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setEmail(rs.getString("email"));
        e.setActif(rs.getBoolean("actif"));

        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            e.setDateNaissance(dateNaissance.toLocalDate());
        }

        e.setTelephone(rs.getString("telephone"));
        e.setAdresse(rs.getString("adresse"));
        e.setFiliere(rs.getString("filiere"));
        e.setNiveau(rs.getString("niveau"));
        e.setAnneeEtude(rs.getInt("annee_etude"));
        e.setMatricule(rs.getString("matricule"));
        e.setGroupe(rs.getString("groupe"));

        Date dateInscription = rs.getDate("date_inscription");
        if (dateInscription != null) {
            e.setDateInscription(dateInscription.toLocalDate());
        }

        e.setStatut(rs.getString("statut"));

        return e;
    }
}