package com.devops.reservationservice.repository;


import com.devops.reservationservice.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Query("SELECT a FROM Accommodation a " +
            "LEFT JOIN FETCH a.availabilityPeriods " +
            "LEFT JOIN FETCH a.specialPrices " +
            "LEFT JOIN FETCH a.photos " +
            "WHERE a.id = :id")
    Optional<Accommodation> findByIdWithAssociations(@Param("id") Long id);

    @Query("SELECT DISTINCT a FROM Accommodation a " +
            "LEFT JOIN FETCH a.availabilityPeriods " +
            "LEFT JOIN FETCH a.specialPrices " +
            "LEFT JOIN FETCH a.photos")
    List<Accommodation> findAllWithAssociations();

    List<Accommodation> findByOwnerId(String ownerId);

    @Query("SELECT a FROM Accommodation a " +
            "JOIN a.availabilityPeriods ap " +
            "WHERE a.location = :location " +
            "AND a.minGuests <= :numGuests " +
            "AND a.maxGuests >= :numGuests " +
            "AND ap.startDate <= :startDate " +
            "AND ap.endDate >= :endDate")
    List<Accommodation> searchAccommodations(
            @Param("location") String location,
            @Param("numGuests") int numGuests,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

