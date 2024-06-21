package com.devops.reservationservice.service;

import com.devops.reservationservice.dto.ReservationDTO;
import com.devops.reservationservice.dto.UserDTO;
import com.devops.reservationservice.model.Accommodation;
import com.devops.reservationservice.model.Reservation;
import com.devops.reservationservice.model.ReservationStatus;
import com.devops.reservationservice.repository.AccommodationRepository;
import com.devops.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final AccommodationRepository accommodationRepository;
    @Autowired
    private RestTemplate restTemplate;

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

        return convertToDTO(reservation);
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



    public List<ReservationDTO> getUserReservations(String userId, String accessToken, String fingerprint) {
        System.out.println(accessToken);
        System.out.println(fingerprint);

        List<Reservation> reservations = reservationRepository.findByGuestId(userId);
        reservations.addAll(reservationRepository.findByAccommodationOwnerId(userId));



        List<ReservationDTO> reservationDTOS = reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        for (ReservationDTO reservation:reservationDTOS){
            UserDTO owner = getUserDetails(reservation.getOwnerId(), accessToken, fingerprint);
            reservation.setOwnerName(owner.getName());
            reservation.setOwnerSurname(owner.getSurname());
        }

        return reservationDTOS;


    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setAccommodationId(reservation.getAccommodation().getId());
        dto.setGuestId(reservation.getGuestId());
        dto.setStartDate(reservation.getStartDate());
        dto.setEndDate(reservation.getEndDate());
        dto.setNumberOfGuests(reservation.getNumberOfGuests());
        dto.setStatus(reservation.getStatus());
        dto.setAccommodationName(reservation.getAccommodation().getName());
        dto.setOwnerId(reservation.getAccommodation().getOwnerId());
        dto.setCancellationCount(getCancellationCount(reservation.getGuestId()));

        return dto;
    }

    private UserDTO getUserDetails(String userId, String accessToken, String fingerprint) {

        System.out.println(accessToken);
        System.out.println(fingerprint);

        String url = "http://localhost:8000/auth-service/api/user/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",   accessToken);
        headers.add("Cookie", "Fingerprint=" + fingerprint);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserDTO.class);
        return response.getBody();

    }

    private int getCancellationCount(String guestId) {
        List<Reservation> reservations = reservationRepository.findByGuestId(guestId);
        return (int) reservations.stream()
                .filter(res -> res.getStatus() == ReservationStatus.CANCELLED)
                .count();
    }
}
