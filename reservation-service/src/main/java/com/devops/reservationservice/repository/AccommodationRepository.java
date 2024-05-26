package com.devops.reservationservice.repository;


import com.devops.reservationservice.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    public Accommodation findOneById(Long id);

}
