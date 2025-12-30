/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.InputStream;

/**
 *
 * @author piper
 */
public class Utilidad {

    public static InputStream inputStreamReporte(String nombreFichero) {
        return Utilidad.class.getResourceAsStream("/reportes/" + nombreFichero);
    }

}
