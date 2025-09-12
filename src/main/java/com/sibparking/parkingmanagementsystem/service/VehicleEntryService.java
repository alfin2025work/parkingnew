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
    private static final int TOTAL_SLOTS = 100;


    // Save a vehicle entry
    public VehicleEntry addVehicle(VehicleEntry vehicleEntry) {
        
    return vehicleRepository.save(vehicleEntry);
}

    //Get vehicle details by mobile number
    public VehicleEntry getVehicleByMobile(String mobileNumber) {
        return vehicleRepository.findByMobileNumber(mobileNumber);
    }


    //Get vehicle details by vehicleNumber
    public VehicleEntry getVehicleByVehicleNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber);
    }


// Check if a slot is available
public boolean isSlotAvailable(String slotId) {
    // Slot is available if no vehicle is currently occupying it (exitTime == null)
    return !vehicleRepository.existsBySlotIdAndExitTimeIsNull(slotId);
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
// Search only mobile numbers by prefix
    public List<String> searchMobileNumbersByPrefix(String prefix) {
        return vehicleRepository.findByMobileNumberStartingWith(prefix)
                .stream()
                .map(VehicleEntry::getMobileNumber) // extract only number
                .toList();
    }

    // Return DTO (without entry/exit date/time)
public VehicleEntryDto getVehicleDTOByMobile(String mobileNumber) {
    VehicleEntry vehicle = vehicleRepository.findByMobileNumber(mobileNumber);
    if (vehicle == null) return null;

    return new VehicleEntryDto(
        vehicle.getId(),
        vehicle.getVehicleNumber(),
        vehicle.getOwnerName(),
        vehicle.getMobileNumber(),
        vehicle.getVehicletype(),
        vehicle.getPurpose()
    );
}
//availability check date+time
public boolean isSlotCurrentlyAvailable(String slotId) {
    List<VehicleEntry> entries = vehicleRepository.findBySlotId(slotId);

    if (entries.isEmpty()) {
        return true; // no entries means slot never used → available
    }

    Date now = new Date();
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    String nowDateStr = sdfDate.format(now);
    String nowTimeStr = sdfTime.format(now);

    for (VehicleEntry entry : entries) {
        if (entry.getEntryDate() == null || entry.getExitDate() == null) continue;

        String entryDateStr = sdfDate.format(entry.getEntryDate());
        String exitDateStr = sdfDate.format(entry.getExitDate());

        String entryTime = entry.getEntryTime();
        String exitTime = entry.getExitTime();

        // Check if today falls within the entry and exit dates
        boolean dateInRange = (nowDateStr.compareTo(entryDateStr) >= 0 &&
                               nowDateStr.compareTo(exitDateStr) <= 0);

        // If same day → check time also
        boolean timeInRange = true;
        if (nowDateStr.equals(entryDateStr) && nowDateStr.equals(exitDateStr)) {
            timeInRange = (nowTimeStr.compareTo(entryTime) >= 0 &&
                           nowTimeStr.compareTo(exitTime) <= 0);
        }

        if (dateInRange && timeInRange) {
            return false; // slot is occupied now
        }
    }
    return true; // no active booking found
}

// showing currently available slots above dashboard
public long getCurrentlyAvailableSlots() {
    long occupied = 0;
    Date now = new Date();

    List<VehicleEntry> allEntries = vehicleRepository.findAll();

    for (VehicleEntry entry : allEntries) {
        if (entry.getEntryDate() == null || entry.getExitDate() == null) continue;

        // Compare current date/time with entry/exit
        String nowDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(now);
        String nowTimeStr = new java.text.SimpleDateFormat("HH:mm").format(now);

        String entryDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(entry.getEntryDate());
        String exitDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(entry.getExitDate());

        String entryTime = entry.getEntryTime();
        String exitTime = entry.getExitTime();

        boolean dateInRange = (nowDateStr.compareTo(entryDateStr) >= 0 &&
                               nowDateStr.compareTo(exitDateStr) <= 0);

        boolean timeInRange = true;
        if (nowDateStr.equals(entryDateStr) && nowDateStr.equals(exitDateStr)) {
            timeInRange = (nowTimeStr.compareTo(entryTime) >= 0 &&
                           nowTimeStr.compareTo(exitTime) <= 0);
        }

        if (dateInRange && timeInRange) {
            occupied++;
        }
    }

    return TOTAL_SLOTS - occupied;
}




//new-slots available per type of vehicle
public Map<String, Long> getAvailableSlotsByType() {
    // Define slot ranges
    Map<String, Integer> totalSlotsByType = new HashMap<>();
    totalSlotsByType.put("Car", 20);      // C1-C20
    totalSlotsByType.put("Bike", 30);     // B21-B50
    totalSlotsByType.put("Scooter", 30);  // S51-S80
    totalSlotsByType.put("Van", 20);      // V81-V100
    totalSlotsByType.put("Other", 20);    // O101-O110 (optional)

    // Count occupied slots
    Map<String, Long> occupied = new HashMap<>();
    Date now = new Date();
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    String nowDateStr = sdfDate.format(now);
    String nowTimeStr = sdfTime.format(now);

    for (VehicleEntry entry : vehicleRepository.findAll()) {
        if (entry.getEntryDate() == null || entry.getExitDate() == null) continue;

        String entryDateStr = sdfDate.format(entry.getEntryDate());
        String exitDateStr = sdfDate.format(entry.getExitDate());

        String entryTime = entry.getEntryTime();
        String exitTime = entry.getExitTime();

        boolean dateInRange = (nowDateStr.compareTo(entryDateStr) >= 0 &&
                               nowDateStr.compareTo(exitDateStr) <= 0);

        boolean timeInRange = true;
        if (nowDateStr.equals(entryDateStr) && nowDateStr.equals(exitDateStr)) {
            timeInRange = (nowTimeStr.compareTo(entryTime) >= 0 &&
                           nowTimeStr.compareTo(exitTime) <= 0);
        }

        if (dateInRange && timeInRange) {
            String slotId = entry.getSlotId();
            if (slotId != null) {
                String type = getTypeFromSlot(slotId);
                occupied.put(type, occupied.getOrDefault(type, 0L) + 1);
            }
        }
    }

    // Calculate available
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
}
