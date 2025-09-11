package com.sibparking.parkingmanagementsystem.dto;

public class VehicleEntryDto {
    private String id;
    private String vehicleNumber;
    private String ownerName;
    private String mobileNumber;
    private String vehicletype;

    public VehicleEntryDto() {}

    public VehicleEntryDto(String id, String vehicleNumber, String ownerName, String mobileNumber, String vehicletype) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.mobileNumber = mobileNumber;
        this.vehicletype = vehicletype;
    }

    public String getId() { return id; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getOwnerName() { return ownerName; }
    public String getMobileNumber() { return mobileNumber; }
    public String getVehicletype() { return vehicletype; }

    public void setId(String id) { this.id = id; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public void setVehicletype(String vehicletype) { this.vehicletype = vehicletype; }
}

