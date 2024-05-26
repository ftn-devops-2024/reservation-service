package com.devops.reservationservice.model;


import com.devops.reservationservice.dto.AccommodationDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String ownerId;

    @Column
    private String name;
    @Column
    private String location;
    @ElementCollection
    private List<Perk> perks;
    @ElementCollection
    private List<String> photos;
    @Column
    Integer minGuests;
    @Column
    Integer maxGuests;
    @Column
    Double pricePerDay;
    @Column
    Boolean automaticReservation;
    @OneToMany(mappedBy = "accommodation")
    private List<ReservationRequest> reservationRequests;


    public void populateAccommodationFields(AccommodationDTO accommodationDTO){
        this.ownerId = accommodationDTO.getOwnerId();
        this.name = accommodationDTO.getName();
        this.location = accommodationDTO.getLocation();
        List<Perk> perks = new ArrayList<Perk>();
        for (String perk:accommodationDTO.getPerks()){
            perks.add(Perk.valueOf(perk));
        }
        this.perks = perks;
        this.photos = accommodationDTO.getPhotos();
        this.minGuests = accommodationDTO.getMinGuests();
        this.maxGuests = accommodationDTO.getMaxGuests();
        this.pricePerDay = accommodationDTO.getPricePerDay();
        this.automaticReservation = accommodationDTO.getAutomaticReservation();


    }


}
