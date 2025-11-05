/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.sql.*;
import java.util.ArrayList;  // Para crear listas de clientes
import java.util.logging.Level;  // Para registrar errores
import java.util.logging.Logger;  // Para guardar mensajes de error
import javax.swing.JOptionPane;  // Para mostrar ventanas de mensaje al usuario
import modelo.Conexion_DB;  // Nuestra clase que conecta con la base de datos

/**
 *
 * @author piper
 */
public class DClientes {
   
    // Estos son los DATOS que guardamos de cada cliente.
    // Cada uno corresponde a una columna de la tabla "clientes" en la BD.
    private Integer id;
    private String nombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private String genero;
    private String telefono;
    private String correoElectronico;
    private String direccion;
    private String ciudad;

    /**
     * Constructor VACÍO Se usa cuando queremos crear un cliente pero AÚN NO
     * tenemos sus datos. Ejemplo: DClientes cliente = new DClientes();
     */
    public DClientes() {

    }

    public DClientes(Integer id, String nombreCompleto, String tipoDocumento,
            String numeroDocumento, String genero, String telefono,
            String correoElectronico, String direccion, String ciudad) {
        // Asignamos cada parámetro al atributo correspondiente
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.genero = genero;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.direccion = direccion;
        this.ciudad = ciudad;

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public static void insertarClientes(DClientes cliente) {

        Connection con = null;  // La conexión a la base de datos
        PreparedStatement ps = null;  // La consulta SQL preparada

        try {

            con = Conexion_DB.getConnection();  // Obtener la conexión

            // PreparedStatement es SEGURO contra inyección SQL
            // Los signos ? son "marcadores de posición" que llenaremos después
            ps = con.prepareStatement(
                    "INSERT INTO `clientes`"
                    + // Insertaremos en la tabla clientes
                    "(`nombre_completo`, `tipo_documento`, `numero_documento`, "
                    + "`genero`, `telefono`, `correo_electronico`, `direccion`, "
                    + "`ciudad`, `estado`) "
                    + // Estas son las columnas
                    "VALUES (?,?,?,?,?,?,?,?,?)" // Estos son los 9 valores (?)
            );

            // Cada setString/setInt reemplaza un ? en orden
            ps.setString(1, cliente.getNombreCompleto());
            ps.setString(2, cliente.getTipoDocumento());
            ps.setString(3, cliente.getNumeroDocumento());
            ps.setString(4, cliente.getGenero());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getCorreoElectronico());
            ps.setString(7, cliente.getDireccion());
            ps.setString(8, cliente.getCiudad());

            // executeUpdate() devuelve el número de filas afectadas
            // Si es diferente de 0, significa que SÍ insertó algo
            if (ps.executeUpdate() != 0) {
                // Mostrar mensaje de ÉXITO
                JOptionPane.showMessageDialog(null, "✅ Cliente agregado exitosamente");
            } else {
                // Mostrar mensaje de ERROR (no se insertó nada)
                JOptionPane.showMessageDialog(null, "❌ Error al agregar cliente");
            }

        } catch (SQLException ex) {
            // Si algo sale mal (ej: el número de documento ya existe)
            // capturamos el error aquí
            JOptionPane.showMessageDialog(null,
                    "❌ Error al insertar cliente: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            // También lo guardamos en el log para los desarrolladores
            Logger.getLogger(DClientes.class.getName()).log(Level.SEVERE, null, ex);

        } finally {

            // El bloque FINALLY siempre se ejecuta, haya error o no
            // Debemos cerrar las conexiones para no desperdiciar recursos
            try {
                if (ps != null) {
                    ps.close();  // Cerrar el PreparedStatement
                }
                if (con != null) {
                    con.close();  // Cerrar la conexión
                }
            } catch (SQLException e) {
                e.printStackTrace();  // Mostrar error si falla al cerrar
            }
        }
    }

    public static ArrayList<DClientes> clienList() {

        ArrayList<DClientes> clienList = new ArrayList<>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM clientes ORDER BY nombre_completo ASC";

        try {

            con = Conexion_DB.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            // rs.next() avanza a la siguiente fila
            // Devuelve true si hay más filas, false si ya no hay más
            while (rs.next()) {

                DClientes cliente = new DClientes(
                        rs.getInt("id"),
                        rs.getString("nombre_completo"),
                        rs.getString("tipo_documento"),
                        rs.getString("numero_documento"),
                        rs.getString("genero"),
                        rs.getString("telefono"),
                        rs.getString("correo_electronico"),
                        rs.getString("direccion"),
                        rs.getString("ciudad")
                );
                clienList.add(cliente);
            }

        } catch (SQLException ex) {
            // Manejar errores (ej: si no existe la tabla)
            Logger.getLogger(DClientes.class.getName()).log(Level.SEVERE, null, ex);

        } finally {

            try {
                if (rs != null) {
                    rs.close();  // Cerrar el ResultSet primero
                }
                if (ps != null) {
                    ps.close();  // Luego el PreparedStatement
                }
                if (con != null) {
                    con.close();  // Finalmente la conexión
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return clienList;
    }

    public static void actualizarClientes(DClientes cliente) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = Conexion_DB.getConnection();

            ps = con.prepareStatement(
                    "UPDATE `clientes` SET "
                    + "`nombre_completo`=?, "
                    + // Actualizar nombre
                    "`tipo_documento`=?, "
                    + // Actualizar tipo documento
                    "`numero_documento`=?, "
                    + // etc...
                    "`genero`=?, "
                    + "`telefono`=?, "
                    + "`correo_electronico`=?, "
                    + "`direccion`=?, "
                    + "`ciudad`=?, "
                    + "WHERE `id` = ?" // IMPORTANTE: solo actualiza este ID
            );

            // Llenar los ? con los nuevos valores
            ps.setString(1, cliente.getNombreCompleto());
            ps.setString(2, cliente.getTipoDocumento());
            ps.setString(3, cliente.getNumeroDocumento());
            ps.setString(4, cliente.getGenero());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getCorreoElectronico());
            ps.setString(7, cliente.getDireccion());
            ps.setString(8, cliente.getCiudad());
            ps.setInt(10, cliente.getId());  // El último ? es el ID (WHERE)

            // Ejecutar la actualización
            if (ps.executeUpdate() != 0) {
                JOptionPane.showMessageDialog(null, "✅ Cliente actualizado");
            } else {
                JOptionPane.showMessageDialog(null, "❌ Error al actualizar cliente");
            }

        } catch (SQLException ex) {
            Logger.getLogger(DClientes.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Cerrar conexión
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void eliminarCliente(Integer id) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = Conexion_DB.getConnection();

            ps = con.prepareStatement("DELETE FROM `clientes` WHERE `id` = ?");
            ps.setInt(1, id);  // Llenar el ? con el ID

            // showConfirmDialog muestra una ventana con botones Sí/No
            // Devuelve 0 si el usuario hace click en SÍ
            int YesOrNo = JOptionPane.showConfirmDialog(null,
                    "¿Realmente desea eliminar este cliente?",
                    "Eliminar cliente",
                    JOptionPane.YES_NO_OPTION);

            // Si el usuario dijo SÍ (YesOrNo == 0)
            if (YesOrNo == 0) {
                // Ejecutar la eliminación
                if (ps.executeUpdate() != 0) {
                    JOptionPane.showMessageDialog(null, "✅ Cliente eliminado");
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Error al eliminar");
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(DClientes.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Cerrar conexión
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getNombreCompelto() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
