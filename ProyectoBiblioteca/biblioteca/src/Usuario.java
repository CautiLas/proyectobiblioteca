import java.sql.*;

public class Usuario {
    public static void agregarUsuario(String nombre, String correo) {
        String sql = "INSERT INTO usuarios (nombre, correo, fecha_registro) VALUES (?, ?, CURDATE())";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, correo);
            stmt.executeUpdate();
            System.out.println("Usuario agregado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al agregar usuario: " + e.getMessage());
        }
    }

    public static void listarUsuarios() {
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                String nombre = rs.getString("nombre");
                String correo = rs.getString("correo");

                if ((nombre == null || nombre.trim().isEmpty()) && (correo == null || correo.trim().isEmpty())) {
                    continue;
                }

                if (nombre == null || nombre.trim().isEmpty()) nombre = "<sin nombre>";
                if (correo == null || correo.trim().isEmpty()) correo = "<sin correo>";

                System.out.println("ID: " + rs.getInt("id") +
                        ", Nombre: " + nombre +
                        ", Correo: " + correo);
            }

            if (!found) {
                System.out.println("No hay usuarios registrados.");
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
    }

    public static void eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Usuario eliminado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }
    }
}