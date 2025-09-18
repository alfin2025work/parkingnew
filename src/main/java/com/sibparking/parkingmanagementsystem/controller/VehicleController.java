package com.sibparking.parkingmanagementsystem.controller;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;
import com.sibparking.parkingmanagementsystem.model.output.AllocateSlotResponseModel;
import com.sibparking.parkingmanagementsystem.service.VehicleEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import com.sibparking.parkingmanagementsystem.dto.VehicleEntryDto;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleEntryService vehicleService;

    // Test API - Check if API is running
    @GetMapping("/test")
    public String testConnection() {
        return "API is working successfully!";
    }

    //Adding new Vehicle (POST)
    @PostMapping("/add")
public ResponseEntity<VehicleEntry> addVehicle(@RequestBody VehicleEntry vehicleEntry) {
    VehicleEntry savedVehicle = vehicleService.addVehicle(vehicleEntry);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
}

//adding-new

@GetMapping("/check/{vehicleNumber}")
public ResponseEntity<Map<String, Object>> checkVehicle(
        @PathVariable String vehicleNumber,
        @RequestParam(required = false) String vehicleType) {

    Map<String, Object> response = new HashMap<>();
    VehicleEntry vehicle = vehicleService.getVehicleByVehicleNumber(vehicleNumber);

    if (vehicle != null && vehicle.isActive()) {
        // Already parked → return all details
        response.put("message", "Vehicle is already parked.");
        response.put("vehicleNumber", vehicle.getVehicleNumber());
        response.put("slotId", vehicle.getSlotId());
        response.put("ownerName", vehicle.getOwnerName());
        response.put("mobileNumber", vehicle.getMobileNumber());
        response.put("vehicletype", vehicle.getVehicletype());
        response.put("purpose", vehicle.getPurpose());
        response.put("active", vehicle.isActive());
        return ResponseEntity.ok(response);

    } else {
        // Vehicle exited before OR new vehicle → allocate first available slot
        String typeToUse;

        if (vehicle != null) {
            typeToUse = vehicle.getVehicletype(); // from DB
        } else if (vehicleType != null && !vehicleType.isEmpty()) {
            typeToUse = vehicleType; // from request param
        }else {
    // Vehicle type missing → return empty {}
    return ResponseEntity.ok(Collections.emptyMap());
}


        String allocatedSlot = vehicleService.allocateSlotForType(typeToUse);

        if (allocatedSlot == null) {
            response.put("message", "No available slots for " + typeToUse);
            response.put("vehicleNumber", vehicleNumber);
            response.put("vehicletype", typeToUse);
            return ResponseEntity.ok(response); // ✅ still 200
        }

        // Success – vehicle can be added
        response.put("message", "Vehicle can be added as new entry.");
        response.put("vehicleNumber", vehicleNumber);
        response.put("vehicletype", typeToUse);
        response.put("slotId", allocatedSlot);

        if (vehicle != null) {
            // include previous data for autofill
            response.put("ownerName", vehicle.getOwnerName());
            response.put("mobileNumber", vehicle.getMobileNumber());
            response.put("purpose", vehicle.getPurpose());
        }
        return ResponseEntity.ok(response);
    }
}

// New API - availability by vehicle type
@GetMapping("/slots/current/availableByType")
public ResponseEntity<?> getAvailableSlotsByType() {
    Map<String, Long> response = vehicleService.getAvailableSlotsByType();
    return ResponseEntity.ok(response);
}
@GetMapping("/{vehicleNumber}")
public ResponseEntity<?> getVehicleByvehicle(@PathVariable String vehicleNumber) {
    VehicleEntry vehicle = vehicleService.getVehicleByVehicleNumber(vehicleNumber);

    if (vehicle == null || !vehicle.isActive()) {
        // return empty curly braces {} with 200 OK
        return ResponseEntity.ok(Collections.emptyMap());
    }

    return ResponseEntity.ok(vehicle);
}
// Allocate slot based on vehicle type
@GetMapping("/allocate/{vehicleType}")
public ResponseEntity<AllocateSlotResponseModel> allocateSlot(@PathVariable String vehicleType) {
    String slotId = vehicleService.allocateSlotForType(vehicleType);
    AllocateSlotResponseModel response = new AllocateSlotResponseModel();
    if (slotId == null) {
        response.setStatus("error");
        response.setMessage("No available slots for " + vehicleType);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    response.setStatus("success");
    response.setMessage("Slot allocated successfully");
    response.setSlotId(slotId);
    return ResponseEntity.ok(response);
}


// new 15/09/25 Mark vehicle exit and update exit details
@PutMapping("/exit/{vehicleNumber}")
public ResponseEntity<?> markVehicleExit(@PathVariable String vehicleNumber) {
    try {
        VehicleEntry vehicle = vehicleService.getVehicleByVehicleNumber(vehicleNumber);
        if (vehicle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No vehicle found with number: " + vehicleNumber);
        }

        // Set exit date & time as current
        Date now = new Date();

        // Formatters
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        // Update fields
        vehicle.setExitDate(now); // full Date object for DB
        vehicle.setExitTime(timeFormat.format(now)); // store formatted string
        vehicle.setActive(false); // mark as exited

        // Save update
        VehicleEntry updated = vehicleService.updateVehicle(vehicle);

        return ResponseEntity.ok(updated);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error updating exit details: " + e.getMessage());
    }
}



// Filter vehicles by date range and optional entry/exit time range
    @GetMapping("/filter")
    public ResponseEntity<List<VehicleEntry>> getfindVehiclesWithinDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            List<VehicleEntry> vehicles = vehicleService.getVehiclesByDateAndTime(start, end, startTime, endTime);
            return ResponseEntity.ok(vehicles);

        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
@GetMapping("/vehicles/active")
public ResponseEntity<?> getActiveVehicles() {
    List<VehicleEntry> activeVehicles = vehicleService.getActiveVehicles();
    if (activeVehicles.isEmpty()) {
        // return {}
        return ResponseEntity.ok(Collections.emptyMap());
    }
    return ResponseEntity.ok(activeVehicles);
}
}




