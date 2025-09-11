package com.sibparking.parkingmanagementsystem.service;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;
import com.sibparking.parkingmanagementsystem.dto.VehicleEntryDto;
import com.sibparking.parkingmanagementsystem.repository.VehicleEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class VehicleEntryService {

    @Autowired
    private VehicleEntryRepository vehicleRepository;
    private static final int TOTAL_SLOTS = 100;


    // Save a vehicle entry
    public VehicleEntry addVehicle(VehicleEntry vehicleEntry) {
    return vehicleRepository.save(vehicleEntry);
}

    // Get all vehicle entries
    public List<VehicleEntry> getAllVehicles() {
        return vehicleRepository.findAll();
    }
    //Get vehicle details by mobile number
    public VehicleEntry getVehicleByMobile(String mobileNumber) {
        return vehicleRepository.findByMobileNumber(mobileNumber);
    }
// Check if a slot is available
public boolean isSlotAvailable(String slotId) {
    // Slot is available if no vehicle is currently occupying it (exitTime == null)
    return !vehicleRepository.existsBySlotIdAndExitTimeIsNull(slotId);
}


// Count remaining slots
    public long getRemainingSlots(int totalSlots) {
    Date now = new Date();
    long occupied = vehicleRepository.countByEntryDateBeforeAndExitDateAfter(now, now);
    return totalSlots - occupied;
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
        vehicle.getVehicletype()
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



}

