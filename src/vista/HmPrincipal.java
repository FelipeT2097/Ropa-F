/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author piper
 */
public class HmPrincipal extends javax.swing.JFrame {

    /**
     * Creates new form HmPrincipal
     */
    public HmPrincipal() {
        initComponents();

        crearPanelUsuarioEnBarra();

        timer.start();
        configurarPermisosPorRol();

        /*  ImageIcon icFacturas = new ImageIcon(getClass().getResource("/imagenes/Factura.png"));
        Icon iconoFac = new ImageIcon(icFacturas.getImage().getScaledInstance(50,55, Image.SCALE_DEFAULT));
        jButton_factura.setIcon(iconoFac);
        
        ImageIcon icRecibos = new ImageIcon(getClass().getResource("/imagenes/Recibos.png"));
        Icon iconoRec = new ImageIcon(icRecibos.getImage().getScaledInstance(50,55, Image.SCALE_DEFAULT));
        jButton_recibos.setIcon(iconoRec);
        
        ImageIcon icAlmacen = new ImageIcon(getClass().getResource("/imagenes/Almacen.png"));
        Icon iconoAlm = new ImageIcon(icAlmacen.getImage().getScaledInstance(50,55, Image.SCALE_DEFAULT));
        jButton_almacen.setIcon(iconoAlm); */
        ImageIcon mGestionUsuarios = new ImageIcon(getClass().getResource("/imagenes/GUsuarios.png"));
        Icon iconoGesUsuarios = new ImageIcon(mGestionUsuarios.getImage().getScaledInstance(20, 15, Image.SCALE_DEFAULT));
        jMenu_GUSUARIOS.setIcon(iconoGesUsuarios);

        ImageIcon GProveedores = new ImageIcon(getClass().getResource("/imagenes/proveedor.png"));
        Icon iconProveedores = new ImageIcon(GProveedores.getImage().getScaledInstance(20, 15, Image.SCALE_DEFAULT));
        jMenu_gestion_proveedores.setIcon(iconProveedores);

        ImageIcon mCatalogos = new ImageIcon(getClass().getResource("/imagenes/catalogo.png"));
        Icon iconoCatalogos = new ImageIcon(mCatalogos.getImage().getScaledInstance(20, 15, Image.SCALE_DEFAULT));
        jMenu_CATALOGO.setIcon(iconoCatalogos);
        
        ImageIcon mClientes = new ImageIcon(getClass().getResource("/imagenes/GUsuarios.png"));
        Icon iconoClientes = new ImageIcon(mClientes.getImage().getScaledInstance(20, 15, Image.SCALE_DEFAULT));
        jMenu_gestion_clientes.setIcon(iconoGesUsuarios);

        timer.start();

        // Configurar permisos según el rol
        configurarPermisosPorRol();

        // Mostrar información del usuario
        mostrarInfoUsuario();
    }

    /**
     * Crea un panel con info del usuario en la barra de menú
     */
    private void crearPanelUsuarioEnBarra() {
        modelo.Usuario_Sesion sesion = modelo.Usuario_Sesion.getInstancia();

        // Crear un panel para el usuario
        javax.swing.JPanel panelUsuario = new javax.swing.JPanel();
        panelUsuario.setOpaque(false);
        panelUsuario.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        // Label con el nombre del usuario
        javax.swing.JLabel lblUsuario = new javax.swing.JLabel(
                "👤 " + sesion.getNombreUsuario() + " (" + sesion.getRol().toUpperCase() + ")"
        );
        lblUsuario.setFont(new java.awt.Font("Lucida Sans", java.awt.Font.BOLD, 12));
        lblUsuario.setForeground(new java.awt.Color(0, 102, 204));

        // Botón de cerrar sesión
        javax.swing.JButton btnCerrarSesion = new javax.swing.JButton("Cerrar Sesión");
        btnCerrarSesion.setFont(new java.awt.Font("Lucida Sans", java.awt.Font.PLAIN, 11));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarSesion();
            }
        });

        // Agregar componentes al panel
        panelUsuario.add(lblUsuario);
        panelUsuario.add(btnCerrarSesion);

        // Agregar glue para empujar todo a la derecha
        jMenuBar1.add(javax.swing.Box.createHorizontalGlue());

        // Agregar el panel a la barra de menú
        jMenuBar1.add(panelUsuario);
    }

    /**
     * Cerrar sesión del usuario
     */
    private void cerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            modelo.Usuario_Sesion.getInstancia().cerrarSesion();
            this.dispose();

            Login login = new Login();
            login.setVisible(true);
        }
    }

    /**
     * Configura qué menús puede ver cada rol
     */
    private void configurarPermisosPorRol() {
        modelo.Usuario_Sesion sesion = modelo.Usuario_Sesion.getInstancia();

        if (sesion.esVendedor()) {
            // Los vendedores NO pueden gestionar usuarios
            jMenu_GUSUARIOS.setVisible(false);

            //❌ Los vendedores NO pueden gestionar proveedores
            jMenu_gestion_proveedores.setVisible(false);

            // Solo pueden ver catálogo y hacer ventas
            jMenu_CATALOGO.setVisible(true);

        } else if (sesion.esAlmacenista()) {
            // Los almacenistas NO pueden gestionar usuarios
            jMenu_GUSUARIOS.setVisible(false);

            //  Pueden gestionar proveedores
            jMenu_gestion_proveedores.setVisible(true);

            // Pueden gestionar catálogo/inventario
            jMenu_CATALOGO.setVisible(true);

        } else if (sesion.esAdmin()) {
            // Los administradores ven TODO
            jMenu_GUSUARIOS.setVisible(true);
            jMenu_gestion_proveedores.setVisible(true);
            jMenu_CATALOGO.setVisible(true);
        }
    }

    /**
     * Muestra info del usuario logueado
     */
    private void mostrarInfoUsuario() {
        modelo.Usuario_Sesion sesion = modelo.Usuario_Sesion.getInstancia();

        jlabel_usuario.setText(sesion.getNombreUsuario() + " (" + sesion.getRol().toUpperCase() + ")");
    }

    Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Calendar cal = new GregorianCalendar();
            int hh, mm, ss, dia, mes, aa;
            hh = cal.get(Calendar.HOUR_OF_DAY);
            ss = cal.get(Calendar.SECOND);
            mm = cal.get(Calendar.MINUTE);

            dia = cal.get(Calendar.DAY_OF_MONTH);
            mes = cal.get(Calendar.MONTH);
            aa = cal.get(Calendar.YEAR);

            jLabel_hora.setText(hh + ":" + mm + ":" + ss);
            jLabel_fecha.setText(dia + "/" + (mes + 1) + "/" + aa);

        }
    });

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel_hora = new javax.swing.JLabel();
        jLabel_fecha = new javax.swing.JLabel();
        jlabel_usuario = new javax.swing.JLabel();
        jDesktopPane_escritorio = new javax.swing.JDesktopPane();
        jLabel4 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu_GUSUARIOS = new javax.swing.JMenu();
        jMenuItem_Usuarios = new javax.swing.JMenuItem();
        jMenu_gestion_proveedores = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu_gestion_clientes = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu_CATALOGO = new javax.swing.JMenu();
        jMenuItem_stock = new javax.swing.JMenuItem();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Opciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 0, 14))); // NOI18N
        jPanel1.setToolTipText("");
        jPanel1.setName(""); // NOI18N

        jLabel1.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel1.setText("Usuario:");

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel2.setText("Hora:");

        jLabel3.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel3.setText("Fecha:");

        jLabel_hora.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel_fecha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jlabel_usuario.setFont(new java.awt.Font("Felix Titling", 1, 18)); // NOI18N

        jDesktopPane_escritorio.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jDesktopPane_escritorio.setLayout(new java.awt.FlowLayout());

        jLabel4.setIcon(new javax.swing.ImageIcon("C:\\Users\\piper\\Documents\\NetBeansProjects\\JAVA\\ropF\\src\\imagenes\\giff.gif")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_hora)
                            .addComponent(jLabel_fecha)
                            .addComponent(jlabel_usuario)))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(jDesktopPane_escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(jLabel4)
                        .addGap(159, 159, 159)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jlabel_usuario))
                        .addGap(44, 44, 44)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel_hora))
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel_fecha)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jDesktopPane_escritorio, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(79, 79, 79))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jMenu_GUSUARIOS.setText("Gestion de Usuarios");

        jMenuItem_Usuarios.setText("Usuarios");
        jMenuItem_Usuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_UsuariosActionPerformed(evt);
            }
        });
        jMenu_GUSUARIOS.add(jMenuItem_Usuarios);

        jMenuBar1.add(jMenu_GUSUARIOS);

        jMenu_gestion_proveedores.setText("Gestion de Proveedores");

        jMenuItem1.setText("Proveedores");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu_gestion_proveedores.add(jMenuItem1);

        jMenuBar1.add(jMenu_gestion_proveedores);

        jMenu_gestion_clientes.setText("Gestion de Clientes");

        jMenuItem2.setText("Clientes");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu_gestion_clientes.add(jMenuItem2);

        jMenuBar1.add(jMenu_gestion_clientes);

        jMenu_CATALOGO.setText("Catalogo");

        jMenuItem_stock.setText("Gestion de productos");
        jMenuItem_stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_stockActionPerformed(evt);
            }
        });
        jMenu_CATALOGO.add(jMenuItem_stock);

        jMenuBar1.add(jMenu_CATALOGO);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem_UsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_UsuariosActionPerformed
        // TODO add your handling code here:
        ConsultasU Busuarios = new ConsultasU();
        jDesktopPane_escritorio.add(Busuarios);
        Busuarios.show();
    }//GEN-LAST:event_jMenuItem_UsuariosActionPerformed

    private void jMenuItem_stockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_stockActionPerformed
        // TODO add your handling code here:
        Inventario SistemStock = new Inventario();
        jDesktopPane_escritorio.add(SistemStock);
        SistemStock.show();
    }//GEN-LAST:event_jMenuItem_stockActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        ConsultaProveedores Bproveedores = new ConsultaProveedores();
        jDesktopPane_escritorio.add(Bproveedores);
        Bproveedores.show();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        ConsultaClientes Bclientes = new ConsultaClientes();
        jDesktopPane_escritorio.add(Bclientes);
        Bclientes.show();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

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
            java.util.logging.Logger.getLogger(HmPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HmPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HmPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HmPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HmPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane jDesktopPane_escritorio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel_fecha;
    private javax.swing.JLabel jLabel_hora;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem_Usuarios;
    private javax.swing.JMenuItem jMenuItem_stock;
    private javax.swing.JMenu jMenu_CATALOGO;
    private javax.swing.JMenu jMenu_GUSUARIOS;
    private javax.swing.JMenu jMenu_gestion_clientes;
    private javax.swing.JMenu jMenu_gestion_proveedores;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JLabel jlabel_usuario;
    // End of variables declaration//GEN-END:variables

    /*public void setDatos (String nombre_usuario, String contraseña){
        jlabel_usuario.setText(nombre_usuario);
        
        } */
}
