/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.Auditoria;
import controlador.RegistrarDevolucionCompras;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Usuario_Sesion;

/**
 *
 * @author piper
 */
public class FrmReversionesCompras extends javax.swing.JInternalFrame {

    // Controlador
    private RegistrarDevolucionCompras controlador;

    // Compra actual cargada
    private Map<String, Object> compraActual;

    // Lista de productos de la compra
    private List<Map<String, Object>> productosCompra;

    // Usuario actual del sistema
    private String usuarioActual;

    // Formateador de fechas
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    public FrmReversionesCompras() {
        
        super("Ventas", true, true, true, true); // Título, cerrable, redimensionable, movible, maximizable

        this.usuarioActual = Usuario_Sesion.getInstancia().getNombreUsuario();
        this.controlador = new RegistrarDevolucionCompras();
        this.productosCompra = new ArrayList<>();

        initComponents();
        configurarTabla();
        deshabilitarCampos();
        cargarHistorial();

        // Centrar el formulario
        this.setSize(900, 750);

        // Registrar acceso al módulo
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(
                    usuarioActual,
                    "Devolución Compras",
                    "Accedió al módulo de Devolución de Compras"
            );
        } catch (Exception e) {
            System.err.println("Error al registrar acceso: " + e.getMessage());
        }
    }

    /**
     * Configura la tabla de productos (solo lectura)
     */
    private void configurarTabla() {
        jTable_productos.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Producto", "Cantidad", "Stock Actual", "Precio", "Total"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ninguna columna es editable
            }
        });
    }

    /**
     * Busca una compra por número de factura
     */
    private void buscarCompra() {
        String busqueda = jTextField_numero_factura.getText().trim();

        if (busqueda.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresa un número de factura",
                    "Campo vacío",
                    JOptionPane.WARNING_MESSAGE);
            jTextField_numero_factura.requestFocus();
            return;
        }

        // Buscar la compra
        compraActual = controlador.buscarCompra(busqueda);

        if (compraActual == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró la compra: " + busqueda,
                    "Compra no encontrada",
                    JOptionPane.ERROR_MESSAGE);
            limpiarFormulario();
            return;
        }

        // Validar si puede ser devuelta
        int compraId = (Integer) compraActual.get("id");
        String errorValidacion = controlador.validarCompraParaDevolucion(compraId);

        if (errorValidacion != null) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ " + errorValidacion,
                    "No se puede devolver",
                    JOptionPane.WARNING_MESSAGE);
            limpiarFormulario();
            return;
        }

        // Cargar datos en la interfaz
        cargarDatosCompra();
        cargarProductos(compraId);

        // Habilitar campos
        habilitarCampos();

        JOptionPane.showMessageDialog(this,
                "Compra encontrada y lista para devolver.\nIngresa el motivo y confirma la devolución.",
                "Compra cargada",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Carga los datos de la compra en los campos
     */
    private void cargarDatosCompra() {
        try {
            jTextField_numero_factura_compra.setText((String) compraActual.get("numero_factura"));
            jTextField_nombre_proveedor.setText((String) compraActual.get("proveedor_nombre"));

            // Cargar NIT del proveedor
            String nit = (String) compraActual.get("numero_documento_proveedor");
            jTextField_numero_documento_proveedor.setText(nit != null ? nit : "");

            double total = (Double) compraActual.get("total");
            jTextField_total_compra.setText(String.format("$%,.0f", total));

            java.sql.Timestamp fecha = (java.sql.Timestamp) compraActual.get("fecha_compra");
            if (fecha != null) {
                jTextField_fecha_factura.setText(formatoFecha.format(fecha));
            }

            jTextField6.setText((String) compraActual.get("estado"));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    // Carga los productos de la compra en la tabla
    private void cargarProductos(int compraId) {
        DefaultTableModel modelo = (DefaultTableModel) jTable_productos.getModel();
        modelo.setRowCount(0);

        productosCompra = controlador.obtenerProductosCompra(compraId);

        for (Map<String, Object> producto : productosCompra) {
            Object[] fila = new Object[6];
            fila[0] = producto.get("codigo");
            fila[1] = producto.get("nombre");
            fila[2] = producto.get("cantidad_comprada");
            fila[3] = producto.get("stock_actual");

            double precio = (Double) producto.get("precio_compra");
            fila[4] = String.format("$%,.0f", precio);

            double subtotal = (Double) producto.get("subtotal");
            fila[5] = String.format("$%,.0f", subtotal);

            modelo.addRow(fila);
        }
    }

    //Procesa la devolución TOTAL de la compra
    private void procesarDevolucion() {
        // Validar que hay una compra cargada
        if (compraActual == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero debes buscar una compra",
                    "No hay compra",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar motivo
        String motivo = textArea_motivo.getText().trim();
        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debes ingresar el motivo de la devolución",
                    "Motivo requerido",
                    JOptionPane.WARNING_MESSAGE);
            textArea_motivo.requestFocus();
            return;
        }

        if (motivo.length() < 10) {
            JOptionPane.showMessageDialog(this,
                    "El motivo debe tener al menos 10 caracteres",
                    "Motivo muy corto",
                    JOptionPane.WARNING_MESSAGE);
            textArea_motivo.requestFocus();
            return;
        }

        // Validar que hay productos
        if (productosCompra.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay productos para devolver",
                    "Sin productos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar stock suficiente para todos los productos
        double totalDevolucion = 0;
        StringBuilder detalleProductos = new StringBuilder();

        for (Map<String, Object> producto : productosCompra) {
            int cantidadComprada = (Integer) producto.get("cantidad_comprada");
            int stockActual = (Integer) producto.get("stock_actual");
            String nombre = (String) producto.get("nombre");
            double precio = (Double) producto.get("precio_compra");

            if (cantidadComprada > stockActual) {
                JOptionPane.showMessageDialog(this,
                        "No hay suficiente stock para devolver.\n"
                        + "Producto: " + nombre + "\n"
                        + "Stock actual: " + stockActual + "\n"
                        + "Cantidad a devolver: " + cantidadComprada,
                        "Stock insuficiente",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            totalDevolucion += cantidadComprada * precio;
            detalleProductos.append("• ")
                    .append(nombre)
                    .append(" x ")
                    .append(cantidadComprada)
                    .append("\n");
        }

        // Confirmar devolución
        String numeroFactura = (String) compraActual.get("numero_factura");
        String proveedor = (String) compraActual.get("proveedor_nombre");

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Confirmas la DEVOLUCIÓN TOTAL de esta compra?\n\n"
                + "Factura: " + numeroFactura + "\n"
                + "Proveedor: " + proveedor + "\n"
                + "Total a devolver: $" + String.format("%,.0f", totalDevolucion) + "\n\n"
                + "Productos:\n" + detalleProductos.toString() + "\n"
                + "El stock se RESTARÁ del inventario\n\n"
                + "Motivo: " + motivo,
                "Confirmar Devolución Total",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Ejecutar devolución
        int compraId = (Integer) compraActual.get("id");
        int resultado = controlador.registrarDevolucionCompra(compraId, motivo, usuarioActual);

        if (resultado > 0) {
            // Registrar en auditoría
            try {
                Auditoria auditoria = new Auditoria();
                auditoria.registrar(
                        usuarioActual,
                        "DEVOLUCION",
                        "Compras",
                        "Devolución total de compra - Factura: " + numeroFactura
                        + " | Proveedor: " + proveedor
                        + " | Total devuelto: $" + String.format("%,.0f", totalDevolucion)
                        + " | Motivo: " + motivo
                );
            } catch (Exception e) {
                System.err.println("Error al registrar auditoría: " + e.getMessage());
            }

            cargarHistorial();
            limpiarFormulario();

            JOptionPane.showMessageDialog(this,
                    "DEVOLUCIÓN REGISTRADA EXITOSAMENTE\n\n"
                    + "El stock ha sido actualizado.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //Limpia el formulario
    private void limpiarFormulario() {
        jTextField_numero_factura.setText("");
        jTextField_numero_factura_compra.setText("");
        jTextField_nombre_proveedor.setText("");
        jTextField_numero_documento_proveedor.setText("");
        jTextField_total_compra.setText("");
        jTextField_fecha_factura.setText("");
        jTextField6.setText("");
        textArea_motivo.setText("");

        DefaultTableModel modelo = (DefaultTableModel) jTable_productos.getModel();
        modelo.setRowCount(0);

        compraActual = null;
        productosCompra.clear();

        deshabilitarCampos();
        jTextField_numero_factura.requestFocus();
    }

    //Carga el historial de devoluciones
    private void cargarHistorial() {
        DefaultTableModel modelo = (DefaultTableModel) jTable_historial.getModel();
        modelo.setRowCount(0);

        List<Map<String, Object>> historial = controlador.obtenerHistorialDevoluciones(20);

        for (Map<String, Object> item : historial) {
            Object[] fila = new Object[7];
            fila[0] = item.get("numero_devolucion");
            fila[1] = item.get("numero_factura_original");
            fila[2] = item.get("proveedor_nombre");

            double total = (Double) item.get("total_devuelto");
            fila[3] = String.format("$%,.0f", total);

            java.sql.Timestamp fecha = (java.sql.Timestamp) item.get("fecha_devolucion");
            fila[4] = formatoFecha.format(fecha);

            fila[5] = item.get("usuario_creacion");

            String motivo = (String) item.get("motivo_devolucion");
            if (motivo != null && motivo.length() > 30) {
                motivo = motivo.substring(0, 27) + "...";
            }
            fila[6] = motivo;

            modelo.addRow(fila);
        }
    }

    private void habilitarCampos() {
        textArea_motivo.setEnabled(true);
        jButton_devolver.setEnabled(true);
    }

    private void deshabilitarCampos() {
        textArea_motivo.setEnabled(false);
        jButton_devolver.setEnabled(false);

        jTextField_numero_factura_compra.setEnabled(false);
        jTextField_nombre_proveedor.setEnabled(false);
        jTextField_numero_documento_proveedor.setEnabled(false);
        jTextField_total_compra.setEnabled(false);
        jTextField_fecha_factura.setEnabled(false);
        jTextField6.setEnabled(false);
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
        jButton_buscar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField_numero_factura_compra = new javax.swing.JTextField();
        jTextField_fecha_factura = new javax.swing.JTextField();
        jTextField_numero_documento_proveedor = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField_nombre_proveedor = new javax.swing.JTextField();
        jTextField_total_compra = new javax.swing.JTextField();
        jPanel_historia = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable_historial = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_productos = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea_motivo = new java.awt.TextArea();
        jButton_devolver = new javax.swing.JButton();
        jButton_limpiar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_datos_reversion.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_datos_reversion.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Reversar Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 1, 18))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel3.setText("N° Factura Compra:");

        jTextField_numero_factura.setBackground(new java.awt.Color(255, 255, 255));

        jButton_buscar.setBackground(new java.awt.Color(255, 255, 255));
        jButton_buscar.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jButton_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/busqueda.png"))); // NOI18N
        jButton_buscar.setText("Buscar");
        jButton_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_buscarActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel4.setText("Datos de la compra ");

        jLabel2.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel2.setText("Número:");

        jLabel5.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel5.setText("Proveedor:");

        jLabel6.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel6.setText("Total:");

        jLabel7.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel7.setText("Fecha:");

        jLabel8.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel8.setText("Documento:");

        jLabel9.setFont(new java.awt.Font("Lucida Sans", 1, 14)); // NOI18N
        jLabel9.setText("Estado:");

        jTextField_numero_factura_compra.setBackground(new java.awt.Color(255, 255, 255));

        jTextField_fecha_factura.setBackground(new java.awt.Color(255, 255, 255));

        jTextField_numero_documento_proveedor.setBackground(new java.awt.Color(255, 255, 255));

        jTextField6.setBackground(new java.awt.Color(255, 255, 255));

        jTextField_nombre_proveedor.setBackground(new java.awt.Color(255, 255, 255));

        jTextField_total_compra.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel_datos_reversionLayout = new javax.swing.GroupLayout(jPanel_datos_reversion);
        jPanel_datos_reversion.setLayout(jPanel_datos_reversionLayout);
        jPanel_datos_reversionLayout.setHorizontalGroup(
            jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                                .addComponent(jTextField_total_compra, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jTextField_nombre_proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_numero_factura_compra))
                        .addGap(103, 103, 103)
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel9)
                                .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addGap(60, 60, 60)
                                    .addComponent(jTextField_fecha_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                                    .addComponent(jTextField_numero_documento_proveedor))
                                .addGap(1, 1, 1))))
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField_numero_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_buscar)
                        .addGap(40, 40, 40)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel_datos_reversionLayout.setVerticalGroup(
            jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField_numero_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addComponent(jLabel4))
                    .addComponent(jButton_buscar))
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField_fecha_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_datos_reversionLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField_numero_factura_compra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(jTextField_numero_documento_proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_nombre_proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel_datos_reversionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_total_compra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel_historia.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_historia.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Historial de Devoluciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 1, 18))); // NOI18N

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane4.setPreferredSize(new java.awt.Dimension(0, 120));

        jTable_historial.setBackground(new java.awt.Color(255, 255, 255));
        jTable_historial.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable_historial.setPreferredSize(new java.awt.Dimension(0, 120));
        jScrollPane4.setViewportView(jTable_historial);

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
        jLabel1.setText("Reversión de Compras");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Productos de la Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans", 1, 18))); // NOI18N
        jPanel2.setToolTipText("");
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel2.setName(""); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTable_productos.setBackground(new java.awt.Color(255, 255, 255));
        jTable_productos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable_productos);

        jLabel10.setFont(new java.awt.Font("Lucida Sans", 1, 18)); // NOI18N
        jLabel10.setText("Motivo de Devolución");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setViewportView(textArea_motivo);

        jButton_devolver.setBackground(new java.awt.Color(255, 0, 0));
        jButton_devolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/reversar.png"))); // NOI18N
        jButton_devolver.setText("REGISTRAR DEVOLUCIÓN");
        jButton_devolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_devolverActionPerformed(evt);
            }
        });

        jButton_limpiar.setBackground(new java.awt.Color(255, 255, 255));
        jButton_limpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/eliminar.png"))); // NOI18N
        jButton_limpiar.setText("Limpiar");
        jButton_limpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_limpiarActionPerformed(evt);
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
                .addComponent(jButton_devolver)
                .addGap(274, 274, 274)
                .addComponent(jButton_limpiar)
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
                    .addComponent(jButton_devolver)
                    .addComponent(jButton_limpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jPanel_historia.getAccessibleContext().setAccessibleName("Historial de Devoluciones");
        jPanel2.getAccessibleContext().setAccessibleName("Productos de la Compra");

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

    private void jButton_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_buscarActionPerformed
        // TODO add your handling code here:
        buscarCompra();
    }//GEN-LAST:event_jButton_buscarActionPerformed

    private void jButton_devolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_devolverActionPerformed
        // TODO add your handling code here:
        procesarDevolucion();
    }//GEN-LAST:event_jButton_devolverActionPerformed

    private void jButton_limpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_limpiarActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_jButton_limpiarActionPerformed

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
            java.util.logging.Logger.getLogger(FrmReversionesCompras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmReversionesCompras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmReversionesCompras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmReversionesCompras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new FrmReversionesCompras().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton_buscar;
    private javax.swing.JButton jButton_devolver;
    private javax.swing.JButton jButton_limpiar;
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
    private javax.swing.JTable jTable_historial;
    private javax.swing.JTable jTable_productos;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField_fecha_factura;
    private javax.swing.JTextField jTextField_nombre_proveedor;
    private javax.swing.JTextField jTextField_numero_documento_proveedor;
    private javax.swing.JTextField jTextField_numero_factura;
    private javax.swing.JTextField jTextField_numero_factura_compra;
    private javax.swing.JTextField jTextField_total_compra;
    private java.awt.TextArea textArea_motivo;
    // End of variables declaration//GEN-END:variables
}
