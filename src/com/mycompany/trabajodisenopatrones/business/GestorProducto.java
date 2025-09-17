package com.mycompany.trabajodisenopatrones.business;

import com.mycompany.trabajodisenopatrones.db.Conexion;
import com.mycompany.trabajodisenopatrones.model.ModeloProducto;
import com.mycompany.trabajodisenopatrones.model.ModeloProductoSimple;
import com.mycompany.trabajodisenopatrones.patterns.Observer;
import com.mycompany.trabajodisenopatrones.patterns.Subject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

// IMPLEMENTA SINGLETON y es un SUBJECT del patrón OBSERVER
public class GestorProducto implements Subject {

    // --- Parte Singleton ---
    private static GestorProducto instancia;
    
    // --- Parte Observer ---
    private List<Observer> observers;
    
    // El constructor es privado para el patrón Singleton
    private GestorProducto() {
        this.observers = new ArrayList<>();
    }

    // Método estático para obtener la única instancia de la clase
    public static synchronized GestorProducto getInstance() {
        if (instancia == null) {
            instancia = new GestorProducto();
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
    
    // --- Métodos de Negocio (Lógica que antes estaba en ProductosDao) ---

    public boolean registrarProducto(ModeloProducto pro) {
        String sql = "INSERT INTO productos (codigo, nombre, proveedor, stock, precio) VALUES (?,?,?,?,?)";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setString(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setDouble(5, pro.getPrecio());
            ps.execute();
            notifyObservers(); // Notificar que se agregó un producto
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar producto: " + e.toString());
            return false;
        }
    }
    
    public void consultarProveedor(JComboBox<String> proveedorComboBox) {
        String sql = "SELECT nombre FROM proveedor";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                proveedorComboBox.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar proveedores: " + e.toString());
        }
    }
    
    public void cargarTablaProductos(DefaultTableModel model) {
        model.setRowCount(0); // Limpiar la tabla antes de cargar
        String sql = "SELECT * FROM productos";
        Connection con = Conexion.getInstance().getConexion();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("proveedor"),
                    rs.getInt("stock"),
                    rs.getDouble("precio")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar productos: " + e.toString());
        }
    }

    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.execute();
            notifyObservers(); // Notificar que se eliminó un producto
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar producto: " + e.toString());
            return false;
        }
    }
    
    public boolean modificarProducto(ModeloProducto pro) {
        String sql = "UPDATE productos SET codigo = ?, nombre = ?, proveedor = ?, stock = ?, precio = ? WHERE id = ?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setString(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setDouble(5, pro.getPrecio());
            ps.setInt(6, pro.getId());
            ps.execute();
            notifyObservers(); // Notificar que se modificó un producto
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar producto: " + e.toString());
            return false;
        }
    }

    public ModeloProducto buscarProductoPorCodigo(String cod) {
        ModeloProducto producto = null;
        String sql = "SELECT * FROM productos WHERE codigo = ?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cod);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto = new ModeloProducto();
                    producto.setId(rs.getInt("id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setStock(rs.getInt("stock"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.toString());
        }
        return producto;
    }
    
    /**
     * Método crucial para actualizar el stock de un producto después de una venta.
     * @param cod Código del producto.
     * @param cantidadVendida La cantidad que se vendió.
     * @return true si la actualización fue exitosa.
     */
    public boolean actualizarStock(String cod, int cantidadVendida) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE codigo = ?";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidadVendida);
            ps.setString(2, cod);
            ps.execute();
            // No notificamos aquí directamente para evitar notificaciones múltiples
            // durante una venta. La notificación principal la hará GestorVenta.
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.toString());
            return false;
        }
    }
    
    /**
     * Devuelve una lista simplificada de productos para ser usada en reportes.
     * @return Una lista de objetos ModeloProductoSimple.
     */
    public List<ModeloProductoSimple> listarProductosParaReporte() {
        List<ModeloProductoSimple> listaReporte = new ArrayList<>();
        String sql = "SELECT codigo, nombre, precio, stock FROM productos";
        Connection con = Conexion.getInstance().getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaReporte.add(new ModeloProductoSimple(
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos para reporte: " + e.toString());
        }
        return listaReporte;
    }
}