package com.sibparking.parkingmanagementsystem.repository;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface VehicleEntryRepository extends MongoRepository<VehicleEntry, String> {
    List<VehicleEntry> findByEntryDateBetween(Date startDate, Date endDate);
    long countByEntryDateBeforeAndExitDateAfter(Date now1, Date now2);
    List<VehicleEntry> findBySlotId(String slotId);
    VehicleEntry findByVehicleNumber(String vehicleNumber);
    List<VehicleEntry> findByActiveTrue();

}
