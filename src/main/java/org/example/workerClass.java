/* // M... - метод
 *  // П... - блок переменных
 *  // Р... - реализация
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

    //M......... Добавление пользователей в БД
    public static void add_worker(String file_path, String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        Calendar calendar = Calendar.getInstance();                              // Создаем календарь для заполнения полей начала работы start_work
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Настраиваем формат дат для календаря
        //П.......... блок переменных
        String line = "", separator = ";";
        int added_Users = 0;                                  // Счетчик добавленных пользователей
        String[] worker_data;                                 // Массив для разбивки строк из файла на отдельные блоки
        List<String> fileReadWorkerList = new ArrayList<>();  // Коллекция для хранения строк из файла-списка работников
        Connection connect = null;                            // Обьявление обьекта соединения с БД
        Statement stat = null;                                // Обьявление обьекта SQL запросов к БД
        //П..........................

        //P.......... блок реализации
        try {
            BufferedReader br = new BufferedReader(new FileReader(file_path)); // Читаем фаил-список работников
            while ((line = br.readLine()) != null) {                           //
                fileReadWorkerList.add(line);                                  // Записываем данные из файла в Колекцию
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try{
            connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql, usr_bd_sql, pass_bd_sql);       // Поединяемся с БД
            stat = connect.createStatement();                       // Создание обьекта SQL запросов

            for (int i = 0 ; i < fileReadWorkerList.toArray().length; i++)                 // ........ В цикле выявляем наличие пользователя в таблице worker БД
            {
                worker_data = fileReadWorkerList.get(i).split(separator);                   // Разбиваем строки разделенные ";" на блоки и записываем их в массив
                ResultSet rs = stat.executeQuery(
                        "select exists(select 1 from worker where first_name = '" + worker_data[0] +
                                "' and last_name = '" + worker_data[1] + "' and birth_date = '" + worker_data[2] + "');"); // Отправляем запрос на существование работника с ответом True/False
                if (rs.next()){                           // Обязательное условие полчения данных из интерфейса ResultSet, if/while (resultSet.next){}
                    if (rs.getString("exists").equals("t")) {          // Если работник существует, сообщить об этом иначе добавляем работника в таблицу БД
                        System.out.println("Worker Exist: " +
                                worker_data[0] + ";" +
                                worker_data[1] + ";" +
                                worker_data[2]);
                    } else {
                        stat.executeUpdate("INSERT INTO WORKER(first_name, last_name, birth_date, start_work)" +
                                " values(" + "'" + worker_data[0]+ "'" + ", " + "'" + worker_data[1] + "'" + ", " +
                                "'" + worker_data[2] + "'" + ", " + "'" + dateFormat.format(calendar.getTime()) + "'" + ");");     // добавляем пользователя в таблицу worker БД
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
        //Р..........................
    }
    // М ..........

    // M......... Отобразить пользователей в БД
    public static void list_Worker(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {

        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {     //подключение к БД
            System.out.println("Workers: ");
            ResultSet rs = stat.executeQuery("SELECT * FROM worker;");                  // Отправляем запрос списка работников из таблици worker
            while (rs.next()) {                                                             // Выводим ответ БД
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
    // М ..........

    // M......... Назначаем работника менеджером в БД
    public static void do_Manager(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        // П.......... блок переменных
        boolean managerExist = false, worker_ok = false, exit = false;
        String id_w = "";
        // П..........

        list_Worker(name_db_sql, usr_bd_sql, pass_bd_sql);                  // вызываем список работников
        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // вызываем список менеджеров
        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                   // вызываем список других работников

        // P.......... блок реализации
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //Подключаемся к БД

            ResultSet rs = stat.executeQuery("select * from worker;");                      //Получаем список работников
            Scanner sc = new Scanner(System.in);                                                //Инициализирую ввод с клавиатуры
            while (!worker_ok) {                                          // Проверка работника на существование в базе
                System.out.println("Введите id работника назначаемого менеджером (или Q для выход): ");
                id_w = sc.nextLine();                                     // получаю ID претендента на должность менеджера
                while (rs.next()) {                                       // Убеждаюсь что работник существует
                    if (rs.getString("id").equals(id_w)) worker_ok = true;
                }
                if (id_w.equals("Q")) {
                    exit = true;
                    break;
                }
            }

            if (!worker_ok && !exit) {                                      // вывожу уведомление, если работника нет в базе
                System.out.println("worker not exist");
            } else if (worker_ok && !exit) {                                // проверяю является ли работник менеджером
                rs = stat.executeQuery("select exists(select worker.id, manager.id_worker" +
                        " from worker, manager where manager.id_worker = '" + id_w + "' and worker.id = '" + id_w + "');"); // Посылаю сложный запрос на существование id в таблице manager
                while (rs.next()) {                                         // если менеджер существует, то присваиваю True
                    if (rs.getString("exists").equals("t")) {
                        managerExist = true;
                    }
                }

                if (!managerExist) {
                    stat.execute("DELETE FROM other WHERE id_worker =" + id_w + ";");  // sql запрос на удаление работника из всех таблиц
                    stat.executeUpdate("INSERT INTO MANAGER(ID_WORKER) values('" + id_w + "');"); // Записываю ID работчего в таблицу manager, назначая работника менеджером
                    managerExist = false;
                } else if (managerExist) {                                  // если менеджер уже существует, то сообщаю об этом
                    System.out.println("Manager already exist");
                }
            } else if (exit) {
                System.out.println("Exiting");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Р..........................
    }
    // M.........

    // M......... Назначаем работника в подчинение менеджеру
    public static void do_subordinates(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        // П.......... блок переменных
        String id_worker, id_manager;
        int id_manager_int = 0;
        boolean workerIdOk = false, managerIdOk = false, subordinatesOk = true;
        ResultSet rs;
        // П..........

        list_Worker(name_db_sql, usr_bd_sql, pass_bd_sql);                  // вызываем список работников
        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // вызываем список менеджеров
        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                 // вызываем список других работников

        // P.......... блок реализации
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //Подключаемся к БД

            Scanner sc = new Scanner(System.in);                            //Инициализирую ввод с клавиатуры
            System.out.println("Choise id worker and id manager!");

            do {
                System.out.println("please choise id worker: ");
                id_worker = sc.nextLine();                                   // Получаю id рабочего которому назначу менеджера
                rs = stat.executeQuery("select * from worker;");   // Получаю все поля из таблицы БД worker
                while (rs.next()) {                                    // Проверяю существование id работника
                    if (rs.getString("id").equals(id_worker)) {
                        workerIdOk = true;
                        System.out.println("Worker Exist");
                    }
                }
            } while (!workerIdOk);                                            // Проверяю в цикле верность указанного ID работника

            do {
                System.out.println("please choise id manager: ");
                id_manager = sc.nextLine();                                   // Получаю id менеджера которому назначу работника
                rs = stat.executeQuery("select * from manager;");   // Получаю все поля из таблицы БД manager
                while (rs.next()) {                                     // Проверяю существование id менеджера
                    if (rs.getString("id_worker").equals(id_manager)) {
                        managerIdOk = true;
                        System.out.println("Manager Exist");
                        id_manager_int = rs.getInt("id");
                    }
                }
            } while (!managerIdOk);                                            // Проверяю в цикле верность указанного ID менеждера

            rs = stat.executeQuery("select id_worker, id_manager from subordinates;");        //Получаю поля из таблици subordinates
            System.out.println("Check on Exists subordinates: ");
            while (rs.next()) {                                     // Проверяю существует ли всязь менеджер-рабочий
                if (rs.getString("id_worker").equals(id_worker) && rs.getString("id_manager").equals(String.valueOf(id_manager_int))) {
                    subordinatesOk = false;
                    System.out.println("Subordinates allready exist");
                } else {
                    subordinatesOk = true;
                    System.out.println("Subordinates not exist");
                }
            }
            if (workerIdOk && managerIdOk && subordinatesOk){                 // Если работник, менеджер существуют, и между ними нет связи, то создаем связь
                stat.executeUpdate("INSERT INTO subordinates(id_worker, id_manager) values(" + id_worker + ",(select id from manager where manager.id_worker=" + id_manager + "));");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Р..........................
    }
    // M.........

    // M......... Метод Удаляем работника
    public static void del_Worker(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {

        list_Worker(name_db_sql, usr_bd_sql, pass_bd_sql);                  // вызываем список работников
        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // вызываем список менеджеров
        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                   // вызываем список других работников

        // P.......... блок реализации
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //Подключаемся к БД
            Scanner sc = new Scanner(System.in);                            //Инициализирую ввод с клавиатуры
            String id_worker;
            ResultSet rs;
            boolean workerIdOk = false;
            do {
                System.out.println("please choise id worker: ");
                id_worker = sc.nextLine();                                   // Получаю id рабочего которому назначу менеджера
                rs = stat.executeQuery("select * from worker;");   // Получаю все поля из таблицы БД worker
                while (rs.next()) {                                    // Проверяю существование id работника
                    if (rs.getString("id").equals(id_worker)) {
                        workerIdOk = true;
                        System.out.println("Worker Exist");
                    }
                }
            } while (!workerIdOk);                                            // Проверяю в цикле верность указанного ID работника
            if (workerIdOk) {                                                 // Если работник существует то удаляем его
                stat.execute("DELETE FROM subordinates WHERE id_manager in (Select id from manager where manager.id_worker = " + id_worker + ");" +  // sql запрос на удаление работника из всех таблиц
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

    // M......... Метод Удаляем менеджера
    public static void del_Manager(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        // П.......... блок переменных
        String id_manager;
        boolean managerIsExist = false;
        ResultSet rs;
        Scanner sc = new Scanner(System.in);                            //Инициализирую ввод с клавиатуры
        // П..........

        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // вызываем список менеджеров

        // P.......... блок реализации
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                 usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //Подключаемся к БД
                rs = stat.executeQuery("select * from manager;");   // Получаю все поля из таблицы БД manager
            do {
                System.out.println("please choise id manager: ");
                id_manager = sc.nextLine();                                   // Получаю id менеджера которому назначу работника
                while (rs.next()) {                                     // Проверяю существование id менеджера
                    if (rs.getString("id_worker").equals(id_manager)) {
                        managerIsExist = true;
                        System.out.println("Manager Exist");
                    }
                }
            } while (!managerIsExist);                                            // Проверяю в цикле верность указанного ID менеждера

            if (managerIsExist) {                                                 // Если работник существует то удаляем его
                stat.execute("DELETE FROM subordinates WHERE id_manager in (Select id from manager where manager.id_worker = " + id_manager + ");" +  // sql запрос на удаление менеджера из таблиц из всех таблиц
                        "DELETE FROM manager WHERE id_worker =" + id_manager + ";");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // P..........
    }
    // M .........

    public static void del_Other(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        // П.......... блок переменных
        String id_other;
        boolean managerIsExist = false;
        ResultSet rs;
        Scanner sc = new Scanner(System.in);                            //Инициализирую ввод с клавиатуры
        // П..........

        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                 // вызываем список менеджеров

        // P.......... блок реализации
        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {         //Подключаемся к БД
            rs = stat.executeQuery("select * from manager;");   // Получаю все поля из таблицы БД manager
            do {
                System.out.println("please choise id other: ");
                id_other = sc.nextLine();                                   // Получаю id менеджера которому назначу работника
                while (rs.next()) {                                     // Проверяю существование id менеджера
                    if (rs.getString("id_worker").equals(id_other)) {
                        managerIsExist = true;
                        System.out.println("Other Exist");
                    }
                }
            } while (!managerIsExist);                                            // Проверяю в цикле верность указанного ID менеждера

            if (managerIsExist) {                                                 // Если работник существует то удаляем его
                stat.execute("DELETE FROM other WHERE id_worker =" + id_other + ";");  // sql запрос на удаление менеджера из таблиц из всех таблиц
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // P..........
    }
    // M .........

    // M......... Метод Назначаем работника В таблицу Другие Other
    public static void do_Other(String name_db_sql, String usr_bd_sql, String pass_bd_sql) throws SQLException {
        list_Worker(name_db_sql, usr_bd_sql, pass_bd_sql);                  // вызываем список работников
        list_manager(name_db_sql, usr_bd_sql, pass_bd_sql);                 // вызываем список менеджеров
        list_Other(name_db_sql, usr_bd_sql, pass_bd_sql);                   // вызываем список других работников

        String id_manager_str = null, descr_worker = null;
        ResultSet rs;
        String id_worker;
        boolean workerIdOk = false, otherIsExist = false, isExit = false;


        try (Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + name_db_sql,
                usr_bd_sql, pass_bd_sql); Statement stat = connect.createStatement()) {          //Подключаемся к БД
            Scanner sc = new Scanner(System.in);                            //Инициализирую ввод с клавиатуры
            System.out.println("Choise id worker to do other!");

            do {
                System.out.println("please choise id worker: ");
                id_worker = sc.nextLine();                                   // Получаю id рабочего которому назначу менеджера
                rs = stat.executeQuery("select * from worker;");   // Получаю все поля из таблицы БД worker
                while (rs.next()) {                                    // Проверяю существование id работника
                    if (rs.getString("id").equals(id_worker)) {
                        id_manager_str = rs.getString("id");
                        workerIdOk = true;
                        System.out.println("Worker Exist");
                    }
                }
            } while (!workerIdOk);                                                       // Проверяю в цикле верность указанного ID работника

            if (workerIdOk) {
                rs = stat.executeQuery("select id_worker from other;");              // Получаю все поля из таблицы БД other
                while (rs.next()) {                                                      // Проверяю существование id
                    if (rs.getString("id_worker").equals(id_worker)) {
                        System.out.println("User allready exist!!!");
                        do {
                            System.out.println("Change User data? (y/n) (q- to exit): ");
                            id_worker = sc.nextLine();
                            if (id_worker.equals("y")) {                                // Подтверждение действий на изменение в пользователе
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
                rs = stat.executeQuery("select * from manager;");                    // Получаю все поля из таблицы БД manager
                while (rs.next()) {                                                      // Проверяю существование id менеджера
                    if (rs.getString("id_worker").equals(id_manager_str)) {
                        stat.execute("DELETE FROM subordinates WHERE id_manager in (Select id from manager where manager.id_worker = " + id_worker + ");" +  // sql запрос на удаление работника из всех таблиц кроме worker
                                    "DELETE FROM manager WHERE id_worker =" + id_worker + ";");
                    }
                }
                System.out.println("Enter Description :");
                descr_worker = sc.nextLine();                                            // Получаем описание пользователя
                stat.executeUpdate("INSERT INTO other(id_worker, id_manager) values(" + id_worker + "," + descr_worker + "));");    // Добавляю в таблицу other пользователя
            }
        }
    }
    // M .........

    // M......... Метод вывода списка менеджеров
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

    // M......... Метод вывода списка Других работников
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

    // M......... Метод вывода списка работников по дате трудоустроства
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

    // M......... Метод вывода списка работников по фамилии
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

