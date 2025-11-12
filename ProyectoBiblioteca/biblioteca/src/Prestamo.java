import java.sql.*;
import java.time.LocalDate;

public class Prestamo {

    public static void registrarPrestamo(int idUsuario, int idLibro) {
        String checkUsuarioSql = "SELECT 1 FROM usuarios WHERE id = ?";
        String checkLibroSql = "SELECT disponible FROM libros WHERE id = ?";
        String insertPrestamoSql = "INSERT INTO prestamos (id_usuario, id_libro, fecha_prestamo) VALUES (?, ?, ?)";
        String updateLibroSql = "UPDATE libros SET disponible = FALSE WHERE id = ?";

        try (Connection conn = ConexionBD.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(checkUsuarioSql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Error: usuario con ID " + idUsuario + " no existe.");
                        conn.rollback();
                        return;
                    }
                }
            }

            boolean disponible;
            try (PreparedStatement ps = conn.prepareStatement(checkLibroSql)) {
                ps.setInt(1, idLibro);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Error: libro con ID " + idLibro + " no existe.");
                        conn.rollback();
                        return;
                    }
                    disponible = rs.getBoolean("disponible");
                    if (rs.wasNull()) disponible = false;
                }
            }

            if (!disponible) {
                System.out.println("El libro con ID " + idLibro + " no está disponible para préstamo.");
                conn.rollback();
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(insertPrestamoSql)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idLibro);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateLibroSql)) {
                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Préstamo registrado correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al registrar préstamo: " + e.getMessage());
        }
    }

    // Ahora la devolución elimina el préstamo en lugar de actualizarlo
    public static void registrarDevolucion(int idPrestamo, int idLibro) {
        String checkPrestamoSql = "SELECT id_libro FROM prestamos WHERE id = ?";
        String deletePrestamoSql = "DELETE FROM prestamos WHERE id = ?";
        String updateLibroSql = "UPDATE libros SET disponible = TRUE WHERE id = ?";

        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            int libroEnPrestamo;
            try (PreparedStatement ps = conn.prepareStatement(checkPrestamoSql)) {
                ps.setInt(1, idPrestamo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No existe préstamo con ID " + idPrestamo + ".");
                        conn.rollback();
                        return;
                    }
                    libroEnPrestamo = rs.getInt("id_libro");
                }
            }

            if (libroEnPrestamo != idLibro) {
                System.out.println("El préstamo " + idPrestamo + " no referencia al libro con ID " + idLibro + ".");
                conn.rollback();
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(deletePrestamoSql)) {
                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateLibroSql)) {
                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Devolución registrada y préstamo eliminado correctamente.");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) { /* ignorar */ }
            }
            System.out.println("Error al registrar devolución: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) { /* ignorar */ }
            }
        }
    }

    public static void listarPrestamos() {
        String sql = "SELECT * FROM prestamos";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                int idUsuario = rs.getInt("id_usuario");
                int idLibro = rs.getInt("id_libro");
                String fechaPrestamo = rs.getString("fecha_prestamo");
                String fechaDevolucion = rs.getString("fecha_devolucion");

                if (fechaPrestamo == null || fechaPrestamo.trim().isEmpty()) fechaPrestamo = "<sin fecha>";
                String estado = (fechaDevolucion == null || fechaDevolucion.trim().isEmpty()) ? "prestado" : "devuelto";
                if (fechaDevolucion == null || fechaDevolucion.trim().isEmpty()) fechaDevolucion = "<sin devolución>";

                System.out.println("ID: " + id +
                        ", Usuario ID: " + idUsuario +
                        ", Libro ID: " + idLibro +
                        ", Fecha préstamo: " + fechaPrestamo +
                        ", Fecha devolución: " + fechaDevolucion +
                        ", Estado: " + estado);
            }

            if (!found) {
                System.out.println("No hay préstamos registrados.");
            }

        } catch (SQLException e) {
            System.out.println("Error al listar préstamos: " + e.getMessage());
        }
    }
}