/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trabajodisenopatrones.business;
 

import com.mycompany.trabajodisenopatrones.db.Conexion;
import com.mycompany.trabajodisenopatrones.model.ModeloConfig;
import com.mycompany.trabajodisenopatrones.patterns.Observer;
import com.mycompany.trabajodisenopatrones.patterns.Subject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

// IMPLEMENTA SINGLETON y es un SUBJECT del patrón OBSERVER
public class GestorConfiguracion implements Subject {

    // --- Parte Singleton ---
    private static GestorConfiguracion instancia;
    
    // --- Parte Observer ---
    private List<Observer> observers;
    
    private GestorConfiguracion() {
        this.observers = new ArrayList<>();
    }

    public static synchronized GestorConfiguracion getInstance() {
        if (instancia == null) {
            instancia = new GestorConfiguracion();
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

    // --- Métodos de Negocio (Lógica que antes estaba en ProductosDao) ---

    /**
     * Busca los datos de configuración de la empresa.
     * Asume que solo hay una fila de configuración, usualmente con id=1.
     * @return Un objeto ModeloConfig con los datos, o null si hay un error.
     */
    public ModeloConfig buscarDatos() {
        ModeloConfig config = null;
        String sql = "SELECT * FROM config LIMIT 1"; // Asumimos que solo hay una fila
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                config = new ModeloConfig();
                config.setId(rs.getInt("id"));
                config.setRuc(rs.getInt("ruc"));
                config.setNombre(rs.getString("nombre"));
                config.setTelefono(rs.getInt("telefono"));
                config.setDireccion(rs.getString("direccion"));
                config.setRazon(rs.getString("razon"));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar datos de configuración: " + e.toString());
        }
        return config;
    }

    /**
     * Modifica los datos de configuración de la empresa.
     * @param config El objeto ModeloConfig con los nuevos datos.
     * @return true si la modificación fue exitosa.
     */
    public boolean modificarDatos(ModeloConfig config) {
        String sql = "UPDATE config SET ruc=?, nombre=?, telefono=?, direccion=?, razon=? WHERE id=?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, config.getRuc());
            ps.setString(2, config.getNombre());
            ps.setInt(3, config.getTelefono());
            ps.setString(4, config.getDireccion());
            ps.setString(5, config.getRazon());
            ps.setInt(6, config.getId());
            ps.execute();
            notifyObservers(); // Notificar a las vistas del cambio
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar datos de la empresa: " + e.toString());
            return false;
        }
    }
}