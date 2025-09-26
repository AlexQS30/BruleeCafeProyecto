/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sistemasoftware.business;
 

import com.mycompany.sistemasoftware.db.Conexion;
import com.mycompany.sistemasoftware.model.ModeloEmpresa;
import com.mycompany.sistemasoftware.patterns.Observer;
import com.mycompany.sistemasoftware.patterns.Subject;
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
    public ModeloEmpresa buscarDatos() {
        ModeloEmpresa empresa = null;
        String sql = "SELECT * FROM empresa LIMIT 1"; // Asumimos que solo hay una fila
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                empresa = new ModeloEmpresa();
                empresa.setId(rs.getInt("id"));
                empresa.setRuc(rs.getString("ruc"));
                empresa.setRepresentanteLegal(rs.getString("representantelegal"));
                empresa.setTelefono(rs.getInt("telefono"));
                empresa.setDireccion(rs.getString("direccion"));
                empresa.setRazonSocial(rs.getString("razonsocial"));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar datos de configuración: " + e.toString());
        }
        return empresa;
    }

    /**
     * Modifica los datos de configuración de la empresa.
     * @param empresa El objeto ModeloConfig con los nuevos datos.
     * @return true si la modificación fue exitosa.
     */
    public boolean modificarDatosEmpresa(ModeloEmpresa empresa) {
        String sql = "UPDATE empresa SET ruc=?, representantelegal=?, telefono=?, direccion=?, razonsocial=? WHERE id=?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empresa.getRuc());
            ps.setString(2, empresa.getRepresentanteLegal());
            ps.setInt(3, empresa.getTelefono());
            ps.setString(4, empresa.getDireccion());
            ps.setString(5, empresa.getRazonSocial());
            ps.setInt(6, empresa.getId());
            ps.execute();
            notifyObservers(); // Notificar a las vistas del cambio
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar datos de la empresa: " + e.toString());
            return false;
        }
    }
}