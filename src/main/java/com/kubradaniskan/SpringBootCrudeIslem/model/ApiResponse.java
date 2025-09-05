package com.kubradaniskan.SpringBootCrudeIslem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ApiResponse {
    private String message;
    private Object data;
    private String status; // Success, Warning, Error

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
        this.status = "Success"; // default
    }

    public String toJson() {

        return "{\"message\":\"" + message + "\", \"data\":" + (data != null ? data.toString() : "null") + "}";
    }
}
