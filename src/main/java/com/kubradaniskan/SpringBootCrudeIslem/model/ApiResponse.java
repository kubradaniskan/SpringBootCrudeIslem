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

}