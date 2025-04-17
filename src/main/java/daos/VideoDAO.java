package daos;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Video;

/**
 *
 * @author zhihan
 */
public class VideoDAO {

    /**
     * Actualiza el número de reproducciones de un vídeo dado su ID
     * @param videoId id de video
     * @param incremento incremento en 1 a la reproducción de video
     * @return True si ha actualizado correctamente el número de reproducción, False si no se ha actualizado correctamente.
     * @throws SQLException Exception en SQL
     */
    public boolean actualizarReproducciones(int videoId, int incremento) throws SQLException {
        String sql = "UPDATE VIDEOS SET reproducciones = reproducciones + ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, incremento);
            ps.setInt(2, videoId);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new SQLException("Error al registrar video", ex);
        }finally {
            // Asegurarse de que los recursos sean cerrados en cualquier caso
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Consulta vídeos por título
     * @param titulo título del video
     * @return la llista de videos encontrados
     * @throws SQLException Excepcion de SQL
     */
    public List<Video> buscarPorTitulo(String titulo) throws SQLException {
        String sql = "SELECT * FROM VIDEOS WHERE titulo LIKE ?";
        List<Video> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + titulo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Video video = mapResultSetToVideo(rs);
                    lista.add(video);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new SQLException("Error al buscar video por título", ex);
        }
        return lista;
    }

    /**
     * Consulta vídeos por autor
     * @param autor autor del vídeo
     * @return la llista de videos encontrados
     * @throws SQLException 
     */
    public List<Video> buscarPorAutor(String autor) throws SQLException {
        String sql = "SELECT * FROM VIDEOS WHERE autor LIKE ?";
        List<Video> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + autor + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Video video = mapResultSetToVideo(rs);
                    lista.add(video);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new SQLException("Error al buscar video por autor", ex);
        }
        return lista;
    }

    /**
     * Consulta vídeos por fecha de creación
     * @param fechaStr fecha en cadena de texto
     * @return la llista de videos encontrados
     */

    public List<Video> buscarPorFecha(String fechaStr) throws SQLException {
        List<Video> lista = new ArrayList<>();

        // Determinar cómo adaptar el criterio de búsqueda con LIKE
        String patronBusqueda;
        if (fechaStr.matches("^\\d{4}$")) {
            // Solo año: 2024 → "2024-%"
            patronBusqueda = fechaStr + "-%";
        } else if (fechaStr.matches("^\\d{2}/\\d{4}$")) {
            // Mes y año: 04/2024 → "2024-04-%"
            String[] partes = fechaStr.split("/");
            patronBusqueda = partes[1] + "-" + partes[0] + "-%";
        } else if (fechaStr.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            // Día completo: 11/04/2024 → "2024-04-11%"
            String[] partes = fechaStr.split("/");
            patronBusqueda = partes[2] + "-" + partes[1] + "-" + partes[0] + "%";
        } else {
            // Formato inválido
            return lista;
        }
        System.out.println("---------------------" + patronBusqueda);
        String sql = "SELECT * FROM VIDEOS WHERE fecha_creacion LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patronBusqueda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Video video = mapResultSetToVideo(rs);
                    lista.add(video);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new SQLException("Error al buscar video por fecha de creación", ex);
        }

        return lista;
    }
    
    /**
     * Método auxiliar para mapear un ResultSet a un objeto Video
     * @param rs resultat set
     * @return video de class Video
     * @throws SQLException Exception de sql
     */
    private Video mapResultSetToVideo(ResultSet rs) throws SQLException {
        Video video = new Video(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("fecha_creacion"),
                        rs.getString("duracion"),
                        rs.getInt("reproducciones"),
                        rs.getString("descripcion"),
                        rs.getString("formato"),
                        rs.getString("path"),
                        rs.getInt("user_id")
                );
        return video;
    }
}
