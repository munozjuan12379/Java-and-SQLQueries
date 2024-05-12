// allows user to enter a database and connect it to this program
// shows a menu for users allowing them to run multiple queries and prompting them for input
// output is printed out for each query in table format

import java.sql.*;
import java.util.*;
public class Main {
    public static String userDatabase(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter database name");
        String database = scan.next();
        return database;
    }
    public static String userName(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter username");
        String user = scan.next();
        return user;
    }
    public static String userPassword(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter password");
        String pass = scan.next();
        return pass;
    }
    public static void showMenu(){
        System.out.println("Enter a number 1-6 to select an option below.");
        System.out.println("---------------------------------------------");
        System.out.println("1. Show all supplier information for suppliers that supply a specific product");
        System.out.println("2. Show the product IDs and supplier IDs for any product that costs less than a particular value.");
        System.out.println("3. Show the customer ID, customers first name, and total widgets ordered for the customer that has the"
                + " greatest total number of widgets ordered.");
        System.out.println("4. Show all widget information for any widget that has no customer orders.");
        System.out.println("5. For each widget, show the total amount ordered from customers.");
        System.out.println("6. Exit");
    }
    public static void query1(Connection con) throws SQLException {
        Scanner scan = new Scanner(System.in);
        int productInt;
        String product_id;
        System.out.println("Enter Product ID");
        while (!scan.hasNextInt()) {
            System.out.println("Please enter a number");
            scan.nextLine();
        }
        productInt = scan.nextInt();
        product_id = String.valueOf(productInt);
        String query = "select supplierid, supplierstreet, suppliercity, supplierstate, supplierzip"
                + " from Supplier s"
                + " where supplierid in"
                + " (select supplierid"
                + " from ProductSupplier"
                + " where productid = ?)";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, product_id);
        ResultSet result = stmt.executeQuery();
        System.out.println("Processing Results");
        while(result.next()) {
            System.out.println("Supplier id: " + result.getString("supplierid") +
            " | Supplier street: " + result.getString("supplierstreet") +
            " | Supplier city: " + result.getString("suppliercity") +
            " | Supplier state: " + result.getString("supplierstate") +
            " | Supplier zip: " + result.getString("supplierzip"));
        }
    }
    public static void query2(Connection con) throws SQLException {
        Scanner scan = new Scanner(System.in);
        float productInt;
        String product_price;
        System.out.println("Enter Product Price");
        while (!scan.hasNextFloat()) {
            System.out.println("Please enter a number");
            scan.nextLine();
        }
        productInt = scan.nextFloat();
        product_price = String.valueOf(productInt);
        String query = "select p.productid, supplierid"
                + " from ProductSupplier p, Product x"
                + " where p.productid = x.productid"
                + " and x.productcost < ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1,product_price);
        ResultSet result = stmt.executeQuery();
        System.out.println("Processing Results");
        while(result.next()) {
            System.out.println("Product ID: " + result.getString("p.productid")
                    + " | Supplier ID: " + result.getString("supplierid"));
        }
    }
    public static void query3(Connection con) throws SQLException{
        String query = "select c.customerid, c.customerfname, cw.orderqty"
                + " from Customer c join CustomerWhWidget cw on c.customerid = cw.customerid"
                + " where orderqty in"
                + " (select max(orderqty)"
                + " from CustomerWhWidget)";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        System.out.println("Processing Results");
        while(result.next()) {
            System.out.println("Customer Id: " + result.getString("c.customerid") +
            " | Customer First Name: " + result.getString("c.customerfname") +
            " | Order quantity: " + result.getString("cw.orderqty"));
        }
    }
    public static void query4(Connection con) throws SQLException{
        String query = "select w.widgetid, w.widgetweight, w.widgetname, w.widgetcost"
                + " from Widget w"
                + " where widgetid not in"
                + " (select widgetid"
                + " from CustomerWhWidget)";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        System.out.println("Processing Results");
        while(result.next()) {
            System.out.println("Widget id: " + result.getString("w.widgetid") + " | Widget weight: " + result.getString("w.widgetweight")
            + " | Widget name: " + result.getString("w.widgetname") + " | Widget cost: " + result.getString("w.widgetcost"));
        }
    }
    public static void query5(Connection con) throws SQLException{
        String query = "select w.widgetid, ifnull(sum(orderqty), 0)"
                + " from Widget w left join CustomerWhWidget c on c.widgetid = w.widgetid"
                + " group by w.widgetid";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        System.out.println("Processing Results");
        while(result.next()) {
            System.out.println("Widget Id: " + result.getString("w.widgetid") +
           " | Amount Ordered: " + result.getString("ifnull(sum(orderqty), 0)"));
        }
    }
    public static void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (Exception e){
            System.out.println("Can't load driver");
        }

        try{
            System.out.println("Starting Connection........");
            Scanner scan = new Scanner(System.in);
            Connection con = DriverManager.getConnection(
                    userDatabase(), userName(), userPassword());
            System.out.println("Connection Established");
            showMenu();
            int userInput = scan.nextInt();
            while(userInput != 6){
                if (userInput == 1){
                    query1(con);
                } else if (userInput == 2) {
                    query2(con);
                }else if (userInput == 3) {
                    query3(con);
                }else if (userInput == 4) {
                    query4(con);
                }else if (userInput == 5) {
                    query5(con);
                }
                showMenu();
                userInput = scan.nextInt();
            }
            System.out.println("Ending session.");
        }
        catch (SQLException e){
            System.out.println(e.getMessage() + " Can't connect to database");
            while(e!=null){
                System.out.println("Message: "+e.getMessage());
                e= e.getNextException();
            }
        }
        catch (Exception e){
            System.out.println("Other Error");
        }
    }
}