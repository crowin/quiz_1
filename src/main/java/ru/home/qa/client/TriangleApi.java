package ru.home.qa.client;

import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import lombok.Getter;
import ru.home.qa.app.ConfigProperties;
import ru.home.qa.client.model.Result;
import ru.home.qa.client.model.TriangleRequest;
import ru.home.qa.client.model.TriangleResponse;

import java.util.List;

/**
 * @author astolnikov: 18.02.2020
 */
public class TriangleApi {
    private UnirestClient unirest;

    public TriangleApi(String... args) {
        if (args.length > 0 && args[0] != null) {
            this.unirest = new UnirestClient(args[0]);
        } else this.unirest = new UnirestClient();
    }

    private final String BASE_URL = ConfigProperties.getInstance().getProperty("web.url");

    public HttpResponse<TriangleResponse> create(TriangleRequest triangle) {
        return unirest.client().post(BASE_URL + "triangle").body(triangle).asObject(TriangleResponse.class);
    }

    public HttpResponse<JsonNode> remove(String rectangleId) {
        return unirest.client().delete(BASE_URL + "triangle/{id}").routeParam("id", rectangleId).asJson();
    }

    public HttpResponse<TriangleResponse> get(String rectangleId) {
        return unirest.client().get(BASE_URL + "triangle/{id}").routeParam("id", rectangleId).asObject(TriangleResponse.class);
    }

    public HttpResponse<List<TriangleResponse>> getAll() {
        return unirest.client().get(BASE_URL + "triangle/all").asObject(new GenericType<List<TriangleResponse>>() {});
    }

    public HttpResponse<Result> getPerimeter(String rectangleId) {
        return unirest.client().get(BASE_URL + "triangle/{id}/perimeter").routeParam("id", rectangleId).asObject(Result.class);

    }

    public HttpResponse<Result> getArea(String rectangleId) {
        return unirest.client().get(BASE_URL + "triangle/{id}/area").routeParam("id", rectangleId).asObject(Result.class);
    }
}
