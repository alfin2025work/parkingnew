package com.sibparking.parkingmanagementsystem.controller;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;
import com.sibparking.parkingmanagementsystem.service.VehicleEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
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

    //  Add Vehicle (with slotId in path and availability check)
    @PostMapping("/add")
    public ResponseEntity<?> addVehicle(@PathVariable String slotId, @RequestBody VehicleEntry vehicleEntry) {
        // Check if the slot is already occupied
        if (!vehicleService.isSlotAvailable(slotId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Slot " + slotId + " is already occupied.");
        }

        // Set slotId in the vehicle entry and save
        vehicleEntry.setSlotId(slotId);
        VehicleEntry savedVehicle = vehicleService.addVehicle(vehicleEntry);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
    }


    //  Get all vehicles (entire collection)
    @GetMapping("/all")
    public ResponseEntity<List<VehicleEntry>> getAllVehicles() {
        ResponseEntity responseEntity = new ResponseEntity(vehicleService.getAllVehicles(), null, 200);
        return responseEntity;
    }

    //  Get a specific vehicle by mobile number
    @GetMapping("/mobile/{mobileNumber}")
    public ResponseEntity<?> getVehicleByMobile(@PathVariable String mobileNumber) {
        VehicleEntry vehicle = vehicleService.getVehicleByMobile(mobileNumber);
        if (vehicle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No vehicle found for mobile number: " + mobileNumber);
        }
        return ResponseEntity.ok(vehicle);
    }

    // Check if a slot is available
    @GetMapping("/slot/check/{slotId}")
    public ResponseEntity<String> checkSlotAvailability(@PathVariable String slotId) {
        boolean available = vehicleService.isSlotAvailable(slotId);
        return ResponseEntity.ok(available ? "Slot is available" : "Slot is occupied");
    }

    // Get remaining slots count
    @GetMapping("/slots/remaining")
    public ResponseEntity<String> getRemainingSlots(@RequestParam int totalSlots) {
        long remaining = vehicleService.getRemainingSlots(totalSlots);
        return ResponseEntity.ok("Remaining slots: " + remaining);
    }
    // Get vehicles by entry date range
    }
    
//new exit date range



