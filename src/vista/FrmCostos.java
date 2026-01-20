/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.Auditoria;
import controlador.AnalisisCostos;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import modelo.Usuario_Sesion;

/**
 *
 * @author piper
 */
public class FrmCostos extends javax.swing.JInternalFrame {

    private AnalisisCostos controlador;
    private String usuarioActual;
    private List<Map<String, Object>> datosGrafico;

    /**
     * Creates new form Costos
     */
    public FrmCostos() {
        
        super("Analisis Costos", true, true, true, true); // Título, cerrable, redimensionable, movible, maximizable
        this.usuarioActual = Usuario_Sesion.getInstancia().getNombreUsuario();
        this.controlador = new AnalisisCostos();

        initComponents();
        configurarColoresCards();
        configurarCards();
        configurarTabla();
        configurarPanelGrafico();
        cargarDashboard();

        // Registrar acceso en auditoría
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.registrarConsulta(usuarioActual, "Analisis de Costos",
                    "Accedió al Analisis de Costos vs Precios");
        } catch (Exception e) {
            System.err.println("Error al registrar acceso: " + e.getMessage());
        }
    }

    private void configurarColoresCards() {
        Color blanco = Color.WHITE;
        Color gris = new Color(200, 200, 200);

        // Card 1
        jLabel_titulo1.setForeground(blanco);
        lblValor1.setForeground(blanco);
        jLabel_sub1.setForeground(gris);

        // Card 2
        jLabel_titulo2.setForeground(blanco);
        lblValor2.setForeground(blanco);
        jLabel_sub2.setForeground(gris);

        // Card 3
        jLabel_titulo3.setForeground(blanco);
        lblValor3.setForeground(blanco);
        jLabel_sub3.setForeground(gris);

        // Card 4
        jLabel_titulo4.setForeground(blanco);
        lblValor4.setForeground(blanco);
        jLabel_sub4.setForeground(gris);

        // Card 5
        jLabel_titulo5.setForeground(blanco);
        lblValor5.setForeground(blanco);
        jLabel_sub5.setForeground(gris);

        // Card 6
        jLabel_titulo6.setForeground(blanco);
        lblValor6.setForeground(blanco);
        jLabel_sub6.setForeground(gris);

        // Card 7
        jLabel_titulo7.setForeground(blanco);
        lblValor7.setForeground(blanco);
        jLabel_sub7.setForeground(gris);

        // Card 8
        jLabel_titulo8.setForeground(blanco);
        lblValor8.setForeground(blanco);
        jLabel_sub8.setForeground(gris);
    }

    private void configurarCards() {
    // Tamaño para cards de arriba (más grandes)
    java.awt.Dimension tamanoCardGrande = new java.awt.Dimension(145, 95);
    
    // Tamaño para cards de abajo (rectangulares, más pequeñas)
    java.awt.Dimension tamanoCardPequena = new java.awt.Dimension(145, 75);

    // Cards de arriba - tamaño grande
    aplicarEstiloCard(jPanel_card1, new Color(41, 128, 185), tamanoCardGrande);
    aplicarEstiloCard(jPanel_card2, new Color(142, 68, 173), tamanoCardGrande);
    aplicarEstiloCard(jPanel_card3, new Color(39, 174, 96), tamanoCardGrande);
    aplicarEstiloCard(jPanel_card4, new Color(230, 126, 34), tamanoCardGrande);
    
    // Cards de abajo - tamaño rectangular pequeño
    aplicarEstiloCard(jPanel_card5, new Color(52, 73, 94), tamanoCardPequena);
    aplicarEstiloCard(jPanel_card6, new Color(22, 160, 133), tamanoCardPequena);
    aplicarEstiloCard(jPanel_card7, new Color(231, 76, 60), tamanoCardPequena);
    aplicarEstiloCard(jPanel_card8, new Color(46, 204, 113), tamanoCardPequena);
    
    // Ajustar el layout de las cards pequeñas
    ajustarLayoutCardPequena(jPanel_card5, jLabel_titulo5, lblValor5, jLabel_sub5);
    ajustarLayoutCardPequena(jPanel_card6, jLabel_titulo6, lblValor6, jLabel_sub6);
    ajustarLayoutCardPequena(jPanel_card7, jLabel_titulo7, lblValor7, jLabel_sub7);
    ajustarLayoutCardPequena(jPanel_card8, jLabel_titulo8, lblValor8, jLabel_sub8);
}

    private void aplicarEstiloCard(javax.swing.JPanel panel, Color colorFondo, java.awt.Dimension tamano) {
    panel.setPreferredSize(tamano);
    panel.setMinimumSize(tamano);
    panel.setMaximumSize(tamano);
    panel.setOpaque(false);
    
    panel.setBorder(new javax.swing.border.AbstractBorder() {
        private final int radio = 20;
        
        @Override
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(colorFondo);
            g2d.fillRoundRect(x, y, width, height, radio, radio);
            g2d.dispose();
        }
        
        @Override
        public java.awt.Insets getBorderInsets(java.awt.Component c) {
            return new java.awt.Insets(0, 0, 0, 0);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    });
}
    private void ajustarLayoutCardPequena(javax.swing.JPanel panel, javax.swing.JLabel titulo, 
                                       javax.swing.JLabel valor, javax.swing.JLabel subtitulo) {
    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
    panel.setLayout(layout);
    
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(titulo)
                .addComponent(subtitulo)
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(valor)))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addComponent(titulo)
            .addGap(3, 3, 3)
            .addComponent(valor)
            .addGap(2, 2, 2)
            .addComponent(subtitulo)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
}
    
       private void configurarPanelGrafico() {
        javax.swing.JPanel nuevoPanel = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGrafico(g);
            }
        };
        nuevoPanel.setBackground(Color.WHITE);
        nuevoPanel.setPreferredSize(new java.awt.Dimension(600, 150));

        jPanel_grafico.remove(panelGrafico);
        jPanel_grafico.setLayout(new java.awt.BorderLayout());
        jPanel_grafico.add(jLabel_tituloGrafico, java.awt.BorderLayout.NORTH);
        jPanel_grafico.add(nuevoPanel, java.awt.BorderLayout.CENTER);
        panelGrafico = nuevoPanel;
        jPanel_grafico.revalidate();
        jPanel_grafico.repaint();
    }

    //Configura la tabla de productos
    private void configurarTabla() {
        jTable_productos.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Producto", "Costo Compra", "Precio Venta", "Ganancia", "Margen %", "Stock"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        jTable_productos.setRowHeight(25);
        jTable_productos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    //Carga todos los datos del dashboard
    private void cargarDashboard() {
        cargarMetricas();
        cargarTablaProductos();
        cargarGrafico();

        // Actualizar fecha/hora
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        jLabel_actualizacion.setText("Actualizado: " + sdf.format(new Date()));
    }

    //Carga las métricas en las cards
    private void cargarMetricas() {
        Map<String, Object> metricas = controlador.obtenerMetricasGenerales();

        // Card 1 - Total productos
        lblValor1.setText(String.valueOf((Integer) metricas.get("total_productos")));

        // Card 2 - Con costo registrado
        lblValor2.setText(String.valueOf((Integer) metricas.get("productos_con_costo")));

        // Card 3 - Margen promedio
        lblValor3.setText(String.format("%.1f%%", (Double) metricas.get("margen_promedio")));

        // Card 4 - Ganancia potencial
        lblValor4.setText(String.format("$%,.0f", (Double) metricas.get("ganancia_potencial")));

        // Card 5 - Costo inventario
        lblValor5.setText(String.format("$%,.0f", (Double) metricas.get("costo_inventario")));

        // Card 6 - Valor inventario
        lblValor6.setText(String.format("$%,.0f", (Double) metricas.get("valor_inventario")));

        // Card 7 - Margen bajo
        lblValor7.setText(String.valueOf((Integer) metricas.get("productos_margen_bajo")));

        // Card 8 - Margen alto
        lblValor8.setText(String.valueOf((Integer) metricas.get("productos_margen_alto")));
    }

    //Carga los productos en la tabla
    private void cargarTablaProductos() {
        DefaultTableModel modelo = (DefaultTableModel) jTable_productos.getModel();
        modelo.setRowCount(0);

        List<Map<String, Object>> productos = controlador.obtenerProductosConMargenes();

        for (Map<String, Object> producto : productos) {
            double costoCompra = (Double) producto.get("costo_compra");
            double precioVenta = (Double) producto.get("precio_venta");
            double ganancia = (Double) producto.get("ganancia");
            double margen = (Double) producto.get("margen");
            int stock = (Integer) producto.get("stock");

            Object[] fila = new Object[7];
            fila[0] = producto.get("codigo");
            fila[1] = producto.get("nombre");
            fila[2] = costoCompra > 0 ? String.format("$%,.0f", costoCompra) : "Sin registro";
            fila[3] = String.format("$%,.0f", precioVenta);
            fila[4] = costoCompra > 0 ? String.format("$%,.0f", ganancia) : "-";
            fila[5] = costoCompra > 0 ? String.format("%.1f%%", margen) : "-";
            fila[6] = stock;

            modelo.addRow(fila);
        }
    }

    //Carga los datos del gráfico
    private void cargarGrafico() {
        datosGrafico = controlador.obtenerTopProductosPorMargen(8, true);

        if (datosGrafico != null) {
            for (Map<String, Object> d : datosGrafico) {
            }
        }

        panelGrafico.repaint();
    }

    //Dibuja el gráfico de barras
    private void dibujarGrafico(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = panelGrafico.getWidth();
        int alto = panelGrafico.getHeight();

        // Fondo blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, ancho, alto);

        // Si no hay datos
        if (datosGrafico == null || datosGrafico.isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2d.drawString("No hay datos con costo registrado", ancho / 2 - 100, alto / 2);
            return;
        }

        // Márgenes del gráfico
        int margenIzq = 50;
        int margenDer = 20;
        int margenTop = 20;
        int margenBot = 50;

        int anchoGrafico = ancho - margenIzq - margenDer;
        int altoGrafico = alto - margenTop - margenBot;

        int numBarras = datosGrafico.size();
        int espacio = 8;
        int anchoBarra = (anchoGrafico - (espacio * (numBarras + 1))) / numBarras;
        if (anchoBarra > 50) {
            anchoBarra = 50;
        }

        // Encontrar el margen máximo
        double maxMargen = 0;
        for (Map<String, Object> dato : datosGrafico) {
            double margen = (Double) dato.get("margen");
            if (margen > maxMargen) {
                maxMargen = margen;
            }
        }
        if (maxMargen == 0) {
            maxMargen = 100;
        }

        // Dibujar líneas de referencia horizontales
        g2d.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= 4; i++) {
            int y = margenTop + (altoGrafico * i / 4);
            g2d.drawLine(margenIzq, y, ancho - margenDer, y);

            // Etiquetas del eje Y
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            double valor = maxMargen * (4 - i) / 4;
            g2d.drawString(String.format("%.0f%%", valor), 5, y + 4);
            g2d.setColor(new Color(230, 230, 230));
        }

        // Colores para las barras
        Color[] colores = {
            new Color(39, 174, 96), // Verde
            new Color(41, 128, 185), // Azul
            new Color(142, 68, 173), // Morado
            new Color(230, 126, 34), // Naranja
            new Color(52, 152, 219), // Azul claro
            new Color(46, 204, 113), // Verde claro
            new Color(155, 89, 182), // Morado claro
            new Color(241, 196, 15) // Amarillo
        };

        // Dibujar cada barra
        int x = margenIzq + espacio;
        for (int i = 0; i < datosGrafico.size(); i++) {
            Map<String, Object> dato = datosGrafico.get(i);
            double margen = (Double) dato.get("margen");
            String nombre = (String) dato.get("nombre");

            // Calcular altura de la barra
            int altoBarra = (int) ((margen / maxMargen) * altoGrafico);
            int yBarra = margenTop + altoGrafico - altoBarra;

            // Dibujar barra
            g2d.setColor(colores[i % colores.length]);
            g2d.fillRect(x, yBarra, anchoBarra, altoBarra);

            // Borde de la barra
            g2d.setColor(colores[i % colores.length].darker());
            g2d.drawRect(x, yBarra, anchoBarra, altoBarra);

            // Valor encima de la barra
            g2d.setColor(new Color(44, 62, 80));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 9));
            String valorTexto = String.format("%.1f%%", margen);
            int anchoTexto = g2d.getFontMetrics().stringWidth(valorTexto);
            g2d.drawString(valorTexto, x + (anchoBarra - anchoTexto) / 2, yBarra - 3);

            // Nombre del producto (rotado 45 grados)
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            String nombreCorto = nombre.length() > 12 ? nombre.substring(0, 10) + ".." : nombre;

            java.awt.geom.AffineTransform original = g2d.getTransform();
            g2d.rotate(Math.toRadians(-45), x + anchoBarra / 2, margenTop + altoGrafico + 8);
            g2d.drawString(nombreCorto, x + anchoBarra / 2, margenTop + altoGrafico + 12);
            g2d.setTransform(original);

            x += anchoBarra + espacio;
        }

        // Dibujar ejes
        g2d.setColor(new Color(44, 62, 80));
        g2d.drawLine(margenIzq, margenTop, margenIzq, margenTop + altoGrafico); // Eje Y
        g2d.drawLine(margenIzq, margenTop + altoGrafico, ancho - margenDer, margenTop + altoGrafico); // Eje X
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_principal = new javax.swing.JPanel();
        jLabel_titulo = new javax.swing.JLabel();
        jLabel_actualizacion = new javax.swing.JLabel();
        jButton_actualizar = new javax.swing.JButton();
        jPanel_card1 = new javax.swing.JPanel();
        jLabel_titulo1 = new javax.swing.JLabel();
        lblValor1 = new javax.swing.JLabel();
        jLabel_sub1 = new javax.swing.JLabel();
        jPanel_card2 = new javax.swing.JPanel();
        jLabel_titulo2 = new javax.swing.JLabel();
        lblValor2 = new javax.swing.JLabel();
        jLabel_sub2 = new javax.swing.JLabel();
        jPanel_card3 = new javax.swing.JPanel();
        jLabel_titulo3 = new javax.swing.JLabel();
        lblValor3 = new javax.swing.JLabel();
        jLabel_sub3 = new javax.swing.JLabel();
        jPanel_card4 = new javax.swing.JPanel();
        jLabel_titulo4 = new javax.swing.JLabel();
        lblValor4 = new javax.swing.JLabel();
        jLabel_sub4 = new javax.swing.JLabel();
        jPanel_card5 = new javax.swing.JPanel();
        jLabel_titulo5 = new javax.swing.JLabel();
        lblValor5 = new javax.swing.JLabel();
        jLabel_sub5 = new javax.swing.JLabel();
        jPanel_card6 = new javax.swing.JPanel();
        jLabel_titulo6 = new javax.swing.JLabel();
        lblValor6 = new javax.swing.JLabel();
        jLabel_sub6 = new javax.swing.JLabel();
        jPanel_card7 = new javax.swing.JPanel();
        jLabel_titulo7 = new javax.swing.JLabel();
        lblValor7 = new javax.swing.JLabel();
        jLabel_sub7 = new javax.swing.JLabel();
        jPanel_card8 = new javax.swing.JPanel();
        jLabel_titulo8 = new javax.swing.JLabel();
        lblValor8 = new javax.swing.JLabel();
        jLabel_sub8 = new javax.swing.JLabel();
        jPanel_grafico = new javax.swing.JPanel();
        jLabel_tituloGrafico = new javax.swing.JLabel();
        panelGrafico = new javax.swing.JPanel();
        jPanel_tabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_productos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel_principal.setBackground(new java.awt.Color(236, 240, 241));

        jLabel_titulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel_titulo.setText("Análisis de Márgenes de Ganancia");

        jLabel_actualizacion.setText("Actualizado:");

        jButton_actualizar.setText("Actualizar");
        jButton_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarActionPerformed(evt);
            }
        });

        jPanel_card1.setBackground(new java.awt.Color(41, 128, 185));

        jLabel_titulo1.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        jLabel_titulo1.setText("Total Productos");

        lblValor1.setBackground(new java.awt.Color(255, 255, 255));
        lblValor1.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblValor1.setText("0");

        jLabel_sub1.setBackground(new java.awt.Color(200, 200, 200));
        jLabel_sub1.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jLabel_sub1.setText("productos en inventario");

        javax.swing.GroupLayout jPanel_card1Layout = new javax.swing.GroupLayout(jPanel_card1);
        jPanel_card1.setLayout(jPanel_card1Layout);
        jPanel_card1Layout.setHorizontalGroup(
            jPanel_card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card1Layout.createSequentialGroup()
                .addGroup(jPanel_card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_card1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel_sub1))
                    .addGroup(jPanel_card1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel_titulo1))
                    .addGroup(jPanel_card1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(lblValor1)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel_card1Layout.setVerticalGroup(
            jPanel_card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_titulo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblValor1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_sub1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_card2.setBackground(new java.awt.Color(142, 68, 173));

        jLabel_titulo2.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        jLabel_titulo2.setText("Con Costo Registrado");

        lblValor2.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblValor2.setText("0");

        jLabel_sub2.setBackground(new java.awt.Color(200, 200, 200));
        jLabel_sub2.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jLabel_sub2.setText("tiene costo registrado");

        javax.swing.GroupLayout jPanel_card2Layout = new javax.swing.GroupLayout(jPanel_card2);
        jPanel_card2.setLayout(jPanel_card2Layout);
        jPanel_card2Layout.setHorizontalGroup(
            jPanel_card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_card2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_titulo2)
                .addGap(30, 30, 30))
            .addGroup(jPanel_card2Layout.createSequentialGroup()
                .addGroup(jPanel_card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_card2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel_sub2))
                    .addGroup(jPanel_card2Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(lblValor2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_card2Layout.setVerticalGroup(
            jPanel_card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_titulo2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblValor2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_sub2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_card3.setBackground(new java.awt.Color(39, 174, 96));

        jLabel_titulo3.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        jLabel_titulo3.setText("Margen Promedio");

        lblValor3.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblValor3.setText("0");

        jLabel_sub3.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jLabel_sub3.setText("de ganancia promedio");

        javax.swing.GroupLayout jPanel_card3Layout = new javax.swing.GroupLayout(jPanel_card3);
        jPanel_card3.setLayout(jPanel_card3Layout);
        jPanel_card3Layout.setHorizontalGroup(
            jPanel_card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_sub3)
                    .addGroup(jPanel_card3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblValor3))
                    .addComponent(jLabel_titulo3))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel_card3Layout.setVerticalGroup(
            jPanel_card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_titulo3)
                .addGap(12, 12, 12)
                .addComponent(lblValor3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_sub3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_card4.setBackground(new java.awt.Color(230, 126, 34));

        jLabel_titulo4.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        jLabel_titulo4.setText("Ganancia Potencial");

        lblValor4.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblValor4.setText("0");

        jLabel_sub4.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jLabel_sub4.setText("ganancia si se vende todo");

        javax.swing.GroupLayout jPanel_card4Layout = new javax.swing.GroupLayout(jPanel_card4);
        jPanel_card4.setLayout(jPanel_card4Layout);
        jPanel_card4Layout.setHorizontalGroup(
            jPanel_card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_sub4)
                    .addGroup(jPanel_card4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblValor4))
                    .addComponent(jLabel_titulo4))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel_card4Layout.setVerticalGroup(
            jPanel_card4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_titulo4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblValor4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_sub4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_card5.setBackground(new java.awt.Color(52, 73, 94));

        jLabel_titulo5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel_titulo5.setText("Costo Inventario");

        lblValor5.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblValor5.setText("0");

        jLabel_sub5.setBackground(new java.awt.Color(200, 200, 200));
        jLabel_sub5.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        jLabel_sub5.setForeground(new java.awt.Color(200, 200, 200));
        jLabel_sub5.setText("inventario en stock");

        javax.swing.GroupLayout jPanel_card5Layout = new javax.swing.GroupLayout(jPanel_card5);
        jPanel_card5.setLayout(jPanel_card5Layout);
        jPanel_card5Layout.setHorizontalGroup(
            jPanel_card5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card5Layout.createSequentialGroup()
                .addGroup(jPanel_card5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_card5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel_titulo5))
                    .addGroup(jPanel_card5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel_sub5))
                    .addGroup(jPanel_card5Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lblValor5)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel_card5Layout.setVerticalGroup(
            jPanel_card5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_titulo5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblValor5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_sub5)
                .addGap(12, 12, 12))
        );

        jPanel_card6.setBackground(new java.awt.Color(22, 160, 133));

        jLabel_titulo6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel_titulo6.setText("Valor Inventario(Venta)");

        lblValor6.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblValor6.setText("0");

        jLabel_sub6.setBackground(new java.awt.Color(200, 200, 200));
        jLabel_sub6.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        jLabel_sub6.setForeground(new java.awt.Color(200, 200, 200));
        jLabel_sub6.setText("valor total de venta");

        javax.swing.GroupLayout jPanel_card6Layout = new javax.swing.GroupLayout(jPanel_card6);
        jPanel_card6.setLayout(jPanel_card6Layout);
        jPanel_card6Layout.setHorizontalGroup(
            jPanel_card6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card6Layout.createSequentialGroup()
                .addGroup(jPanel_card6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_card6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel_titulo6))
                    .addGroup(jPanel_card6Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(lblValor6))
                    .addGroup(jPanel_card6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel_sub6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_card6Layout.setVerticalGroup(
            jPanel_card6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_titulo6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(lblValor6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_sub6)
                .addGap(12, 12, 12))
        );

        jPanel_card7.setBackground(new java.awt.Color(231, 76, 60));

        jLabel_titulo7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel_titulo7.setText("Margen < 20%");

        lblValor7.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblValor7.setText("0");

        jLabel_sub7.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        jLabel_sub7.setForeground(new java.awt.Color(200, 200, 200));
        jLabel_sub7.setText("productos (revisar precios)");

        javax.swing.GroupLayout jPanel_card7Layout = new javax.swing.GroupLayout(jPanel_card7);
        jPanel_card7.setLayout(jPanel_card7Layout);
        jPanel_card7Layout.setHorizontalGroup(
            jPanel_card7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_card7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_sub7)
                    .addGroup(jPanel_card7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblValor7)
                        .addComponent(jLabel_titulo7)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel_card7Layout.setVerticalGroup(
            jPanel_card7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_titulo7)
                .addGap(18, 18, 18)
                .addComponent(lblValor7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel_sub7)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jPanel_card8.setBackground(new java.awt.Color(46, 204, 113));

        jLabel_titulo8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel_titulo8.setText("Margen > 50%");

        lblValor8.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblValor8.setText("0");

        jLabel_sub8.setFont(new java.awt.Font("Segoe UI", 0, 9)); // NOI18N
        jLabel_sub8.setForeground(new java.awt.Color(200, 200, 200));
        jLabel_sub8.setText("productos (buen margen)");

        javax.swing.GroupLayout jPanel_card8Layout = new javax.swing.GroupLayout(jPanel_card8);
        jPanel_card8.setLayout(jPanel_card8Layout);
        jPanel_card8Layout.setHorizontalGroup(
            jPanel_card8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_card8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_sub8)
                    .addGroup(jPanel_card8Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(lblValor8))
                    .addComponent(jLabel_titulo8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_card8Layout.setVerticalGroup(
            jPanel_card8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_card8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_titulo8)
                .addGap(18, 18, 18)
                .addComponent(lblValor8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel_sub8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_grafico.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_grafico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));

        jLabel_tituloGrafico.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel_tituloGrafico.setText("Top 8 Productos - Mayor Margen");

        panelGrafico.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelGraficoLayout = new javax.swing.GroupLayout(panelGrafico);
        panelGrafico.setLayout(panelGraficoLayout);
        panelGraficoLayout.setHorizontalGroup(
            panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 590, Short.MAX_VALUE)
        );
        panelGraficoLayout.setVerticalGroup(
            panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel_graficoLayout = new javax.swing.GroupLayout(jPanel_grafico);
        jPanel_grafico.setLayout(jPanel_graficoLayout);
        jPanel_graficoLayout.setHorizontalGroup(
            jPanel_graficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel_graficoLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel_tituloGrafico)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_graficoLayout.setVerticalGroup(
            jPanel_graficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_graficoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_tituloGrafico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(panelGrafico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel_tabla.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_tabla.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalle de Productos"));

        jTable_productos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Producto", "Costo Compra", "Precio Venta", "Ganancia", "Margen %", "Stock"
            }
        ));
        jScrollPane1.setViewportView(jTable_productos);

        javax.swing.GroupLayout jPanel_tablaLayout = new javax.swing.GroupLayout(jPanel_tabla);
        jPanel_tabla.setLayout(jPanel_tablaLayout);
        jPanel_tablaLayout.setHorizontalGroup(
            jPanel_tablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel_tablaLayout.setVerticalGroup(
            jPanel_tablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel_principalLayout = new javax.swing.GroupLayout(jPanel_principal);
        jPanel_principal.setLayout(jPanel_principalLayout);
        jPanel_principalLayout.setHorizontalGroup(
            jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_principalLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_principalLayout.createSequentialGroup()
                        .addComponent(jLabel_titulo)
                        .addGap(65, 65, 65)
                        .addComponent(jLabel_actualizacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_actualizar))
                    .addGroup(jPanel_principalLayout.createSequentialGroup()
                        .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_principalLayout.createSequentialGroup()
                                .addComponent(jPanel_card1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_principalLayout.createSequentialGroup()
                                .addComponent(jPanel_card5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel_card2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel_card6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_principalLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jPanel_card3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_principalLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel_card7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel_card8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel_card4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel_grafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_tabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel_principalLayout.setVerticalGroup(
            jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_principalLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_titulo)
                    .addComponent(jLabel_actualizacion)
                    .addComponent(jButton_actualizar))
                .addGap(18, 18, 18)
                .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_card4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel_card2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel_card1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel_card3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_principalLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel_card6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_principalLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel_principalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel_card5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel_card7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel_card8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addComponent(jPanel_grafico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jPanel_tabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_principal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_principal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarActionPerformed
        // TODO add your handling code here:
        cargarDashboard();
    }//GEN-LAST:event_jButton_actualizarActionPerformed

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
            java.util.logging.Logger.getLogger(FrmCostos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmCostos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmCostos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmCostos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmCostos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_actualizar;
    private javax.swing.JLabel jLabel_actualizacion;
    private javax.swing.JLabel jLabel_sub1;
    private javax.swing.JLabel jLabel_sub2;
    private javax.swing.JLabel jLabel_sub3;
    private javax.swing.JLabel jLabel_sub4;
    private javax.swing.JLabel jLabel_sub5;
    private javax.swing.JLabel jLabel_sub6;
    private javax.swing.JLabel jLabel_sub7;
    private javax.swing.JLabel jLabel_sub8;
    private javax.swing.JLabel jLabel_titulo;
    private javax.swing.JLabel jLabel_titulo1;
    private javax.swing.JLabel jLabel_titulo2;
    private javax.swing.JLabel jLabel_titulo3;
    private javax.swing.JLabel jLabel_titulo4;
    private javax.swing.JLabel jLabel_titulo5;
    private javax.swing.JLabel jLabel_titulo6;
    private javax.swing.JLabel jLabel_titulo7;
    private javax.swing.JLabel jLabel_titulo8;
    private javax.swing.JLabel jLabel_tituloGrafico;
    private javax.swing.JPanel jPanel_card1;
    private javax.swing.JPanel jPanel_card2;
    private javax.swing.JPanel jPanel_card3;
    private javax.swing.JPanel jPanel_card4;
    private javax.swing.JPanel jPanel_card5;
    private javax.swing.JPanel jPanel_card6;
    private javax.swing.JPanel jPanel_card7;
    private javax.swing.JPanel jPanel_card8;
    private javax.swing.JPanel jPanel_grafico;
    private javax.swing.JPanel jPanel_principal;
    private javax.swing.JPanel jPanel_tabla;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_productos;
    private javax.swing.JLabel lblValor1;
    private javax.swing.JLabel lblValor2;
    private javax.swing.JLabel lblValor3;
    private javax.swing.JLabel lblValor4;
    private javax.swing.JLabel lblValor5;
    private javax.swing.JLabel lblValor6;
    private javax.swing.JLabel lblValor7;
    private javax.swing.JLabel lblValor8;
    private javax.swing.JPanel panelGrafico;
    // End of variables declaration//GEN-END:variables
}
