/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import modelo.Producto;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author piper
 */
public class ProductoTest {

    public ProductoTest() {
    }

    /**
     * Test of getId method, of class Producto.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Producto instance = new Producto();
        instance.setId(1); // Configuramos un ID
        Integer expResult = 1; // Lo que esperamos que devuelva
        Integer result = instance.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setId method, of class Producto.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        Integer id = 2;
        Producto instance = new Producto();
        instance.setId(id);
        assertEquals(id, instance.getId());
    }

    /**
     * Test of getNombre and setNombre methods, of class Producto.
     */
    @Test
    public void testSetGetNombre() {
        System.out.println("setGetNombre");
        Producto instance = new Producto();
        String nombre = "Camisa";
        instance.setNombre(nombre);
        String result = instance.getNombre();
        assertEquals(nombre, result);
    }

    /**
     * Test of getPrecio and setPrecio methods, of class Producto.
     */
    @Test
    public void testSetGetPrecio() {
        System.out.println("setGetPrecio");
        Producto instance = new Producto();
        String precio = "100";
        instance.setPrecio(precio);
        String result = instance.getPrecio();
        assertEquals(precio, result);
    }

    /**
     * Test of getCantidad and setCantidad methods, of class Producto.
     */
    @Test
    public void testSetGetCantidad() {
        System.out.println("setGetCantidad");
        Producto instance = new Producto();
        Integer cantidad = 50;
        instance.setCantidad(cantidad);
        Integer result = instance.getCantidad();
        assertEquals(cantidad, result);
    }

    /**
     * Test of getTalla and setTalla methods, of class Producto.
     */
    @Test
    public void testSetGetTalla() {
        System.out.println("setGetTalla");
        Producto instance = new Producto();
        String talla = "M";
        instance.setTalla(talla);
        String result = instance.getTalla();
        assertEquals(talla, result);
    }

    /**
     * Test of getColor and setColor methods, of class Producto.
     */
    @Test
    public void testSetGetColor() {
        System.out.println("setGetColor");
        Producto instance = new Producto();
        String color = "Rojo";
        instance.setColor(color);
        String result = instance.getColor();
        assertEquals(color, result);
    }

    /**
     * Test of getGenero and setGenero methods, of class Producto.
     */
    @Test
    public void testSetGetGenero() {
        System.out.println("setGetGenero");
        Producto instance = new Producto();
        String genero = "Unisex";
        instance.setGenero(genero);
        String result = instance.getGenero();
        assertEquals(genero, result);
    }

    /**
     * Test of insertarProducto method, of class Producto.
     */
    @Test
    public void testInsertarProducto() {
        System.out.println("insertarProducto");
        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Camisa");
        producto.setPrecio("100");
        // Asumiendo que el método insertarProducto devuelve un booleano indicando éxito
    }

    /**
     * Test of isCellEditable method, of class Producto.
     */
    @Test
    public void testIsCellEditable() {
        System.out.println("isCellEditable");
        int rowIndex = 0;
        int columnIndex = 0;
        Producto instance = new Producto();
        boolean expResult = false; // Asume que no es editable por defecto
        boolean result = instance.isCellEditable(rowIndex, columnIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of actualizarProducto method, of class Producto.
     */
    @Test
    public void testActualizarProducto() {
        // Crear un producto
        Producto producto = new Producto();
        producto.setId(20);  // Suponemos que el ID es 10 (o cualquier otro ID que exista en la base de datos)
        producto.setNombre("Camiseta");
        producto.setPrecio("25");
        producto.setCantidad(10);
        producto.setTalla("M");
        producto.setColor("Rojo");
        producto.setGenero("Unisex");

        // Llamar al método que actualiza el producto
        Producto.actualizarProducto(producto);

        // Validar que el producto fue actualizado (puedes usar consultas para verificar la base de datos)
        // Por ejemplo, verificar si el precio es el correcto después de la actualización:
        Producto productoActualizado = new Producto();
        productoActualizado.setId(1);  // El mismo ID que actualizamos

        // Obtén el producto actualizado desde la base de datos (esto es solo un ejemplo)
        String precioEsperado = "25";  // Este es el precio que deberías haber establecido en el paso anterior

    }


    /**
     * Test of eliminarProducto method, of class Producto.
     */
    @Test
    public void testEliminarProducto() {
        System.out.println("eliminarProducto");
        Integer codigo = 2; // Ajusta según la lógica
        Producto.eliminarProducto(codigo);
        // Puedes verificar si el producto fue eliminado adecuadamente
    }

    /**
     * Test of productoList method, of class Producto.
     */
    @Test
    public void testProductoList() {
        System.out.println("productoList");
        String val = "Camisa";
        Producto instance = new Producto();
        ArrayList<Producto> result = instance.productoList(val);
        assertNotNull(result); // Asegura que la lista no sea nula
    }

    /**
     * Test of updateProduct method, of class Producto.
     */
    @Test
   /* public void testUpdateProduct() {
        System.out.println("updateProduct");
        int productoId = 1;
        int columnIndex = 2;
        String newValue = "200";
        Producto instance = new Producto();
        instance.updateProduct(productoId, columnIndex, newValue);
        // Añadir una verificación si fuera posible obtener el valor actualizado
    }*/

    /**
     * Test of getNombre method, of class Producto.
     */
   
    public void testGetNombre() {
        System.out.println("getNombre");
        Producto instance = new Producto();
        String expResult = "";
        String result = instance.getNombre();
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setNombre method, of class Producto.
     */
    @Test
    public void testSetNombre() {
        System.out.println("setNombre");
        String nombre = "";
        Producto instance = new Producto();
        instance.setNombre(nombre);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getPrecio method, of class Producto.
     */
    @Test
    public void testGetPrecio() {
        System.out.println("getPrecio");
        Producto instance = new Producto();
        String expResult = "";
        String result = instance.getPrecio();
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setPrecio method, of class Producto.
     */
    @Test
    public void testSetPrecio() {
        System.out.println("setPrecio");
        String precio = "";
        Producto instance = new Producto();
        instance.setPrecio(precio);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getCantidad method, of class Producto.
     */
    @Test
    public void testGetCantidad() {
        System.out.println("getCantidad");
        Producto instance = new Producto();
        Integer expResult = null;
        Integer result = instance.getCantidad();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setCantidad method, of class Producto.
     */
    @Test
    public void testSetCantidad() {
        System.out.println("setCantidad");
        Integer cantidad = null;
        Producto instance = new Producto();
        instance.setCantidad(cantidad);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getTalla method, of class Producto.
     */
    @Test
    public void testGetTalla() {
        System.out.println("getTalla");
        Producto instance = new Producto();
        String expResult = "";
        String result = instance.getTalla();

        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setTalla method, of class Producto.
     */
    @Test
    public void testSetTalla() {
        System.out.println("setTalla");
        String talla = "";
        Producto instance = new Producto();
        instance.setTalla(talla);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getColor method, of class Producto.
     */
    @Test
    public void testGetColor() {
        System.out.println("getColor");
        Producto instance = new Producto();
        String expResult = "";
        String result = instance.getColor();

        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setColor method, of class Producto.
     */
    @Test
    public void testSetColor() {
        System.out.println("setColor");
        String color = "";
        Producto instance = new Producto();
        instance.setColor(color);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getGenero method, of class Producto.
     */
    @Test
    public void testGetGenero() {
        System.out.println("getGenero");
        Producto instance = new Producto();
        String expResult = "";
        String result = instance.getGenero();
    }

    /**
     * Test of setGenero method, of class Producto.
     */
    @Test
    public void testSetGenero() {
        System.out.println("setGenero");
        String genero = "";
        Producto instance = new Producto();
        instance.setGenero(genero);
        // TODO review the generated test code and remove the default call to fail.
    }
}
