/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author piper
 */
public class BackupDB {

    private static final String DB_NAME = "inventario_ropa_f";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final String DB_PORT = "3306";
    private static final String MYSQLDUMP_PATH = "C:\\xampp\\mysql\\bin\\mysqldump.exe";

    public static boolean realizarBackup() {
        try {
            File mysqldump = new File(MYSQLDUMP_PATH);
            if (!mysqldump.exists()) {
                JOptionPane.showMessageDialog(null,
                        "No se encontró mysqldump en: " + MYSQLDUMP_PATH,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Backup");

            String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            fileChooser.setSelectedFile(new File("backup_" + DB_NAME + "_" + fecha + ".sql"));

            int resultado = fileChooser.showSaveDialog(null);

            if (resultado == JFileChooser.APPROVE_OPTION) {
                String rutaDestino = fileChooser.getSelectedFile().getAbsolutePath();
                if (!rutaDestino.endsWith(".sql")) {
                    rutaDestino += ".sql";
                }

                // Construir comando con argumentos separados
                ProcessBuilder pb = new ProcessBuilder(
                        MYSQLDUMP_PATH,
                        "-u", DB_USER,
                        "-h", "127.0.0.1",
                        "-P", DB_PORT,
                        "--result-file=" + rutaDestino,
                        DB_NAME
                );

                pb.redirectErrorStream(true);
                Process proceso = pb.start();

                // Capturar salida/errores
                BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
                StringBuilder output = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    output.append(linea).append("\n");
                    System.out.println(linea); // Mostrar en consola
                }

                int exitCode = proceso.waitFor();

                if (exitCode == 0) {
                    // Verificar que el archivo se creó
                    File archivoBackup = new File(rutaDestino);
                    if (archivoBackup.exists() && archivoBackup.length() > 0) {
                        JOptionPane.showMessageDialog(null,
                                "Backup creado exitosamente en:\n" + rutaDestino,
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "El archivo de backup está vacío",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Error al crear backup.\nCódigo: " + exitCode + "\n" + output.toString(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
