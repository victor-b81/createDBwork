/* // M... - �����
 *  // �... - ���� ����������
 *  // �... - ����������
 *
 * */

package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class workerClass {

    //M......... ���������� ������������� � ��
    public static void add_worker(String file_path, String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        Calendar calendar = Calendar.getInstance();                              // ������� ��������� ��� ���������� ����� ������ ������ start_work
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // ����������� ������ ��� ��� ���������
        //�.......... ���� ����������
        String line = "", separator = ";";
        int added_Users = 0;                                  // ������� ����������� �������������
        String[] worker_data;                                 // ������ ��� �������� ����� �� ����� �� ��������� �����
        List<String> fileReadWorkerList = new ArrayList<>();  // ��������� ��� �������� ����� �� �����-������ ����������
        Connection connect = null;                            // ���������� ������� ���������� � ��
        Statement stat = null;                                // ���������� ������� SQL �������� � ��
        //�..........................

        //P.......... ���� ����������
        try {
            BufferedReader br = new BufferedReader(new FileReader(file_path)); // ������ ����-������ ����������
            while ((line = br.readLine()) != null) {                           //
                fileReadWorkerList.add(line);                                  // ���������� ������ �� ����� � ��������
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try{
            connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql, usr_bd_sql, pass_bd_sql);       // ����������� � ��
            stat = connect.createStatement();                       // �������� ������� SQL ��������

            for (int i = 0 ; i < fileReadWorkerList.toArray().length; i++)                 // ........ � ����� �������� ������� ������������ � ������� worker ��
            {
                worker_data = fileReadWorkerList.get(i).split(separator);                   // ��������� ������ ����������� ";" �� ����� � ���������� �� � ������
                ResultSet rs = stat.executeQuery(
                        "select exists(select 1 from worker where first_name = '" + worker_data[0] +
                                "' and last_name = '" + worker_data[1] + "' and birth_date = '" + worker_data[2] + "');"); // ���������� ������ �� ������������� ��������� � ������� True/False
                if (rs.next()){                           // ������������ ������� �������� ������ �� ���������� ResultSet, if/while (resultSet.next){}
                    if (rs.getString("exists").equals("t")) {          // ���� �������� ����������, �������� �� ���� ����� ��������� ��������� � ������� ��
                        System.out.println("Worker Exist: " +
                                worker_data[0] + ";" +
                                worker_data[1] + ";" +
                                worker_data[2]);
                    } else {
                        stat.executeUpdate("INSERT INTO WORKER(first_name, last_name, birth_date, start_work)" +
                                " values(" + "'" + worker_data[0]+ "'" + ", " + "'" + worker_data[1] + "'" + ", " +
                                "'" + worker_data[2] + "'" + ", " + "'" + dateFormat.format(calendar.getTime()) + "'" + ");");     // ��������� ������������ � ������� worker ��
                        added_Users++;
                    }
                }
            }                                                                               // ..........
            System.out.println("Added Users :" + added_Users);
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            if (connect != null) {
                connect.close();
            }
            if (stat != null) {
                stat.close();
            }
        }
        //�..........................
    }
    // � ..........

    // M......... ���������� ������������� � ��
    public static void list_Worker(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {

        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {     //����������� � ��
            System.out.println("Workers: ");
            ResultSet rs = stat.executeQuery("SELECT * FROM worker;");                  // ���������� ������ ������ ���������� �� ������� worker
            while (rs.next()) {                                                             // ������� ����� ��
                System.out.print(rs.getString(1));
                System.out.print(" | ");
                System.out.print(rs.getString(2));
                System.out.print(" | ");
                System.out.print(rs.getString(3));
                System.out.print(" | ");
                System.out.println(rs.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // � ..........

    // M......... ��������� ��������� ���������� � ��
    public static void do_Manager(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        // �.......... ���� ����������
        boolean managerExist = false, worker_ok = false, exit = false;
        String id_w = "";
        // �..........

        list_Worker(name_db_sql, usr_bd_sql, pass_bd_sql);                  // �������� ������ ����������
        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // �������� ������ ����������
        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                   // �������� ������ ������ ����������

        // P.......... ���� ����������
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //������������ � ��

            ResultSet rs = stat.executeQuery("select * from worker;");                      //�������� ������ ����������
            Scanner sc = new Scanner(System.in);                                                //������������� ���� � ����������
            while (!worker_ok) {                                          // �������� ��������� �� ������������� � ����
                System.out.println("������� id ��������� ������������ ���������� (��� Q ��� �����): ");
                id_w = sc.nextLine();                                     // ������� ID ����������� �� ��������� ���������
                while (rs.next()) {                                       // ��������� ��� �������� ����������
                    if (rs.getString("id").equals(id_w)) worker_ok = true;
                }
                if (id_w.equals("Q")) {
                    exit = true;
                    break;
                }
            }

            if (!worker_ok && !exit) {                                      // ������ �����������, ���� ��������� ��� � ����
                System.out.println("worker not exist");
            } else if (worker_ok && !exit) {                                // �������� �������� �� �������� ����������
                rs = stat.executeQuery("select exists(select worker.id, manager.id_worker" +
                        " from worker, manager where manager.id_worker = '" + id_w + "' and worker.id = '" + id_w + "');"); // ������� ������� ������ �� ������������� id � ������� manager
                while (rs.next()) {                                         // ���� �������� ����������, �� ���������� True
                    if (rs.getString("exists").equals("t")) {
                        managerExist = true;
                    }
                }

                if (!managerExist) {
                    stat.execute("DELETE FROM other WHERE id_worker =" + id_w + ";");  // sql ������ �� �������� ��������� �� ���� ������
                    stat.executeUpdate("INSERT INTO MANAGER(ID_WORKER) values('" + id_w + "');"); // ��������� ID ��������� � ������� manager, �������� ��������� ����������
                    managerExist = false;
                } else if (managerExist) {                                  // ���� �������� ��� ����������, �� ������� �� ����
                    System.out.println("Manager already exist");
                }
            } else if (exit) {
                System.out.println("Exiting");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //�..........................
    }
    // M.........

    // M......... ��������� ��������� � ���������� ���������
    public static void do_subordinates(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        // �.......... ���� ����������
        String id_worker, id_manager;
        int id_manager_int = 0;
        boolean workerIdOk = false, managerIdOk = false, subordinatesOk = true;
        ResultSet rs;
        // �..........

        list_Worker(name_db_sql, usr_bd_sql, pass_bd_sql);                  // �������� ������ ����������
        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // �������� ������ ����������
        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                 // �������� ������ ������ ����������

        // P.......... ���� ����������
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //������������ � ��

            Scanner sc = new Scanner(System.in);                            //������������� ���� � ����������
            System.out.println("Choise id worker and id manager!");

            do {
                System.out.println("please choise id worker: ");
                id_worker = sc.nextLine();                                   // ������� id �������� �������� ������� ���������
                rs = stat.executeQuery("select * from worker;");   // ������� ��� ���� �� ������� �� worker
                while (rs.next()) {                                    // �������� ������������� id ���������
                    if (rs.getString("id").equals(id_worker)) {
                        workerIdOk = true;
                        System.out.println("Worker Exist");
                    }
                }
            } while (!workerIdOk);                                            // �������� � ����� �������� ���������� ID ���������

            do {
                System.out.println("please choise id manager: ");
                id_manager = sc.nextLine();                                   // ������� id ��������� �������� ������� ���������
                rs = stat.executeQuery("select * from manager;");   // ������� ��� ���� �� ������� �� manager
                while (rs.next()) {                                     // �������� ������������� id ���������
                    if (rs.getString("id_worker").equals(id_manager)) {
                        managerIdOk = true;
                        System.out.println("Manager Exist");
                        id_manager_int = rs.getInt("id");
                    }
                }
            } while (!managerIdOk);                                            // �������� � ����� �������� ���������� ID ���������

            rs = stat.executeQuery("select id_worker, id_manager from subordinates;");        //������� ���� �� ������� subordinates
            System.out.println("Check on Exists subordinates: ");
            while (rs.next()) {                                     // �������� ���������� �� ����� ��������-�������
                if (rs.getString("id_worker").equals(id_worker) && rs.getString("id_manager").equals(String.valueOf(id_manager_int))) {
                    subordinatesOk = false;
                    System.out.println("Subordinates allready exist");
                } else {
                    subordinatesOk = true;
                    System.out.println("Subordinates not exist");
                }
            }
            if (workerIdOk && managerIdOk && subordinatesOk){                 // ���� ��������, �������� ����������, � ����� ���� ��� �����, �� ������� �����
                stat.executeUpdate("INSERT INTO subordinates(id_worker, id_manager) values(" + id_worker + ",(select id from manager where manager.id_worker=" + id_manager + "));");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //�..........................
    }
    // M.........

    // M......... ����� ������� ���������
    public static void del_Worker(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {

        list_Worker(name_db_sql, usr_bd_sql, pass_bd_sql);                  // �������� ������ ����������
        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // �������� ������ ����������
        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                   // �������� ������ ������ ����������

        // P.......... ���� ����������
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //������������ � ��
            Scanner sc = new Scanner(System.in);                            //������������� ���� � ����������
            String id_worker;
            ResultSet rs;
            boolean workerIdOk = false;
            do {
                System.out.println("please choise id worker: ");
                id_worker = sc.nextLine();                                   // ������� id �������� �������� ������� ���������
                rs = stat.executeQuery("select * from worker;");   // ������� ��� ���� �� ������� �� worker
                while (rs.next()) {                                    // �������� ������������� id ���������
                    if (rs.getString("id").equals(id_worker)) {
                        workerIdOk = true;
                        System.out.println("Worker Exist");
                    }
                }
            } while (!workerIdOk);                                            // �������� � ����� �������� ���������� ID ���������
            if (workerIdOk) {                                                 // ���� �������� ���������� �� ������� ���
                stat.execute("DELETE FROM subordinates WHERE id_manager in (Select id from manager where manager.id_worker = " + id_worker + ");" +  // sql ������ �� �������� ��������� �� ���� ������
                        "DELETE FROM manager WHERE id_worker =" + id_worker + ";" +
                        "DELETE FROM other WHERE id_worker =" + id_worker + ";" +
                        "DELETE FROM worker WHERE id ='" + id_worker + "';");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // P..........
    }
    // M .........

    // M......... ����� ������� ���������
    public static void del_Manager(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        // �.......... ���� ����������
        String id_manager;
        boolean managerIsExist = false;
        ResultSet rs;
        Scanner sc = new Scanner(System.in);                            //������������� ���� � ����������
        // �..........

        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // �������� ������ ����������

        // P.......... ���� ����������
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                 usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //������������ � ��
                rs = stat.executeQuery("select * from manager;");   // ������� ��� ���� �� ������� �� manager
            do {
                System.out.println("please choise id manager: ");
                id_manager = sc.nextLine();                                   // ������� id ��������� �������� ������� ���������
                while (rs.next()) {                                     // �������� ������������� id ���������
                    if (rs.getString("id_worker").equals(id_manager)) {
                        managerIsExist = true;
                        System.out.println("Manager Exist");
                    }
                }
            } while (!managerIsExist);                                            // �������� � ����� �������� ���������� ID ���������

            if (managerIsExist) {                                                 // ���� �������� ���������� �� ������� ���
                stat.execute("DELETE FROM subordinates WHERE id_manager in (Select id from manager where manager.id_worker = " + id_manager + ");" +  // sql ������ �� �������� ��������� �� ������ �� ���� ������
                        "DELETE FROM manager WHERE id_worker =" + id_manager + ";");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // P..........
    }
    // M .........

    public static void del_Other(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        // �.......... ���� ����������
        String id_other;
        boolean managerIsExist = false;
        ResultSet rs;
        Scanner sc = new Scanner(System.in);                            //������������� ���� � ����������
        // �..........

        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                 // �������� ������ ����������

        // P.......... ���� ����������
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //������������ � ��
            rs = stat.executeQuery("select * from manager;");   // ������� ��� ���� �� ������� �� manager
            do {
                System.out.println("please choise id other: ");
                id_other = sc.nextLine();                                   // ������� id ��������� �������� ������� ���������
                while (rs.next()) {                                     // �������� ������������� id ���������
                    if (rs.getString("id_worker").equals(id_other)) {
                        managerIsExist = true;
                        System.out.println("Other Exist");
                    }
                }
            } while (!managerIsExist);                                            // �������� � ����� �������� ���������� ID ���������

            if (managerIsExist) {                                                 // ���� �������� ���������� �� ������� ���
                stat.execute("DELETE FROM other WHERE id_worker =" + id_other + ";");  // sql ������ �� �������� ��������� �� ������ �� ���� ������
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // P..........
    }
    // M .........

    // M......... ����� ��������� ��������� � ������� ������ Other
    public static void do_Other(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        list_Worker(name_db_sql, usr_bd_sql, pass_bd_sql);                  // �������� ������ ����������
        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // �������� ������ ����������
        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                   // �������� ������ ������ ����������

        String id_manager_str = null, descr_worker = null;
        ResultSet rs;
        String id_worker;
        boolean workerIdOk = false, otherIsExist = false, isExit = false;


        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {          //������������ � ��
            Scanner sc = new Scanner(System.in);                            //������������� ���� � ����������
            System.out.println("Choise id worker to do other!");

            do {
                System.out.println("please choise id worker: ");
                id_worker = sc.nextLine();                                   // ������� id �������� �������� ������� ���������
                rs = stat.executeQuery("select * from worker;");   // ������� ��� ���� �� ������� �� worker
                while (rs.next()) {                                    // �������� ������������� id ���������
                    if (rs.getString("id").equals(id_worker)) {
                        id_manager_str = rs.getString("id");
                        workerIdOk = true;
                        System.out.println("Worker Exist");
                    }
                }
            } while (!workerIdOk);                                                       // �������� � ����� �������� ���������� ID ���������

            if (workerIdOk) {
                rs = stat.executeQuery("select id_worker from other;");              // ������� ��� ���� �� ������� �� other
                while (rs.next()) {                                                      // �������� ������������� id
                    if (rs.getString("id_worker").equals(id_worker)) {
                        System.out.println("User allready exist!!!");
                        do {
                            System.out.println("Change User data? (y/n) (q- to exit): ");
                            id_worker = sc.nextLine();
                            if (id_worker.equals("y")) {                                // ������������� �������� �� ��������� � ������������
                                otherIsExist = false;
                                isExit = true;
                            } else if (id_worker.equals("n") | id_worker.equals("q")) {
                                otherIsExist = true;
                                isExit = true; }
                        } while (!isExit);
                    }
                }
            }


            if (!otherIsExist){
                rs = stat.executeQuery("select * from manager;");                    // ������� ��� ���� �� ������� �� manager
                while (rs.next()) {                                                      // �������� ������������� id ���������
                    if (rs.getString("id_worker").equals(id_manager_str)) {
                        stat.execute("DELETE FROM subordinates WHERE id_manager in (Select id from manager where manager.id_worker = " + id_worker + ");" +  // sql ������ �� �������� ��������� �� ���� ������ ����� worker
                                    "DELETE FROM manager WHERE id_worker =" + id_worker + ";");
                    }
                }
                System.out.println("Enter Description :");
                descr_worker = sc.nextLine();                                            // �������� �������� ������������
                stat.executeUpdate("INSERT INTO other(id_worker, id_manager) values(" + id_worker + "," + descr_worker + "));");    // �������� � ������� other ������������
            }
        }
    }
    // M .........

    // M......... ����� ������ ������ ����������
    public static void list_manager (String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {

        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql, usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {

            System.out.println("Managers: ");
            ResultSet SQLQuery = stat.executeQuery("select first_name, last_name, birth_date, id_worker from worker, manager where manager.id_worker = worker.id;");
            while (SQLQuery.next()) {
                System.out.println(SQLQuery.getString("id_worker") + " " + SQLQuery.getString("first_name") +
                        " " + SQLQuery.getString("last_name") + " " + SQLQuery.getString("birth_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // M.........

    // M......... ����� ������ ������ ������ ����������
    public static void list_Other(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {

        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql, usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {

            System.out.println("Other: ");
            ResultSet SQLQuery = stat.executeQuery("select first_name, last_name, birth_date, id_worker, descr_worker from worker, other where other.id_worker = worker.id;");
            while (SQLQuery.next()) {
                System.out.println(SQLQuery.getString("id_worker") + " " + SQLQuery.getString("first_name") +
                        " " + SQLQuery.getString("last_name") + " " + SQLQuery.getString("birth_date") +
                        " " + SQLQuery.getString("descr_worker"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // M.........

    // M......... ����� ������ ������ ���������� �� ���� ��������������
    public static void do_ListFirstDayWork(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        ResultSet SQLQuery;
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql, usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {
            System.out.println("ID | FIRST_NAME | LAST_NAME | BRTHDAY | FIRSTDAY");
            SQLQuery = stat.executeQuery("SELECT * FROM worker ORDER BY start_work DESC;");
            while (SQLQuery.next()){
                System.out.println(
                        SQLQuery.getString(1) + "; " +
                                SQLQuery.getString(2) + "; " +
                                SQLQuery.getString(3) + "; " +
                                SQLQuery.getString(4) + "; " +
                                SQLQuery.getString(5)
                );
            }
        }
    }
    // M.........

    // M......... ����� ������ ������ ���������� �� �������
    public static void do_ListLastName(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        ResultSet SQLQuery;
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql, usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {
            System.out.println("ID | FIRST_NAME | LAST_NAME | BRTHDAY | FIRSTDAY");
            SQLQuery = stat.executeQuery("SELECT * FROM worker ORDER BY last_name DESC;");
            while (SQLQuery.next()){
                System.out.println(
                        SQLQuery.getString(1) + "; " +
                                SQLQuery.getString(2) + "; " +
                                SQLQuery.getString(3) + "; " +
                                SQLQuery.getString(4) + "; " +
                                SQLQuery.getString(5)
                );
            }
        }
    }
    // M.........
}

