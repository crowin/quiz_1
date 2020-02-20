package ru.home.qa.client.model;

import lombok.Data;

/**
 * @author astolnikov: 19.02.2020
 */

@Data
public class ErrorResponse {
    private long timestamp;
    private Integer status;
    private String error;
    private String exception;
    private String message;
    private String path;
}
