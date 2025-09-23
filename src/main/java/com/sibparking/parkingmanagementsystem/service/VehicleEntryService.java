package com.sibparking.parkingmanagementsystem.service;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;

import com.sibparking.parkingmanagementsystem.repository.VehicleEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.text.ParseException;
import java.util.Calendar;
import org.springframework.data.domain.Sort;


@Service
public class VehicleEntryService {

    @Autowired
    private VehicleEntryRepository vehicleRepository;


    public VehicleEntry getVehicleByVehicleNumber(String vehicleNumber) {
    List<VehicleEntry> vehicles = vehicleRepository.findByVehicleNumber(vehicleNumber);

    if (vehicles == null || vehicles.isEmpty()) {
        return null; // no record at all
    }

     // Prefer active
    return vehicles.stream()
            .filter(VehicleEntry::isActive)
            .findFirst()
            // else return the latest record (even if inactive)
            .orElse(vehicles.stream()
                    .max(Comparator.comparing(VehicleEntry::getEntryDate))
                    .orElse(null));
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
    totalSlotsByType.put("FourWheeler", 40);      // A1-A40
    totalSlotsByType.put("TwoWheeler", 50);     // B41-B90
    totalSlotsByType.put("Others", 10);  // C91-C100

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
    if (slotId.startsWith("A")) return "FourWheeler";
    if (slotId.startsWith("B")) return "TwoWheeler";
    if (slotId.startsWith("C")) return "Others";
    return "Unknown";
}
// Allocate first available slot for a vehicle type
public String allocateSlotForType(String vehicletype) {
    int start = 0, end = 0;

    switch (vehicletype.toLowerCase()) {
        case "fourwheeler":
            start = 1; end = 40; break;   // A1–A40
        case "twowheeler":
            start = 41; end = 90; break;  // B41–B90
        case "others":
            start = 91; end = 100; break;  // C91–C100
        default:
            return null;
    }

    for (int i = start; i <= end; i++) {
        String slotId;
        if (vehicletype.equalsIgnoreCase("fourwheeler")) slotId = "A" + i;
        else if (vehicletype.equalsIgnoreCase("twowheeler")) slotId = "B" + i;
        else slotId = "C" + i;

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

    public List<VehicleEntry> getVehiclesByDateAndTime(Date startDate, Date endDate,
                                                   String startTime, String endTime) {
    //  Fetch by date range directly from DB (avoid pulling all records)
    List<VehicleEntry> vehicles = vehicleRepository.findByEntryDateBetween(startDate, endDate);

    //  If time filters are provided, apply them
    if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a"); // handles "03:35 pm"
            Date filterStart = timeFormat.parse(startTime);
            Date filterEnd = timeFormat.parse(endTime);

            vehicles = vehicles.stream()
                    .filter(vehicle -> {
                        try {
                            // Parse vehicle times
                            Date entryT = (vehicle.getEntryTime() != null)
                                    ? timeFormat.parse(vehicle.getEntryTime())
                                    : null;
                            Date exitT = (vehicle.getExitTime() != null)
                                    ? timeFormat.parse(vehicle.getExitTime())
                                    : null;

                            if (entryT != null && exitT == null) {
                                // Still active → valid if entered within the window
                                return !entryT.before(filterStart) && !entryT.after(filterEnd);
                            }
                            if (entryT != null && exitT != null) {
                                // Overlap check: (entry ≤ filterEnd && exit ≥ filterStart)
                                return !entryT.after(filterEnd) && !exitT.before(filterStart);
                            }
                        } catch (ParseException e) {
                            return false; // skip invalid times
                        }
                        return true;
                    })
                    .toList();
        } catch (ParseException e) {
            // Invalid time format → ignore time filtering
        }
    }
// sort by createdAt or id (descending)
    vehicles.sort((v1, v2) -> v2.getId().compareTo(v1.getId()));
    return vehicles;
}



public VehicleEntry addVehicle(VehicleEntry vehicleEntry) {
    // Fetch all records for this vehicle number
    List<VehicleEntry> existingList = vehicleRepository.findByVehicleNumber(vehicleEntry.getVehicleNumber());

    // Check if any active record exists
    VehicleEntry activeRecord = existingList.stream()
            .filter(VehicleEntry::isActive)
            .findFirst()
            .orElse(null);

    if (activeRecord != null) {
        // Vehicle is already inside → return existing (or throw error if you want to block duplicate entry)
        return activeRecord;
    }

    // New entry or previously exited → allocate new slot
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

// Get all active vehicles in descending order of entry
public List<VehicleEntry> getActiveVehicles() {
    return vehicleRepository.findByActiveTrue(Sort.by(Sort.Direction.DESC, "_id"));
}


//getting active vehicle numbers for marking exittime
public List<String> getActiveVehiclesNumbers() {
    // Fetch active vehicles
    List<VehicleEntry> activeVehicles = vehicleRepository.findByActiveTrue(Sort.by(Sort.Direction.DESC, "_id"));
    // Map to vehicle numbers
    return activeVehicles.stream()
            .map(VehicleEntry::getVehicleNumber)
            .toList();
}
//getting unique vehicle numbers for dropdown
public List<String> getUniqueVehicleNumbers() {
        List<VehicleEntry> vehicles = vehicleRepository.findAllVehicleNumbers();
        return vehicles.stream()
            .sorted((v1, v2) -> v2.getId().compareTo(v1.getId())) // latest first
            .map(VehicleEntry::getVehicleNumber)
            .distinct() // keep only first occurrence (latest one)
            .collect(Collectors.toList());
    }
}
