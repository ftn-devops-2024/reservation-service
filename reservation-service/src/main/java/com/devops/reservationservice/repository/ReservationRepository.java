package com.devops.reservationservice.repository;

import com.devops.reservationservice.model.Reservation;
import com.devops.reservationservice.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByAccommodationIdAndStatus(Long accommodationId, ReservationStatus status);
    List<Reservation> findByAccommodationIdAndStatusAndStartDateBetween(
            Long accommodationId, ReservationStatus status, LocalDate startDate1, LocalDate endDate1);

    List<Reservation> findByGuestId(String guestId);

    List<Reservation> findByAccommodationOwnerId(String ownerId);
}
