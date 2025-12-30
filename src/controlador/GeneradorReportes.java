/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import modelo.ConexionDB;

/**
 *
 * @author piper
 */
public class GeneradorReportes {

    private final Connection conn;
    private final SimpleDateFormat formatoFecha;
    private final SimpleDateFormat formatoFechaHora;

    public GeneradorReportes() {
        this.conn = ConexionDB.getConnection();
        // Formato estándar para fechas
        this.formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        this.formatoFechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    // REPORTES DE VENTAS  
    //Obtiene ventas del día actual (no anuladas)
    public ArrayList<Map<String, Object>> obtenerVentasDelDia() {
        String sql = "SELECT v.id, v.fecha, v.cliente, v.subtotal, v.descuento, v.total, "
                + "v.metodo_pago, v.estado, v.usuario, "
                + "(SELECT COUNT(*) FROM detalle_ventas WHERE venta_id = v.id) as cantidad_productos "
                + "FROM ventas v "
                + "WHERE DATE(v.fecha) = CURDATE() AND v.estado != 'ANULADA' "
                + "ORDER BY v.fecha DESC";

        return ejecutarConsultaVentas(sql);
    }

    //Resumen estadístico de ventas del día
    public Map<String, Object> obtenerResumenVentasDelDia() {
        String sql = "SELECT COUNT(*) as total_ventas, "
                + "COALESCE(SUM(subtotal), 0) as total_subtotal, "
                + "COALESCE(SUM(descuento), 0) as total_descuento, "
                + "COALESCE(SUM(total), 0) as total_ingresos, "
                + "COALESCE(AVG(total), 0) as promedio_venta "
                + "FROM ventas "
                + "WHERE DATE(fecha) = CURDATE() AND estado != 'ANULADA'";

        return ejecutarConsultaResumen(sql);
    }

    //Ventas dentro de un rango de fechas
    public ArrayList<Map<String, Object>> obtenerVentasPorRango(java.util.Date fechaInicio,
            java.util.Date fechaFin) {
        String sql = "SELECT v.id, v.fecha, v.cliente, v.subtotal, v.descuento, v.total, "
                + "v.metodo_pago, v.estado, v.usuario, "
                + "(SELECT COUNT(*) FROM detalle_ventas WHERE venta_id = v.id) as cantidad_productos "
                + "FROM ventas v "
                + "WHERE DATE(v.fecha) BETWEEN ? AND ? AND v.estado != 'ANULADA' "
                + "ORDER BY v.fecha DESC";

        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Map<String, Object>> ventas = new ArrayList<>();

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, formatoFecha.format(fechaInicio));
            ps.setString(2, formatoFecha.format(fechaFin));
            rs = ps.executeQuery();

            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            manejarError("obtener ventas por rango", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return ventas;
    }

    //Estadísticas de ventas para un rango de fechas
    public Map<String, Object> obtenerResumenVentasPorRango(java.util.Date fechaInicio,
            java.util.Date fechaFin) {
        String sql = "SELECT COUNT(*) as total_ventas, "
                + "COALESCE(SUM(subtotal), 0) as total_subtotal, "
                + "COALESCE(SUM(descuento), 0) as total_descuento, "
                + "COALESCE(SUM(total), 0) as total_ingresos, "
                + "COALESCE(AVG(total), 0) as promedio_venta, "
                + "COALESCE(MAX(total), 0) as venta_mayor, "
                + "COALESCE(MIN(total), 0) as venta_menor "
                + "FROM ventas "
                + "WHERE DATE(fecha) BETWEEN ? AND ? AND estado != 'ANULADA'";

        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Object> resumen = new HashMap<>();

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, formatoFecha.format(fechaInicio));
            ps.setString(2, formatoFecha.format(fechaFin));
            rs = ps.executeQuery();

            if (rs.next()) {
                resumen = mapearResumen(rs);
            }
        } catch (SQLException e) {
            manejarError("obtener resumen por rango", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return resumen;
    }

    // REPORTES DE PRODUCTOS
    //Productos más vendidos según cantidad total vendida
    public ArrayList<Map<String, Object>> obtenerProductosMasVendidos(int limite) {
        String sql = "SELECT "
                + "p.id, "
                + "p.codigo, "
                + "p.nombre, "
                + "p.talla, "
                + "p.color, "
                + "CAST(p.precio AS DECIMAL(10,2)) as precio, "
                + "SUM(dv.cantidad) as cantidad_vendida, "
                + "SUM(dv.cantidad * dv.precio_unitario) as total_ingresos "
                + "FROM detalle_ventas dv "
                + "INNER JOIN productos p ON dv.producto_id = p.codigo "
                + "INNER JOIN ventas v ON dv.venta_id = v.id "
                + "WHERE v.estado != 'ANULADA' "
                + "GROUP BY p.id, p.codigo, p.nombre, p.talla, p.color, p.precio "
                + "ORDER BY cantidad_vendida DESC "
                + "LIMIT ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Map<String, Object>> productos = new ArrayList<>();

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limite);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> producto = new HashMap<>();
                producto.put("id", rs.getInt("id"));
                producto.put("codigo", rs.getString("codigo"));
                producto.put("nombre", rs.getString("nombre"));
                producto.put("talla", rs.getString("talla"));
                producto.put("color", rs.getString("color"));
                producto.put("precio", rs.getDouble("precio"));
                producto.put("cantidad_vendida", rs.getInt("cantidad_vendida"));
                producto.put("total_ingresos", rs.getDouble("total_ingresos"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            manejarError("obtener productos más vendidos", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return productos;
    }

    // REPORTES DE INGRESOS
    //Ingresos totales agrupados por mes del año actual
    public ArrayList<Map<String, Object>> obtenerIngresosMensuales() {
        String sql = "SELECT YEAR(fecha) as anio, MONTH(fecha) as mes, "
                + "MONTHNAME(fecha) as nombre_mes, COUNT(*) as total_ventas, "
                + "COALESCE(SUM(subtotal), 0) as total_subtotal, "
                + "COALESCE(SUM(descuento), 0) as total_descuento, "
                + "COALESCE(SUM(total), 0) as total_ingresos "
                + "FROM ventas "
                + "WHERE YEAR(fecha) = YEAR(CURDATE()) AND estado != 'ANULADA' "
                + "GROUP BY YEAR(fecha), MONTH(fecha) "
                + "ORDER BY mes";

        ArrayList<Map<String, Object>> ingresos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> mes = new HashMap<>();
                mes.put("anio", rs.getInt("anio"));
                mes.put("mes", rs.getInt("mes"));
                mes.put("nombre_mes", rs.getString("nombre_mes"));
                mes.put("total_ventas", rs.getInt("total_ventas"));
                mes.put("total_subtotal", rs.getDouble("total_subtotal"));
                mes.put("total_descuento", rs.getDouble("total_descuento"));
                mes.put("total_ingresos", rs.getDouble("total_ingresos"));
                ingresos.add(mes);
            }
        } catch (SQLException e) {
            manejarError("obtener ingresos mensuales", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return ingresos;
    }

    // CONSULTAS DE VENTAS ESPECÍFICAS
    //Busca venta por número de factura Acepta formato: "1" o "FVENTA-00001"
    public int obtenerVentaIdPorNumeroFactura(String numeroFactura) {
        numeroFactura = numeroFactura.trim();

        // Auto-formatear si el usuario solo ingresó el número
        if (!numeroFactura.toUpperCase().startsWith("FVENTA-")) {
            try {
                int numero = Integer.parseInt(numeroFactura);
                numeroFactura = String.format("FVENTA-%05d", numero);
            } catch (NumberFormatException e) {
                // Mantener el formato original si no es un número válido
            }
        }

        String sql = "SELECT venta_id FROM factura WHERE UPPER(numero_factura) = UPPER(?)";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, numeroFactura);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("venta_id");
            }
        } catch (SQLException e) {
            manejarError("buscar venta por número de factura", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return -1;
    }

    //Obtiene información completa de una venta por número de factura
    public Map<String, Object> obtenerVentaPorNumeroFactura(String numeroFactura) {
        numeroFactura = numeroFactura.trim();

        // Auto-formatear número de factura
        if (!numeroFactura.toUpperCase().startsWith("FVENTA-")) {
            try {
                int numero = Integer.parseInt(numeroFactura);
                numeroFactura = String.format("FVENTA-%05d", numero);
            } catch (NumberFormatException e) {
                // Mantener formato original
            }
        }

        String sql = "SELECT v.id, v.fecha, v.cliente, v.tipo_documento_cliente, "
                + "v.numero_documento_cliente, v.metodo_pago, v.estado, v.subtotal, "
                + "v.descuento, v.total, f.numero_factura, v.usuario "
                + "FROM ventas v "
                + "INNER JOIN factura f ON v.id = f.venta_id "
                + "WHERE UPPER(f.numero_factura) = UPPER(?)";

        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Object> venta = new HashMap<>();

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, numeroFactura);
            rs = ps.executeQuery();

            if (rs.next()) {
                venta = mapearVentaCompleta(rs);
            }
        } catch (SQLException e) {
            manejarError("obtener venta por número de factura", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return venta;
    }

    //Productos asociados a una venta específica
    public ArrayList<Map<String, Object>> obtenerProductosVenta(int ventaId) {
        String sql = "SELECT "
                + "dv.id, "
                + "dv.cantidad, "
                + "dv.precio_unitario, "
                + "dv.subtotal, "
                + "dv.codigo_producto as codigo, "
                + "dv.nombre_producto as nombre, "
                + "p.talla, "
                + "p.color "
                + "FROM detalle_ventas dv "
                + "LEFT JOIN productos p ON dv.producto_id = p.codigo "
                + "WHERE dv.venta_id = ? "
                + "ORDER BY dv.id";

        ArrayList<Map<String, Object>> productos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, ventaId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> prod = new HashMap<>();
                prod.put("id", rs.getInt("id"));
                prod.put("cantidad", rs.getInt("cantidad"));
                prod.put("precio_unitario", rs.getDouble("precio_unitario"));
                prod.put("subtotal", rs.getDouble("subtotal"));
                prod.put("codigo", rs.getString("codigo"));
                prod.put("nombre", rs.getString("nombre"));
                prod.put("talla", rs.getString("talla") != null ? rs.getString("talla") : "N/A");
                prod.put("color", rs.getString("color") != null ? rs.getString("color") : "N/A");
                productos.add(prod);
            }
        } catch (SQLException e) {
            manejarError("obtener productos de la venta", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return productos;
    }

    // CONSULTAS POR CLIENTE
    //Busca ventas por nombre o documento del cliente
    public ArrayList<Map<String, Object>> obtenerVentasPorCliente(String busqueda) {
        String sql = "SELECT v.id, v.fecha, v.cliente, v.numero_documento_cliente, "
                + "v.subtotal, v.descuento, v.total, v.metodo_pago, v.estado, "
                + "(SELECT COUNT(*) FROM detalle_ventas WHERE venta_id = v.id) as cantidad_productos "
                + "FROM ventas v "
                + "WHERE (v.cliente LIKE ? OR v.numero_documento_cliente LIKE ?) "
                + "AND v.estado != 'ANULADA' "
                + "ORDER BY v.fecha DESC";

        ArrayList<Map<String, Object>> ventas = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            String parametroBusqueda = "%" + busqueda + "%";
            ps.setString(1, parametroBusqueda);
            ps.setString(2, parametroBusqueda);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> venta = new HashMap<>();
                venta.put("id", rs.getInt("id"));
                venta.put("fecha", rs.getTimestamp("fecha"));
                venta.put("cliente", rs.getString("cliente"));
                venta.put("numero_documento_cliente", rs.getString("numero_documento_cliente"));
                venta.put("subtotal", rs.getDouble("subtotal"));
                venta.put("descuento", rs.getDouble("descuento"));
                venta.put("total", rs.getDouble("total"));
                venta.put("metodo_pago", rs.getString("metodo_pago"));
                venta.put("estado", rs.getString("estado"));
                venta.put("cantidad_productos", rs.getInt("cantidad_productos"));
                ventas.add(venta);
            }
        } catch (SQLException e) {
            manejarError("obtener ventas por cliente", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return ventas;
    }

    // Obtiene resumen estadístico de compras de un cliente
    public Map<String, Object> obtenerResumenCliente(String busqueda) {
        String sql = "SELECT COUNT(*) as total_compras, "
                + "COALESCE(SUM(total), 0) as total_gastado, "
                + "COALESCE(AVG(total), 0) as promedio_compra, "
                + "COALESCE(MAX(total), 0) as compra_mayor, "
                + "COALESCE(MIN(total), 0) as compra_menor, "
                + "MIN(fecha) as primera_compra, "
                + "MAX(fecha) as ultima_compra, "
                + "MAX(cliente) as nombre_cliente, "
                + "MAX(numero_documento_cliente) as documento_cliente "
                + "FROM ventas "
                + "WHERE (cliente LIKE ? OR numero_documento_cliente LIKE ?) "
                + "AND estado != 'ANULADA'";

        Map<String, Object> resumen = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            String parametroBusqueda = "%" + busqueda + "%";
            ps.setString(1, parametroBusqueda);
            ps.setString(2, parametroBusqueda);
            rs = ps.executeQuery();

            if (rs.next()) {
                resumen.put("busqueda", busqueda);
                resumen.put("cliente", rs.getString("nombre_cliente"));
                resumen.put("numero_documento_cliente", rs.getString("documento_cliente"));
                resumen.put("total_compras", rs.getInt("total_compras"));
                resumen.put("total_gastado", rs.getDouble("total_gastado"));
                resumen.put("promedio_compra", rs.getDouble("promedio_compra"));
                resumen.put("compra_mayor", rs.getDouble("compra_mayor"));
                resumen.put("compra_menor", rs.getDouble("compra_menor"));
                resumen.put("primera_compra", rs.getTimestamp("primera_compra"));
                resumen.put("ultima_compra", rs.getTimestamp("ultima_compra"));
            }
        } catch (SQLException e) {
            manejarError("obtener resumen del cliente", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return resumen;
    }

    // MÉTODOS AUXILIARES
    // Ejecuta una consulta SQL que retorna una lista de ventas.
    //étodo auxiliar para evitar duplicación de código.
    private ArrayList<Map<String, Object>> ejecutarConsultaVentas(String sql) {
        ArrayList<Map<String, Object>> ventas = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            manejarError("ejecutar consulta de ventas", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return ventas;
    }

    //Ejecuta una consulta SQL que retorna un resumen estadístico.
    private Map<String, Object> ejecutarConsultaResumen(String sql) {
        Map<String, Object> resumen = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                resumen = mapearResumen(rs);
            }
        } catch (SQLException e) {
            manejarError("ejecutar consulta de resumen", e);
        } finally {
            cerrarRecursos(rs, ps);
        }

        return resumen;
    }

    // Convierte ResultSet en Map de venta
    private Map<String, Object> mapearVenta(ResultSet rs) throws SQLException {
        Map<String, Object> venta = new HashMap<>();
        venta.put("id", rs.getInt("id"));
        venta.put("fecha", rs.getTimestamp("fecha"));
        venta.put("cliente", rs.getString("cliente"));
        venta.put("subtotal", rs.getDouble("subtotal"));
        venta.put("descuento", rs.getDouble("descuento"));
        venta.put("total", rs.getDouble("total"));
        venta.put("metodo_pago", rs.getString("metodo_pago"));
        venta.put("estado", rs.getString("estado"));
        venta.put("usuario", rs.getString("usuario"));
        venta.put("cantidad_productos", rs.getInt("cantidad_productos"));
        return venta;
    }

    //Mapea un ResultSet a un Map con información COMPLETA de una venta.
    //Incluye datos del cliente y factura.
    private Map<String, Object> mapearVentaCompleta(ResultSet rs) throws SQLException {
        Map<String, Object> venta = new HashMap<>();
        venta.put("id", rs.getInt("id"));
        venta.put("fecha", rs.getTimestamp("fecha"));
        venta.put("cliente", rs.getString("cliente"));
        venta.put("tipo_documento_cliente", rs.getString("tipo_documento_cliente"));
        venta.put("numero_documento_cliente", rs.getString("numero_documento_cliente"));
        venta.put("subtotal", rs.getDouble("subtotal"));
        venta.put("descuento", rs.getDouble("descuento"));
        venta.put("total", rs.getDouble("total"));
        venta.put("metodo_pago", rs.getString("metodo_pago"));
        venta.put("estado", rs.getString("estado"));
        venta.put("usuario", rs.getString("usuario"));
        venta.put("numero_factura", rs.getString("numero_factura"));
        return venta;
    }

    // Mapea dinámicamente cualquier ResultSet
    private Map<String, Object> mapearResumen(ResultSet rs) throws SQLException {
        Map<String, Object> resumen = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Object value = rs.getObject(i);
            resumen.put(columnName, value);
        }

        return resumen;
    }

    // Cierra recursos JDBC
    private void cerrarRecursos(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            // Solo registra el error sin interrumpir el flujo
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }

    // Manejo básico de errores
    private void manejarError(String operacion, SQLException e) {
        System.err.println("Error al " + operacion + ": " + e.getMessage());
        System.err.println("Código SQL: " + e.getErrorCode());
        System.err.println("Estado SQL: " + e.getSQLState());
        e.printStackTrace();
    }

    // Cierra la conexión Verifica que la conexión esté abierta antes de intentar cerrarla.
    public void cerrarConexion() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }

    //Cantidad de productos con stock bajo (menos de 10 unidades)
    public ArrayList<Map<String, Object>> obtenerListaProductosStockBajo(int limite) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ArrayList<Map<String, Object>> productos = new ArrayList<>();

        try {
            conn = ConexionDB.getConnection();
            // Ajusta el nombre de la columna según tu base de datos
            String sql = "SELECT codigo, nombre, talla, color, cantidad, precio "
                    + "FROM productos "
                    + "WHERE cantidad < ? "
                    + "ORDER BY cantidad ASC";

            pst = conn.prepareStatement(sql);
            pst.setInt(1, limite);
            rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, Object> producto = new HashMap<>();
                producto.put("codigo", rs.getString("codigo"));
                producto.put("nombre", rs.getString("nombre"));
                producto.put("talla", rs.getString("talla"));
                producto.put("color", rs.getString("color"));
                producto.put("cantidad", rs.getInt("cantidad"));
                producto.put("precio", rs.getDouble("precio"));
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener lista de productos con stock bajo: " + e.getMessage());
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

        return productos;
    }

    //Total de clientes registrados
    public int obtenerTotalProductos() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            // Sin filtro de estado - cuenta todos los productos
            String sql = "SELECT COUNT(*) as total FROM productos";
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

    //Lista detallada de productos con stock bajo
    public int obtenerProductosStockBajo() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            // Sin filtro de estado - solo verifica stock
            String sql = "SELECT COUNT(*) as total FROM productos WHERE cantidad < 10";
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
            // Sin filtro de estado - cuenta todos los clientes
            String sql = "SELECT COUNT(*) as total FROM clientes";
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
}
