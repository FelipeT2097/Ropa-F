/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import modelo.Usuarios;
import static modelo.Usuarios.insertUser;
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
import javax.swing.table.JTableHeader;
import reportes.VistaReportes;
import util.Utilidad;

/**
 *
 * @author piper
 */
public class ConsultasUsuarios extends javax.swing.JInternalFrame {

    /**
     * Creates new form ConsultasU
     */
    public ConsultasUsuarios() {

        super("Consultas", true, true, true, true); // Título, cerrable, redimensionable, movible, maximizable
        initComponents();
        setSize(595, 460); // Establece el tamaño de la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Permite cerrar la ventana
        setVisible(true); // Haz la ventana visible

        jTextField_ID.setEnabled(false);

        //Carga la tabla que nos trae y nos muestra los registros de la base de datos contenidos en la trabla users
        populateJtable();
        jTable_Usuarios.setShowGrid(true);
        jTable_Usuarios.setGridColor(Color.YELLOW);
        jTable_Usuarios.setSelectionBackground(Color.gray);

        JTableHeader th = jTable_Usuarios.getTableHeader();

        th.setFont(new Font("Tahoma", Font.PLAIN, 16));

        jLabel8.setText("Rol:");

        jComboBox_rol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Administrador", "Vendedor", "Almacenista"}));
    }

    private void limpiarCampos() {
        jTextField_ID.setText("");
        jTextField_NOMBRE_COMPLETO.setText("");
        jTextField_NOMBRE_USUARIO.setText("");
        jTextField1_numero_documento.setText("");
        jComboBox1_tipo_d.setSelectedIndex(0); // Restablece el JComboBox a su primer elemento
        jTextField_TELEFONO.setText("");
        jTextField2_correo.setText("");
        jPasswordField.setText(""); // Limpia el campo de contraseña
        jComboBox_rol.setSelectedIndex(0);
    }
    public static ConsultasUsuarios ventanaPrincipal;
    private Integer productId;
    int pos = 0;

    public void populateJtable() {

        modelo.Usuarios user = new modelo.Usuarios();
        // Obtener la lista de usuarios desde la base de datos
        ArrayList<Usuarios> userList = getUsersList();

        // Definir las columnas de la tabla
        String[] colNames = {"ID", "Nombre Completo", "Nombre Usuario",
            "Tipo ID", "Documento", "Genero", "Telefono", "Correo", "Contraseña", "Rol"};

        // Crear una matriz para almacenar las filas de la tabla
        Object[][] rows = new Object[userList.size()][10];

        // Rellenar la matriz con los datos de la lista de usuarios
        for (int i = 0; i < userList.size(); i++) {
            rows[i][0] = userList.get(i).getId();
            rows[i][1] = userList.get(i).getNombreCompleto();
            rows[i][2] = userList.get(i).getNombreUsuario();
            rows[i][3] = userList.get(i).getTipoDocumento();
            rows[i][4] = userList.get(i).getNumeroDocumento();
            rows[i][5] = userList.get(i).getGenero();
            rows[i][6] = userList.get(i).getTelefono();
            rows[i][7] = userList.get(i).getCorreoElectronico();
            rows[i][8] = userList.get(i).getContraseña();
            rows[i][9] = userList.get(i).getRol();
        }

        // Crear el modelo de la tabla con los datos y las columnas
        modelo.TablaConsulta mmd = new modelo.TablaConsulta(rows, colNames);

        // Asignar el modelo a la JTable
        jTable_Usuarios.setModel(mmd);
        jTable_Usuarios.setRowHeight(30); // Ajustar la altura de las filas
    }

    public void ShowItem(int index) {

        // Asignar valores a los campos del formulario
        jTextField_ID.setText(Integer.toString(getUsersList().get(index).getId()));

        jTextField_NOMBRE_USUARIO.setText(getUsersList().get(index).getNombreUsuario());
        jTextField_NOMBRE_COMPLETO.setText(getUsersList().get(index).getNombreCompleto());
        jComboBox1_tipo_d.setSelectedItem(getUsersList().get(index).getTipoDocumento());
        jTextField1_numero_documento.setText(getUsersList().get(index).getNumeroDocumento());
        // Manejo del género
        if (getUsersList().get(index).getGenero().equals("Hombre")) {
            jRadioButton_MASCULINO.setSelected(true);
            jRadioButton_FEMENINO.setSelected(false);
        } else if (getUsersList().get(index).getGenero().equals("Mujer")) {
            jRadioButton_MASCULINO.setSelected(false);
            jRadioButton_FEMENINO.setSelected(true);

        } else {
            JOptionPane.showMessageDialog(null, "Índice no válido o la lista de usuarios está vacía.");
        }
        jTextField_TELEFONO.setText(getUsersList().get(index).getTelefono());
        jTextField2_correo.setText(getUsersList().get(index).getCorreoElectronico());
        jPasswordField.setText(getUsersList().get(index).getContraseña());
        jComboBox_rol.setSelectedItem(getUsersList().get(index).getRol());
    }

    public boolean verifyFields() {

        String name = jTextField_NOMBRE_COMPLETO.getText();
        String username = jTextField_NOMBRE_USUARIO.getText();
        String tipoId = (String) jComboBox1_tipo_d.getSelectedItem();
        String id = jTextField1_numero_documento.getText();
        String genero = "Hombre";
        String telefono = jTextField_TELEFONO.getText();
        String email = jTextField2_correo.getText();
        String pass = String.valueOf(jPasswordField.getPassword());
        String rol = (String) jComboBox_rol.getSelectedItem();

        // Verificar si hay campos vacíos
        if (name.trim().equals("") || username.trim().equals("") || tipoId == null || tipoId.trim().equals("")
                || id.trim().equals("") || telefono.trim().equals("") || email.trim().equals("")
                || pass.trim().equals("") || rol == null || rol.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Uno o mas campos estan vacios", "Campos vacios", 2);
            return false;
        } // check if the two password are equals
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField_ID = new javax.swing.JTextField();
        jTextField_NOMBRE_COMPLETO = new javax.swing.JTextField();
        jTextField_NOMBRE_USUARIO = new javax.swing.JTextField();
        jTextField_TELEFONO = new javax.swing.JTextField();
        jRadioButton_MASCULINO = new javax.swing.JRadioButton();
        jRadioButton_FEMENINO = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Usuarios = new javax.swing.JTable();
        jButton_imprimir = new javax.swing.JButton();
        jButton_insertar = new javax.swing.JButton();
        jButton_actualizar = new javax.swing.JButton();
        jButton_eliminar = new javax.swing.JButton();
        jButton_first = new javax.swing.JButton();
        jButton_siguiente = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPasswordField = new javax.swing.JPasswordField();
        jLabel9 = new javax.swing.JLabel();
        jTextField1_numero_documento = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jComboBox1_tipo_d = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jTextField2_correo = new javax.swing.JTextField();
        jButton_refrescar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jComboBox_rol = new javax.swing.JComboBox<>();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 204));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel1.setText("ID:");

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel2.setText("Nombre Completo:");

        jLabel3.setBackground(new java.awt.Color(51, 51, 51));
        jLabel3.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel3.setText("Nombre de Usuario:");

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel4.setText("Contraseña:");

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel5.setText("Telefono:");

        jLabel6.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel6.setText("Genero:");

        jRadioButton_MASCULINO.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jRadioButton_MASCULINO.setText("Hombre");
        jRadioButton_MASCULINO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_MASCULINOActionPerformed(evt);
            }
        });

        jRadioButton_FEMENINO.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jRadioButton_FEMENINO.setText("Mujer");

        jTable_Usuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable_Usuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_UsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_Usuarios);

        jButton_imprimir.setBackground(new java.awt.Color(255, 255, 255));
        jButton_imprimir.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_imprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/imprimir.png"))); // NOI18N
        jButton_imprimir.setText("Imprimir");
        jButton_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprimirActionPerformed(evt);
            }
        });

        jButton_insertar.setBackground(new java.awt.Color(255, 255, 255));
        jButton_insertar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_insertar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/insert.png"))); // NOI18N
        jButton_insertar.setText("Insertar");
        jButton_insertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_insertarActionPerformed(evt);
            }
        });

        jButton_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        jButton_actualizar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/update.png"))); // NOI18N
        jButton_actualizar.setText("Actualizar");
        jButton_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarActionPerformed(evt);
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

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/logo3.PNG"))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel9.setText("Numero de Documento:");

        jLabel10.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel10.setText("Tipo de Documento:");

        jComboBox1_tipo_d.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cédula de Ciudadanía", "Cédula de extranjería" }));

        jLabel11.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel11.setText("Correo Electronico:");

        jButton_refrescar.setBackground(new java.awt.Color(255, 255, 255));
        jButton_refrescar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_refrescar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/refrescar.png"))); // NOI18N
        jButton_refrescar.setText("Refrescar");
        jButton_refrescar.setBorder(new javax.swing.border.MatteBorder(null));
        jButton_refrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_refrescarActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel8.setText("Rol:");

        jComboBox_rol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrador\t", "Vendedor", "Almacenista ", " ", " " }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel9))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(38, 38, 38)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel11)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6)
                                .addComponent(jLabel4)
                                .addComponent(jLabel8)))))
                .addGap(62, 62, 62)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jTextField2_correo, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jRadioButton_FEMENINO)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jRadioButton_MASCULINO))
                                .addComponent(jTextField_TELEFONO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField1_numero_documento, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jTextField_NOMBRE_USUARIO, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                                            .addComponent(jTextField_ID, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jComboBox1_tipo_d, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(jTextField_NOMBRE_COMPLETO, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 0, Short.MAX_VALUE)))))
                    .addComponent(jComboBox_rol, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jButton_refrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_insertar)
                .addGap(18, 18, 18)
                .addComponent(jButton_actualizar)
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
                .addContainerGap(62, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_imprimir)
                .addGap(337, 337, 337))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField_NOMBRE_COMPLETO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField_NOMBRE_USUARIO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jComboBox1_tipo_d, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1_numero_documento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jRadioButton_MASCULINO)
                            .addComponent(jRadioButton_FEMENINO))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField_TELEFONO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField2_correo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jComboBox_rol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton_actualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton_eliminar, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton_imprimir)
                                .addGap(39, 39, 39)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton_siguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton_refrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton_insertar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jButton_first, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(61, Short.MAX_VALUE))
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
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_UsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_UsuariosMouseClicked
        // TODO add your handling code here:
        Integer rowIndex = jTable_Usuarios.getSelectedRow();
        productId = Integer.valueOf(jTable_Usuarios.getValueAt(rowIndex, 0).toString());
        jTextField_ID.setText(jTable_Usuarios.getValueAt(rowIndex, 0).toString());
        jTextField_NOMBRE_COMPLETO.setText(jTable_Usuarios.getValueAt(rowIndex, 1).toString());
        jTextField_NOMBRE_USUARIO.setText(jTable_Usuarios.getValueAt(rowIndex, 2).toString());
        jComboBox1_tipo_d.setSelectedItem(jTable_Usuarios.getValueAt(rowIndex, 3).toString());
        jTextField1_numero_documento.setText(jTable_Usuarios.getValueAt(rowIndex, 4).toString());
        String genero = (jTable_Usuarios.getValueAt(rowIndex, 5).toString());

        if (genero.equals("Hombre")) {
            jRadioButton_FEMENINO.setSelected(false);
            jRadioButton_MASCULINO.setSelected(true);
        } else if (genero.equals("Mujer")) {
            jRadioButton_MASCULINO.setSelected(false);
            jRadioButton_FEMENINO.setSelected(true);
        } else {
            jRadioButton_MASCULINO.setSelected(false);
            jRadioButton_FEMENINO.setSelected(false);
        }
        jTextField_TELEFONO.setText(jTable_Usuarios.getValueAt(rowIndex, 6).toString());
        jTextField2_correo.setText(jTable_Usuarios.getValueAt(rowIndex, 7).toString());
        jPasswordField.setText(jTable_Usuarios.getValueAt(rowIndex, 8).toString());
        jComboBox_rol.setSelectedItem(jTable_Usuarios.getValueAt(rowIndex, 9).toString());
    }//GEN-LAST:event_jTable_UsuariosMouseClicked

    private void jButton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarActionPerformed
        // TODO add your handling code here:
        if (!jTextField_ID.getText().equals("")) {
            try {
                int id_user = Integer.parseInt(jTextField_ID.getText());
                modelo.Usuarios.eliminarUsuarios(id_user);
                populateJtable();

            } catch (Exception ex) {
                Logger.getLogger(ConsultasUsuarios.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Usuarios no eliminado");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Usuarios no eliminados: No hay ID para eliminar");
        }
    }//GEN-LAST:event_jButton_eliminarActionPerformed

    private void jButton_firstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_firstActionPerformed
        // TODO add your handling code here:
        pos = 0;
        ShowItem(pos);
    }//GEN-LAST:event_jButton_firstActionPerformed

    private void jButton_siguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_siguienteActionPerformed
        // TODO add your handling code here:
        pos++;

        if (pos >= getUsersList().size()) {
            pos = getUsersList().size() - 1;
        }

        ShowItem(pos);
    }//GEN-LAST:event_jButton_siguienteActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        pos--;

        if (pos < 0) {
            pos = 0;
        }

        ShowItem(pos);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        pos = getUsersList().size() - 1;
        ShowItem(pos);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton_insertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_insertarActionPerformed
        // TODO add your handling code here:
        // Crear un nuevo objeto Usuarios
        Usuarios user = new Usuarios();

        // Obtener los valores de los campos de texto
        user.setNombreCompleto(jTextField_NOMBRE_COMPLETO.getText());
        user.setNombreUsuario(jTextField_NOMBRE_USUARIO.getText());
        user.setNumeroDocumento(jTextField1_numero_documento.getText());
        user.setTipoDocumento((String) jComboBox1_tipo_d.getSelectedItem());
        user.setTelefono(jTextField_TELEFONO.getText());

        // Obtener el género seleccionado de los botones de radio
        String genero = "";
        if (jRadioButton_MASCULINO.isSelected()) {
            genero = "Hombre";
        } else if (jRadioButton_FEMENINO.isSelected()) {
            genero = "Mujer";
        }
        user.setGenero(genero);
        user.setCorreoElectronico(jTextField2_correo.getText());

        // Encriptar la contraseña
        String sha1 = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            String contraseña = new String(jPasswordField.getPassword()); // Convierte char[] a String
            digest.update(contraseña.getBytes("utf8"));
            sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setContraseña(sha1); // Establecer la contraseña encriptada
        user.setRol((String) jComboBox_rol.getSelectedItem());

        user.setRol((String) jComboBox_rol.getSelectedItem());
        // Llamar al método 
        insertUser(user);
    }//GEN-LAST:event_jButton_insertarActionPerformed

    private void jButton_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarActionPerformed
        // TODO add your handling code here:
        // Obtener los datos del formulario
        String nombreCompleto = jTextField_NOMBRE_COMPLETO.getText();
        String nombreUsuario = jTextField_NOMBRE_USUARIO.getText();
        String tipoId = (String) jComboBox1_tipo_d.getSelectedItem();
        String numeroDocumento = jTextField1_numero_documento.getText();
        String contraseña = String.valueOf(jPasswordField.getPassword());
        String telefono = jTextField_TELEFONO.getText();
        String correoElectronico = jTextField2_correo.getText();
        String rol = (String) jComboBox_rol.getSelectedItem();

        String genero = "Hombre";
        if (jRadioButton_FEMENINO.isSelected()) {
            genero = "Mujer";
        }

        // Encriptar la contraseña
        String sha1 = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(contraseña.getBytes("utf8"));
            sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Verificar que todos los campos son válidos
        if (verifyFields()) {
            try {
                // Obtener el ID del usuario seleccionado en la tabla
                int selectedRow = jTable_Usuarios.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Seleccione un usuario para actualizar.",
                            "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int idUsuario = (int) jTable_Usuarios.getValueAt(selectedRow, 0); // Suponiendo que el ID está en la columna 0

                // Crear el objeto usuarios con el ID correcto y los datos del formulario
                modelo.Usuarios usuarios = new modelo.Usuarios(idUsuario, nombreCompleto, nombreUsuario,
                        tipoId, numeroDocumento, genero, telefono, correoElectronico, sha1, rol);

                // Llamar al método para actualizar el usuario
                modelo.Usuarios.actualizarUsuarios(usuarios);
                populateJtable();

            } catch (Exception e) {
                // Manejo de excepciones
                JOptionPane.showMessageDialog(null, "Error al actualizar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            // Mensaje si los campos no son válidos
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos obligatorios.", "Campos Inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton_actualizarActionPerformed

    private void jButton_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirActionPerformed
        // TODO add your handling code here:
        // si se oprime el botón Imprimir
        if (jButton_imprimir.getText().equals("Imprimir")) {
            try {
                InputStream datosReporte = Utilidad.inputStreamReporte("Usuarios.jrxml");
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
            this.jScrollPane1.getViewport().add(this.jTable_Usuarios);
            this.jButton_imprimir.setText("Imprimir");
            this.jButton_imprimir.setMnemonic('V');
        }

    }//GEN-LAST:event_jButton_imprimirActionPerformed

    private void jRadioButton_MASCULINOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_MASCULINOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton_MASCULINOActionPerformed

    private void jButton_refrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_refrescarActionPerformed
        // TODO add your handling code here:
        limpiarCampos(); // Llamar al método para limpiar todos los campos
        populateJtable();
    }//GEN-LAST:event_jButton_refrescarActionPerformed

    public ArrayList<Usuarios> getUsersList() {
        ArrayList<Usuarios> userList = new ArrayList<Usuarios>();

        Connection con = modelo.ConexionDB.getConnection();
        String query = "SELECT * FROM usuarios";

        Statement st;
        ResultSet rs;

        try {

            st = con.createStatement();
            rs = st.executeQuery(query);
            Usuarios user;

            while (rs.next()) { //cargo cada registro llamándolo desde la tabla con sus respectivos nombres de campos
                user = new Usuarios(rs.getInt("id"),
                        rs.getString("nombre_completo"),
                        rs.getString("nombre_usuario"),
                        rs.getString("tipo_documento"),
                        rs.getString("numero_documento"),
                        rs.getString("genero"),
                        rs.getString("telefono"),
                        rs.getString("Correo_electronico"),
                        rs.getString("contraseña"),
                        rs.getString("rol"));
                userList.add(user);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ConsultasUsuarios.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userList;

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
            java.util.logging.Logger.getLogger(ConsultasUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultasUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultasUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultasUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsultasUsuarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton_actualizar;
    private javax.swing.JButton jButton_eliminar;
    private javax.swing.JButton jButton_first;
    private javax.swing.JButton jButton_imprimir;
    private javax.swing.JButton jButton_insertar;
    private javax.swing.JButton jButton_refrescar;
    private javax.swing.JButton jButton_siguiente;
    private javax.swing.JComboBox<String> jComboBox1_tipo_d;
    private javax.swing.JComboBox<String> jComboBox_rol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField;
    private javax.swing.JRadioButton jRadioButton_FEMENINO;
    private javax.swing.JRadioButton jRadioButton_MASCULINO;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable jTable_Usuarios;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField1_numero_documento;
    private javax.swing.JTextField jTextField2_correo;
    private javax.swing.JTextField jTextField_ID;
    private javax.swing.JTextField jTextField_NOMBRE_COMPLETO;
    private javax.swing.JTextField jTextField_NOMBRE_USUARIO;
    private javax.swing.JTextField jTextField_TELEFONO;
    // End of variables declaration//GEN-END:variables

    public void setExtendedState(int ICONIFIED) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
