package ru.home.qa.triangle;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.assertj.core.api.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.home.qa.client.model.ErrorResponse;
import ru.home.qa.client.model.ResponseCodes;
import ru.home.qa.client.model.TriangleRequest;
import ru.home.qa.client.model.TriangleResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author astolnikov: 18.02.2020
 */
public class CRUDTriangleServiceTest extends TestBase {

    @DataProvider
    Object[] provideValidTriangles() {
        return new Object[]{

                //different sides sizes of triangle
                new TriangleRequest(0.1, 0.1, 0.1),
                new TriangleRequest(10.0, 20.55, 20.55),
                new TriangleRequest(20.55, 20.55, 20.55),
                new TriangleRequest(2.5, 2.44, 4.939),

                //most popular separators
                // you can also add more special symbols but I think these're degenerate cases
                new TriangleRequest(";", 1.0, 2.0, 3.0),
                new TriangleRequest(",", 1.0, 2.0, 3.0),
                new TriangleRequest("/", 1.0, 2.0, 3.0),
                new TriangleRequest(" ", 1.0, 2.0, 3.0),

                //double and int are in one input
                new TriangleRequest(1,10.1,10)
        };

    }

    @DataProvider
    Object[] provideInvalidTriangles() {
        return new Object[] {
                new TriangleRequest(2.5, 2.44, 4.94),
                new TriangleRequest(1.0, 2.0, 3.001),

                new TriangleRequest(3.001, 2.0, 1.0),
                new TriangleRequest(1.0, 3.001, 2.0),
                new TriangleRequest(0.0, 1.0, 2.0),
                new TriangleRequest(1.0, 2.0),
                new TriangleRequest(-1.0, 2.0, 3.0),
                new TriangleRequest().setInput("2;3;s"),

                new TriangleRequest().setInput(""),
                new TriangleRequest().setInput(null),
                null,

                new TriangleRequest("", 1.0, 2.0, 3.0),
                new TriangleRequest(".", 1.0, 2.0, 3.0),
                new TriangleRequest().setSeparator(";").setInput("1,2,3"),
        };
    }


    @Test(dataProvider = "provideValidTriangles", description = "Verify POST valid triangle request")
    void verifyPOSTValidTriangle(TriangleRequest request) {
        triangleResponse = api.create(request);

        Assertions.assertThat(triangleResponse.getStatus())
                .as("Bad response code. Error: %s. Request: %s", getErrorResponse(), request).isEqualTo(ResponseCodes.OK);
        Assertions.assertThat(triangleResponse.getBody())
                .as("Response should contain body").isNotNull();
        Assertions.assertThat(triangleResponse.getBody())
                .as("Bad response").isEqualToIgnoringGivenFields(request.getExpectedResponse(), "id");
        Assertions.assertThat(triangleResponse.getBody().getId())
                .as("Response should contain no null id").isNotNull();
    }

    @Test(dataProvider = "provideInvalidTriangles", description = "Verify POST invalid triangle request")
    void verifyPOSTInvalidTriangle(TriangleRequest request) {
        triangleResponse = api.create(request);

        Assertions.assertThat(triangleResponse.getStatus())
                .as("Incorrect response code").isEqualTo(ResponseCodes.UNPROCESSIBLE);
        Assertions.assertThat(getErrorResponse())
                .contains("Cannot process input");
    }

    @Test(description = "Verify DELETE existed triangle")
    void verifyDeleteExistedTriangleRemoved() {
        triangleResponse = api.create(new TriangleRequest(2.0, 3.0, 4.0));

        HttpResponse<JsonNode> response = api.remove(triangleResponse.getBody().getId());
        HttpResponse<TriangleResponse> triangleResponseAfter = api.get(triangleResponse.getBody().getId());

        Assertions.assertThat(response.getStatus())
                .as("Bad code").isEqualTo(ResponseCodes.OK);
        Assertions.assertThat(triangleResponseAfter.getStatus())
                .as("Bad response code for removed triangle").isEqualTo(ResponseCodes.NOT_FOUND);
        Assertions.assertThat(triangleResponseAfter.mapError(ErrorResponse.class).getMessage())
                .as("Bad error message for removed triangle").isEqualTo("Not Found");
    }

    @Test(description = "Verify DELETE nonexistent triangle")
    void verifyDeleteNoExistedTriangle() {
        HttpResponse<JsonNode> responseAfter = api.remove("badId");

        Assertions.assertThat(responseAfter.getStatus())
                .as("Bad response code for nonexistent id").isEqualTo(ResponseCodes.NOT_FOUND);
    }

    @Test(description = "Verify GET nonexistent triangle")
    void verifyNoExistedTriangle() {
        HttpResponse<TriangleResponse> response = api.get("badId");

        Assertions.assertThat(response.getStatus())
                .as("Bad response code for nonexistent id").isEqualTo(ResponseCodes.NOT_FOUND);
    }

    @Test(description = "Verify GET all triangles")
    void verifyGetAllTriangles() {
        int amount = 10;
        List<TriangleResponse> expectedList = new ArrayList<>();

        HttpResponse<List<TriangleResponse>> getAllResponseBefore = api.getAll();

        for (int i = 0; i < amount; i++) {
            expectedList.add(api.create(new TriangleRequest(i + 1, i + 1, i + 1)).getBody());
        }

        HttpResponse<List<TriangleResponse>> getAllResponseAfter = api.getAll();

        Assertions.assertThat(getAllResponseBefore.getStatus())
                .as("Something wrong happened. Error: " + getAllResponseBefore.mapError(ErrorResponse.class)).isEqualTo(ResponseCodes.OK);
        Assertions.assertThat(getAllResponseBefore.getBody())
                .as("Something wrong happened. Should be 0 size of triangles list").hasSize(0);

        Assertions.assertThat(getAllResponseAfter.getStatus())
                .as("Something wrong happened. Error: " + getAllResponseBefore.mapError(ErrorResponse.class)).isEqualTo(ResponseCodes.OK);
        Assertions.assertThat(getAllResponseAfter.getBody())
                .as("Something wrong happened. Should be 10 size of triangles list").hasSize(10);

        Assertions.assertThat(getAllResponseAfter.getBody())
                .as("Service return incorrect triangles list or bad sorting is in list").isEqualTo(expectedList);
    }

    @Test(description = "Verify POST triangles limit")
    void verifyPostLimit() {
        int amount = 12;
        List<HttpResponse<TriangleResponse>> responses = new ArrayList<>();
        TriangleRequest request = new TriangleRequest(1.0, 2.0, 3.0);

        for (int i = 0; i < amount; i++) {
            responses.add(api.create(request));
        }

        List<HttpResponse<TriangleResponse>> goodList = responses.subList(0, responses.size() - 2);
        List<HttpResponse<TriangleResponse>> badList = responses.subList(responses.size() - 2, responses.size());

        Assertions.assertThat(goodList).extracting(HttpResponse::getStatus)
                .as("Some response in 1...10 range has bad status instead successful").containsOnly(ResponseCodes.OK);
        Assertions.assertThat(goodList).extracting(HttpResponse::getBody).usingElementComparatorIgnoringFields("id")
                .as("Some response in 1...10 range contains incorrect body data").containsOnly(request.getExpectedResponse());

        Assertions.assertThat(badList).extracting(HttpResponse::getStatus)
                .as("Some response in 11...12 range has successful status instead bad").containsOnly(ResponseCodes.UNPROCESSIBLE);
        Assertions.assertThat(badList).extracting( r -> r.mapError(ErrorResponse.class)).extracting(ErrorResponse::getMessage)
                .as("Some response in 11...12 range contains incorrect error message").containsOnly("Limit exceeded");
    }
}