package com.devops.reservationservice.controller;

import com.devops.reservationservice.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    Logger logger = LoggerFactory.getLogger(ReservationService.class);

    @GetMapping("/test")
    public String test(){
        logger.info("reservation ideee");
        return "Welcome from reservation-service";
    }
}

