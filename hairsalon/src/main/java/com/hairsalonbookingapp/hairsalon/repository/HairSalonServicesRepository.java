package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HairSalonServicesRepository extends JpaRepository<HairSalonService, Long> {
    public HairSalonService findHairSalonServiceById(long id);
    public List<HairSalonService> findHairSalonServicesByIsDeletedFalse();
}
