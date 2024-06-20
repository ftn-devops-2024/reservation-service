package com.devops.reservationservice.controller;

import com.devops.reservationservice.dto.ReservationDTO;
import com.devops.reservationservice.dto.SearchResultDTO;
import com.devops.reservationservice.exceptions.UnauthorizedException;
import com.devops.reservationservice.service.AuthService;
import com.devops.reservationservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    private AuthService authService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO requestDTO) {
        try {
            // vraca UserDTO pa imamo info o useru
            //authService.authorizeGuest(authToken, fingerprint);
            ReservationDTO createdReservation = reservationService.createReservation(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Long id) {
        try {
            //authService.authorizeGuest(authToken, fingerprint);
            ReservationDTO confirmedReservation = reservationService.confirmReservation(id);
            return ResponseEntity.ok(confirmedReservation);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getUserReservations(@PathVariable Long userId) {
        try {
            //authService.authorizeGuest(authToken, fingerprint);
            List<ReservationDTO> reservations = reservationService.getUserReservations(userId);
            return ResponseEntity.ok(reservations);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }


}
