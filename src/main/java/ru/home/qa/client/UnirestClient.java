package ru.home.qa.client;

import kong.unirest.ContentType;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import ru.home.qa.app.ConfigProperties;

/**
 * @author astolnikov: 18.02.2020
 */
public class UnirestClient {

    private UnirestInstance client;

    public UnirestClient() {
        client = Unirest.spawnInstance();
        setDefaultToken();
        setBaseHeaders();
    }

    public UnirestClient(String token) {
        client = Unirest.spawnInstance();
        setBaseHeaders();
        setUserToken(token);
    }

    public UnirestInstance client() {
        return client;
    }

    private void setDefaultToken() {
        setUserToken(ConfigProperties.getInstance().getProperty("user.token"));
    }

    private void setBaseHeaders() {
        client.config().addDefaultHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
    }

    public void setUserToken(String token) {
        client.config().addDefaultHeader("X-User", token);
    }

    public void applyDefault() {
        setDefaultToken();
        setBaseHeaders();
    }

}
