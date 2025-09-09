package com.sibparking.parkingmanagementsystem.repository;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface VehicleEntryRepository extends MongoRepository<VehicleEntry, String> {
    VehicleEntry findByMobileNumber(String mobileNumber);
    boolean existsBySlotIdAndExitTimeIsNull(String slotId);
    long countByExitTimeIsNull();
    List<VehicleEntry> findByEntryDateBetween(Date startDate, Date endDate);
    long countByEntryDateBeforeAndExitDateAfter(Date now1, Date now2);

    
}
