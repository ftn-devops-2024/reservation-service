package com.devops.reservationservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private LocalDateTime startDate;
    @Column
    private LocalDateTime endDate;
    @Column
    private String accommodationID;
    @Column
    private String guestID;
    @Column
    private int guestNumber;
    @Column
    private ReservationRequestStatus status;
    @Column
    private String ownerID;
    @Column
    private String reservedTermId;
    @Column
    private String accommodationName;
}
