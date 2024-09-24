package qeats.microservices.qa.routes.restaurant_service;

import static io.restassured.RestAssured.given;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.restassured.response.Response;
import qeats.microservices.qa.routes.BaseRoute;

public class GetRestaurantsRoute extends BaseRoute {
    private String url;
    private String endpoint = "qeats/v1/restaurants";
    private String lat;
    private String lon;

    public GetRestaurantsRoute() {
        this.url = super.baseUri + ":" + super.restaurantPort + "/";
    }

    public void setLatLon(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public Response executeRequest() {
        try {
            String encodedLat = URLEncoder.encode(lat, StandardCharsets.UTF_8.toString());
            String encodedLon = URLEncoder.encode(lon, StandardCharsets.UTF_8.toString());
            String fullUrl = url + endpoint + "?latitude=" + encodedLat + "&longitude=" + encodedLon;
            System.out.println("Sending request to: " + fullUrl);

            Response response = given()
                    .baseUri(url)
                    .basePath(endpoint)
                    .queryParam("latitude", encodedLat)
                    .queryParam("longitude", encodedLon)
                    .when()
                    .get();

            return response;
        } catch (Exception e) {
            return null;
        }
    }

    public int getFirstRestaurantId(Response response){
        return response.jsonPath().getInt("restaurants[0].restaurantId");
    }

    public boolean validateResponse(Response response) {
        if (response.getStatusCode() != 200) {
            return false;
        }
        List<Object> restaurants = response.jsonPath().getList("restaurants");
        if (restaurants == null || restaurants.isEmpty()) {
            System.out.println("Warning: 'restaurants' key is empty in the response.");
            return false;
        }
        return true;
    }
}
