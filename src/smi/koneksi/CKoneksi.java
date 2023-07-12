package smi.koneksi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class CKoneksi {private static Connection koneksi;
     public static Connection getKoneksi(){
         if (koneksi == null ){
             try {
                 String dbURL = "jdbc:mysql://localhost:3306/db_smi";
                 String user = "root";
                 String password = "";
                 koneksi = DriverManager.getConnection(dbURL,user,password);
                 System.out.println("koneksi berhasil");
             } catch (SQLException e) {
                 System.out.println("error:"+e);
             }
         }
         return koneksi;
     }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        getKoneksi();
    }
  
    
}
