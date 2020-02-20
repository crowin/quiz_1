package ru.home.qa.triangle;

import kong.unirest.HttpResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import ru.home.qa.client.TriangleApi;
import ru.home.qa.client.model.ErrorResponse;
import ru.home.qa.client.model.TriangleResponse;

import java.util.List;

/**
 * @author astolnikov: 19.02.2020
 */
public class TestBase {
    public TriangleApi api;
    public HttpResponse<TriangleResponse> triangleResponse;

    @BeforeClass(alwaysRun = true)
    public void initClient() {
        api = new TriangleApi();
    }

    //because of limited triangles amount
    @BeforeMethod(alwaysRun = true)
    public void removeTriangle() {
        List<TriangleResponse> list = api.getAll().getBody();
        list.forEach(r -> api.remove(r.getId()));
    }



    public String getErrorResponse() {
        ErrorResponse error = triangleResponse.mapError(ErrorResponse.class);
        if (error == null) return "none";

        return error.getPath() + ": " + error.getMessage();
    }

}
