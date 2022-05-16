/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package barserver;

import java.net.*;
import java.io.*;
import static java.lang.Integer.parseInt;
import java.sql.*;
import java.lang.String;
import java.util.Calendar;

/**
 *
 * @author 9ussell7hang
 */
public class BarServer {
    static Connection connection = null;
    static Statement statement = null;
    static ServerSocket ss;


    public static void main(String[] args) {
        
        createTable();
        insertValues();



        try{
            serverSetUp();
        }catch(IOException e){
            System.out.println(e);
        }
        

        
       
    }
    
    public static void createInvoice(String name, String[] details){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int secs = c.get(Calendar.SECOND);
        String[][] order = new String[details.length/4][2];
        int total = 0;
        for (int i = 0; i < details.length; i+= 4){
            order[i/4][0] = details[i];
            order[i/4][1] = details[i+2];
            total += (parseInt(details[i+2]) * parseInt(details[i+1])); 
        }
        try{
            try (PrintWriter writer = new PrintWriter(name+"_"+ month+ "_" + day + "_" + year + "_" + secs + ".txt", "UTF-8")) {
                for (String[] order1 : order) {
                 writer.println(order1[1]+ "ct" + "\t" + order1[0]);
                }
                
                writer.println("\n\nTotal due:  "+ total+ ".00 USD");

                writer.println("At\t"+ hour + ":" + minute + "EDT");
            }
            
        }catch(FileNotFoundException | UnsupportedEncodingException e){
            System.out.println(e);
        }

    }
    public static void serverSetUp() throws IOException{
        
        ss = new ServerSocket(1297);
        while(!ss.isClosed()){
            
            Socket s = ss.accept();
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int sec = c.get(Calendar.SECOND);
            int m = c.get(Calendar.MINUTE); //minute
            int h = c.get(Calendar.HOUR); //hour
            System.out.println( day + "/" + month+" " + h + ":" +m + ":"+ sec + "\nA UI is connected~~\n\n");

            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);

            String order = bf.readLine();
            if ("DB".equals(order)){
                String menu = readValues();
                PrintWriter pr = new PrintWriter(s.getOutputStream());
                pr.println(menu);
                pr.flush();
            }
            else if("ORDER".equals(order)){
                System.out.println(day + "/" + month+" " + h + ":" +m + ":"+ sec + "\nGot an order~\n\n");
                String name = bf.readLine();
                //System.out.println(name);
                
                String detail = bf.readLine();
                System.out.println(detail);
                String[] details = detail.split(" ");
                
                if (details != null){
                    processOrder(details);
                    createInvoice(name, details);
                }
                
            }

           
            

        
        }
        
        
        
    }
    public static void processOrder(String[] detail){

        String[][] order = new String[detail.length/4][2];
        for (int i = 0; i < detail.length; i+= 4){
            order[i/4][0] = detail[i];
            order[i/4][1] = detail[i+3];
        }
        editValue(order);
    }
    
    public static void editValue(String[][] order){
        connectServer();  
         try{
             for (String[] order1 : order) {
                 String query = "UPDATE drinks SET stock='" + order1[1] + "' WHERE name='" + order1[0] + "'";
                 statement = connection.createStatement(); 
                 statement.executeUpdate(query);
             }
             
             Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int sec = c.get(Calendar.SECOND);
            int m = c.get(Calendar.MINUTE); //minute
            int h = c.get(Calendar.HOUR); //hour
             System.out.println(day + "/" + month+" " + h + ":" +m + ":"+ sec + "\nsuccessfully updated the stock.\n\n");
             
         }catch(SQLException e){
             System.out.println(e);
         }
         
         try{
             connection.close();
         }catch(SQLException e){
             System.out.println(e);
         }
        
    }
    
    @SuppressWarnings("empty-statement")
    public static void insertValues(){
        connectServer();  

        String[][] queries = new String[15][3];
        queries[0] = new String[] {"Rum", "10.00", "90"};
        queries[1] = new String[] {"Cola", "5.00", "100"};
        queries[2] = new String[] {"Sparkling_Water", "5.00", "100"};
        queries[3] = new String[] {"Pina_Colada", "10.00", "80"};
        queries[4] = new String[] {"Margarita", "8.00", "80"};
        queries[5] = new String[] {"Mojito", "8.00", "80"};
        queries[6] = new String[] {"Martini", "9.00", "80"};
        queries[7] = new String[] {"Bloody_Mary", "8.00", "80"};
        queries[8] = new String[] {"Mimosa", "8.00", "90"};
        queries[9] = new String[] {"Blood_Orange_Mule", "8.00", "80"};
        queries[10] = new String[] {"Old_Fashioned", "8.00", "80"};
        queries[11] = new String[] {"Manhattan", "8.00", "80"};
        queries[12] = new String[] {"Whisky_Sour", "8.00", "80"};
        queries[13] = new String[] {"Bar_97", "10.00", "80"};
        queries[14] = new String[] {"Cold_Brew", "3.00", "30"};
        
        
        
        try{
            for (int i = 0; i < queries.length; i++){
                String query = "INSERT INTO Drinks(name, price, stock)" + "VALUES('" +queries[i][0] +"', "+queries[i][1] +", " + queries[i][2]+")";
                statement = connection.createStatement(); 
                statement.executeUpdate(query);
            }
            
            System.out.println("sucessfully inserted into the Drinks table!");
        }catch(SQLException e){
            System.out.println(e);
        
    }
  
        try{
             connection.close();
         }catch(SQLException e){
             System.out.println(e);
         }
    }
    
    public static String readValues(){
        connectServer();  
        
        String menu = "";
        try{
            
            String query = "select * from drinks";       
            statement = connection.createStatement();
            ResultSet result = null;
            result = statement.executeQuery(query);
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int sec = c.get(Calendar.SECOND);
            int year = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MINUTE); //minute
            int h = c.get(Calendar.HOUR); //hour
            System.out.println(day + "/" + month+" " + h + ":" +m + ":"+ sec + "\nSuccesfully read data from the db\n\n");
            
            while (result.next()){
                //System.out.println(result.getString(1));
                menu += result.getString(1) + " ";
                //System.out.println(result.getString(2));
                menu += result.getString(2) + " ";
                //System.out.println(result.getString(3));
                menu += result.getString(3) + " ";
            }
            //System.out.println(menu);
            
        }catch(SQLException e){
            System.out.println(e);
        
    }
        try{
             connection.close();
         }catch(SQLException e){
             System.out.println(e);
         }
        return menu;
    }
    
    public static void connectServer(){
        try{
            Class.forName("org.postgresql.Driver");
            String databaseName = "javaFinal";
            String userName = "9ussell7hang";
            String password = "";
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+databaseName,userName , password);
            if (connection == null){
                System.out.println("Connection failed");
            }
            else{
                System.out.println("Connection with database successed~");
                //System.out.println(connection);


            }
        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e);
        }
    }
    public static void createTable(){
        connectServer();
        try{
            String query = "Create Table Drinks(name varchar primary key, price int, stock int,UNIQUE(name))";
                    
            statement = connection.createStatement();
            statement.executeUpdate(query);
            System.out.println("sucessfully created the Drinks table!");
        }catch(SQLException e){
            System.out.println(e);
        
    }
        try{
             connection.close();
         }catch(SQLException e){
             System.out.println(e);
         }
    }
    
}



