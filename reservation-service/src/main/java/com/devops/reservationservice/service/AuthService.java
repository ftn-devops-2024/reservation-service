package com.devops.reservationservice.service;

import com.devops.reservationservice.dto.UserDTO;
import com.devops.reservationservice.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public UserDTO authorizeGuest(String authToken, String fingerprint) {
        WebClient webClient = webClientBuilder.build();

        String url = "http://localhost:8000/auth-service/api/authorize/guest";

        Mono<UserDTO> response = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .cookie("Fingerprint", fingerprint)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
                        return Mono.error(new UnauthorizedException("Unauthorized"));
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        return Mono.error(new RuntimeException("Client Error"));
                    } else if (clientResponse.statusCode().is5xxServerError()) {
                        return Mono.error(new RuntimeException("Server Error"));
                    } else {
                        return clientResponse.bodyToMono(UserDTO.class);
                    }
                });

        try {
            return response.block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to authorize guest", e);
        }
    }

    public UserDTO authorizeHost(String authToken, String fingerprint) {
        WebClient webClient = webClientBuilder.build();

        String url = "http://localhost:8000/auth-service/api/authorize/host";

        Mono<UserDTO> response = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .cookie("Fingerprint", fingerprint)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
                        return Mono.error(new UnauthorizedException("Unauthorized"));
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        return Mono.error(new RuntimeException("Client Error"));
                    } else if (clientResponse.statusCode().is5xxServerError()) {
                        return Mono.error(new RuntimeException("Server Error"));
                    } else {
                        return clientResponse.bodyToMono(UserDTO.class);
                    }
                });

        try {
            return response.block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to authorize host", e);
        }
    }
}
