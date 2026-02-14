package com.metafit.entity.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code; // BASIC, STANDARD, PREMIUM

    @Column(nullable = false)
    private String name;

    @Column(name = "monthly_price", nullable = false)
    private BigDecimal monthlyPrice;

    @Column(name = "yearly_price", nullable = false)
    private BigDecimal yearlyPrice;

    @Column(name = "max_members")
    private Integer maxMembers;

    @Column(name = "max_trainers")
    private Integer maxTrainers;

    @Column(columnDefinition = "jsonb")
    private String features; // JSON string of features

    @Column(name = "is_active")
    private Boolean isActive = true;
}