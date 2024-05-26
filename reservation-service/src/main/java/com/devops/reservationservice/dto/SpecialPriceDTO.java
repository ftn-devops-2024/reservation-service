package com.devops.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpecialPriceDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;
}
