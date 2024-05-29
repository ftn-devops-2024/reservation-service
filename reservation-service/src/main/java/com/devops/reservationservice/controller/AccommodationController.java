package com.devops.reservationservice.controller;

import com.devops.reservationservice.dto.AccommodationDTO;
import com.devops.reservationservice.service.AccommodationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    public AccommodationController(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @PostMapping
    public ResponseEntity<AccommodationDTO> createAccommodation(@PathVariable Long id, @RequestBody AccommodationDTO requestDTO) {
        AccommodationDTO createdAccommodation = accommodationService.createAccommodation(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccommodation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccommodationDTO> updateAccommodation(@RequestBody AccommodationDTO requestDTO) {
        AccommodationDTO updatedAccommodation = accommodationService.updateAccommodation(requestDTO);
        return ResponseEntity.ok(updatedAccommodation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getAccommodationById(@PathVariable Long id) {
        AccommodationDTO accommodation = accommodationService.getAccommodationById(id);
        return ResponseEntity.ok(accommodation);
    }

    @GetMapping
    public ResponseEntity<List<AccommodationDTO>> getAllAccommodations() {
        List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
        return ResponseEntity.ok(accommodations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable Long id) {
        boolean deleted = accommodationService.deleteAccommodation(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
