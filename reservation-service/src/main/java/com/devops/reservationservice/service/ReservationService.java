package com.devops.reservationservice.service;

import com.devops.reservationservice.dto.AccommodationDTO;
import com.devops.reservationservice.dto.ReservationDTO;
import com.devops.reservationservice.dto.UserDTO;
import com.devops.reservationservice.exceptions.UnauthorizedException;
import com.devops.reservationservice.model.Accommodation;
import com.devops.reservationservice.model.Reservation;
import com.devops.reservationservice.model.ReservationStatus;
import com.devops.reservationservice.repository.AccommodationRepository;
import com.devops.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final AccommodationRepository accommodationRepository;

    private final AccommodationService accommodationService;


    @Autowired
    private WebClient.Builder webClientBuilder;

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
    public ReservationDTO cancelReservation(Long reservationId) {
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
        return convertToDTO(reservation);
    }

    @Transactional
    public ReservationDTO rejectReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            if (reservation.getStartDate().isAfter(LocalDate.now())) {
                reservation.setStatus(ReservationStatus.REJECTED);
                reservationRepository.save(reservation);
            } else {
                throw new IllegalStateException("Cannot reject a reservation that has already started");
            }
        } else if (reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.REJECTED);
            reservationRepository.save(reservation);
        }
        return convertToDTO(reservation);
    }


    public List<ReservationDTO> getUserReservations(String userId, String accessToken, String fingerprint) {

        List<Reservation> reservations = reservationRepository.findByGuestId(userId);
        reservations.addAll(reservationRepository.findByAccommodationOwnerId(userId));

        List<ReservationDTO> reservationDTOS = reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        for (ReservationDTO reservation:reservationDTOS){
            UserDTO owner = getUserDetails(reservation.getOwnerId(), accessToken, fingerprint);
            UserDTO guest = getUserDetails(reservation.getGuestId(), accessToken, fingerprint);

            reservation.setOwnerName(owner.getName());
            reservation.setOwnerSurname(owner.getSurname());
            reservation.setOwnerId(owner.getId());

            reservation.setGuestName(guest.getName());
            reservation.setGuestSurname(guest.getSurname());
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

        WebClient webClient = webClientBuilder.build();

        Mono<UserDTO> response = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .cookie("Fingerprint", fingerprint)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
                        return Mono.error(new UnauthorizedException("Unauthorized"));
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        return Mono.error(new RuntimeException("Client Error"));
                    } else if (clientResponse.statusCode().is5xxServerError()) {
                        return Mono.error(new RuntimeException("Server Error"));
                    } else {
                        return clientResponse.bodyToMono(UserDTO.class);
                    }
                });

        try {
            return response.block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to authorize", e);
        }
    }

    private int getCancellationCount(String guestId) {
        List<Reservation> reservations = reservationRepository.findByGuestId(guestId);
        return (int) reservations.stream()
                .filter(res -> res.getStatus() == ReservationStatus.CANCELLED)
                .count();
    }

    public List<AccommodationDTO> getUserStays(String userId) {
        List<Reservation> reservations = reservationRepository.findByGuestId(userId).stream()
                .filter(res -> res.getStatus() == ReservationStatus.CONFIRMED
                        && res.getEndDate().isBefore(LocalDate.now()))
                .toList();

        Set<Accommodation> uniqueAccommodations = new HashSet<>();
        for (Reservation res : reservations) {
            uniqueAccommodations.add(res.getAccommodation());
        }

        return uniqueAccommodations.stream()
                .map(accommodationService::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUserHosts(String userId, String authToken, String fingerprint) {
        List<Reservation> reservations = reservationRepository.findByGuestId(userId).stream()
                .filter(res -> res.getStatus() == ReservationStatus.CONFIRMED
                        && res.getEndDate().isBefore(LocalDate.now()))
                .toList();

        Set<String> uniqueUserIds = new HashSet<>();
        for (Reservation res : reservations) {
            uniqueUserIds.add(res.getAccommodation().getOwnerId());
        }

        return uniqueUserIds.stream()
                .map(id -> getUserDetails(id, authToken, fingerprint))
                .collect(Collectors.toList());
    }
}
