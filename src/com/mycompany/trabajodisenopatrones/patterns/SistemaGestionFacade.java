package com.mycompany.trabajodisenopatrones.patterns;

// Imports de todos los gestores y modelos que la fachada necesita coordinar
import com.mycompany.trabajodisenopatrones.model.ModeloVenta;
import com.mycompany.trabajodisenopatrones.model.ModeloCliente;
import com.mycompany.trabajodisenopatrones.model.ModeloUsuario;
import com.mycompany.trabajodisenopatrones.model.ModeloConfig;
import com.mycompany.trabajodisenopatrones.model.ModeloDetalleVenta;
import com.mycompany.trabajodisenopatrones.model.ModeloProducto;
import com.mycompany.trabajodisenopatrones.model.ModeloProveedor;
import com.mycompany.trabajodisenopatrones.business.GestorConfiguracion;
import com.mycompany.trabajodisenopatrones.business.GestorCliente;
import com.mycompany.trabajodisenopatrones.business.GestorProducto;
import com.mycompany.trabajodisenopatrones.business.GestorReportes;
import com.mycompany.trabajodisenopatrones.business.GestorVenta;
import com.mycompany.trabajodisenopatrones.business.GestorProveedor;
import com.mycompany.trabajodisenopatrones.business.GestorAutenticacion;
import com.mycompany.trabajodisenopatrones.model.ModeloDatosPdfVentaDTO;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

// PATRÓN ESTRUCTURAL: FACADE (Fachada)
// Propósito: Proporciona una interfaz simple y unificada a un subsistema complejo.
// La UI solo interactúa con esta fachada, lo que reduce el acoplamiento y simplifica su código.
public class SistemaGestionFacade {

    // La fachada tiene una referencia a cada gestor del subsistema de negocio.
    private GestorAutenticacion gestorAuth;
    private GestorCliente gestorCliente;
    private GestorProducto gestorProducto;
    private GestorProveedor gestorProveedor;
    private GestorVenta gestorVenta;
    private GestorConfiguracion gestorConfig;
    private GestorReportes gestorReportes;

    public SistemaGestionFacade() {
        // La fachada obtiene las instancias únicas de los gestores (que son Singletons).
        this.gestorAuth = GestorAutenticacion.getInstance();
        this.gestorCliente = GestorCliente.getInstance();
        this.gestorProducto = GestorProducto.getInstance();
        this.gestorProveedor = GestorProveedor.getInstance();
        this.gestorVenta = GestorVenta.getInstance();
        this.gestorConfig = GestorConfiguracion.getInstance();
        this.gestorReportes = GestorReportes.getInstance();

    }

    // --- MÉTODOS DE FACHADA PARA AUTENTICACIÓN Y USUARIOS --- 
    public ModeloUsuario autenticarUsuario(String correo, String contrasena) {
        return gestorAuth.validarCredenciales(correo, contrasena);
    }

    public boolean registrarNuevoUsuario(String nombre, String correo, String pass, String rol) {
        ModeloUsuario nuevoUsuario = new ModeloUsuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setPass(pass);
        nuevoUsuario.setRol(rol);
        return gestorAuth.registrarUsuario(nuevoUsuario);
    }

    // --- MÉTODOS DE FACHADA PARA CLIENTES ---
    public void cargarTablaClientes(DefaultTableModel model) {
        gestorCliente.cargarTabla(model);
    }

    public boolean agregarCliente(ModeloCliente cliente) {
        // Podríamos añadir validaciones aquí si fuera necesario
        return gestorCliente.registrarCliente(cliente);
    }

    public boolean modificarCliente(ModeloCliente cliente) {
        return gestorCliente.modificarCliente(cliente);
    }

    public void eliminarCliente(int id) {
        gestorCliente.eliminarCliente(id);
    }

    public ModeloCliente buscarClientePorDni(int dni) {
        return gestorCliente.buscarCliente(dni);
    }

    // --- MÉTODOS DE FACHADA PARA PROVEEDORES ---
    // Métodos de fachada para proveedores:
    public void cargarTablaProveedores(DefaultTableModel model) {
        gestorProveedor.cargarTabla(model);
    }

    public boolean agregarProveedor(ModeloProveedor proveedor) {
        return gestorProveedor.registrarProveedor(proveedor);
    }

    public boolean modificarProveedor(ModeloProveedor proveedor) {
        return gestorProveedor.modificarProveedor(proveedor);
    }

    public void eliminarProveedor(int id) {
        gestorProveedor.eliminarProveedor(id);
    }

    /**
     * Delega la tarea de poblar un JComboBox con los nombres de los
     * proveedores. La lógica reside en GestorProducto según el código original.
     *
     * @param comboBox El JComboBox que se va a rellenar.
     */
    public void consultarProveedores(JComboBox<String> comboBox) {
        gestorProducto.consultarProveedor(comboBox);
    }

    // --- MÉTODOS DE FACHADA PARA PRODUCTOS ---
    public void cargarTablaProductos(DefaultTableModel model) {
        gestorProducto.cargarTablaProductos(model);
    }

    public boolean agregarProducto(ModeloProducto producto) {
        return gestorProducto.registrarProducto(producto);
    }

    public boolean modificarProducto(ModeloProducto producto) {
        return gestorProducto.modificarProducto(producto);
    }

    public void eliminarProducto(int id) {
        gestorProducto.eliminarProducto(id);
    }

    public ModeloProducto buscarProductoPorCodigo(String codigo) {
        return gestorProducto.buscarProductoPorCodigo(codigo);
    }

    // --- MÉTODOS DE FACHADA PARA VENTAS ---
    public void cargarTablaVentas(DefaultTableModel model) {
        gestorVenta.cargarTablaVentas(model);
    }

    /**
     * Orquesta el proceso completo de registrar una venta.
     *
     * @param venta El objeto de la cabecera de la venta.
     * @param detalles La lista de productos vendidos.
     * @return true si la venta y sus detalles se registraron correctamente.
     */
    public int realizarVenta(ModeloVenta venta, List<ModeloDetalleVenta> detalles) {
        // La fachada coordina la operación compleja de venta.
        return gestorVenta.registrarVenta(venta, detalles);
    }

    // --- MÉTODOS DE FACHADA PARA CONFIGURACIÓN ---
    public ModeloConfig obtenerConfiguracion() {
        return gestorConfig.buscarDatos();
    }

    public boolean actualizarConfiguracion(ModeloConfig config) {
        return gestorConfig.modificarDatos(config);
    }

    public void generarReporteProductosExcel() {
        gestorReportes.generarReporteProductosExcel();
    }

    public void generarGraficoVentas(String fecha) {
        gestorReportes.generarGraficoVentasDelDia(fecha);
    }

    public void generarPdfVenta(int idVenta, int dniCliente, List<ModeloDetalleVenta> detalles, double totalVenta) {
        // 1. Obtener datos de la empresa
        ModeloConfig config = gestorConfig.buscarDatos();

        // 2. Obtener datos del cliente
        ModeloCliente cliente = gestorCliente.buscarCliente(dniCliente);

        // 3. Crear el DTO con toda la información
        ModeloDatosPdfVentaDTO datosPdf = new ModeloDatosPdfVentaDTO(config, cliente, detalles, totalVenta, idVenta);

        // 4. Llamar al gestor de reportes
        gestorReportes.generarPdfVenta(datosPdf);
    }
}
