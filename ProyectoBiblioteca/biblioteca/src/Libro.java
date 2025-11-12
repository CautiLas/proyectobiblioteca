
import java.sql.*;

public class Libro {
    public static void agregarLibro(String titulo, String autor, String categoria, int anio) {
        String sql = "INSERT INTO libros (titulo, autor, categoria, anio_publicacion, disponible) VALUES (?, ?, ?, ?, TRUE)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            stmt.setString(2, autor);
            stmt.setString(3, categoria);
            stmt.setInt(4, anio);
            stmt.executeUpdate();
            System.out.println("Libro agregado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al agregar libro: " + e.getMessage());
        }
    }

    public static void listarLibros() {
        String sql = "SELECT * FROM libros";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String categoria = rs.getString("categoria");
                int anio = rs.getInt("anio_publicacion");
                boolean anioNull = rs.wasNull();

                if ((titulo == null || titulo.trim().isEmpty()) &&
                        (autor == null || autor.trim().isEmpty()) &&
                        (categoria == null || categoria.trim().isEmpty()) &&
                        anioNull) {
                    continue;
                }

                if (titulo == null || titulo.trim().isEmpty()) titulo = "<sin título>";
                if (autor == null || autor.trim().isEmpty()) autor = "<sin autor>";
                if (categoria == null || categoria.trim().isEmpty()) categoria = "<sin categoría>";

                System.out.println("ID: " + rs.getInt("id") +
                        ", Título: " + titulo +
                        ", Autor: " + autor +
                        ", Categoría: " + categoria +
                        ", Año: " + (anioNull ? "<desconocido>" : anio));
            }

            if (!found) {
                System.out.println("No hay libros registrados.");
            }

        } catch (SQLException e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }
    }

    public static void eliminarLibro(int id) {
        String checkPrestamosSql = "SELECT COUNT(*) FROM prestamos WHERE id_libro = ?";
        String deleteSql = "DELETE FROM libros WHERE id = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement psCheck = conn.prepareStatement(checkPrestamosSql)) {

            psCheck.setInt(1, id);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("No se puede eliminar el libro: existe(n) préstamo(s) que lo referencian. Elimina primero los préstamos relacionados o ajusta la clave foránea (por ejemplo, ON DELETE CASCADE).");
                    return;
                }
            }

            try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                psDel.setInt(1, id);
                int rows = psDel.executeUpdate();
                if (rows == 0) {
                    System.out.println("No existe libro con ID " + id + ".");
                } else {
                    System.out.println("Libro eliminado correctamente.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al eliminar libro: " + e.getMessage());
        }
    }
}