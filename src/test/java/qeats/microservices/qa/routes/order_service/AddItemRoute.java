package qeats.microservices.qa.routes.order_service;
import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.Response;
import qeats.microservices.qa.routes.BaseRoute;

public class AddItemRoute extends BaseRoute {
    private String url;
    private String endpoint = "qeats/v1/cart/item";
    private int cartId;
    private int itemId;
    private int restaurantId;

    public AddItemRoute() {
        this.url = super.baseUri + ":" + super.orderPort + "/";
    }

    public void setCartItemId(int cartId){
        this.cartId = cartId;
    }

    public void setItemId(int itemId){
        this.itemId = itemId;
    }

    public void setRestaurantId(int restaurantId){
        this.restaurantId = restaurantId;
    }

    public Response executeRequest() {
        String fullUrl = this.url + this.endpoint;
        System.out.println("Sending request to: " + fullUrl);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("restaurantId", String.valueOf(this.restaurantId));
        requestBody.put("cartId", String.valueOf(this.cartId));
        requestBody.put("itemId", String.valueOf(this.itemId));
        System.out.println("Sending request body: ");
        System.out.println(requestBody.toString());


        Response response = given()
            .baseUri(this.url)
            .basePath(this.endpoint)
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post();
        return response;
    }

    public boolean validateResponse(Response response) {
        if (response.getStatusCode() != 200) {
            return false;
        }
        return true;
    }
}