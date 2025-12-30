/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.Auditoria;
import controlador.RegistrarVentas;
import java.awt.Color;
import java.awt.Dimension;
import modelo.Clientes;
import modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import modelo.ConexionDB;
import modelo.Usuario_Sesion;

/**
 *
 * @author piper
 */
public class FrmVentas extends javax.swing.JInternalFrame {

    private RegistrarVentas controladorVentas;

    public FrmVentas() {

        super("Ventas", true, true, true, true); // Título, cerrable, redimensionable, movible, maximizable

        initComponents();

        //REGISTRAR ACCESO AL MÓDULO
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "Ventas",
                    "Accedió al módulo de Ventas"
            );
            System.out.println("Acceso al módulo de Ventas registrado");
        } catch (Exception e) {
            System.err.println("Error al registrar acceso: " + e.getMessage());
        }

        //Para que no baje de 0
        SpinnerNumberModel modelo = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        jSpinner_cantidad.setModel(modelo);

        // Configurar tamaño del jSpinner
        jSpinner_cantidad.setPreferredSize(new Dimension(60, jSpinner_cantidad.getPreferredSize().height));

        // Configurar tamaño del TextField stock
        jTextField1_cantidad_stock.setPreferredSize(new Dimension(60, 25));  // Ancho: 100, Alto: 25
        jTextField1_cantidad_stock.setEnabled(false);  // Para que no puedan escribir
        jTextField_nombre_producto.setEnabled(false);

        inicializarComboMetodoPago();  //Inicializa el ComboBox
        controladorVentas = new RegistrarVentas(this);
        configurarTablaProductos();
        jScrollPane_ventas.setViewportView(jTable_ventas);
        actualizarTotales();

        jTextField_nombre_completo_cliente.setEnabled(false);
        jTextField_subtotal.setEnabled(false);
        jTextField_iva.setEnabled(false);
        jTextField_total.setEnabled(false);

    }

    private DefaultTableModel modeloTabla;

    private void configurarTablaProductos() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Talla", "Color", "Precio", "Cantidad", "Subtotal"}, 0
        );

        jTable_ventas.setModel(modeloTabla);
    }

    private void limpiarCampos() {
        jTextField_buscar_codigo_producto.setText("");
        jTextField_cliente.setText("");
        jTextField_nombre_completo_cliente.setText("");
        jTextField_subtotal.setText("");
        jTextField_iva.setText("");
        jSpinner_cantidad.setValue(0);
        jTextField_total.setText("");
    }

    private void actualizarTotales() {
        double subtotal = 0.0;

        //Sumar todos los subtotales de la tabla
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object valor = modeloTabla.getValueAt(i, 6); // Columna "Subtotal"
            if (valor != null) {
                try {
                    subtotal += Double.parseDouble(valor.toString());
                } catch (NumberFormatException e) {
                    System.err.println("Error al convertir subtotal: " + valor);
                }
            }
        }

        //Calcular IVA (19%)
        double iva = subtotal * 0.19;

        //Calcular total general
        double total = subtotal + iva;

        //Mostrar en etiquetas con formato de moneda
        jTextField_subtotal.setText(String.format("$ %.2f", subtotal));
        jTextField_iva.setText(String.format("$ %.2f", iva));
        jTextField_total.setText(String.format("$ %.2f", total));
    }

    private void recalcularTotales() {
        double subtotal = 0.0;

        // Recorre todas las filas y suma los subtotales
        for (int i = 0; i < jTable_ventas.getRowCount(); i++) {
            Object valor = jTable_ventas.getValueAt(i, 6); // Columna "Subtotal"
            if (valor != null && !valor.toString().isEmpty()) {
                subtotal += Double.parseDouble(valor.toString());
            }
        }

        // Calcular IVA (19%) y total
        double iva = subtotal * 0.19;
        double total = subtotal + iva;

        // Mostrar los valores en los labels o textfields
        jTextField_subtotal.setText(String.format("$%.2f", subtotal));
        jTextField_iva.setText(String.format("$%.2f", iva)); // Aquí usas este campo como IVA
        jTextField_total.setText(String.format("$%.2f", total));
    }

    //BUSQUEDA DE CLIENTES
    //Busca un cliente por su número de documento y muestra su nombre.
    private void buscarClientePorDocumento() {

        String numeroDocumento = jTextField_cliente.getText().trim();
        if (numeroDocumento.isEmpty()) {
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo establecer conexión con la base de datos.",
                        "Error de Conexión",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String sql = "SELECT nombre_completo FROM clientes WHERE numero_documento = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, numeroDocumento);
            rs = ps.executeQuery();

            if (rs.next()) {
                jTextField_nombre_completo_cliente
                        .setText(rs.getString("nombre_completo"));
            } else {
                jTextField_nombre_completo_cliente.setText("");
                JOptionPane.showMessageDialog(
                        this,
                        "Cliente no encontrado.\nDocumento: " + numeroDocumento,
                        "Cliente no encontrado",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al buscar cliente:\n" + e.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error inesperado:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
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

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jProgressBar2 = new javax.swing.JProgressBar();
        jPanel1_venta = new javax.swing.JPanel();
        jLabel_titulo = new javax.swing.JLabel();
        jPanel_busqueda = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_buscar_codigo_producto = new javax.swing.JTextField();
        jButton_eliminar_producto = new javax.swing.JButton();
        jButton1_buscar_producto = new javax.swing.JButton();
        jPanel_inferior = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jLabel_metodo_pago = new javax.swing.JLabel();
        jComboBoxMetodoPago = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jTextField_nombre_completo_cliente = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel_botones = new javax.swing.JPanel();
        jButton_procesar_venta = new javax.swing.JButton();
        jButton_nueva_venta = new javax.swing.JButton();
        jButton_cancelar = new javax.swing.JButton();
        jPanel_totales = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel_subtotal = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField_iva = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel_total = new javax.swing.JLabel();
        jTextField_subtotal = new javax.swing.JTextField();
        jTextField_total = new javax.swing.JTextField();
        jLabel_cantidad = new javax.swing.JLabel();
        jSpinner_cantidad = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane_ventas = new javax.swing.JScrollPane();
        jTable_ventas = new javax.swing.JTable();
        jTextField_nombre_producto = new javax.swing.JTextField();
        jTextField1_cantidad_stock = new javax.swing.JTextField();
        jButton_agregar = new javax.swing.JButton();
        jLabel_estado_busqueda = new javax.swing.JLabel();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jScrollPane2.setViewportView(jEditorPane1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable2);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane5.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1_venta.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1_venta.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel_titulo.setFont(new java.awt.Font("Lucida Sans", 1, 24)); // NOI18N
        jLabel_titulo.setText("PUNTO DE VENTA");

        jPanel_busqueda.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_busqueda.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel3.setText("Codigo o nombre:");

        jLabel1.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel1.setText("Buscar Producto");

        jTextField_buscar_codigo_producto.setBackground(new java.awt.Color(255, 255, 255));
        jTextField_buscar_codigo_producto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_buscar_codigo_productoActionPerformed(evt);
            }
        });

        jButton_eliminar_producto.setBackground(new java.awt.Color(204, 0, 0));
        jButton_eliminar_producto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cancelar  o borrar.png"))); // NOI18N
        jButton_eliminar_producto.setText("ELIMINAR");
        jButton_eliminar_producto.setMaximumSize(new java.awt.Dimension(110, 38));
        jButton_eliminar_producto.setMinimumSize(new java.awt.Dimension(110, 38));
        jButton_eliminar_producto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminar_productoActionPerformed(evt);
            }
        });

        jButton1_buscar_producto.setBackground(new java.awt.Color(0, 102, 255));
        jButton1_buscar_producto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/busqueda.png"))); // NOI18N
        jButton1_buscar_producto.setText("BUSCAR");
        jButton1_buscar_producto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1_buscar_productoActionPerformed(evt);
            }
        });

        jPanel_inferior.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_inferior.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel2.setText("Cliente:");

        jTextField_cliente.setBackground(new java.awt.Color(255, 255, 255));
        jTextField_cliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField_clienteFocusLost(evt);
            }
        });

        jLabel_metodo_pago.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel_metodo_pago.setText("Metodo de Pago:");

        jComboBoxMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Tarjeta Debido", "Tarjeta Credito" }));

        jLabel8.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel8.setText("Informacion de Pago");

        jTextField_nombre_completo_cliente.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel9.setText("Nombre Completo:");

        javax.swing.GroupLayout jPanel_inferiorLayout = new javax.swing.GroupLayout(jPanel_inferior);
        jPanel_inferior.setLayout(jPanel_inferiorLayout);
        jPanel_inferiorLayout.setHorizontalGroup(
            jPanel_inferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_inferiorLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel_inferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_inferiorLayout.createSequentialGroup()
                        .addGroup(jPanel_inferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel_metodo_pago)
                            .addComponent(jLabel9)
                            .addComponent(jLabel2))
                        .addGap(62, 62, 62)
                        .addGroup(jPanel_inferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_nombre_completo_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel8))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel_inferiorLayout.setVerticalGroup(
            jPanel_inferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_inferiorLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_inferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_inferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_nombre_completo_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(17, 17, 17)
                .addGroup(jPanel_inferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_metodo_pago))
                .addGap(71, 71, 71))
        );

        jPanel_botones.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_botones.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton_procesar_venta.setBackground(new java.awt.Color(0, 102, 0));
        jButton_procesar_venta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/aprobado.png"))); // NOI18N
        jButton_procesar_venta.setText("PROCESAR VENTA");
        jButton_procesar_venta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_procesar_ventaActionPerformed(evt);
            }
        });

        jButton_nueva_venta.setBackground(new java.awt.Color(255, 102, 51));
        jButton_nueva_venta.setForeground(new java.awt.Color(255, 255, 255));
        jButton_nueva_venta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/recargar.png"))); // NOI18N
        jButton_nueva_venta.setText("NUEVA VENTA");
        jButton_nueva_venta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_nueva_ventaActionPerformed(evt);
            }
        });

        jButton_cancelar.setBackground(new java.awt.Color(204, 0, 0));
        jButton_cancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cancelar  o borrar.png"))); // NOI18N
        jButton_cancelar.setText("CANCELAR");
        jButton_cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_botonesLayout = new javax.swing.GroupLayout(jPanel_botones);
        jPanel_botones.setLayout(jPanel_botonesLayout);
        jPanel_botonesLayout.setHorizontalGroup(
            jPanel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_botonesLayout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addComponent(jButton_procesar_venta, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_nueva_venta, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(132, 132, 132)
                .addComponent(jButton_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97))
        );
        jPanel_botonesLayout.setVerticalGroup(
            jPanel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_botonesLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_procesar_venta, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_nueva_venta, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        jPanel_totales.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_totales.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel4.setText("Totales");

        jLabel_subtotal.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel_subtotal.setText("Subtotal:");

        jLabel5.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel5.setText("IVA 19%");

        jTextField_iva.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel6.setText("TOTAL:");

        jTextField_subtotal.setBackground(new java.awt.Color(255, 255, 255));

        jTextField_total.setBackground(new java.awt.Color(255, 255, 255));
        jTextField_total.setForeground(new java.awt.Color(0, 204, 0));
        jTextField_total.setText("$");

        javax.swing.GroupLayout jPanel_totalesLayout = new javax.swing.GroupLayout(jPanel_totales);
        jPanel_totales.setLayout(jPanel_totalesLayout);
        jPanel_totalesLayout.setHorizontalGroup(
            jPanel_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_totalesLayout.createSequentialGroup()
                .addGroup(jPanel_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_totalesLayout.createSequentialGroup()
                        .addGroup(jPanel_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_totalesLayout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addGroup(jPanel_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel_subtotal)))
                            .addGroup(jPanel_totalesLayout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel_total)))
                        .addGap(77, 77, 77)
                        .addGroup(jPanel_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField_iva, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_total, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_totalesLayout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jLabel4)))
                .addContainerGap(128, Short.MAX_VALUE))
        );
        jPanel_totalesLayout.setVerticalGroup(
            jPanel_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_totalesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(22, 22, 22)
                .addGroup(jPanel_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_totalesLayout.createSequentialGroup()
                        .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_iva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2))
                    .addGroup(jPanel_totalesLayout.createSequentialGroup()
                        .addComponent(jLabel_subtotal)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)))
                .addGroup(jPanel_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_totalesLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel_total))
                    .addGroup(jPanel_totalesLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel_totalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel_cantidad.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel_cantidad.setText("Cantidad:");

        jTable_ventas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Producto", "Talla", "Color", "Precio Unit.", "Cantidad", "Subtotal"
            }
        ));
        jScrollPane_ventas.setViewportView(jTable_ventas);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane_ventas)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane_ventas, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTextField_nombre_producto.setBackground(new java.awt.Color(255, 255, 255));

        jTextField1_cantidad_stock.setBackground(new java.awt.Color(255, 255, 255));

        jButton_agregar.setBackground(new java.awt.Color(0, 102, 0));
        jButton_agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/AgregarCarro.png"))); // NOI18N
        jButton_agregar.setText("AGREGAR");
        jButton_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_busquedaLayout = new javax.swing.GroupLayout(jPanel_busqueda);
        jPanel_busqueda.setLayout(jPanel_busquedaLayout);
        jPanel_busquedaLayout.setHorizontalGroup(
            jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_botones, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                .addComponent(jPanel_inferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel_totales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_nombre_producto, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_buscar_codigo_producto, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1_buscar_producto, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addComponent(jLabel_estado_busqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(55, 55, 55)
                .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                        .addComponent(jLabel_cantidad)
                        .addGap(18, 18, 18)
                        .addComponent(jSpinner_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField1_cantidad_stock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton_agregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_eliminar_producto, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addGap(42, 42, 42))
        );
        jPanel_busquedaLayout.setVerticalGroup(
            jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton_eliminar_producto, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_nombre_producto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton_agregar)
                                .addComponent(jTextField1_cantidad_stock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_buscar_codigo_producto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSpinner_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel_cantidad)
                            .addComponent(jButton1_buscar_producto))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel_estado_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_totales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_busquedaLayout.createSequentialGroup()
                        .addComponent(jPanel_inferior, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addComponent(jPanel_botones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jPanel1_ventaLayout = new javax.swing.GroupLayout(jPanel1_venta);
        jPanel1_venta.setLayout(jPanel1_ventaLayout);
        jPanel1_ventaLayout.setHorizontalGroup(
            jPanel1_ventaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1_ventaLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel1_ventaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1_ventaLayout.createSequentialGroup()
                        .addComponent(jPanel_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1_ventaLayout.createSequentialGroup()
                        .addComponent(jLabel_titulo)
                        .addGap(368, 368, 368))))
        );
        jPanel1_ventaLayout.setVerticalGroup(
            jPanel1_ventaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1_ventaLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel_titulo)
                .addGap(18, 18, 18)
                .addComponent(jPanel_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1_venta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1_venta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_nueva_ventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_nueva_ventaActionPerformed
        // TODO add your handling code here:
        limpiarCampos();
    }//GEN-LAST:event_jButton_nueva_ventaActionPerformed

    private void jButton1_buscar_productoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1_buscar_productoActionPerformed
        // TODO add your handling code here:
        // Obtener el código o nombre que se busca
        String valorBuscar = jTextField_buscar_codigo_producto.getText().trim();

        //Verificar que no esté vacío
        if (valorBuscar.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingresa un código o nombre de producto.",
                    "Campo vacío",
                    JOptionPane.WARNING_MESSAGE);
            jTextField_buscar_codigo_producto.requestFocus();
            return;
        }

        // BUSCAR EL PRODUCTO EN LA BASE DE DATOS
        Producto producto = new Producto();
        ArrayList<Producto> lista = producto.productoList(valorBuscar);

        //Verificar que se encontró el producto
        if (lista.isEmpty()) {

            jLabel_estado_busqueda.setText("Producto no encontrado ✖");
            jLabel_estado_busqueda.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                    "Producto no encontrado.\n\n"
                    + "Búsqueda: " + valorBuscar,
                    "Producto no encontrado",
                    JOptionPane.ERROR_MESSAGE);

            // Limpiar campos
            jTextField_nombre_producto.setText("");
            jTextField1_cantidad_stock.setText("");
            jSpinner_cantidad.setValue(0);

            return;
        }

        //OBTENER EL PRIMER PRODUCTO DE LA LISTA
        Producto p = lista.get(0);

        //MOSTRAR INFORMACIÓN EN LOS TEXTFIELD (NO EN LA TABLA)
        //Nombre completo del producto
        String nombreCompleto = p.getNombre() + " - " + p.getTalla() + " - " + p.getColor();
        jTextField_nombre_producto.setText(nombreCompleto);

        //Stock disponible
        jTextField1_cantidad_stock.setText("Stock: " + p.getCantidad());

        //Resetear spinner a 1
        jSpinner_cantidad.setValue(1);

        //Cambiar el label de estado 
        jLabel_estado_busqueda.setText("Producto encontrado ✔");
        jLabel_estado_busqueda.setForeground(Color.GREEN);

        // Dar foco al spinner para seleccionar cantidad
        jSpinner_cantidad.requestFocus();
    }//GEN-LAST:event_jButton1_buscar_productoActionPerformed

    private void jTextField_buscar_codigo_productoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_buscar_codigo_productoActionPerformed
        // TODO add your handling code here:
        String nit = jTextField_cliente.getText().trim();

        if (!nit.isEmpty()) {
            //Llamamos al método static desde Clientes
            Clientes cliente = Clientes.buscarClientePorNIT(nit);

            if (cliente != null) {
                jTextField_cliente.setText(cliente.getNombreCompleto());
                JOptionPane.showMessageDialog(this,
                        "Cliente encontrado: " + cliente.getNombreCompleto(),
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cliente no encontrado. Regístralo primero.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTextField_buscar_codigo_productoActionPerformed

    private void jButton_eliminar_productoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminar_productoActionPerformed
        // TODO add your handling code here:
        int filaSeleccionada = jTable_ventas.getSelectedRow();

        if (filaSeleccionada >= 0) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas eliminar este producto?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                DefaultTableModel modelo = (DefaultTableModel) jTable_ventas.getModel();
                modelo.removeRow(filaSeleccionada);
                recalcularTotales(); // este método actualiza el total
            }

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un producto para eliminar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }//GEN-LAST:event_jButton_eliminar_productoActionPerformed

    private void jTextField_clienteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField_clienteFocusLost
        // TODO add your handling code here:
        buscarClientePorDocumento();
    }//GEN-LAST:event_jTextField_clienteFocusLost

    private void jButton_cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cancelarActionPerformed
        // TODO add your handling code here:
        this.dispose(); // Cierra solo esta ventana interna
    }//GEN-LAST:event_jButton_cancelarActionPerformed

    private void jButton_procesar_ventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_procesar_ventaActionPerformed
        // TODO add your handling code here:
        // Ejecutar el proceso completo
        controladorVentas.registrarVenta();
        //REGISTRAR ACCESO AL MÓDULO
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "Ventas",
                    "Intento procesar venta"
            );
            System.out.println("Se realizo una venta");
        } catch (Exception e) {
            System.err.println("Error al registrar acceso: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton_procesar_ventaActionPerformed

    private void jButton_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarActionPerformed
        // TODO add your handling code here:
        //Verificar que hay un producto buscado

        if (jTextField_nombre_producto.getText().trim().isEmpty()) {

            jLabel_estado_busqueda.setText("Primero busca un producto");
            jLabel_estado_busqueda.setForeground(Color.RED);

            JOptionPane.showMessageDialog(this,
                    "Primero debes BUSCAR un producto antes de agregarlo",
                    "No hay producto",
                    JOptionPane.WARNING_MESSAGE);

            jTextField_buscar_codigo_producto.requestFocus();
            return;
        }

        //Verificar cantidad en spinner
        int cantidad = (Integer) jSpinner_cantidad.getValue();

        if (cantidad <= 0) {

            jLabel_estado_busqueda.setText("Cantidad inválida");
            jLabel_estado_busqueda.setForeground(Color.RED);

            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser mayor a 0",
                    "Cantidad inválida",
                    JOptionPane.WARNING_MESSAGE);

            jSpinner_cantidad.requestFocus();
            return;
        }

        // OBTENER EL PRODUCTO NUEVAMENTE PARA TENER DATOS ACTUALIZADOS   
        String valorBuscar = jTextField_buscar_codigo_producto.getText().trim();
        Producto producto = new Producto();
        ArrayList<Producto> lista = producto.productoList(valorBuscar);

        if (lista.isEmpty()) {
            System.out.println("Error: Producto no encontrado en BD");

            jLabel_estado_busqueda.setText("Error obteniendo producto ✖");
            jLabel_estado_busqueda.setForeground(Color.RED);

            JOptionPane.showMessageDialog(this,
                    "Error: No se pudo obtener el producto.\n"
                    + "Por favor búscalo nuevamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Producto p = lista.get(0);

        //Verificar que hay stock suficiente
        int stockDisponible = p.getCantidad();

        if (cantidad > stockDisponible) {

            jLabel_estado_busqueda.setText("Stock insuficiente");
            jLabel_estado_busqueda.setForeground(Color.RED);

            JOptionPane.showMessageDialog(this,
                    "Stock insuficiente\n\n"
                    + "Cantidad solicitada: " + cantidad + "\n"
                    + "Stock disponible: " + stockDisponible,
                    "Stock insuficiente",
                    JOptionPane.ERROR_MESSAGE);

            jSpinner_cantidad.setValue(stockDisponible);
            return;
        }

        // VALIDACIÓN 4: Verificar si el producto ya está en la tabla
        DefaultTableModel modelo = (DefaultTableModel) jTable_ventas.getModel();
        System.out.println("Verificando si producto ya existe en tabla...");

        for (int i = 0; i < modelo.getRowCount(); i++) {
            String codigoEnTabla = modelo.getValueAt(i, 0).toString();

            if (codigoEnTabla.equals(p.getCodigo())) {

                // Producto ya existe, preguntar si desea aumentar cantidad
                int cantidadActual = Integer.parseInt(modelo.getValueAt(i, 5).toString());
                int nuevaCantidad = cantidadActual + cantidad;

                if (nuevaCantidad > stockDisponible) {
                    System.out.println("Error: Nueva cantidad excede stock");

                    JOptionPane.showMessageDialog(this,
                            "Stock insuficiente\n\n"
                            + "Ya tienes " + cantidadActual + " en el carrito\n"
                            + "Intentas agregar " + cantidad + " más\n"
                            + "Total sería: " + nuevaCantidad + "\n"
                            + "Stock disponible: " + stockDisponible,
                            "Stock insuficiente",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirmar = JOptionPane.showConfirmDialog(this,
                        "Este producto ya está en la tabla.\n\n"
                        + "Cantidad actual: " + cantidadActual + "\n"
                        + "¿Deseas aumentar la cantidad en " + cantidad + "?\n"
                        + "Nueva cantidad sería: " + nuevaCantidad,
                        "Producto duplicado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirmar == JOptionPane.YES_OPTION) {

                    // Actualizar cantidad y subtotal en la fila existente
                    double precio = Double.parseDouble(p.getPrecio());
                    double nuevoSubtotal = precio * nuevaCantidad;

                    modelo.setValueAt(nuevaCantidad, i, 5);
                    modelo.setValueAt(String.format("%.2f", nuevoSubtotal), i, 6);

                    // Actualizar totales
                    actualizarTotales();

                    // Limpiar campos
                    limpiarCampos();

                    JOptionPane.showMessageDialog(this,
                            "✅ Cantidad actualizada correctamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                }

                return;
            }
        }

        // CALCULAR PRECIO Y SUBTOTAL
        double precio = Double.parseDouble(p.getPrecio());
        double subtotal = precio * cantidad;

        modelo.addRow(new Object[]{
            p.getCodigo(),
            p.getNombre(),
            p.getTalla(),
            p.getColor(),
            String.format("%.2f", precio),
            cantidad,
            String.format("%.2f", subtotal)
        });

        // ACTUALIZAR TOTALES
        actualizarTotales();

        // Dar foco al campo de búsqueda para el siguiente producto
        jTextField_buscar_codigo_producto.requestFocus();
    }//GEN-LAST:event_jButton_agregarActionPerformed

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
            java.util.logging.Logger.getLogger(FrmVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmVentas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1_buscar_producto;
    private javax.swing.JButton jButton_agregar;
    private javax.swing.JButton jButton_cancelar;
    private javax.swing.JButton jButton_eliminar_producto;
    private javax.swing.JButton jButton_nueva_venta;
    private javax.swing.JButton jButton_procesar_venta;
    private javax.swing.JComboBox<String> jComboBoxMetodoPago;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_cantidad;
    private javax.swing.JLabel jLabel_estado_busqueda;
    private javax.swing.JLabel jLabel_metodo_pago;
    private javax.swing.JLabel jLabel_subtotal;
    private javax.swing.JLabel jLabel_titulo;
    private javax.swing.JLabel jLabel_total;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel1_venta;
    private javax.swing.JPanel jPanel_botones;
    private javax.swing.JPanel jPanel_busqueda;
    private javax.swing.JPanel jPanel_inferior;
    private javax.swing.JPanel jPanel_totales;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane_ventas;
    private javax.swing.JSpinner jSpinner_cantidad;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable_ventas;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1_cantidad_stock;
    private javax.swing.JTextField jTextField_buscar_codigo_producto;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_iva;
    private javax.swing.JTextField jTextField_nombre_completo_cliente;
    private javax.swing.JTextField jTextField_nombre_producto;
    private javax.swing.JTextField jTextField_subtotal;
    private javax.swing.JTextField jTextField_total;
    // End of variables declaration//GEN-END:variables

// GETTERS Y MÉTODOS PÚBLICOS PARA EL CONTROLADOR
    //Obtiene la tabla de ventas
    public javax.swing.JTable getTablaVentas() {
        return jTable_ventas;
    }

    //Obtiene el campo de texto del cliente
    public javax.swing.JTextField getTxtCliente() {
        return jTextField_cliente;
    }

    // Obtiene el campo del nombre completo del cliente
    public javax.swing.JTextField getJTextFieldNombreCompletoCliente() {
        return jTextField_nombre_completo_cliente;
    }

    //Obtiene el campo del subtotal
    public javax.swing.JTextField getJTextFieldSubtotal() {
        return jTextField_subtotal;
    }

    // Obtiene el campo del IVA
    public javax.swing.JTextField getTxtIva() {
        return jTextField_iva;
    }

    //Obtiene el campo del total
    public javax.swing.JTextField getTxtTotal() {
        return jTextField_total;
    }

    //Obtiene el combo box del método de pago
    public javax.swing.JComboBox<String> getCboMetodoPago() {
        return jComboBoxMetodoPago;
    }

    // MÉTODOS QUE DEVUELVEN LOS VALORES DIRECTAMENTE (Más encapsulación)
    //Obtiene el nombre del cliente
    public String getNombreCliente() {
        if (jTextField_cliente == null) {
            return "";
        }
        return jTextField_cliente.getText().trim();
    }

    // Obtiene el nombre completo del cliente
    public String getNombreCompletoCliente() {
        if (jTextField_nombre_completo_cliente == null) {
            return "";
        }
        String nombre = jTextField_nombre_completo_cliente.getText().trim();
        return nombre.isEmpty() ? getNombreCliente() : nombre;
    }

    //Obtiene el subtotal como texto
    public String getSubtotalTexto() {
        if (jTextField_subtotal == null) {
            return "0";
        }
        String txt = jTextField_subtotal.getText().trim();
        return txt.isEmpty() ? "0" : txt;
    }

    //Obtiene el IVA como texto
    public String getIvaTexto() {
        if (jTextField_iva == null) {
            return "0";
        }
        String txt = jTextField_iva.getText().trim();
        return txt.isEmpty() ? "0" : txt;
    }

    // Obtiene el descuento como texto (si existe un campo separado)
    public String getDescuentoTexto() {
        // Si tienes un campo separado para descuento, úsalo aquí
        // Si no, devuelve 0
        return "0";
    }

    // Obtiene el total como texto
    public String getTotalTexto() {
        if (jTextField_total == null) {
            return "0";
        }
        String txt = jTextField_total.getText().trim();
        return txt.isEmpty() ? "0" : txt;
    }

    //Obtiene el método de pago seleccionado
    public String getMetodoPagoSeleccionado() {
        // Validar que el ComboBox no sea null
        if (jComboBoxMetodoPago == null) {
            System.out.println("jComboBoxMetodoPago es null, usando Efectivo");
            return "Efectivo";
        }

        // Validar que tenga algo seleccionado
        if (jComboBoxMetodoPago.getSelectedItem() == null) {
            System.out.println("No hay método de pago seleccionado, usando Efectivo");
            return "Efectivo";
        }

        String metodoPago = jComboBoxMetodoPago.getSelectedItem().toString();

        // Validar que no esté vacío o sea "Seleccionar"
        if (metodoPago == null || metodoPago.isEmpty()
                || metodoPago.equals("Seleccionar") || metodoPago.equals("-- Seleccionar --")) {
            System.out.println("Método de pago vacío o 'Seleccionar', usando Efectivo");
            return "Efectivo";
        }

        return metodoPago;
    }

    //Obtiene la cantidad de productos en la tabla
    public int getCantidadProductos() {
        if (jTable_ventas == null) {
            return 0;
        }
        return jTable_ventas.getRowCount();
    }

    //Limpia el formulario
    public void limpiarFormulario() {
        if (jTextField_cliente != null) {
            jTextField_cliente.setText("");
        }

        if (jTextField_nombre_completo_cliente != null) {
            jTextField_nombre_completo_cliente.setText("");
        }

        if (jTextField_subtotal != null) {
            jTextField_subtotal.setText("0");
        }

        if (jTextField_iva != null) {
            jTextField_iva.setText("0");
        }

        if (jTextField_total != null) {
            jTextField_total.setText("0");
        }

        if (jTable_ventas != null) {
            javax.swing.table.DefaultTableModel modelo
                    = (javax.swing.table.DefaultTableModel) jTable_ventas.getModel();
            modelo.setRowCount(0);
        }
    }

    private void inicializarComboMetodoPago() {
        jComboBoxMetodoPago.removeAllItems();
        jComboBoxMetodoPago.addItem("Efectivo");
        jComboBoxMetodoPago.addItem("Tarjeta Débito");
        jComboBoxMetodoPago.addItem("Tarjeta Crédito");

        // Seleccionar el primero por defecto
        jComboBoxMetodoPago.setSelectedIndex(0);  // "Efectivo"
    }
}
