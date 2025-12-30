/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.Auditoria;
import controlador.GeneradorReportes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import modelo.ConexionDB;
import modelo.Usuario_Sesion;
import util.ExportadorExcel;

/**
 *
 * @author piper
 */
public class FrmReportes extends javax.swing.JInternalFrame {

    // Componentes principales
    private final GeneradorReportes reportes;
    private final DefaultTableModel modeloTabla;
    private final SimpleDateFormat formatoFecha;
    private final SimpleDateFormat formatoFechaHora;

    // Constantes para tipos de reporte
    private static final int REPORTE_VENTAS_DIA = 0;
    private static final int REPORTE_VENTAS_RANGO = 1;
    private static final int REPORTE_PRODUCTOS_VENDIDOS = 2;
    private static final int REPORTE_INGRESOS_MENSUALES = 3;
    private static final int REPORTE_DETALLE_VENTA = 4;
    private static final int REPORTE_VENTAS_CLIENTE = 5;

    /**
     * Creates new form FrmReportes
     */
    public FrmReportes() {

        super("Reportes", true, true, true, true); // Título, cerrable, redimensionable, movible, maximizable

        initComponents();

        // Inicializar componentes
        this.reportes = new GeneradorReportes();
        this.formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        this.formatoFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Configurar tabla
        this.modeloTabla = new DefaultTableModel();
        jTable1.setModel(modeloTabla);
        jTable1.setDefaultEditor(Object.class, null);

        //Configurar fechas por defecto
        jDateChooser2.setDate(new Date());
        jDateChooser3.setDate(new Date());

        inicializarEstadisticas();

        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "Reportes",
                    "Accedió al módulo de Reportes"
            );
            System.out.println("Acceso registrado");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    private void inicializarEstadisticas() {
        try {
            // Obtener estadísticas del día
            Map<String, Object> resumenHoy = reportes.obtenerResumenVentasDelDia();

            // Actualizar ventas de hoy con manejo seguro
            Object totalVentasObj = resumenHoy.get("total_ventas");
            Object totalIngresosObj = resumenHoy.get("total_ingresos");

            int totalVentas = 0;
            double totalIngresos = 0.0;

            if (totalVentasObj != null) {
                totalVentas = ((Number) totalVentasObj).intValue();
            }

            if (totalIngresosObj != null) {
                totalIngresos = ((Number) totalIngresosObj).doubleValue();
            }

            jLabel_cantidad_ventas.setText(String.valueOf(totalVentas));
            jLabel_total_ventas.setText(String.format("$%,.2f", totalIngresos));

            // Obtener total de productos
            int totalProductos = reportes.obtenerTotalProductos();
            jLabel_cant_productos.setText(String.valueOf(totalProductos));
            System.out.println("Total productos: " + totalProductos);

            // Obtener productos con stock bajo
            int productosStockBajo = reportes.obtenerProductosStockBajo();
            jLabel_cant_stock_bajo.setText(String.valueOf(productosStockBajo));
            System.out.println("Productos stock bajo: " + productosStockBajo);

            // Obtener total de clientes
            int totalClientes = reportes.obtenerTotalClientes();
            jLabel_cant_clientes.setText(String.valueOf(totalClientes));
            System.out.println("Total clientes: " + totalClientes);

            System.out.println("✓ Estadísticas cargadas correctamente");

        } catch (Exception e) {
            System.err.println("✗ Error al cargar estadísticas: " + e.getMessage());
            e.printStackTrace();

            // Establecer valores por defecto
            jLabel_cantidad_ventas.setText("0");
            jLabel_total_ventas.setText("$0.00");
            jLabel_cant_productos.setText("0");
            jLabel_cant_stock_bajo.setText("0");
            jLabel_cant_clientes.setText("0");
        }
    }

    //Muestra el detalle de productos con stock bajo
    private void mostrarDetalleStockBajo() {
        ArrayList<Map<String, Object>> productos = reportes.obtenerListaProductosStockBajo(10);

        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay productos con stock bajo.\n\n¡Excelente! Todos los productos tienen stock suficiente.",
                    "Stock Saludable",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("═══════════════════════════════════════════════════════\n");
        mensaje.append("           PRODUCTOS CON STOCK BAJO (<10 unidades)\n");
        mensaje.append("═══════════════════════════════════════════════════════\n\n");

        for (Map<String, Object> producto : productos) {
            mensaje.append(String.format("Código: %s\n", producto.get("codigo")));
            mensaje.append(String.format("Nombre: %s\n", producto.get("nombre")));
            mensaje.append(String.format("Talla: %s | Color: %s\n",
                    producto.get("talla"),
                    producto.get("color")));
            mensaje.append(String.format("Stock actual: %d unidades\n",
                    producto.get("cantidad")));
            mensaje.append(String.format("Precio: $%.2f\n",
                    producto.get("precio")));
            mensaje.append("───────────────────────────────────────────────────────\n");
        }

        mensaje.append(String.format("\nTotal productos con stock bajo: %d", productos.size()));

        // Crea un JTextArea para mostrar el mensaje con scroll
        JTextArea textArea = new JTextArea(mensaje.toString());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", 0, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 400));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Detalle de Stock Bajo",
                JOptionPane.WARNING_MESSAGE);

        // Registra consulta en auditoría
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "Reportes",
                    "Consultó productos con stock bajo"
            );
        } catch (Exception e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }

    public int obtenerTotalProductos() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            String sql = "SELECT COUNT(*) as total FROM productos WHERE estado = 1";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener total de productos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return 0;
    }

    //Obtiene la cantidad de productos con stock bajo (menos de 10 unidades)
    public int obtenerProductosStockBajo() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            String sql = "SELECT COUNT(*) as total FROM productos WHERE estado = 1 AND stock < 10";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos con stock bajo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return 0;
    }

    //Obtiene el total de clientes registrados
    public int obtenerTotalClientes() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            String sql = "SELECT COUNT(*) as total FROM clientes WHERE estado = 1";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener total de clientes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return 0;
    }

    // MÉTODOS PARA CONFIGURAR COLUMNAS DINÁMICAS
    //Configura las columnas según el tipo de reporte
    private void configurarColumnasTabla(int tipoReporte) {
        limpiarTabla();

        switch (tipoReporte) {
            case REPORTE_VENTAS_DIA:
                configurarColumnasVentasDelDia();
                break;
            case REPORTE_VENTAS_RANGO:
                configurarColumnasVentasPorRango();
                break;
            case REPORTE_PRODUCTOS_VENDIDOS:
                configurarColumnasProductosMasVendidos();
                break;
            case REPORTE_INGRESOS_MENSUALES:
                configurarColumnasIngresosMensuales();
                break;
            case REPORTE_DETALLE_VENTA:
                configurarColumnasDetalleVenta();
                break;
            case REPORTE_VENTAS_CLIENTE:
                configurarColumnasVentasPorCliente();
                break;
        }
    }

    private void configurarColumnasVentasDelDia() {
        String[] columnas = {"ID", "Fecha", "Cliente", "Subtotal", "Total", "Método Pago", "Estado"};
        int[] anchos = {50, 120, 180, 100, 100, 120, 80};
        configurarColumnas(columnas, anchos);
    }

    private void configurarColumnasVentasPorRango() {
        String[] columnas = {"ID", "Fecha", "Cliente", "Total", "Método Pago", "Estado"};
        int[] anchos = {50, 120, 200, 100, 120, 80};
        configurarColumnas(columnas, anchos);
    }

    private void configurarColumnasProductosMasVendidos() {
        String[] columnas = {"Código", "Nombre", "Talla", "Color", "Precio", "Cant. Vendida", "Total Ingresos"};
        int[] anchos = {100, 200, 60, 80, 80, 100, 120};
        configurarColumnas(columnas, anchos);
    }

    private void configurarColumnasIngresosMensuales() {
        String[] columnas = {"Mes", "Nombre Mes", "Total Ventas", "Subtotal", "Total Ingresos"};
        int[] anchos = {50, 120, 100, 120, 120};
        configurarColumnas(columnas, anchos);
    }

    private void configurarColumnasDetalleVenta() {
        String[] columnas = {"Código", "Nombre", "Talla", "Color", "Cantidad", "Precio Unit.", "Subtotal"};
        int[] anchos = {100, 200, 60, 80, 80, 100, 100};
        configurarColumnas(columnas, anchos);
    }

    private void configurarColumnasVentasPorCliente() {
        String[] columnas = {"ID", "Fecha", "Subtotal", "Total", "Método Pago", "# Productos"};
        int[] anchos = {50, 120, 100, 100, 120, 80};
        configurarColumnas(columnas, anchos);
    }

    private void configurarColumnas(String[] nombres, int[] anchos) {
        modeloTabla.setColumnIdentifiers(nombres);
        for (int i = 0; i < anchos.length; i++) {
            jTable1.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    // GENERACIÓN DE REPORTES
    private void generarReporte() {
        int tipoReporte = jComboBox1_tipo_reportes.getSelectedIndex();

        try {
            switch (tipoReporte) {
                case REPORTE_VENTAS_DIA:
                    generarReporteVentasDelDia();
                    break;
                case REPORTE_VENTAS_RANGO:
                    generarReporteVentasPorRango();
                    break;
                case REPORTE_PRODUCTOS_VENDIDOS:
                    generarReporteProductosMasVendidos();
                    break;
                case REPORTE_INGRESOS_MENSUALES:
                    generarReporteIngresosMensuales();
                    break;
                case REPORTE_DETALLE_VENTA:
                    generarReporteDetalleVenta();
                    break;
                case REPORTE_VENTAS_CLIENTE:
                    generarReporteVentasPorCliente();
                    break;
                default:
                    mostrarError("Tipo de reporte no válido");
            }
        } catch (Exception e) {
            mostrarError("Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generarReporteVentasDelDia() {
        configurarColumnasTabla(REPORTE_VENTAS_DIA);

        ArrayList<Map<String, Object>> ventas = reportes.obtenerVentasDelDia();

        if (ventas.isEmpty()) {
            mostrarInfo("No hay ventas registradas para el día de hoy");
            return;
        }

        for (Map<String, Object> venta : ventas) {
            agregarFilaVenta(venta, true);
        }

        mostrarResumenVentasDelDia();
    }

    private void generarReporteVentasPorRango() {
        Date fechaInicio = jDateChooser2.getDate();
        Date fechaFin = jDateChooser3.getDate();

        if (!validarFechas(fechaInicio, fechaFin)) {
            return;
        }

        configurarColumnasTabla(REPORTE_VENTAS_RANGO);

        ArrayList<Map<String, Object>> ventas = reportes.obtenerVentasPorRango(fechaInicio, fechaFin);

        if (ventas.isEmpty()) {
            mostrarInfo("No hay ventas en el rango de fechas seleccionado");
            return;
        }

        for (Map<String, Object> venta : ventas) {
            agregarFilaVenta(venta, false);
        }

        mostrarResumenVentasPorRango(fechaInicio, fechaFin);
    }

    private void generarReporteProductosMasVendidos() {
        configurarColumnasTabla(REPORTE_PRODUCTOS_VENDIDOS);

        ArrayList<Map<String, Object>> productos = reportes.obtenerProductosMasVendidos(10);

        if (productos.isEmpty()) {
            mostrarInfo("No hay productos vendidos registrados");
            return;
        }

        for (Map<String, Object> producto : productos) {
            agregarFilaProducto(producto);
        }
    }

    private void generarReporteIngresosMensuales() {
        configurarColumnasTabla(REPORTE_INGRESOS_MENSUALES);

        ArrayList<Map<String, Object>> meses = reportes.obtenerIngresosMensuales();

        if (meses.isEmpty()) {
            mostrarInfo("No hay ingresos registrados para este año");
            return;
        }

        for (Map<String, Object> mes : meses) {
            agregarFilaMes(mes);
        }
    }

    private void generarReporteDetalleVenta() {
        String numeroFactura = solicitarNumeroFactura();

        if (numeroFactura == null || numeroFactura.trim().isEmpty()) {
            return;
        }

        int ventaId = reportes.obtenerVentaIdPorNumeroFactura(numeroFactura);

        if (ventaId == -1) {
            mostrarAdvertencia("No se encontró una venta con el número de factura: " + numeroFactura
                    + "\n\nVerifica que el número sea correcto."
                    + "\nEjemplos: 1, 23, 456 o FVENTA-00001");
            return;
        }

        configurarColumnasTabla(REPORTE_DETALLE_VENTA);

        ArrayList<Map<String, Object>> productos = reportes.obtenerProductosVenta(ventaId);

        if (productos.isEmpty()) {
            mostrarAdvertencia("No se encontraron productos para la factura: " + numeroFactura);
            return;
        }

        for (Map<String, Object> producto : productos) {
            agregarFilaProductoDetalle(producto);
        }

        mostrarDetalleVenta(numeroFactura);
    }

    private void generarReporteVentasPorCliente() {
        String cliente = solicitarNombreCliente();

        if (cliente == null || cliente.trim().isEmpty()) {
            return;
        }

        configurarColumnasTabla(REPORTE_VENTAS_CLIENTE);

        ArrayList<Map<String, Object>> ventas = reportes.obtenerVentasPorCliente(cliente);

        if (ventas.isEmpty()) {
            mostrarInfo("No se encontraron ventas para el cliente: " + cliente);
            return;
        }

        for (Map<String, Object> venta : ventas) {
            agregarFilaVentaCliente(venta);
        }

        mostrarResumenCliente(cliente);
    }

    // MÉTODOS PARA AGREGAR FILAS A LA TABLA
    private void agregarFilaVenta(Map<String, Object> venta, boolean incluirSubtotal) {
        Object fecha = venta.get("fecha");
        String fechaStr = fecha != null ? formatoFechaHora.format(fecha) : "N/A";

        if (incluirSubtotal) {
            modeloTabla.addRow(new Object[]{
                venta.get("id"),
                fechaStr,
                venta.get("cliente"),
                formatearMoneda(venta.get("subtotal")),
                formatearMoneda(venta.get("total")),
                venta.get("metodo_pago"),
                venta.get("estado")
            });
        } else {
            modeloTabla.addRow(new Object[]{
                venta.get("id"),
                fechaStr,
                venta.get("cliente"),
                formatearMoneda(venta.get("total")),
                venta.get("metodo_pago"),
                venta.get("estado")
            });
        }
    }

    private void agregarFilaProducto(Map<String, Object> producto) {
        modeloTabla.addRow(new Object[]{
            producto.get("codigo"),
            producto.get("nombre"),
            producto.get("talla"),
            producto.get("color"),
            formatearMoneda(producto.get("precio")),
            producto.get("cantidad_vendida"),
            formatearMoneda(producto.get("total_ingresos"))
        });
    }

    private void agregarFilaMes(Map<String, Object> mes) {
        modeloTabla.addRow(new Object[]{
            mes.get("mes"),
            mes.get("nombre_mes"),
            mes.get("total_ventas"),
            formatearMoneda(mes.get("total_subtotal")),
            formatearMoneda(mes.get("total_ingresos"))
        });
    }

    private void agregarFilaProductoDetalle(Map<String, Object> producto) {
        modeloTabla.addRow(new Object[]{
            producto.get("codigo"),
            producto.get("nombre"),
            producto.get("talla"),
            producto.get("color"),
            producto.get("cantidad"),
            formatearMoneda(producto.get("precio_unitario")),
            formatearMoneda(producto.get("subtotal"))
        });
    }

    private void agregarFilaVentaCliente(Map<String, Object> venta) {
        Object fecha = venta.get("fecha");
        String fechaStr = fecha != null ? formatoFechaHora.format(fecha) : "N/A";

        modeloTabla.addRow(new Object[]{
            venta.get("id"),
            fechaStr,
            formatearMoneda(venta.get("subtotal")),
            formatearMoneda(venta.get("total")),
            venta.get("metodo_pago"),
            venta.get("cantidad_productos")
        });
    }

    // MÉTODOS PARA MOSTRAR RESÚMENES
    private void mostrarResumenVentasDelDia() {
        Map<String, Object> resumen = reportes.obtenerResumenVentasDelDia();

        String mensaje = String.format(
                "═══════════════════════════════════════\n"
                + "RESUMEN VENTAS DEL DÍA\n"
                + "═══════════════════════════════════════\n\n"
                + "Total de ventas:      %d\n"
                + "Subtotal:            $%.2f\n"
                + "Descuento:           $%.2f\n"
                + "Total ingresos:      $%.2f\n"
                + "Promedio por venta:  $%.2f",
                resumen.get("total_ventas"),
                resumen.get("total_subtotal"),
                resumen.get("total_descuento"),
                resumen.get("total_ingresos"),
                resumen.get("promedio_venta")
        );

        JOptionPane.showMessageDialog(this, mensaje, "Resumen del Día", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarResumenVentasPorRango(Date fechaInicio, Date fechaFin) {
        Map<String, Object> resumen = reportes.obtenerResumenVentasPorRango(fechaInicio, fechaFin);

        String mensaje = String.format(
                "═══════════════════════════════════════\n"
                + "RESUMEN VENTAS POR RANGO\n"
                + "═══════════════════════════════════════\n\n"
                + "Período: %s al %s\n\n"
                + "Total de ventas:      %d\n"
                + "Total ingresos:      $%.2f\n"
                + "Promedio por venta:  $%.2f\n"
                + "Venta mayor:         $%.2f\n"
                + "Venta menor:         $%.2f",
                formatoFecha.format(fechaInicio),
                formatoFecha.format(fechaFin),
                resumen.get("total_ventas"),
                resumen.get("total_ingresos"),
                resumen.get("promedio_venta"),
                resumen.get("venta_mayor"),
                resumen.get("venta_menor")
        );

        JOptionPane.showMessageDialog(this, mensaje, "Resumen por Rango", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDetalleVenta(String numeroFactura) {
        Map<String, Object> venta = reportes.obtenerVentaPorNumeroFactura(numeroFactura);

        if (venta.isEmpty()) {
            mostrarError("No se pudo obtener el detalle de la venta.");
            return;
        }

        String mensaje = String.format(
                "═══════════════════════════════════════\n"
                + "DETALLE DE VENTA #%s\n"
                + "═══════════════════════════════════════\n\n"
                + "Fecha:           %s\n"
                + "Cliente:         %s\n"
                + "Documento:       %s %s\n"
                + "Método de pago:  %s\n"
                + "Estado:          %s\n\n"
                + "Subtotal:        $%.2f\n"
                + "Descuento:       $%.2f\n"
                + "Total:           $%.2f\n\n"
                + "Factura:         %s\n"
                + "Usuario:         %s",
                venta.get("id"),
                formatoFechaHora.format(venta.get("fecha")),
                venta.get("cliente"),
                obtenerValorSeguro(venta, "tipo_documento"),
                obtenerValorSeguro(venta, "numero_documento"),
                venta.get("metodo_pago"),
                venta.get("estado"),
                venta.get("subtotal"),
                venta.get("descuento"),
                venta.get("total"),
                venta.get("numero_factura"),
                venta.get("usuario")
        );

        JOptionPane.showMessageDialog(this, mensaje, "Detalle de Venta", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarResumenCliente(String cliente) {
        Map<String, Object> resumen = reportes.obtenerResumenCliente(cliente);

        if (resumen.isEmpty()) {
            return;
        }

        String primeraCompra = resumen.get("primera_compra") != null
                ? formatoFecha.format(resumen.get("primera_compra"))
                : "N/A";
        String ultimaCompra = resumen.get("ultima_compra") != null
                ? formatoFecha.format(resumen.get("ultima_compra"))
                : "N/A";

        String mensaje = String.format(
                "═══════════════════════════════════════\n"
                + "RESUMEN DEL CLIENTE\n"
                + "═══════════════════════════════════════\n\n"
                + "Cliente:             %s\n\n"
                + "Total compras:       %d\n"
                + "Total gastado:       $%.2f\n"
                + "Promedio compra:     $%.2f\n"
                + "Compra mayor:        $%.2f\n"
                + "Compra menor:        $%.2f\n\n"
                + "Primera compra:      %s\n"
                + "Última compra:       %s",
                cliente,
                resumen.get("total_compras"),
                resumen.get("total_gastado"),
                resumen.get("promedio_compra"),
                resumen.get("compra_mayor"),
                resumen.get("compra_menor"),
                primeraCompra,
                ultimaCompra
        );

        JOptionPane.showMessageDialog(this, mensaje, "Resumen del Cliente", JOptionPane.INFORMATION_MESSAGE);
    }

    // MÉTODOS AUXILIARES
    private boolean validarFechas(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            mostrarAdvertencia("Por favor selecciona ambas fechas");
            return false;
        }

        if (fechaInicio.after(fechaFin)) {
            mostrarError("La fecha de inicio no puede ser mayor a la fecha fin");
            return false;
        }

        return true;
    }

    private String solicitarNumeroFactura() {
        return JOptionPane.showInputDialog(this,
                "Ingresa el número de factura:\n\n"
                + "Puedes ingresar:\n"
                + "  • Solo el número: 1, 23, 456\n"
                + "  • O el formato completo: FVENTA-00001",
                "Número de Factura",
                JOptionPane.QUESTION_MESSAGE);
    }

    private String solicitarNombreCliente() {
        return JOptionPane.showInputDialog(this,
                "Ingresa el nombre del cliente:",
                "Buscar Cliente",
                JOptionPane.QUESTION_MESSAGE);
    }

    private void actualizarVisibilidadFiltros() {
        int tipo = jComboBox1_tipo_reportes.getSelectedIndex();
        boolean mostrarFechas = (tipo == REPORTE_VENTAS_RANGO);

        jLabel3.setVisible(mostrarFechas);
        jLabel4.setVisible(mostrarFechas);
        jLabel5.setVisible(mostrarFechas);
        jDateChooser2.setVisible(mostrarFechas);
        jDateChooser3.setVisible(mostrarFechas);
    }

    private void limpiarTabla() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
    }

    private String formatearMoneda(Object valor) {
        if (valor == null) {
            return "$0.00";
        }
        try {
            double monto = (valor instanceof Double) ? (Double) valor : Double.parseDouble(valor.toString());
            return String.format("$%.2f", monto);
        } catch (Exception e) {
            return "$0.00";
        }
    }

    private String obtenerValorSeguro(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        return valor != null ? valor.toString() : "";
    }

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private boolean validarDatosTabla() {
        if (modeloTabla.getRowCount() == 0) {
            mostrarAdvertencia("No hay datos. Genera un reporte primero.");
            return false;
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1_tipo_reportes = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton_imprimir = new javax.swing.JButton();
        jButton_exportar = new javax.swing.JButton();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jLabel_logo = new javax.swing.JLabel();
        jButton_generar = new javax.swing.JButton();
        jPanel_estadisticas = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel_cantidad_ventas = new javax.swing.JLabel();
        jLabel_total_ventas = new javax.swing.JLabel();
        jPanel_total_productos = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel_cant_productos = new javax.swing.JLabel();
        jPanel_stock_bajo = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel_cant_stock_bajo = new javax.swing.JLabel();
        jPanel_total_clientes = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel_cant_clientes = new javax.swing.JLabel();

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Lucida Sans", 1, 24)); // NOI18N
        jLabel1.setText("Reportes");

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel2.setText("Seleccione un tipo de reporte");

        jComboBox1_tipo_reportes.setBackground(new java.awt.Color(255, 255, 255));
        jComboBox1_tipo_reportes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ventas del día", "Ventas por rango de fechas", "Productos más vendidos", "Ingresos mensuales", "Detalle de venta específica", "Ventas por cliente" }));
        jComboBox1_tipo_reportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1_tipo_reportesActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel3.setText("Filtros");

        jLabel4.setFont(new java.awt.Font("Lucida Sans", 1, 12)); // NOI18N
        jLabel4.setText("Fecha inicio:");

        jLabel5.setFont(new java.awt.Font("Lucida Sans", 1, 12)); // NOI18N
        jLabel5.setText("Fecha fin:");

        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton_imprimir.setBackground(new java.awt.Color(211, 47, 47));
        jButton_imprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/imprimir.png"))); // NOI18N
        jButton_imprimir.setText("IMPRIMIR");
        jButton_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprimirActionPerformed(evt);
            }
        });

        jButton_exportar.setBackground(new java.awt.Color(56, 142, 60));
        jButton_exportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exportar.png"))); // NOI18N
        jButton_exportar.setText("EXPORTAR");
        jButton_exportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_exportarActionPerformed(evt);
            }
        });

        jDateChooser2.setBackground(new java.awt.Color(255, 255, 255));

        jDateChooser3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/logo_pequeño.png"))); // NOI18N
        jLabel_logo.setMaximumSize(new java.awt.Dimension(150, 269));
        jLabel_logo.setMinimumSize(new java.awt.Dimension(150, 269));

        jButton_generar.setBackground(new java.awt.Color(25, 118, 210));
        jButton_generar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/busqueda.png"))); // NOI18N
        jButton_generar.setText("GENERAR");
        jButton_generar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarActionPerformed(evt);
            }
        });

        jPanel_estadisticas.setBackground(new java.awt.Color(0, 153, 51));
        jPanel_estadisticas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel_estadisticas.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/ventas.png"))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel7.setText("Ventas Hoy ");

        jLabel_total_ventas.setText("0");

        javax.swing.GroupLayout jPanel_estadisticasLayout = new javax.swing.GroupLayout(jPanel_estadisticas);
        jPanel_estadisticas.setLayout(jPanel_estadisticasLayout);
        jPanel_estadisticasLayout.setHorizontalGroup(
            jPanel_estadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_estadisticasLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(53, 53, 53))
            .addGroup(jPanel_estadisticasLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(jPanel_estadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel_total_ventas)
                    .addComponent(jLabel_cantidad_ventas))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_estadisticasLayout.setVerticalGroup(
            jPanel_estadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_estadisticasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_estadisticasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel_cantidad_ventas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel_total_ventas)
                .addGap(22, 22, 22))
        );

        jPanel_total_productos.setBackground(new java.awt.Color(51, 102, 255));
        jPanel_total_productos.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/producto.png"))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel9.setText("Total Productos");

        jLabel_cant_productos.setText("0");

        javax.swing.GroupLayout jPanel_total_productosLayout = new javax.swing.GroupLayout(jPanel_total_productos);
        jPanel_total_productos.setLayout(jPanel_total_productosLayout);
        jPanel_total_productosLayout.setHorizontalGroup(
            jPanel_total_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_total_productosLayout.createSequentialGroup()
                .addGroup(jPanel_total_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_total_productosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9))
                    .addGroup(jPanel_total_productosLayout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(jLabel_cant_productos)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel_total_productosLayout.setVerticalGroup(
            jPanel_total_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_total_productosLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel_total_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addGap(33, 33, 33)
                .addComponent(jLabel_cant_productos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_stock_bajo.setBackground(new java.awt.Color(255, 153, 51));
        jPanel_stock_bajo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel_stock_bajo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel_stock_bajoMouseClicked(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/advertencia.png"))); // NOI18N

        jLabel11.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel11.setText("Productos Stock Bajo");

        jLabel_cant_stock_bajo.setText("0");

        javax.swing.GroupLayout jPanel_stock_bajoLayout = new javax.swing.GroupLayout(jPanel_stock_bajo);
        jPanel_stock_bajo.setLayout(jPanel_stock_bajoLayout);
        jPanel_stock_bajoLayout.setHorizontalGroup(
            jPanel_stock_bajoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_stock_bajoLayout.createSequentialGroup()
                .addGroup(jPanel_stock_bajoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_stock_bajoLayout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(jLabel_cant_stock_bajo))
                    .addGroup(jPanel_stock_bajoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_stock_bajoLayout.setVerticalGroup(
            jPanel_stock_bajoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_stock_bajoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel_stock_bajoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(27, 27, 27)
                .addComponent(jLabel_cant_stock_bajo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_total_clientes.setBackground(new java.awt.Color(102, 0, 204));
        jPanel_total_clientes.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/clientes.png"))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel13.setText("Total Clientes");

        jLabel_cant_clientes.setText("0");

        javax.swing.GroupLayout jPanel_total_clientesLayout = new javax.swing.GroupLayout(jPanel_total_clientes);
        jPanel_total_clientes.setLayout(jPanel_total_clientesLayout);
        jPanel_total_clientesLayout.setHorizontalGroup(
            jPanel_total_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_total_clientesLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(jPanel_total_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_total_clientesLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_total_clientesLayout.createSequentialGroup()
                        .addComponent(jLabel_cant_clientes)
                        .addGap(90, 90, 90))))
        );
        jPanel_total_clientesLayout.setVerticalGroup(
            jPanel_total_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_total_clientesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel_total_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12))
                .addGap(27, 27, 27)
                .addComponent(jLabel_cant_clientes)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(jButton_imprimir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_exportar)
                .addGap(120, 120, 120))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 912, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(87, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jComboBox1_tipo_reportes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(137, 137, 137)
                                        .addComponent(jLabel3))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(347, 347, 347)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel4)
                                                    .addComponent(jLabel5)))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jPanel_estadisticas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jPanel_total_productos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(39, 39, 39)
                                                .addComponent(jPanel_stock_bajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(129, 129, 129)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton_generar)
                                .addGap(57, 57, 57)
                                .addComponent(jLabel_logo, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel_total_clientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(89, 89, 89))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jComboBox1_tipo_reportes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4))
                                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_generar))
                            .addComponent(jLabel5)
                            .addComponent(jLabel_logo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(61, 61, 61)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel_stock_bajo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_total_clientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_total_productos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_estadisticas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_imprimir)
                    .addComponent(jButton_exportar))
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1_tipo_reportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1_tipo_reportesActionPerformed
        // TODO add your handling code here:
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
    }//GEN-LAST:event_jComboBox1_tipo_reportesActionPerformed

    private void jButton_generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarActionPerformed
        // TODO add your handling code here:
        try {
            String tipoReporte = jComboBox1_tipo_reportes.getSelectedItem().toString();
            Auditoria auditoria = new Auditoria();
            auditoria.registrar(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "CONSULTAR",
                    "Reportes",
                    "Generó reporte: " + tipoReporte
            );
            System.out.println("Reporte generado registrado");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        generarReporte();
    }//GEN-LAST:event_jButton_generarActionPerformed

    private void jButton_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirActionPerformed
        // TODO add your handling code here:
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay datos para imprimir. Genera un reporte primero.",
                    "Sin datos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // REGISTRAR IMPRESIÓN
        try {
            String tipoReporte = jComboBox1_tipo_reportes.getSelectedItem().toString();

            Auditoria auditoria = new Auditoria();
            auditoria.registrar(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "IMPRIMIR",
                    "Reportes",
                    "Imprimió reporte: " + tipoReporte
            );
            System.out.println("Impresión de reporte registrada: " + tipoReporte);
        } catch (Exception e) {
            System.err.println("Error al registrar: " + e.getMessage());
        }
        JOptionPane.showMessageDialog(this,
                "Función de impresión en desarrollo.\n\n"
                + "Se implementará con JasperReports.",
                "Próximamente",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton_imprimirActionPerformed

    private void jButton_exportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_exportarActionPerformed
        // TODO add your handling code here:}
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay datos para exportar.\nGenera un reporte primero.",
                    "Sin datos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreHoja = jComboBox1_tipo_reportes.getSelectedItem().toString();

        try {
            ExportadorExcel.exportarTabla(jTable1, nombreHoja);

            try {
                Auditoria auditoria = new Auditoria();
                auditoria.registrar(
                        Usuario_Sesion.getInstancia().getNombreUsuario(),
                        "EXPORTAR",
                        "Reportes",
                        "Exportó reporte: " + nombreHoja
                );
                System.out.println("Exportación de reporte registrada: " + nombreHoja);
            } catch (Exception e) {
                System.err.println("Error al registrar: " + e.getMessage());
            }
            JOptionPane.showMessageDialog(this,
                    "El archivo se exportó correctamente.",
                    "Exportación completa",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar el archivo:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton_exportarActionPerformed

    private void jPanel_stock_bajoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel_stock_bajoMouseClicked
        // TODO add your handling code here:
        ArrayList<Map<String, Object>> productos = reportes.obtenerListaProductosStockBajo(10);

        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay productos con stock bajo.\n\n¡Excelente! Todos los productos tienen stock suficiente.",
                    "Stock Saludable",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("═══════════════════════════════════════════════════════\n");
        mensaje.append("           PRODUCTOS CON STOCK BAJO (<10 unidades)\n");
        mensaje.append("═══════════════════════════════════════════════════════\n\n");

        for (Map<String, Object> producto : productos) {
            mensaje.append(String.format("Código: %s\n", producto.get("codigo")));
            mensaje.append(String.format("Nombre: %s\n", producto.get("nombre")));
            mensaje.append(String.format("Talla: %s | Color: %s\n",
                    producto.get("talla"),
                    producto.get("color")));
            mensaje.append(String.format("Stock actual: %d unidades\n",
                    producto.get("cantidad")));
            mensaje.append(String.format("Precio: $%.2f\n",
                    producto.get("precio")));
            mensaje.append("───────────────────────────────────────────────────────\n");
        }

        mensaje.append(String.format("\nTotal productos con stock bajo: %d", productos.size()));

        // Crear un JTextArea para mostrar el mensaje con scroll
        JTextArea textArea = new JTextArea(mensaje.toString());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", 0, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 400));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Detalle de Stock Bajo",
                JOptionPane.WARNING_MESSAGE);

        // Registrar consulta en auditoría
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "Reportes",
                    "Consultó productos con stock bajo"
            );
            System.out.println("Consulta de stock bajo registrada");
        } catch (Exception e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }//GEN-LAST:event_jPanel_stock_bajoMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmReportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmReportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmReportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmReportes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmReportes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_exportar;
    private javax.swing.JButton jButton_generar;
    private javax.swing.JButton jButton_imprimir;
    private javax.swing.JComboBox<String> jComboBox1_tipo_reportes;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDateChooser3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_cant_clientes;
    private javax.swing.JLabel jLabel_cant_productos;
    private javax.swing.JLabel jLabel_cant_stock_bajo;
    private javax.swing.JLabel jLabel_cantidad_ventas;
    private javax.swing.JLabel jLabel_logo;
    private javax.swing.JLabel jLabel_total_ventas;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_estadisticas;
    private javax.swing.JPanel jPanel_stock_bajo;
    private javax.swing.JPanel jPanel_total_clientes;
    private javax.swing.JPanel jPanel_total_productos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
