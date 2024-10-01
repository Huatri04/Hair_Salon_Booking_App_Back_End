package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.HairSalonServiceRequest;
import com.hairsalonbookingapp.hairsalon.model.HairSalonServiceUpdate;
import com.hairsalonbookingapp.hairsalon.repository.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HairSalonBookingAppService {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ModelMapper modelMapper;

    //tạo mới service
    public HairSalonService createNewService(HairSalonServiceRequest hairSalonServiceRequest){
        try{
            HairSalonService newService = modelMapper.map(hairSalonServiceRequest, HairSalonService.class);
            newService.setStatus("Available");
            return serviceRepository.save(newService);
        } catch (Exception e){
            throw new DuplicateEntity("Duplicate name!");
        }
    }

    //update service
    public HairSalonService updateService(HairSalonServiceUpdate hairSalonServiceUpdate, long id){
        HairSalonService oldService = serviceRepository.findHairSalonServiceById(id);
        if(oldService != null){
            try{
                oldService.setName(hairSalonServiceUpdate.getName());
                oldService.setCost(hairSalonServiceUpdate.getCost());
                oldService.setTimeOfService(hairSalonServiceUpdate.getTimeOfService());
                oldService.setImage(hairSalonServiceUpdate.getImage());

                HairSalonService newService = serviceRepository.save(oldService);
                return newService;
            } catch(Exception e) {
                throw new DuplicateEntity("Duplicate name!");
            }
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //delete service
    public HairSalonService deleteService(long id){
        HairSalonService oldService = serviceRepository.findHairSalonServiceById(id);
        if(oldService != null){
            oldService.setStatus("Not available");
            return serviceRepository.save(oldService);
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //restart service
    public HairSalonService startService(long id){
        HairSalonService oldService = serviceRepository.findHairSalonServiceById(id);
        if(oldService != null){
            oldService.setStatus("Available");
            return serviceRepository.save(oldService);
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //get all service
    public List<HairSalonService> getAllService(){
        List<HairSalonService> list = serviceRepository.findAll();
        return list;
    }


}
