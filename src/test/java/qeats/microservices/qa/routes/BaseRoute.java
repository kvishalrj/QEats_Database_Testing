package qeats.microservices.qa.routes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.restassured.response.Response;

public abstract class BaseRoute {
    protected String baseUri;
    protected String orderPort;
    protected String orderDBPort;
    protected String restaurantPort;
    protected String restaurantDBPort;
    protected String orderDbName;
    protected String restaurantDbName;
    protected String orderDbUser;
    protected String restaurantDbUser;
    protected String orderDbPassword;
    protected String restaurantDbPassword;

    public BaseRoute() {
        Properties prop = loadProperties();
        this.baseUri = "http://" + prop.getProperty("qeats_base_uri");
        this.orderPort = prop.getProperty("qeats_order_port");
        this.orderDBPort = prop.getProperty("qeats_order_db_port");
        this.restaurantPort = prop.getProperty("qeats_restaurant_port");
        this.restaurantDBPort = prop.getProperty("qeats_restaurant_db_port");
        this.orderDbName = prop.getProperty("qeats_order_db_name");
        this.restaurantDbName = prop.getProperty("qeats_restaurant_db_name");
        this.orderDbUser = prop.getProperty("qeats_order_db_user");
        this.restaurantDbUser = prop.getProperty("qeats_restaurant_db_user");
        this.orderDbPassword = prop.getProperty("qeats_order_db_pwd");
        this.restaurantDbPassword = prop.getProperty("qeats_restaurant_db_pwd");

    }

    private Properties loadProperties() {
        String profile = System.getProperty("test.profile", "default");
        String fileName = "application-qa.properties";
        if ("qa".equals(profile)) {
            fileName = "application-qa.properties";
        } else if ("stage".equals(profile)) {
            fileName = "application-stage.properties";
        }

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find " + fileName);
                return null;
            }
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public abstract Response executeRequest();

    public abstract boolean validateResponse(Response response);
}
