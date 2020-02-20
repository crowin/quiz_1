package ru.home.qa.client.model;

import lombok.Data;

/**
 * @author astolnikov: 19.02.2020
 */
@Data
public class TriangleResponse {
    private String id;
    private Double firstSide;
    private Double secondSide;
    private Double thirdSide;

    public TriangleResponse(String id, Double firstSide, Double secondSide, Double thirdSide) {
        this.id = id;
        this.firstSide = firstSide;
        this.secondSide = secondSide;
        this.thirdSide = thirdSide;
    }

    public TriangleResponse() { }
}
