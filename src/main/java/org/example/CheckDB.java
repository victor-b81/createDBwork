package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CheckDB {
    // М .... Проверка и создание БД
    public static void check_table_db(String name_db_sql, String usr_bd_sql, String pass_bd_sql, HashMap<String, String> table_data, String[][] key_table_arr) throws SQLException {
        Connection connect = null;
        Statement stat = null;
        boolean check_table_exist = false, check_key = false, db_ok = false;
        ResultSet result_table;

        do {
            try {
                connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql, usr_bd_sql, pass_bd_sql); // Пробуем подключиться к БД
                System.out.println("Base is exist");        // если БД существует, то сообщаем об этом
                stat = connect.createStatement();
                for (Map.Entry<String, String> entry : table_data.entrySet()) {                            // Перебираем переданный список
                    result_table = stat.executeQuery("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = " + "'" + entry.getKey() + "'" + ");");       // Получение данных из базы
                    while (result_table.next()) {
                        check_table_exist = (result_table.getString("exists").equals("t")) ? true : false;
                    }
                    if (check_table_exist) {                                                                // если база существует
                        System.out.println(entry.getKey() + " is exist");                                   // Сообщаем, что база существует
                    } else {                                                                                // Если базы нет то создаем базу
                        System.out.println(entry.getKey() + " is NOT exist, Creating DB");
                        stat.execute("create table " + entry.getKey() + "(" + entry.getValue() + ");");
                        System.out.println("Table " + entry.getKey() + " created");
                    }
                }

                for (int i = 0; i <= key_table_arr.length - 1; i++) {                                        // Проверяю наличие требуемых ключей
                    ResultSet result_keys = stat.executeQuery("SELECT EXISTS(SELECT 1 FROM information_schema.table_constraints WHERE constraint_name='" + key_table_arr[i][0] + "' AND table_name='" + key_table_arr[i][1] + "');");
                    while (result_keys.next()) {
                        check_key = (result_keys.getString("exists").equals("t")) ? true : false;
                    }
                    if (check_key) {                                                                        // Если ключ есть, то сообщаем что ключ есть
                        System.out.println(key_table_arr[i][0] + " is exist");
                    } else {                                                                                // Если ключа нет, то содаем ключ.
                        System.out.println(key_table_arr[i][0] + " is NOT exist");
                        stat.execute(key_table_arr[i][2]);
                        System.out.println("Key " + key_table_arr[i][2] + " created");
                    }
                }
                db_ok = true;

            } catch (SQLException e) {
                if (e.getSQLState().equals("3D000")) {                                                      // Если базы нет, то создаем базу данных
                    System.out.println("Base is NOT exist");
                    connect = DriverManager.getConnection("jdbc:postgresql://localhost/", usr_bd_sql, pass_bd_sql);
                    stat = connect.createStatement();
                    stat.executeUpdate("create database " + name_db_sql + ";");                         // Команда создания БД
                    System.out.println("DB " + name_db_sql + " is created");

                } else {
                    System.out.println(e.getSQLState());
                }
            } finally {
                if (connect != null) {
                    connect.close();
                }
                if (stat != null) {
                    stat.close();
                }
            }
        } while (!db_ok);
    }
}