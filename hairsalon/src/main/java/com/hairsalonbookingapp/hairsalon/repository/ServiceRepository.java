package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<HairSalonService, Long> {
    HairSalonService findHairSalonServiceByIdAndIsAvailableTrue(long id);
    //List<HairSalonService> findHairSalonServicesByIsAvailableTrue();
    Page<HairSalonService> findHairSalonServicesByIsAvailableTrue(Pageable pageable);
    //List<HairSalonService> findHairSalonServicesByIsAvailableTrue();
    HairSalonService findHairSalonServiceById(long id);
}
