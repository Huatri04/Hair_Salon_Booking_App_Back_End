package com.hairsalonbookingapp.hairsalon.repository;

import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<HairSalonService, Long> {
    HairSalonService findHairSalonServiceById(long id);
}
