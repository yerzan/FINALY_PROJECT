package dao;

import database.DatabaseConnection;
import model.Zajecia;
import model.Laboratorium;
import model.Projekt;
import model.Wyklad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static database.DatabaseConnection.connect;

public class ZajeciaDAO {

    public List<Zajecia> getAllZajecia() {
        List<Zajecia> lista = new ArrayList<>();
        String query = "SELECT * FROM zajecia ORDER BY dzien, godzina";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                lista.add(mapZajeciaFromResultSet(rs, conn));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void usunZajecia(int id) {
        String sql = "DELETE FROM zajecia WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Zajecia> filtrujPoGrupie(String grupa) {
        List<Zajecia> lista = new ArrayList<>();
        String sql = """
        SELECT * FROM zajecia WHERE id IN (
            SELECT zajecia_id FROM laboratoria WHERE nr_grupy = ?
            UNION
            SELECT zajecia_id FROM projekty WHERE grupa1 = ? OR grupa2 = ?
        ) ORDER BY dzien, godzina
        """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, grupa);
            stmt.setString(2, grupa);
            stmt.setString(3, grupa);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapZajeciaFromResultSet(rs, conn));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Zajecia> filtrujPoSali(String sala) {
        List<Zajecia> lista = new ArrayList<>();
        String sql = "SELECT * FROM zajecia WHERE sala = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sala);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapZajeciaFromResultSet(rs, conn));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void dodajZajecia(Zajecia zajecie) {
        String sql = "INSERT INTO zajecia (typ, kierunek, przedmiot, prowadzacy, sala, dzien, godzina) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id;";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, zajecie.getClass().getSimpleName());
            stmt.setString(2, zajecie.getKierunek());
            stmt.setString(3, zajecie.getPrzedmiot());
            stmt.setString(4, zajecie.getProwadzacy());
            stmt.setString(5, zajecie.getSala());
            stmt.setString(6, zajecie.getDzienTygodnia());
            stmt.setInt(7, zajecie.getGodzina());

            ResultSet rs = stmt.executeQuery();
            int zajeciaId = -1;
            if (rs.next()) {
                zajeciaId = rs.getInt(1);
            }

            if (zajecie instanceof Laboratorium lab) {
                String sqlLab = "INSERT INTO laboratoria (zajecia_id, nr_grupy) VALUES (?, ?);";
                try (PreparedStatement stmtLab = conn.prepareStatement(sqlLab)) {
                    stmtLab.setInt(1, zajeciaId);
                    stmtLab.setString(2, lab.getNrGrupy());
                    stmtLab.executeUpdate();
                }
            } else if (zajecie instanceof Projekt pr) {
                String sqlPr = "INSERT INTO projekty (zajecia_id, grupa1, grupa2) VALUES (?, ?, ?);";
                try (PreparedStatement stmtPr = conn.prepareStatement(sqlPr)) {
                    stmtPr.setInt(1, zajeciaId);
                    stmtPr.setString(2, pr.getGrupa1());
                    stmtPr.setString(3, pr.getGrupa2());
                    stmtPr.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean czySalaZajeta(String sala, String dzien, int godzina, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM zajecia WHERE sala = ? AND dzien = ? AND godzina = ? AND id != ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sala);
            stmt.setString(2, dzien);
            stmt.setInt(3, godzina);
            stmt.setInt(4, excludeId == null ? -1 : excludeId);

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean czyGrupaMaZajecia(String grupa, String dzien, int godzina, Integer excludeId) {
        String sql = "SELECT * FROM zajecia WHERE dzien = ? AND godzina = ?";
        if (excludeId != null) {
            sql += " AND id != ?";
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dzien);
            pstmt.setInt(2, godzina);
            if (excludeId != null) {
                pstmt.setInt(3, excludeId);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String typ = rs.getString("typ");

                if (typ.equals("Wyklad")) {
                    // wykłady są zawsze dla obu grup
                    return true;
                }

                if (typ.equals("Laboratorium")) {
                    PreparedStatement labStmt = conn.prepareStatement(
                            "SELECT nr_grupy FROM laboratoria WHERE zajecia_id = ?");
                    labStmt.setInt(1, id);
                    ResultSet labRs = labStmt.executeQuery();
                    if (labRs.next()) {
                        String nr = labRs.getString("nr_grupy");
                        if (grupa.equals(nr)) return true;
                    }
                    labRs.close();
                    labStmt.close();
                }

                if (typ.equals("Projekt")) {
                    PreparedStatement prStmt = conn.prepareStatement(
                            "SELECT grupa1, grupa2 FROM projekty WHERE zajecia_id = ?");
                    prStmt.setInt(1, id);
                    ResultSet prRs = prStmt.executeQuery();
                    if (prRs.next()) {
                        String g1 = prRs.getString("grupa1");
                        String g2 = prRs.getString("grupa2");
                        if (grupa.equals(g1) || grupa.equals(g2)) return true;
                    }
                    prRs.close();
                    prStmt.close();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public void updateZajecia(Zajecia zajecie) {
        String sql = """
        UPDATE zajecia
        SET kierunek = ?, przedmiot = ?, prowadzacy = ?, sala = ?, dzien = ?, godzina = ?
        WHERE id = ?
        """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, zajecie.getKierunek());
            stmt.setString(2, zajecie.getPrzedmiot());
            stmt.setString(3, zajecie.getProwadzacy());
            stmt.setString(4, zajecie.getSala());
            stmt.setString(5, zajecie.getDzienTygodnia());
            stmt.setInt(6, zajecie.getGodzina());
            stmt.setInt(7, zajecie.getId());

            stmt.executeUpdate();

            if (zajecie instanceof Laboratorium lab) {
                String sqlLab = "UPDATE laboratoria SET nr_grupy = ? WHERE zajecia_id = ?";
                try (PreparedStatement stmtLab = conn.prepareStatement(sqlLab)) {
                    stmtLab.setString(1, lab.getNrGrupy());
                    stmtLab.setInt(2, zajecie.getId());
                    stmtLab.executeUpdate();
                }
            } else if (zajecie instanceof Projekt pr) {
                String sqlPr = "UPDATE projekty SET grupa1 = ?, grupa2 = ? WHERE zajecia_id = ?;";
                try (PreparedStatement stmtPr = conn.prepareStatement(sqlPr)) {
                    stmtPr.setString(1, pr.getGrupa1());
                    stmtPr.setString(2, pr.getGrupa2());
                    stmtPr.setInt(3, zajecie.getId());
                    stmtPr.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Zajecia> filtrujPoTypie(String typ) {
        List<Zajecia> lista = new ArrayList<>();
        String sql = "SELECT * FROM zajecia WHERE typ = ? ORDER BY dzien, godzina";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, typ);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapZajeciaFromResultSet(rs, conn));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    private Zajecia mapZajeciaFromResultSet(ResultSet rs, Connection conn) throws SQLException {
        int id = rs.getInt("id");
        String typ = rs.getString("typ");
        String kierunek = rs.getString("kierunek");
        String przedmiot = rs.getString("przedmiot");
        String prowadzacy = rs.getString("prowadzacy");
        String sala = rs.getString("sala");
        String dzien = rs.getString("dzien");
        int godzina = rs.getInt("godzina");

        if (typ.equals("Laboratorium")) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT nr_grupy FROM laboratoria WHERE zajecia_id = ?")) {
                stmt.setInt(1, id);
                ResultSet result = stmt.executeQuery();
                if (result.next()) {
                    return new Laboratorium(id, kierunek, przedmiot, prowadzacy, sala, dzien, godzina, result.getString("nr_grupy"));
                }
            }
            return new Laboratorium(id, kierunek, przedmiot, prowadzacy, sala, dzien, godzina, "");
        }

        if (typ.equals("Projekt")) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT grupa1, grupa2 FROM projekty WHERE zajecia_id = ?")) {
                stmt.setInt(1, id);
                ResultSet result = stmt.executeQuery();
                if (result.next()) {
                    return new Projekt(id, kierunek, przedmiot, prowadzacy, sala, dzien, godzina,
                            result.getString("grupa1"), result.getString("grupa2"));
                }
            }
            return new Projekt(id, kierunek, przedmiot, prowadzacy, sala, dzien, godzina, "", "");
        }

        return new Wyklad(id, kierunek, przedmiot, prowadzacy, sala, dzien, godzina);
    }
}
