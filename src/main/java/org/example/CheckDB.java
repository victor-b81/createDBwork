import java.sql.*;

public class CheckDB {

    public static Boolean check_db(String name_sql_bd, String usr_sql_bd, String pass_sql_bd) throws SQLException {
        boolean db_is_exist = false;
        Connection connect = null;
        try {
            connect = DriverManager.getConnection("jdbc:postgresql://localhost/"+name_sql_bd, usr_sql_bd, pass_sql_bd);
            db_is_exist = true;
            System.out.println("Base is exist");
        } catch (SQLException e){
            // e.printStackTrace();
            System.out.println("Base is NOT exist");
        }finally {
            if (connect != null) {
                connect.close();
            }
        }
    return db_is_exist;
    }

    public static Boolean check_table_db(String name_sql_bd, String usr_sql_bd, String pass_sql_bd, String sql_Table_Query) throws SQLException {
        boolean query_Ans = false;
        Connection connect = null;
        Statement stat = null;

        try {
            connect = DriverManager.getConnection("jdbc:postgresql://localhost/"+name_sql_bd,usr_sql_bd, pass_sql_bd);
            stat = connect.createStatement();
            ResultSet result = stat.executeQuery("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = " + "'" + sql_Table_Query + "'" + ");");
            while (result.next()){
                query_Ans = (result.getString("exists").equals("t"))? true : false;
            }
            if (query_Ans) System.out.println("Table " + sql_Table_Query +  " exist"); else System.out.println("Table " + sql_Table_Query +  " is NOT exist");
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (stat != null) {
                stat.close();
            }
            if (connect != null) {
                connect.close();
            }
        }
        return query_Ans;
    }
}
