package com.devops.reservationservice;

import com.devops.reservationservice.controller.TestController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(TestController.class)
@ActiveProfiles("test")
class ReservationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
