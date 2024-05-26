package com.devops.reservationservice.service;

import com.devops.reservationservice.dto.AccommodationDTO;
import com.devops.reservationservice.dto.AvailabilityPeriodDTO;
import com.devops.reservationservice.dto.SpecialPriceDTO;
import com.devops.reservationservice.model.Accommodation;
import com.devops.reservationservice.model.AvailabilityPeriod;
import com.devops.reservationservice.model.SpecialPrice;
import com.devops.reservationservice.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    public AccommodationDTO createAccommodation(AccommodationDTO requestDTO) {
        Accommodation accommodation = new Accommodation();
        accommodation.populateAccommodationFields(requestDTO);

        // Populate availability periods
        List<AvailabilityPeriod> availabilityPeriods = requestDTO.getAvailabilityPeriods().stream()
                .map(dto -> new AvailabilityPeriod(null, dto.getStartDate(), dto.getEndDate(), accommodation))
                .collect(Collectors.toList());
        accommodation.setAvailabilityPeriods(availabilityPeriods);

        // Populate special prices
        List<SpecialPrice> specialPrices = requestDTO.getSpecialPrices().stream()
                .map(dto -> new SpecialPrice(null, dto.getStartDate(), dto.getEndDate(), dto.getPrice(), accommodation))
                .collect(Collectors.toList());
        accommodation.setSpecialPrices(specialPrices);

        accommodationRepository.save(accommodation);

        return mapToDTO(accommodation);
    }

    public AccommodationDTO updateAccommodation(AccommodationDTO requestDTO) {
        Accommodation accommodation = accommodationRepository.findById(requestDTO.getId()).orElse(null);
        if (accommodation == null) {
            return null;
        }
        accommodation.populateAccommodationFields(requestDTO);

        // Update availability periods
        accommodation.setAvailabilityPeriods(requestDTO.getAvailabilityPeriods().stream()
                .map(dto -> new AvailabilityPeriod(null, dto.getStartDate(), dto.getEndDate(), accommodation))
                .collect(Collectors.toList()));

        // Update special prices
        accommodation.setSpecialPrices(requestDTO.getSpecialPrices().stream()
                .map(dto -> new SpecialPrice(null, dto.getStartDate(), dto.getEndDate(), dto.getPrice(), accommodation))
                .collect(Collectors.toList()));

        accommodationRepository.save(accommodation);

        return mapToDTO(accommodation);
    }

    public AccommodationDTO getAccommodationById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElse(null);
        if (accommodation == null) {
            return null;
        }
        return mapToDTO(accommodation);
    }

    public List<AccommodationDTO> getAllAccommodations() {
        List<Accommodation> accommodations = accommodationRepository.findAll();
        return accommodations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public boolean deleteAccommodation(Long id) {
        if (!accommodationRepository.existsById(id)) {
            return false;
        }
        accommodationRepository.deleteById(id);
        return true;
    }

    private AccommodationDTO mapToDTO(Accommodation accommodation) {
        List<AvailabilityPeriodDTO> availabilityPeriods = accommodation.getAvailabilityPeriods().stream()
                .map(ap -> new AvailabilityPeriodDTO(ap.getStartDate(), ap.getEndDate()))
                .collect(Collectors.toList());

        List<SpecialPriceDTO> specialPrices = accommodation.getSpecialPrices().stream()
                .map(sp -> new SpecialPriceDTO(sp.getStartDate(), sp.getEndDate(), sp.getPrice()))
                .collect(Collectors.toList());

        return new AccommodationDTO(
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
                availabilityPeriods,
                specialPrices
        );
    }
}
