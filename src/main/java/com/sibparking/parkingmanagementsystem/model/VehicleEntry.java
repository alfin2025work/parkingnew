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
    private boolean active;
    // Constructors, Getters, and Seters

    public VehicleEntry() {
        // Default constructor
    }

    public VehicleEntry(String slotId, String vehicleNumber, String ownerName, String mobileNumber, String vehicletype, String purpose, Date entryDate, String entryTime) {
        this.slotId = slotId;
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.mobileNumber = mobileNumber;
        this.vehicletype = vehicletype;
        this.purpose = purpose;
        this.entryDate = entryDate;
        this.entryTime = entryTime;
        this.exitDate = null;
        this.exitTime = null;
        this.active=true;
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
    public void setEntryDate(Date entryDate) {
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
    public void setExitDate(Date exitDate) {
        this.exitDate = exitDate;
    }
    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active =active;
    }
}