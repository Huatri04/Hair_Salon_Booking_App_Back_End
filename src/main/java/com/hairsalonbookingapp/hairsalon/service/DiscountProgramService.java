package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.AccountForCustomer;
import com.hairsalonbookingapp.hairsalon.entity.AccountForEmployee;
import com.hairsalonbookingapp.hairsalon.entity.DiscountProgram;
import com.hairsalonbookingapp.hairsalon.entity.Feedback;
import com.hairsalonbookingapp.hairsalon.exception.Duplicate;
import com.hairsalonbookingapp.hairsalon.exception.UpdatedException;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.DiscountProgramRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountProgramService {
    @Autowired
    DiscountProgramRepository discountProgramRepository;

    @Autowired
    ModelMapper modelMapper;

    // create feedback
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

    // show list of feedback
    public List<DiscountProgram> getAllDiscountProgram(){
        List<DiscountProgram> discountPrograms = discountProgramRepository.findDiscountProgramsByIsDeletedFalse();
        return discountPrograms;
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
        DiscountProgram oldAccountDiscountProgram = discountProgramRepository.findDiscountProgramByDiscountProgramId(id);
        if (oldAccountDiscountProgram == null) {
            throw new Duplicate("Discount program not found!");// cho dung luon
        } else {
            try{
                if (discountProgram.getName() != null && !discountProgram.getName().isEmpty()) {
                    oldAccountDiscountProgram.setName(discountProgram.getName());
                }

                if (discountProgram.getDescription() != null && !discountProgram.getDescription().isEmpty()) {
                    oldAccountDiscountProgram.setDescription(discountProgram.getDescription());
                }

                if (discountProgram.getStartedDate() != null) {
                    oldAccountDiscountProgram.setStartedDate(discountProgram.getStartedDate());
                }

                if (discountProgram.getEndedDate() != null) {
                    oldAccountDiscountProgram.setEndedDate(discountProgram.getEndedDate());
                }

                if (discountProgram.getStatus() != null && !discountProgram.getStatus().isEmpty()) {
                    oldAccountDiscountProgram.setStatus(discountProgram.getStatus());
                }

                if (discountProgram.getPercentage() != 0) {
                    oldAccountDiscountProgram.setPercentage(discountProgram.getPercentage());
                }

                if (discountProgram.getAmount() != 0) {
                    oldAccountDiscountProgram.setAmount(discountProgram.getAmount());
                }

                // Lưu cập nhật vào cơ sở dữ liệu
                DiscountProgram updatedDiscountProgram = discountProgramRepository.save(oldAccountDiscountProgram);
                return modelMapper.map(updatedDiscountProgram, UpdateDiscountProgramResponse.class);
            } catch (Exception e) {
                throw new UpdatedException("Discount Program can not update!");
            }
        }
    }

}
