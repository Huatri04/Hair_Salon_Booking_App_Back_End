package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<HairSalonService, Long> {
    HairSalonService findHairSalonServiceByIdAndIsAvailableTrue(long id);
    List<HairSalonService> findHairSalonServicesByIsAvailableTrue();
    //List<HairSalonService> findHairSalonServicesByIsAvailableTrue();
    HairSalonService findHairSalonServiceById(long id);
}
