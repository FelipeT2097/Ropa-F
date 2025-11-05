/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Conexion_DB;

/**
 *
 * @author piper
 */
public class DProveedores {

    private Integer id;
    private String nombreProveedor;
    private String tipoDocumento;
    private String numeroDocumento;
    private String genero;
    private String telefono;
    private String correoElectronico;
    // private String user_type;

    public DProveedores() {

    }

    public DProveedores(Integer id, String nombreProveedor, String tipoDocumento, String numeroDocumento, String genero, String telefono, String correoElectronico) {

        this.id = id;
        this.nombreProveedor = nombreProveedor;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.genero = genero;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public static void insertarProveedores(DProveedores proveedores) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = Conexion_DB.getConnection();
            ps = con.prepareStatement(
                "INSERT INTO `proveedores`(`nombre_proveedor`,`tipo_documento`, " +
                "`numero_documento`, `genero`, `telefono`,`correo_electronico`) " +
                "VALUES (?,?,?,?,?,?)"
            );

            ps.setString(1, proveedores.getNombreProveedor());
            ps.setString(2, proveedores.getTipoDocumento());
            ps.setString(3, proveedores.getNumeroDocumento());
            ps.setString(4, proveedores.getGenero());
            ps.setString(5, proveedores.getTelefono());
            ps.setString(6, proveedores.getCorreoElectronico());

            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Nuevo proveedor Agregado");
            } else {
                JOptionPane.showMessageDialog(null, "Algo corre, valida los datos");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Error al insertar el proveedor: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(DProveedores.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<DProveedores> proveeList(String prove) {
        ArrayList<DProveedores> prove_List = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT `id`, `nombre_proveedor`,`tipo_documento`," +
                      "`numero_documento`, `genero`, `telefono`, `correo_electronico` " +
                      "FROM `proveedores`";

        try {
            cn = Conexion_DB.getConnection();
            ps = cn.prepareStatement(query);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                DProveedores proveedores = new DProveedores(
                    rs.getInt("id"),
                    rs.getString("nombre_proveedor"),
                    rs.getString("tipo_documento"),
                    rs.getString("numero_documento"),
                    rs.getString("genero"),
                    rs.getString("telefono"),
                    rs.getString("correo_electronico")
                );
                prove_List.add(proveedores);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DProveedores.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (cn != null) cn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return prove_List;
    }

    public static void actualizarProveedores(DProveedores proveedores) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = Conexion_DB.getConnection();
            ps = con.prepareStatement(
                "UPDATE `proveedores` SET `nombre_proveedor`=?, `tipo_documento`=?, " +
                "`numero_documento`=?, `genero`=?, `telefono`=?, `correo_electronico`=? " +
                "WHERE `id` = ?"
            );

            ps.setString(1, proveedores.getNombreProveedor());
            ps.setString(2, proveedores.getTipoDocumento());
            ps.setString(3, proveedores.getNumeroDocumento());
            ps.setString(4, proveedores.getGenero());
            ps.setString(5, proveedores.getTelefono());
            ps.setString(6, proveedores.getCorreoElectronico());
            ps.setInt(7, proveedores.getId());

            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Proveedor Actualizado");
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Algo salió mal, no se pudo actualizar el proveedor.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DProveedores.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void eliminarProveedor(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = Conexion_DB.getConnection();
            ps = con.prepareStatement("DELETE FROM `proveedores` WHERE `id` = ?");
            ps.setInt(1, id);

            int YesOrNo = JOptionPane.showConfirmDialog(null, 
                "Realmente desea eliminar este proveedor", 
                "Eliminar usuarios", JOptionPane.YES_NO_OPTION);
                
            if (YesOrNo == 0) {
                if (ps.executeUpdate() != 0) {
                    JOptionPane.showMessageDialog(null, "Proveedor Eliminado");
                } else {
                    JOptionPane.showMessageDialog(null, "Algo salio mal");
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(DProveedores.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
