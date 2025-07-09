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

    
     Connection connection;
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
        Connection con = Conexion_DB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("INSERT INTO `proveedores`(`nombre_proveedor`,`tipo_documento`, `numero_documento`, `genero`, `telefono`,`correo_electronico`) VALUES (?,?,?,?,?,?)");

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
            JOptionPane.showMessageDialog(null, "Error al insertar el proveedor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(DProveedores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<DProveedores> proveeList(String prove) {

        ArrayList<DProveedores> prove_List = new ArrayList<>();
        Connection cn = Conexion_DB.getConnection(); // Consistencia en la conexión con el código anterior

        PreparedStatement ps;
        ResultSet rs;

        String query = "SELECT `id`, `nombre_proveedor`,`tipo_documento`,`numero_documento`, `genero`, `telefono`, `correo_electronico` FROM `proveedores` " ;
              

        try {
        ps = connection.prepareStatement(query);
            ps.setString(1, "%" + prove + "%");
            rs = ps.executeQuery();
            
            DProveedores proveedores;
            // El orden del constructor coincide con el orden del SELECT
            while (rs.next()) {
                proveedores = new DProveedores(
                        rs.getInt("id"),
                        rs.getString("nombre_proveedor"),
                        rs.getString("tipo_documento"),
                        rs.getString("numero_documento"),
                        rs.getString("genero"),
                        rs.getString("telefono"),
                        rs.getString("correo_electronico")                
                );
                prove_List.add(proveedores); // Añadir el proveedor a la lista
            }

        } catch (SQLException ex) {
            Logger.getLogger(DProveedores.class.getName()).log(Level.SEVERE, null, ex);
        }
        return prove_List; // Retornar la lista de usuarios
    }
    

//    public DefaultTableModel mostrarProveedor(DProveedores Mproveedores) {
//        Connection cn = Conexion_DB.getConnection();
//        DefaultTableModel miModelo = null;
//        try {
//            // Definir los títulos de las columnas de la tabla
//            String titulos[] = {"id", "Nombre", "TipoD", "Documento", "Genero", "Telefono", "Correo" };
//            String dts[] = new String[7];
//            miModelo = new DefaultTableModel(null, titulos);
//            // Llamada al procedimiento almacenado para mostrar/buscar usuarios
//            CallableStatement cst = cn.prepareCall("{call sp_mostrarbuscar_proveedores(?,?,?,?,?,?)}");
//            cst.setString(1, Mproveedores.getNombreProveedor());
//            ResultSet rs = cst.executeQuery();
//
//            // Recorrer los resultados y agregar las filas al modelo de tabla
//            while (rs.next()) {
//                dts[0] = rs.getString("id");
//                dts[1] = rs.getString("Nombre");
//                dts[2] = rs.getString("TipoD");
//                dts[3] = rs.getString("Documento");
//                dts[4] = rs.getString("Genero");
//                dts[5] = rs.getString("Telefono");
//                dts[6] = rs.getString("Correo");               
//                miModelo.addRow(dts);
//            }// Manejo de excepciones SQL
//        } catch (SQLException ex) {
//        }
//        return miModelo;// Retornar el modelo de tabla con los usuarios cargados
//    }

    public static void actualizarProveedores(DProveedores proveedores) {
    Connection con = Conexion_DB.getConnection();
    PreparedStatement ps;

    try {
        ps = con.prepareStatement("UPDATE `proveedores` SET `nombre_proveedor`=?, `tipo_documento`=?, `numero_documento`=?, `genero`=?, `telefono`=?, `correo_electronico`=? WHERE `id` = ?");

        // Establecer los parámetros para la consulta
        ps.setString(1, proveedores.getNombreProveedor());
        ps.setString(2, proveedores.getTipoDocumento());
        ps.setString(3, proveedores.getNumeroDocumento());
        ps.setString(4, proveedores.getGenero());
        ps.setString(5, proveedores.getTelefono());
        ps.setString(6, proveedores.getCorreoElectronico());
        ps.setInt(7, proveedores.getId());

        // Ejecutar la actualización
        if (ps.executeUpdate() !=0) {
            JOptionPane.showMessageDialog(null, "Proveedor Actualizado");
        } else {
            JOptionPane.showMessageDialog(null, "Algo salió mal, no se pudo actualizar el proveedor.");
        }
    } catch (SQLException ex) {
        Logger.getLogger(DProveedores.class.getName()).log(Level.SEVERE, null, ex);
    }
}



    public static void eliminarProveedor(Integer id) {
        Connection con = Conexion_DB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("DELETE FROM `proveedores` WHERE `id` = ?");
           
            ps.setInt(1, id);
        
            // show a confirmation message before deleting the product
            int YesOrNo = JOptionPane.showConfirmDialog(null, "Realmente desea eliminar este proveedor", "Eliminar usuarios", JOptionPane.YES_NO_OPTION);
            if (YesOrNo == 0) {

                if (ps.executeUpdate() != 0) {
                    JOptionPane.showMessageDialog(null, "Proveedor Eliminado");

                } else {
                    JOptionPane.showMessageDialog(null, "Algo salio mal");

                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(DProveedores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   }

