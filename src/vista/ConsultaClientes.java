/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.Auditoria;
import modelo.Clientes;
import modelo.Proveedores;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.io.InputStream;
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
import modelo.Usuario_Sesion;
import reportes.VistaReportes;
import util.Utilidad;

/**
 *
 * @author piper
 */
public class ConsultaClientes extends javax.swing.JInternalFrame {

    private ArrayList<Clientes> clienListCache;

    public ConsultaClientes() {
        super("Consultas", true, true, true, true);
        initComponents();
        setSize(595, 460);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        jTextField_ID.setEnabled(false);
        clienListCache = getClienList();  // Inicializar la lista

        // Cargar la lista inicialmente
        populateJtable("");
        jTable_Clientes.setShowGrid(true);
        jTable_Clientes.setGridColor(Color.YELLOW);
        jTable_Clientes.setSelectionBackground(Color.gray);

        JTableHeader th = jTable_Clientes.getTableHeader();
        th.setFont(new Font("Tahoma", Font.PLAIN, 16));

        // REGISTRAR ACCESO AL MÓDULO
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "Clientes",
                    "Accedió al módulo de Clientes"
            );
            System.out.println("Acceso a ConsultaClientes registrado");
        } catch (Exception e) {
            System.err.println("Error al registrar acceso: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        jTextField_ID.setText("");
        jTextField_nombre_cliente.setText("");
        jTextField1_numero_documento.setText("");
        jTextField_telefono.setText("");
        jTextField2_correo.setText("");
        jTextField3_direccion.setText("");
        jTextField4_ciudad.setText("");

    }

    public static ConsultaClientes ventanaPrincipal;
    private Integer Id;
    int pos = 0;

    public void populateJtable(String prove) {

        // proveeListCache = getProveeList();
        // Definir las columnas de la tabla
        String[] colNames = {"ID", "Nombre Completo", "Tipo ID", "Documento", "Genero", "Telefono", "Correo", "Direccion", "Ciudad"};

        // Crear una matriz para almacenar las filas de la tabla
        Object[][] rows = new Object[clienListCache.size()][9];

        // Rellenar la matriz con los datos de la lista de proveedores
        for (int i = 0; i < clienListCache.size(); i++) {
            rows[i][0] = clienListCache.get(i).getId();
            rows[i][1] = clienListCache.get(i).getNombreCompleto();
            rows[i][2] = clienListCache.get(i).getTipoDocumento();
            rows[i][3] = clienListCache.get(i).getNumeroDocumento();
            rows[i][4] = clienListCache.get(i).getGenero();
            rows[i][5] = clienListCache.get(i).getTelefono();
            rows[i][6] = clienListCache.get(i).getCorreoElectronico();
            rows[i][7] = clienListCache.get(i).getDireccion();
            rows[i][8] = clienListCache.get(i).getCiudad();
        }

        // Crear el modelo de la tabla con los datos y las columnas
        DefaultTableModel model = new DefaultTableModel(rows, colNames);

        // Asignar el modelo a la JTable
        jTable_Clientes.setModel(model);
        jTable_Clientes.setRowHeight(30);
    }

    private void cargarTablaClientes() {
        clienListCache = getClienList();  // Recargar desde BD
        populateJtable("");  // Actualizar la tabla visual
    }

    public void ShowItem(int index) {
        // VALIDACIÓN: Verificar que la cache esté cargada y el índice sea válido
        if (clienListCache == null || clienListCache.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "La lista de clientes está vacía.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (index < 0 || index >= clienListCache.size()) {
            JOptionPane.showMessageDialog(null,
                    "Índice no válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Obtener el proveedor UNA sola vez
        Clientes clientes = clienListCache.get(index);

        // Asignar valores a los campos del formulario
        jTextField_ID.setText(Integer.toString(clientes.getId()));
        jTextField_nombre_cliente.setText(clientes.getNombreCompleto());
        jComboBox1_tipo_d.setSelectedItem(clientes.getTipoDocumento());
        jTextField1_numero_documento.setText(clientes.getNumeroDocumento());
        // Manejo del género
        String genero = clientes.getGenero();
        jRadioButton_masculino.setSelected(genero.equals("Hombre"));
        jRadioButton_femenino.setSelected(genero.equals("Mujer"));
        jRadioButton_empresa.setSelected(genero.equals("Empresa"));
        jTextField_telefono.setText(clientes.getTelefono());
        jTextField2_correo.setText(clientes.getCorreoElectronico());
        jTextField3_direccion.setText(clientes.getCorreoElectronico());
        jTextField4_ciudad.setText(clientes.getCiudad());
    }

    public boolean verifyFields() {

        String name = jTextField_nombre_cliente.getText();
        String doc = jTextField1_numero_documento.getText();
        String telefono = jTextField_telefono.getText();
        String email = jTextField2_correo.getText();
        String direccion = jTextField3_direccion.getText();
        String ciudad = jTextField4_ciudad.getText();

        // Verificar si hay campos vacíos
        if (name.trim().equals("") || doc.trim().equals("") || telefono.trim().equals("") || email.trim().equals("") || direccion.trim().equals("") || ciudad.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Uno o mas campos estan vacios", "Campos vacios", 2);
            return false;
        } else {
            return true;
        }

    }

    private boolean validarCampos() {
        if (this.jTextField_nombre_cliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠️ El nombre es obligatorio");
            return false;
        }

        if (this.jTextField1_numero_documento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠️ El número de documento es obligatorio");
            return false;
        }

        if (this.jTextField_telefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠️ El teléfono es obligatorio");
            return false;
        }

        return true;
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
        jTextField_nombre_cliente = new javax.swing.JTextField();
        jTextField_telefono = new javax.swing.JTextField();
        jRadioButton_masculino = new javax.swing.JRadioButton();
        jRadioButton_femenino = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Clientes = new javax.swing.JTable();
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
        jRadioButton_empresa = new javax.swing.JRadioButton();
        jButton_refrescar = new javax.swing.JButton();
        jButton_imprimir = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField4_ciudad = new javax.swing.JTextField();
        jTextField3_direccion = new javax.swing.JTextField();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel1.setText("ID:");

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel2.setText("Nombre Completo:");

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel5.setText("Telefono:");

        jLabel6.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel6.setText("Genero:");

        jRadioButton_masculino.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
        jRadioButton_masculino.setText("Hombre");
        jRadioButton_masculino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_masculinoActionPerformed(evt);
            }
        });

        jRadioButton_femenino.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
        jRadioButton_femenino.setText("Mujer");

        jTable_Clientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable_Clientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_ClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_Clientes);

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
        jButton4.setPreferredSize(new java.awt.Dimension(78, 25));
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
        jLabel11.setText("Dirección:");

        jTextField2_correo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2_correoActionPerformed(evt);
            }
        });

        jRadioButton_empresa.setText("Empresa");

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

        jLabel12.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel12.setText("Correo Electronico:");

        jLabel13.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel13.setText("Ciudad:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel11)
                    .addComponent(jLabel13))
                .addGap(65, 65, 65)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1_numero_documento, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jTextField_nombre_cliente)
                        .addComponent(jTextField_ID, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jComboBox1_tipo_d, 0, 201, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButton_femenino)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButton_masculino)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton_empresa))
                    .addComponent(jTextField2_correo, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4_ciudad, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(171, 171, 171)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton_insertar)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1_actualizar)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_eliminar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_first))
                    .addComponent(jButton_imprimir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton_siguiente)
                        .addGap(27, 27, 27)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton_refrescar))
                .addGap(37, 37, 37)
                .addComponent(jButton5)
                .addGap(31, 31, 31))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField_nombre_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jComboBox1_tipo_d, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jTextField1_numero_documento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jRadioButton_femenino)
                                .addComponent(jRadioButton_masculino)
                                .addComponent(jRadioButton_empresa)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jTextField_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField2_correo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField3_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextField4_ciudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_refrescar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_insertar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_first, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton_siguiente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
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

    private void jTable_ClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_ClientesMouseClicked
        // TODO add your handling code here:
        Integer rowIndex = jTable_Clientes.getSelectedRow();
        Id = Integer.valueOf(jTable_Clientes.getValueAt(rowIndex, 0).toString());
        jTextField_ID.setText(jTable_Clientes.getValueAt(rowIndex, 0).toString());
        jTextField_nombre_cliente.setText(jTable_Clientes.getValueAt(rowIndex, 1).toString());
        jComboBox1_tipo_d.setSelectedItem(jTable_Clientes.getValueAt(rowIndex, 2).toString());
        jTextField1_numero_documento.setText(jTable_Clientes.getValueAt(rowIndex, 3).toString());
        String genero = (jTable_Clientes.getValueAt(rowIndex, 4).toString());

        if (genero.equals("Hombre")) {
            jRadioButton_femenino.setSelected(false);
            jRadioButton_masculino.setSelected(true);
            jRadioButton_empresa.setSelected(false);
        } else if (genero.equals("Mujer")) {
            jRadioButton_masculino.setSelected(false);
            jRadioButton_femenino.setSelected(true);
            jRadioButton_empresa.setSelected(false);
        } else if (genero.equals("Empresa")) {
            jRadioButton_masculino.setSelected(false);
            jRadioButton_femenino.setSelected(false);
            jRadioButton_empresa.setSelected(true);
        }
        jTextField_telefono.setText(jTable_Clientes.getValueAt(rowIndex, 5).toString());
        jTextField2_correo.setText(jTable_Clientes.getValueAt(rowIndex, 6).toString());
        jTextField3_direccion.setText(jTable_Clientes.getValueAt(rowIndex, 7).toString());
        jTextField4_ciudad.setText(jTable_Clientes.getValueAt(rowIndex, 8).toString());

    }//GEN-LAST:event_jTable_ClientesMouseClicked

    private void jButton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarActionPerformed
        // TODO add your handling code here:
        try {
            int filaSeleccionada = this.jTable_Clientes.getSelectedRow();

            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(null, "⚠️ Debe seleccionar un cliente de la tabla");
                return;
            }

            //OBTENER DATOS ANTES DE ELIMINAR
            int idClienteEliminar = Integer.parseInt(
                    this.jTable_Clientes.getValueAt(filaSeleccionada, 0).toString()
            );
            String numeroDocumento = this.jTable_Clientes.getValueAt(filaSeleccionada, 3).toString();
            String nombreCompleto = this.jTable_Clientes.getValueAt(filaSeleccionada, 1).toString();

            System.out.println("?Eliminando cliente: " + numeroDocumento + " - " + nombreCompleto);

            // Eliminar
            Clientes.eliminarCliente(idClienteEliminar);

            // ⭐ REGISTRAR EN AUDITORÍA
            try {
                Auditoria auditoria = new Auditoria();
                auditoria.registrar(
                        Usuario_Sesion.getInstancia().getNombreUsuario(),
                        "ELIMINAR",
                        "Clientes",
                        "Eliminó cliente: " + numeroDocumento + " - " + nombreCompleto
                );
                System.out.println("Eliminación de cliente registrada");
            } catch (Exception e) {
                System.err.println("Error al registrar: " + e.getMessage());
            }

            // Recargar y limpiar
            this.cargarTablaClientes();
            this.limpiarCampos();

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage());
        }
    }//GEN-LAST:event_jButton_eliminarActionPerformed

    private void jButton_firstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_firstActionPerformed
        // TODO add your handling code here:
        pos = 0;
        ShowItem(pos);
    }//GEN-LAST:event_jButton_firstActionPerformed

    private void jButton_siguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_siguienteActionPerformed
        // TODO add your handling code here:
        if (clienListCache != null && !clienListCache.isEmpty()) {
            pos++;
            if (pos >= clienListCache.size()) {
                pos = clienListCache.size() - 1;
            }
            ShowItem(pos);
        } else {
            JOptionPane.showMessageDialog(this,
                    "La lista de proveedores está vacía o no se pudo cargar.");
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
        if (clienListCache != null && !clienListCache.isEmpty()) {
            pos = clienListCache.size() - 1;
            ShowItem(pos);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton_insertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_insertarActionPerformed
        // TODO add your handling code here:
        // Crear un nuevo objeto DUsers
        Clientes clientes = new Clientes();

        // Obtener los valores de los campos de texto
        clientes.setNombreCompleto(jTextField_nombre_cliente.getText());
        clientes.setNumeroDocumento(jTextField1_numero_documento.getText());
        clientes.setTipoDocumento((String) jComboBox1_tipo_d.getSelectedItem());
        clientes.setTelefono(jTextField_telefono.getText());
        clientes.setDireccion(jTextField3_direccion.getText());
        clientes.setCiudad(jTextField4_ciudad.getText());
        // Obtener el género seleccionado de los botones de radio
        String genero = "";
        if (jRadioButton_masculino.isSelected()) {
            genero = "Hombre";
        } else if (jRadioButton_femenino.isSelected()) {
            genero = "Mujer";
        } else if (jRadioButton_empresa.isSelected()) {
            genero = "Empresa";
        }
        clientes.setGenero(genero);
        clientes.setCorreoElectronico(jTextField2_correo.getText());

        // Llamar al método para insertar el usuario
        modelo.Clientes.insertarClientes(clientes);

        // registra cuando inserta un cliente en audtoria
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrar(
                    Usuario_Sesion.getInstancia().getNombreUsuario(),
                    "CREAR",
                    "Clientes",
                    "Creó cliente: " + clientes.getNumeroDocumento() + " - " + clientes.getNombreCompleto()
            );
            System.out.println("Creación de cliente registrada");

        } catch (Exception e) {
            System.err.println("Error al registrar: " + e.getMessage());
        }
        cargarTablaClientes();
        limpiarCampos();
    }//GEN-LAST:event_jButton_insertarActionPerformed

    private void jButton1_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1_actualizarActionPerformed
        // TODO add your handling code here:
        String nombreCompleto = jTextField_nombre_cliente.getText().trim();
        String tipoId = (String) jComboBox1_tipo_d.getSelectedItem();
        String numeroDocumento = jTextField1_numero_documento.getText().trim();
        String telefono = jTextField_telefono.getText().trim();
        String correoElectronico = jTextField2_correo.getText().trim();
        String direccion = jTextField3_direccion.getText().trim();
        String ciudad = jTextField4_ciudad.getText().trim();

        String genero = "Hombre";
        if (jRadioButton_femenino.isSelected()) {
            genero = "Mujer";
        } else if (jRadioButton_empresa.isSelected()) {
            genero = "Empresa";
        }

        if (verifyFields()) {
            try {
                int selectedRow = jTable_Clientes.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null,
                            "Seleccione un cliente para actualizar.",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int idCliente = (int) jTable_Clientes.getValueAt(selectedRow, 0);

                Clientes cliente = new Clientes(
                        idCliente, nombreCompleto, tipoId, numeroDocumento,
                        genero, telefono, correoElectronico, direccion, ciudad
                );

                //Llamada correcta (método estático)
                Clientes.actualizarClientes(cliente);

                // registra cuando inserta un cliente en audtoria
                try {
                    Auditoria auditoria = new Auditoria();
                    auditoria.registrarModificacion(
                            Usuario_Sesion.getInstancia().getNombreUsuario(),
                            "Clientes",
                            "Actualizó cliente: " + numeroDocumento + " - " + nombreCompleto
                    );
                    System.out.println("Actualización de cliente registrada");
                } catch (Exception e) {
                    System.err.println("Error al registrar: " + e.getMessage());
                }
                //Refrescar tabla
                populateJtable("");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error al actualizar el cliente: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Por favor, complete todos los campos obligatorios.",
                    "Campos Inválidos",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton1_actualizarActionPerformed

    private void jRadioButton_masculinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_masculinoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton_masculinoActionPerformed

    private void jButton_refrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_refrescarActionPerformed
        // TODO add your handling code here:
        limpiarCampos();
        populateJtable("");
    }//GEN-LAST:event_jButton_refrescarActionPerformed

    private void jButton_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirActionPerformed

        // si se oprime el botón Imprimir
        if (jButton_imprimir.getText().equals("Imprimir")) {
            try {
                InputStream datosReporte = Utilidad.inputStreamReporte("RClientes.jrxml");
                Map<String, String> parametros = new HashMap<>();
                parametros.put("RUsuarios", "Juan Felipe Triana ");

                // Mostrar el reporte en el panel
                Container panelReporte = VistaReportes.mostrarReporte(datosReporte, parametros);
                panelReporte.setPreferredSize(new Dimension(600, 400));

                this.jScrollPane1.getViewport().removeAll();
                this.jScrollPane1.getViewport().add(panelReporte);
                this.jScrollPane1.revalidate();
                this.jScrollPane1.repaint();

                this.jButton_imprimir.setText("Volver");
                this.jButton_imprimir.setMnemonic('V');
                this.jButton_imprimir.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/atras.png")));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "No se puede mostrar los Clientes\n" + ex.getMessage());
            }
        } else if (jButton_imprimir.getText().equals("Volver")) {
            this.jScrollPane1.getViewport().removeAll();
            this.jScrollPane1.getViewport().add(this.jTable_Clientes);
            this.jButton_imprimir.setText("Imprimir");
            this.jButton_imprimir.setMnemonic('I');
            this.jButton_imprimir.setIcon(new ImageIcon(this.getClass().getResource("/imagenes/imprimir.png")));
        }
    }//GEN-LAST:event_jButton_imprimirActionPerformed

    private void jTextField2_correoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2_correoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2_correoActionPerformed

    public ArrayList<Clientes> getClienList() {
        ArrayList<Clientes> clientList = new ArrayList<>();
        String query = "SELECT * FROM clientes";

        try (Connection con = modelo.ConexionDB.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Clientes cliente = new Clientes(
                        rs.getInt("id"),
                        rs.getString("nombre_completo"),
                        rs.getString("tipo_documento_cliente"),
                        rs.getString("numero_documento"),
                        rs.getString("genero"),
                        rs.getString("telefono"),
                        rs.getString("correo_electronico"),
                        rs.getString("direccion"),
                        rs.getString("ciudad")
                );
                clientList.add(cliente);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar el cliente:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ConsultaClientes.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        return clientList;
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
            java.util.logging.Logger.getLogger(ConsultaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultaClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsultaClientes().setVisible(true);
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
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton_empresa;
    private javax.swing.JRadioButton jRadioButton_femenino;
    private javax.swing.JRadioButton jRadioButton_masculino;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Clientes;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField1_numero_documento;
    private javax.swing.JTextField jTextField2_correo;
    private javax.swing.JTextField jTextField3_direccion;
    private javax.swing.JTextField jTextField4_ciudad;
    private javax.swing.JTextField jTextField_ID;
    private javax.swing.JTextField jTextField_nombre_cliente;
    private javax.swing.JTextField jTextField_telefono;
    // End of variables declaration//GEN-END:variables

}
