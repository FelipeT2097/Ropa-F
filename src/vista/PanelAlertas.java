/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.GeneradorReportes;

/**
 *
 * @author piper
 */
public class PanelAlertas extends javax.swing.JInternalFrame {

    private int stockBajo;
    private int sinMovimiento;
    private int ventasHoy;
    private GeneradorReportes reportes;
    
    // Constructor sin argumentos para el diseñador
    public PanelAlertas() {
        initComponents();
    }
    
    //Muestra el detalle completo de las alertas
    private void mostrarDetalleCompleto() {
        if (reportes == null) return;
        
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("════════════════════════════════════════════════════════\n");
        mensaje.append("                       PANEL DE ALERTAS - SISTEMA INVENTARIO         \n");
        mensaje.append("════════════════════════════════════════════════════════\n\n");
        
        if (stockBajo > 0 || sinMovimiento > 0) {
            mensaje.append("ALERTAS IMPORTANTES:\n");
            mensaje.append("════════════════════════════════════════════════════════\n\n");
        }
        
        if (stockBajo > 0) {
            mensaje.append(String.format("STOCK BAJO: %d productos con menos de 10 unidades\n", stockBajo));
            mensaje.append("   -> Accion requerida: Revisar inventario urgente\n\n");
        }
        
        if (sinMovimiento > 0) {
            mensaje.append(String.format("BAJA ROTACION: %d productos sin ventas en 30+ dias\n", sinMovimiento));
            mensaje.append("   -> Considera: Promociones o descuentos\n\n");
            
            java.util.ArrayList<java.util.Map<String, Object>> productosSinMov = reportes.obtenerProductosSinMovimiento(30);
            if (!productosSinMov.isEmpty()) {
                mensaje.append("   Top 3 productos sin venta:\n");
                int limite = Math.min(3, productosSinMov.size());
                for (int i = 0; i < limite; i++) {
                    java.util.Map<String, Object> p = productosSinMov.get(i);
                    mensaje.append(String.format("   - %s (Stock: %d)\n", 
                        p.get("nombre"), p.get("cantidad")));
                }
                mensaje.append("\n");
            }
        }
        
        mensaje.append("RESUMEN DEL DIA:\n");
        mensaje.append("════════════════════════════════════════════════════════\n");
        mensaje.append(String.format("   Ventas hoy: %d\n", ventasHoy));
        
        java.util.Calendar cal = new java.util.GregorianCalendar();
        mensaje.append(String.format("   Fecha: %02d/%02d/%d\n", 
            cal.get(java.util.Calendar.DAY_OF_MONTH),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.YEAR)));
        mensaje.append(String.format("   Usuario: %s\n", 
            modelo.Usuario_Sesion.getInstancia().getNombreUsuario()));
        
        if (stockBajo > 0 || sinMovimiento > 0) {
            mensaje.append("\nRECOMENDACIONES:\n");
            mensaje.append("════════════════════════════════════════════════════════\n");
            if (stockBajo > 0) {
                mensaje.append("   - Realizar pedido de reposicion\n");
            }
            if (sinMovimiento > 0) {
                mensaje.append("   - Crear promociones para baja rotacion\n");
            }
        }
        
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(mensaje.toString());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", 0, 12));
        
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 500));
        
        int tipoMensaje = (stockBajo > 0 || sinMovimiento > 5) 
            ? javax.swing.JOptionPane.WARNING_MESSAGE 
            : javax.swing.JOptionPane.INFORMATION_MESSAGE;
        
        javax.swing.JOptionPane.showMessageDialog(this, scrollPane, 
            "Alertas del Sistema", tipoMensaje);
    }
     // Constructor con datos
    public PanelAlertas(int stockBajo, int sinMovimiento, int ventasHoy, GeneradorReportes reportes) {
        this.stockBajo = stockBajo;
        this.sinMovimiento = sinMovimiento;
        this.ventasHoy = ventasHoy;
        this.reportes = reportes;
        
        initComponents();
        configurarPanel();
    }
    
    //Configura el panel con los datos
    private void configurarPanel() {
    // Color según urgencia
    if (stockBajo > 0 || sinMovimiento > 5) {
        jPanel_fondo.setBackground(new java.awt.Color(255, 152, 0));
        setTitle("ALERTAS");
    } else {
        jPanel_fondo.setBackground(new java.awt.Color(76, 175, 80));
        setTitle("Sistema OK");
    }
    

    setIconifiable(true);
    setMaximizable(false);
    setResizable(true);
    
    // Actualizar textos
    if (stockBajo > 0) {
        jLabel_stock.setText(stockBajo + " productos con stock bajo");
        jLabel_stock.setVisible(true);
    } else {
        jLabel_stock.setVisible(false);
    }
    
    if (sinMovimiento > 0) {
        jLabel_movimiento.setText(sinMovimiento + " productos sin ventas (30+ dias)");
        jLabel_movimiento.setVisible(true);
    } else {
        jLabel_movimiento.setVisible(false);
    }
    
    jLabel_ventas.setText("Ventas hoy: " + ventasHoy);
    setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    
    // MouseAdapter para arrastrar y hacer click
    final java.awt.Point[] puntoInicial = {null};
    final boolean[] seArrastro = {false};
    
    java.awt.event.MouseAdapter adaptador = new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent e) {
            puntoInicial[0] = e.getPoint();
            seArrastro[0] = false;
        }
        
        public void mouseDragged(java.awt.event.MouseEvent e) {
            if (puntoInicial[0] != null) {
                seArrastro[0] = true;
                java.awt.Point ubicacion = getLocation();
                int x = ubicacion.x + e.getX() - puntoInicial[0].x;
                int y = ubicacion.y + e.getY() - puntoInicial[0].y;
                setLocation(x, y);
            }
        }
        
        public void mouseReleased(java.awt.event.MouseEvent e) {
            // Si no se arrastró, entonces fue un click
            if (!seArrastro[0]) {
                mostrarDetalleCompleto();
            }
        }
        
        public void mouseEntered(java.awt.event.MouseEvent e) {
            jPanel_fondo.setBorder(javax.swing.BorderFactory.createLineBorder(
                java.awt.Color.WHITE, 3));
        }
        
        public void mouseExited(java.awt.event.MouseEvent e) {
            jPanel_fondo.setBorder(null);
        }
    };
    
    // Aplicar a todos los componentes
    jPanel_fondo.addMouseListener(adaptador);
    jPanel_fondo.addMouseMotionListener(adaptador);
    
    jLabel_titulo.addMouseListener(adaptador);
    jLabel_titulo.addMouseMotionListener(adaptador);
    
    jLabel_stock.addMouseListener(adaptador);
    jLabel_stock.addMouseMotionListener(adaptador);
    
    jLabel_movimiento.addMouseListener(adaptador);
    jLabel_movimiento.addMouseMotionListener(adaptador);
    
    jLabel_ventas.addMouseListener(adaptador);
    jLabel_ventas.addMouseMotionListener(adaptador);

}
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanel_fondo = new javax.swing.JPanel();
        jLabel_titulo = new javax.swing.JLabel();
        jSeparator = new javax.swing.JSeparator();
        jLabel_stock = new javax.swing.JLabel();
        jLabel_movimiento = new javax.swing.JLabel();
        jLabel_ventas = new javax.swing.JLabel();
        jLabel_click_detalle = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel_fondo.setBackground(new java.awt.Color(255, 152, 0));
        jPanel_fondo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel_fondo.setToolTipText("");
        jPanel_fondo.setPreferredSize(new java.awt.Dimension(320, 220));
        jPanel_fondo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_titulo.setBackground(new java.awt.Color(255, 255, 255));
        jLabel_titulo.setFont(new java.awt.Font("Lucida Sans", 1, 16)); // NOI18N
        jLabel_titulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alerta.png"))); // NOI18N
        jLabel_titulo.setText("ALERTAS DEL SISTEMA");
        jPanel_fondo.add(jLabel_titulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        jSeparator.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator.setForeground(new java.awt.Color(255, 255, 255));
        jPanel_fondo.add(jSeparator, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 45, 420, 10));

        jLabel_stock.setFont(new java.awt.Font("Lucida Sans", 1, 13)); // NOI18N
        jLabel_stock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alert.png"))); // NOI18N
        jLabel_stock.setText("0 productos con stock bajo");
        jPanel_fondo.add(jLabel_stock, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));

        jLabel_movimiento.setFont(new java.awt.Font("Lucida Sans", 1, 13)); // NOI18N
        jLabel_movimiento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alert2.png"))); // NOI18N
        jLabel_movimiento.setText("0 productos sin ventas (30+ días)");
        jPanel_fondo.add(jLabel_movimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, -1));

        jLabel_ventas.setFont(new java.awt.Font("Lucida Sans", 1, 13)); // NOI18N
        jLabel_ventas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/estadistica.png"))); // NOI18N
        jLabel_ventas.setText("Ventas hoy: 0");
        jPanel_fondo.add(jLabel_ventas, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, -1));

        jLabel_click_detalle.setFont(new java.awt.Font("Lucida Sans", 1, 13)); // NOI18N
        jLabel_click_detalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/dedo.png"))); // NOI18N
        jLabel_click_detalle.setText("Click para ver detalles");
        jPanel_fondo.add(jLabel_click_detalle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, -1));

        jButton2.setBackground(new java.awt.Color(255, 51, 0));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Delect.png"))); // NOI18N
        jButton2.setText("CERRAR");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel_fondo.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 250, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_fondo, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_fondo, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(PanelAlertas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PanelAlertas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PanelAlertas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PanelAlertas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new PanelAlertas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel_click_detalle;
    private javax.swing.JLabel jLabel_movimiento;
    private javax.swing.JLabel jLabel_stock;
    private javax.swing.JLabel jLabel_titulo;
    private javax.swing.JLabel jLabel_ventas;
    private javax.swing.JPanel jPanel_fondo;
    private javax.swing.JSeparator jSeparator;
    // End of variables declaration//GEN-END:variables
}
