package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class addWorker {
    public static void add_worker(String file_path){
        String line = "", separator = ";";
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(file_path));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] worker_data = line.split(separator);    // use comma as separator
                System.out.println("worker First Name = " + worker_data[0] + ", Last Name = " + worker_data[1]);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}

