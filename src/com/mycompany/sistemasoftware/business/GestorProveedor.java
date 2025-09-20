 

package com.mycompany.sistemasoftware.business;

import com.mycompany.sistemasoftware.db.Conexion;
import com.mycompany.sistemasoftware.model.ModeloProveedor;
import com.mycompany.sistemasoftware.patterns.Observer;
import com.mycompany.sistemasoftware.patterns.Subject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

// IMPLEMENTA SINGLETON y es un SUBJECT del patrón OBSERVER
public class GestorProveedor implements Subject {

    // --- Parte Singleton ---
    private static GestorProveedor instancia;
    
    // --- Parte Observer ---
    private List<Observer> observers;
    
    // Constructor privado para el patrón Singleton
    private GestorProveedor() {
        this.observers = new ArrayList<>();
    }

    // Método estático para obtener la única instancia de la clase
    public static synchronized GestorProveedor getInstance() {
        if (instancia == null) {
            instancia = new GestorProveedor();
        }
        return instancia;
    }

    // --- Métodos del patrón Observer ---
    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        // Notifica a cada UI suscrita que debe actualizarse
        for (Observer observer : observers) {
            observer.update();
        }
    }
    
    // --- Métodos de Negocio (Lógica que antes estaba en ProveedorDao) ---

    public boolean registrarProveedor(ModeloProveedor pr) {
        String sql = "INSERT INTO proveedor(ruc, nombre, telefono, direccion, razon) VALUES (?,?,?,?,?)";
        // Usa el Singleton de Conexion
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pr.getRuc());
            ps.setString(2, pr.getNombre());
            ps.setInt(3, pr.getTelefono());
            ps.setString(4, pr.getDireccion());
            ps.setString(5, pr.getRazon());
            ps.execute();
            
            notifyObservers(); // Notificar a las vistas del cambio
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar proveedor: " + e.toString());
            return false;
        }
    }

    public void cargarTabla(DefaultTableModel model) {
        model.setRowCount(0); // Limpiar la tabla antes de cargar
        String sql = "SELECT * FROM proveedor";
        Connection con = Conexion.getInstance().getConexion();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("ruc"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("direccion"),
                    rs.getString("razon")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar proveedores: " + e.toString());
        }
    }

    public boolean eliminarProveedor(int id) {
        String sql = "DELETE FROM proveedor WHERE id = ?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.execute();
            
            notifyObservers(); // Notificar a las vistas del cambio
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar proveedor: " + e.toString());
            return false;
        }
    }
    
    public boolean modificarProveedor(ModeloProveedor pr) {
        String sql = "UPDATE proveedor SET ruc=?, nombre=?, telefono=?, direccion=?, razon=? WHERE id=?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pr.getRuc());
            ps.setString(2, pr.getNombre());
            ps.setInt(3, pr.getTelefono());
            ps.setString(4, pr.getDireccion());
            ps.setString(5, pr.getRazon());
            ps.setInt(6, pr.getId());
            ps.execute();
            
            notifyObservers(); // Notificar a las vistas del cambio
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar proveedor: " + e.toString());
            return false;
        }
    }
}