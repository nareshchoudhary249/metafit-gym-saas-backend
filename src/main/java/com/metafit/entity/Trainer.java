package com.metafit.entity;

import com.metafit.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainer {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user; // Link to staff user account

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    private String email;

    private String specialization; // e.g., "Weight Training, CrossFit"

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate = LocalDate.now();

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String bio; // Trainer bio/description

    @Column(name = "max_clients")
    private Integer maxClients = 20; // Maximum members they can handle

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}