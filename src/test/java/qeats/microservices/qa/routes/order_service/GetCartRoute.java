package qeats.microservices.qa.routes.order_service;
import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import qeats.microservices.qa.routes.BaseRoute;

public class GetCartRoute extends BaseRoute {
    private String url;
    private String endpoint = "qeats/v1/cart";
    private String userId;

    public GetCartRoute() {
        this.url = super.baseUri + ":" + super.orderPort + "/";
    }

    public void setUserId(String string){
        this.userId = string;
    }

    public Response executeRequest() {
        try {
            String fullUrl = url + endpoint + "?userId=" + userId;
            System.out.println("Sending request to: " + fullUrl);

            Response response = given()
                    .baseUri(url)
                    .basePath(endpoint)
                    .queryParam("userId", userId)
                    .when()
                    .get();

            return response;
        } catch (Exception e) {
            return null;
        }
    }
    
    public int getCartId(Response response){
        return response.jsonPath().getInt("id");
    }
    public int getRestaurantId(Response response){
        return response.jsonPath().getInt("restaurantId");
    }

    public boolean validateResponse(Response response) {
        if (response.getStatusCode() != 200) {
            return false;
        }
        String menu = response.jsonPath().getString("id");
        if (menu == null || menu.isEmpty()) {
            System.out.println("Warning: Cart ID key is empty in the response.");
            return false;
        }
        return true;
    }
}