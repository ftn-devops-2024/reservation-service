package com.devops.reservationservice.dto;

import com.devops.reservationservice.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long accommodationId;
    private Long guestId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfGuests;
    private ReservationStatus status;
}
