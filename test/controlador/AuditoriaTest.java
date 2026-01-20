/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author piper
 */
public class AuditoriaTest {
    
    public AuditoriaTest() {
    }

    /**
     * Test of registrar method, of class Auditoria.
     */
    @Test
    public void testRegistrar() {
        System.out.println("registrar");
        String usuario = "";
        String accion = "";
        String modulo = "";
        String descripcion = "";
        Auditoria instance = new Auditoria();
        boolean expResult = false;
        boolean result = instance.registrar(usuario, accion, modulo, descripcion);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registrarLogin method, of class Auditoria.
     */
    @Test
    public void testRegistrarLogin() {
        System.out.println("registrarLogin");
        String usuario = "";
        Auditoria instance = new Auditoria();
        instance.registrarLogin(usuario);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registrarLogout method, of class Auditoria.
     */
    @Test
    public void testRegistrarLogout() {
        System.out.println("registrarLogout");
        String usuario = "";
        Auditoria instance = new Auditoria();
        instance.registrarLogout(usuario);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registrarVenta method, of class Auditoria.
     */
    @Test
    public void testRegistrarVenta() {
        System.out.println("registrarVenta");
        String usuario = "";
        int ventaId = 0;
        double total = 0.0;
        Auditoria instance = new Auditoria();
        instance.registrarVenta(usuario, ventaId, total);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registrarAnulacionVenta method, of class Auditoria.
     */
    @Test
    public void testRegistrarAnulacionVenta() {
        System.out.println("registrarAnulacionVenta");
        String usuario = "";
        int ventaId = 0;
        String motivo = "";
        Auditoria instance = new Auditoria();
        instance.registrarAnulacionVenta(usuario, ventaId, motivo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registrarConsulta method, of class Auditoria.
     */
    @Test
    public void testRegistrarConsulta() {
        System.out.println("registrarConsulta");
        String usuario = "";
        String modulo = "";
        String detalle = "";
        Auditoria instance = new Auditoria();
        instance.registrarConsulta(usuario, modulo, detalle);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registrarModificacion method, of class Auditoria.
     */
    @Test
    public void testRegistrarModificacion() {
        System.out.println("registrarModificacion");
        String usuario = "";
        String modulo = "";
        String detalle = "";
        Auditoria instance = new Auditoria();
        instance.registrarModificacion(usuario, modulo, detalle);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerTodos method, of class Auditoria.
     */
    @Test
    public void testObtenerTodos() {
        System.out.println("obtenerTodos");
        Auditoria instance = new Auditoria();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerTodos();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of buscar method, of class Auditoria.
     */
    @Test
    public void testBuscar() {
        System.out.println("buscar");
        String usuario = "";
        Date fecha = null;
        Auditoria instance = new Auditoria();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.buscar(usuario, fecha);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerPorUsuario method, of class Auditoria.
     */
    @Test
    public void testObtenerPorUsuario() {
        System.out.println("obtenerPorUsuario");
        String usuario = "";
        Auditoria instance = new Auditoria();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerPorUsuario(usuario);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerEstadisticasPorUsuario method, of class Auditoria.
     */
    @Test
    public void testObtenerEstadisticasPorUsuario() {
        System.out.println("obtenerEstadisticasPorUsuario");
        Auditoria instance = new Auditoria();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerEstadisticasPorUsuario();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerAccionesFrecuentes method, of class Auditoria.
     */
    @Test
    public void testObtenerAccionesFrecuentes() {
        System.out.println("obtenerAccionesFrecuentes");
        int limite = 0;
        Auditoria instance = new Auditoria();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerAccionesFrecuentes(limite);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerActividadReciente method, of class Auditoria.
     */
    @Test
    public void testObtenerActividadReciente() {
        System.out.println("obtenerActividadReciente");
        Auditoria instance = new Auditoria();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerActividadReciente();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerPorRangoFechas method, of class Auditoria.
     */
    @Test
    public void testObtenerPorRangoFechas() {
        System.out.println("obtenerPorRangoFechas");
        Date fechaInicio = null;
        Date fechaFin = null;
        Auditoria instance = new Auditoria();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerPorRangoFechas(fechaInicio, fechaFin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
