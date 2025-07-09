/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.DProveedores;
import static controlador.DProveedores.actualizarProveedores;
import static controlador.DProveedores.insertarProveedores;
import controlador.DUsers;
import static controlador.DUsers.insertUser;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import reportes.VistaReportes;
import util.Utilidad;

/**
 *
 * @author piper
 */
public class ConsultaProveedores extends javax.swing.JInternalFrame {

    /**
     * Creates new form ConsultasU
     */
    public ConsultaProveedores() {

        super("Consultas", true, true, true, true); // Título, cerrable, redimensionable, movible, maximizable
        initComponents();
        setSize(595, 460); // Establece el tamaño de la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Permite cerrar la ventana
        setVisible(true); // Haz la ventana visible

        //cargo la tabla que nos trae y nos muestra los registros de la base de datos contenidos en la trabla users
        populateJtable("");
        jTable_Proveedores.setShowGrid(true);

        jTable_Proveedores.setGridColor(Color.YELLOW);

        jTable_Proveedores.setSelectionBackground(Color.gray);

        JTableHeader th = jTable_Proveedores.getTableHeader();

        th.setFont(new Font("Tahoma", Font.PLAIN, 16));

    }
    
    private void limpiarCampos() {
    jTextField_ID.setText("");    
    jTextField_NOMBRE_PROVEEDOR.setText("");
    jTextField1_numero_documento.setText("");
    jTextField_TELEFONO.setText("");
    jTextField2_correo.setText("");

}

    public static ConsultaProveedores ventanaPrincipal;
    private Integer Id;
    int pos = 0;


    public void populateJtable(String prove) {

        controlador.DProveedores proveedores = new controlador.DProveedores();
        // Obtener la lista de proveedores desde la base de datos
        ArrayList<DProveedores> proveList = getProveeList();

        // Definir las columnas de la tabla
        String[] colNames = {"ID", "Nombre Completo", "Tipo ID", "Documento", "Genero", "Telefono", "Correo"};

        // Crear una matriz para almacenar las filas de la tabla
        Object[][] rows = new Object[proveList.size()][7];

        // Rellenar la matriz con los datos de la lista de usuarios
        for (int i = 0; i < proveList.size(); i++) {
            rows[i][0] = proveList.get(i).getId();
            rows[i][1] = proveList.get(i).getNombreProveedor();
            rows[i][2] = proveList.get(i).getTipoDocumento();
            rows[i][3] = proveList.get(i).getNumeroDocumento();
            rows[i][4] = proveList.get(i).getGenero();
            rows[i][5] = proveList.get(i).getTelefono();
            rows[i][6] = proveList.get(i).getCorreoElectronico();
        }

        // Crear el modelo de la tabla con los datos y las columnas
         DefaultTableModel model = new DefaultTableModel(rows, colNames);

        // Asignar el modelo a la JTable
        jTable_Proveedores.setModel(model);
        jTable_Proveedores.setRowHeight(30); // Ajustar la altura de las filas
    }

    public void ShowItem(int index) {

        // Asignar valores a los campos del formulario
        jTextField_ID.setText(Integer.toString(getProveeList().get(index).getId()));

        jTextField_NOMBRE_PROVEEDOR.setText(getProveeList().get(index).getNombreProveedor());
        jComboBox1_tipo_d.setSelectedItem(getProveeList().get(index).getTipoDocumento());
        jTextField1_numero_documento.setText(getProveeList().get(index).getNumeroDocumento());
        // Manejo del género
        if (getProveeList().get(index).getGenero().equals("Hombre")) {
            jRadioButton_MASCULINO.setSelected(true);
            jRadioButton_FEMENINO.setSelected(false);
            jRadioButton_EMPRESA.setSelected(false);
        } else if (getProveeList().get(index).getGenero().equals("Mujer")) {
            jRadioButton_MASCULINO.setSelected(false);
            jRadioButton_FEMENINO.setSelected(true);
            jRadioButton_EMPRESA.setSelected(false);
        } else if (getProveeList().get(index).getGenero().equals("Empresa")) {
            jRadioButton_MASCULINO.setSelected(false);
            jRadioButton_FEMENINO.setSelected(false);
            jRadioButton_EMPRESA.setSelected(true);

        } else {
            JOptionPane.showMessageDialog(null, "Índice no válido o la lista de proveedores está vacía.");
        }
        jTextField_TELEFONO.setText(getProveeList().get(index).getTelefono());
        jTextField2_correo.setText(getProveeList().get(index).getCorreoElectronico());

    }

    public boolean verifyFields() {

        String name = jTextField_NOMBRE_PROVEEDOR.getText();
        String doc = jTextField1_numero_documento.getText();
        String telefono = jTextField_TELEFONO.getText();
        String email = jTextField2_correo.getText();


        // Verificar si hay campos vacíos
        if (name.trim().equals("") ||  doc.trim().equals("") || telefono.trim().equals("") || email.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Uno o mas campos estan vacios", "Campos vacios", 2);
            return false;
        } 
        else {
            return true;
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

        jTextField1 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField_ID = new javax.swing.JTextField();
        jTextField_NOMBRE_PROVEEDOR = new javax.swing.JTextField();
        jTextField_TELEFONO = new javax.swing.JTextField();
        jRadioButton_MASCULINO = new javax.swing.JRadioButton();
        jRadioButton_FEMENINO = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Proveedores = new javax.swing.JTable();
        jButton_insertar = new javax.swing.JButton();
        jButton1_actualizar = new javax.swing.JButton();
        jButton_eliminar = new javax.swing.JButton();
        jButton_first = new javax.swing.JButton();
        jButton_siguiente = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField1_numero_documento = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jComboBox1_tipo_d = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jTextField2_correo = new javax.swing.JTextField();
        jRadioButton_EMPRESA = new javax.swing.JRadioButton();
        jButton_refrescar = new javax.swing.JButton();
        jButton_imprimir = new javax.swing.JButton();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel1.setText("ID:");

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel2.setText("Nombre Proveedor:");

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel5.setText("Telefono:");

        jLabel6.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel6.setText("Genero:");

        jRadioButton_MASCULINO.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
        jRadioButton_MASCULINO.setText("Hombre");
        jRadioButton_MASCULINO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_MASCULINOActionPerformed(evt);
            }
        });

        jRadioButton_FEMENINO.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
        jRadioButton_FEMENINO.setText("Mujer");

        jTable_Proveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable_Proveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_ProveedoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_Proveedores);

        jButton_insertar.setBackground(new java.awt.Color(255, 255, 255));
        jButton_insertar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_insertar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/insert.png"))); // NOI18N
        jButton_insertar.setText("Insertar");
        jButton_insertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_insertarActionPerformed(evt);
            }
        });

        jButton1_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        jButton1_actualizar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton1_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/update.png"))); // NOI18N
        jButton1_actualizar.setText("Actualizar");
        jButton1_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1_actualizarActionPerformed(evt);
            }
        });

        jButton_eliminar.setBackground(new java.awt.Color(255, 255, 255));
        jButton_eliminar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/delete.png"))); // NOI18N
        jButton_eliminar.setText("Eliminar");
        jButton_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminarActionPerformed(evt);
            }
        });

        jButton_first.setBackground(new java.awt.Color(255, 255, 255));
        jButton_first.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_first.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/izq.png"))); // NOI18N
        jButton_first.setText("Primero");
        jButton_first.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_firstActionPerformed(evt);
            }
        });

        jButton_siguiente.setBackground(new java.awt.Color(255, 255, 255));
        jButton_siguiente.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_siguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/siguiente.png"))); // NOI18N
        jButton_siguiente.setText("Siguiente");
        jButton_siguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_siguienteActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/atras.png"))); // NOI18N
        jButton4.setText("Atrás");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/der.png"))); // NOI18N
        jButton5.setText("Ultimo");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/LOGO5.PNG"))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel9.setText("Numero de Documento:");

        jLabel10.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel10.setText("Tipo de Documento:");

        jComboBox1_tipo_d.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cédula de Ciudadanía", "Cédula de extranjería", "NIT" }));

        jLabel11.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel11.setText("Correo Electronico:");

        jRadioButton_EMPRESA.setText("Empresa");

        jButton_refrescar.setBackground(new java.awt.Color(255, 255, 255));
        jButton_refrescar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_refrescar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/refrescar.png"))); // NOI18N
        jButton_refrescar.setText("Refrescar");
        jButton_refrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_refrescarActionPerformed(evt);
            }
        });

        jButton_imprimir.setBackground(new java.awt.Color(255, 255, 255));
        jButton_imprimir.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_imprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/imprimir.png"))); // NOI18N
        jButton_imprimir.setText("Imprimir");
        jButton_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel1))
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(64, 64, 64)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2_correo, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField_TELEFONO, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField1_numero_documento, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTextField_NOMBRE_PROVEEDOR)
                                        .addComponent(jTextField_ID, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jComboBox1_tipo_d, 0, 201, Short.MAX_VALUE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jRadioButton_FEMENINO)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton_MASCULINO)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jRadioButton_EMPRESA)))
                        .addGap(31, 31, 31)
                        .addComponent(jScrollPane1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(171, 171, 171)
                        .addComponent(jButton_insertar)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1_actualizar)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_eliminar)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_first)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_siguiente)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5)
                        .addGap(0, 58, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(582, 582, 582)
                        .addComponent(jButton_imprimir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_refrescar)
                        .addGap(221, 221, 221)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 44, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(14, 14, 14)))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_NOMBRE_PROVEEDOR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1_tipo_d, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1_numero_documento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jRadioButton_MASCULINO)
                            .addComponent(jRadioButton_FEMENINO)
                            .addComponent(jRadioButton_EMPRESA))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField_TELEFONO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField2_correo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(106, 106, 106))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton_refrescar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton_imprimir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_insertar)
                    .addComponent(jButton1_actualizar)
                    .addComponent(jButton_eliminar)
                    .addComponent(jButton_first)
                    .addComponent(jButton_siguiente)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(76, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_ProveedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_ProveedoresMouseClicked
        // TODO add your handling code here:
        Integer rowIndex = jTable_Proveedores.getSelectedRow();
        Id = Integer.valueOf(jTable_Proveedores.getValueAt(rowIndex, 0).toString());
        jTextField_ID.setText(jTable_Proveedores.getValueAt(rowIndex, 0).toString());
        jTextField_NOMBRE_PROVEEDOR.setText(jTable_Proveedores.getValueAt(rowIndex, 1).toString());
        jComboBox1_tipo_d.setSelectedItem(jTable_Proveedores.getValueAt(rowIndex, 2).toString());
        jTextField1_numero_documento.setText(jTable_Proveedores.getValueAt(rowIndex, 3).toString());
        String genero = (jTable_Proveedores.getValueAt(rowIndex, 4).toString());

        if (genero.equals("Hombre")) {
            jRadioButton_FEMENINO.setSelected(false);
            jRadioButton_MASCULINO.setSelected(true);
        } else if (genero.equals("Mujer")) {
            jRadioButton_MASCULINO.setSelected(false);
            jRadioButton_FEMENINO.setSelected(true);
        } else if(genero.equals("Empresa")){
            jRadioButton_MASCULINO.setSelected(false);
            jRadioButton_FEMENINO.setSelected(false);
        }
        jTextField_TELEFONO.setText(jTable_Proveedores.getValueAt(rowIndex, 5).toString());
        jTextField2_correo.setText(jTable_Proveedores.getValueAt(rowIndex, 6).toString());

    }//GEN-LAST:event_jTable_ProveedoresMouseClicked

    private void jButton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarActionPerformed
        // TODO add your handling code here:
        if (!jTextField_ID.getText().equals("")) {
            try {
                int id_proveedores = Integer.parseInt(jTextField_ID.getText());
                controlador.DProveedores.eliminarProveedor(id_proveedores);
                populateJtable("");

            } catch (Exception ex) {
                Logger.getLogger(ConsultaProveedores.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Proveedor no eliminado");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Proveedor no eliminados: No hay ID para eliminar");
        }
    }//GEN-LAST:event_jButton_eliminarActionPerformed

    private void jButton_firstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_firstActionPerformed
        // TODO add your handling code here:
        pos = 0;
        ShowItem(pos);
    }//GEN-LAST:event_jButton_firstActionPerformed

    private void jButton_siguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_siguienteActionPerformed
        // TODO add your handling code here:
    if (getProveeList() != null && !getProveeList().isEmpty()) {
        pos++;
        if (pos >= getProveeList().size()) {
            pos = getProveeList().size() - 1;
        }

        ShowItem(pos);
    } else {
        JOptionPane.showMessageDialog(this, "La lista de proveedores está vacía o no se pudo cargar.");
    }
    }//GEN-LAST:event_jButton_siguienteActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        pos--;
        if (pos < 0) {
            pos = 0;
        }
        ShowItem(pos);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        pos = getProveeList().size() - 1;
        ShowItem(pos);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton_insertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_insertarActionPerformed
        // TODO add your handling code here:
        // Crear un nuevo objeto DUsers
        DProveedores proveedores = new DProveedores();

        // Obtener los valores de los campos de texto
        proveedores.setNombreProveedor(jTextField_NOMBRE_PROVEEDOR.getText());
        proveedores.setNumeroDocumento(jTextField1_numero_documento.getText());
        proveedores.setTipoDocumento((String) jComboBox1_tipo_d.getSelectedItem());
        proveedores.setTelefono(jTextField_TELEFONO.getText());
        // Obtener el género seleccionado de los botones de radio
        String genero = "";
        if (jRadioButton_MASCULINO.isSelected()) {
            genero = "Hombre";
        } else if (jRadioButton_FEMENINO.isSelected()) {
            genero = "Mujer";
        } else if (jRadioButton_EMPRESA.isSelected()) {
            genero = "Empresa";
        }
        proveedores.setGenero(genero);
        proveedores.setCorreoElectronico(jTextField2_correo.getText());

        // Llamar al método para insertar el usuario
        insertarProveedores(proveedores);
    }//GEN-LAST:event_jButton_insertarActionPerformed

    private void jButton1_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1_actualizarActionPerformed
        // TODO add your handling code here:
        
     String nombreCompleto = jTextField_NOMBRE_PROVEEDOR.getText().trim();
    String tipoId = (String) jComboBox1_tipo_d.getSelectedItem();
    String numeroDocumento = jTextField1_numero_documento.getText().trim();
    String telefono = jTextField_TELEFONO.getText().trim();
    String correoElectronico = jTextField2_correo.getText().trim();

    // Determinar el género seleccionado
    String genero = "Hombre"; 
    if (jRadioButton_FEMENINO.isSelected()) {
        genero = "Mujer";
    } else if (jRadioButton_EMPRESA.isSelected()) {
        genero = "Empresa";
    }

    // Verificar que todos los campos son válidos
    if (verifyFields()) {
        try {
            // Obtener el ID del proveedor seleccionado en la tabla
            int selectedRow = jTable_Proveedores.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione un proveedor para actualizar.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idProveedor = (int) jTable_Proveedores.getValueAt(selectedRow, 0); // Supongamos que el ID está en la columna 0

            // Crear el objeto proveedores con el ID correcto y los datos del formulario
            controlador.DProveedores proveedores = new DProveedores(idProveedor, nombreCompleto, tipoId, numeroDocumento, genero, telefono, correoElectronico);

            // Intentar actualizar el proveedor
            controlador.DProveedores.actualizarProveedores(proveedores);
            populateJtable("");

            
        } catch (Exception e) {
            // Manejo de excepciones
            JOptionPane.showMessageDialog(null, "Error al actualizar el proveedor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Imprimir el stack trace para depuración
        }
    } else {
        // Mensaje si los campos no son válidos
        JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos obligatorios.", "Campos Inválidos", JOptionPane.WARNING_MESSAGE);
    } 
    }//GEN-LAST:event_jButton1_actualizarActionPerformed

    private void jRadioButton_MASCULINOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_MASCULINOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton_MASCULINOActionPerformed

    private void jButton_refrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_refrescarActionPerformed
        // TODO add your handling code here:
        limpiarCampos();
        populateJtable("");
    }//GEN-LAST:event_jButton_refrescarActionPerformed

    private void jButton_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirActionPerformed
        // TODO add your handling code here:
        // si se oprime el botón Imprimir
        if (jButton_imprimir.getText().equals("Imprimir")) {
            try {
                InputStream datosReporte = Utilidad.inputStreamReporte("Proveedores.jrxml");
                Map<String, String> parametros = new HashMap<>();
                parametros.put("RUsuarios", "Juan Felipe Triana ");

                // Mostrar el reporte en el panel
                Container panelReporte = VistaReportes.mostrarReporte(datosReporte, parametros);
                panelReporte.setPreferredSize(new Dimension(600, 400)); // Ajusta el tamaño según sea necesario

                this.jScrollPane1.getViewport().removeAll();
                this.jScrollPane1.getViewport().add(panelReporte);
                this.jScrollPane1.revalidate(); // Vuelve a validar el jScrollPane
                this.jScrollPane1.repaint(); // Redibuja el jScrollPane

                // Cambiar el texto del botón a "Volver"
                this.jButton_imprimir.setText("Volver");
                this.jButton_imprimir.setMnemonic('V');
                this.jButton_imprimir.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/atras.png")));
            } catch (Exception ex) {
                ex.printStackTrace(); // Imprime la traza de la excepción
                JOptionPane.showMessageDialog(this, "No se puede mostrar los Contactos de la Agenda\n" + ex.getMessage());
            }
        } else if (jButton_imprimir.getText().equals("Volver")) {
            this.jScrollPane1.getViewport().removeAll();
            this.jScrollPane1.getViewport().add(this.jTable_Proveedores);
            this.jButton_imprimir.setText("Imprimir");
            this.jButton_imprimir.setMnemonic('V');
        }
    }//GEN-LAST:event_jButton_imprimirActionPerformed

    public ArrayList<DProveedores> getProveeList() {
        ArrayList<DProveedores> proveeList = new ArrayList<DProveedores>();
        //  Connection con = getConnection();
        Connection con = modelo.Conexion_DB.getConnection();
        String query = "SELECT * FROM proveedores";

        Statement st;
        ResultSet rs;

        try {

            st = con.createStatement();
            rs = st.executeQuery(query);
            DProveedores proveedores;

            while (rs.next()) { //cargo cada registro llamándolo desde la tabla con sus respectivos nombres de campos
                proveedores = new DProveedores(rs.getInt("id"), rs.getString("nombre_proveedor"), rs.getString("tipo_documento"), rs.getString("numero_documento"), rs.getString("genero"), rs.getString("telefono"), rs.getString("Correo_electronico"));
                proveeList.add(proveedores);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ConsultaProveedores.class.getName()).log(Level.SEVERE, null, ex);
        }

        return proveeList;

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
            java.util.logging.Logger.getLogger(ConsultaProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultaProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultaProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultaProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsultaProveedores().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1_actualizar;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton_eliminar;
    private javax.swing.JButton jButton_first;
    private javax.swing.JButton jButton_imprimir;
    private javax.swing.JButton jButton_insertar;
    private javax.swing.JButton jButton_refrescar;
    private javax.swing.JButton jButton_siguiente;
    private javax.swing.JComboBox<String> jComboBox1_tipo_d;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton_EMPRESA;
    private javax.swing.JRadioButton jRadioButton_FEMENINO;
    private javax.swing.JRadioButton jRadioButton_MASCULINO;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Proveedores;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField1_numero_documento;
    private javax.swing.JTextField jTextField2_correo;
    private javax.swing.JTextField jTextField_ID;
    private javax.swing.JTextField jTextField_NOMBRE_PROVEEDOR;
    private javax.swing.JTextField jTextField_TELEFONO;
    // End of variables declaration//GEN-END:variables

}
