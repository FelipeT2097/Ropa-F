/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Connection;
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
public class PerfilUsuario {

    Connection connection;

    private Integer id;
    private String nombre_completo;
    private String nombre_usuario;
    private String telefono;
    private String correo_electronico;

    public PerfilUsuario() {
    }

    public PerfilUsuario(Integer ID, String FNAME, String UNAME, String TEL, String EMAIL) {
        this.id = ID;
        this.nombre_completo = FNAME;
        this.nombre_usuario = UNAME;
        this.telefono = TEL;
        this.correo_electronico = EMAIL;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo_electronico() {
        return correo_electronico;
    }

    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }

    // obtener la lista de usuarios
    public ArrayList<PerfilUsuario> listaUsuarios() {

        ArrayList<PerfilUsuario> lista_usuarios = new ArrayList<>();
        connection = ConexionDB.getConnection();
        Statement st;
        ResultSet rs;
        PreparedStatement ps;

        String query = "SELECT `id`, `nombre_completo`, `nombre_usuario`, `telefono`, `correo_electronico` FROM `perfilUsuario`";

        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            PerfilUsuario perfilUsuario;
            while (rs.next()) {
                perfilUsuario = new PerfilUsuario(rs.getInt("id"),
                        rs.getString("nombre_completo"),
                        rs.getString("nombre_usuario"),
                        rs.getString("telefono"),
                        rs.getString("correo_electronico")
                );

                lista_usuarios.add(perfilUsuario);
            }

        } catch (SQLException ex) {
            Logger.getLogger(PerfilUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista_usuarios;
    }

    // insertar un nuevo usuario
    public static void insertarPerfilUsuario(PerfilUsuario perfilUsuario) {
        Connection con = ConexionDB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("INSERT INTO `perfilUsuario`(`nombre_completo`, `nombre_usuario`, `telefono`, `correo_electronico`) VALUES (?,?,?,?)");

            ps.setString(1, perfilUsuario.getNombre_completo());
            ps.setString(2, perfilUsuario.getNombre_usuario());
            ps.setString(3, perfilUsuario.getTelefono());
            ps.setString(4, perfilUsuario.getCorreo_electronico());

            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Nuevo Usuario Agregado");

            } else {
                JOptionPane.showMessageDialog(null, "Algo salió mal");

            }

        } catch (SQLException ex) {
            Logger.getLogger(PerfilUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // actualizar los datos del usuario
    public static void actualizarPerfilUsuario(PerfilUsuario perfilUsuario) {
        Connection con = ConexionDB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("UPDATE `perfilUsuario` SET `nombre_completo`=?,`nombre_usuario`=?,`telefono`=?,`correo_electronico`=? WHERE `id`=?");

            ps.setString(1, perfilUsuario.getNombre_completo());
            ps.setString(2, perfilUsuario.getNombre_usuario());
            ps.setString(3, perfilUsuario.getTelefono());
            ps.setString(4, perfilUsuario.getCorreo_electronico());
            ps.setInt(5, perfilUsuario.getId());

            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Usuario Actualizado");

            } else {
                JOptionPane.showMessageDialog(null, "Algo salió mal");

            }

        } catch (SQLException ex) {
            Logger.getLogger(PerfilUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // eliminar usuario por id
    public static void eliminarPerfilUsuario(Integer perfilUsuarioId) {
        Connection con = ConexionDB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("DELETE FROM `perfilUsuario` WHERE `id` = ?");

            ps.setInt(1, perfilUsuarioId);

            // mostrar un mensaje de confirmación antes de eliminar el usuario
            int YesOrNo = JOptionPane.showConfirmDialog(null, "¿Realmente quieres eliminar este usuario?", "Eliminar Usuario", JOptionPane.YES_NO_OPTION);
            if (YesOrNo == 0) {

                if (ps.executeUpdate() != 0) {
                    JOptionPane.showMessageDialog(null, "Usuario Eliminado");
                } else {
                    JOptionPane.showMessageDialog(null, "Algo salió mal");
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(PerfilUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
