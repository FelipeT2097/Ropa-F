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
    private static String password = "EllaNoTeAma20";
    
       static Connection con=null;
    public static Connection getConnection()
    {
        if (con != null) return con;
        // get db, user, pass from settings file
        return getConnection(dbname, username, password);
    }

    private static Connection getConnection(String db_name,String username,String password)
    {
        try
        {   //com.mysql.cj.jdbc.Driver Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost:33065/"+db_name+"?user="+username+"&password="+password);
            System.out.println("connected");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return con;     
    
    }
}
