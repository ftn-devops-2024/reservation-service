package com.devops.reservationservice.controller;

import com.devops.reservationservice.dto.AccommodationDTO;
import com.devops.reservationservice.dto.ReservationDTO;
import com.devops.reservationservice.dto.UserDTO;
import com.devops.reservationservice.exceptions.UnauthorizedException;
import com.devops.reservationservice.service.AuthService;
import com.devops.reservationservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    private AuthService authService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO requestDTO,
                                                            @RequestHeader("Authorization") String authToken,
                                                            @CookieValue("Fingerprint") String fingerprint) {
        try {
            // vraca UserDTO pa imamo info o useru
            authService.authorizeGuest(authToken, fingerprint);
            ReservationDTO createdReservation = reservationService.createReservation(requestDTO);
            simpMessagingTemplate.convertAndSend("/notification/reservation-created", createdReservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Long id,
                                                             @RequestHeader("Authorization") String authToken,
                                                             @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeHost(authToken, fingerprint);
            ReservationDTO confirmedReservation = reservationService.confirmReservation(id);
            simpMessagingTemplate.convertAndSend("/notification/reservation-confirmed", confirmedReservation);
            return ResponseEntity.ok(confirmedReservation);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id,
                                                  @RequestHeader("Authorization") String authToken,
                                                  @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeGuest(authToken, fingerprint);
            ReservationDTO r = reservationService.cancelReservation(id);
            simpMessagingTemplate.convertAndSend("/notification/reservation-cancelled", r);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @GetMapping("/reject/{id}")
    public ResponseEntity<Void> rejectReservation(@PathVariable Long id,
                                                  @RequestHeader("Authorization") String authToken,
                                                  @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeHost(authToken, fingerprint);
            ReservationDTO r = reservationService.rejectReservation(id);
            simpMessagingTemplate.convertAndSend("/notification/reservation-rejected", r);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getUserReservations(@PathVariable String userId,
                                                                    @RequestHeader("Authorization") String authToken,
                                                                    @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeGuest(authToken, fingerprint);
            List<ReservationDTO> reservations = reservationService.getUserReservations(userId, authToken, fingerprint);
            return ResponseEntity.ok(reservations);
        } catch (UnauthorizedException e) {
            try{
                authService.authorizeHost(authToken, fingerprint);
                List<ReservationDTO> reservations = reservationService.getUserReservations(userId, authToken, fingerprint);
                return ResponseEntity.ok(reservations);
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
            }
        }

    }

    @GetMapping("/userStays/{userId}")
    public ResponseEntity<List<AccommodationDTO>> getUserStays(@PathVariable String userId,
                                                               @RequestHeader("Authorization") String authToken,
                                                               @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeGuest(authToken, fingerprint);
            List<AccommodationDTO> reservations = reservationService.getUserStays(userId);
            return ResponseEntity.ok(reservations);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", ex);
        }

    }

    @GetMapping("/userHosts/{userId}")
    public ResponseEntity<List<UserDTO>> getUserHosts(@PathVariable String userId,
                                                      @RequestHeader("Authorization") String authToken,
                                                      @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeGuest(authToken, fingerprint);
            List<UserDTO> reservations = reservationService.getUserHosts(userId, authToken, fingerprint);
            return ResponseEntity.ok(reservations);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", ex);
        }

    }


}
