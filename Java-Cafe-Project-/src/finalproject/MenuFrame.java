package finalproject;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class MenuFrame extends javax.swing.JFrame {
    
    private String name;
    private String type;
    private String seat;
    private int total = 0;
    private int set = 0;
    private static int[][] order = new int[9][4]; 
    private int tax = 0;
    private int stock;
    private boolean registered = false;
    private boolean totaled = false;
    private boolean stocked = false;
    private static final String url  = "jdbc:mysql://localhost:3306/cafe";
    private static final String user = "root";
    private static final String password = "";
    
    
    
    private void OrderUpdate(String x, int y, int z, int a){
        String name = x;
        int product_id = y;
        int qty = z;
        int price = a;
        registered = true;
        
            set++;
            if(set==1){
                cafe();
            }
            if (order[product_id-1][1] == 0) {
                order[product_id-1][0]= product_id;
                order[product_id-1][1]= qty;
            } 
            else {
                order[product_id-1][1]+= qty;
            }
                price *= qty;
                total += price;
                tax = (int)(total*0.11);
                TaxText.setText("Rp. " + tax );
                SubTotalText.setText("Rp. " + total);
                TotalText.setText("Rp. "+ (tax + total));
                ReceiptText.setText(ReceiptText.getText()+ "      " + set +". " + name + "\t\t" +price +"\n");
        
    }
    public MenuFrame() {
        initComponents();
        init();
        ConnectToDatabase();

    }
    public MenuFrame(String X, String Y, String Z) {
        initComponents();
        init();
        name = X;
        seat = Y;
        type = Z;
        ConnectToDatabase();
        System.out.println(name);
        System.out.println(type);
        System.out.println(seat);
    }
    private void ConnectToDatabase(){
         try {
            Connection connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                System.out.println("Connected to the database!");
                connection.close();
            } else {
                System.out.println("Failed to do so");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: "+ e.getMessage());
        }
    }
    private void InsertCustomer() {
        String sql = "INSERT INTO customer (customer_name) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Customer inserted successfully");
            } else {
                System.out.println("Failed to insert customer");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: "+ e.getMessage());
        }
    }
    private void UpdateStock() {
        String sql = "UPDATE menu SET product_stock = ? WHERE product_id = ?";
        for (int[] order1 : order) {
            if (order1[1] > 0) {
                int id = order1[0];
                int newStock = order1[2];
                try (Connection connection = DriverManager.getConnection(url, user, password);
                        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, newStock);
                    preparedStatement.setInt(2, id);
                    int rowsAffected = preparedStatement.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        System.out.println("Stock updated successfully for ID: " + id);
                    } else {
                        System.out.println("Failed to update stock for ID: " + id);
                    }
                }catch (SQLException e) {
                    System.err.println("SQL Exception: "+ e.getMessage());
                }
            }
        }
    }

    private boolean CheckStock(int x, int y, String z) {
    if(QtyZero(y)){
        String sql = "SELECT product_stock FROM menu WHERE product_id = ?";
        int id = x;
        int qty = y;
        String product_name = z;
            try (Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                
                if (resultSet.next()) {
                    if(order[id-1][3]==0){
                        stock = resultSet.getInt("product_stock");
                    }else{
                        stock = order[id-1][2];
                    }
                    if (stock == 0) {
                        JOptionPane.showMessageDialog(null,  product_name + " is currently out of stock\n We apologize for the inconvenience");
                        return false;
                    } else if (stock < qty) {
                        JOptionPane.showMessageDialog(null, "We only have " + stock + " " + product_name + " left from the stock\n"
                                                            + "We apologize for the inconvenience\n"
                                                            + "May we interest you in another product of ours :)");
                        return false;
                    } else {
                        order[id-1][2] = stock - qty;
                        order[id-1][3] = 1;
                        return true;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Product with ID " + id + " not found");
                    return false;
                }
            } catch (SQLException e) {
                System.err.println("SQL Exception: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    private void InsertOrder() {
        String sql = "INSERT INTO orders (order_type, customer_seat, order_total) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, seat);
            preparedStatement.setInt(3, total + tax);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order inserted successfully");
            } else {
                System.out.println("Failed to insert order");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: "+ e.getMessage());
        }
    }
    public void init(){
        setImage();
        setTime();
    }
    public void setImage(){
        ImageIcon icon1 = new javax.swing.ImageIcon(getClass().getResource("/image/item1.jpeg"));
        ImageIcon icon2 = new javax.swing.ImageIcon(getClass().getResource("/image/item2.jpeg"));
        ImageIcon icon3 = new javax.swing.ImageIcon(getClass().getResource("/image/item3.jpeg"));
        ImageIcon icon4 = new javax.swing.ImageIcon(getClass().getResource("/image/item4.jpeg"));
        ImageIcon icon5 = new javax.swing.ImageIcon(getClass().getResource("/image/item5.jpeg"));
        ImageIcon icon6 = new javax.swing.ImageIcon(getClass().getResource("/image/item6.jpeg"));
        ImageIcon icon7 = new javax.swing.ImageIcon(getClass().getResource("/image/item7.jpeg"));
        ImageIcon icon8 = new javax.swing.ImageIcon(getClass().getResource("/image/item8.jpeg"));
        ImageIcon icon9 = new javax.swing.ImageIcon(getClass().getResource("/image/item9.jpeg"));
        
        Image img1 = icon1.getImage().getScaledInstance(image1.getWidth(), image1.getHeight(), Image.SCALE_SMOOTH);
        Image img2 = icon2.getImage().getScaledInstance(image1.getWidth(), image1.getHeight(), Image.SCALE_SMOOTH);
        Image img3 = icon3.getImage().getScaledInstance(image1.getWidth(), image1.getHeight(), Image.SCALE_SMOOTH);
        Image img4 = icon4.getImage().getScaledInstance(image1.getWidth(), image1.getHeight(), Image.SCALE_SMOOTH);
        Image img5 = icon5.getImage().getScaledInstance(image1.getWidth(), image1.getHeight(), Image.SCALE_SMOOTH);
        Image img6 = icon6.getImage().getScaledInstance(image1.getWidth(), image1.getHeight(), Image.SCALE_SMOOTH);
        Image img7 = icon7.getImage().getScaledInstance(image1.getWidth(), image1.getHeight(), Image.SCALE_SMOOTH);
        Image img8 = icon8.getImage().getScaledInstance(image1.getWidth(), image1.getHeight(), Image.SCALE_SMOOTH);
        Image img9 = icon9.getImage().getScaledInstance(image1.getWidth(), image1.getHeight(), Image.SCALE_SMOOTH);
        image1.setIcon(new ImageIcon(img1));
        image2.setIcon(new ImageIcon(img2));
        image3.setIcon(new ImageIcon(img3));
        image4.setIcon(new ImageIcon(img4));
        image5.setIcon(new ImageIcon(img5));
        image6.setIcon(new ImageIcon(img6));
        image7.setIcon(new ImageIcon(img7));
        image8.setIcon(new ImageIcon(img8));
        image9.setIcon(new ImageIcon(img9));
    }                                               
    public boolean QtyZero(int qty){
        if(qty==0){
            JOptionPane.showMessageDialog(null, "Please increase the item quantity");
            return false;
        }
        return true;
    }
    
    
    public void reset(){
        item1.setValue(0);
        item2.setValue(0);
        item3.setValue(0);
        item4.setValue(0);
        item5.setValue(0);
        item6.setValue(0);
        item7.setValue(0);
        item8.setValue(0);
        item9.setValue(0);
        TaxText.setText("Rp. 0");
        SubTotalText.setText("Rp. 0");
        TotalText.setText("Rp. 0");
        ReceiptText.setText("");
        total= set = stock = 0;
        registered = totaled = stocked = false;
        for (int[] reset : order) {
            for (int j = 0; j < reset.length; j++) {
                reset[j] = 0;
            }
        }
    }
    public void cafe(){
         ReceiptText.setText( "***********************PU Cafe************************\n"
                                + "Time: " + HeaderTime.getText()+ "\n"
                                + "Date: " + HeaderDate.getText()+"\n"
                                + "*******************************************************\n"
                                + "  Item Name:\t\t\t" + "Price($)\n");
    }
    public void ReturnToRegister(){
        reset();
        setVisible(false);
        IdentificationFrame register = new IdentificationFrame();
        register.setVisible(true);
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jFrame2 = new javax.swing.JFrame();
        jFrame3 = new javax.swing.JFrame();
        jFrame4 = new javax.swing.JFrame();
        jLabel2 = new javax.swing.JLabel();
        jDialog1 = new javax.swing.JDialog();
        label3 = new java.awt.Label();
        label2 = new java.awt.Label();
        label1 = new java.awt.Label();
        jPanel4 = new javax.swing.JPanel();
        image3 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        item3 = new javax.swing.JSpinner();
        price3 = new javax.swing.JLabel();
        name3 = new javax.swing.JLabel();
        cart3 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        image2 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        item2 = new javax.swing.JSpinner();
        price2 = new javax.swing.JLabel();
        name2 = new javax.swing.JLabel();
        cart2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        image1 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        item1 = new javax.swing.JSpinner();
        price1 = new javax.swing.JLabel();
        name1 = new javax.swing.JLabel();
        cart1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        TotalText = new javax.swing.JTextField();
        SubTotalText = new javax.swing.JTextField();
        TaxText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ReceiptText = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        image6 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        item6 = new javax.swing.JSpinner();
        price6 = new javax.swing.JLabel();
        name6 = new javax.swing.JLabel();
        cart6 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        image5 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        item5 = new javax.swing.JSpinner();
        price5 = new javax.swing.JLabel();
        name5 = new javax.swing.JLabel();
        cart5 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        image4 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        item4 = new javax.swing.JSpinner();
        price4 = new javax.swing.JLabel();
        name4 = new javax.swing.JLabel();
        cart4 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        image9 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        item9 = new javax.swing.JSpinner();
        price9 = new javax.swing.JLabel();
        name9 = new javax.swing.JLabel();
        cart9 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        image8 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        item8 = new javax.swing.JSpinner();
        price8 = new javax.swing.JLabel();
        name8 = new javax.swing.JLabel();
        cart8 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        image7 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        item7 = new javax.swing.JSpinner();
        price7 = new javax.swing.JLabel();
        name7 = new javax.swing.JLabel();
        cart7 = new javax.swing.JButton();
        BtnExit = new javax.swing.JButton();
        BtnTotal = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        BtnReset = new javax.swing.JButton();
        HeaderDate = new javax.swing.JLabel();
        HeaderTime = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        label3.setText("label3");

        label2.setText("label2");

        label1.setText("label1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1135, 850));
        setPreferredSize(new java.awt.Dimension(1135, 850));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(241, 194, 194));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        image3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(image3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setText("Price     :");
        jPanel4.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 62, -1));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setText("Name   :");
        jPanel4.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 62, -1));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("Quantity  :");
        jPanel4.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, -1));

        item3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        item3.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        jPanel4.add(item3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        price3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        price3.setText("Rp. 22000");
        jPanel4.add(price3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 70, -1));

        name3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        name3.setText("Cappuccino");
        jPanel4.add(name3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 90, -1));

        cart3.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cart3.setText("Add to cart");
        cart3.setBorder(null);
        cart3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cart3ActionPerformed(evt);
            }
        });
        jPanel4.add(cart3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 185, 110, 30));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 40, 200, 220));

        jPanel6.setBackground(new java.awt.Color(241, 194, 194));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        image2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.add(image2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel32.setText("Price     :");
        jPanel6.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 62, -1));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel33.setText("Name   :");
        jPanel6.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 62, -1));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel34.setText("Quantity  :");
        jPanel6.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, 25));

        item2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        item2.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        jPanel6.add(item2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        price2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        price2.setText("Rp. 24000");
        jPanel6.add(price2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 70, -1));

        name2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        name2.setText("Vanilla Latte");
        jPanel6.add(name2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 100, -1));

        cart2.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cart2.setText("Add to cart");
        cart2.setBorder(null);
        cart2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cart2ActionPerformed(evt);
            }
        });
        jPanel6.add(cart2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 185, 110, 30));

        getContentPane().add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 40, 200, 220));

        jPanel5.setBackground(new java.awt.Color(241, 194, 194));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        image1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(image1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setText("Price     :");
        jPanel5.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 62, -1));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setText("Name   :");
        jPanel5.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 62, -1));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setText("Quantity  :");
        jPanel5.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, -1));

        item1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        item1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        jPanel5.add(item1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        price1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        price1.setText("Rp. 18000");
        jPanel5.add(price1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 70, -1));

        name1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        name1.setText("Americano");
        jPanel5.add(name1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 80, -1));

        cart1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cart1.setText("Add to cart");
        cart1.setBorder(null);
        cart1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cart1ActionPerformed(evt);
            }
        });
        jPanel5.add(cart1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 185, 110, 30));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 200, 220));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        TotalText.setEditable(false);
        TotalText.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        TotalText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TotalText.setText("Rp 0");
        jPanel1.add(TotalText, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 640, 140, 40));

        SubTotalText.setEditable(false);
        SubTotalText.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        SubTotalText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        SubTotalText.setText("Rp 0");
        SubTotalText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubTotalTextActionPerformed(evt);
            }
        });
        jPanel1.add(SubTotalText, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 580, 140, 40));

        TaxText.setEditable(false);
        TaxText.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        TaxText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TaxText.setText("Rp 0");
        TaxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TaxTextActionPerformed(evt);
            }
        });
        jPanel1.add(TaxText, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 520, 140, 40));

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        jLabel5.setText("Total");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 640, 100, 40));

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        jLabel6.setText("Tax");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 520, 100, 40));

        jLabel7.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        jLabel7.setText("Sub Total");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 580, 100, 40));

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));
        jPanel2.add(jLabel3);

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 500, 400, 200));

        ReceiptText.setEditable(false);
        ReceiptText.setColumns(20);
        ReceiptText.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        ReceiptText.setRows(5);
        ReceiptText.setBorder(null);
        jScrollPane1.setViewportView(ReceiptText);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 500));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 40, 400, 700));

        jPanel7.setBackground(new java.awt.Color(241, 194, 194));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        image6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.add(image6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel38.setText("Price     :");
        jPanel7.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 62, -1));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel39.setText("Name   :");
        jPanel7.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 62, -1));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel40.setText("Quantity  :");
        jPanel7.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, -1));

        item6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        item6.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        jPanel7.add(item6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        price6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        price6.setText("Rp. 20000");
        jPanel7.add(price6, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 70, -1));

        name6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        name6.setText("Jasmine Tea");
        jPanel7.add(name6, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 90, -1));

        cart6.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cart6.setText("Add to cart");
        cart6.setBorder(null);
        cart6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cart6ActionPerformed(evt);
            }
        });
        jPanel7.add(cart6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 185, 110, 30));

        getContentPane().add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 280, 200, 220));

        jPanel8.setBackground(new java.awt.Color(241, 194, 194));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        image5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.add(image5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel44.setText("Price     :");
        jPanel8.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 62, -1));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel45.setText("Name   :");
        jPanel8.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 62, -1));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel46.setText("Quantity  :");
        jPanel8.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, -1));

        item5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        item5.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        jPanel8.add(item5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        price5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        price5.setText("Rp. 24000");
        jPanel8.add(price5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 70, -1));

        name5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        name5.setText("Matcha");
        jPanel8.add(name5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 80, -1));

        cart5.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cart5.setText("Add to cart");
        cart5.setBorder(null);
        cart5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cart5ActionPerformed(evt);
            }
        });
        jPanel8.add(cart5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 185, 110, 30));

        getContentPane().add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 280, 200, 220));

        jPanel9.setBackground(new java.awt.Color(241, 194, 194));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        image4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.add(image4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));

        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel50.setText("Price     :");
        jPanel9.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 62, -1));

        jLabel51.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel51.setText("Name   :");
        jPanel9.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 62, -1));

        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel52.setText("Quantity  :");
        jPanel9.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, -1));

        item4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        item4.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        jPanel9.add(item4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        price4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        price4.setText("Rp. 20000");
        jPanel9.add(price4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 70, -1));

        name4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        name4.setText("Black Tea");
        jPanel9.add(name4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 90, -1));

        cart4.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cart4.setText("Add to cart");
        cart4.setBorder(null);
        cart4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cart4ActionPerformed(evt);
            }
        });
        jPanel9.add(cart4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 185, 110, 30));

        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 280, 200, 220));

        jPanel10.setBackground(new java.awt.Color(241, 194, 194));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        image9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel10.add(image9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel56.setText("Price     :");
        jPanel10.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 62, -1));

        jLabel57.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel57.setText("Name   :");
        jPanel10.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 62, -1));

        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel58.setText("Quantity  :");
        jPanel10.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, -1));

        item9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        item9.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        jPanel10.add(item9, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        price9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        price9.setText("Rp. 15000");
        jPanel10.add(price9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 70, -1));

        name9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        name9.setText("Brownie");
        jPanel10.add(name9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, -1, -1));

        cart9.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cart9.setText("Add to cart");
        cart9.setBorder(null);
        cart9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cart9ActionPerformed(evt);
            }
        });
        jPanel10.add(cart9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 185, 110, 30));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("/2pcs");
        jPanel10.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 110, -1, -1));

        getContentPane().add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 520, 200, 220));

        jPanel17.setBackground(new java.awt.Color(241, 194, 194));
        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        image8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel17.add(image8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));

        jLabel98.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel98.setText("Price     :");
        jPanel17.add(jLabel98, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 62, -1));

        jLabel99.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel99.setText("Name   :");
        jPanel17.add(jLabel99, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 62, -1));

        jLabel100.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel100.setText("Quantity  :");
        jPanel17.add(jLabel100, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, -1));

        item8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        item8.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        jPanel17.add(item8, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        price8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        price8.setText("Rp. 25000");
        jPanel17.add(price8, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 70, -1));

        name8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        name8.setText("Macaron");
        jPanel17.add(name8, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 60, 20));

        cart8.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cart8.setText("Add to cart");
        cart8.setBorder(null);
        cart8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cart8ActionPerformed(evt);
            }
        });
        jPanel17.add(cart8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 185, 110, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("/4pcs");
        jPanel17.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 110, -1, -1));

        getContentPane().add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 520, 200, 220));

        jPanel18.setBackground(new java.awt.Color(241, 194, 194));
        jPanel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        image7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        image7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel18.add(image7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));

        jLabel104.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel104.setText("Price     :");
        jPanel18.add(jLabel104, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 125, 62, -1));

        jLabel105.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel105.setText("Name   :");
        jPanel18.add(jLabel105, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 105, 62, 20));

        jLabel106.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel106.setText("Quantity  :");
        jPanel18.add(jLabel106, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 80, -1));

        item7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        item7.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        jPanel18.add(item7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        price7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        price7.setText("Rp. 20000");
        jPanel18.add(price7, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 125, 70, -1));

        name7.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        name7.setText("Strawberry Cake");
        jPanel18.add(name7, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 106, 120, 20));

        cart7.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        cart7.setText("Add to cart");
        cart7.setBorder(null);
        cart7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cart7ActionPerformed(evt);
            }
        });
        jPanel18.add(cart7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 185, 110, 30));

        getContentPane().add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 520, 200, 220));

        BtnExit.setBackground(new java.awt.Color(255, 0, 51));
        BtnExit.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        BtnExit.setText("Exit");
        BtnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnExitActionPerformed(evt);
            }
        });
        getContentPane().add(BtnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 760, 110, 40));

        BtnTotal.setBackground(new java.awt.Color(102, 255, 102));
        BtnTotal.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        BtnTotal.setText("Total");
        BtnTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTotalActionPerformed(evt);
            }
        });
        getContentPane().add(BtnTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 760, 110, 40));

        jButton3.setBackground(new java.awt.Color(102, 102, 255));
        jButton3.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        jButton3.setText("Receipt");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 760, 110, 40));

        BtnReset.setBackground(new java.awt.Color(255, 255, 0));
        BtnReset.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        BtnReset.setText("Reset");
        BtnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnResetActionPerformed(evt);
            }
        });
        getContentPane().add(BtnReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 760, 110, 40));

        HeaderDate.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        getContentPane().add(HeaderDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 200, 20));

        HeaderTime.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        getContentPane().add(HeaderTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 160, 20));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTotalActionPerformed
        if(total==0.0){
            JOptionPane.showMessageDialog(null, "Please add an item into the cart");
        }
        else if(!totaled){
            tax = (int) (total*0.11);
            ReceiptText.setText(ReceiptText.getText()
                    +"*******************************************************\n"
                            + "  Tax: \t\t\t"+tax+"\n"
                            + "  Sub Total: \t\t\t"+total+"\n"
                            + "  Total: \t\t\t"+(tax + total)+"\n"
                    + "***********************Thank You**********************\n");
            totaled = true;
        }
    }//GEN-LAST:event_BtnTotalActionPerformed

    
    private void BtnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnResetActionPerformed
        reset();
    }//GEN-LAST:event_BtnResetActionPerformed

    private void BtnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnExitActionPerformed
        ReturnToRegister();
    }//GEN-LAST:event_BtnExitActionPerformed

    private void SubTotalTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubTotalTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SubTotalTextActionPerformed

    private void cart9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cart9ActionPerformed
        int qty = Integer.parseInt(item9.getValue().toString()); 
        int product_id = 9;
        String name = name9.getText();
        if(CheckStock(product_id, qty, name)){
            OrderUpdate(name, product_id, qty, 15000);
        }
    }//GEN-LAST:event_cart9ActionPerformed
    
    private void cart1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cart1ActionPerformed
        int qty = Integer.parseInt(item1.getValue().toString()); 
        int product_id = 1;
        String name = name1.getText();
        if(CheckStock(product_id, qty, name)){
            OrderUpdate(name, product_id, qty, 18000);
        }
    }//GEN-LAST:event_cart1ActionPerformed

    private void cart2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cart2ActionPerformed
        int qty = Integer.parseInt(item2.getValue().toString()); 
        int product_id = 2;
        String name = name2.getText();
        if(CheckStock(product_id, qty, name)){
            OrderUpdate(name, product_id, qty, 24000);
        }
    }//GEN-LAST:event_cart2ActionPerformed

    private void cart3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cart3ActionPerformed
        int qty = Integer.parseInt(item3.getValue().toString()); 
        int product_id = 3;
        String name = name3.getText();
        if(CheckStock(product_id, qty, name)){
            OrderUpdate(name, product_id, qty, 22000);
        }
    }//GEN-LAST:event_cart3ActionPerformed

    private void cart6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cart6ActionPerformed
        int qty = Integer.parseInt(item6.getValue().toString()); 
        int product_id = 6;
        String name = name6.getText();
        if(CheckStock(product_id, qty, name)){
            OrderUpdate(name, product_id, qty, 20000);
        }
    }//GEN-LAST:event_cart6ActionPerformed

    private void cart5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cart5ActionPerformed
        int qty = Integer.parseInt(item5.getValue().toString()); 
        int product_id = 5;
        String name = name5.getText()+"\t";
        if(CheckStock(product_id, qty, name)){
            OrderUpdate(name, product_id, qty, 24000);
        }
    }//GEN-LAST:event_cart5ActionPerformed

    private void cart4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cart4ActionPerformed
        int qty = Integer.parseInt(item4.getValue().toString()); 
        int product_id = 4;
        String name = name4.getText();
        if(CheckStock(product_id, qty, name)){
            OrderUpdate(name, product_id, qty, 20000);
        }
    }//GEN-LAST:event_cart4ActionPerformed

    private void cart7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cart7ActionPerformed
        int qty = Integer.parseInt(item7.getValue().toString()); 
        int product_id = 7;
        String name = name7.getText();
        if(CheckStock(product_id, qty, name)){
            OrderUpdate(name, product_id, qty, 20000);
        }
    }//GEN-LAST:event_cart7ActionPerformed

    private void cart8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cart8ActionPerformed
        int qty = Integer.parseInt(item8.getValue().toString()); 
        int product_id = 8;
        String name = name8.getText();
        if(CheckStock(product_id, qty, name)){
            OrderUpdate(name, product_id, qty, 25000);
        }
    }//GEN-LAST:event_cart8ActionPerformed

    private void TaxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TaxTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TaxTextActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(total==0.0){
            JOptionPane.showMessageDialog(null, "Please add an item into the cart");
        }else if(!totaled){
            JOptionPane.showMessageDialog(null, "Conclude your order by pressing the total button");
        }else{
            if(name==null||seat==null||type==null){
                JOptionPane.showMessageDialog(null, "Please insert your data first");
                ReturnToRegister();
            }else{
                JOptionPane.showMessageDialog(null, "Collect your receipt in the next cashier, have a nice day :)");
                InsertOrder();
                InsertCustomer();
                UpdateStock();
                ReturnToRegister();
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed
    
    public void setTime(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(MenuFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                    Date date = new Date();
                    SimpleDateFormat tf = new SimpleDateFormat("h:mm:ss aa");
                    SimpleDateFormat df = new SimpleDateFormat("EEEE, dd-MM-yyyy");
                    String time = tf.format(date);
                    HeaderTime.setText(time.split(" ")[0] +" " + time.split(" ")[1]);
                    HeaderDate.setText(df.format(date));
                }
            }
        }).start();
    }
    
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnExit;
    private javax.swing.JButton BtnReset;
    private javax.swing.JButton BtnTotal;
    private javax.swing.JLabel HeaderDate;
    private javax.swing.JLabel HeaderTime;
    private javax.swing.JTextArea ReceiptText;
    private javax.swing.JTextField SubTotalText;
    private javax.swing.JTextField TaxText;
    private javax.swing.JTextField TotalText;
    private javax.swing.JButton cart1;
    private javax.swing.JButton cart2;
    private javax.swing.JButton cart3;
    private javax.swing.JButton cart4;
    private javax.swing.JButton cart5;
    private javax.swing.JButton cart6;
    private javax.swing.JButton cart7;
    private javax.swing.JButton cart8;
    private javax.swing.JButton cart9;
    private javax.swing.JLabel image1;
    private javax.swing.JLabel image2;
    private javax.swing.JLabel image3;
    private javax.swing.JLabel image4;
    private javax.swing.JLabel image5;
    private javax.swing.JLabel image6;
    private javax.swing.JLabel image7;
    private javax.swing.JLabel image8;
    private javax.swing.JLabel image9;
    private javax.swing.JSpinner item1;
    private javax.swing.JSpinner item2;
    private javax.swing.JSpinner item3;
    private javax.swing.JSpinner item4;
    private javax.swing.JSpinner item5;
    private javax.swing.JSpinner item6;
    private javax.swing.JSpinner item7;
    private javax.swing.JSpinner item8;
    private javax.swing.JSpinner item9;
    private javax.swing.JButton jButton3;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JFrame jFrame3;
    private javax.swing.JFrame jFrame4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private javax.swing.JLabel name1;
    private javax.swing.JLabel name2;
    private javax.swing.JLabel name3;
    private javax.swing.JLabel name4;
    private javax.swing.JLabel name5;
    private javax.swing.JLabel name6;
    private javax.swing.JLabel name7;
    private javax.swing.JLabel name8;
    private javax.swing.JLabel name9;
    private javax.swing.JLabel price1;
    private javax.swing.JLabel price2;
    private javax.swing.JLabel price3;
    private javax.swing.JLabel price4;
    private javax.swing.JLabel price5;
    private javax.swing.JLabel price6;
    private javax.swing.JLabel price7;
    private javax.swing.JLabel price8;
    private javax.swing.JLabel price9;
    // End of variables declaration//GEN-END:variables
}
