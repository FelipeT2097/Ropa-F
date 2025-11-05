/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author piper
 */
public class Conexion_DB {
    private static String dbname = "inventario_ropa_f";
    private static String username = "root";
    private static String password = " ";
      
    public static Connection getConnection() {
        Connection con = null;  // Variable LOCAL, no estática
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:33065/" + dbname + 
                "?user=" + username + "&password=" + password
            );
            System.out.println("connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}
