package com.devops.reservationservice.service;

import com.devops.reservationservice.dto.AccommodationDTO;
import com.devops.reservationservice.dto.AvailabilityPeriodDTO;
import com.devops.reservationservice.dto.SpecialPriceDTO;
import com.devops.reservationservice.model.Accommodation;
import com.devops.reservationservice.model.AvailabilityPeriod;
import com.devops.reservationservice.model.Perk;
import com.devops.reservationservice.model.SpecialPrice;
import com.devops.reservationservice.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Transactional
    public AccommodationDTO createAccommodation(AccommodationDTO requestDTO) {
        Accommodation accommodation = new Accommodation();
        accommodation.populateAccommodationFields(requestDTO);
        accommodationRepository.save(accommodation);
        return requestDTO;
    }

    @Transactional
    public AccommodationDTO updateAccommodation(AccommodationDTO requestDTO) {
        Accommodation accommodation = accommodationRepository.findById(requestDTO.getId())
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
    public List<AccommodationDTO> getAllAccommodations() {
        List<Accommodation> accommodations = accommodationRepository.findAll();
        return accommodations.stream().map(this::mapToDTO).collect(Collectors.toList());
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
}
