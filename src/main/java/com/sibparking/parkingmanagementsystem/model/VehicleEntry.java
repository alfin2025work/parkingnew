package com.sibparking.parkingmanagementsystem.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;




@Document(collection = "newvehicleentry") // MongoDB collection name
public class VehicleEntry {

    @Id
    private String id;
    private String slotId;
    private String vehicleNumber;
    private String ownerName;
    private String mobileNumber;
    private String vehicletype;
    private String purpose;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date   entryDate;
    private String entryTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date   exitDate;
    private String exitTime;
    private String status; // e.g., "ENTERED", "EXITED"
    // Constructors, Getters, and Seters

    public VehicleEntry() {
        // Default constructor
    }

    public VehicleEntry(String slotId, String vehicleNumber, String ownerName, String mobileNumber, String vehicletype, String purpose, Date entryDate, String entryTime, Date exitDate, String exitTime,String status) {
        this.slotId = slotId;
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.mobileNumber = mobileNumber;
        this.vehicletype = vehicletype;
        this.purpose = purpose;
        this.entryDate = entryDate;
        this.entryTime = entryTime;
        this.exitDate = exitDate;
        this.exitTime = exitTime;
        this.status = status;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSlotId() {
        return slotId;
    }
    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    public String getVehicletype() {
        return vehicletype;
    }
    public void setVehicletype(String vehicletype) {
        this.vehicletype = vehicletype;
    }
    public String getPurpose() {
        return purpose;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public Date getEntryDate() {
        return entryDate;
    }
    public void setEntrydate(Date entryDate) {
        this.entryDate = entryDate;
    }
    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }
    
    public Date getExitDate() {
        return exitDate;
    }
    public void setExitdate(Date exitDate) {
        this.exitDate = exitDate;
    }
    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }
    public String getStatus() {
        return status;
    }
    @JsonIgnore
    public void setStatus(String status) {
        this.status = status;
    }
}