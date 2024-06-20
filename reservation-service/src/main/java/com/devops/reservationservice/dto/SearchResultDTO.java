package com.devops.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {
    private Long id;
    private Long ownerId;
    private String name;
    private String location;
    private List<String> perks;
    private List<String> photos;
    private Integer minGuests;
    private Integer maxGuests;
    private Double pricePerDay;
    private Boolean automaticReservation;
    private Double totalPrice;
}
