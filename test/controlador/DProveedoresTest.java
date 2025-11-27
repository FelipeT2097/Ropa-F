/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import modelo.Proveedores;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author piper
 */
public class DProveedoresTest {

    public DProveedoresTest() {
    }

    /**
     * Test of getId method, of class Proveedores.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Proveedores instance = new Proveedores();
        Integer expResult = null; // Valor esperado si no se ha establecido un id
        Integer result = instance.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setId method, of class Proveedores.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        Integer id = 1; // Definir un valor de ID para probar
        Proveedores instance = new Proveedores();
        instance.setId(id);
        assertEquals(id, instance.getId()); // Verificar que el valor se haya asignado correctamente
    }

    /**
     * Test of getNombreProveedor method, of class Proveedores.
     */
    @Test
    public void testGetNombreProveedor() {
        System.out.println("getNombreProveedor");
        Proveedores instance = new Proveedores();
        String expResult = null; // El valor predeterminado debería ser null
        String result = instance.getNombreProveedor();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNombreProveedor method, of class Proveedores.
     */
    @Test
    public void testSetNombreProveedor() {
        System.out.println("setNombreProveedor");
        String nombreProveedor = "Proveedor Test"; // Valor para el nombre
        Proveedores instance = new Proveedores();
        instance.setNombreProveedor(nombreProveedor);
        assertEquals(nombreProveedor, instance.getNombreProveedor()); // Verificar que se haya asignado correctamente
    }

    /**
     * Test of getTipoDocumento method, of class Proveedores.
     */
    @Test
    public void testGetTipoDocumento() {
        System.out.println("getTipoDocumento");
        Proveedores instance = new Proveedores();
        String expResult = null; // El valor predeterminado debería ser null
        String result = instance.getTipoDocumento();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTipoDocumento method, of class Proveedores.
     */
    @Test
    public void testSetTipoDocumento() {
        System.out.println("setTipoDocumento");
        String tipoDocumento = "CC"; // Tipo de documento de ejemplo
        Proveedores instance = new Proveedores();
        instance.setTipoDocumento(tipoDocumento);
        assertEquals(tipoDocumento, instance.getTipoDocumento());
    }

    /**
     * Test of getNumeroDocumento method, of class Proveedores.
     */
    @Test
    public void testGetNumeroDocumento() {
        System.out.println("getNumeroDocumento");
        Proveedores instance = new Proveedores();
        String expResult = null; // Valor predeterminado debería ser null
        String result = instance.getNumeroDocumento();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNumeroDocumento method, of class Proveedores.
     */
    @Test
    public void testSetNumeroDocumento() {
        System.out.println("setNumeroDocumento");
        String numeroDocumento = "123456789"; // Número de documento de ejemplo
        Proveedores instance = new Proveedores();
        instance.setNumeroDocumento(numeroDocumento);
        assertEquals(numeroDocumento, instance.getNumeroDocumento());
    }

    /**
     * Test of getGenero method, of class Proveedores.
     */
    @Test
    public void testGetGenero() {
        System.out.println("getGenero");
        Proveedores instance = new Proveedores();
        String expResult = null; // Valor predeterminado debería ser null
        String result = instance.getGenero();
        assertEquals(expResult, result);
    }

    /**
     * Test of setGenero method, of class Proveedores.
     */
    @Test
    public void testSetGenero() {
        System.out.println("setGenero");
        String genero = "Masculino"; // Valor de ejemplo
        Proveedores instance = new Proveedores();
        instance.setGenero(genero);
        assertEquals(genero, instance.getGenero());
    }

    /**
     * Test of getTelefono method, of class Proveedores.
     */
    @Test
    public void testGetTelefono() {
        System.out.println("getTelefono");
        Proveedores instance = new Proveedores();
        String expResult = null; // Valor predeterminado debería ser null
        String result = instance.getTelefono();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTelefono method, of class Proveedores.
     */
    @Test
    public void testSetTelefono() {
        System.out.println("setTelefono");
        String telefono = "3001234567"; // Valor de teléfono de ejemplo
        Proveedores instance = new Proveedores();
        instance.setTelefono(telefono);
        assertEquals(telefono, instance.getTelefono());
    }

    /**
     * Test of getCorreoElectronico method, of class Proveedores.
     */
    @Test
    public void testGetCorreoElectronico() {
        System.out.println("getCorreoElectronico");
        Proveedores instance = new Proveedores();
        String expResult = null; // Valor predeterminado debería ser null
        String result = instance.getCorreoElectronico();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCorreoElectronico method, of class Proveedores.
     */
    @Test
    public void testSetCorreoElectronico() {
        System.out.println("setCorreoElectronico");
        String correoElectronico = "proveedor@correo.com"; // Ejemplo de correo
        Proveedores instance = new Proveedores();
        instance.setCorreoElectronico(correoElectronico);
        assertEquals(correoElectronico, instance.getCorreoElectronico());
    }

    /**
     * Test of insertarProveedores method, of class Proveedores.
     */
    @Test
    public void testInsertarProveedores() {
        System.out.println("insertarProveedores");
        Proveedores proveedores = new Proveedores(); // Ejemplo de proveedor
        proveedores.setNombreProveedor("Proveedor Test");
        proveedores.setTipoDocumento("Cedula Ciudadania");
        proveedores.setNumeroDocumento("23345678");
        proveedores.setTelefono("3001234567");
        proveedores.setGenero("Hombre");
        proveedores.setCorreoElectronico("test@correo.com");
        Proveedores.insertarProveedores(proveedores);
        // Asegúrate de que la inserción se haya realizado correctamente en la base de datos
        // (si es posible, consulta el registro insertado o realiza una validación posterior)
    }

    /**
     * Test of proveeList method, of class Proveedores.
     */
    @Test
    public void testProveeList() {
        System.out.println("proveeList");
        String prove = "Test"; // Parámetro de búsqueda
        Proveedores instance = new Proveedores();

   
        // Verificar que la lista contenga los elementos esperados
    }

    /**
     * Test of actualizarProveedores method, of class Proveedores.
     */
    @Test
    public void testActualizarProveedores() {
        System.out.println("actualizarProveedores");
        Proveedores proveedores = new Proveedores(); // Ejemplo de proveedor
        proveedores.setId(1); // ID del proveedor a actualizar
        proveedores.setNombreProveedor("Proveedor Actualizado");
        Proveedores.actualizarProveedores(proveedores);
        // Verifica que los datos se hayan actualizado correctamente
        // (consulta el registro actualizado)
    }

    /**
     * Test of eliminarProveedor method, of class Proveedores.
     */
    @Test
    public void testEliminarProveedor() {
        System.out.println("eliminarProveedor");
        Integer id = 1; // ID del proveedor a eliminar
        Proveedores.eliminarProveedor(id);
        // Verifica que el proveedor haya sido eliminado
        // (consulta la base de datos para asegurarte)
    }
}