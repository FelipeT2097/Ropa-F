/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import modelo.ConexionDB;

/**
 *
 * @author piper
 */
public class Auditoria {

    private final Connection conn;
    private final SimpleDateFormat formatoFechaHora;

    public Auditoria() {
        this.conn = ConexionDB.getConnection();
        this.formatoFechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Registra un evento de auditoría en el sistema.
     *
     * @param usuario Usuario que realizó la acción
     * @param accion Tipo de acción (LOGIN, CREAR, MODIFICAR, ELIMINAR, etc.)
     * @param modulo Módulo del sistema donde ocurrió la acción
     * @param descripcion Descripción detallada del evento
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrar(String usuario, String accion, String modulo, String descripcion) {
        String sql = "INSERT INTO auditoria (usuario, accion, modulo, descripcion, fecha_hora) "
                + "VALUES (?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, accion);
            ps.setString(3, modulo);
            ps.setString(4, descripcion);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
            return false;
        }
    }

    //Registra un inicio de sesión
    public void registrarLogin(String usuario) {
        registrar(usuario, "LOGIN", "Sistema", "Inicio de sesión exitoso");
    }

    //Registra un cierre de sesión
    public void registrarLogout(String usuario) {
        registrar(usuario, "LOGOUT", "Sistema", "Cerró sesión");
    }

    // Registra la creación de una venta
    public void registrarVenta(String usuario, int ventaId, double total) {
        String desc = String.format("Nueva venta registrada - ID: %d, Total: $%.2f", ventaId, total);
        registrar(usuario, "CREAR", "Ventas", desc);
    }

    // Registra la anulación de una venta
    public void registrarAnulacionVenta(String usuario, int ventaId, String motivo) {
        String desc = String.format("Anuló la venta ID: %d - Motivo: %s", ventaId, motivo);
        registrar(usuario, "ANULAR", "Ventas", desc);
    }

    // Registra consulta a un módulo
    public void registrarConsulta(String usuario, String modulo, String detalle) {
        registrar(usuario, "CONSULTAR", modulo, detalle);
    }

    // Registra modificación de datos
    public void registrarModificacion(String usuario, String modulo, String detalle) {
        registrar(usuario, "MODIFICAR", modulo, detalle);
    }

    // Obtiene todos los registros de auditoría ordenados por fecha descendente
    public ArrayList<Map<String, Object>> obtenerTodos() {
        String sql = "SELECT * FROM auditoria ORDER BY fecha_hora DESC LIMIT 1000";
        return ejecutarConsulta(sql);
    }

    // Busca registros de auditoría con filtros opcionales.
    public ArrayList<Map<String, Object>> buscar(String usuario,
            java.util.Date fecha) {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM auditoria WHERE 1=1"
        );

        ArrayList<Object> parametros = new ArrayList<>();

        // Filtro por usuario
        if (usuario != null && !usuario.trim().isEmpty()) {
            sql.append(" AND usuario LIKE ?");
            parametros.add("%" + usuario + "%");
        }

        // Filtro por fecha
        if (fecha != null) {
            sql.append(" AND DATE(fecha_hora) = ?");
            parametros.add(new java.sql.Date(fecha.getTime()));
        }

        sql.append(" ORDER BY fecha_hora DESC LIMIT 500");

        return ejecutarConsultaConParametros(sql.toString(), parametros);
    }

    //Obtiene los registros de auditoría de un usuario específico
    public ArrayList<Map<String, Object>> obtenerPorUsuario(String usuario) {
        String sql = "SELECT * FROM auditoria WHERE usuario = ? ORDER BY fecha_hora DESC";

        ArrayList<Map<String, Object>> registros = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapearRegistro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar auditoría por usuario: " + e.getMessage());
        }

        return registros;
    }

    // Obtiene estadísticas de actividad por usuario
    public ArrayList<Map<String, Object>> obtenerEstadisticasPorUsuario() {
        String sql = "SELECT "
                + "usuario, "
                + "COUNT(*) as total_acciones, "
                + "MAX(fecha_hora) as ultima_actividad, "
                + "MIN(fecha_hora) as primera_actividad "
                + "FROM auditoria "
                + "GROUP BY usuario "
                + "ORDER BY total_acciones DESC";

        return ejecutarConsulta(sql);
    }

    //Obtiene las acciones más frecuentes del sistema
    public ArrayList<Map<String, Object>> obtenerAccionesFrecuentes(int limite) {
        String sql = "SELECT "
                + "accion, "
                + "modulo, "
                + "COUNT(*) as frecuencia "
                + "FROM auditoria "
                + "GROUP BY accion, modulo "
                + "ORDER BY frecuencia DESC "
                + "LIMIT ?";

        ArrayList<Map<String, Object>> resultados = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("accion", rs.getString("accion"));
                    row.put("modulo", rs.getString("modulo"));
                    row.put("frecuencia", rs.getInt("frecuencia"));
                    resultados.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener acciones frecuentes: " + e.getMessage());
        }

        return resultados;
    }

    //Obtiene actividad reciente del sistema (últimas 24 horas)
    public ArrayList<Map<String, Object>> obtenerActividadReciente() {
        String sql = "SELECT * FROM auditoria "
                + "WHERE fecha_hora >= DATE_SUB(NOW(), INTERVAL 24 HOUR) "
                + "ORDER BY fecha_hora DESC";

        return ejecutarConsulta(sql);
    }

    //Obtiene registros por rango de fechas
    public ArrayList<Map<String, Object>> obtenerPorRangoFechas(java.util.Date fechaInicio,
            java.util.Date fechaFin) {
        String sql = "SELECT * FROM auditoria "
                + "WHERE DATE(fecha_hora) BETWEEN ? AND ? "
                + "ORDER BY fecha_hora DESC";

        ArrayList<Object> parametros = new ArrayList<>();
        parametros.add(new java.sql.Date(fechaInicio.getTime()));
        parametros.add(new java.sql.Date(fechaFin.getTime()));

        return ejecutarConsultaConParametros(sql, parametros);
    }

    // Ejecuta una consulta SQL simple sin parámetros
    private ArrayList<Map<String, Object>> ejecutarConsulta(String sql) {
        ArrayList<Map<String, Object>> registros = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                registros.add(mapearRegistro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar consulta de auditoría: " + e.getMessage());
        }

        return registros;
    }

    //Ejecuta una consulta SQL con parámetros dinámicos
    private ArrayList<Map<String, Object>> ejecutarConsultaConParametros(String sql,
            ArrayList<Object> parametros) {
        ArrayList<Map<String, Object>> registros = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Establecer parámetros
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapearRegistro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar consulta parametrizada: " + e.getMessage());
        }

        return registros;
    }

    // Mapea un ResultSet a un Map con los datos del registro
    private Map<String, Object> mapearRegistro(ResultSet rs) throws SQLException {
        Map<String, Object> registro = new HashMap<>();
        registro.put("id", rs.getInt("id"));
        registro.put("usuario", rs.getString("usuario"));
        registro.put("accion", rs.getString("accion"));
        registro.put("modulo", rs.getString("modulo"));
        registro.put("descripcion", rs.getString("descripcion"));
        registro.put("fecha_hora", rs.getTimestamp("fecha_hora"));

        // Formato legible de fecha
        Timestamp timestamp = rs.getTimestamp("fecha_hora");
        if (timestamp != null) {
            registro.put("fecha_formateada", formatoFechaHora.format(timestamp));
        }

        return registro;
    }
}
