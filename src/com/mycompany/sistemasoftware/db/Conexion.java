
package com.mycompany.sistemasoftware.db; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

// PATRÓN CREACIONAL: SINGLETON
// Propósito: Asegurar que exista una única instancia de la clase Conexion en toda la
// aplicación. Esto es vital para gestionar de forma centralizada y eficiente el
// recurso de la conexión a la base de datos, evitando múltiples conexiones innecesarias.
public class Conexion {

    // 1. La única instancia de la clase, privada y estática.
    private static Conexion instancia;
    
    private Connection con;
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String user = "root";
    private final String pass = "1234";
    private final String url = "jdbc:mysql://localhost:3306/sistemaventa?serverTimezone=UTC";

    // 2. Constructor privado para impedir la instanciación directa con 'new'.
    private Conexion() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "FATAL ERROR: No se pudo conectar a la base de datos.\n" + e.getMessage());
            System.exit(1); // Termina la aplicación si la conexión falla.
        }
    }

    // 3. Método público, estático y sincronizado para obtener la instancia.
    // 'synchronized' lo hace seguro en entornos multi-hilo.
    public static synchronized Conexion getInstance() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    // Método para que el resto de la aplicación obtenga el objeto Connection.
    public Connection getConexion() {
        return con;
    }
}