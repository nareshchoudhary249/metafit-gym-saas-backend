package com.metafit.dto.response.trainer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import com.metafit.dto.response.member.MemberResponse;

import java.util.List;

/**
 * Detailed response DTO for Trainer entity
 * Includes assigned members and capacity information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDetailResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private String bio;

    // Capacity Management
    private Integer maxClients;
    private Integer currentClients; // Number of currently assigned members
    private Boolean hasCapacity; // true if currentClients < maxClients
    private Double capacityPercentage; // (currentClients / maxClients) * 100

    // Status
    private Boolean active;

    // Assigned Members (detailed list)
    private List<MemberResponse> assignedMembers;

    // Audit Information
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy; // Username of creator
    private String updatedBy; // Username of last updater

    /**
     * Calculate capacity percentage
     */
    public void calculateCapacityPercentage() {
        if (maxClients != null && maxClients > 0) {
            this.capacityPercentage = (currentClients.doubleValue() / maxClients) * 100;
        }
    }

}
