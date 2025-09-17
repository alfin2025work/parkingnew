package com.sibparking.parkingmanagementsystem.model.output;

import lombok.Data;

@Data
public class AllocateSlotResponseModel extends CommonResponseModel {
    private String slotId;
}
