/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.ConexionDB;

/**
 *
 * @author piper
 */
public class Venta {

    private Integer id;
    private Date fecha;
    private String cliente;
    private Double subtotal;
    private Double descuento;
    private Double total;
    private String metodoPago;
    private String estado;
    private String usuario;
    private String tipoDocumentoCliente;
    private String numeroDocumentoCliente;

    public Venta() {
    }

    // Constructor completo (usado cuando se quiere crear un objeto con todos los datos)
    public Venta(Integer id, Date fecha, String cliente, Double subtotal,
            Double descuento, Double total, String metodoPago,
            String estado, String usuario, String tipoDocumentoCliente, String numeroDocumentoCliente) {
        this.id = id;
        this.fecha = fecha;
        this.cliente = cliente;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.usuario = usuario;
        this.usuario = tipoDocumentoCliente;
        this.usuario = numeroDocumentoCliente;
    }

    // Constructor sin ID (útil cuando se crea una nueva venta que aún no está en la BD)
    public Venta(String cliente, Double subtotal, Double descuento,
            Double total, String metodoPago, String usuario, String tipoDocumentoCliente, String numeroDocumentoCliente) {
        this.cliente = cliente;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = "COMPLETADA"; // Valor por defecto al registrar una nueva venta
        this.usuario = usuario;
        this.usuario = tipoDocumentoCliente;
        this.usuario = numeroDocumentoCliente;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTipoDocumentoCliente() {
        return usuario;

    }

    public void setTipoDocumentoCliente(String tipoDocumentoCliente) {
        this.tipoDocumentoCliente = tipoDocumentoCliente;
    }
    
        public String getNumeroDocumentoCliente() {
        return usuario;

    }

    public void setTipoNumeroDocumentoCliente(String numeroDocumentoCliente) {
        this.numeroDocumentoCliente = numeroDocumentoCliente;
    }

    
    //Registra una nueva venta en la base de datos. Inserta la venta y devuelve
    public static int registrarVenta(Venta venta) {
        int ventaId = -1; // ID por defecto si no se inserta nada
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Sentencia SQL para insertar una venta
        // NOW() agrega la fecha actual automáticamente
        String query = "INSERT INTO ventas (fecha, cliente, subtotal, descuento, total, metodo_pago, estado, usuario, tipo_documento_cliente, numero_documento_cliente) "
                + "VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            con = ConexionDB.getConnection(); // Conexión a la base de datos
            ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            // Se asignan los valores a los parámetros SQL
            ps.setString(1, venta.getCliente());
            ps.setDouble(2, venta.getSubtotal());
            ps.setDouble(3, venta.getDescuento());
            ps.setDouble(4, venta.getTotal());
            ps.setString(5, venta.getMetodoPago());
            ps.setString(6, venta.getEstado());
            ps.setString(7, venta.getUsuario());
            ps.setString(8, venta.getTipoDocumentoCliente());
            ps.setString(9, venta.getNumeroDocumentoCliente());

            // Ejecuta la consulta y verifica si afectó filas
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                // Obtiene el ID generado por la base de datos
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    ventaId = rs.getInt(1);
                }
                JOptionPane.showMessageDialog(null,
                        "Venta registrada correctamente. ID: " + ventaId,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            // Muestra el error en consola y en una alerta
            Logger.getLogger(Venta.class.getName()).log(Level.SEVERE,
                    "Error al registrar venta: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null,
                    "Error al registrar venta: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Cierra recursos abiertos para evitar fugas de memoria
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
                Logger.getLogger(Venta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ventaId;
    }

    /**
     * Obtiene una lista de todas las ventas registradas.
     *
     * @return Lista con todas las ventas ordenadas por fecha descendente.
     */
    public ArrayList<Venta> listarVentas() {
        ArrayList<Venta> lista = new ArrayList<>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "SELECT * FROM ventas ORDER BY fecha DESC";

        try {
            con = ConexionDB.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query);

            // Recorre los resultados y crea objetos Venta
            while (rs.next()) {
                Venta venta = new Venta(
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        rs.getString("cliente"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("descuento"),
                        rs.getDouble("total"),
                        rs.getString("metodo_pago"),
                        rs.getString("estado"),
                        rs.getString("usuario"),
                        rs.getString("tipo_documento_cliente"),
                        rs.getString("numero_documento_cliente")
                );
                lista.add(venta);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Venta.class.getName()).log(Level.SEVERE,
                    "Error al listar ventas: " + ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Venta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lista;
    }

    /*Busca ventas según un texto (por ID, cliente, método de pago o estado).
     * @return Lista con las ventas que coincidan.
     */
    public ArrayList<Venta> buscarVentas(String filtro) {
        ArrayList<Venta> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM ventas WHERE "
                + "CONCAT(id, cliente, metodo_pago, estado) LIKE ? "
                + "ORDER BY fecha DESC";

        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, "%" + filtro + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Venta venta = new Venta(
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        rs.getString("cliente"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("descuento"),
                        rs.getDouble("total"),
                        rs.getString("metodo_pago"),
                        rs.getString("estado"),
                        rs.getString("usuario"),
                        rs.getString("tipo_documento_cliente"),
                        rs.getString("numero_documento_cliente")
                );
                lista.add(venta);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Venta.class.getName()).log(Level.SEVERE,
                    "Error al buscar ventas: " + ex.getMessage(), ex);
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
                Logger.getLogger(Venta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lista;
    }

    /*Obtiene una venta específica por su ID.
     return Objeto Venta o null si no existe.
     */
    public static Venta obtenerVentaPorId(int id) {
        Venta venta = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM ventas WHERE id = ?";

        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                venta = new Venta(
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        rs.getString("cliente"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("descuento"),
                        rs.getDouble("total"),
                        rs.getString("metodo_pago"),
                        rs.getString("estado"),
                        rs.getString("usuario"),
                        rs.getString("tipo_documento_cliente"),
                        rs.getString("numero_documento_cliente")
                );
            }

        } catch (SQLException ex) {
            Logger.getLogger(Venta.class.getName()).log(Level.SEVERE,
                    "Error al obtener venta: " + ex.getMessage(), ex);
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
                Logger.getLogger(Venta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return venta;
    }

    /*Cambia el estado de una venta a "ANULADA".
    * return true si la operación fue exitosa.
     */
    public static boolean anularVenta(int ventaId) {
        Connection con = null;
        PreparedStatement ps = null;
        boolean resultado = false;

        String query = "UPDATE ventas SET estado = 'ANULADA' WHERE id = ?";

        try {
            con = ConexionDB.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, ventaId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                resultado = true;
                JOptionPane.showMessageDialog(null,
                        "Venta anulada correctamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Venta.class.getName()).log(Level.SEVERE,
                    "Error al anular venta: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null,
                    "Error al anular venta: " + ex.getMessage(),
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
                Logger.getLogger(Venta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return resultado;
    }

    // Representación textual de la venta (útil para depuración o logs)
    @Override
    public String toString() {
        return "Venta{" + "id=" + id + ", fecha=" + fecha + ", cliente=" + cliente
                + ", total=" + total + ", metodoPago=" + metodoPago + ", tipoDocumentoCliente=" + tipoDocumentoCliente 
                + ", numeroDocumentoCliente=" + numeroDocumentoCliente + '}';
    }
}
