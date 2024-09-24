package qeats.microservices.qa.tests;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.sql.ResultSet;

public class TestCaseDB extends PlaceOrder {
  Connection orderDBConn, restaurantDBConn;
  Statement stmt;
  int tableCount;
  ResultSet resultSet;

  @BeforeTest
  void setup() throws SQLException, MalformedURLException {
    orderDBConn = orderDbConnection();
    restaurantDBConn = restaurantDbConnection();
    orderDBConn.setAutoCommit(true);
    restaurantDBConn.setAutoCommit(true);
  }

  @Test
  public void TestCase01() throws SQLException {

    try {
      stmt = restaurantDBConn.createStatement();
      stmt.execute("USE qeats_restaurant_service_database_qa");
      var restaurantTables = stmt.executeQuery("SHOW TABLES");
      tableCount = 0;
      while (restaurantTables.next()) {
        tableCount++;
      }
      Assert.assertTrue(tableCount == 5,
          "Expected 5 tables in qeats_restaurant_service_database_qa, found " + tableCount);
      System.out.println("Total tables in qeats_restaurant_service_database_qa : " + tableCount);

      stmt = orderDBConn.createStatement();
      stmt.execute("USE qeats_order_service_database_qa");
      var orderTables = stmt.executeQuery("SHOW TABLES");
      tableCount = 0;
      while (orderTables.next()) {
        tableCount++;
      }
      Assert.assertTrue(tableCount == 4, "Expected 4 tables in qeats_order_service_database_qa, found " + tableCount);
      System.out.println("Total tables in qeats_order_service_database_qa : " + tableCount);

    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }

  }

  @Test
  public void TestCase02() throws InterruptedException, SQLException {

    stmt = orderDBConn.createStatement();
    stmt.execute("USE qeats_order_service_database_qa");

    // Add product to cart
    PlaceOrder placeOrder = new PlaceOrder();
    placeOrder.addToCart();

    // Get cart_id and item_id / menu_id for the product
    String query1 = "SELECT * FROM cart_item LIMIT 1";
    resultSet = stmt.executeQuery(query1);
    int cartId = -1;
    int menuitemId = -1;
    if (resultSet.next()) {
      cartId = resultSet.getInt("cart_id");
      menuitemId = resultSet.getInt("menuitem_id");
    }

    // Assert entry from cart and cart_item table is removed once order placed
    placeOrder.placeOrder();

    // Assert entry removed from cart_item_table
    String query2 = "SELECT * FROM cart_item WHERE cart_id = " + cartId + " AND menuitem_id = " + menuitemId;
    resultSet = stmt.executeQuery(query2);
    Assert.assertFalse(resultSet.next(), "Entry still exists in cart_item table");

    // // Assert entry removed from cart_table
    String query3 = "SELECT * FROM cart WHERE cart_id = " + cartId;
    resultSet = stmt.executeQuery(query3);
    System.out.println(resultSet.next());
    Assert.assertFalse(resultSet.next(), "Entry still exists in cart table");

    // Assert orders table is populated with order details
    String query4 = "SELECT * FROM orders WHERE cart_id = " + cartId;
    resultSet = stmt.executeQuery(query4);
    Assert.assertTrue(resultSet.next(), "Order table not populated");
    int orderId = resultSet.getInt("order_id");

    // Assert order_item table is populated
    String query5 = "SELECT * FROM order_item WHERE order_id = " + orderId;
    resultSet = stmt.executeQuery(query5);
    Assert.assertTrue(resultSet.next(), "Order_item table not populated");

  }

  @Test
  public void TestCase03() throws SQLException {
    try {
      // Get the list of all orders
      System.out.println("Getting the list of all orders");
      stmt = orderDBConn.createStatement();
      stmt.execute("USE qeats_order_service_database_qa");

      String query = "SELECT * FROM orders";
      ResultSet resultSet = stmt.executeQuery(query);
      tableCount = 0;
      int orderId = 0;
      while (resultSet.next()) {
        tableCount++;
        orderId = resultSet.getInt("order_id");
        System.out.println("Order ID: " + orderId);
      }
      System.out.println("Total orders: " + tableCount);

      // Try deleting the most recent order
      if (tableCount > 0) {
        System.out.println("Trying to delete the most recent order");
        String deleteQuery = "DELETE FROM orders WHERE order_id = "+orderId;

        stmt.executeUpdate(deleteQuery);

        // Assert if the order got deleted
        query = "SELECT * FROM orders";
        resultSet = stmt.executeQuery(query);
        int newTableCount = 0;
        while (resultSet.next()) {
          newTableCount++;
        }
        resultSet.close();
        Assert.assertEquals(newTableCount, tableCount - 1, "The most recent order was not deleted");
        
      } else {
        System.out.println("There are no orders to delete");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void TestCase04() throws SQLException {
    try {
      // Get list of all restaurant id's in OrderDB
      stmt = orderDBConn.createStatement();
      stmt.execute("USE qeats_order_service_database_qa");
      String orderDBQuery = "SELECT DISTINCT restaurant_id FROM orders";
      ResultSet orderDBResult = stmt.executeQuery(orderDBQuery);

      List<Integer> orderDBRestaurantIds = new ArrayList<>();
      while (orderDBResult.next()) {
        orderDBRestaurantIds.add(orderDBResult.getInt("restaurant_id"));
      }

      // Get list of all available restaurants from restaurantDB
      stmt = restaurantDBConn.createStatement();
      stmt.execute("USE qeats_restaurant_service_database_qa");
      String restaurantDBQuery = "SELECT DISTINCT restaurant_id FROM restaurant";
      ResultSet restaurantDBResult = stmt.executeQuery(restaurantDBQuery);

      List<Integer> restaurantDBIds = new ArrayList<>();
      while (restaurantDBResult.next()) {
        restaurantDBIds.add(restaurantDBResult.getInt("restaurant_id"));
      }

      // Assert if all restaurant ID's from OrderDb are present in restaurantDB
      for (Integer restaurantId : orderDBRestaurantIds) {
        Assert.assertTrue(restaurantDBIds.contains(restaurantId),
            "Restaurant ID " + restaurantId + " not found in restaurantDB");
      }
    } finally {
      // Close resources
      if (stmt != null) {
        stmt.close();
      }
    }
  }

  @Test
  public void TestCase05() throws InterruptedException, SQLException {
    // Get list of all order ids
    System.out.println("Getting list of all order ids before placing order");
    stmt = orderDBConn.createStatement();
    stmt.execute("USE qeats_order_service_database_qa");
    String sql = "SELECT order_id FROM orders";
    ResultSet rsBefore = stmt.executeQuery(sql);
    ArrayList<Integer> orderIdsBefore = new ArrayList<>();
    while (rsBefore.next()) {
      orderIdsBefore.add(rsBefore.getInt("order_id"));
    }
    rsBefore.close();

    // Try placing an order with restaurantId=90
    System.out.println("Trying to place an order with restaurantId=90");
    this.restaurantId = 90; // setting restaurantId to 90
    addToCart(); // add items to cart
    placeOrder(); // place the order

    // Get list of all order ids after placing order
    System.out.println("Getting list of all order ids after placing order");
    ResultSet rsAfter = stmt.executeQuery(sql);
    ArrayList<Integer> orderIdsAfter = new ArrayList<>();
    while (rsAfter.next()) {
      orderIdsAfter.add(rsAfter.getInt("order_id"));
    }
    rsAfter.close();

    // Assert the same order ids still exist before and after
    Assert.assertEquals(orderIdsBefore, orderIdsAfter, "Order ids before and after placing order are not the same");
  }

  @Test
  void getMostOrderedDishes() throws SQLException {
    // Connect to the order database
    try (Statement stmt = orderDBConn.createStatement()) {
      // Execute SQL query to count the occurrences of each menu item in the
      // order_item table
      String sqlQuery = "SELECT menuitem_id, COUNT(*) AS order_count FROM order_item GROUP BY menuitem_id";
      ResultSet rs = stmt.executeQuery(sqlQuery);

      // Create a map to store menuitem_id and their corresponding order counts
      Map<Integer, Integer> menuItemCounts = new HashMap<>();

      // Populate the map with data from the result set
      while (rs.next()) {
        int menuItemId = rs.getInt("menuitem_id");
        int orderCount = rs.getInt("order_count");
        menuItemCounts.put(menuItemId, orderCount);
      }

      // Sort the menu items by their order counts in descending order
      Map<Integer, Integer> sortedMenuItemCounts = new TreeMap<>((a, b) -> {
        int compare = menuItemCounts.get(b).compareTo(menuItemCounts.get(a));
        if (compare == 0)
          return 1;
        return compare;
      });
      sortedMenuItemCounts.putAll(menuItemCounts);

      // Print the top 3 most ordered dishes
      System.out.println("Top 3 Most Ordered Dishes:");
      int count = 0;
      for (Map.Entry<Integer, Integer> entry : sortedMenuItemCounts.entrySet()) {
        if (count >= 3)
          break;
        System.out.println("Menu Item ID: " + entry.getKey() + ", Order Count: " + entry.getValue());
        count++;
      }
    }
  }
  
}
