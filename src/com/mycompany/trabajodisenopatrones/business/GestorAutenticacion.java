/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trabajodisenopatrones.business; 

import com.mycompany.trabajodisenopatrones.db.Conexion;
import com.mycompany.trabajodisenopatrones.model.ModeloUsuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// PATRÓN SINGLETON
// Centraliza toda la logica de negocio relacionada con la autenticación y gestión de usuarios.
public class GestorAutenticacion {

    private static GestorAutenticacion instancia;

    // Constructor privado para el patrón Singleton.
    private GestorAutenticacion() {}

    // Método estático para obtener la única instancia de la clase.
    public static synchronized GestorAutenticacion getInstance() {
        if (instancia == null) {
            instancia = new GestorAutenticacion();
        }
        return instancia;
    }

    /**
     * Valida las credenciales de un usuario contra la base de datos.
     * @param correo El correo del usuario.
     * @param pass La contraseña del usuario.
     * @return Un objeto ModeloUsuario si las credenciales son correctas, de lo contrario null.
     */
    public ModeloUsuario validarCredenciales(String correo, String pass) {
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND pass = ?";
        // Usa el Singleton de Conexion para obtener la conexión.
        Connection con = Conexion.getInstance().getConexion();
        
        // Usamos try-with-resources para asegurar que PreparedStatement y ResultSet se cierren.
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, pass);
            
            // Se corrigió el error de llamar a executeQuery() dos veces.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si se encuentra un usuario, se crea y devuelve el objeto ModeloUsuario.
                    return new ModeloUsuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("pass"),
                        rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.toString());
        }
        // Si no se encuentra el usuario o hay un error, se devuelve null.
        return null;
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * @param nuevoUsuario Objeto ModeloUsuario con los datos a registrar.
     * @return true si el registro fue exitoso, false en caso contrario.
     */
    public boolean registrarUsuario(ModeloUsuario nuevoUsuario) {
        String sql = "INSERT INTO usuarios(nombre, correo, pass, rol) VALUES (?,?,?,?)";
        Connection con = Conexion.getInstance().getConexion();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoUsuario.getNombre());
            ps.setString(2, nuevoUsuario.getCorreo());
            ps.setString(3, nuevoUsuario.getPass());
            ps.setString(4, nuevoUsuario.getRol());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.toString());
            return false;
        }
    }
}