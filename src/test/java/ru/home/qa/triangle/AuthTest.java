package ru.home.qa.triangle;

import kong.unirest.HttpResponse;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;
import ru.home.qa.client.TriangleApi;
import ru.home.qa.client.model.ErrorResponse;
import ru.home.qa.client.model.ResponseCodes;
import ru.home.qa.client.model.TriangleRequest;
import ru.home.qa.client.model.TriangleResponse;

import java.util.List;

/**
 * @author astolnikov: 19.02.2020
 */
public class AuthTest extends TestBase {

    @Test(description = "POST triangle by bad token")
    public void verifyPOSTByBadToken() {
        HttpResponse<TriangleResponse> badResponse = badTokenClient().create(new TriangleRequest(1.0, 2.0, 3.0));

        List<TriangleResponse> getAllResponse = api.getAll().getBody();

        Assertions.assertThat(badResponse.getStatus())
                .as("Incorrect response code").isEqualTo(ResponseCodes.UNAUTHORIZED);
        Assertions.assertThat(badResponse.mapError(ErrorResponse.class).getMessage())
                .as("Incorrect error message").isEqualTo("No message available");
        Assertions.assertThat(getAllResponse)
                .as("Trianage should not be created").hasSize(0);
    }

    @Test(description = "GET all triangles by bad token")
    public void verifyGETByBadToken() {
        HttpResponse<List<TriangleResponse>> badResponse = badTokenClient().getAll();

        Assertions.assertThat(badResponse.getStatus())
                .as("Incorrect response code").isEqualTo(ResponseCodes.UNAUTHORIZED);
        Assertions.assertThat(badResponse.mapError(ErrorResponse.class).getMessage())
                .as("Incorrect error message").isEqualTo("No message available");
    }

    //Also you can add other cases of GET, DELETE, etc endpoints for permissions testing by bad token/without token


    public TriangleApi badTokenClient() {
        return new TriangleApi("bad-token");
    }
}
