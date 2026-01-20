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
public class GeneradorReportesTest {
    
    public GeneradorReportesTest() {
    }

    /**
     * Test of obtenerVentasDelDia method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerVentasDelDia() {
        System.out.println("obtenerVentasDelDia");
        GeneradorReportes instance = new GeneradorReportes();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerVentasDelDia();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerResumenVentasDelDia method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerResumenVentasDelDia() {
        System.out.println("obtenerResumenVentasDelDia");
        GeneradorReportes instance = new GeneradorReportes();
        Map<String, Object> expResult = null;
        Map<String, Object> result = instance.obtenerResumenVentasDelDia();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerVentasPorRango method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerVentasPorRango() {
        System.out.println("obtenerVentasPorRango");
        Date fechaInicio = null;
        Date fechaFin = null;
        GeneradorReportes instance = new GeneradorReportes();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerVentasPorRango(fechaInicio, fechaFin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerResumenVentasPorRango method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerResumenVentasPorRango() {
        System.out.println("obtenerResumenVentasPorRango");
        Date fechaInicio = null;
        Date fechaFin = null;
        GeneradorReportes instance = new GeneradorReportes();
        Map<String, Object> expResult = null;
        Map<String, Object> result = instance.obtenerResumenVentasPorRango(fechaInicio, fechaFin);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerProductosMasVendidos method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerProductosMasVendidos() {
        System.out.println("obtenerProductosMasVendidos");
        int limite = 0;
        GeneradorReportes instance = new GeneradorReportes();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerProductosMasVendidos(limite);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerIngresosMensuales method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerIngresosMensuales() {
        System.out.println("obtenerIngresosMensuales");
        GeneradorReportes instance = new GeneradorReportes();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerIngresosMensuales();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerVentaIdPorNumeroFactura method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerVentaIdPorNumeroFactura() {
        System.out.println("obtenerVentaIdPorNumeroFactura");
        String numeroFactura = "";
        GeneradorReportes instance = new GeneradorReportes();
        int expResult = 0;
        int result = instance.obtenerVentaIdPorNumeroFactura(numeroFactura);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerVentaPorNumeroFactura method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerVentaPorNumeroFactura() {
        System.out.println("obtenerVentaPorNumeroFactura");
        String numeroFactura = "";
        GeneradorReportes instance = new GeneradorReportes();
        Map<String, Object> expResult = null;
        Map<String, Object> result = instance.obtenerVentaPorNumeroFactura(numeroFactura);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerProductosVenta method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerProductosVenta() {
        System.out.println("obtenerProductosVenta");
        int ventaId = 0;
        GeneradorReportes instance = new GeneradorReportes();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerProductosVenta(ventaId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerVentasPorCliente method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerVentasPorCliente() {
        System.out.println("obtenerVentasPorCliente");
        String busqueda = "";
        GeneradorReportes instance = new GeneradorReportes();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerVentasPorCliente(busqueda);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerResumenCliente method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerResumenCliente() {
        System.out.println("obtenerResumenCliente");
        String busqueda = "";
        GeneradorReportes instance = new GeneradorReportes();
        Map<String, Object> expResult = null;
        Map<String, Object> result = instance.obtenerResumenCliente(busqueda);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cerrarConexion method, of class GeneradorReportes.
     */
    @Test
    public void testCerrarConexion() {
        System.out.println("cerrarConexion");
        GeneradorReportes instance = new GeneradorReportes();
        instance.cerrarConexion();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerListaProductosStockBajo method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerListaProductosStockBajo() {
        System.out.println("obtenerListaProductosStockBajo");
        int limite = 0;
        GeneradorReportes instance = new GeneradorReportes();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerListaProductosStockBajo(limite);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerTotalProductos method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerTotalProductos() {
        System.out.println("obtenerTotalProductos");
        GeneradorReportes instance = new GeneradorReportes();
        int expResult = 0;
        int result = instance.obtenerTotalProductos();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerProductosStockBajo method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerProductosStockBajo() {
        System.out.println("obtenerProductosStockBajo");
        GeneradorReportes instance = new GeneradorReportes();
        int expResult = 0;
        int result = instance.obtenerProductosStockBajo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerTotalClientes method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerTotalClientes() {
        System.out.println("obtenerTotalClientes");
        GeneradorReportes instance = new GeneradorReportes();
        int expResult = 0;
        int result = instance.obtenerTotalClientes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerProductosSinMovimiento method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerProductosSinMovimiento() {
        System.out.println("obtenerProductosSinMovimiento");
        int dias = 0;
        GeneradorReportes instance = new GeneradorReportes();
        ArrayList<Map<String, Object>> expResult = null;
        ArrayList<Map<String, Object>> result = instance.obtenerProductosSinMovimiento(dias);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerResumenAlertas method, of class GeneradorReportes.
     */
    @Test
    public void testObtenerResumenAlertas() {
        System.out.println("obtenerResumenAlertas");
        GeneradorReportes instance = new GeneradorReportes();
        Map<String, Integer> expResult = null;
        Map<String, Integer> result = instance.obtenerResumenAlertas();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
