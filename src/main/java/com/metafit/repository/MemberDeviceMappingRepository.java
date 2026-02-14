package com.metafit.repository;

import com.metafit.entity.*;
import com.metafit.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberDeviceMappingRepository extends JpaRepository<MemberDeviceMapping, Long> {

    List<MemberDeviceMappingRepository> findByMemberId(Long memberId);

    List<MemberDeviceMapping> findByDeviceId(Long deviceId);

    @Query("SELECT m FROM MemberDeviceMapping m WHERE " +
            "m.device.id = :deviceId AND " +
            "m.deviceIdentifier = :identifier AND " +
            "m.active = true")
    Optional<MemberDeviceMapping> findByDeviceAndIdentifier(
            @Param("deviceId") Long deviceId,
            @Param("identifier") String identifier
    );

    @Query("SELECT m FROM MemberDeviceMapping m WHERE " +
            "m.deviceIdentifier = :identifier AND " +
            "m.deviceType = :type AND " +
            "m.active = true")
    Optional<MemberDeviceMapping> findByIdentifierAndType(
            @Param("identifier") String identifier,
            @Param("type") DeviceType type
    );

    @Query("SELECT m FROM MemberDeviceMapping m WHERE " +
            "m.member.id = :memberId AND " +
            "m.deviceType = :type AND " +
            "m.active = true")
    List<MemberDeviceMapping> findByMemberIdAndDeviceType(
            @Param("memberId") Long memberId,
            @Param("type") DeviceType type
    );

    boolean existsByDeviceIdAndDeviceIdentifier(Long deviceId, String deviceIdentifier);

    @Query("SELECT COUNT(m) FROM MemberDeviceMapping m WHERE m.member.id = :memberId AND m.active = true")
    long countActiveMappingsByMember(@Param("memberId") Long memberId);
}