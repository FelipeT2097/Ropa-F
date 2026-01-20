/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author piper
 */
public class RegistrarReversionesTest {
    
    public RegistrarReversionesTest() {
    }

    /**
     * Test of buscarFactura method, of class RegistrarReversionesVentas.
     */
    @Test
    public void testBuscarFactura() {
        System.out.println("buscarFactura");
        String busqueda = "";
        RegistrarReversionesVentas instance = new RegistrarReversionesVentas();
        Map<String, Object> expResult = null;
        Map<String, Object> result = instance.buscarFactura(busqueda);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerProductosFactura method, of class RegistrarReversionesVentas.
     */
    @Test
    public void testObtenerProductosFactura() {
        System.out.println("obtenerProductosFactura");
        int facturaId = 0;
        RegistrarReversionesVentas instance = new RegistrarReversionesVentas();
        List<Map<String, Object>> expResult = null;
        List<Map<String, Object>> result = instance.obtenerProductosFactura(facturaId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reversarFactura method, of class RegistrarReversionesVentas.
     */
    @Test
    public void testReversarFactura() {
        System.out.println("reversarFactura");
        int facturaId = 0;
        String motivo = "";
        String usuario = "";
        RegistrarReversionesVentas instance = new RegistrarReversionesVentas();
        int expResult = 0;
        int result = instance.reversarFactura(facturaId, motivo, usuario);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of validarFacturaParaReversion method, of class RegistrarReversionesVentas.
     */
    @Test
    public void testValidarFacturaParaReversion() {
        System.out.println("validarFacturaParaReversion");
        int facturaId = 0;
        RegistrarReversionesVentas instance = new RegistrarReversionesVentas();
        String expResult = "";
        String result = instance.validarFacturaParaReversion(facturaId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerHistorialReversiones method, of class RegistrarReversionesVentas.
     */
    @Test
    public void testObtenerHistorialReversiones() {
        System.out.println("obtenerHistorialReversiones");
        int limite = 0;
        RegistrarReversionesVentas instance = new RegistrarReversionesVentas();
        List<Map<String, Object>> expResult = null;
        List<Map<String, Object>> result = instance.obtenerHistorialReversiones(limite);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
