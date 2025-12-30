/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ConexionDB;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import vista.FrmVentas;

/**
 *
 * @author piper
 */
public class RegistrarVentas {

    private FrmVentas vista;

    public RegistrarVentas(FrmVentas vista) {
        this.vista = vista;
    }

    // REGISTRAR VENTA COMPLETA
    public void registrarVenta() {

        Connection con = null;

        try {

            //VALIDACIONES COMPLETAS
            //Validar que hay productos
            if (vista.getCantidadProductos() == 0) {
                mensaje("Debe agregar productos a la venta");
                return;
            }

            // Validar nombre del cliente
            String nombreCliente = vista.getNombreCliente();
            if (nombreCliente == null || nombreCliente.isEmpty()) {
                mensaje("Debe escribir el nombre del cliente");
                return;
            }

            // Validar nombre completo
            String nombreCompleto = vista.getNombreCompletoCliente();
            if (nombreCompleto == null || nombreCompleto.isEmpty()) {
                nombreCompleto = nombreCliente;
            }

            // Validar subtotal
            String subtotalTexto = vista.getSubtotalTexto();
            if (subtotalTexto == null || subtotalTexto.isEmpty()) {
                mensaje("El subtotal no puede estar vacío");
                return;
            }
            double subtotal = getDouble(subtotalTexto);
            if (subtotal <= 0) {
                mensaje("El subtotal debe ser mayor a 0");
                return;
            }

            // Validar descuento (puede ser 0)
            String descuentoTexto = vista.getDescuentoTexto();
            double descuento = 0;
            if (descuentoTexto != null && !descuentoTexto.isEmpty()) {
                descuento = getDouble(descuentoTexto);
            }

            // Validar total
            String totalTexto = vista.getTotalTexto();
            if (totalTexto == null || totalTexto.isEmpty()) {
                mensaje("El total no puede estar vacío");
                return;
            }
            double total = getDouble(totalTexto);
            if (total <= 0) {
                mensaje("El total debe ser mayor a 0");
                return;
            }

            //Validar método de pago
            String metodoPago = vista.getMetodoPagoSeleccionado();
            if (metodoPago == null || metodoPago.isEmpty() || metodoPago.equals("Seleccionar")) {
                mensaje("Debe seleccionar un método de pago");
                return;
            }

            //Obtener usuario
            String usuario = null;
            try {
                usuario = modelo.Usuario_Sesion.getInstancia().getNombreUsuario();
            } catch (Exception e) {
                // Usuario no disponible
            }

            if (usuario == null || usuario.isEmpty()) {
                usuario = "admin";
            }

            //Estado de la venta
            String estado = "pendiente";

            //Buscar cliente en la base de datos por nombre
            String tipoDocumentoCliente = "CC";
            String numeroDocumentoCliente = "0000000000";

            Connection conTemp = null;
            try {
                conTemp = ConexionDB.getConnection();
                String sqlBuscarCliente = "SELECT numero_documento, tipo_documento_cliente "
                        + "FROM clientes "
                        + "WHERE nombre_completo LIKE ? "
                        + "LIMIT 1";

                PreparedStatement psBuscar = conTemp.prepareStatement(sqlBuscarCliente);
                psBuscar.setString(1, "%" + nombreCompleto + "%");
                ResultSet rsBuscar = psBuscar.executeQuery();

                if (rsBuscar.next()) {
                    numeroDocumentoCliente = rsBuscar.getString("numero_documento");
                    String tipoDoc = rsBuscar.getString("tipo_documento_cliente");

                    if (tipoDoc != null && !tipoDoc.isEmpty()) {
                        tipoDoc = tipoDoc.toUpperCase().trim();

                        if (tipoDoc.contains("CEDULA") || tipoDoc.contains("CÉDULA") || tipoDoc.contains("CIUDADANIA") || tipoDoc.contains("CIUDADANÍA")) {
                            tipoDocumentoCliente = "CC";
                        } else if (tipoDoc.contains("TARJETA") || tipoDoc.contains("IDENTIDAD")) {
                            tipoDocumentoCliente = "TI";
                        } else if (tipoDoc.contains("EXTRANJERIA") || tipoDoc.contains("XTRANJERÍA")) {
                            tipoDocumentoCliente = "CE";
                        } else if (tipoDoc.contains("NIT")) {
                            tipoDocumentoCliente = "NIT";
                        } else if (tipoDoc.contains("PASAPORTE")) {
                            tipoDocumentoCliente = "PAS";
                        } else if (tipoDoc.length() <= 5) {
                            tipoDocumentoCliente = tipoDoc;
                        } else {
                            tipoDocumentoCliente = "CC";
                        }
                    }
                }

                rsBuscar.close();
                psBuscar.close();
                conTemp.close();

            } catch (SQLException ex) {
                // Continuar con el valor por defecto
            } finally {
                if (conTemp != null) {
                    try {
                        conTemp.close();
                    } catch (SQLException e) {
                        // Ignorar
                    }
                }
            }

            //CONECTAR Y TRANSACCIÓN
            con = ConexionDB.getConnection();

            if (con == null) {
                throw new SQLException("No se pudo establecer conexión con la base de datos");
            }

            con.setAutoCommit(false);

            //INSERTAR VENTA
            int ventaId = insertarVenta(con, nombreCompleto, subtotal, descuento,
                    total, metodoPago, estado, usuario,
                    tipoDocumentoCliente, numeroDocumentoCliente);

            if (ventaId <= 0) {
                throw new SQLException("No se generó el ID de la venta");
            }

            //INSERTAR DETALLE Y ACTUALIZAR STOCK
            int productosActualizados = insertarDetalleYStock(con, ventaId);

            // PASO 5: CREAR FACTURA
            int facturaId = -1;

            try {
                facturaId = crearFactura(con, ventaId, usuario);
            } catch (SQLException e) {
                facturaId = -1;
            }

            //CONFIRMAR TRANSACCIÓN
            con.commit();

            try {
                Auditoria auditoria = new Auditoria();
                auditoria.registrarVenta(
                        usuario,
                        ventaId,
                        total
                );
                System.out.println("✅Venta registrada en auditoría: ID=" + ventaId + ", Total=$" + total);
            } catch (Exception e) {
                System.err.println("Error al registrar venta en auditoría: " + e.getMessage());
                // No interrumpir el flujo si falla la auditoría
            }

            mensaje("Venta realizada correctamente.\n\n"
                    + "Factura Nº: " + facturaId + "\n"
                    + "Total: $" + String.format("%,.2f", total));

            //IMPRIMIR (OPCIONAL)
            int respuesta = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Desea imprimir la factura?",
                    "Imprimir",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                imprimirFactura(facturaId);
            }

            //LIMPIAR
            vista.limpiarFormulario();

        } catch (SQLException ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException e) {
                // Error al revertir
            }

            mensaje("Error de base de datos:\n\n" + ex.getMessage());

        } catch (NullPointerException ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException e) {
                // Error al revertir
            }

            mensaje("Error: Faltan campos por llenar.\n\n"
                    + "Verifique que:\n"
                    + "El nombre del cliente esté completo\n"
                    + "El método de pago esté seleccionado\n"
                    + "Todos los campos tengan valores");

        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException e) {
                // Error al revertir
            }

            mensaje("Error al procesar venta:\n\n" + ex.getMessage());

        } finally {
            if (con != null) {
                ConexionDB.closeConnection(con);
            }
        }
    }

    // INSERTAR VENTA (con validaciones NULL)
    private int insertarVenta(Connection con, String cliente, double subtotal,
            double descuento, double total, String metodoPago, String estado,
            String usuario, String tipoDocumentoCliente, String numeroDocumentoCliente) throws SQLException {

        // Validaciones finales antes de insertar
        if (con == null) {
            throw new SQLException("La conexión es null");
        }
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new SQLException("El nombre del cliente es obligatorio");
        }
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new SQLException("El método de pago es obligatorio");
        }
        if (usuario == null || usuario.trim().isEmpty()) {
            usuario = "admin"; // Valor por defecto
        }

        String sql = "INSERT INTO ventas("
                + "fecha, cliente, subtotal, descuento, total, "
                + "metodo_pago, estado, usuario, tipo_documento_cliente, numero_documento_cliente) "
                + "VALUES(NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cliente);
            ps.setDouble(2, subtotal);
            ps.setDouble(3, descuento);
            ps.setDouble(4, total);
            ps.setString(5, metodoPago);
            ps.setString(6, estado);
            ps.setString(7, usuario);
            ps.setString(8, tipoDocumentoCliente);
            ps.setString(9, numeroDocumentoCliente);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo insertar la venta");
            }

            rs = ps.getGeneratedKeys();
            int id = -1;
            if (rs.next()) {
                id = rs.getInt(1);
            }

            if (id <= 0) {
                throw new SQLException("No se generó el ID de la venta");
            }

            return id;

        } catch (SQLException e) {
            System.err.println("Error en insertarVenta():");
            System.err.println("SQL: " + sql);
            System.err.println("Cliente: " + cliente);
            System.err.println("Método de pago: " + metodoPago);
            System.err.println("Usuario: " + usuario);
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    // INSERTAR DETALLE Y ACTUALIZAR STOCK
    private int insertarDetalleYStock(Connection con, int ventaId)
            throws SQLException {

        DefaultTableModel modelo = (DefaultTableModel) vista.getTablaVentas().getModel();

        if (modelo == null) {
            throw new SQLException("El modelo de la tabla es null");
        }

        if (modelo.getRowCount() == 0) {
            throw new SQLException("No hay productos en la tabla");
        }

        PreparedStatement psDetalle = null;
        PreparedStatement psStock = null;
        PreparedStatement psVerificar = null;

        try {
            psDetalle = con.prepareStatement(
                    "INSERT INTO detalle_ventas("
                    + "venta_id, producto_id, codigo_producto, nombre_producto, "
                    + "cantidad, precio_unitario, subtotal, iva, totalFactura) "
                    + "VALUES(?,?,?,?,?,?,?,?,?)"
            );

            psStock = con.prepareStatement(
                    "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ?"
            );

            psVerificar = con.prepareStatement(
                    "SELECT cantidad, nombre FROM productos WHERE codigo = ?"
            );

            int productosActualizados = 0;

            for (int i = 0; i < modelo.getRowCount(); i++) {
                // Obtener datos de la fila (con validación)
                Object idObj = modelo.getValueAt(i, 0);
                if (idObj == null) {
                    throw new SQLException("El ID del producto en la fila " + (i + 1) + " es null");
                }
                String productoId = idObj.toString();

                String codigo = modelo.getValueAt(i, 0).toString();
                String nombre = modelo.getValueAt(i, 1).toString();
                String talla = modelo.getValueAt(i, 2).toString();
                String color = modelo.getValueAt(i, 3).toString();
                double precio = Double.parseDouble(modelo.getValueAt(i, 4).toString());
                int cantidad = Integer.parseInt(modelo.getValueAt(i, 5).toString());
                double subtotal = Double.parseDouble(modelo.getValueAt(i, 6).toString());

                double iva = subtotal * 0.19;
                double totalConIva = subtotal + iva;

                // Verificar stock
                psVerificar.setString(1, productoId);
                ResultSet rsStock = psVerificar.executeQuery();

                if (rsStock.next()) {
                    int stockActual = rsStock.getInt("cantidad");
                    String nombreProducto = rsStock.getString("nombre");

                    System.out.println("Producto: " + nombreProducto);
                    System.out.println("Stock actual: " + stockActual);
                    System.out.println("Cantidad a vender: " + cantidad);

                    if (stockActual < cantidad) {
                        rsStock.close();
                        throw new SQLException(
                                "Stock insuficiente para: " + nombreProducto + "\n"
                                + "Stock disponible: " + stockActual + "\n"
                                + "Cantidad solicitada: " + cantidad
                        );
                    }
                    System.out.println("Stock suficiente");
                }
                rsStock.close();

                // Insertar detalle
                psDetalle.setInt(1, ventaId);
                psDetalle.setString(2, codigo);
                psDetalle.setString(3, productoId);
                psDetalle.setString(4, nombre);
                psDetalle.setInt(5, cantidad);
                psDetalle.setDouble(6, precio);
                psDetalle.setDouble(7, subtotal);
                psDetalle.setDouble(8, iva);
                psDetalle.setDouble(9, totalConIva);
                psDetalle.executeUpdate();
                System.out.println("Detalle insertado");

                // Actualizar stock
                psStock.setInt(1, cantidad);
                psStock.setString(2, productoId);
                int filasActualizadas = psStock.executeUpdate();

                if (filasActualizadas > 0) {
                    productosActualizados++;
                    psVerificar.setString(1, productoId);
                    ResultSet rsNuevo = psVerificar.executeQuery();
                    if (rsNuevo.next()) {
                        int nuevoStock = rsNuevo.getInt("cantidad");
                        System.out.println("Stock actualizado (nuevo: " + nuevoStock + ")");
                    }
                    rsNuevo.close();
                } else {
                    throw new SQLException("No se pudo actualizar el stock del producto: " + nombre);
                }
                System.out.println();
            }

            return productosActualizados;

        } finally {
            if (psVerificar != null) {
                psVerificar.close();
            }
            if (psDetalle != null) {
                psDetalle.close();
            }
            if (psStock != null) {
                psStock.close();
            }
        }
    }

    // Crea la factura completa con todos los campos obligatorios
    private int crearFactura(Connection con, int ventaId, String usuario) throws SQLException {

        if (usuario == null || usuario.isEmpty()) {
            usuario = "admin";
        }

        PreparedStatement psVenta = null;
        PreparedStatement psConfig = null;
        PreparedStatement psFactura = null;
        ResultSet rsVenta = null;
        ResultSet rsConfig = null;
        ResultSet rsId = null;

        try {
            //OBTENER DATOS REALES DE LA VENTA
            String sqlVenta = "SELECT v.cliente, v.subtotal, v.descuento, v.total, v.metodo_pago, "
                    + "v.numero_documento_cliente, "
                    + "c.nombre_completo, c.tipo_documento_cliente, c.numero_documento, "
                    + "c.correo_electronico, c.direccion, c.telefono, c.ciudad "
                    + "FROM ventas v "
                    + "LEFT JOIN clientes c ON c.numero_documento = v.numero_documento_cliente "
                    + "WHERE v.id = ?";

            psVenta = con.prepareStatement(sqlVenta);
            psVenta.setInt(1, ventaId);
            rsVenta = psVenta.executeQuery();

            // Variables para almacenar los datos
            String nombreCliente = "Cliente Genérico";
            String tipoDocumento = "CC";
            String numeroDocumento = "0000000000";
            String correoCliente = "";
            String direccionCliente = "Sin dirección registrada";
            String telefonoCliente = "";
            String ciudadCliente = "";
            double subtotal = 0.0;
            double descuento = 0.0;
            double total = 0.0;
            String metodoPago = "Efectivo";

            if (rsVenta.next()) {
                String clienteVenta = rsVenta.getString("cliente");
                subtotal = rsVenta.getDouble("subtotal");
                descuento = rsVenta.getDouble("descuento");
                total = rsVenta.getDouble("total");
                metodoPago = rsVenta.getString("metodo_pago");
                String numeroDocumentoVenta = rsVenta.getString("numero_documento_cliente");

                String nombreCompletoCliente = rsVenta.getString("nombre_completo");
                String tipoDocCliente = rsVenta.getString("tipo_documento_cliente");
                String numDocCliente = rsVenta.getString("numero_documento");
                String correoClienteBD = rsVenta.getString("correo_electronico");
                String direccionClienteBD = rsVenta.getString("direccion");
                String telefonoClienteBD = rsVenta.getString("telefono");
                String ciudadClienteBD = rsVenta.getString("ciudad");

                if (nombreCompletoCliente != null && !nombreCompletoCliente.isEmpty()) {
                    nombreCliente = nombreCompletoCliente;
                } else if (clienteVenta != null && !clienteVenta.isEmpty()) {
                    nombreCliente = clienteVenta;
                }

                if (tipoDocCliente != null && !tipoDocCliente.isEmpty()) {
                    tipoDocumento = tipoDocCliente;
                }

                if (numDocCliente != null && !numDocCliente.isEmpty() && !numDocCliente.equals("0000000000")) {
                    numeroDocumento = numDocCliente;
                } else if (numeroDocumentoVenta != null && !numeroDocumentoVenta.isEmpty() && !numeroDocumentoVenta.equals("0000000000")) {
                    numeroDocumento = numeroDocumentoVenta;
                }

                if (correoClienteBD != null && !correoClienteBD.isEmpty()) {
                    correoCliente = correoClienteBD;
                }

                if (direccionClienteBD != null && !direccionClienteBD.isEmpty()) {
                    direccionCliente = direccionClienteBD;
                }

                if (telefonoClienteBD != null && !telefonoClienteBD.isEmpty()) {
                    telefonoCliente = telefonoClienteBD;
                }

                if (ciudadClienteBD != null && !ciudadClienteBD.isEmpty()) {
                    ciudadCliente = ciudadClienteBD;
                }

            } else {
                throw new SQLException("No se encontró la venta ID: " + ventaId);
            }

            //OBTENER CONFIGURACIÓN DE LA EMPRESA
            String sqlConfig = "SELECT nit_empresa, clave_tecnica_dian, ambiente_operacion "
                    + "FROM configuracion_facturacion LIMIT 1";
            psConfig = con.prepareStatement(sqlConfig);
            rsConfig = psConfig.executeQuery();

            String nitEmisor = "900123456";
            String claveTecnica = "clave_tecnica_ejemplo";
            int ambiente = 1;

            if (rsConfig.next()) {
                nitEmisor = rsConfig.getString("nit_empresa");
                claveTecnica = rsConfig.getString("clave_tecnica_dian");
                ambiente = rsConfig.getInt("ambiente_operacion");
            }

            rsConfig.close();
            psConfig.close();

            //CALCULAR IVA Y PREPARAR DATOS
            double baseIva = subtotal - descuento;
            double porcentajeIva = 19.0;
            double valorIva = baseIva * 0.19;

            String prefijo = "FACT";

            // 4. INSERTAR FACTURA TEMPORALMENTE
            String sqlFactura
                    = "INSERT INTO factura("
                    + "numero_factura, cufe, prefijo, consecutivo, venta_id, "
                    + "fecha_emision, nombre_cliente, "
                    + "tipo_documento_cliente, numero_documento, "
                    + "telefono, correo_electronico, "
                    + "direccion_facturacion, ciudad, "
                    + "subtotal, descuento, base_iva, porcentaje_iva, valor_iva, "
                    + "otros_impuestos, total, "
                    + "metodo_pago, estado_pago, estado_dian, estado, usuario_creacion"
                    + ") VALUES ('TEMP', 'TEMP', ?, 0, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            psFactura = con.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS);

            psFactura.setString(1, prefijo);
            psFactura.setInt(2, ventaId);
            psFactura.setString(3, nombreCliente);
            psFactura.setString(4, tipoDocumento);
            psFactura.setString(5, numeroDocumento);
            psFactura.setString(6, telefonoCliente);
            psFactura.setString(7, correoCliente);
            psFactura.setString(8, direccionCliente);
            psFactura.setString(9, ciudadCliente);
            psFactura.setDouble(10, subtotal);
            psFactura.setDouble(11, descuento);
            psFactura.setDouble(12, baseIva);
            psFactura.setDouble(13, porcentajeIva);
            psFactura.setDouble(14, valorIva);
            psFactura.setDouble(15, 0.0);
            psFactura.setDouble(16, total);
            psFactura.setString(17, metodoPago);
            psFactura.setString(18, "pendiente");
            psFactura.setString(19, "no_enviado");
            psFactura.setString(20, "activa");
            psFactura.setString(21, usuario);

            int filas = psFactura.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No se insertó la factura");
            }

            // Obtener el ID generado
            rsId = psFactura.getGeneratedKeys();
            int facturaId = -1;

            if (rsId.next()) {
                facturaId = rsId.getInt(1);
            } else {
                throw new SQLException("No se obtuvo el ID de la factura");
            }

            //GENERAR CUFE
            String numeroFactura = prefijo + "-" + String.format("%06d", facturaId);

            // Obtener fecha actual para el CUFE
            java.util.Date fechaFactura = new java.util.Date();

            // Generar CUFE usando la clase GeneradorCUFE
            String cufe = util.GeneradorCUFE.generarCUFESimple(
                    numeroFactura,
                    fechaFactura,
                    baseIva,
                    valorIva,
                    total,
                    nitEmisor,
                    numeroDocumento,
                    claveTecnica
            );

            //ACTUALIZAR FACTURA CON CUFE Y NÚMERO REAL
            PreparedStatement psUpdate = con.prepareStatement(
                    "UPDATE factura SET numero_factura = ?, consecutivo = ?, cufe = ? WHERE id = ?"
            );
            psUpdate.setString(1, numeroFactura);
            psUpdate.setInt(2, facturaId);
            psUpdate.setString(3, cufe);
            psUpdate.setInt(4, facturaId);
            psUpdate.executeUpdate();
            psUpdate.close();

            return facturaId;

        } finally {
            if (rsId != null) {
                rsId.close();
            }
            if (rsConfig != null) {
                rsConfig.close();
            }
            if (rsVenta != null) {
                rsVenta.close();
            }
            if (psFactura != null) {
                psFactura.close();
            }
            if (psConfig != null) {
                psConfig.close();
            }
            if (psVenta != null) {
                psVenta.close();
            }
        }
    }

    // IMPRIMIR FACTURA
    private void imprimirFactura(int facturaId) {

        Connection con = null;

        try {
            if (facturaId <= 0) {
                throw new Exception("ID de factura inválido: " + facturaId);
            }

            // Conectar a la BD
            con = ConexionDB.getConnection();
            if (con == null) {
                throw new Exception("No hay conexión con la base de datos");
            }

            // OBTENER EL CUFE DE LA FACTURA
            String sqlCufe = "SELECT cufe FROM factura WHERE id = ?";
            PreparedStatement psCufe = con.prepareStatement(sqlCufe);
            psCufe.setInt(1, facturaId);
            ResultSet rsCufe = psCufe.executeQuery();

            String cufe = null;
            if (rsCufe.next()) {
                cufe = rsCufe.getString("cufe");
            }

            rsCufe.close();
            psCufe.close();

            // GENERAR CÓDIGO QR CON EL CUFE
            java.awt.Image qrCodeImage = null;

            if (cufe != null && !cufe.isEmpty() && !cufe.equals("TEMP")) {
                try {
                    // Generar QR usando la clase GeneradorQR
                    qrCodeImage = util.GeneradorQr.generarQRComoImage(cufe);
                } catch (Exception e) {
                    System.err.println("Error al generar código QR: " + e.getMessage());
                    // Continuar sin QR si hay error
                }
            }

            // PREPARAR REPORTE JASPER
            String rutaJRXML = "src/reportes/Facturas.jrxml";
            File archivo = new File(rutaJRXML);

            if (!archivo.exists()) {
                throw new Exception(
                        "No se encontró el archivo de reporte.\nRuta: " + archivo.getAbsolutePath()
                );
            }

            // Compilar el JRXML
            JasperReport reporte = JasperCompileManager.compileReport(rutaJRXML);

            // Parámetros para el reporte
            Map<String, Object> params = new HashMap<>();
            params.put("idFactura", facturaId);

            // Agregar QR como parámetro (puede ser null si no se generó)
            params.put("qrCodeImage", qrCodeImage);

            // Llenar reporte con datos
            JasperPrint print = JasperFillManager.fillReport(
                    reporte,
                    params,
                    con
            );

            // Mostrar visor
            JasperViewer.viewReport(print, false);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error al imprimir factura:\n" + e.getMessage()
            );
        } finally {
            ConexionDB.closeConnection(con);
        }
    }

    // UTILIDADES 
    private double getDouble(String txt) {
        if (txt == null || txt.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(txt.replace("$", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            System.err.println("Error al convertir a double: '" + txt + "'");
            return 0.0;
        }
    }

    private void mensaje(String msg) {
        JOptionPane.showMessageDialog(vista, msg);
    }
}
