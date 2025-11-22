package com.metafit.repository;

import com.metafit.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, UUID> {

    List<Trainer> findByIsActiveTrue();

    boolean existsByPhone(String phone);
}
