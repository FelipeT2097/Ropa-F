/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.Auditoria;
import javax.swing.JOptionPane;
import modelo.Usuario_Sesion;

/**
 *
 * @author piper
 */
public class FrmAgregarProductos extends javax.swing.JFrame {
    private java.util.HashMap<Integer, String> mapaProveedores = new java.util.HashMap<>();
    public Integer productoId;

    // Variables para manejo de proveedores
    

    /**
     * Obtiene el ID del proveedor por su nombre
     */
    private Integer obtenerProveedorId(String nombreProveedor) {
        for (java.util.Map.Entry<Integer, String> entry : mapaProveedores.entrySet()) {
            if (entry.getValue().equals(nombreProveedor)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Busca un producto por su código
     *
     * @return ID del producto o null si no existe
     */
    private Integer buscarProductoPorCodigo(String codigo) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pst = null;
        java.sql.ResultSet rs = null;

        try {
            conn = modelo.ConexionDB.getConnection();
            String sql = "SELECT id FROM productos WHERE codigo = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, codigo);
            rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Registra la compra en la base de datos (transacción completa)
     *
     * @return ID de la compra creada o -1 si falla
     */
    private int registrarCompraEnBD(String numeroFactura, Integer proveedorId,
            String proveedorNombre, String fechaCompra,
            double total, Integer productoId, String codigoProducto,
            String nombreProducto, int cantidad, double precioCompra,
            String observaciones) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pst = null;
        java.sql.ResultSet rs = null;

        try {
            conn = modelo.ConexionDB.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar compra (cabecera)
            String sqlCompra = "INSERT INTO compras (numero_factura, proveedor_id, proveedor_nombre, "
                    + "fecha_compra, subtotal, impuesto, total, estado, observaciones, usuario) "
                    + "VALUES (?, ?, ?, ?, ?, 0, ?, 'COMPLETADA', ?, ?)";

            pst = conn.prepareStatement(sqlCompra, java.sql.Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, numeroFactura);
            pst.setInt(2, proveedorId);
            pst.setString(3, proveedorNombre);
            pst.setString(4, fechaCompra);
            pst.setDouble(5, total);
            pst.setDouble(6, total);
            pst.setString(7, observaciones);
            pst.setString(8, Usuario_Sesion.getInstancia().getNombreUsuario());

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas == 0) {
                conn.rollback();
                return -1;
            }

            // Obtener ID de la compra
            rs = pst.getGeneratedKeys();
            int compraId = -1;
            if (rs.next()) {
                compraId = rs.getInt(1);
            }

            if (compraId == -1) {
                conn.rollback();
                return -1;
            }

            // 2. Insertar detalle de compra
            String sqlDetalle = "INSERT INTO detalle_compras (compra_id, producto_id, codigo_producto, "
                    + "nombre_producto, cantidad, precio_compra, subtotal) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pst = conn.prepareStatement(sqlDetalle);
            pst.setInt(1, compraId);
            pst.setInt(2, productoId);
            pst.setString(3, codigoProducto);
            pst.setString(4, nombreProducto);
            pst.setInt(5, cantidad);
            pst.setDouble(6, precioCompra);
            pst.setDouble(7, total);

            pst.executeUpdate();

            conn.commit(); // Confirmar transacción
            System.out.println("Compra registrada con ID: " + compraId);
            return compraId;

        } catch (Exception e) {
            System.err.println("Error al registrar compra: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return -1;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Actualiza el stock y precio de compra del producto
     */
    private boolean actualizarStockProducto(Integer productoId, int cantidad, double precioCompra) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pst = null;

        try {
            conn = modelo.ConexionDB.getConnection();
            String sql = "UPDATE productos SET cantidad = cantidad + ?, precio_compra = ? WHERE id = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, cantidad);
            pst.setDouble(2, precioCompra);
            pst.setInt(3, productoId);

            int filasAfectadas = pst.executeUpdate();
            System.out.println("Stock actualizado. Producto ID: " + productoId + " | Cantidad agregada: " + cantidad);
            return filasAfectadas > 0;

        } catch (Exception e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Convierte java.util.Date a String formato SQL (yyyy-MM-dd)
     */
    private String convertirFechaASQL(java.util.Date fecha) {
        if (fecha == null) {
            return null;
        }
        java.text.SimpleDateFormat formato = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return formato.format(fecha);
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        // Limpiar datos de compra
        jComboBox_proveedores.setSelectedIndex(0);
        jTextField_numero_factura.setText("");
        jDateChooser_fecha_compra.setDate(new java.util.Date());
        jTextArea_observaciones.setText("");

        // Limpiar datos del producto
        jTextField_codigo.setText("");
        jTextField_nombre_producto.setText("");
        jTextField_precio.setText("");
        jTextField_cantidad.setText("");
        jTextField_talla.setText("");
        jTextField_color.setText("");
        jComboBox_genero.setSelectedIndex(0);

        // Focus al primer campo
        jComboBox_proveedores.requestFocus();
    }

    public FrmAgregarProductos() {
        initComponents();

        // Cargar proveedores en el ComboBox
        cargarProveedores();

        //REGISTRAR ACCESO AL MÓDULO DE PRODUCTOS
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "Productos",
                    "Accedió al módulo de Agregar Productos"
            );
        } catch (Exception e) {
            System.err.println("Error al registrar acceso: " + e.getMessage());
        }
    }

    /**
     * Carga los proveedores desde la BD al ComboBox
     */
    private void cargarProveedores() {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pst = null;
        java.sql.ResultSet rs = null;

        try {
            conn = modelo.ConexionDB.getConnection();
            String sql = "SELECT id, nombre_proveedor FROM proveedores ORDER BY nombre_proveedor";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            // Limpiar ComboBox
            jComboBox_proveedores.removeAllItems();
            jComboBox_proveedores.addItem("Seleccione un proveedor...");

            // Cargar proveedores
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombrePorveedor = rs.getString("nombre_proveedor");
                mapaProveedores.put(id, nombrePorveedor);
                jComboBox_proveedores.addItem(nombrePorveedor);
            }

            System.out.println("Proveedores cargados: " + mapaProveedores.size());

        } catch (Exception e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar proveedores.\nVerifique la conexión a la base de datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel = new javax.swing.JLabel();
        jLabel_nombre_producto = new javax.swing.JLabel();
        jLabel_precio = new javax.swing.JLabel();
        jLabel_cantidad = new javax.swing.JLabel();
        jLabel_talla = new javax.swing.JLabel();
        jLabel_color = new javax.swing.JLabel();
        jLabel_genero = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jTextField_nombre_producto = new javax.swing.JTextField();
        jTextField_precio = new javax.swing.JTextField();
        jTextField_talla = new javax.swing.JTextField();
        jTextField_color = new javax.swing.JTextField();
        jButton_cancelar = new javax.swing.JButton();
        jComboBox_genero = new javax.swing.JComboBox<>();
        jLabel_codigo_producto = new javax.swing.JLabel();
        jTextField_codigo = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jTextField_cantidad = new javax.swing.JTextField();
        jLabel1_proveedor = new javax.swing.JLabel();
        jLabel_fecha_compra = new javax.swing.JLabel();
        jDateChooser_fecha_compra = new com.toedter.calendar.JDateChooser();
        jLabel_numero_factura = new javax.swing.JLabel();
        jTextField_numero_factura = new javax.swing.JTextField();
        jComboBox_proveedores = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea_observaciones = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));

        jLabel1.setFont(new java.awt.Font("Lucida Sans", 1, 36)); // NOI18N
        jLabel1.setText("COMPRAS");

        jLabel.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel.setText("DETALLE DE LA COMPRA");

        jLabel_nombre_producto.setText("Nombre:");

        jLabel_precio.setText("Precio:");

        jLabel_cantidad.setText("Cantidad:");

        jLabel_talla.setText("Talla:");

        jLabel_color.setText("Color:");

        jLabel_genero.setText("Genero:");

        btnAgregar.setBackground(new java.awt.Color(0, 153, 0));
        btnAgregar.setFont(new java.awt.Font("Lucida Sans", 1, 12)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/aprobado.png"))); // NOI18N
        btnAgregar.setText("REGISTRAR COMPRA");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/logo2.png"))); // NOI18N

        jTextField_nombre_producto.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jTextField_nombre_producto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_nombre_productoActionPerformed(evt);
            }
        });

        jTextField_precio.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));

        jTextField_talla.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));

        jTextField_color.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));

        jButton_cancelar.setBackground(new java.awt.Color(255, 51, 0));
        jButton_cancelar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_cancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Delect.png"))); // NOI18N
        jButton_cancelar.setText("CANCELAR");
        jButton_cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cancelarActionPerformed(evt);
            }
        });

        jComboBox_genero.setFont(new java.awt.Font("Lucida Sans", 1, 12)); // NOI18N
        jComboBox_genero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hombre", "Mujer" }));

        jLabel_codigo_producto.setText("Codigo:");

        jTextField_codigo.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));

        jTextField_cantidad.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));

        jLabel1_proveedor.setText("Proveedor:");

        jLabel_fecha_compra.setText("Fecha compra:");

        jLabel_numero_factura.setText("Numero Factura:");

        jTextField_numero_factura.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 0, 14)); // NOI18N
        jLabel2.setText("DATOS DE COMPRA");

        jLabel3.setText("Observaciones:");

        jTextArea_observaciones.setColumns(20);
        jTextArea_observaciones.setRows(5);
        jScrollPane1.setViewportView(jTextArea_observaciones);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addComponent(btnAgregar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_cancelar)
                .addGap(56, 56, 56))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1_proveedor)
                            .addComponent(jLabel_fecha_compra)
                            .addComponent(jLabel_numero_factura))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField_numero_factura)
                            .addComponent(jComboBox_proveedores, 0, 168, Short.MAX_VALUE)
                            .addComponent(jTextField_nombre_producto, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(jTextField_codigo)
                            .addComponent(jTextField_precio, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(jComboBox_genero, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_color)
                            .addComponent(jTextField_talla)
                            .addComponent(jTextField_cantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(jDateChooser_fecha_compra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(20, 20, 20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel)
                        .addGap(8, 8, 8)))
                .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addGap(2, 2, 2))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(121, 121, 121)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel_genero)
                    .addComponent(jLabel3)
                    .addComponent(jLabel_color)
                    .addComponent(jLabel_talla)
                    .addComponent(jLabel_cantidad)
                    .addComponent(jLabel_precio)
                    .addComponent(jLabel_nombre_producto, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_codigo_producto, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox_proveedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1_proveedor))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_numero_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_numero_factura))
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooser_fecha_compra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel_fecha_compra))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_codigo_producto))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_nombre_producto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_nombre_producto))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_precio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_precio))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_cantidad))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_talla)
                    .addComponent(jTextField_talla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_color, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_color))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_genero)
                    .addComponent(jComboBox_genero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(53, 53, 53))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // TODO add your handling code here:
       
        // VALIDAR TODOS LOS CAMPOS
         if (!verifFields()) {
            return; // Si hay errores, salir
        }
        // OBTENER DATOS DEL FORMULARIO
        // Datos del producto
        String codigo = jTextField_codigo.getText().trim();
        String nombre = jTextField_nombre_producto.getText().trim();
        String precioVenta = jTextField_precio.getText().trim();
        String talla = jTextField_talla.getText().trim();
        String color = jTextField_color.getText().trim();
        String genero = (String) jComboBox_genero.getSelectedItem();

        // Datos de la compra
        String proveedorNombre = (String) jComboBox_proveedores.getSelectedItem();
        Integer proveedorId = obtenerProveedorId(proveedorNombre);
        String numeroFactura = jTextField_numero_factura.getText().trim();
        java.util.Date fechaDate = jDateChooser_fecha_compra.getDate();
        String observaciones = jTextArea_observaciones.getText().trim();

        // Convertir valores numéricos
        int cantidad = Integer.parseInt(jTextField_cantidad.getText().trim());
        double precioCompra = Double.parseDouble(jTextField_precio.getText().trim());
        double totalCompra = cantidad * precioCompra;

        // Convertir fecha a String formato SQL
        String fechaCompra = convertirFechaASQL(fechaDate);

        // PASO 1: VERIFICAR SI EL PRODUCTO YA EXISTE
        Integer productoId = buscarProductoPorCodigo(codigo);

        if (productoId == null) {
            // El producto NO existe → Crearlo primero
            System.out.println("Producto no existe. Creando producto: " + codigo);

            modelo.Producto producto = new modelo.Producto(
                    null, codigo, nombre, precioVenta, 0, talla, color, genero
            );

            try {
                // Intentar insertar el producto
                modelo.Producto.insertarProducto(producto);

                System.out.println("Producto creado: " + codigo);

            } catch (Exception e) {
                System.err.println("Error al crear producto: " + e.getMessage());
                JOptionPane.showMessageDialog(
                        this,
                        "Error al crear el producto en el catálogo.\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Obtener el ID del producto recién creado
            productoId = buscarProductoPorCodigo(codigo);

            if (productoId == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al obtener el ID del producto creado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            System.out.println("Producto creado con ID: " + productoId);
        } else {
            System.out.println("Producto ya existe con ID: " + productoId);
        }

        // PASO 2: REGISTRAR LA COMPRA EN LA BD
        int compraId = registrarCompraEnBD(
                numeroFactura, proveedorId, proveedorNombre, fechaCompra,
                totalCompra, productoId, codigo, nombre, cantidad,
                precioCompra, observaciones
        );

        if (compraId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar la compra.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // PASO 3: ACTUALIZAR STOCK DEL PRODUCTO
        boolean stockActualizado = actualizarStockProducto(productoId, cantidad, precioCompra);

        if (!stockActualizado) {
            JOptionPane.showMessageDialog(
                    this,
                    "La compra se registró pero hubo un error al actualizar el stock.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }

        // PASO 4: REGISTRAR EN AUDITORÍA
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrar(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "CREAR",
                    "Compras",
                    "Registró compra - Factura: " + numeroFactura
                    + " | Producto: " + codigo + " - " + nombre
                    + " | Cantidad: " + cantidad
                    + " | Precio compra: $" + String.format("%,.0f", precioCompra)
                    + " | Total: $" + String.format("%,.0f", totalCompra)
                    + " | Proveedor: " + proveedorNombre
            );
            System.out.println("Compra registrada en auditoría");
        } catch (Exception e) {
            System.err.println("Error al registrar en auditoría: " + e.getMessage());
        }

        // MOSTRAR MENSAJE DE ÉXITO
        JOptionPane.showMessageDialog(
                this,
                "✓ COMPRA REGISTRADA EXITOSAMENTE\n\n"
                + "Factura: " + numeroFactura + "\n"
                + "Proveedor: " + proveedorNombre + "\n"
                + "Producto: " + nombre + " (" + codigo + ")\n"
                + "Talla: " + talla + " | Color: " + color + "\n"
                + "Cantidad: " + cantidad + " unidades\n"
                + "Precio compra: $" + String.format("%,.0f", precioCompra) + "\n"
                + "Total: $" + String.format("%,.0f", totalCompra) + "\n\n"
                + "Stock actualizado en inventario",
                "Compra Exitosa",
                JOptionPane.INFORMATION_MESSAGE
        );
        limpiarCampos();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void jButton_cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cancelarActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton_cancelarActionPerformed

    private void jTextField_nombre_productoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_nombre_productoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_nombre_productoActionPerformed

    public boolean verifFields() {
        if (jTextField_numero_factura.getText().equals("") || jDateChooser_fecha_compra.getDate() == null
                || jTextField_cantidad.getText().equals("") || jTextField_precio.getText().equals("")
                || jTextField_nombre_producto.getText().equals("") || jTextField_talla.getText().equals("")
                || jTextField_color.getText().equals("") || jComboBox_genero.getSelectedItem() == null
                || jComboBox_proveedores.getSelectedItem() == null || jTextArea_observaciones.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Uno o más campos están vacíos", "Campos vacíos", 0);
            return false;
        } else {
            try {
                String.valueOf(jTextField_precio.getText());
                return true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Valores Invalidos", 0);
                return false;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmAgregarProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmAgregarProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmAgregarProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmAgregarProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmAgregarProductos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton jButton_cancelar;
    public javax.swing.JComboBox<String> jComboBox_genero;
    private javax.swing.JComboBox<String> jComboBox_proveedores;
    private com.toedter.calendar.JDateChooser jDateChooser_fecha_compra;
    private javax.swing.JLabel jLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel1_proveedor;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_cantidad;
    private javax.swing.JLabel jLabel_codigo_producto;
    private javax.swing.JLabel jLabel_color;
    private javax.swing.JLabel jLabel_fecha_compra;
    private javax.swing.JLabel jLabel_genero;
    private javax.swing.JLabel jLabel_nombre_producto;
    private javax.swing.JLabel jLabel_numero_factura;
    private javax.swing.JLabel jLabel_precio;
    private javax.swing.JLabel jLabel_talla;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextArea jTextArea_observaciones;
    public javax.swing.JTextField jTextField_cantidad;
    public javax.swing.JTextField jTextField_codigo;
    public javax.swing.JTextField jTextField_color;
    public javax.swing.JTextField jTextField_nombre_producto;
    public javax.swing.JTextField jTextField_numero_factura;
    public javax.swing.JTextField jTextField_precio;
    public javax.swing.JTextField jTextField_talla;
    // End of variables declaration//GEN-END:variables
}
