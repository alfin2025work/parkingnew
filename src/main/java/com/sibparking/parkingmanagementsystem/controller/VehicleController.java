package com.sibparking.parkingmanagementsystem.controller;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;
import com.sibparking.parkingmanagementsystem.service.VehicleEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import com.sibparking.parkingmanagementsystem.dto.VehicleEntryDto;


import java.text.SimpleDateFormat;
import java.util.List;
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


    //  Get all vehicles (entire collection)
    @GetMapping("/all")
    public ResponseEntity<List<VehicleEntry>>getAllVehicles() {
        ResponseEntity responseEntity = new ResponseEntity(vehicleService.getAllVehicles(), null, 200);
        return responseEntity;
    }
    // correct- Add Vehicle (POST)
    @PostMapping("/add")
public ResponseEntity<VehicleEntry> addVehicle(@RequestBody VehicleEntry vehicleEntry) {
    VehicleEntry savedVehicle = vehicleService.addVehicle(vehicleEntry);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
}
    
@GetMapping("/slot/current/check/{slotId}")
public ResponseEntity<?> checkCurrentSlotAvailability(@PathVariable String slotId) {
    boolean available = vehicleService.isSlotCurrentlyAvailable(slotId);

    return ResponseEntity.ok(
        new java.util.HashMap<String, Object>() {{
            put("available", available);
        }}
    );
}


    // Filter vehicles by date range and optional time
    // Filter vehicles by date range and optional entry/exit time range
    @GetMapping("/filter")
    public ResponseEntity<List<VehicleEntry>> getVehiclesByDateAndTime(
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
    // Search only mobile numbers by prefix
@GetMapping("/searchByMobile")
public ResponseEntity<List<String>> searchByMobile(@RequestParam String prefix) {
    List<String> results = vehicleService.searchMobileNumbersByPrefix(prefix);
    return ResponseEntity.ok(results);
}
//  Get a specific vehicle by mobile number (returns reduced data)
@GetMapping("/{mobileNumber}")
public ResponseEntity<?> getVehicleByMobile(@PathVariable String mobileNumber) {
    VehicleEntryDto vehicle = vehicleService.getVehicleDTOByMobile(mobileNumber);
    if (vehicle == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No vehicle found for mobile number: " + mobileNumber);
    }
    return ResponseEntity.ok(vehicle);
}
// new-Get currently available slots
@GetMapping("/slots/current/available")
public ResponseEntity<?> getCurrentAvailableSlots() {
    long available = vehicleService.getCurrentlyAvailableSlots();
    
    // Use a Map to return JSON
    Map<String, Long> response = new HashMap<>();
    response.put("available", available);
    
    return ResponseEntity.ok(response);
}
}

    

