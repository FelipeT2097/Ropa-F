package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.ConexionDB;

/**
 * Controlador para el Dashboard de Costos vs Precios de Venta
 * @author piper
 */
public class AnalisisCostos {

    //El costo se obtiene del último precio de compra registrado
    public List<Map<String, Object>> obtenerProductosConMargenes() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String, Object>> productos = new ArrayList<>();

        try {
            conn = ConexionDB.getConnection();

            // Obtener productos con el último precio de compra
            String sql = "SELECT p.id, p.codigo, p.nombre, p.precio as precio_venta, p.cantidad as stock, " +
                        "p.talla, p.color, p.genero, " +
                        "COALESCE(dc.precio_compra, 0) as costo_compra " +
                        "FROM productos p " +
                        "LEFT JOIN ( " +
                        "    SELECT producto_id, precio_compra " +
                        "    FROM detalle_compras dc1 " +
                        "    WHERE dc1.id = ( " +
                        "        SELECT MAX(dc2.id) FROM detalle_compras dc2 " +
                        "        WHERE dc2.producto_id = dc1.producto_id " +
                        "    ) " +
                        ") dc ON p.id = dc.producto_id " +
                        "ORDER BY p.nombre";

            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, Object> producto = new HashMap<>();
                producto.put("id", rs.getInt("id"));
                producto.put("codigo", rs.getString("codigo"));
                producto.put("nombre", rs.getString("nombre"));
                producto.put("stock", rs.getInt("stock"));
                producto.put("talla", rs.getString("talla"));
                producto.put("color", rs.getString("color"));
                producto.put("genero", rs.getString("genero"));
                
                // Precio de venta (viene como String, convertir a double)
                double precioVenta = 0;
                try {
                    precioVenta = Double.parseDouble(rs.getString("precio_venta"));
                } catch (Exception e) {
                    precioVenta = 0;
                }
                producto.put("precio_venta", precioVenta);
                
                // Costo de compra
                double costoCompra = rs.getDouble("costo_compra");
                producto.put("costo_compra", costoCompra);
                
                // Calcular ganancia y margen
                double ganancia = precioVenta - costoCompra;
                double margen = 0;
                if (precioVenta > 0) {
                    margen = (ganancia / precioVenta) * 100;
                }
                
                producto.put("ganancia", ganancia);
                producto.put("margen", margen);
                
                productos.add(producto);
            }

        } catch (Exception e) {
            System.err.println("Error al obtener productos con márgenes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, pst, rs);
        }

        return productos;
    }

    //Obtiene las métricas generales del dashboard
    public Map<String, Object> obtenerMetricasGenerales() {
        Map<String, Object> metricas = new HashMap<>();
        
        List<Map<String, Object>> productos = obtenerProductosConMargenes();
        
        int totalProductos = productos.size();
        int productosConCosto = 0;
        double sumaMargen = 0;
        double gananciaTotalPotencial = 0;
        double costoTotalInventario = 0;
        double valorTotalInventario = 0;
        int productosMargenBajo = 0; // Margen < 20%
        int productosMargenAlto = 0; // Margen > 50%
        
        for (Map<String, Object> producto : productos) {
            double costoCompra = (Double) producto.get("costo_compra");
            double precioVenta = (Double) producto.get("precio_venta");
            double ganancia = (Double) producto.get("ganancia");
            double margen = (Double) producto.get("margen");
            int stock = (Integer) producto.get("stock");
            
            if (costoCompra > 0) {
                productosConCosto++;
                sumaMargen += margen;
                
                // Ganancia potencial basada en stock
                gananciaTotalPotencial += ganancia * stock;
                costoTotalInventario += costoCompra * stock;
                valorTotalInventario += precioVenta * stock;
                
                if (margen < 20) {
                    productosMargenBajo++;
                } else if (margen > 50) {
                    productosMargenAlto++;
                }
            }
        }
        
        double margenPromedio = productosConCosto > 0 ? sumaMargen / productosConCosto : 0;
        
        metricas.put("total_productos", totalProductos);
        metricas.put("productos_con_costo", productosConCosto);
        metricas.put("margen_promedio", margenPromedio);
        metricas.put("ganancia_potencial", gananciaTotalPotencial);
        metricas.put("costo_inventario", costoTotalInventario);
        metricas.put("valor_inventario", valorTotalInventario);
        metricas.put("productos_margen_bajo", productosMargenBajo);
        metricas.put("productos_margen_alto", productosMargenAlto);
        
        return metricas;
    }

    //Obtiene los top productos por margen (mejores o peores)
    public List<Map<String, Object>> obtenerTopProductosPorMargen(int limite, boolean mejores) {
        List<Map<String, Object>> productos = obtenerProductosConMargenes();
        List<Map<String, Object>> resultado = new ArrayList<>();
        
        // Filtrar solo productos con costo registrado
        for (Map<String, Object> producto : productos) {
            double costoCompra = (Double) producto.get("costo_compra");
            if (costoCompra > 0) {
                resultado.add(producto);
            }
        }
        
        // Ordenar por margen
        resultado.sort((a, b) -> {
            double margenA = (Double) a.get("margen");
            double margenB = (Double) b.get("margen");
            if (mejores) {
                return Double.compare(margenB, margenA); // Descendente
            } else {
                return Double.compare(margenA, margenB); // Ascendente
            }
        });
        
        // Limitar resultados
        if (resultado.size() > limite) {
            resultado = resultado.subList(0, limite);
        }
        
        return resultado;
    }

    //Obtiene estadísticas por género
    public List<Map<String, Object>> obtenerEstadisticasPorGenero() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String, Object>> estadisticas = new ArrayList<>();

        try {
            conn = ConexionDB.getConnection();

            String sql = "SELECT p.genero, COUNT(*) as cantidad, " +
                        "AVG(CAST(p.precio AS DECIMAL(10,2))) as precio_promedio " +
                        "FROM productos p " +
                        "GROUP BY p.genero " +
                        "ORDER BY cantidad DESC";

            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("genero", rs.getString("genero"));
                stat.put("cantidad", rs.getInt("cantidad"));
                stat.put("precio_promedio", rs.getDouble("precio_promedio"));
                estadisticas.add(stat);
            }

        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas por género: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, pst, rs);
        }

        return estadisticas;
    }

    private void cerrarRecursos(Connection conn, PreparedStatement pst, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
