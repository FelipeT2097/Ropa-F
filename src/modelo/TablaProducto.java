/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author piper
 */
public class TablaProducto extends AbstractTableModel {
    private String[] columnNames;
    private Object[][] data;

    public TablaProducto(Object[][] data, String[] columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int index) {
        return columnNames[index];
    }

    @Override
    public Object getValueAt(int row, int column) {
        return data[row][column];
    }

    // Método para añadir el MouseListener a la JTable
   /* public void addMouseListenerToTable(JTable jTable_productos, controlador.Producto producto) {
        jTable_productos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Doble clic
                    int row = jTable_productos.getSelectedRow();
                    int column = jTable_productos.getSelectedColumn();

                    if (row != -1 && column != -1) {
                        Object currentValue = jTable_productos.getValueAt(row, column);
                        String newValue = JOptionPane.showInputDialog("Edita el valor:", currentValue);

                        if (newValue != null && !newValue.trim().isEmpty()) {
                            // Llama a updateProduct con el ID del producto y el nuevo valor
                            int productoId = (int) jTable_productos.getValueAt(row, 0); // Suponiendo que la primera columna es ID
                            
                            // Actualiza el producto usando el método correspondiente
                            try {
                                // Se asume que updateProduct tiene la lógica necesaria para actualizar el producto en la base de datos
                                producto.updateProduct(productoId, column, newValue); // Ajustar el índice de columna para la base de datos
                                jTable_productos.setValueAt(newValue, row, column); // Actualiza la celda visualmente
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Error al actualizar: " + ex.getMessage());
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "El valor no puede estar vacío.");
                        }
                    }
                }
            }
        });
    }*/
}
