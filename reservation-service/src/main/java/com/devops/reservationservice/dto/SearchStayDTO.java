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
public class SearchStayDTO {

    private String location;
    private int guests;
    private LocalDate startDate;
    private LocalDate endDate;

}
