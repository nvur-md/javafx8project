package unischool.dao;

import unischool.models.Message;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public void create(Message message) throws SQLException {
        String sql = "INSERT INTO message (expediteur_id, expediteur_role, destinataire_id, destinataire_type, objet, contenu, filiere, annee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, message.getExpediteurId());
            stmt.setString(2, message.getExpediteurRole());

            if (message.getDestinataireId() != null) {
                stmt.setInt(3, message.getDestinataireId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, message.getDestinataireType());
            stmt.setString(5, message.getObjet());
            stmt.setString(6, message.getContenu());
            stmt.setString(7, message.getFiliere());

            if (message.getAnnee() != null) {
                stmt.setInt(8, message.getAnnee());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Message> getAllMessages() throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.nom AS expediteur_nom, u.prenom AS expediteur_prenom FROM message m JOIN utilisateur u ON m.expediteur_id = u.id ORDER BY m.date_envoi DESC";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        }
        return messages;
    }

    public List<Message> getMessagesByDestinataire(int destinataireId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.nom AS expediteur_nom, u.prenom AS expediteur_prenom FROM message m JOIN utilisateur u ON m.expediteur_id = u.id WHERE m.destinataire_id = ? ORDER BY m.date_envoi DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, destinataireId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
        }
        return messages;
    }

    public List<Message> getMessagesByType(String destinataireType) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.nom AS expediteur_nom, u.prenom AS expediteur_prenom FROM message m JOIN utilisateur u ON m.expediteur_id = u.id WHERE m.destinataire_type LIKE ? ORDER BY m.date_envoi DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + destinataireType + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
        }
        return messages;
    }

    public void updateLu(int id) throws SQLException {
        String sql = "UPDATE message SET lu = TRUE, date_lecture = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM message WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM message";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM message";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countNonLus() throws SQLException {
        String sql = "SELECT COUNT(*) FROM message WHERE lu = FALSE";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getInt("id"));
        m.setExpediteurId(rs.getInt("expediteur_id"));
        m.setExpediteurRole(rs.getString("expediteur_role"));

        int destinataireId = rs.getInt("destinataire_id");
        if (!rs.wasNull()) {
            m.setDestinataireId(destinataireId);
        }

        m.setDestinataireType(rs.getString("destinataire_type"));
        m.setObjet(rs.getString("objet"));
        m.setContenu(rs.getString("contenu"));

        Timestamp dateEnvoi = rs.getTimestamp("date_envoi");
        if (dateEnvoi != null) {
            m.setDateEnvoi(dateEnvoi.toLocalDateTime());
        }

        m.setLu(rs.getBoolean("lu"));

        Timestamp dateLecture = rs.getTimestamp("date_lecture");
        if (dateLecture != null) {
            m.setDateLecture(dateLecture.toLocalDateTime());
        }

        m.setFiliere(rs.getString("filiere"));

        int annee = rs.getInt("annee");
        if (!rs.wasNull()) {
            m.setAnnee(annee);
        }

        // Informations associées
        String nom = rs.getString("expediteur_nom");
        String prenom = rs.getString("expediteur_prenom");
        if (nom != null && prenom != null) {
            m.setNomExpediteur(prenom + " " + nom);
        }

        return m;
    }
}