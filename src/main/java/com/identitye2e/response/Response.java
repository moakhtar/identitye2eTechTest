package com.identitye2e.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private Object data;
    private String message;
    private int statusCode;

    public Response() {}

    public Response(Object data, int statusCode) {
        this.data = data;
        this.statusCode = statusCode;
        this.message = null;
    }

    public Response(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
