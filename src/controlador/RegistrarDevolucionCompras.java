package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import modelo.ConexionDB;

/**
 * Controlador para gestionar devoluciones de compras a proveedores
 * Usa tablas: devoluciones_compras y detalle_devoluciones_compras
 * @author piper
 */
public class RegistrarDevolucionCompras {

    //Busca una compra por número de factura
    public Map<String, Object> buscarCompra(String busqueda) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Map<String, Object> compra = null;

        try {
            conn = ConexionDB.getConnection();
            
            String sql = "SELECT c.id, c.numero_factura, c.proveedor_id, c.proveedor_nombre, " +
                        "c.fecha_compra, c.subtotal, c.impuesto, c.total, c.estado, " +
                        "c.observaciones, c.usuario, p.numero_documento " +
                        "FROM compras c " +
                        "LEFT JOIN proveedores p ON c.proveedor_id = p.id " +
                        "WHERE c.numero_factura = ? OR c.id = ?";

            pst = conn.prepareStatement(sql);
            pst.setString(1, busqueda);
            
            try {
                pst.setInt(2, Integer.parseInt(busqueda));
            } catch (NumberFormatException e) {
                pst.setInt(2, -1);
            }
            
            rs = pst.executeQuery();

            if (rs.next()) {
                compra = new HashMap<>();
                compra.put("id", rs.getInt("id"));
                compra.put("numero_factura", rs.getString("numero_factura"));
                compra.put("proveedor_id", rs.getInt("proveedor_id"));
                compra.put("proveedor_nombre", rs.getString("proveedor_nombre"));
                compra.put("numero_documento_proveedor", rs.getString("numero_documento"));
                compra.put("fecha_compra", rs.getTimestamp("fecha_compra"));
                compra.put("subtotal", rs.getDouble("subtotal"));
                compra.put("impuesto", rs.getDouble("impuesto"));
                compra.put("total", rs.getDouble("total"));
                compra.put("estado", rs.getString("estado"));
                compra.put("observaciones", rs.getString("observaciones"));
                compra.put("usuario", rs.getString("usuario"));
            }

        } catch (Exception e) {
            System.err.println("Error al buscar compra: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, pst, rs);
        }

        return compra;
    }

    //Valida si una compra puede ser devuelta
    public String validarCompraParaDevolucion(int compraId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();

            // Verificar estado de la compra
            String sql = "SELECT estado FROM compras WHERE id = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, compraId);
            rs = pst.executeQuery();

            if (rs.next()) {
                String estado = rs.getString("estado");
                if ("ANULADA".equalsIgnoreCase(estado)) {
                    return "Esta compra ya fue anulada";
                }
                if ("DEVUELTA".equalsIgnoreCase(estado)) {
                    return "Esta compra ya fue devuelta";
                }
            } else {
                return "Compra no encontrada";
            }
            rs.close();
            pst.close();

            // Verificar si ya existe una devolución para esta compra
            String sqlDev = "SELECT id FROM devoluciones_compras WHERE compra_id = ?";
            pst = conn.prepareStatement(sqlDev);
            pst.setInt(1, compraId);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                return "Esta compra ya tiene una devolución registrada";
            }

        } catch (Exception e) {
            return "Error al validar: " + e.getMessage();
        } finally {
            cerrarRecursos(conn, pst, rs);
        }

        return null;
    }

    //Obtiene los productos de una compra
    public List<Map<String, Object>> obtenerProductosCompra(int compraId) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String, Object>> productos = new ArrayList<>();

        try {
            conn = ConexionDB.getConnection();

            String sql = "SELECT dc.id, dc.producto_id, dc.codigo_producto, dc.nombre_producto, " +
                        "dc.cantidad, dc.precio_compra, dc.subtotal, " +
                        "p.cantidad as stock_actual " +
                        "FROM detalle_compras dc " +
                        "LEFT JOIN productos p ON dc.producto_id = p.id " +
                        "WHERE dc.compra_id = ?";

            pst = conn.prepareStatement(sql);
            pst.setInt(1, compraId);
            rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, Object> producto = new HashMap<>();
                producto.put("detalle_id", rs.getInt("id"));
                producto.put("producto_id", rs.getInt("producto_id"));
                producto.put("codigo", rs.getString("codigo_producto"));
                producto.put("nombre", rs.getString("nombre_producto"));
                producto.put("cantidad_comprada", rs.getInt("cantidad")); // Para el formulario
                producto.put("cantidad", rs.getInt("cantidad"));
                producto.put("precio_compra", rs.getDouble("precio_compra"));
                producto.put("subtotal", rs.getDouble("subtotal"));
                producto.put("stock_actual", rs.getInt("stock_actual"));
                productos.add(producto);
            }

        } catch (Exception e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, pst, rs);
        }

        return productos;
    }

    //Registra la devolución TOTAL de una compra
    public int registrarDevolucionCompra(int compraId, String motivo, String usuario) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int devolucionId = -1;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            // PASO 1: Obtener datos de la compra original           
            String sqlCompra = "SELECT c.numero_factura, c.proveedor_id, c.proveedor_nombre, " +
                              "c.fecha_compra, c.subtotal, c.impuesto, c.total, p.numero_documento " +
                              "FROM compras c " +
                              "LEFT JOIN proveedores p ON c.proveedor_id = p.id " +
                              "WHERE c.id = ?";
            pst = conn.prepareStatement(sqlCompra);
            pst.setInt(1, compraId);
            rs = pst.executeQuery();

            String numeroFactura = "";
            int proveedorId = 0;
            String proveedorNombre = "";
            String docProveedor = "";
            java.sql.Timestamp fechaCompra = null;
            double subtotalCompra = 0;
            double impuestoCompra = 0;
            double totalCompra = 0;

            if (rs.next()) {
                numeroFactura = rs.getString("numero_factura");
                proveedorId = rs.getInt("proveedor_id");
                proveedorNombre = rs.getString("proveedor_nombre");
                docProveedor = rs.getString("numero_documento") != null ? rs.getString("numero_documento") : "";
                fechaCompra = rs.getTimestamp("fecha_compra");
                subtotalCompra = rs.getDouble("subtotal");
                impuestoCompra = rs.getDouble("impuesto");
                totalCompra = rs.getDouble("total");
            }
            rs.close();
            
            // PASO 2: Obtener productos de la compra         
            List<Map<String, Object>> productos = obtenerProductosCompra(compraId);
            
            if (productos.isEmpty()) {
                throw new Exception("No se encontraron productos en la compra");
            }

            System.out.println("→ Productos a devolver: " + productos.size());

            // Verificar stock suficiente para todos los productos
            for (Map<String, Object> prod : productos) {
                int cantidad = (Integer) prod.get("cantidad_comprada");
                int stockActual = (Integer) prod.get("stock_actual");
                String nombre = (String) prod.get("nombre");
                
                if (cantidad > stockActual) {
                    throw new Exception("Stock insuficiente para devolver '" + nombre + 
                                      "'. Stock actual: " + stockActual + ", Cantidad a devolver: " + cantidad);
                }
            }
            // PASO 3: Generar número de devolución
            
            String numeroDevolucion = generarNumeroDevolucion(conn);

            // PASO 4: Insertar en devoluciones_compras           
            String sqlDevolucion = "INSERT INTO devoluciones_compras (numero_devolucion, compra_id, " +
                                  "numero_factura_compra, proveedor_id, proveedor_nombre, " +
                                  "numero_documento_proveedor, fecha_devolucion, fecha_compra_original, " +
                                  "subtotal_devuelto, impuesto_devuelto, total_devuelto, " +
                                  "motivo_devolucion, estado, usuario_creacion) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, 'COMPLETADA', ?)";

            pst = conn.prepareStatement(sqlDevolucion, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, numeroDevolucion);
            pst.setInt(2, compraId);
            pst.setString(3, numeroFactura);
            pst.setInt(4, proveedorId);
            pst.setString(5, proveedorNombre);
            pst.setString(6, docProveedor);
            pst.setTimestamp(7, fechaCompra);
            pst.setDouble(8, subtotalCompra);
            pst.setDouble(9, impuestoCompra);
            pst.setDouble(10, totalCompra);
            pst.setString(11, motivo);
            pst.setString(12, usuario);

            pst.executeUpdate();

            rs = pst.getGeneratedKeys();
            if (rs.next()) {
                devolucionId = rs.getInt(1);
                System.out.println("Devolución registrada con ID: " + devolucionId);
            }
            rs.close();

            // PASO 5: Insertar detalle y actualizar stock           
            for (Map<String, Object> prod : productos) {
                int productoId = (Integer) prod.get("producto_id");
                String codigo = (String) prod.get("codigo");
                String nombre = (String) prod.get("nombre");
                int cantidad = (Integer) prod.get("cantidad_comprada");
                double precioCompra = (Double) prod.get("precio_compra");
                double subtotalProd = (Double) prod.get("subtotal");

                // Insertar detalle
                String sqlDetalle = "INSERT INTO detalle_devoluciones_compras (devolucion_id, producto_id, " +
                                   "codigo_producto, nombre_producto, cantidad, precio_unitario, subtotal) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?)";

                pst = conn.prepareStatement(sqlDetalle);
                pst.setInt(1, devolucionId);
                pst.setInt(2, productoId);
                pst.setString(3, codigo);
                pst.setString(4, nombre);
                pst.setInt(5, cantidad);
                pst.setDouble(6, precioCompra);
                pst.setDouble(7, subtotalProd);
                pst.executeUpdate();

                System.out.println("  ✓ Detalle: " + codigo + " - " + nombre + " x " + cantidad);

                // Actualizar stock (RESTAR porque devolvemos al proveedor)
                String sqlStock = "UPDATE productos SET cantidad = cantidad - ? WHERE id = ?";
                pst = conn.prepareStatement(sqlStock);
                pst.setInt(1, cantidad);
                pst.setInt(2, productoId);
                pst.executeUpdate();

                System.out.println("    → Stock actualizado: -" + cantidad + " unidades");
            }

            // PASO 6: Actualizar estado de la compra            
            String sqlEstado = "UPDATE compras SET estado = 'DEVUELTA' WHERE id = ?";
            pst = conn.prepareStatement(sqlEstado);
            pst.setInt(1, compraId);
            pst.executeUpdate();

            System.out.println("  ✓ Estado de compra actualizado a: DEVUELTA");

            // COMMIT
            
            conn.commit();
            return devolucionId;

        } catch (Exception e) {
            System.err.println("Error en devolución: " + e.getMessage());
            e.printStackTrace();

            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("ROLLBACK ejecutado");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(null,
                    "Error al registrar devolución:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            return -1;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            cerrarRecursos(conn, pst, rs);
        }
    }

    //Genera un número único de devolución
    private String generarNumeroDevolucion(Connection conn) throws Exception {
        String numero = "DC-";
        
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 as siguiente FROM devoluciones_compras";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            numero += String.format("%06d", rs.getInt("siguiente"));
        } else {
            numero += "000001";
        }
        
        rs.close();
        pst.close();
        
        return numero;
    }

    //Obtiene el historial de devoluciones de compras
    public List<Map<String, Object>> obtenerHistorialDevoluciones(int limite) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String, Object>> historial = new ArrayList<>();

        try {
            conn = ConexionDB.getConnection();

            String sql = "SELECT d.id, d.numero_devolucion, d.numero_factura_compra, " +
                        "d.proveedor_nombre, d.numero_documento_proveedor, d.total_devuelto, " +
                        "d.fecha_devolucion, d.usuario_creacion, d.motivo_devolucion " +
                        "FROM devoluciones_compras d " +
                        "ORDER BY d.fecha_devolucion DESC " +
                        "LIMIT ?";

            pst = conn.prepareStatement(sql);
            pst.setInt(1, limite);
            rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("numero_devolucion", rs.getString("numero_devolucion"));
                item.put("numero_factura_original", rs.getString("numero_factura_compra")); // Para el formulario
                item.put("proveedor_nombre", rs.getString("proveedor_nombre"));
                item.put("numero_documento", rs.getString("numero_documento_proveedor"));
                item.put("total_devuelto", rs.getDouble("total_devuelto"));
                item.put("fecha_devolucion", rs.getTimestamp("fecha_devolucion"));
                item.put("usuario_creacion", rs.getString("usuario_creacion"));
                item.put("motivo_devolucion", rs.getString("motivo_devolucion"));
                historial.add(item);
            }

        } catch (Exception e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, pst, rs);
        }

        return historial;
    }

    //Cierra recursos de base de datos
    private void cerrarRecursos(Connection conn, PreparedStatement pst, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
