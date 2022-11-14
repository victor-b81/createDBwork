package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static String db_name = "sftest";
    static String db_user = "postgres";
    static String db_passwd = "";

    public static void main(String[] args) throws SQLException {
        boolean workerIsExist = false, managerIsExist = false, dbIsExist = false, keyIsExist = false;
        String file_path = "src//main//resources//addworker.txt";

        HashMap<String, String> table_Collection = new HashMap<String, String>();
        String[][] key_table_arr = {
                {"manager_fk_worker", "manager", "ALTER TABLE \"manager\" ADD CONSTRAINT \"manager_fk_worker\" FOREIGN KEY (\"id_worker\") REFERENCES \"worker\"(\"id\");"},
                {"subordinates_fk_worker", "subordinates", "ALTER TABLE \"subordinates\" ADD CONSTRAINT \"subordinates_fk_worker\" FOREIGN KEY (\"id_worker\") REFERENCES \"worker\"(\"id\");"},
                {"subordinates_fk_manager", "subordinates", "ALTER TABLE \"subordinates\" ADD CONSTRAINT \"subordinates_fk_manager\" FOREIGN KEY (\"id_manager\") REFERENCES \"manager\"(\"id\");"},
                {"other_fk_worker", "other", "ALTER TABLE \"other\" ADD CONSTRAINT \"other_fk_worker\" FOREIGN KEY (\"id_worker\") REFERENCES \"worker\"(\"id\");"}
        };

        table_Collection.put("worker", "ID SERIAL PRIMARY KEY NOT NULL, " +
                "first_name VARCHAR(255) NOT NULL, " +
                "last_name VARCHAR(255) NOT NULL, " +
                "birth_date DATE NOT NULL, " +
                "start_work DATE NOT NULL ");

        table_Collection.put("manager", "ID SERIAL PRIMARY KEY NOT NULL, " +
                "ID_WORKER INT, ");

        table_Collection.put("subordinates", "ID SERIAL PRIMARY KEY NOT NULL, " +
                "id_worker int NOT NULL, " +
                "id_manager int NOT NULL ");

        table_Collection.put("other", "ID SERIAL PRIMARY KEY NOT NULL, " +
                "id_worker int NOT NULL, " +
                "descr_worker text ");

        Scanner sc = new Scanner(System.in);

        System.out.println("1. Check Base \n"+
                "2. Input Worker \n" +
                "3. List Worker \n" +
                "4. Do manager \n" +
                "5. Do Subordinates \n" +
                "6. List workers on FirstDayWork \n" +
                "7. List workers on LastName \n" +
                "8. Delete User \n" +
                "9. Do Other \n" +
                "10. Delete Manager \n" +
                "11. Delete Other \n" +
                "12. Exit \n" +
                "Choise the number: ");

        String menu_select = sc.nextLine();

        switch (menu_select){
            case "1":
                CheckDB.check_table_db(db_name, db_user, db_passwd, table_Collection, key_table_arr);
                break;
            case "2":
                    workerClass.add_worker(file_path, db_name, db_user, db_passwd);
                break;
            case "3":
                workerClass.list_Worker(db_name, db_user, db_passwd);
                break;
            case "4":
                workerClass.do_Manager(db_name, db_user, db_passwd);
                break;
            case "5":
                workerClass.do_subordinates(db_name, db_user, db_passwd);
                break;
            case "6":
                workerClass.do_ListFirstDayWork(db_name, db_user, db_passwd);
                break;
            case "7":
                workerClass.do_ListLastName(db_name, db_user, db_passwd);
                break;
            case "8":
                workerClass.del_Worker(db_name, db_user, db_passwd);
                break;
            case "9":
                workerClass.do_Other(db_name, db_user, db_passwd);
                break;
            case "10":
                workerClass.del_Manager(db_name, db_user, db_passwd);
                break;
            case "11":
                workerClass.del_Other(db_name, db_user, db_passwd);
                break;
            case "12":
                break;
            default:
                break;
        }
    }
}