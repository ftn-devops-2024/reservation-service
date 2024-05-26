package com.devops.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccommodationDTO {
    private Long id;
    private String ownerId;
    private String name;
    private String location;
    private List<String> perks;
    private List<String> photos;
    private Integer minGuests;
    private Integer maxGuests;
    private Double pricePerDay;
    private Boolean automaticReservation;
}
