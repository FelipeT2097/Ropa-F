/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportes;

import java.awt.Container;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import util.JrxmlFiltro;
import vista.ConsultasUsuarios;

/**
 *
 * @author piper
 */
public class VistaReportes {

    public VistaReportes() {

    }

    public static void mostrarReporte(String rutaReporte, Map parametros) throws Exception {
        try {
            // compila el reporte fichero .jrxml y lo convierte en fichero .jasper
            // InputStream io = this.getClass().getResourceAsStream("/agenda/vistas/reporte/informe_todas_personas.jrxml");
            JasperReport report = JasperCompileManager.compileReport(rutaReporte);
            // conexion a la base de datos
            //  ConexionBaseDatos basedatos = new ConexionBaseDatos();
            // se coencta al SMDB
            Connection con = modelo.ConexionDB.getConnection();
            //   PreparedStatement ps;

            // basedatos.conectar();
            // rellena el reporte con los datos de la bd, la pasa el reporte compilado, los parametros y la conexion a la BD
            JasperPrint print = JasperFillManager.fillReport(report, parametros, con);
            //cambia el true por false si no quiera el Dialogo de Impresion del SO
            JasperPrintManager.printReport(print, true);
            // Exporta el informe a PDF
            //JasperExportManager.exportReportToPdfFile(print,"C:\\reporte.pdf");
            //Para visualizar el pdf directamente desde java utilizando la misma venta que visualiza el reporte en el diseñador IReport
            JasperViewer visor = new JasperViewer(print);
            JasperViewer.viewReport(print, true);
            visor.setExtendedState(JFrame.MAXIMIZED_BOTH);
            ConsultasUsuarios.ventanaPrincipal.setExtendedState(JFrame.ICONIFIED);
        } catch (Exception e) {
            throw new Exception("Error al genrar el Reporte " + e.getMessage());
        }
    }

    public static Container mostrarReporte(InputStream ioReporte, Map parametros) throws Exception {
        try {
            // compila el reporte fichero .jrxml y lo convierte en fichero .jasper
            JasperReport reporte = JasperCompileManager.compileReport(ioReporte);
            // conexion a la base de datos
            // se conecta al SMDB
            Connection con = modelo.ConexionDB.getConnection();
            // se coencta al SMDB
            // basedatos.conectar();
            // rellena el reporte con los datos de la bd, la pasa el reporte compilado, los parametros y la conexion a la BD
            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, con);
            // Exporta el informe a PDF
            //JasperExportManager.exportReportToPdfFile(print,"C:\\reporte.pdf");
            //Para visualizar el pdf directamente desde java utilizando la misma venta que visualiza el reporte en el diseñador IReport
            //cambia el true por false si no quieres print dialog
            //JasperPrintManager.printReport(print, true);
            // Exporta el informe a PDF
            //JasperExportManager.exportReportToPdfFile(print,"C:\\reporte.pdf");
            //Para visualizar el pdf directamente desde java utilizando la misma venta que visualiza el reporte en el diseñador IReport

            JasperViewer visor = new JasperViewer(print);
            return visor.getContentPane();
            /*
              visor.viewReport(print, true);
              visor.setExtendedState(JFrame.MAXIMIZED_BOTH);
              VentanaAgenda.ventanPrincipal.setExtendedState(JFrame.ICONIFIED);
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al genrar el Reporte " + e.getMessage());
        }

    }

    public static void mostrarReporte(Map parametros) {
        // crea un ficehro carpeta por defecto
        File carpetaReportes = new File("reportes");
        // creo un objeto para trabajar con un navegador de ficheros (Abrir o guardad)
        JFileChooser ventanaAbrirFichero = new JFileChooser();
        // coloco el titulo de la ventana abrir
        ventanaAbrirFichero.setDialogTitle("Buscar el Reporte .jrxml");
        // cambio en nombre del boton Abrir por Seleccionar Reporte
        ventanaAbrirFichero.setApproveButtonText("Seleccionar Reporte");
        // coloco una rayita sobre la S sel nombre del boton
        ventanaAbrirFichero.setApproveButtonMnemonic('S');
        // Coloco un menesaje emergente cuadno coloco el raton sobre el boton abrir
        ventanaAbrirFichero.setApproveButtonToolTipText("Precionelo una vez este seleccionado el fichero .jrxml");
        // hago visible los controles de la ventana
        ventanaAbrirFichero.setControlButtonsAreShown(true);
        // le dpaso la carpeta en la que va a inicar la navegacion
        ventanaAbrirFichero.setCurrentDirectory(carpetaReportes);
        // creo un fichero para que lo coloque por defecto en la casilla nombre dle fichero a abrir
        File ficheroSeleccionadoDefault = new File("todos_contactos_s_ec.jrxml");
        // coloco el fichero a buscar por defecto
        ventanaAbrirFichero.setSelectedFile(ficheroSeleccionadoDefault);
        // coloco un objeto para filtrar los tipos de archivos que se pueden seleccionar
        // en este caso solo archivos de con extencion .jrxml
        ventanaAbrirFichero.setFileFilter(new JrxmlFiltro());
        // muestro la ventana de abrir
        int respusta = ventanaAbrirFichero.showOpenDialog(ConsultasUsuarios.ventanaPrincipal);
        // verifico si se oprimio el boton abrir o el boton cancelar (no entra al if)
        if (respusta == JFileChooser.APPROVE_OPTION) {
            // obtengo el fichero seleccinado desde la ventana abrir
            File ficheroSeleccionado = ventanaAbrirFichero.getSelectedFile();
            // obtengo el no,bre del fichero
            String nombreFicehro = ficheroSeleccionado.getName();
            // verifico so el nombre del fichero contiene la extencion .jrxml, iniciando la busqueda desde el final del nombre
            if (nombreFicehro.lastIndexOf(".jrxml") == -1) {
                // muestro un mensaje diciendo que el ficehro debe ser .jrxml
                JOptionPane.showMessageDialog(ventanaAbrirFichero, "Debes seleccionar un fichero .jrxml");
                ventanaAbrirFichero.showOpenDialog(ventanaAbrirFichero); // vuelvo a mostrar la ventana de abrir
            } else {
                try {
                    // llamo al metodo mostrarReporte y le paso la ruta competa del fichero seleccionado con al ventan abrir y le paso los parametros
                    mostrarReporte(ficheroSeleccionado.getAbsolutePath(), parametros);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ventanaAbrirFichero, ex.getMessage());
                }
            }

        }
    }
}
