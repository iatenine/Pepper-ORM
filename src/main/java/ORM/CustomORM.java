package ORM;

import logging.ORMLogger;

import java.sql.*;
import java.util.HashMap;
import java.util.Locale;

import static ORM.HelperOrm.buildColumn;

public class CustomORM{

    static Connection conn = null;

    public static boolean connect(){
        conn = JDBCConnection.getConnection();
        return conn != null;
    }

    public static boolean connect(String endpoint, String username, String password) {
            try{
                String url = "jdbc:postgresql://" + endpoint + "/postgres";
                Connection testConn = DriverManager.getConnection(url, username, password);
                if(testConn != null)
                    conn = testConn;
                return (conn instanceof JDBCConnection);
            } catch (SQLException e){
                ORMLogger.logger.error(e.getSQLState());
                for (StackTraceElement elem : e.getStackTrace()) {
                    ORMLogger.logger.info(elem);
                }
                return false;
            }
    }

    public static String buildTable(String tableName, HashMap<String, Class> columns) {
        // Protect against errors caused by spaces and eliminate all UPPERCASE..age
        tableName = tableName.replaceAll(" ", "_").toLowerCase(Locale.ROOT);
        String str = buildColumn(columns);
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "( id serial primary key "
                + str
                + ")";
        HelperOrm.executeStatement(conn, sql);
        return tableName;
    }

    public static void dropTable(String tableName){
        String sql = "Drop Table if exists " + tableName + ";";
        HelperOrm.executeStatement(conn, sql);
    }

    public static int addRow(String tableName, Object ...entries){
        return -1;
    }

    public static ResultSet getRow(String tableName, int id, String[] colNames){
        return null;
    }

    // update row
    public static ResultSet updateRow(String tableName, int id, HashMap<String, Object> newEntries){
        return null;
    }

    // delete row
    public static ResultSet deleteRow(String tableName, int id){
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM " + tableName + " WHERE id = " + id + ";";

            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            int count = 0;
            while(rs.next()){
                count++;
            }
            if(count == 1){
                String query = "DELETE FROM " + tableName + " WHERE id = " + id + ";";
                stmt.execute(query);
                
            } else {
                return null;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
}
