package com.tripmind.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tripmind.backend.entity.Trip;

public interface TripRepository extends JpaRepository<Trip,Long>{
     List<Trip> findByUser_Id(Long userId);
     
     Optional<Trip> findByIdAndUser_Id(Long id,Long userId);
}
