package com.mycompany.trabajodisenopatrones.business;

import com.mycompany.trabajodisenopatrones.db.Conexion;
import com.mycompany.trabajodisenopatrones.model.ModeloDetalleVenta;
import com.mycompany.trabajodisenopatrones.model.ModeloVenta;
import com.mycompany.trabajodisenopatrones.model.ModeloVentaVendedorDTO;
import com.mycompany.trabajodisenopatrones.patterns.Observer;
import com.mycompany.trabajodisenopatrones.patterns.Subject;
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
public class GestorVenta implements Subject {

    // --- Parte Singleton ---
    private static GestorVenta instancia;

    // --- Parte Observer ---
    private List<Observer> observers;

    private GestorVenta() {
        this.observers = new ArrayList<>();
    }

    public static synchronized GestorVenta getInstance() {
        if (instancia == null) {
            instancia = new GestorVenta();
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

    // --- Métodos de Negocio ---
    /**
     * Registra una venta completa (cabecera y detalles) usando una transacción.
     *
     * @param venta El objeto ModeloVenta con los datos de la cabecera.
     * @param detalles La lista de ModeloDetalleVenta con los productos.
     * @return El ID de la venta si fue exitosa, -1 si falló.
     */
    public int registrarVenta(ModeloVenta venta, List<ModeloDetalleVenta> detalles) {
        Connection con = Conexion.getInstance().getConexion();
        int ventaId = -1;

        try {
            // Iniciar transacción
            con.setAutoCommit(false);

            // 1. Registrar la cabecera de la Venta
            String sqlVenta = "INSERT INTO ventas (cliente, vendedor, total, fecha) VALUES (?,?,?,?)";
            try ( PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setString(1, venta.getCliente());
                psVenta.setString(2, venta.getVendedor());
                psVenta.setDouble(3, venta.getTotal());
                psVenta.setString(4, venta.getFecha());
                psVenta.executeUpdate();

                try ( ResultSet generatedKeys = psVenta.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ventaId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la venta, la creación falló.");
                    }
                }
            }

            // 2. Registrar los detalles de la Venta y actualizar stock
            String sqlDetalle = "INSERT INTO detalle (cod_pro, cantidad, precio, id_venta) VALUES (?,?,?,?)";
            try ( PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
                for (ModeloDetalleVenta det : detalles) {
                    psDetalle.setString(1, det.getCod_pro());
                    psDetalle.setInt(2, det.getCantidad());
                    psDetalle.setDouble(3, det.getPrecio());
                    psDetalle.setInt(4, ventaId);
                    psDetalle.addBatch();

                    // 3. Coordinar con GestorProducto para actualizar el stock
                    GestorProducto.getInstance().actualizarStock(det.getCod_pro(), det.getCantidad());
                }
                psDetalle.executeBatch();
            }

            // Si todo fue exitoso, confirmar la transacción
            con.commit();
            notifyObservers(); // Notificar a las vistas que hay una nueva venta

        } catch (SQLException e) {
            System.err.println("Error en la transacción de venta. Revirtiendo cambios... " + e.getMessage());
            try {
                // Si algo falla, revertir todos los cambios
                con.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al intentar revertir la transacción: " + ex.getMessage());
            }
            ventaId = -1; // Indicar que la operación falló
        } finally {
            try {
                // Volver al modo de autocommit
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar auto-commit: " + e.getMessage());
            }
        }

        return ventaId;
    }

    /**
     * Carga todas las ventas en un DefaultTableModel para la vista.
     *
     * @param model El modelo de la tabla a poblar.
     */
    public void cargarTablaVentas(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT * FROM ventas";
        Connection con = Conexion.getInstance().getConexion();
        try ( Statement st = con.createStatement();  ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getString("vendedor"),
                    rs.getDouble("total")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar las ventas: " + e.toString());
        }
    }

    /**
     * Obtiene el total de ventas agrupado por vendedor para una fecha
     * específica.
     *
     * @param fecha La fecha en formato "dd/MM/yyyy".
     * @return Una lista de DTOs con los datos para el gráfico.
     */
    public List<ModeloVentaVendedorDTO> listarVentasPorVendedorParaGrafico(String fecha) {
        List<ModeloVentaVendedorDTO> listaDatos = new ArrayList<>();
        // Consulta SQL que agrupa las ventas por vendedor y suma sus totales.
        String sql = "SELECT vendedor, SUM(total) as total_vendido FROM ventas WHERE fecha = ? GROUP BY vendedor";
        Connection con = Conexion.getInstance().getConexion();

        try ( PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaDatos.add(new ModeloVentaVendedorDTO(
                            rs.getString("vendedor"),
                            rs.getDouble("total_vendido")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener datos para el gráfico: " + e.toString());
        }
        return listaDatos;
    }
}
