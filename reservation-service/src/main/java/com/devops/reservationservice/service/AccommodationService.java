package com.devops.reservationservice.service;


import com.devops.reservationservice.dto.AccommodationDTO;
import com.devops.reservationservice.model.Accommodation;
import com.devops.reservationservice.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationService {


    @Autowired
    private AccommodationRepository accommodationRepository;

    public AccommodationDTO createAccommodation(AccommodationDTO requestDTO){
        Accommodation accommodation = new Accommodation();
        accommodation.populateAccommodationFields(requestDTO);

        accommodationRepository.save(accommodation);

        return requestDTO;
    }

    public AccommodationDTO updateAccommodation(AccommodationDTO requestDTO){
        Accommodation accommodation = accommodationRepository.findOneById(requestDTO.getId());
        accommodation.populateAccommodationFields(requestDTO);
        accommodationRepository.save(accommodation);

        return requestDTO;

    }

    public AccommodationDTO getAccommodationById(Long id){
        return new AccommodationDTO();

    }

    public List<AccommodationDTO> getAllAccommodations(){
        return new ArrayList<AccommodationDTO>();

    }

    public boolean deleteAccommodation(Long id){
     return true;
    }

}
