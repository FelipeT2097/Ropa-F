/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

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
import modelo.ConexionDB;

/**
 *
 * @author piper
 */
public class Usuarios {
  
    private Integer id;
    private String nombreCompleto;
    private String nombreUsuario;
    private String tipoDocumento;
    private String numeroDocumento;
    private String genero;
    private String telefono;
    private String correoElectronico;
    private String contraseña;

    public Usuarios() {
    }

    public Usuarios(Integer id, String nombreCompleto, String nombreUsuario, 
                  String tipoDocumento, String numeroDocumento, String genero, 
                  String telefono, String correoElectronico, String contraseña) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.genero = genero;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.contraseña = contraseña;
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

     public static void insertUser(Usuarios usuarios) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = modelo.ConexionDB.getConnection();
            ps = con.prepareStatement(
                "INSERT INTO `usuarios`(`nombre_completo`, `nombre_usuario`, " +
                "`tipo_documento`, `numero_documento`, `genero`, `telefono`, " +
                "`correo_electronico`, `contraseña`) VALUES (?,?,?,?,?,?,?,?)"
            );

            ps.setString(1, usuarios.getNombreCompleto());
            ps.setString(2, usuarios.getNombreUsuario());
            ps.setString(3, usuarios.getTipoDocumento());
            ps.setString(4, usuarios.getNumeroDocumento());
            ps.setString(5, usuarios.getGenero());
            ps.setString(6, usuarios.getTelefono());
            ps.setString(7, usuarios.getCorreoElectronico());
            ps.setString(8, usuarios.getContraseña());

            // DEBUG
            System.out.println("Insertando usuario en BD...");
            System.out.println("Contraseña a guardar: " + usuarios.getContraseña());
            System.out.println("Longitud: " + usuarios.getContraseña().length());
            
            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Nuevo usuario Agregado");
            } else {
                JOptionPane.showMessageDialog(null, "Algo falló, valida los datos");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Error al insertar usuario: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Usuarios> UsersList(String user) {
        ArrayList<Usuarios> user_list = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

               String query = "SELECT `id`, `nombre_completo`, `nombre_usuario`, " +
                      "`tipo_documento`, `numero_documento`, `genero`, `telefono`, " +
                      "`correo_electronico`, `contraseña` FROM `usuarios`";

        try {
            cn = ConexionDB.getConnection();
            ps = cn.prepareStatement(query);  
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Usuarios usuarios = new Usuarios(
                    rs.getInt("id"),
                    rs.getString("nombre_completo"),
                    rs.getString("nombre_usuario"),
                    rs.getString("tipo_documento"),
                    rs.getString("numero_documento"),
                    rs.getString("genero"),
                    rs.getString("telefono"),
                    rs.getString("correo_electronico"),
                    rs.getString("contraseña")
                );
                user_list.add(usuarios);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (cn != null) cn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user_list;
    }

    public DefaultTableModel mostrarUsuarios(Usuarios misUsuarios) {
        DefaultTableModel miModelo = null;
        Connection cn = null;
        CallableStatement cst = null;
        ResultSet rs = null;
        
        try {
            cn = ConexionDB.getConnection();
            
            // Definir los títulos de las columnas de la tabla
            String titulos[] = {"id", "Nombre", "Usuario", "TipoId", "Documento", 
                               "Genero", "Telefono", "Correo", "Contraseña"};
            String dts[] = new String[9];  // CORREGIDO: eran 8, necesitas 9
            miModelo = new DefaultTableModel(null, titulos);
            
            // Llamada al procedimiento almacenado
            cst = cn.prepareCall("{call sp_mostrarbuscar_usuarios(?)}");
            cst.setString(1, misUsuarios.getNombreUsuario());
            rs = cst.executeQuery();

            // Recorrer los resultados y agregar las filas al modelo de tabla
            while (rs.next()) {
                dts[0] = rs.getString("idUsuario");
                dts[1] = rs.getString("Nombre");
                dts[2] = rs.getString("Usuario");
                dts[3] = rs.getString("TipoId");
                dts[4] = rs.getString("Documento");
                dts[5] = rs.getString("Genero");
                dts[6] = rs.getString("Telefono");
                dts[7] = rs.getString("Correo");
                dts[8] = rs.getString("Contraseña");

                miModelo.addRow(dts);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (cst != null) cst.close();
                if (cn != null) cn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return miModelo;
    }

    public static void actualizarUsuarios(Usuarios misUsuarios) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = modelo.ConexionDB.getConnection();
            ps = con.prepareStatement(
                "UPDATE `usuarios` SET `nombre_completo`=?, `nombre_usuario`=?, " +
                "`tipo_documento`=?, `numero_documento`=?, `genero`=?, `telefono`=?, " +
                "`correo_electronico`=?, `contraseña`=? WHERE `id` = ?"
            );

            ps.setString(1, misUsuarios.getNombreCompleto());
            ps.setString(2, misUsuarios.getNombreUsuario());
            ps.setString(3, misUsuarios.getTipoDocumento());
            ps.setString(4, misUsuarios.getNumeroDocumento());
            ps.setString(5, misUsuarios.getGenero());
            ps.setString(6, misUsuarios.getTelefono());
            ps.setString(7, misUsuarios.getCorreoElectronico());
            ps.setString(8, misUsuarios.getContraseña());
            ps.setInt(9, misUsuarios.getId());

            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Usuario Actualizado");
            } else {
                JOptionPane.showMessageDialog(null, "Algo salió mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void eliminarUsuarios(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = modelo.ConexionDB.getConnection();
            ps = con.prepareStatement("DELETE FROM `usuarios` WHERE `id` = ?");
            ps.setInt(1, id);

            int YesOrNo = JOptionPane.showConfirmDialog(null, 
                "Realmente desea eliminar este usuario", 
                "Eliminar usuarios", JOptionPane.YES_NO_OPTION);
                
            if (YesOrNo == 0) {
                if (ps.executeUpdate() != 0) {
                    JOptionPane.showMessageDialog(null, "Usuario Eliminado");
                } else {
                    JOptionPane.showMessageDialog(null, "Algo salió mal");
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static Usuarios getUserById(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
