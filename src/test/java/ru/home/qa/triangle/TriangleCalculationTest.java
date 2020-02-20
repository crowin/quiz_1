package ru.home.qa.triangle;

import kong.unirest.HttpResponse;
import org.assertj.core.api.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.home.qa.client.model.ErrorResponse;
import ru.home.qa.client.model.ResponseCodes;
import ru.home.qa.client.model.Result;
import ru.home.qa.client.model.TriangleRequest;

import java.math.BigDecimal;

/**
 * @author astolnikov: 19.02.2020
 */
public class TriangleCalculationTest extends TestBase {

    @DataProvider
    Object[][] provideTriangleAndPerimeter() {
        return new Object[][] {
                {new TriangleRequest(1.09, 10.0, 10.5), 21.59},
                {new TriangleRequest(0.05, 0.04, 0.02), 0.11},
                {new TriangleRequest(10, 10, 10), 30.0},
                {new TriangleRequest(1, 10.01, 10), 21.01},
        };
    }


    @DataProvider
    Object[][] provideTriangleAndArea() {
        return new Object[][] {
                {new TriangleRequest(2.55, 2.55, 2.55), 2.815665},
                {new TriangleRequest(4.0, 2.01, 2.01), 0.4005},
                {new TriangleRequest(3.0, 4.0, 5.0), 6.0},
                {new TriangleRequest(5,4,2), 3.799671},
        };
    }

    @Test(description = "Verify perimeter calculation", dataProvider = "provideTriangleAndPerimeter")
    public void verifyPerimeterCalculation(TriangleRequest request, Double expectedResult) {
        triangleResponse = api.create(request);
        HttpResponse<Result> result = api.getPerimeter(triangleResponse.getBody().getId());

        Assertions.assertThat(result.getStatus()).as("Bad response").isEqualTo(ResponseCodes.OK);
        Assertions.assertThat(result.getBody()).as("Result should not be null").isNotNull();
        Assertions.assertThat(result.getBody().getResult()).as("Backend return bad result").isEqualTo(expectedResult);
    }

    @Test(description = "Verify perimeter calculation by bad id")
    public void verifyPerimeterByBadId() {
        HttpResponse<Result> result = api.getPerimeter("badId");

        Assertions.assertThat(result.getStatus()).as("Successful response instead bad")
                .isEqualTo(ResponseCodes.NOT_FOUND);
        Assertions.assertThat(result.mapError(ErrorResponse.class).getMessage())
                .as("Bad error message").isEqualTo("Not Found");
    }

    @Test(description = "Verify area calculation", dataProvider = "provideTriangleAndArea")
    public void verifyAreaCalculation(TriangleRequest request, Double expectedResult) {
        triangleResponse = api.create(request);
        HttpResponse<Result> result = api.getArea(triangleResponse.getBody().getId());

        Assertions.assertThat(result.getStatus())
                .as("Bad response").isEqualTo(ResponseCodes.OK);
        Assertions.assertThat(result.getBody())
                .as("Result should not be null").isNotNull();
        Assertions.assertThat(roundNumber(result.getBody().getResult()))
                .as("Backend return bad result").isEqualTo(roundNumber(expectedResult));
    }

    @Test(description = "Verify area calculation by bad id")
    public void verifyAreaByBadId() {
        HttpResponse<Result> result = api.getArea("badId");

        Assertions.assertThat(result.getStatus()).as("Successful response instead bad")
                .isEqualTo(ResponseCodes.NOT_FOUND);
        Assertions.assertThat(result.mapError(ErrorResponse.class).getMessage())
                .as("Bad error message").isEqualTo("Not Found");
    }

    private Double roundNumber(Double number) {
        return new BigDecimal(number).setScale(4, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }
}
