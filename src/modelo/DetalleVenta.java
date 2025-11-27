/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.ConexionDB;

/**
 *
 * @author piper
 */
public class DetalleVenta {

    private Integer id;
    private Integer ventaId;
    private Integer productoId;
    private String codigoProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    // Constructor vacío
    public DetalleVenta() {
    }

    // Constructor completo
    public DetalleVenta(Integer id, Integer ventaId, Integer productoId,
            String codigoProducto, String nombreProducto,
            Integer cantidad, Double precioUnitario, Double subtotal) {
        this.id = id;
        this.ventaId = ventaId;
        this.productoId = productoId;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // Constructor sin ID (para nuevos detalles)
    public DetalleVenta(Integer ventaId, Integer productoId,
            String codigoProducto, String nombreProducto,
            Integer cantidad, Double precioUnitario) {
        this.ventaId = ventaId;
        this.productoId = productoId;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVentaId() {
        return ventaId;
    }

    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    // Método auxiliar para calcular subtotal
    private void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = cantidad * precioUnitario;
        }
    }


    // MÉTODOS DE NEGOCIO
    public static boolean registrarDetalleVenta(DetalleVenta detalle) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean resultado = false;

        String query = "INSERT INTO detalle_ventas "
                + "(venta_id, producto_id, codigo_producto, nombre_producto, "
                + "cantidad, precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(query);

            ps.setInt(1, detalle.getVentaId());
            ps.setInt(2, detalle.getProductoId());
            ps.setString(3, detalle.getCodigoProducto());
            ps.setString(4, detalle.getNombreProducto());
            ps.setInt(5, detalle.getCantidad());
            ps.setDouble(6, detalle.getPrecioUnitario());
            ps.setDouble(7, detalle.getSubtotal());

            int affectedRows = ps.executeUpdate();
            resultado = (affectedRows > 0);

        } catch (SQLException ex) {
            Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE,
                    "Error al registrar detalle de venta: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null,
                    "Error al registrar detalle de venta: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return resultado;
    }

    /**
     * Registra múltiples detalles de venta en una transacción
     *
     * @param detalles Lista de detalles a insertar
     * @return true si todos se insertaron correctamente
     */
    public static boolean registrarDetallesVenta(ArrayList<DetalleVenta> detalles) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean resultado = false;

        String query = "INSERT INTO detalle_ventas "
                + "(venta_id, producto_id, codigo_producto, nombre_producto, "
                + "cantidad, precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false); // Iniciar transacción

            ps = con.prepareStatement(query);

            for (DetalleVenta detalle : detalles) {
                ps.setInt(1, detalle.getVentaId());
                ps.setInt(2, detalle.getProductoId());
                ps.setString(3, detalle.getCodigoProducto());
                ps.setString(4, detalle.getNombreProducto());
                ps.setInt(5, detalle.getCantidad());
                ps.setDouble(6, detalle.getPrecioUnitario());
                ps.setDouble(7, detalle.getSubtotal());

                ps.addBatch();
            }

            ps.executeBatch();
            con.commit(); // Confirmar transacción
            resultado = true;

        } catch (SQLException ex) {
            if (con != null) {
                try {
                    con.rollback(); // Revertir transacción en caso de error
                } catch (SQLException rollbackEx) {
                    Logger.getLogger(DetalleVenta.class.getName())
                            .log(Level.SEVERE, null, rollbackEx);
                }
            }
            Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE,
                    "Error al registrar detalles de venta: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null,
                    "Error al registrar detalles de venta: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return resultado;
    }

    public static ArrayList<DetalleVenta> obtenerDetallesPorVenta(int ventaId) {
        ArrayList<DetalleVenta> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM detalle_ventas WHERE venta_id = ?";

        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, ventaId);
            rs = ps.executeQuery();

            while (rs.next()) {
                DetalleVenta detalle = new DetalleVenta(
                        rs.getInt("id"),
                        rs.getInt("venta_id"),
                        rs.getInt("producto_id"),
                        rs.getString("codigo_producto"),
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unitario"),
                        rs.getDouble("subtotal")
                );
                lista.add(detalle);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE,
                    "Error al obtener detalles de venta: " + ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lista;
    }

    //Actualiza el stock de productos después de una venta
     
    public static boolean actualizarStockProductos(ArrayList<DetalleVenta> detalles) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean resultado = false;

        String query = "UPDATE productos SET cantidad = cantidad - ? WHERE id = ?";

        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false); // Iniciar transacción

            ps = con.prepareStatement(query);

            for (DetalleVenta detalle : detalles) {
                ps.setInt(1, detalle.getCantidad());
                ps.setInt(2, detalle.getProductoId());
                ps.addBatch();
            }

            ps.executeBatch();
            con.commit(); // Confirmar transacción
            resultado = true;

        } catch (SQLException ex) {
            if (con != null) {
                try {
                    con.rollback(); // Revertir en caso de error
                } catch (SQLException rollbackEx) {
                    Logger.getLogger(DetalleVenta.class.getName())
                            .log(Level.SEVERE, null, rollbackEx);
                }
            }
            Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE,
                    "Error al actualizar stock: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null,
                    "Error al actualizar stock: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return resultado;
    }

    //Elimina todos los detalles de una venta (por si se anula)
    public static boolean eliminarDetallesPorVenta(int ventaId) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean resultado = false;

        String query = "DELETE FROM detalle_ventas WHERE venta_id = ?";

        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, ventaId);

            ps.executeUpdate();
            resultado = true;

        } catch (SQLException ex) {
            Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE,
                    "Error al eliminar detalles: " + ex.getMessage(), ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DetalleVenta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return resultado;
    }

    @Override
    public String toString() {
        return "DetalleVenta{" + "id=" + id + ", ventaId=" + ventaId
                + ", productoId=" + productoId + ", nombreProducto=" + nombreProducto
                + ", cantidad=" + cantidad + ", subtotal=" + subtotal + '}';
    }
}
