package com.sibparking.parkingmanagementsystem.service;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;
import com.sibparking.parkingmanagementsystem.dto.VehicleEntryDto;
import com.sibparking.parkingmanagementsystem.repository.VehicleEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Service
public class VehicleEntryService {

    @Autowired
    private VehicleEntryRepository vehicleRepository;
    private static final int TOTAL_SLOTS = 110;


    //Get vehicle details by vehicleNumber
    public VehicleEntry getVehicleByVehicleNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber);
    }
 
//availability check date+time
public boolean isSlotCurrentlyAvailable(String slotId) {
    List<VehicleEntry> entries = vehicleRepository.findBySlotId(slotId);

    if (entries.isEmpty()) {
        return true; // slot never used → available
    }

    Date now = new Date();
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    String nowDateStr = sdfDate.format(now);
    String nowTimeStr = sdfTime.format(now);

    for (VehicleEntry entry : entries) {
        // If entry is active → slot is occupied
        if (entry.isActive()) {
            return false;
        }

        // If entry exited, still check if now is in between entry & exit date/time
        if (entry.getEntryDate() != null && entry.getExitDate() != null
                && entry.getEntryTime() != null && entry.getExitTime() != null) {

            String entryDateStr = sdfDate.format(entry.getEntryDate());
            String exitDateStr = sdfDate.format(entry.getExitDate());

            String entryTime = entry.getEntryTime();
            String exitTime = entry.getExitTime();

            boolean dateInRange = nowDateStr.compareTo(entryDateStr) >= 0 &&
                                  nowDateStr.compareTo(exitDateStr) <= 0;

            boolean timeInRange = true;
            if (nowDateStr.equals(entryDateStr) && nowDateStr.equals(exitDateStr)) {
                timeInRange = nowTimeStr.compareTo(entryTime) >= 0 &&
                              nowTimeStr.compareTo(exitTime) <= 0;
            }

            if (dateInRange && timeInRange) {
                return false; // slot occupied
            }
        }
    }

    return true; // slot free
}
//new-slots available per type of vehicle
public Map<String, Long> getAvailableSlotsByType() {
    // Define total slots by type
    Map<String, Integer> totalSlotsByType = new HashMap<>();
    totalSlotsByType.put("Car", 20);      // C1-C20
    totalSlotsByType.put("Bike", 30);     // B21-B50
    totalSlotsByType.put("Scooter", 30);  // S51-S80
    totalSlotsByType.put("Van", 20);      // V81-V100
    totalSlotsByType.put("Other", 10);    // O101-O110

    // Track occupied slots
    Map<String, Long> occupied = new HashMap<>();
    Date now = new Date();
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    String nowDateStr = sdfDate.format(now);
    String nowTimeStr = sdfTime.format(now);

    for (VehicleEntry entry : vehicleRepository.findAll()) {
        String slotId = entry.getSlotId();
        if (slotId == null) continue;

        String type = getTypeFromSlot(slotId);

        // Case 1: Active vehicle (still parked, exitDate = null)
        if (entry.isActive()) {
            occupied.put(type, occupied.getOrDefault(type, 0L) + 1);
            continue;
        }

        // Case 2: Past record with entry + exit → check if now is within that range
        if (entry.getEntryDate() != null && entry.getExitDate() != null &&
            entry.getEntryTime() != null && entry.getExitTime() != null) {

            String entryDateStr = sdfDate.format(entry.getEntryDate());
            String exitDateStr = sdfDate.format(entry.getExitDate());
            String entryTime = entry.getEntryTime();
            String exitTime = entry.getExitTime();

            boolean dateInRange = nowDateStr.compareTo(entryDateStr) >= 0 &&
                                  nowDateStr.compareTo(exitDateStr) <= 0;

            boolean timeInRange = true;
            if (nowDateStr.equals(entryDateStr) && nowDateStr.equals(exitDateStr)) {
                timeInRange = nowTimeStr.compareTo(entryTime) >= 0 &&
                              nowTimeStr.compareTo(exitTime) <= 0;
            }

            if (dateInRange && timeInRange) {
                occupied.put(type, occupied.getOrDefault(type, 0L) + 1);
            }
        }
    }

    // Calculate available slots
    Map<String, Long> available = new HashMap<>();
    for (Map.Entry<String, Integer> typeEntry : totalSlotsByType.entrySet()) {
        String type = typeEntry.getKey();
        int total = typeEntry.getValue();
        long used = occupied.getOrDefault(type, 0L);
        available.put(type, (long) total - used);
    }

    return available;
}

// Helper to detect type from slotId prefix
private String getTypeFromSlot(String slotId) {
    if (slotId.startsWith("C")) return "Car";
    if (slotId.startsWith("B")) return "Bike";
    if (slotId.startsWith("S")) return "Scooter";
    if (slotId.startsWith("V")) return "Van";
    if (slotId.startsWith("O")) return "Other";
    return "Unknown";
}
// Allocate first available slot for a vehicle type
public String allocateSlotForType(String vehicletype) {
    int start = 0, end = 0;

    switch (vehicletype.toLowerCase()) {
        case "car":
            start = 1; end = 20; break;   // C1–C20
        case "bike":
            start = 21; end = 50; break;  // B21–B50
        case "scooter":
            start = 51; end = 80; break;  // S51–S80
        case "van":
            start = 81; end = 100; break; // V81–V100
        case "other":
            start = 101; end = 110; break; // O101–O110
        default:
            return null;
    }

    for (int i = start; i <= end; i++) {
        String slotId;
        if (vehicletype.equalsIgnoreCase("car")) slotId = "C" + i;
        else if (vehicletype.equalsIgnoreCase("bike")) slotId = "B" + i;
        else if (vehicletype.equalsIgnoreCase("scooter")) slotId = "S" + i;
        else if (vehicletype.equalsIgnoreCase("van")) slotId = "V" + i;
        else slotId = "O" + i;

        // ✅ Check if slot is free at current time
        if (isSlotCurrentlyAvailable(slotId)) {
            return slotId; // first available slot
        }
    }
    return null; // no available slots
}

public VehicleEntry updateVehicle(VehicleEntry vehicle) {
    return vehicleRepository.save(vehicle);
}

// New method to get vehicles by date range and optional time

    public List<VehicleEntry> getVehiclesByDateAndTime(Date startDate, Date endDate, String startTime, String endTime) {
    // Get vehicles between startDate and endDate
    List<VehicleEntry> vehicles = vehicleRepository.findByEntryDateBetween(startDate, endDate);

    // If startTime and endTime are provided, filter by time range
    if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
        vehicles.removeIf(vehicle -> {
            String entryTime = vehicle.getEntryTime();
            String exitTime = vehicle.getExitTime();

            // If entryTime or exitTime is null, skip
            if (entryTime == null || exitTime == null) return true;

            return (entryTime.compareTo(startTime) < 0 || exitTime.compareTo(endTime) > 0);
        });
    }

    return vehicles;
}

public VehicleEntry addVehicle(VehicleEntry vehicleEntry) {
    // Check if vehicle exists
    VehicleEntry existing = vehicleRepository.findByVehicleNumber(vehicleEntry.getVehicleNumber());

    if (existing != null && existing.isActive()) {
        // Vehicle is already inside → just return existing (or throw error if needed)
        return existing;
    }

    // New entry or previously exited → allocate slot
    String allocatedSlot = allocateSlotForType(vehicleEntry.getVehicletype());
    vehicleEntry.setSlotId(allocatedSlot);

    // Set entry details
    Date now = new Date();
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");

    vehicleEntry.setEntryDate(now);
    vehicleEntry.setEntryTime(sdfTime.format(now));
    vehicleEntry.setExitDate(null);
    vehicleEntry.setExitTime(null);
    vehicleEntry.setActive(true);

    return vehicleRepository.save(vehicleEntry);
}
public List<VehicleEntry> getActiveVehicles() {
    return vehicleRepository.findByActiveTrue();
}
}
