package com.sibparking.parkingmanagementsystem.model.output;

import lombok.Data;

@Data
public class CommonResponseModel {
    private String status;
    private String message;

    CommonResponseModel() {
        this.status = "success";
        this.message = "Operation completed successfully";
    }
}
