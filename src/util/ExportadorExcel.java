/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JTable;
import javax.swing.JFileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author piper
 */
public class ExportadorExcel {
     public static void exportarTabla(JTable tabla, String nombreHoja) throws Exception {

        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet(nombreHoja);

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < tabla.getColumnCount(); col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(tabla.getColumnName(col));
        }

        // Crear filas con datos
        for (int row = 0; row < tabla.getRowCount(); row++) {
            Row dataRow = sheet.createRow(row + 1);
            for (int col = 0; col < tabla.getColumnCount(); col++) {
                Object value = tabla.getValueAt(row, col);
                dataRow.createCell(col).setCellValue(
                        value == null ? "" : value.toString()
                );
            }
        }

        // Cuadro de guardar archivo
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar archivo Excel");
        chooser.setSelectedFile(new File("Reporte.xlsx"));

        int userSelection = chooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            FileOutputStream out = new FileOutputStream(file);
            book.write(out);
            out.close();
            book.close();
        }
    }
}
