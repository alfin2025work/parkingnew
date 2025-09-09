package com.sibparking.parkingmanagementsystem.service;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;
import com.sibparking.parkingmanagementsystem.repository.VehicleEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

@Service
public class VehicleEntryService {

    @Autowired
    private VehicleEntryRepository vehicleRepository;

    // Save a vehicle entry
    public VehicleEntry addVehicle(VehicleEntry vehicleEntry) {
        // Check if slot is already occupied
        if (vehicleRepository.existsBySlotIdAndExitTimeIsNull(vehicleEntry.getSlotId())) {
            throw new RuntimeException("Slot " + vehicleEntry.getSlotId() + " is already occupied!");
        }
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
    //slotcheck
    public boolean isSlotAvailable(String slotId) {
    return !vehicleRepository.existsBySlotIdAndExitTimeIsNull(slotId);
}
// Count remaining slots
    public long getRemainingSlots(int totalSlots) {
    Date now = new Date();
    long occupied = vehicleRepository.countByEntryDateBeforeAndExitDateAfter(now, now);
    return totalSlots - occupied;
}
    
    

}

