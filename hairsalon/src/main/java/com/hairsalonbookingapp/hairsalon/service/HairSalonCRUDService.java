package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.NotFoundException;
import com.hairsalonbookingapp.hairsalon.repository.HairSalonServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HairSalonCRUDService {

    @Autowired
    HairSalonServicesRepository hairSalonServicesRepository;

    //thêm 1 service
    public HairSalonService createNewService(HairSalonService hairSalonService){
        try{
            HairSalonService newService = hairSalonServicesRepository.save(hairSalonService);
            return newService;
        } catch (Exception e) {
            throw new DuplicateEntity("Duplicate name!");
        }
    }

    //update service
    public HairSalonService updateService(HairSalonService hairSalonService, long id){
        HairSalonService oldService = hairSalonServicesRepository.findHairSalonServiceById(id);
        if(oldService == null || oldService.isDeleted()){
            throw new NotFoundException("Service not found!");
        }
        oldService.setCost(hairSalonService.getCost());
        oldService.setTimeOfService(hairSalonService.getTimeOfService());
        return hairSalonServicesRepository.save(oldService);
    }

    //delete Service
    public HairSalonService deleteService(long id){
        HairSalonService service = hairSalonServicesRepository.findHairSalonServiceById(id);
        if(service == null || service.isDeleted()){
            throw new NotFoundException("Service not found!");
        }

        service.setDeleted(true);
        return hairSalonServicesRepository.save(service);
    }

    //getAllservice
    public List<HairSalonService> getAllServices(){
        List<HairSalonService> list = hairSalonServicesRepository.findHairSalonServicesByIsDeletedFalse();
        if(list == null){
            throw new NotFoundException("Service not found!");
        }

        return list;
    }
}
