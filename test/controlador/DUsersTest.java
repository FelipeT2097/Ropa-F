/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author piper
 */
public class DUsersTest {

    public DUsersTest() {
    }

    @Test
    public void testGetId() {
        System.out.println("getId");
        DUsers instance = new DUsers();
        Integer expResult = 1; // Valor esperado para el ID
        instance.setId(expResult); // Asegúrate de establecer el ID
        Integer result = instance.getId();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetId() {
        System.out.println("setId");
        Integer id = 1; // ID válido para probar
        DUsers instance = new DUsers();
        instance.setId(id);
        assertEquals(id, instance.getId()); // Verificamos que se haya establecido correctamente
    }

    @Test
    public void testGetNombreCompleto() {
        System.out.println("getNombreCompleto");
        DUsers instance = new DUsers();
        String expResult = "Juan Pérez"; // Nombre esperado
        instance.setNombreCompleto(expResult); // Establecer nombre completo
        String result = instance.getNombreCompleto();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetNombreCompleto() {
        System.out.println("setNombreCompleto");
        String nombreCompleto = "Juan Pérez"; // Nombre esperado
        DUsers instance = new DUsers();
        instance.setNombreCompleto(nombreCompleto);
        assertEquals(nombreCompleto, instance.getNombreCompleto()); // Verificación
    }

    @Test
    public void testGetNombreUsuario() {
        System.out.println("getNombreUsuario");
        DUsers instance = new DUsers();
        String expResult = "juanperez"; // Usuario esperado
        instance.setNombreUsuario(expResult); // Establecer nombre de usuario
        String result = instance.getNombreUsuario();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetNombreUsuario() {
        System.out.println("setNombreUsuario");
        String nombreUsuario = "juanperez"; // Usuario esperado
        DUsers instance = new DUsers();
        instance.setNombreUsuario(nombreUsuario);
        assertEquals(nombreUsuario, instance.getNombreUsuario()); // Verificación
    }

    @Test
    public void testGetTipoDocumento() {
        System.out.println("getTipoDocumento");
        DUsers instance = new DUsers();
        String expResult = "Cédula"; // Tipo de documento esperado
        instance.setTipoDocumento(expResult);
        String result = instance.getTipoDocumento();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetTipoDocumento() {
        System.out.println("setTipoDocumento");
        String tipoDocumento = "Cédula"; // Tipo de documento esperado
        DUsers instance = new DUsers();
        instance.setTipoDocumento(tipoDocumento);
        assertEquals(tipoDocumento, instance.getTipoDocumento()); // Verificación
    }

    @Test
    public void testGetNumeroDocumento() {
        System.out.println("getNumeroDocumento");
        DUsers instance = new DUsers();
        String expResult = "123456789"; // Número de documento esperado
        instance.setNumeroDocumento(expResult);
        String result = instance.getNumeroDocumento();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetNumeroDocumento() {
        System.out.println("setNumeroDocumento");
        String numeroDocumento = "123456789"; // Número de documento esperado
        DUsers instance = new DUsers();
        instance.setNumeroDocumento(numeroDocumento);
        assertEquals(numeroDocumento, instance.getNumeroDocumento()); // Verificación
    }

    @Test
    public void testGetGenero() {
        System.out.println("getGenero");
        DUsers instance = new DUsers();
        String expResult = "Masculino"; // Género esperado
        instance.setGenero(expResult);
        String result = instance.getGenero();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetGenero() {
        System.out.println("setGenero");
        String genero = "Masculino"; // Género esperado
        DUsers instance = new DUsers();
        instance.setGenero(genero);
        assertEquals(genero, instance.getGenero()); // Verificación
    }

    @Test
    public void testGetTelefono() {
        System.out.println("getTelefono");
        DUsers instance = new DUsers();
        String expResult = "3001234567"; // Teléfono esperado
        instance.setTelefono(expResult);
        String result = instance.getTelefono();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetTelefono() {
        System.out.println("setTelefono");
        String telefono = "3001234567"; // Teléfono esperado
        DUsers instance = new DUsers();
        instance.setTelefono(telefono);
        assertEquals(telefono, instance.getTelefono()); // Verificación
    }

    @Test
    public void testGetCorreoElectronico() {
        System.out.println("getCorreoElectronico");
        DUsers instance = new DUsers();
        String expResult = "juan@correo.com"; // Correo esperado
        instance.setCorreoElectronico(expResult);
        String result = instance.getCorreoElectronico();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetCorreoElectronico() {
        System.out.println("setCorreoElectronico");
        String correoElectronico = "juan@correo.com"; // Correo esperado
        DUsers instance = new DUsers();
        instance.setCorreoElectronico(correoElectronico);
        assertEquals(correoElectronico, instance.getCorreoElectronico()); // Verificación
    }

    @Test
    public void testGetContraseña() {
        System.out.println("getContraseña");
        DUsers instance = new DUsers();
        String expResult = "contraseña123"; // Contraseña esperada
        instance.setContraseña(expResult);
        String result = instance.getContraseña();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetContraseña() {
        System.out.println("setContraseña");
        String contraseña = "contraseña123"; // Contraseña esperada
        DUsers instance = new DUsers();
        instance.setContraseña(contraseña);
        assertEquals(contraseña, instance.getContraseña()); // Verificación
    }

    @Test
    public void testInsertUser() {
        System.out.println("insertUser");
        DUsers usuario = new DUsers();
        usuario.setId(1);
        usuario.setNombreCompleto("Juan Pepito");
        usuario.setNombreUsuario("Juanty");
        usuario.setTipoDocumento("Cédula de Ciudadanía");
        usuario.setNumeroDocumento("12345079");
        usuario.setGenero("Hombre");
        usuario.setTelefono("300012467");
        usuario.setCorreoElectronico("juanpeiñ@correo.com");
        usuario.setContraseña("contraseña123");
        DUsers.insertUser(usuario); // Aquí deberías hacer un mock si interactúa con BD
        // Verifica que el método haga lo esperado (mocking de la base de datos)
    }

    @Test
    public void testUsersList() {
        System.out.println("UsersList");
        String user = "juanperez"; // Usuario para buscar
        DUsers instance = new DUsers();
        ArrayList<DUsers> expResult = new ArrayList<>();
        expResult.add(new DUsers(1, "Juan Pérez", "juanperez", "Cédula ciudadania", "123456789", "Masculino", "3001234567", "juan@correo.com", "contraseña123"));

    }

    @Test
    public void testMostrarUsuarios() {
        System.out.println("mostrarUsuarios");
        DUsers misUsuarios = new DUsers();
        DUsers instance = new DUsers();
        DefaultTableModel expResult = new DefaultTableModel();
        expResult.addColumn("ID");
        expResult.addColumn("Nombre Completo");
        expResult.addColumn("Nombre Usuario");
        expResult.addColumn("Tipo Documento");
        expResult.addColumn("Numero Documento");
        expResult.addColumn("Genero");
        expResult.addColumn("Correo Electronico");
        expResult.addColumn("Contraseña");
        expResult.addRow(new Object[]{1, "Juan Pérez"});
        DefaultTableModel result = instance.mostrarUsuarios(misUsuarios);
        
    }

    @Test
    public void testActualizarUsuarios() {
        System.out.println("actualizarUsuarios");
        DUsers misUsuarios = new DUsers();
        misUsuarios.setId(1);
        misUsuarios.setNombreCompleto("Juan Pérez");
        DUsers instance = new DUsers();
        instance.actualizarUsuarios(misUsuarios);
        // Verifica si los datos han sido actualizados en la base de datos o en el modelo
    }

    @Test
    public void testEliminarUsuarios() {
        System.out.println("eliminarUsuarios");
        Integer id = 1; // ID del usuario que se va a eliminar
        DUsers.eliminarUsuarios(id);
        // Verifica si el usuario con el ID proporcionado ha sido eliminado correctamente
    }
}
