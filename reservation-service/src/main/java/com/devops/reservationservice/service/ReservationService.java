package com.devops.reservationservice.service;

import com.devops.reservationservice.dto.ReservationDTO;
import com.devops.reservationservice.model.Accommodation;
import com.devops.reservationservice.model.Reservation;
import com.devops.reservationservice.model.ReservationStatus;
import com.devops.reservationservice.repository.AccommodationRepository;
import com.devops.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final AccommodationRepository accommodationRepository;

    @Transactional
    public ReservationDTO createReservation(ReservationDTO requestDTO) {
        Accommodation accommodation = accommodationRepository.findById(requestDTO.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        List<Reservation> overlappingReservations = reservationRepository
                .findByAccommodationIdAndStatusAndStartDateBetween(
                        accommodation.getId(), ReservationStatus.CONFIRMED, requestDTO.getStartDate(), requestDTO.getEndDate());

        if (!overlappingReservations.isEmpty()) {
            throw new IllegalStateException("Accommodation is already booked for the given dates");
        }

        Reservation reservation = new Reservation();
        reservation.setAccommodation(accommodation);
        reservation.setGuestId(requestDTO.getGuestId());
        reservation.setStartDate(requestDTO.getStartDate());
        reservation.setEndDate(requestDTO.getEndDate());
        reservation.setNumberOfGuests(requestDTO.getNumberOfGuests());
        reservation.setStatus(accommodation.getAutomaticReservation() ? ReservationStatus.CONFIRMED : ReservationStatus.PENDING);

        reservationRepository.save(reservation);
        requestDTO.setId(reservation.getId());
        requestDTO.setStatus(reservation.getStatus());

        return requestDTO;
    }

    @Transactional
    public ReservationDTO confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);

            // Reject overlapping pending reservations
            List<Reservation> overlappingReservations = reservationRepository
                    .findByAccommodationIdAndStatusAndStartDateBetween(
                            reservation.getAccommodation().getId(), ReservationStatus.PENDING,
                            reservation.getStartDate(), reservation.getEndDate());

            for (Reservation overlappingReservation : overlappingReservations) {
                overlappingReservation.setStatus(ReservationStatus.REJECTED);
                reservationRepository.save(overlappingReservation);
            }
        }

        return mapToDTO(reservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            if (reservation.getStartDate().isAfter(LocalDate.now())) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
            } else {
                throw new IllegalStateException("Cannot cancel a reservation that has already started");
            }
        } else if (reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        }
    }

    private ReservationDTO mapToDTO(Reservation reservation) {
        return new ReservationDTO(
                reservation.getId(),
                reservation.getAccommodation().getId(),
                reservation.getGuestId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getNumberOfGuests(),
                reservation.getStatus()
        );
    }
}
