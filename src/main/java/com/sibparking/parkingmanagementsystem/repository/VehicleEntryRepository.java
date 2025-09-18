package com.sibparking.parkingmanagementsystem.repository;

import com.sibparking.parkingmanagementsystem.model.VehicleEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface VehicleEntryRepository extends MongoRepository<VehicleEntry, String> {
    List<VehicleEntry> findByEntryDateBetween(Date startDate, Date endDate);
    long countByEntryDateBeforeAndExitDateAfter(Date now1, Date now2);
    List<VehicleEntry> findBySlotId(String slotId);
    List<VehicleEntry> findByVehicleNumber(String vehicleNumber);
    List<VehicleEntry> findByActiveTrue();
    @Query("SELECT v FROM VehicleEntry v " +
       "WHERE (v.entryDate BETWEEN :startDate AND :endDate) " +
       "   OR (v.exitDate BETWEEN :startDate AND :endDate) " +
       "   OR (v.entryDate <= :startDate AND (v.exitDate IS NULL OR v.exitDate >= :endDate))")
List<VehicleEntry> findVehiclesWithinDateRange(@Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate);

}

