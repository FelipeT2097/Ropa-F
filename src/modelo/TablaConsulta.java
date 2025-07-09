/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author piper
 */
public class TablaConsulta extends AbstractTableModel{
    
    private String[] columns;
    private Object[][] rows;
    
    public TablaConsulta(){}
    
    public TablaConsulta(Object[][] data, String[] columnsName){
        this.columns = columnsName;
        this.rows = data;
    }
    
    public Class getColumnClass(int col)
    {
        // the index of the image column is 9
        if(col == 9){ return Icon.class; }
        
        else{
            return getValueAt(0, col).getClass();
        }
    }
    
    @Override
    public int getRowCount() {
    
        return this.rows.length;
        
    }

    @Override
    public int getColumnCount() {
    
        return this.columns.length;
        
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
    
        return this.rows[rowIndex][columnIndex];
        
    }
    
    @Override
    public String getColumnName(int col){
        
        return this.columns[col];
        
    }

}
