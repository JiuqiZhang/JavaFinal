/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package bargui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.*;
import static java.lang.Integer.parseInt;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


/**
 *
 * @author 9ussell7hang
 */
public class BarGUI {

    /**
     * @param args the command line arguments
     *
     */
    static JFrame jf;
    static Socket s;
    static PrintWriter pr;
    static String[] drinks = new String[15];
    static int[] stock = new int[15];
    static int[] price = new int[15];
    static int[] detail = new int[15];
    static JButton[] products = new JButton[15];
    static int total;
    static int totalPrice;
    static JButton confirm;
    static String name;

    
    
    public static void main(String[] args) throws IOException {
        jf = new JFrame("No.97 Bar");
        jf.setSize(500,500);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //jf.setBackground(Color.BLACK);
        
        EntryScreen(jf);
        jf.setVisible(true);
        


       
    }
    
    public static void clientSetUp() throws IOException{
        s = new Socket("localhost", 1297);
        
        pr = new PrintWriter(s.getOutputStream());
        pr.println("DB");
        pr.flush();

        
        
        
        
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        String menu = bf.readLine();
        //System.out.println(menu);
        String[] menuLst = menu.split(" ");
        total = 0;totalPrice = 0;
        
        //confirm button set up
        confirm = new JButton("("+total+")"+"Confirm Order -- "+totalPrice+".00$");
        confirm.addActionListener((ActionEvent e) -> {
            try {
                if(total!=0){
                    sendOrder();
                }
                
            } catch (IOException ex) {
                System.out.println(ex);
            }
        });
        confirm.setSize(300, 300);
        confirm.setForeground(Color.RED);
        
        s.close();
        
        for (int i = 0; i < drinks.length; i++){
           
            drinks[i] = menuLst[i*3]; //name 
            price[i] = parseInt(menuLst[i*3 + 1]); // price
            stock[i] = parseInt(menuLst[i*3 + 2]); //stock
            products[i] = new JButton(drinks[i]);
            products[i].setFont(new Font("Serif", Font.PLAIN, 15));
            detail[i] = 0;
            products[i].addActionListener((ActionEvent e) -> {
          JButton pressed = (JButton) e.getSource();
          for (int j = 0; j < 15; j++) {
              if (pressed == products[j]) {
                  //total ordered products number plus one.
                  total+=1; 
                   //calculate the total price
                  totalPrice += price[j];

                  //stock will be updated
                  stock[j] -= 1;
                  detail[j] += 1;
                  confirm.setText("("+total+")"+"Confirm Order -- "+totalPrice+".00$");
                  //if there's no more stock, disble the button so the customers cannot add it again
                  if (stock[j] == 0){
                      products[j].setEnabled(false);
                  }
              }
              
          }});
            
            //if the stock is 0, meaning this product annot be ordered anymore
            if (stock[i] == 0){
                products[i].setEnabled(false);
            }


        }
        
        
        
    }
    public static void MenuScreen(JFrame jf){
        jf.getContentPane().removeAll();

        
        JPanel jp4 = new JPanel(new BorderLayout());
        jp4.setBackground(Color.BLACK);
     

        JLabel jl1 = new JLabel("Menu", SwingConstants.CENTER);
        jl1.setForeground(Color.RED);
        jl1.setFont(new Font("Serif", Font.PLAIN, 30));
        jp4.add(jl1,BorderLayout.NORTH);

        JPanel jp3 = new JPanel(new GridLayout(15,2));
        JScrollPane jp5 = new JScrollPane(jp3);
        
        for (int i = 0 ; i < 15; i++){
            jp3.add(products[i]);
        }
        jp3.setPreferredSize(new Dimension(397, 1000));
        jp4.add(jp5, BorderLayout.CENTER);
        
        jp4.add(confirm, BorderLayout.SOUTH);
        jf.add(jp4);

        

        
    }
    
    public static void sendOrder() throws IOException{
        s = new Socket("localhost", 1297);

        String orderInfo = "";
        for (int i = 0; i < 15; i++){
            if (detail[i] != 0){
                orderInfo = orderInfo + drinks[i] + " " + price[i] + " " + detail[i] + " " + stock[i] + " ";
            }
           
        }

//        System.out.println(orderInfo);
        
        pr = new PrintWriter(s.getOutputStream());
        pr.println("ORDER");
        pr.flush();
        if(!"Name or table number".equals(name)){
            pr.println(name);
        }
        else{
            pr.println("Anonymous");
        }
        
        pr.flush();
        pr.println(orderInfo);

        pr.flush();
        try{
            s.close();
        } catch (IOException e) {
            System.out.println("Could not close socket");
            System.exit(-1);
        }
        BarGUI.EndingScreen(jf);
        jf.setVisible(true);

    }
    
    
    public static String removeLastChar(String s) {
    return (s == null || s.length() == 0)
      ? null 
      : (s.substring(0, s.length() - 1));
}
    public static void EntryScreen(JFrame jf) throws IOException{
        jf.getContentPane().removeAll();
        JPanel jp1 = new JPanel(new BorderLayout(10, 10));
        jp1.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));    
        jp1.setBackground(Color.BLACK);
        
        JPanel jp2 = new JPanel(new BorderLayout());
        jp2.setBackground(Color.BLACK);
        jp1.add(jp2, BorderLayout.CENTER);
        JButton jb1 = new JButton("ORDER");
        JTextField jt = new JTextField("Name or table number");
        jt.addActionListener((ActionEvent event) -> {
            
            System.out.println(name);
        });
        jp1.add(jt, BorderLayout.SOUTH);
        jb1.setSize(300, 300);
        jb1.setForeground(Color.RED);
        //jb1.addActionListener(new Btn(jf, "MENU"));  
        jb1.addActionListener((ActionEvent event) -> {
            name = jt.getText();
            BarGUI.MenuScreen(jf);
            jf.setVisible(true);
        });

        jp2.add(jb1,BorderLayout.SOUTH);
        JLabel jl1 = new JLabel("No.97 BAR", SwingConstants.CENTER);
        jl1.setForeground(Color.RED);
        jl1.setFont(new Font("Serif", Font.PLAIN, 30));
        jp2.add(jl1,BorderLayout.CENTER);
        jf.add(jp1);
        
        clientSetUp();

    }
    
    public static void EndingScreen(JFrame jf){
        
        jf.getContentPane().removeAll();
        JPanel jp1 = new JPanel(new BorderLayout(10, 10));
        jp1.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));    
        jp1.setBackground(Color.BLACK);
        
        JPanel jp2 = new JPanel(new BorderLayout());
        jp2.setBackground(Color.BLACK);
        jp1.add(jp2, BorderLayout.CENTER);

 
        JLabel jl = new JLabel("Thanks for order~ Drinks will be delievered soon.", SwingConstants.CENTER);
        jl.setForeground(Color.RED);
        jp2.add(jl,BorderLayout.SOUTH);
        JLabel jl1 = new JLabel("No.97 BAR", SwingConstants.CENTER);
        jl1.setForeground(Color.RED);
        jl1.setFont(new Font("Serif", Font.PLAIN, 30));
        jp2.add(jl1,BorderLayout.CENTER);
        jf.add(jp1);

    
    }
    
   
    
}
//class Btn implements ActionListener {
//    JFrame jf;
//    String screen;
//    Btn(JFrame jf, String screen){
//        this.jf = jf;
//        this.screen = screen;
//    
//}
//    @Override
//    public void actionPerformed(ActionEvent e) {
//
//            //Execute when button is pressed
//            if ("MENU".equals(screen)){
//                BarGUI.MenuScreen(jf);
//                jf.setVisible(true);
//            }
//            else{
//                try {
//                    BarGUI.EntryScreen(jf);
//                } catch (IOException ex) {
//                    System.out.println(ex);
//                }
//                jf.setVisible(true);
//                
//            }
//            
//
//        }
//
// 
//}
//
