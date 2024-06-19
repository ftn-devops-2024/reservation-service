package com.devops.reservationservice.controller;

import com.devops.reservationservice.dto.AccommodationDTO;
import com.devops.reservationservice.exceptions.UnauthorizedException;
import com.devops.reservationservice.service.AccommodationService;
import com.devops.reservationservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @Autowired
    private AuthService authService;

    public AccommodationController(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @PostMapping
    public ResponseEntity<AccommodationDTO> createAccommodation(
                                                                @RequestBody AccommodationDTO requestDTO) {
        try {
            AccommodationDTO createdAccommodation = accommodationService.createAccommodation(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccommodation);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccommodationDTO> updateAccommodation(@PathVariable Long id,
                                                                @RequestBody AccommodationDTO requestDTO) {
        try {
            AccommodationDTO updatedAccommodation = accommodationService.updateAccommodation(id, requestDTO);
            return ResponseEntity.ok(updatedAccommodation);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getAccommodationById(@PathVariable Long id) {
        AccommodationDTO accommodation = accommodationService.getAccommodationById(id);
        return ResponseEntity.ok(accommodation);
    }

    @GetMapping ("/owner/{id}")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsByOwner(@PathVariable Long id){
        List<AccommodationDTO> accommodations = accommodationService.getAccommodationsByOwnerId(id);
        return ResponseEntity.ok(accommodations);
    }

    @GetMapping
    public ResponseEntity<List<AccommodationDTO>> getAllAccommodations() {
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
        return ResponseEntity.ok(accommodations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable Long id,
                                                    @RequestHeader("Authorization") String authToken,
                                                    @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeHost(authToken, fingerprint);
            boolean deleted = accommodationService.deleteAccommodation(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }
}
