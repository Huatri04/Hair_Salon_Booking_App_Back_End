package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.exception.UpdatedException;
import com.hairsalonbookingapp.hairsalon.model.request.RequestDiscountprogram;
import com.hairsalonbookingapp.hairsalon.model.request.RequestUpdateDiscountProgram;
import com.hairsalonbookingapp.hairsalon.model.response.DiscountProgramInfoResponse;
import com.hairsalonbookingapp.hairsalon.model.response.DiscountProgramListResponse;
import com.hairsalonbookingapp.hairsalon.model.response.DiscountProgramResponse;
import com.hairsalonbookingapp.hairsalon.model.response.UpdateDiscountProgramResponse;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DiscountProgramService {
    @Autowired
    DiscountProgramRepository discountProgramRepository;

    @Autowired
    ModelMapper modelMapper;

    // create DiscountProgram
    public DiscountProgramResponse createDiscountProgram(RequestDiscountprogram requestDiscountprogram){
        DiscountProgram discountProgram = modelMapper.map(requestDiscountprogram, DiscountProgram.class);
        try{
//            String newId = generateId();
//            discountProgram.setDiscountProgramId(newId);
            DiscountProgram newDiscountProgram = discountProgramRepository.save(discountProgram);
            return modelMapper.map(newDiscountProgram, DiscountProgramResponse.class);
        } catch (Exception e) {
//            if(e.getMessage().contains(discountProgram.getDiscountProgramId())){
//                throw new Duplicate("duplicate discount program id! ");
//            }
        }
        return null;
    }

//    public String generateId() {
//        // Tìm ID cuối cùng theo vai trò
//        Optional<DiscountProgram> lasDiscountProgram = discountProgramRepository.findTopByOrderByDiscountProgramIdDesc();
//        int newIdNumber = 1; // Mặc định bắt đầu từ 1
//
//        // Nếu có tài khoản cuối cùng, lấy ID
//        if (lasDiscountProgram.isPresent()) {
//            String lastId = lasDiscountProgram.get().getDiscountProgramId();
//            newIdNumber = Integer.parseInt(lastId.replaceAll("\\D+", "")) + 1; // Tăng số lên 1
//        }
//
//
//        String prefix = "DP";
//
//        return String.format("%s%06d", prefix, newIdNumber); // Tạo ID mới với format
//    }


    //delete feedback
    public DiscountProgramResponse deleteDiscountProgram(int discountProgramId){
        // tim toi id ma FE cung cap
        DiscountProgram discountProgramNeedDelete = discountProgramRepository.findDiscountProgramByDiscountProgramId(discountProgramId);
        if(discountProgramNeedDelete == null){
            throw new Duplicate("Feedback not found!"); // dung tai day
        }

        discountProgramNeedDelete.setDeleted(true);
        DiscountProgram deletedDiscountProgramm = discountProgramRepository.save(discountProgramNeedDelete);
        return modelMapper.map(deletedDiscountProgramm, DiscountProgramResponse.class);
    }

    // show list of DiscountProgram
    public DiscountProgramListResponse getAllDiscountProgram(int page, int size){
//        List<DiscountProgram> discountPrograms = discountProgramRepository.findDiscountProgramsByIsDeletedFalse();
//        return discountPrograms;
        Page discountProgramPage = discountProgramRepository.findDiscountProgramsByIsDeletedFalseOrderByEndedDateAsc(PageRequest.of(page, size));
        DiscountProgramListResponse discountProgramListResponse = new DiscountProgramListResponse();
        discountProgramListResponse.setTotalPage(discountProgramPage.getTotalPages());
        discountProgramListResponse.setContent(discountProgramPage.getContent());
        discountProgramListResponse.setPageNumber(discountProgramPage.getNumber());
        discountProgramListResponse.setTotalElement(discountProgramPage.getTotalElements());
        return discountProgramListResponse;
    }

//    public DiscountProgram getCurrentDiscountProgram(){
//        DiscountProgram discountProgram = (DiscountProgram) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return discountProgramRepository.findDiscountProgramByDiscountProgramId(discountProgram.getDiscountProgramId());
//    }
    public DiscountProgram getCurrentDiscountProgram() {
        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại

        // Tìm chương trình khuyến mãi đang hoạt động
        Optional<DiscountProgram> currentProgram = discountProgramRepository
                .findFirstByStartedDateBeforeAndEndedDateAfterAndIsDeletedFalse(now, now);

        // Kiểm tra xem có tìm thấy chương trình khuyến mãi không
        if (currentProgram.isPresent()) {
            return currentProgram.get(); // Nếu có, trả về chương trình khuyến mãi
        } else {
            // Nếu không tìm thấy, ném ra ngoại lệ với thông điệp rõ ràng
            throw new Duplicate("No active discount program found.");
        }
    }

    public UpdateDiscountProgramResponse updatedDiscountProgram(RequestUpdateDiscountProgram requestUpdateDiscountProgram, int id) {
        DiscountProgram discountProgram = modelMapper.map(requestUpdateDiscountProgram, DiscountProgram.class);
//        List<DiscountProgram> discountPrograms = discountProgramRepository.findDiscountProgramByName(name);
        DiscountProgram oldDiscountProgram = discountProgramRepository.findDiscountProgramByDiscountProgramId(id);
        if (oldDiscountProgram == null) {
            throw new Duplicate("Discount program not found!");// cho dung luon
        } else {
            try{
                if (discountProgram.getName() != null && !discountProgram.getName().isEmpty()) {
                    oldDiscountProgram.setName(discountProgram.getName());
                }

                if (discountProgram.getDescription() != null && !discountProgram.getDescription().isEmpty()) {
                    oldDiscountProgram.setDescription(discountProgram.getDescription());
                }

                if (discountProgram.getStartedDate() != null) {
                    oldDiscountProgram.setStartedDate(discountProgram.getStartedDate());
                }

                if (discountProgram.getEndedDate() != null) {
                    oldDiscountProgram.setEndedDate(discountProgram.getEndedDate());
                }

                if (discountProgram.getStatus() != null && !discountProgram.getStatus().isEmpty()) {
                    oldDiscountProgram.setStatus(discountProgram.getStatus());
                }

                if (discountProgram.getPercentage() != 0) {
                    oldDiscountProgram.setPercentage(discountProgram.getPercentage());
                }

                if (discountProgram.getAmount() != 0) {
                    oldDiscountProgram.setAmount(discountProgram.getAmount());
                }

                // Lưu cập nhật vào cơ sở dữ liệu
                DiscountProgram updatedDiscountProgram = discountProgramRepository.save(oldDiscountProgram);
                return modelMapper.map(updatedDiscountProgram, UpdateDiscountProgramResponse.class);
            } catch (Exception e) {
                throw new UpdatedException("Discount Program can not update!");
            }
        }
    }

    //GET PROFILE DiscountProgram
    public DiscountProgramInfoResponse getInfoDiscountProgram(int id){
        DiscountProgram discountProgram = discountProgramRepository.findDiscountProgramByDiscountProgramId(id);
        return modelMapper.map(discountProgram, DiscountProgramInfoResponse.class);
    }

}
