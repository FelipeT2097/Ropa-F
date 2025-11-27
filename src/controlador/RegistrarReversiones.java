/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;


import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;
import modelo.ConexionDB;
/**
 *
 * @author piper
 */
public class RegistrarReversiones {

    /**
     * Busca una factura por número o ID
     */
    public java.util.Map<String, Object> buscarFactura(String busqueda) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            con = ConexionDB.getConnection();
            
            String sql = "SELECT "
                    + "f.id, f.numero_factura, f.fecha_emision, "
                    + "f.nombre_cliente, f.numero_documento, "
                    + "f.subtotal, f.valor_iva, f.total, "
                    + "f.venta_id, f.estado, f.tiene_devolucion "
                    + "FROM factura f "
                    + "WHERE f.numero_factura = ? OR f.id = ? "
                    + "LIMIT 1";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, busqueda);
            
            try {
                int id = Integer.parseInt(busqueda);
                ps.setInt(2, id);
            } catch (NumberFormatException e) {
                ps.setInt(2, -1);
            }
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                java.util.Map<String, Object> factura = new java.util.HashMap<String, Object>();
                factura.put("id", rs.getInt("id"));
                factura.put("numero_factura", rs.getString("numero_factura"));
                factura.put("fecha_emision", rs.getTimestamp("fecha_emision"));
                factura.put("nombre_cliente", rs.getString("nombre_cliente"));
                factura.put("numero_documento", rs.getString("numero_documento"));
                factura.put("subtotal", rs.getDouble("subtotal"));
                factura.put("valor_iva", rs.getDouble("valor_iva"));
                factura.put("total", rs.getDouble("total"));
                factura.put("venta_id", rs.getInt("venta_id"));
                factura.put("estado", rs.getString("estado"));
                factura.put("tiene_devolucion", rs.getBoolean("tiene_devolucion"));
                
                return factura;
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al buscar factura: " + e.getMessage());
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Obtiene los productos de una factura
     */
    public java.util.List<java.util.Map<String, Object>> obtenerProductosFactura(int facturaId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        java.util.List<java.util.Map<String, Object>> productos = new java.util.ArrayList<java.util.Map<String, Object>>();
        
        try {
            con = ConexionDB.getConnection();
            
            String sql = "SELECT "
                    + "dv.producto_id, dv.codigo_producto, dv.nombre_producto, "
                    + "dv.cantidad, dv.precio_unitario, dv.iva, dv.subtotal, "
                    + "dv.totalFactura "
                    + "FROM detalle_ventas dv "
                    + "INNER JOIN factura f ON f.venta_id = dv.venta_id "
                    + "WHERE f.id = ?";
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, facturaId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                java.util.Map<String, Object> producto = new java.util.HashMap<String, Object>();
                producto.put("producto_id", rs.getString("producto_id"));
                producto.put("codigo", rs.getString("codigo_producto"));
                producto.put("nombre", rs.getString("nombre_producto"));
                producto.put("cantidad", rs.getInt("cantidad"));
                producto.put("precio_unitario", rs.getDouble("precio_unitario"));
                producto.put("iva", rs.getDouble("iva"));
                producto.put("subtotal", rs.getDouble("subtotal"));
                producto.put("total", rs.getDouble("totalFactura"));
                
                productos.add(producto);
            }
            
            return productos;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al obtener productos: " + e.getMessage());
            return productos;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * REVERSA/ANULA una factura completamente
     * - Genera Nota Crédito
     * - Devuelve stock automáticamente
     * - Cambia estado de factura
     * 
     * @param facturaId ID de la factura
     * @param motivo Motivo de la reversión
     * @param usuario Usuario que ejecuta
     * @return ID de la devolución creada, o -1 si error
     */
    public int reversarFactura(int facturaId, String motivo, String usuario) {
        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        PreparedStatement ps4 = null;
        PreparedStatement ps5 = null;
        ResultSet rs = null;
        
        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false);
            
            // ====================================================
            // 1. OBTENER DATOS DE LA FACTURA
            // ====================================================
            String sqlFactura = "SELECT "
                    + "f.numero_factura, f.fecha_emision, f.venta_id, "
                    + "f.nombre_cliente, f.numero_documento, "
                    + "f.subtotal, f.valor_iva, f.total, f.estado "
                    + "FROM factura f "
                    + "WHERE f.id = ?";
            
            ps1 = con.prepareStatement(sqlFactura);
            ps1.setInt(1, facturaId);
            rs = ps1.executeQuery();
            
            if (!rs.next()) {
                throw new SQLException("Factura no encontrada");
            }
            
            String numeroFactura = rs.getString("numero_factura");
            Timestamp fechaFactura = rs.getTimestamp("fecha_emision");
            int ventaId = rs.getInt("venta_id");
            String nombreCliente = rs.getString("nombre_cliente");
            String numeroDocumento = rs.getString("numero_documento");
            double subtotal = rs.getDouble("subtotal");
            double iva = rs.getDouble("valor_iva");
            double total = rs.getDouble("total");
            String estadoActual = rs.getString("estado");
            
            // Validar que se puede reversar
            if ("nota_credito".equals(estadoActual) || "anulada".equals(estadoActual)) {
                throw new SQLException("Esta factura ya está reversada o anulada");
            }
            
            rs.close();
            ps1.close();
            
            // ====================================================
            // 2. GENERAR CONSECUTIVO DE NOTA CRÉDITO
            // ====================================================
            String sqlConsecutivo = "SELECT COALESCE(MAX(consecutivo), 0) + 1 FROM devoluciones";
            ps2 = con.prepareStatement(sqlConsecutivo);
            rs = ps2.executeQuery();
            rs.next();
            int consecutivo = rs.getInt(1);
            String numeroNC = "NC-" + String.format("%06d", consecutivo);
            
            rs.close();
            ps2.close();
            
            // ====================================================
            // 3. CREAR NOTA CRÉDITO (DEVOLUCIÓN)
            // ====================================================
            String sqlDevolucion = "INSERT INTO devoluciones ("
                    + "numero_devolucion, prefijo, consecutivo, "
                    + "factura_id, venta_id, numero_factura_original, "
                    + "nombre_cliente, numero_documento, "
                    + "fecha_devolucion, fecha_factura_original, "
                    + "subtotal_devuelto, iva_devuelto, total_devuelto, "
                    + "tipo_operacion, alcance, motivo_devolucion, "
                    + "estado, usuario_creacion"
                    + ") VALUES (?, 'NC', ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?, 'anulacion_factura', 'total', ?, 'aprobada', ?)";
            
            ps3 = con.prepareStatement(sqlDevolucion, Statement.RETURN_GENERATED_KEYS);
            ps3.setString(1, numeroNC);
            ps3.setInt(2, consecutivo);
            ps3.setInt(3, facturaId);
            ps3.setInt(4, ventaId);
            ps3.setString(5, numeroFactura);
            ps3.setString(6, nombreCliente);
            ps3.setString(7, numeroDocumento);
            ps3.setTimestamp(8, fechaFactura);
            ps3.setDouble(9, subtotal);
            ps3.setDouble(10, iva);
            ps3.setDouble(11, total);
            ps3.setString(12, motivo);
            ps3.setString(13, usuario);
            
            ps3.executeUpdate();
            
            ResultSet rsId = ps3.getGeneratedKeys();
            if (!rsId.next()) {
                throw new SQLException("No se pudo obtener ID de devolución");
            }
            int devolucionId = rsId.getInt(1);
            rsId.close();
            ps3.close();
            
            // ====================================================
            // 4. COPIAR DETALLE DE PRODUCTOS
            // ====================================================
            String sqlDetalle = "INSERT INTO detalle_devoluciones ("
                    + "devolucion_id, producto_id, codigo_producto, nombre_producto, "
                    + "cantidad_original, cantidad_devuelta, "
                    + "precio_unitario, subtotal, iva, total"
                    + ") "
                    + "SELECT ?, dv.producto_id, dv.codigo_producto, dv.nombre_producto, "
                    + "dv.cantidad, dv.cantidad, "
                    + "dv.precio_unitario, dv.subtotal, dv.iva, dv.totalFactura "
                    + "FROM detalle_ventas dv "
                    + "WHERE dv.venta_id = ?";
            
            ps4 = con.prepareStatement(sqlDetalle);
            ps4.setInt(1, devolucionId);
            ps4.setInt(2, ventaId);
            ps4.executeUpdate();
            ps4.close();
            
            // ====================================================
            // 5. DEVOLVER STOCK AL INVENTARIO
            // ====================================================
            String sqlStock = "UPDATE productos p "
                    + "INNER JOIN detalle_ventas dv ON dv.producto_id = p.codigo "
                    + "SET p.cantidad = p.cantidad + dv.cantidad "
                    + "WHERE dv.venta_id = ?";
            
            ps5 = con.prepareStatement(sqlStock);
            ps5.setInt(1, ventaId);
            int productosActualizados = ps5.executeUpdate();
            ps5.close();
            
            // ====================================================
            // 6. ACTUALIZAR ESTADO DE FACTURA
            // ====================================================
            String sqlUpdateFactura = "UPDATE factura SET "
                    + "estado = 'nota_credito', "
                    + "tiene_devolucion = 1, "
                    + "fecha_devolucion = NOW() "
                    + "WHERE id = ?";
            
            PreparedStatement psUpdateFactura = con.prepareStatement(sqlUpdateFactura);
            psUpdateFactura.setInt(1, facturaId);
            psUpdateFactura.executeUpdate();
            psUpdateFactura.close();
            
            // ====================================================
            // 7. ANULAR VENTA
            // ====================================================
            String sqlUpdateVenta = "UPDATE ventas SET estado = 'ANULADA' WHERE id = ?";
            PreparedStatement psUpdateVenta = con.prepareStatement(sqlUpdateVenta);
            psUpdateVenta.setInt(1, ventaId);
            psUpdateVenta.executeUpdate();
            psUpdateVenta.close();
            
            // ====================================================
            // 8. REGISTRAR EN HISTORIAL
            // ====================================================
            String sqlHistorial = "INSERT INTO historial_devoluciones "
                    + "(devolucion_id, usuario, accion, descripcion) "
                    + "VALUES (?, ?, 'reversada', ?)";
            
            PreparedStatement psHistorial = con.prepareStatement(sqlHistorial);
            psHistorial.setInt(1, devolucionId);
            psHistorial.setString(2, usuario);
            psHistorial.setString(3, "Factura reversada: " + numeroFactura + 
                    " - Stock devuelto (" + productosActualizados + " productos)");
            psHistorial.executeUpdate();
            psHistorial.close();
            
            // ====================================================
            // COMMIT
            // ====================================================
            con.commit();
            
            JOptionPane.showMessageDialog(null, 
                "✅ FACTURA REVERSADA EXITOSAMENTE\n\n" +
                "Nota Crédito: " + numeroNC + "\n" +
                "Factura Anulada: " + numeroFactura + "\n" +
                "Productos devueltos al inventario: " + productosActualizados + "\n" +
                "Estado: Procesada",
                "Reversión Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
            
            return devolucionId;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "❌ Error al reversar factura:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return -1;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (rs != null) rs.close();
                if (ps1 != null) ps1.close();
                if (ps2 != null) ps2.close();
                if (ps3 != null) ps3.close();
                if (ps4 != null) ps4.close();
                if (ps5 != null) ps5.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Valida si una factura puede ser reversada
     */
    public String validarFacturaParaReversion(int facturaId) {
        java.util.Map<String, Object> factura = buscarFactura(String.valueOf(facturaId));
        
        if (factura == null) {
            return "Factura no encontrada";
        }
        
        String estado = (String) factura.get("estado");
        
        if ("anulada".equals(estado) || "nota_credito".equals(estado)) {
            return "Esta factura ya está reversada o anulada";
        }
        
        boolean tieneDevolucion = (boolean) factura.get("tiene_devolucion");
        if (tieneDevolucion) {
            return "Esta factura ya tiene una reversión registrada";
        }
        
        return null; // Puede reversarse
    }
    
    /**
     * Obtiene el historial de reversiones
     */
    public java.util.List<java.util.Map<String, Object>> obtenerHistorialReversiones(int limite) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        java.util.List<java.util.Map<String, Object>> historial = new java.util.ArrayList<java.util.Map<String, Object>>();
        
        try {
            con = ConexionDB.getConnection();
            
            String sql = "SELECT "
                    + "d.id, d.numero_devolucion, d.numero_factura_original, "
                    + "d.nombre_cliente, d.total_devuelto, d.motivo_devolucion, "
                    + "d.fecha_devolucion, d.usuario_creacion "
                    + "FROM devoluciones d "
                    + "ORDER BY d.fecha_devolucion DESC "
                    + "LIMIT ?";
            
            ps = con.prepareStatement(sql);
            ps.setInt(1, limite);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                java.util.Map<String, Object> item = new java.util.HashMap<String, Object>();
                item.put("id", rs.getInt("id"));
                item.put("numero_devolucion", rs.getString("numero_devolucion"));
                item.put("numero_factura_original", rs.getString("numero_factura_original"));
                item.put("nombre_cliente", rs.getString("nombre_cliente"));
                item.put("total_devuelto", rs.getDouble("total_devuelto"));
                item.put("motivo_devolucion", rs.getString("motivo_devolucion"));
                item.put("fecha_devolucion", rs.getTimestamp("fecha_devolucion"));
                item.put("usuario_creacion", rs.getString("usuario_creacion"));
                
                historial.add(item);
            }
            
            return historial;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return historial;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}





















