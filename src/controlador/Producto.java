/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.Conexion_DB;

/**
 *
 * @author piper
 */
public class Producto {

    Connection connection;

    private Integer id;
    private String nombre;
    private String precio;
    private Integer cantidad;
    private String talla;
    private String color;
    private String genero;

    public Producto() {
    }

    public Producto(Integer id, String nombre, String precio, Integer cantidad, String talla, String color, String genero) {

        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.talla = talla;
        this.color = color;
        this.genero = genero;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    // insertar un nuevo producto
    public static void insertarProducto(Producto producto) {
        Connection con = Conexion_DB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("INSERT INTO `producto`(`nombre`, `precio`, `cantidad`, `talla`, `color` , `genero`) VALUES (?,?,?,?,?,?)");

            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getPrecio());
            ps.setInt(3, producto.getCantidad());
            ps.setString(4, producto.getTalla());
            ps.setString(5, producto.getColor());
            ps.setString(6, producto.getGenero());

            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Nuevo Producto Agregado");

            } else {
                JOptionPane.showMessageDialog(null, "Algo Salio Mal");

            }

        } catch (SQLException ex) {
            Logger.getLogger(Producto.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Permitir la edición de todas las celdas excepto la columna de ID
        return columnIndex != 0; // Suponiendo que la primera columna es ID y no quieres que sea editable
    }

    // actualiza producto
   public static void actualizarProducto(Producto producto) {
        Connection con = Conexion_DB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("UPDATE `producto` SET  `nombre`=?, `precio`=?, `cantidad`=?, `talla`=?, `color`=?, `genero`=? WHERE `id` = ?");

            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getPrecio());
            ps.setInt(3, producto.getCantidad());
            ps.setString(4, producto.getTalla());
            ps.setString(5, producto.getColor());
            ps.setString(6, producto.getGenero());
            ps.setInt(7, producto.getId());

            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "Producto Actualizado");
            } else {
                JOptionPane.showMessageDialog(null, "Algo Salio Mal");

            }

        } catch (SQLException ex) {
            Logger.getLogger(Producto.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Eliminar producto por id
    public static void eliminarProducto(Integer codigo) {
        Connection con = Conexion_DB.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("DELETE FROM `producto` WHERE `id` = ?");

            ps.setInt(1, codigo);

            // mostrar un mensaje de confirmación antes de eliminar el producto
            int YesOrNo = JOptionPane.showConfirmDialog(null, "¿Desea realmente eliminar este producto?", "Producto Eliminado", JOptionPane.YES_NO_OPTION);
            if (YesOrNo == 0) {

                if (ps.executeUpdate() != 0) {
                    JOptionPane.showMessageDialog(null, "Producto Eliminado");

                } else {
                    JOptionPane.showMessageDialog(null, "Algo Salio Mal");

                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(Producto.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // obtener la lista de productos usando arraylist
    public ArrayList<Producto> productoList(String val) {
        ArrayList<Producto> producto_list = new ArrayList<>();
        connection = Conexion_DB.getConnection();
        ResultSet rs;
        PreparedStatement ps;

       
        String query = "SELECT `id`, `nombre`, `precio`, `cantidad`, `talla`, `color`, `genero` "
                + "FROM `producto` "
                + "WHERE CONCAT(`nombre`, `precio`, `cantidad`, `talla`, `color`, `genero`) LIKE ?";

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, "%" + val + "%");
            rs = ps.executeQuery();

            Producto prd;

            while (rs.next()) {
                prd = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("precio"),
                        rs.getInt("cantidad"),
                        rs.getString("talla"),
                        rs.getString("color"),
                        rs.getString("genero")
                );

                producto_list.add(prd);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Producto.class.getName()).log(Level.SEVERE, "Error al obtener productos: " + ex.getMessage(), ex);
        }

        return producto_list;
    }

    public void updateProduct(int productoId, int columnIndex, String newValue) {
        Connection con = Conexion_DB.getConnection();
        PreparedStatement ps;
        String query = "";

        // Definimos la consulta según el índice de columna
        switch (columnIndex) {
            case 1: // nombre
                query = "UPDATE `producto` SET `nombre` = ? WHERE `id` = ?";
                break;
            case 2: // precio
                query = "UPDATE `producto` SET `precio` = ? WHERE `id` = ?";
                break;
            case 3: // cantidad
                query = "UPDATE `producto` SET `cantidad` = ? WHERE `id` = ?";
                break;
            case 4: // talla
                query = "UPDATE `producto` SET `talla` = ? WHERE `id` = ?";
                break;
            case 5: // color
                query = "UPDATE `producto` SET `color` = ? WHERE `id` = ?";
                break;
            case 6: // genero
                query = "UPDATE `producto` SET `genero` = ? WHERE `id` = ?";
                break;
            default:
                throw new IllegalArgumentException("Columna no válida para actualización");
        }

        try {
            ps = con.prepareStatement(query);
            ps.setString(1, newValue);
            ps.setInt(2, productoId);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Producto actualizado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Producto.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al actualizar el producto: " + ex.getMessage());
        }
    }

}
