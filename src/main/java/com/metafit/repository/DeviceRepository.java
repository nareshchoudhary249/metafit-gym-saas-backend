package com.metafit.repository;

import com.metafit.entity.*;
import com.metafit.enums.DeviceStatus;
import com.metafit.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// ==================== DEVICE REPOSITORY ====================
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findBySerialNumber(String serialNumber);

    Optional<Device> findByApiKey(String apiKey);

    List<Device> findByDeviceType(DeviceType deviceType);

    List<Device> findByStatus(DeviceStatus status);

    List<Device> findByActiveTrue();

    @Query("SELECT d FROM Device d WHERE d.active = true AND d.status = 'ONLINE'")
    List<Device> findActiveAndOnlineDevices();

    @Query("SELECT d FROM Device d WHERE d.status = 'OFFLINE' AND d.lastPing < :threshold")
    List<Device> findOfflineDevicesSince(@Param("threshold") LocalDateTime threshold);

    List<Device> findByLocation(String location);

    boolean existsBySerialNumber(String serialNumber);

    boolean existsByApiKey(String apiKey);

    long countByStatus(DeviceStatus status);
}