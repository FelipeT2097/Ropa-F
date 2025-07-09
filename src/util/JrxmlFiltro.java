/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import javax.swing.filechooser.FileFilter;


public class JrxmlFiltro  extends FileFilter  {
    
    
     final static String extencionReporte = "jrxml";

    public JrxmlFiltro() {
        super();
    }


    // Accept all directories and all gif, jpg, or tiff files.
    public boolean accept(File archivoReporte) {

        if (archivoReporte.isDirectory()) {
            return true;
        }

        String s = archivoReporte.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (extencionReporte.equals(extension) ) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    // The description of this filter
    public String getDescription() {
        return "Fichero de Reportes JasperReport";
    }
    
    
    
}
