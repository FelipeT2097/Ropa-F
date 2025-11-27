/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vista;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.table.JTableHeader;
import modelo.TablaProducto;
import reportes.VistaReportes;
import util.Utilidad;

/**
 *
 * @author piper
 */
public class Inventario extends javax.swing.JInternalFrame {

    /**
     * Creates new form Inventario
     */
    modelo.Producto producto;

    public Inventario() {
        super("Inventario", true, true, true, true);
        initComponents();

        setSize(400, 460);
        setVisible(true);
        jTable_productos.setShowGrid(true);
        jTable_productos.setGridColor(Color.YELLOW);
        jTable_productos.setSelectionBackground(Color.GRAY);
        JTableHeader th = jTable_productos.getTableHeader();
        th.setFont(new Font("Tahoma", Font.PLAIN, 16));

        // Inicializa el modelo de la tabla
        populateJtable("");  // Llama a este método para llenar la tabla
    }

    private JTabbedPane pestañasPadre; // asigna esto al JTabbedPane que gestione tu app

    private void abrirVentanaActualizar() {
        if (jTable_productos == null) {
            JOptionPane.showMessageDialog(this, "La tabla no está inicializada.");
            return;
        }

        int fila = jTable_productos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto primero.");
            return;
        }

        try {
            String id = jTable_productos.getValueAt(fila, 0).toString();
            String codigo = jTable_productos.getValueAt(fila, 1).toString();
            String nombre = jTable_productos.getValueAt(fila, 2).toString();
            String precio = jTable_productos.getValueAt(fila, 3).toString().replace("$", "").trim();
            String cantidad = jTable_productos.getValueAt(fila, 4).toString();
            String talla = jTable_productos.getValueAt(fila, 5).toString();
            String color = jTable_productos.getValueAt(fila, 6).toString();
            String genero = jTable_productos.getValueAt(fila, 7).toString();

            // Crear panel de actualización (puede ser tu FrmActualizarProductos que extienda JPanel)
            FrmActualizarProductos actualizar = new FrmActualizarProductos(); // preferible si es JPanel o componente
            actualizar.setDatosProducto(id, codigo, nombre, precio, cantidad, talla, color, genero);

            // Añadir como nueva pestaña (evitar duplicados)
            String titulo = "Editar: " + codigo;
            for (int i = 0; i < pestañasPadre.getTabCount(); i++) {
                if (pestañasPadre.getTitleAt(i).equals(titulo)) {
                    pestañasPadre.setSelectedIndex(i);
                    return;
                }
            }

            pestañasPadre.addTab(titulo, actualizar);
            pestañasPadre.setSelectedComponent(actualizar);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al abrir pestaña de actualización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void populateJtable(String val) {

        modelo.Producto producto = new modelo.Producto();
        ArrayList<modelo.Producto> productosList = producto.productoList(val);
        String[] colNames = {"Id", "Codigo", "Nombre", "Precio", "Cantidad", "Talla", "Color", "Genero"};
        Object[][] rows = new Object[productosList.size()][8];

        for (int i = 0; i < productosList.size(); i++) {
            rows[i][0] = productosList.get(i).getId();
            rows[i][1] = productosList.get(i).getCodigo();
            rows[i][2] = productosList.get(i).getNombre();
            rows[i][3] = "$ " + productosList.get(i).getPrecio();
            rows[i][4] = productosList.get(i).getCantidad();
            rows[i][5] = productosList.get(i).getTalla();
            rows[i][6] = productosList.get(i).getColor();
            rows[i][7] = productosList.get(i).getGenero();
        }

        // Instancia el modelo de tabla aquí
        TablaProducto modeloTabla = new TablaProducto(rows, colNames);
        jTable_productos.setModel(modeloTabla);
        jTable_productos.setRowHeight(40);

        // Configura anchos de columnas
        jTable_productos.getColumnModel().getColumn(5).setPreferredWidth(150); // Color
        jTable_productos.getColumnModel().getColumn(6).setPreferredWidth(120); // Talla o Imagen

       // modeloTabla.addMouseListenerToTable(jTable_productos, producto);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_inventario = new javax.swing.JPanel();
        jButton_actualizar = new javax.swing.JButton();
        jButton_eliminar_product = new javax.swing.JButton();
        jTextField_busqueda_products = new javax.swing.JTextField();
        jButton_buscar_product = new javax.swing.JButton();
        jButton_refrescar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_productos = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jButton_imprimir = new javax.swing.JButton();
        jButton_agregar_product1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel_inventario.setBackground(new java.awt.Color(255, 255, 255));

        jButton_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        jButton_actualizar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/update.png"))); // NOI18N
        jButton_actualizar.setText("Actualizar");
        jButton_actualizar.setBorder(new javax.swing.border.MatteBorder(null));
        jButton_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarActionPerformed(evt);
            }
        });

        jButton_eliminar_product.setBackground(new java.awt.Color(255, 51, 51));
        jButton_eliminar_product.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_eliminar_product.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Delect.png"))); // NOI18N
        jButton_eliminar_product.setText("Eliminar Producto");
        jButton_eliminar_product.setBorder(new javax.swing.border.MatteBorder(null));
        jButton_eliminar_product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminar_productActionPerformed(evt);
            }
        });

        jButton_buscar_product.setBackground(new java.awt.Color(255, 255, 255));
        jButton_buscar_product.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_buscar_product.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/busqueda.png"))); // NOI18N
        jButton_buscar_product.setText("Buscar");
        jButton_buscar_product.setBorder(new javax.swing.border.MatteBorder(null));
        jButton_buscar_product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_buscar_productActionPerformed(evt);
            }
        });

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

        jTable_productos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable_productos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_productosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_productos);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/logo2_1.png"))); // NOI18N

        jButton_imprimir.setBackground(new java.awt.Color(255, 255, 255));
        jButton_imprimir.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_imprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/imprimir.png"))); // NOI18N
        jButton_imprimir.setText("Imprimir");
        jButton_imprimir.setBorder(new javax.swing.border.MatteBorder(null));
        jButton_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprimirActionPerformed(evt);
            }
        });

        jButton_agregar_product1.setBackground(new java.awt.Color(51, 102, 0));
        jButton_agregar_product1.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_agregar_product1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/insert_2.png"))); // NOI18N
        jButton_agregar_product1.setText("Agregar Producto");
        jButton_agregar_product1.setBorder(new javax.swing.border.MatteBorder(null));
        jButton_agregar_product1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregar_product1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_inventarioLayout = new javax.swing.GroupLayout(jPanel_inventario);
        jPanel_inventario.setLayout(jPanel_inventarioLayout);
        jPanel_inventarioLayout.setHorizontalGroup(
            jPanel_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_inventarioLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_inventarioLayout.createSequentialGroup()
                            .addGap(41, 41, 41)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton_imprimir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton_eliminar_product, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_agregar_product1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_inventarioLayout.createSequentialGroup()
                        .addComponent(jTextField_busqueda_products, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_buscar_product, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(jButton_refrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 809, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(138, Short.MAX_VALUE))
        );
        jPanel_inventarioLayout.setVerticalGroup(
            jPanel_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_inventarioLayout.createSequentialGroup()
                .addGroup(jPanel_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_inventarioLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel_inventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton_refrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton_buscar_product, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_busqueda_products, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_inventarioLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_agregar_product1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_eliminar_product, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jButton_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_inventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel_inventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarActionPerformed
        // TODO add your handling code here:
        FrmActualizarProductos actualizarProductos = new FrmActualizarProductos();
        actualizarProductos.pack();
        actualizarProductos.setVisible(true);
        actualizarProductos.setLocationRelativeTo(null);
        actualizarProductos.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }//GEN-LAST:event_jButton_actualizarActionPerformed

    private void jButton_refrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_refrescarActionPerformed
        // TODO add your handling code here:
        populateJtable("");
    }//GEN-LAST:event_jButton_refrescarActionPerformed

    private void jButton_buscar_productActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_buscar_productActionPerformed
        // TODO add your handling code here:
        populateJtable(jTextField_busqueda_products.getText());
    }//GEN-LAST:event_jButton_buscar_productActionPerformed

    private void jButton_eliminar_productActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminar_productActionPerformed
        // TODO add your handling code here:
        try {
            Integer rowIndex = jTable_productos.getSelectedRow();
            Integer id = Integer.valueOf(jTable_productos.getValueAt(rowIndex, 0).toString());
            modelo.Producto.eliminarProducto(id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un producto de la tabla", "No se ha seleccionado ningún producto", 2);
        }
    }//GEN-LAST:event_jButton_eliminar_productActionPerformed

    private void jButton_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirActionPerformed
        // TODO add your handling code here:
        // si se oprime el botón Imprimir
        if (jButton_imprimir.getText().equals("Imprimir")) {
            try {
                InputStream datosReporte = Utilidad.inputStreamReporte("RRProductos.jrxml");
                Map<String, String> parametros = new HashMap<>();
                parametros.put("RUsuarios", "Juan Felipe Triana ");

                // Mostrar el reporte en el panel
                Container panelReporte = VistaReportes.mostrarReporte(datosReporte, parametros);
                panelReporte.setPreferredSize(new Dimension(600, 700)); // Ajusta el tamaño según sea necesario

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
            this.jScrollPane1.getViewport().add(this.jTable_productos);
            this.jButton_imprimir.setText("Imprimir");
            this.jButton_imprimir.setMnemonic('V');
        }
    }//GEN-LAST:event_jButton_imprimirActionPerformed

    private void jButton_agregar_product1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregar_product1ActionPerformed
        // TODO add your handling code here:
        FrmAgregarProductos agregarProductos = new FrmAgregarProductos();
        agregarProductos.pack();
        agregarProductos.setVisible(true);
        agregarProductos.setLocationRelativeTo(null);
        agregarProductos.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }//GEN-LAST:event_jButton_agregar_product1ActionPerformed

    private void jTable_productosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_productosMouseClicked
        // TODO add your handling code here:
// Detectar si el clic fue en las columnas ID (0) o Codigo (1)
        int columna = jTable_productos.columnAtPoint(evt.getPoint());
        int fila = jTable_productos.getSelectedRow();

        // Si hizo clic en la columna ID (0) o Codigo (1)
        if ((columna == 0 || columna == 1) && fila != -1) {
            try {
                // Obtener los datos de la fila seleccionada
                String id = jTable_productos.getValueAt(fila, 0).toString();
                String codigo = jTable_productos.getValueAt(fila, 1).toString();
                String nombre = jTable_productos.getValueAt(fila, 2).toString();
                String precio = jTable_productos.getValueAt(fila, 3).toString().replace("$", "").trim();
                String cantidad = jTable_productos.getValueAt(fila, 4).toString();
                String talla = jTable_productos.getValueAt(fila, 5).toString();
                String color = jTable_productos.getValueAt(fila, 6).toString();
                String genero = jTable_productos.getValueAt(fila, 7).toString();

                // Crear y configurar la ventana de actualización
                FrmActualizarProductos actualizarProductos = new FrmActualizarProductos();
                actualizarProductos.setDatosProducto(id, codigo, nombre, precio, cantidad, talla, color, genero);
                actualizarProductos.pack();
                actualizarProductos.setVisible(true);
                actualizarProductos.setLocationRelativeTo(null);
                actualizarProductos.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al abrir ventana de actualización: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jTable_productosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_actualizar;
    private javax.swing.JButton jButton_agregar_product1;
    private javax.swing.JButton jButton_buscar_product;
    private javax.swing.JButton jButton_eliminar_product;
    private javax.swing.JButton jButton_imprimir;
    private javax.swing.JButton jButton_refrescar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel_inventario;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_productos;
    private javax.swing.JTextField jTextField_busqueda_products;
    // End of variables declaration//GEN-END:variables
}
