package com.devops.reservationservice.controller;

import com.devops.reservationservice.dto.AccommodationDTO;
import com.devops.reservationservice.dto.SearchResultDTO;
import com.devops.reservationservice.dto.SearchStayDTO;
import com.devops.reservationservice.exceptions.UnauthorizedException;
import com.devops.reservationservice.service.AccommodationService;
import com.devops.reservationservice.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.Consumes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Base64;
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

    @PostMapping(value = "/{id}/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file,
                                              @RequestHeader("Authorization") String authToken,
                                              @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeHost(authToken, fingerprint);
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            accommodationService.addPhoto(id, base64Image);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/{id}/getImages")
//    public ResponseEntity<List<String>> getImages(@PathVariable Long id,
//                                                  @RequestHeader("Authorization") String authToken,
//                                                  @CookieValue("Fingerprint") String fingerprint) {
//        try {
//            authService.authorizeHost(authToken, fingerprint);
//
//            List<String> photos = accommodationService.getPhotos(id);
//            return ResponseEntity.ok(photos);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @PostMapping
    public ResponseEntity<AccommodationDTO> createAccommodation(
                                                                @RequestBody AccommodationDTO requestDTO,
                                                                @RequestHeader("Authorization") String authToken,
                                                                @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeHost(authToken, fingerprint);
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
                                                                @RequestBody AccommodationDTO requestDTO,
                                                                @RequestHeader("Authorization") String authToken,
                                                                @CookieValue("Fingerprint") String fingerprint) {
        try {
            authService.authorizeHost(authToken, fingerprint);
            AccommodationDTO updatedAccommodation = accommodationService.updateAccommodation(id, requestDTO);
            return ResponseEntity.ok(updatedAccommodation);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getAccommodationById(@PathVariable Long id,
                                                                 @RequestHeader("Authorization") String authToken,
                                                                 @CookieValue("Fingerprint") String fingerprint) {
        try{
            authService.authorizeHost(authToken, fingerprint);
            AccommodationDTO accommodation = accommodationService.getAccommodationById(id);
            return ResponseEntity.ok(accommodation);
        }catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }

    }

    @GetMapping ("/owner/{id}")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsByOwner(@PathVariable String id,
                                                                           @RequestHeader("Authorization") String authToken,
                                                                           @CookieValue("Fingerprint") String fingerprint){
        try{
            authService.authorizeHost(authToken, fingerprint);
            List<AccommodationDTO> accommodations = accommodationService.getAccommodationsByOwnerId(id);
            return ResponseEntity.ok(accommodations);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }

    }

    @GetMapping
    public ResponseEntity<List<AccommodationDTO>> getAllAccommodations(
                                                    @RequestHeader("Authorization") String authToken,
                                                    @CookieValue("Fingerprint") String fingerprint) {
        try{
            authService.authorizeHost(authToken, fingerprint);
            List<AccommodationDTO> accommodations = accommodationService.getAllAccommodations();
            return ResponseEntity.ok(accommodations);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }

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

    @PostMapping("/search")
    public ResponseEntity<List<SearchResultDTO>> searchAccommodations(@RequestBody SearchStayDTO dto) {
        try{
            List<SearchResultDTO> results = accommodationService.searchAccommodations(
                    dto.getLocation(), dto.getGuests(), dto.getStartDate(), dto.getEndDate());
            return ResponseEntity.ok(results);
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
    }

    }
}
