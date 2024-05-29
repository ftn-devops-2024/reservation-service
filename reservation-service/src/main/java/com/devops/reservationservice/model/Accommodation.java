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
    private Long ownerId;

    @Column
    private String name;

    @Column
    private String location;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Perk> perks;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> photos;

    @Column
    private Integer minGuests;

    @Column
    private Integer maxGuests;

    @Column
    private Double pricePerDay;

    @Column
    private Boolean automaticReservation;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.EAGER)
    private List<Reservation> reservationRequests;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilityPeriod> availabilityPeriods;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialPrice> specialPrices;

    public void populateAccommodationFields(AccommodationDTO accommodationDTO) {
        this.ownerId = accommodationDTO.getOwnerId();
        this.name = accommodationDTO.getName();
        this.location = accommodationDTO.getLocation();
        List<Perk> perkArrayList = new ArrayList<>();
        for (String perk : accommodationDTO.getPerks()) {
            perkArrayList.add(Perk.valueOf(perk));
        }
        this.perks = perkArrayList;
        this.photos = accommodationDTO.getPhotos();
        this.minGuests = accommodationDTO.getMinGuests();
        this.maxGuests = accommodationDTO.getMaxGuests();
        this.pricePerDay = accommodationDTO.getPricePerDay();
        this.automaticReservation = accommodationDTO.getAutomaticReservation();
    }
}
