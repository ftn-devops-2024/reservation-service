package com.devops.reservationservice.service;

import com.devops.reservationservice.dto.AccommodationDTO;
import com.devops.reservationservice.dto.AvailabilityPeriodDTO;
import com.devops.reservationservice.dto.SearchResultDTO;
import com.devops.reservationservice.dto.SpecialPriceDTO;
import com.devops.reservationservice.model.Accommodation;
import com.devops.reservationservice.model.AvailabilityPeriod;
import com.devops.reservationservice.model.Perk;
import com.devops.reservationservice.model.SpecialPrice;
import com.devops.reservationservice.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    @Transactional
    public AccommodationDTO createAccommodation(AccommodationDTO requestDTO) {
        Accommodation accommodation = new Accommodation();
        accommodation.populateAccommodationFields(requestDTO);
        accommodationRepository.save(accommodation);
        return requestDTO;
    }

    @Transactional
    public AccommodationDTO updateAccommodation(Long id, AccommodationDTO requestDTO) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        accommodation.populateAccommodationFields(requestDTO);
        accommodationRepository.save(accommodation);
        return requestDTO;
    }

    @Transactional(readOnly = true)
    public AccommodationDTO getAccommodationById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));
        return mapToDTO(accommodation);
    }

    @Transactional(readOnly = true)
    public List<AccommodationDTO> getAccommodationsByOwnerId(Long ownerId){
        List<Accommodation> accommodations = accommodationRepository.findByOwnerId(ownerId);
        return accommodations.stream().map(this::mapToDTO).toList();

    }

    @Transactional(readOnly = true)
    public List<AccommodationDTO> getAllAccommodations() {
        List<Accommodation> accommodations = accommodationRepository.findAll();
        return accommodations.stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public boolean deleteAccommodation(Long id) {
        if (accommodationRepository.existsById(id)) {
            accommodationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private AccommodationDTO mapToDTO(Accommodation accommodation) {
        AccommodationDTO dto = new AccommodationDTO();
        dto.setId(accommodation.getId());
        dto.setOwnerId(accommodation.getOwnerId());
        dto.setName(accommodation.getName());
        dto.setLocation(accommodation.getLocation());
        dto.setPerks(accommodation.getPerks().stream().map(Perk::name).collect(Collectors.toList()));
        dto.setPhotos(accommodation.getPhotos());
        dto.setMinGuests(accommodation.getMinGuests());
        dto.setMaxGuests(accommodation.getMaxGuests());
        dto.setPricePerDay(accommodation.getPricePerDay());
        dto.setAutomaticReservation(accommodation.getAutomaticReservation());
        dto.setAvailabilityPeriods(accommodation.getAvailabilityPeriods().stream().map(this::mapToAvailabilityPeriodDTO).collect(Collectors.toList()));
        dto.setSpecialPrices(accommodation.getSpecialPrices().stream().map(this::mapToSpecialPriceDTO).collect(Collectors.toList()));
        return dto;
    }

    private AvailabilityPeriodDTO mapToAvailabilityPeriodDTO(AvailabilityPeriod availabilityPeriod) {
        return new AvailabilityPeriodDTO(availabilityPeriod.getId(), availabilityPeriod.getStartDate(), availabilityPeriod.getEndDate());
    }

    private SpecialPriceDTO mapToSpecialPriceDTO(SpecialPrice specialPrice) {
        return new SpecialPriceDTO(specialPrice.getId(), specialPrice.getStartDate(), specialPrice.getEndDate(), specialPrice.getPrice());
    }

    public List<SearchResultDTO> searchAccommodations(String location, int numGuests, LocalDate startDate, LocalDate endDate) {
        List<Accommodation> accommodations = accommodationRepository.searchAccommodations(location, numGuests, startDate, endDate);

        return accommodations.stream().map(accommodation -> {
            double totalPrice = calculateTotalPrice(accommodation, startDate, endDate);
            return new SearchResultDTO(
                    accommodation.getId(),
                    accommodation.getOwnerId(),
                    accommodation.getName(),
                    accommodation.getLocation(),
                    accommodation.getPerks().stream().map(Enum::name).collect(Collectors.toList()),
                    accommodation.getPhotos(),
                    accommodation.getMinGuests(),
                    accommodation.getMaxGuests(),
                    accommodation.getPricePerDay(),
                    accommodation.getAutomaticReservation(),
                    totalPrice
            );
        }).collect(Collectors.toList());
    }

    private double calculateTotalPrice(Accommodation accommodation, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        double totalPrice = days * accommodation.getPricePerDay();

        // Apply special prices if any
        for (SpecialPrice specialPrice : accommodation.getSpecialPrices()) {
            if (!startDate.isAfter(specialPrice.getEndDate()) && !endDate.isBefore(specialPrice.getStartDate())) {
                LocalDate overlapStart = startDate.isAfter(specialPrice.getStartDate()) ? startDate : specialPrice.getStartDate();
                LocalDate overlapEnd = endDate.isBefore(specialPrice.getEndDate()) ? endDate : specialPrice.getEndDate();
                long overlapDays = ChronoUnit.DAYS.between(overlapStart, overlapEnd);
                totalPrice += overlapDays * (specialPrice.getPrice() - accommodation.getPricePerDay());
            }
        }

        return totalPrice;
    }
}
