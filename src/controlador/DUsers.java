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
public class DUsers {

    static DUsers getUserById(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Connection connection;
    private Integer id;
    private String nombreCompleto;
    private String nombreUsuario;
    private String tipoDocumento;
    private String numeroDocumento;
    private String genero;
    private String telefono;
    private String correoElectronico;
    private String contraseña;
    
    
    

    public DUsers() {

    }

    public DUsers(Integer id, String nombreCompleto, String nombreUsuario, String tipoDocumento, String numeroDocumento, String genero, String telefono, String correoElectronico, String contraseña ) {

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

    
    public static void insertUser(DUsers usuarios) {
        Connection con = modelo.Conexion_DB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("INSERT INTO `usuarios`(`nombre_completo`, `nombre_usuario`, `tipo_documento`, `numero_documento`, `genero`, `telefono`,`correo_electronico`, `contraseña`) VALUES (?,?,?,?,?,?,?,?)");

            ps.setString(1, usuarios.getNombreCompleto());
            ps.setString(2, usuarios.getNombreUsuario());
            ps.setString(3, usuarios.getTipoDocumento());
            ps.setString(4, usuarios.getNumeroDocumento());
            ps.setString(5, usuarios.getGenero());
            ps.setString(6, usuarios.getTelefono());
            ps.setString(7, usuarios.getCorreoElectronico());
            ps.setString(8, usuarios.getContraseña());
            
            

            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Nuevo usuario Agregado");
            } else {
                JOptionPane.showMessageDialog(null, "Algo corre, valida los datos");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(DUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<DUsers> UsersList(String user) {

        ArrayList<DUsers> user_list = new ArrayList<>();
        Connection cn = Conexion_DB.getConnection(); // Consistencia en la conexión con el código anterior

        PreparedStatement ps;
        ResultSet rs;

        String query = "SELECT `id`, `nombre_completo`, `nombre_usuario`,`tipo_documento`,`numero_documento`, `genero`, `telefono`, `correo_electronico`,`contraseña`,  FROM `usuarios`"; 

        try {
             ps = connection.prepareStatement(query);
            ps.setString(1, "%" + user + "%");
            rs = ps.executeQuery();
            
            DUsers usuarios;
            // El orden del constructor coincide con el orden del SELECT
            while (rs.next()) {
                usuarios = new DUsers(
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
                user_list.add(usuarios); // Añadir el usuario a la lista
            }

        } catch (SQLException ex) {
            Logger.getLogger(DUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user_list; // Retornar la lista de usuarios
    }
    Connection cn = Conexion_DB.getConnection();

    public DefaultTableModel mostrarUsuarios(DUsers misUsuarios) {
        DefaultTableModel miModelo = null;
        try {
            // Definir los títulos de las columnas de la tabla
            String titulos[] = {"id", "Nombre", "Usuario", "TipoId", "Documento", "Genero", "Telefono", "Correo", "Contraseña" };
            String dts[] = new String[8];
            miModelo = new DefaultTableModel(null, titulos);
            // Llamada al procedimiento almacenado para mostrar/buscar usuarios
            CallableStatement cst = cn.prepareCall("{call sp_mostrarbuscar_usuarios(?,?,?,?,?,?,?,?)}");
            cst.setString(1, misUsuarios.getNombreUsuario());
            ResultSet rs = cst.executeQuery();

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
            }// Manejo de excepciones SQL
        } catch (SQLException ex) {
        }
        return miModelo;// Retornar el modelo de tabla con los usuarios cargados
    }

    public static void actualizarUsuarios(DUsers misUsuarios) {
        Connection con = modelo.Conexion_DB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("UPDATE `usuarios` SET `nombre_completo`=?, `nombre_usuario`=?, `tipo_documento`=?,`numero_documento`=?, `genero`=?, `telefono`=?, `correo_electronico`=?, `contraseña`=?  WHERE `id` = ?");

            // Establecer los parámetros para la consulta
            ps.setString(1, misUsuarios.getNombreCompleto());
            ps.setString(2, misUsuarios.getNombreUsuario());
            ps.setString(3, misUsuarios.getTipoDocumento());
            ps.setString(4, misUsuarios.getNumeroDocumento());
            ps.setString(5, misUsuarios.getGenero());
            ps.setString(6, misUsuarios.getTelefono());
            ps.setString(7, misUsuarios.getCorreoElectronico());
            ps.setString(8, misUsuarios.getContraseña());
            ps.setInt(9, misUsuarios.getId());

            // Ejecutar la actualización
            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Usuario Actualizado");
            } else {
                JOptionPane.showMessageDialog(null, "Algo salio mal");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void eliminarUsuarios(Integer id) {
        Connection con = modelo.Conexion_DB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("DELETE FROM `usuarios` WHERE `id` = ?");
           
            ps.setInt(1, id);

            // show a confirmation message before deleting the product
            int YesOrNo = JOptionPane.showConfirmDialog(null, "Realmente desea eliminar este usuario", "Eliminar usuarios", JOptionPane.YES_NO_OPTION);
            if (YesOrNo == 0) {

                if (ps.executeUpdate() != 0) {
                    JOptionPane.showMessageDialog(null, "Usuario Eliminado");

                } else {
                    JOptionPane.showMessageDialog(null, "Algo salio mal");

                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(DUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
