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

    //tạo mới service -> MANAGER LÀM
    public HairSalonService createNewService(HairSalonServiceRequest hairSalonServiceRequest){
        try{
            HairSalonService newService = modelMapper.map(hairSalonServiceRequest, HairSalonService.class);
            newService.setStatus(true);
            return serviceRepository.save(newService);
        } catch (Exception e){
            throw new DuplicateEntity("Duplicate name!");
        }
    }

    //update service -> MANAGER LÀM
    public HairSalonService updateService(HairSalonServiceUpdate hairSalonServiceUpdate, long id){
        HairSalonService oldService = serviceRepository.findHairSalonServiceByIdAndStatusTrue(id);
        if(oldService != null){
            try{
                if(!hairSalonServiceUpdate.getName().isEmpty()){
                    oldService.setName(hairSalonServiceUpdate.getName());
                }

                if(hairSalonServiceUpdate.getCost() > 0){
                    oldService.setCost(hairSalonServiceUpdate.getCost());
                }

                if(hairSalonServiceUpdate.getTimeOfService() > 0 && hairSalonServiceUpdate.getTimeOfService() < 60){
                    oldService.setTimeOfService(hairSalonServiceUpdate.getTimeOfService());
                }

                if(!hairSalonServiceUpdate.getImage().isEmpty()){
                    oldService.setImage(hairSalonServiceUpdate.getImage());
                }

                HairSalonService newService = serviceRepository.save(oldService);
                return newService;
            } catch(Exception e) {
                throw new DuplicateEntity("Duplicate name!");
            }
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //delete service  -> MANAGER LÀM
    public HairSalonService deleteService(long id){
        HairSalonService oldService = serviceRepository.findHairSalonServiceByIdAndStatusTrue(id);
        if(oldService != null){
            oldService.setStatus(false);
            return serviceRepository.save(oldService);
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //restart service -> MANAGER LÀM
    public HairSalonService startService(long id){
        HairSalonService oldService = serviceRepository.findHairSalonServiceByIdAndStatusTrue(id);
        if(oldService != null){
            oldService.setStatus(true);
            return serviceRepository.save(oldService);
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //get all service  -> CUSTOMER LÀM
    public List<HairSalonService> getAllService(){
        List<HairSalonService> list = serviceRepository.findHairSalonServicesByStatusTrue();
        if(list != null){
            return list;
        } else {
            //throw new EntityNotFoundException("Service not found!");
            return null;
        }
    }


}
