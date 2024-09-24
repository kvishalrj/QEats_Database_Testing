package qeats.microservices.qa.routes.order_service;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.Response;
import qeats.microservices.qa.routes.BaseRoute;

public class PlaceOrderRoute extends BaseRoute {
    private String url;
    private String endpoint = "qeats/v1/order";
    private int cartId;

    public PlaceOrderRoute() {
        this.url = super.baseUri + ":" + super.orderPort + "/";
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public Response executeRequest() {
        String fullUrl = url + endpoint;
        System.out.println("Sending request to: " + fullUrl);

        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("cartId", cartId);
        System.out.println("Sending request body: ");
        System.out.println(requestBody.toString());

        Response response = given()
                .baseUri(url)
                .basePath(endpoint)
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