/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sistemasoftware.business; 

import com.mycompany.sistemasoftware.db.Conexion;
import com.mycompany.sistemasoftware.model.ModeloCliente;
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
public class GestorCliente implements Subject {

    // --- Parte Singleton ---
    private static GestorCliente instancia;
    
    // --- Parte Observer ---
    private List<Observer> observers;
    
    // Constructor privado para el patrón Singleton
    private GestorCliente() {
        this.observers = new ArrayList<>();
    }

    // Método estático para obtener la única instancia de la clase
    public static synchronized GestorCliente getInstance() {
        if (instancia == null) {
            instancia = new GestorCliente();
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
        for (Observer observer : observers) {
            observer.update();
        }
    }
    
    // --- Métodos de Negocio (Lógica que antes estaba en ClienteDao) ---

    public boolean registrarCliente(ModeloCliente cl) {
        String sql = "INSERT INTO clientes (dni, nombre, telefono, direccion, razon) VALUES (?,?,?,?,?)";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cl.getDni());
            ps.setString(2, cl.getNombre());
            ps.setInt(3, cl.getTelefono());
            ps.setString(4, cl.getDireccion());
            ps.setString(5, cl.getRazon());
            ps.execute();
            notifyObservers(); // Notificar a las vistas del cambio
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar cliente: " + e.toString());
            return false;
        }
    }
    
    public void cargarTabla(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT * FROM clientes";
        Connection con = Conexion.getInstance().getConexion();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("dni"),
                    rs.getString("nombre"),
                    rs.getInt("telefono"),
                    rs.getString("direccion"),
                    rs.getString("razon")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar clientes: " + e.toString());
        }
    }

    public boolean eliminarCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.execute();
            notifyObservers(); // Notificar a las vistas del cambio
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar cliente: " + e.toString());
            return false;
        }
    }
    
    public boolean modificarCliente(ModeloCliente cl) {
        String sql = "UPDATE clientes SET dni=?, nombre=?, telefono=?, direccion=?, razon=? WHERE id=?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cl.getDni());
            ps.setString(2, cl.getNombre());
            ps.setInt(3, cl.getTelefono());
            ps.setString(4, cl.getDireccion());
            ps.setString(5, cl.getRazon());
            ps.setInt(6, cl.getId());
            ps.execute();
            notifyObservers(); // Notificar a las vistas del cambio
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar cliente: " + e.toString());
            return false;
        }
    }
    
    public ModeloCliente buscarCliente(int dni) {
        ModeloCliente cl = null; // Inicializar a null para saber si se encontró
        String sql = "SELECT * FROM clientes WHERE dni = ?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cl = new ModeloCliente();
                    cl.setId(rs.getInt("id"));
                    cl.setDni(rs.getInt("dni"));
                    cl.setNombre(rs.getString("nombre"));
                    cl.setTelefono(rs.getInt("telefono"));
                    cl.setDireccion(rs.getString("direccion"));
                    cl.setRazon(rs.getString("razon"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar cliente: " + e.toString());
        }
        return cl;
    }
}