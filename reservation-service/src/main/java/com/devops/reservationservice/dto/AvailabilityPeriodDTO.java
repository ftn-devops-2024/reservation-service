package com.devops.reservationservice.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityPeriodDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
}
