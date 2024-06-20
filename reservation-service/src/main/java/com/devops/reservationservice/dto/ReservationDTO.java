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
    private String guestId;
    private String guestName;
    private String guestSurname;
    private String ownerId;
    private String ownerName;
    private String ownerSurname;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfGuests;
    private ReservationStatus status;
    private String accommodationName;
    private int cancellationCount;
}
