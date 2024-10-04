package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.HairSalonService;
import com.hairsalonbookingapp.hairsalon.exception.DuplicateEntity;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.HairSalonServiceRequest;
import com.hairsalonbookingapp.hairsalon.model.HairSalonServiceResponse;
import com.hairsalonbookingapp.hairsalon.model.HairSalonServiceUpdate;
import com.hairsalonbookingapp.hairsalon.repository.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HairSalonBookingAppService {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ModelMapper modelMapper;

    //tạo mới service -> MANAGER LÀM
    public HairSalonServiceResponse createNewService(HairSalonServiceRequest hairSalonServiceRequest){
        try{
            HairSalonService newService = modelMapper.map(hairSalonServiceRequest, HairSalonService.class);
            newService.setStatus(true);
            HairSalonService savedService = serviceRepository.save(newService);
            return modelMapper.map(savedService, HairSalonServiceResponse.class);
        } catch (Exception e){
            throw new DuplicateEntity("Duplicate name!");
        }
    }

    //update service -> MANAGER LÀM
    public HairSalonServiceResponse updateService(HairSalonServiceUpdate hairSalonServiceUpdate, long id){
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
                return modelMapper.map(newService, HairSalonServiceResponse.class);
            } catch(Exception e) {
                throw new DuplicateEntity("Duplicate name!");
            }
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //delete service  -> MANAGER LÀM
    public HairSalonServiceResponse deleteService(long id){
        HairSalonService oldService = serviceRepository.findHairSalonServiceByIdAndStatusTrue(id);
        if(oldService != null){
            oldService.setStatus(false);
            HairSalonService savedService = serviceRepository.save(oldService);
            return modelMapper.map(savedService, HairSalonServiceResponse.class);
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //restart service -> MANAGER LÀM
    public HairSalonServiceResponse startService(long id){
        HairSalonService oldService = serviceRepository.findHairSalonServiceByIdAndStatusTrue(id);
        if(oldService != null){
            oldService.setStatus(true);
            HairSalonService savedService = serviceRepository.save(oldService);
            return modelMapper.map(savedService, HairSalonServiceResponse.class);
        } else {
            throw new EntityNotFoundException("Service not found!");
        }
    }

    //get all service  -> CUSTOMER, MANAGER LÀM
    public List<HairSalonServiceResponse> getAllService(){
        List<HairSalonService> list = serviceRepository.findHairSalonServicesByStatusTrue();
        if(list != null){
            List<HairSalonServiceResponse> responseList = new ArrayList<>();
            for(HairSalonService hairSalonService : list){
                HairSalonServiceResponse hairSalonServiceResponse = modelMapper.map(hairSalonService, HairSalonServiceResponse.class);
                responseList.add(hairSalonServiceResponse);
            }
            return responseList;
        } else {
            //throw new EntityNotFoundException("Service not found!");
            return null;
        }
    }


}
