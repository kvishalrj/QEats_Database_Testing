package qeats.microservices.qa.tests;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import io.restassured.response.Response;
import qeats.microservices.qa.routes.BaseRoute;
import qeats.microservices.qa.routes.order_service.AddItemRoute;
import qeats.microservices.qa.routes.order_service.GetCartRoute;
import qeats.microservices.qa.routes.order_service.PlaceOrderRoute;
import qeats.microservices.qa.routes.restaurant_service.GetMenuRoute;
import qeats.microservices.qa.routes.restaurant_service.GetRestaurantsRoute;

public class PlaceOrder extends BaseRoute {

  protected int cartId;
  protected int restaurantId = 0;
  String lat = "40.71", lon = "-74.003000";

  private Connection createDbConnection(String dbURI, String user, String password)
      throws SQLException {
    return DriverManager.getConnection(dbURI, user, password);
  }

  public Connection orderDbConnection() throws MalformedURLException, SQLException {
    URL url = new URL(super.baseUri);
    String IP = url.getHost();
    String orderDbURI = "jdbc:mysql://" + IP + ":" + super.orderDBPort + "/" + super.orderDbName;
    return createDbConnection(orderDbURI, orderDbUser, orderDbPassword);
  }

  public Connection restaurantDbConnection() throws MalformedURLException, SQLException {
    URL url = new URL(super.baseUri);
    String IP = url.getHost();
    String restaurantDbURI = "jdbc:mysql://" + IP + ":" + super.restaurantDBPort + "/" + super.restaurantDbName;
    return createDbConnection(restaurantDbURI, restaurantDbUser, restaurantDbPassword);
  }

  public void addToCart() throws InterruptedException {
    initializeRestaurant();
    setupCart();
    addItemsToCart();
  }

  private void initializeRestaurant() {
    System.out.println("Setting restaurant coordinates");
    GetRestaurantsRoute getRestaurant = new GetRestaurantsRoute();
    getRestaurant.setLatLon(lat, lon);
    Response response = getRestaurant.executeRequest();
    int firstRestaurantId = getRestaurant.getFirstRestaurantId(response);
    this.restaurantId = (this.restaurantId == 0) ? firstRestaurantId : this.restaurantId;
    System.out.println("Using Restaurant ID = " + restaurantId);
  }

  private void setupCart() {
    System.out.println("Setting user ID and retrieving cart");
    GetCartRoute getCart = new GetCartRoute();
    getCart.setUserId("1");
    Response response = getCart.executeRequest();
    cartId = getCart.getCartId(response);
  }

  private void addItemsToCart() {
    System.out.println("Setting restaurant ID and retrieving menu items");
    GetMenuRoute getMenu = new GetMenuRoute();
    getMenu.setRestaurantId(this.restaurantId);
    Response response = getMenu.executeRequest();
    ArrayList<Integer> menuItems = getMenu.pickFirstTwoItems(response);
    System.out.println("Adding menu items to the cart");
    AddItemRoute addItem = new AddItemRoute();
    for (Integer itemId : menuItems) {
      addItem.setCartItemId(cartId);
      addItem.setItemId(itemId);
      addItem.setRestaurantId(this.restaurantId);
      response = addItem.executeRequest();
    }
  }

  public void placeOrder() throws InterruptedException {
    System.out.println("Placing the order");
    PlaceOrderRoute placeOrder = new PlaceOrderRoute();
    placeOrder.setCartId(cartId);
    Response response = placeOrder.executeRequest();
    System.out.println("Restaurant ID = " + restaurantId);
    System.out.println("---------------\n" + response.asPrettyString() + "\n------------------------\n");

    placeOrder.validateResponse(response);
  }

  @Override
  public Response executeRequest() {
    throw new UnsupportedOperationException("Unimplemented method 'executeRequest'");
  }

  @Override
  public boolean validateResponse(Response response) {
    throw new UnsupportedOperationException("Unimplemented method 'validateResponse'");
  }
}
