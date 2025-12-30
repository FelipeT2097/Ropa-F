/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.Auditoria;
import controlador.RegistrarReversiones;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author piper
 */
public class FrmReversiones extends javax.swing.JInternalFrame {

    // Controlador
    private RegistrarReversiones controlador;

    // Factura actual cargada
    private java.util.Map<String, Object> facturaActual;

    // Usuario actual del sistema
    private String usuarioActual;

    // Formateador de fechas
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public FrmReversiones(String usuario) {

        super("Reversiones", true, true, true, true); // Título, cerrable, redimensionable, movible, maximizable
        this.usuarioActual = usuario;
        this.controlador = new RegistrarReversiones();

        initComponents();
        deshabilitarCampos();
        cargarHistorial();

        // Centrar el formulario
        this.setSize(850, 800);

        //REGISTRAR ACCESO AL MÓDULO (AGREGAR AL FINAL)
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(
                    usuarioActual,
                    "Reversiones",
                    "Accedió al módulo de Reversiones"
            );
            System.out.println("Acceso al módulo de Reversiones registrado");
        } catch (Exception e) {
            System.err.println("Error al registrar acceso: " + e.getMessage());
        }
    }

    //Constructor sin parámetros (para diseñador)
    public FrmReversiones() {
        this("admin"); // Usuario por defecto
    }

    //Buscar factura por número o ID
    private void buscarFactura() {
        String busqueda = jTextField_numero_factura.getText().trim();

        if (busqueda.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresa un número de factura o ID",
                    "Campo vacío",
                    JOptionPane.WARNING_MESSAGE);
            jTextField_numero_factura.requestFocus();
            return;
        }

        // Buscar la factura
        facturaActual = controlador.buscarFactura(busqueda);

        if (facturaActual == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró la factura: " + busqueda,
                    "Factura no encontrada",
                    JOptionPane.ERROR_MESSAGE);
            limpiarFormulario();
            return;
        }

        // Validar si puede ser reversada
        int facturaId = ((Integer) facturaActual.get("id")).intValue();
        String errorValidacion = controlador.validarFacturaParaReversion(facturaId);

        if (errorValidacion != null) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ " + errorValidacion,
                    "No se puede reversar",
                    JOptionPane.WARNING_MESSAGE);
            limpiarFormulario();
            return;
        }

        // Cargar datos en la interfaz
        cargarDatosFactura();
        cargarProductos(facturaId);

        // Habilitar campos
        habilitarCampos();

        JOptionPane.showMessageDialog(this,
                "Factura encontrada y lista para reversar",
                "Factura cargada",
                JOptionPane.INFORMATION_MESSAGE);
    }

    //Cargar datos de la factura en los campos
    private void cargarDatosFactura() {
        try {
            // Número de factura
            jTextField1.setText((String) facturaActual.get("numero_factura"));

            // Cliente
            jTextField7.setText((String) facturaActual.get("nombre_cliente"));

            // Documento
            jTextField5.setText((String) facturaActual.get("numero_documento"));

            // Total
            double total = ((Double) facturaActual.get("total")).doubleValue();
            jTextField8.setText(String.format("$%.2f", total));

            // Fecha
            java.sql.Timestamp fecha = (java.sql.Timestamp) facturaActual.get("fecha_emision");
            jTextField4.setText(formatoFecha.format(fecha));

            // Estado
            String estado = (String) facturaActual.get("estado");
            jTextField6.setText(estado.toUpperCase());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar datos: " + e.getMessage());
        }
    }

    //Cargar productos de la factura en la tabla
    private void cargarProductos(int facturaId) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0); // Limpiar tabla

        java.util.List<java.util.Map<String, Object>> productos
                = controlador.obtenerProductosFactura(facturaId);

        for (java.util.Map<String, Object> producto : productos) {
            Object[] fila = new Object[5];

            fila[0] = producto.get("codigo");
            fila[1] = producto.get("nombre");
            fila[2] = producto.get("cantidad");

            double precioUnitario = ((Double) producto.get("precio_unitario")).doubleValue();
            fila[3] = String.format("$%.2f", precioUnitario);

            double totalProd = ((Double) producto.get("total")).doubleValue();
            fila[4] = String.format("$%.2f", totalProd);

            modelo.addRow(fila);
        }
    }

    //Reversar la factura actual
    private void reversarFactura() {
        // Validar que hay una factura cargada
        if (facturaActual == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero debes buscar una factura",
                    "No hay factura",
                    JOptionPane.WARNING_MESSAGE);
            jTextField_numero_factura.requestFocus();
            return;
        }

        // Validar motivo
        String motivo = textArea1.getText().trim();
        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debes ingresar el motivo de la reversión",
                    "Motivo requerido",
                    JOptionPane.WARNING_MESSAGE);
            textArea1.requestFocus();
            return;
        }

        if (motivo.length() < 10) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ El motivo debe tener al menos 10 caracteres",
                    "Motivo muy corto",
                    JOptionPane.WARNING_MESSAGE);
            textArea1.requestFocus();
            return;
        }

        // Confirmar reversión
        String numeroFactura = (String) facturaActual.get("numero_factura");
        double total = ((Double) facturaActual.get("total")).doubleValue();

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿ESTÁS SEGURO de reversar esta factura?\n\n"
                + "Factura: " + numeroFactura + "\n"
                + "Total: $" + String.format("%.2f", total) + "\n"
                + "Cliente: " + facturaActual.get("nombre_cliente") + "\n\n"
                + "ESTA ACCIÓN NO SE PUEDE DESHACER\n"
                + "• Se generará una Nota Crédito\n"
                + "• Se devolverá el stock al inventario\n"
                + "• La factura quedará anulada\n\n"
                + "Motivo: " + motivo,
                "⚠️ CONFIRMAR REVERSIÓN",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Ejecutar reversión
        int facturaId = ((Integer) facturaActual.get("id")).intValue();
        int resultado = controlador.reversarFactura(facturaId, motivo, usuarioActual);

        if (resultado > 0) {

            // REGISTRAR EN AUDITORÍA
            try {
                Auditoria auditoria = new Auditoria();
                auditoria.registrar(
                        usuarioActual,
                        "ANULAR",
                        "Reversiones",
                        "Reversó factura " + numeroFactura + " - Total: $" + String.format("%.2f", total)
                        + " - Motivo: " + motivo
                );
                System.out.println("Reversión registrada en auditoría: " + numeroFactura);
            } catch (Exception e) {
                System.err.println("Error al registrar reversión: " + e.getMessage());
            }
            cargarHistorial();
            limpiarFormulario();

            JOptionPane.showMessageDialog(this,
                    "REVERSIÓN EXITOSA\n\n"
                    + "La factura ha sido reversada correctamente.\n"
                    + "Revisa el historial para ver la Nota Crédito generada.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        // El controlador ya muestra mensajes de error
    }

    //Limpiar todos los campos del formulario
    private void limpiarFormulario() {
        // Limpiar búsqueda
        jTextField_numero_factura.setText("");

        // Limpiar datos de factura
        jTextField1.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField8.setText("");

        // Limpiar motivo
        textArea1.setText("");

        // Limpiar tabla de productos
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);

        // Limpiar factura actual
        facturaActual = null;

        // Deshabilitar campos
        deshabilitarCampos();

        // Focus en búsqueda
        jTextField_numero_factura.requestFocus();
    }

    //Cargar historial de reversiones
    private void cargarHistorial() {
        DefaultTableModel modelo = (DefaultTableModel) jTable2.getModel();
        modelo.setRowCount(0); // Limpiar tabla

        java.util.List<java.util.Map<String, Object>> historial
                = controlador.obtenerHistorialReversiones(20);

        for (java.util.Map<String, Object> item : historial) {
            Object[] fila = new Object[7];

            fila[0] = item.get("numero_devolucion");
            fila[1] = item.get("numero_factura_original");
            fila[2] = item.get("nombre_cliente");

            double totalDev = ((Double) item.get("total_devuelto")).doubleValue();
            fila[3] = String.format("$%.2f", totalDev);

            java.sql.Timestamp fecha = (java.sql.Timestamp) item.get("fecha_devolucion");
            fila[4] = formatoFecha.format(fecha);

            fila[5] = item.get("usuario_creacion");

            String motivo = (String) item.get("motivo_devolucion");
            // Truncar motivo si es muy largo
            if (motivo != null && motivo.length() > 40) {
                motivo = motivo.substring(0, 37) + "...";
            }
            fila[6] = motivo;

            modelo.addRow(fila);
        }
    }

    //Habilitar campos para reversión
    private void habilitarCampos() {
        textArea1.setEnabled(true);
        jButton2.setEnabled(true);
    }

    // Deshabilitar campos
    private void deshabilitarCampos() {
        textArea1.setEnabled(false);
        jButton2.setEnabled(false);

        // Los campos de información siempre disabled (solo lectura)
        jTextField1.setEnabled(false);
        jTextField4.setEnabled(false);
        jTextField5.setEnabled(false);
        jTextField6.setEnabled(false);
        jTextField7.setEnabled(false);
        jTextField8.setEnabled(false);
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
        jPanel_datos_reversion = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_numero_factura = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jPanel_historia = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea1 = new java.awt.TextArea();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_datos_reversion.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_datos_reversion.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Reversar Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 1, 18))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel3.setText("Numero de Factura:");

        jTextField_numero_factura.setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/busqueda.png"))); // NOI18N
        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel4.setText("Información de Factura");

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel2.setText("Número:");

        jLabel5.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel5.setText("Cliente:");

        jLabel6.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel6.setText("Total:");

        jLabel7.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel7.setText("Fecha:");

        jLabel8.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel8.setText("Documento:");

        jLabel9.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel9.setText("Estado:");

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));

        jTextField4.setBackground(new java.awt.Color(255, 255, 255));

        jTextField5.setBackground(new java.awt.Color(255, 255, 255));

        jTextField6.setBackground(new java.awt.Color(255, 255, 255));

        jTextField7.setBackground(new java.awt.Color(255, 255, 255));

        jTextField8.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel_datos_reversionLayout = new javax.swing.GroupLayout(jPanel_datos_reversion);
        jPanel_datos_reversion.setLayout(jPanel_datos_reversionLayout);
        jPanel_datos_reversionLayout.setHorizontalGroup(
            jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE))
                            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                                .addGap(24, 24, 24)
                                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                                    .addComponent(jTextField8))))
                        .addGap(103, 103, 103)
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_datos_reversionLayout.createSequentialGroup()
                                    .addComponent(jLabel8)
                                    .addGap(18, 18, 18)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(60, 60, 60)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_numero_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(55, 55, 55)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel_datos_reversionLayout.setVerticalGroup(
            jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_numero_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addComponent(jButton1))
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel_historia.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_historia.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Historial de Reversiones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 1, 18))); // NOI18N

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane4.setPreferredSize(new java.awt.Dimension(0, 120));

        jTable2.setBackground(new java.awt.Color(255, 255, 255));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "NC", "Factura", "Cliente", "Total", "Fecha", "Usuario", "Motivo"
            }
        ));
        jTable2.setPreferredSize(new java.awt.Dimension(0, 120));
        jScrollPane4.setViewportView(jTable2);

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/update.png"))); // NOI18N
        jButton5.setText("Actualizar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_historiaLayout = new javax.swing.GroupLayout(jPanel_historia);
        jPanel_historia.setLayout(jPanel_historiaLayout);
        jPanel_historiaLayout.setHorizontalGroup(
            jPanel_historiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_historiaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel_historiaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jButton5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_historiaLayout.setVerticalGroup(
            jPanel_historiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_historiaLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jButton5)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Reversión de Facturas");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Productos de la Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 1, 18))); // NOI18N
        jPanel2.setToolTipText("");
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel2.setName(""); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Código", "Producto", "Cantidad", "Precio", "Total"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel10.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel10.setText("Motivo de Reversión");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setViewportView(textArea1);

        jButton2.setBackground(new java.awt.Color(255, 0, 0));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/reversar.png"))); // NOI18N
        jButton2.setText("REVERSAR FACTURA");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/eliminar.png"))); // NOI18N
        jButton3.setText("Limpiar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(128, 128, 128)
                .addComponent(jButton2)
                .addGap(274, 274, 274)
                .addComponent(jButton3)
                .addGap(149, 149, 149))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel_datos_reversion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel_historia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(278, 278, 278)
                        .addComponent(jLabel1)))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_datos_reversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_historia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        buscarFactura();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        reversarFactura();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        cargarHistorial();
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(FrmReversiones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmReversiones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmReversiones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmReversiones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmReversiones().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_datos_reversion;
    private javax.swing.JPanel jPanel_historia;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField_numero_factura;
    private java.awt.TextArea textArea1;
    // End of variables declaration//GEN-END:variables
}
