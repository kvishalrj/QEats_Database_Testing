package qeats.microservices.qa.routes.restaurant_service;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.restassured.response.Response;
import qeats.microservices.qa.routes.BaseRoute;

public class GetMenuRoute extends BaseRoute {
    private String url;
    private String endpoint = "qeats/v1/menu";
    private int restaurantId;

    public GetMenuRoute() {
        this.url = super.baseUri + ":" + super.restaurantPort + "/";
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Response executeRequest() {
        try {
            String fullUrl = url + endpoint + "?restaurantId=" + restaurantId;
            System.out.println("Sending request to: " + fullUrl);

            Response response = given()
                    .baseUri(url)
                    .basePath(endpoint)
                    .queryParam("restaurantId", restaurantId)
                    .when()
                    .get();

            return response;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateResponse(Response response) {
        if (response.getStatusCode() != 200) {
            return false;
        }
        Map<Object, Object> menu = response.jsonPath().getMap("menu");
        if (menu == null || menu.isEmpty()) {
            System.out.println("Warning: 'menu' key is empty in the response.");
            return false;
        }
        return true;
    }

    public ArrayList<Integer> pickFirstTwoItems(Response response) {
        List<Integer> itemList = response.jsonPath().getList("menu.items[0,1].itemId", Integer.class);
    
        return new ArrayList<>(itemList);
    }
}