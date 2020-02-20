package ru.home.qa.client.model;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

/**
 * @author astolnikov: 18.02.2020
 */
@Data @Accessors(chain = true)
public class TriangleRequest {
    private String separator;
    private String input;
    @Expose(serialize = false, deserialize = false)
    private TriangleResponse expectedResponse;

    /**
     * constructor
     * @param sides int, double, string, null types
     */
    public TriangleRequest(Object... sides) {
        this.input = StringUtils.join(sides, ";");
        andAddExpectedResponse(sides);
    }

    /**
     * constructor
     * @param separator
     * @param sides int, double, string, null types
     */
    public TriangleRequest(String separator, Object... sides) {
        this.separator = separator;
        this.input = StringUtils.join(sides, separator);
        andAddExpectedResponse(sides);
    }

    public TriangleRequest() {}

    private void andAddExpectedResponse(Object... lengths) {
        if (lengths.length >= 3) {
            expectedResponse = new TriangleResponse(null, Double.parseDouble(lengths[0].toString()), Double.parseDouble(lengths[1].toString()), Double.parseDouble(lengths[2].toString()));
        }
    }

    public TriangleRequest andAddExpectedResponse(String separator) {
        double[] inputs = Stream.of(input.split(separator)).mapToDouble(Double::parseDouble).toArray();
        expectedResponse = new TriangleResponse(null, inputs[0], inputs[1], inputs[2]);
        return this;
    }
}
