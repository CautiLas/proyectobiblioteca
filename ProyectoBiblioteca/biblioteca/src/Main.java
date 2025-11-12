import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            printMenu();
            int opcion = readInt("Elige una opción: ");

            try {
                switch (opcion) {
                    case 1: agregarUsuario(); break;
                    case 2: Usuario.listarUsuarios(); break;
                    case 3: eliminarUsuario(); break;
                    case 4: agregarLibro(); break;
                    case 5: Libro.listarLibros(); break;
                    case 6: eliminarLibro(); break;
                    case 7: registrarPrestamo(); break;
                    case 8: registrarDevolucion(); break;
                    case 9: Prestamo.listarPrestamos(); break;
                    case 0:
                        System.out.println("¡Hasta luego!");
                        sc.close();
                        return;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Sistema de Gestión de Biblioteca ---");
        System.out.println("1. Agregar usuario");
        System.out.println("2. Listar usuarios");
        System.out.println("3. Eliminar usuario");
        System.out.println("4. Agregar libro");
        System.out.println("5. Listar libros");
        System.out.println("6. Eliminar libro");
        System.out.println("7. Registrar préstamo");
        System.out.println("8. Registrar devolución");
        System.out.println("9. Listar préstamos");
        System.out.println("0. Salir");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            if (line == null) line = "";
            line = line.trim();
            if (line.isEmpty()) {
                System.out.println("Entrada vacía. Intenta de nuevo.");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Intenta de nuevo.");
            }
        }
    }

    private static String readString(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine();
            if (s == null) s = "";
            s = s.trim();
            if (required && s.isEmpty()) {
                System.out.println("Este campo no puede estar vacío.");
                continue;
            }
            return s;
        }
    }

    private static void agregarUsuario() {
        String nombre = readString("Nombre: ", true);
        String correo = readString("Correo: ", true);
        Usuario.agregarUsuario(nombre, correo);
    }

    private static void eliminarUsuario() {
        int id = readInt("ID del usuario a eliminar: ");
        Usuario.eliminarUsuario(id);
    }

    private static void agregarLibro() {
        String titulo = readString("Título: ", true);
        String autor = readString("Autor: ", true);
        String categoria = readString("Categoría: ", false);
        int anio = readInt("Año de publicación: ");
        Libro.agregarLibro(titulo, autor, categoria, anio);
    }

    private static void eliminarLibro() {
        int id = readInt("ID del libro a eliminar: ");
        Libro.eliminarLibro(id);
    }

    private static void registrarPrestamo() {
        int idUsuario = readInt("ID del usuario: ");
        int idLibro = readInt("ID del libro: ");
        Prestamo.registrarPrestamo(idUsuario, idLibro);
    }

    private static void registrarDevolucion() {
        int idPrestamo = readInt("ID del préstamo: ");
        int idLibro = readInt("ID del libro: ");
        Prestamo.registrarDevolucion(idPrestamo, idLibro);
    }
}
