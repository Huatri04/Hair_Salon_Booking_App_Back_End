package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.AccountNotFoundException;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.*;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    SlotService slotService;

    @Autowired
    ShiftEmployeeService shiftEmployeeService;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    HairSalonBookingAppService hairSalonBookingAppService;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    TimeService timeService;

    @Autowired
    DiscountCodeRepository discountCodeRepository;

    //CUSTOMER XEM VÀ CHỌN DỊCH VỤ
    //- CHỨC NĂNG getAllAvailableService(); BÊN HAIR SALON BOOKING APP SERVICE : CUSTOMER XEM CÁC DỊCH VỤ KHẢ DỤNG

    //CUSTOMER XEM NGÀY HÔM NAY VÀ NGÀY TIẾP THEO CÓ CÁC CA LÀM VIỆC CỦA AI
    //- CHỨC NĂNG getAllAvailableSlots(); BÊN SHIFT EMPLOYEE SERVICE: CUSTOMER XEM CA LÀM VIỆC CỦA STYLIST VÀ LỰA CHỌN


    //CUSTOMER NHẬP MÃ GIẢM GIÁ (TÙY CHỌN)
    public DiscountCode getDiscountCode(String code) {    // HÀM LẤY MÃ GIẢM GIÁ
        DiscountCode discountCode = discountCodeRepository.findDiscountCodeById(code);
        if(discountCode != null && discountCode.getAppointment() == null){
            return discountCode;
        } else {
            throw new EntityNotFoundException("Code not available!");
        }
    }

    //TEST CHO VUI
    /*public long getAppoint(AppointmentRequest appointmentRequest){
        return appointmentRequest.getServiceIdList().get(0);
    }*/

    //HỆ THỐNG CHỐT -> CUSTOMER LÀM
    public AppointmentResponse createNewAppointment(AppointmentRequest appointmentRequest) {
        try {
            List<String> serviceNameList = new ArrayList<>();  //TẠO LIST CHỨA TÊN CÁC DỊCH VỤ CUSTOMER CHỌN
            List<Long> serviceIdList = appointmentRequest.getServiceIdList();  // LẤY DANH SÁCH ID DỊCH VỤ CUSTOMER CHỌN
            List<HairSalonService> hairSalonServiceList = new ArrayList<>();   // TẠO LIST CHỨA OBJ DỊCH VỤ
            double bonusDiscountCode = 0;    // PHÍ GIẢM GIÁ CỦA MÃ (NẾU CÓ)
            double bonusEmployee = 0;   // PHÍ TRẢ THÊM CHO STYLIST DỰA TRÊN CẤP ĐỘ
            double serviceFee = 0;
            for(long serviceId : serviceIdList){  // VỚI MỖI ID DỊCH VỤ STYLIST CHỌN, CHUYỂN NÓ THÀNH OBJ VÀ GÁN VÀO LIST
                HairSalonService service = serviceRepository.findHairSalonServiceById(serviceId);
                hairSalonServiceList.add(service);  // GÁN VÀO DANH SÁCH OBJ DỊCH VỤ
                serviceNameList.add(service.getName());  // GÁN VÀO DANH SÁCH TÊN DỊCH VỤ
                serviceFee += service.getCost();  // PHÍ GỐC CỦA SERVICE
            }
            //TẠO APPOINTMENT
            Appointment appointment = new Appointment();

            // SLOT
            //Slot slot = slotRepository.findSlotByIdAndIsAvailableTrue(appointmentRequest.getSlotId());
            Slot slot = slotRepository
                    .findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_IdAndIsAvailableTrue(
                            appointmentRequest.getStartHour(),
                            appointmentRequest.getDate(),
                            appointmentRequest.getStylistId()
                    );
            appointment.setSlot(slot);  // TÌM SLOT DỰA TRÊN THÔNG TIN REQUEST VÀ GÁN VÀO APPOINTMENT

            //ACCOUNT FOR CUSTOMER
            AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
            appointment.setAccountForCustomer(accountForCustomer);

            //HAIR SALON SERVICE
            appointment.setHairSalonServices(hairSalonServiceList);

            //DISCOUNT CODE
            if (!appointmentRequest.getDiscountCode().isEmpty()) {
                DiscountCode discountCode = getDiscountCode(appointmentRequest.getDiscountCode());
                appointment.setDiscountCode(discountCode);
                bonusDiscountCode += (discountCode.getDiscountProgram().getPercentage()) / 100;
            }

            AccountForEmployee accountForEmployee = slot.getShiftEmployee().getAccountForEmployee();
            if (accountForEmployee.getExpertStylistBonus() != 0) {
                bonusEmployee += (accountForEmployee.getExpertStylistBonus()) / 100;
            }

            double totalCost = serviceFee - (bonusDiscountCode * serviceFee) + (bonusEmployee * serviceFee);
            appointment.setCost(totalCost);

            appointment.setDate(slot.getDate());
            appointment.setStartHour(slot.getStartSlot());
            appointment.setStylist(slot.getShiftEmployee().getAccountForEmployee().getName());

            Appointment newAppointment = appointmentRepository.save(appointment);

            //SET OBJ APPOINTMENT VÀO CÁC OBJ KHÁC
            slot.setAppointments(newAppointment);
            slot.setAvailable(false);
            slotRepository.save(slot);

            List<Appointment> appointmentList = accountForCustomer.getAppointments();
            appointmentList.add(newAppointment);
            accountForCustomer.setAppointments(appointmentList);
            customerRepository.save(accountForCustomer);

            for(HairSalonService hairSalonService : hairSalonServiceList){
                List<Appointment> appointments = hairSalonService.getAppointments();
                appointments.add(newAppointment);
                hairSalonService.setAppointments(appointments);
                serviceRepository.save(hairSalonService);
            }

            if (!appointmentRequest.getDiscountCode().isEmpty()) {
                DiscountCode discountCode = getDiscountCode(appointmentRequest.getDiscountCode());
                discountCode.setAppointment(newAppointment);
                discountCodeRepository.save(discountCode);
            }

            AppointmentResponse appointmentResponse = new AppointmentResponse();

            appointmentResponse.setId(newAppointment.getId());
            appointmentResponse.setCost(newAppointment.getCost());
            appointmentResponse.setDay(newAppointment.getSlot().getDate());
            appointmentResponse.setStartHour(newAppointment.getSlot().getStartSlot());
            appointmentResponse.setCustomer(accountForCustomer.getCustomerName());
            appointmentResponse.setService(serviceNameList);
            appointmentResponse.setStylist(newAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getName());

            return appointmentResponse;
        } catch (Exception e) {
            throw new EntityNotFoundException("Can not create appointment: " + e.getMessage());
        }
    }

    //HỆ THỐNG TỰ TÌM STYLIST PHÙ HỢP VÀ ĐẶT LỊCH LUÔN CHO KHÁCH - LÀM BỞI CUSTOMER
    public  AppointmentResponse createNewAppointmentBySystem(AppointmentRequestSystem appointmentRequestSystem){
        List<Slot> slotList = slotRepository
                .findSlotsByDateAndStartSlotAndIsAvailableTrue(
                        appointmentRequestSystem.getDate(),
                        appointmentRequestSystem.getStartHour()
                );
        if(!slotList.isEmpty()){
            AccountForEmployee accountForEmployee = slotList.get(0).getShiftEmployee().getAccountForEmployee();
            /*for(Slot slot : slotList){
                accountForEmployee = slot.getShiftEmployee().getAccountForEmployee();
                break;
            }*/


            //LOGIC Y CHANG HÀM TẠO, KHÁC Ở CHỖ STYLIST EXPERT KHÔNG CỘNG BONUS THÊM
            try {
                List<String> serviceNameList = new ArrayList<>();
                List<Long> serviceIdList = appointmentRequestSystem.getServiceIdList();  // NGƯỜI DÙNG CHỌN NHIỀU LOẠI DỊCH VỤ
                List<HairSalonService> hairSalonServiceList = new ArrayList<>();
                double bonusDiscountCode = 0;    // PHÍ GIẢM GIÁ CỦA MÃ (NẾU CÓ)
                //double bonusEmployee = 0;   // PHÍ TRẢ THÊM CHO STYLIST DỰA TRÊN CẤP ĐỘ
                double serviceFee = 0;
                for(long serviceId : serviceIdList){
                    HairSalonService service = serviceRepository.findHairSalonServiceById(serviceId);
                    hairSalonServiceList.add(service);
                    serviceNameList.add(service.getName());
                    serviceFee += service.getCost();  // PHÍ GỐC CỦA SERVICE
                }
                //TẠO APPOINTMENT
                Appointment appointment = new Appointment();

                // SLOT
                //Slot slot = slotRepository.findSlotByIdAndIsAvailableTrue(appointmentRequest.getSlotId());
                Slot slot = slotRepository
                        .findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_IdAndIsAvailableTrue(
                                appointmentRequestSystem.getStartHour(),
                                appointmentRequestSystem.getDate(),
                                accountForEmployee.getId()
                        );
                appointment.setSlot(slot);

                //ACCOUNT FOR CUSTOMER
                AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
                appointment.setAccountForCustomer(accountForCustomer);

                //HAIR SALON SERVICE
                appointment.setHairSalonServices(hairSalonServiceList);

                //DISCOUNT CODE
                if (!appointmentRequestSystem.getDiscountCode().isEmpty()) {
                    DiscountCode discountCode = getDiscountCode(appointmentRequestSystem.getDiscountCode());
                    appointment.setDiscountCode(discountCode);
                    bonusDiscountCode += (discountCode.getDiscountProgram().getPercentage()) / 100;
                }

                //AccountForEmployee accountForEmployee = slot.getShiftEmployee().getAccountForEmployee();
                //if (accountForEmployee.getExpertStylistBonus() != 0) {
                    //bonusEmployee += (accountForEmployee.getExpertStylistBonus()) / 100;
                //}

                double totalCost = serviceFee - (bonusDiscountCode * serviceFee);
                appointment.setCost(totalCost);

                appointment.setDate(slot.getDate());
                appointment.setStartHour(slot.getStartSlot());
                appointment.setStylist(slot.getShiftEmployee().getAccountForEmployee().getName());

                Appointment newAppointment = appointmentRepository.save(appointment);

                //SET OBJ APPOINTMENT VÀO CÁC OBJ KHÁC
                slot.setAppointments(newAppointment);
                slot.setAvailable(false);
                slotRepository.save(slot);

                List<Appointment> appointmentList = accountForCustomer.getAppointments();
                appointmentList.add(newAppointment);
                accountForCustomer.setAppointments(appointmentList);
                customerRepository.save(accountForCustomer);

                for(HairSalonService hairSalonService : hairSalonServiceList){
                    List<Appointment> appointments = hairSalonService.getAppointments();
                    appointments.add(newAppointment);
                    hairSalonService.setAppointments(appointments);
                    serviceRepository.save(hairSalonService);
                }

                if (!appointmentRequestSystem.getDiscountCode().isEmpty()) {
                    DiscountCode discountCode = getDiscountCode(appointmentRequestSystem.getDiscountCode());
                    discountCode.setAppointment(newAppointment);
                    discountCodeRepository.save(discountCode);
                }

                AppointmentResponse appointmentResponse = new AppointmentResponse();

                appointmentResponse.setId(newAppointment.getId());
                appointmentResponse.setCost(newAppointment.getCost());
                appointmentResponse.setDay(newAppointment.getSlot().getDate());
                appointmentResponse.setStartHour(newAppointment.getSlot().getStartSlot());
                appointmentResponse.setCustomer(accountForCustomer.getCustomerName());
                appointmentResponse.setService(serviceNameList);
                appointmentResponse.setStylist(newAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getName());

                return appointmentResponse;
            } catch (Exception e) {
                throw new EntityNotFoundException("Can not create appointment: " + e.getMessage());
            }
            //-----------------------------------------------------------------------------------------------
        } else {
            throw new EntityNotFoundException("Can not find slot!");
        }
    }

    //STAFF ĐẶT LỊCH HẸN GIÚP CUSTOMER
    public AppointmentResponse createNewAppointmentByStaff(AppointmentRequest appointmentRequest){
        try {
            List<String> serviceNameList = new ArrayList<>();  //TẠO LIST CHỨA TÊN CÁC DỊCH VỤ CUSTOMER CHỌN
            List<Long> serviceIdList = appointmentRequest.getServiceIdList();  // LẤY DANH SÁCH ID DỊCH VỤ CUSTOMER CHỌN
            List<HairSalonService> hairSalonServiceList = new ArrayList<>();   // TẠO LIST CHỨA OBJ DỊCH VỤ
            double bonusEmployee = 0;   // PHÍ TRẢ THÊM CHO STYLIST DỰA TRÊN CẤP ĐỘ
            double serviceFee = 0;
            for(long serviceId : serviceIdList){  // VỚI MỖI ID DỊCH VỤ STYLIST CHỌN, CHUYỂN NÓ THÀNH OBJ VÀ GÁN VÀO LIST
                HairSalonService service = serviceRepository.findHairSalonServiceById(serviceId);
                hairSalonServiceList.add(service);  // GÁN VÀO DANH SÁCH OBJ DỊCH VỤ
                serviceNameList.add(service.getName());  // GÁN VÀO DANH SÁCH TÊN DỊCH VỤ
                serviceFee += service.getCost();  // PHÍ GỐC CỦA SERVICE
            }
            //TẠO APPOINTMENT
            Appointment appointment = new Appointment();

            // SLOT
            //Slot slot = slotRepository.findSlotByIdAndIsAvailableTrue(appointmentRequest.getSlotId());
            Slot slot = slotRepository
                    .findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_IdAndIsAvailableTrue(
                            appointmentRequest.getStartHour(),
                            appointmentRequest.getDate(),
                            appointmentRequest.getStylistId()
                    );
            appointment.setSlot(slot);  // TÌM SLOT DỰA TRÊN THÔNG TIN REQUEST VÀ GÁN VÀO APPOINTMENT

            //ACCOUNT FOR CUSTOMER
            appointment.setAccountForCustomer(null); // GUEST CHƯA CÓ ACC

            //HAIR SALON SERVICE
            appointment.setHairSalonServices(hairSalonServiceList);

            //DISCOUNT CODE
            appointment.setDiscountCode(null);

            AccountForEmployee accountForEmployee = slot.getShiftEmployee().getAccountForEmployee();
            if (accountForEmployee.getExpertStylistBonus() != 0) {
                bonusEmployee += (accountForEmployee.getExpertStylistBonus()) / 100;
            }

            double totalCost = serviceFee + (bonusEmployee * serviceFee);
            appointment.setCost(totalCost);

            appointment.setDate(slot.getDate());
            appointment.setStartHour(slot.getStartSlot());
            appointment.setStylist(slot.getShiftEmployee().getAccountForEmployee().getName());

            Appointment newAppointment = appointmentRepository.save(appointment);

            //SET OBJ APPOINTMENT VÀO CÁC OBJ KHÁC
            slot.setAppointments(newAppointment);
            slot.setAvailable(false);
            slotRepository.save(slot);

            for(HairSalonService hairSalonService : hairSalonServiceList){
                List<Appointment> appointments = hairSalonService.getAppointments();
                appointments.add(newAppointment);
                hairSalonService.setAppointments(appointments);
                serviceRepository.save(hairSalonService);
            }

            AppointmentResponse appointmentResponse = new AppointmentResponse();

            appointmentResponse.setId(newAppointment.getId());
            appointmentResponse.setCost(newAppointment.getCost());
            appointmentResponse.setDay(newAppointment.getSlot().getDate());
            appointmentResponse.setStartHour(newAppointment.getSlot().getStartSlot());
            appointmentResponse.setCustomer("Guest");
            appointmentResponse.setService(serviceNameList);
            appointmentResponse.setStylist(newAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getName());

            return appointmentResponse;
        } catch (Exception e) {
            throw new EntityNotFoundException("Can not create appointment: " + e.getMessage());
        }
    }

    //HỆ THỐNG TỰ TÌM STYLIST VÀ ĐẶT LỊCH CHO KHÁCH - LÀM BỞI STAFF
    public  AppointmentResponse createNewAppointmentBySystemStaff(AppointmentRequestSystem appointmentRequestSystem){
        List<Slot> slotList = slotRepository
                .findSlotsByDateAndStartSlotAndIsAvailableTrue(
                        appointmentRequestSystem.getDate(),
                        appointmentRequestSystem.getStartHour()
                );
        if(!slotList.isEmpty()){
            AccountForEmployee accountForEmployee = slotList.get(0).getShiftEmployee().getAccountForEmployee();

            //LOGIC Y CHANG HÀM TẠO, KHÁC Ở CHỖ STYLIST EXPERT KHÔNG CỘNG BONUS THÊM
            try {
                List<String> serviceNameList = new ArrayList<>();
                List<Long> serviceIdList = appointmentRequestSystem.getServiceIdList();  // NGƯỜI DÙNG CHỌN NHIỀU LOẠI DỊCH VỤ
                List<HairSalonService> hairSalonServiceList = new ArrayList<>();

                double serviceFee = 0;
                for(long serviceId : serviceIdList){
                    HairSalonService service = serviceRepository.findHairSalonServiceById(serviceId);
                    hairSalonServiceList.add(service);
                    serviceNameList.add(service.getName());
                    serviceFee += service.getCost();  // PHÍ GỐC CỦA SERVICE
                }
                //TẠO APPOINTMENT
                Appointment appointment = new Appointment();

                // SLOT
                //Slot slot = slotRepository.findSlotByIdAndIsAvailableTrue(appointmentRequest.getSlotId());
                Slot slot = slotRepository
                        .findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_IdAndIsAvailableTrue(
                                appointmentRequestSystem.getStartHour(),
                                appointmentRequestSystem.getDate(),
                                accountForEmployee.getId()
                        );
                appointment.setSlot(slot);

                //ACCOUNT FOR CUSTOMER
                appointment.setAccountForCustomer(null);

                //HAIR SALON SERVICE
                appointment.setHairSalonServices(hairSalonServiceList);

                //DISCOUNT CODE
                appointment.setDiscountCode(null);

                double totalCost = serviceFee;
                appointment.setCost(totalCost);

                appointment.setDate(slot.getDate());
                appointment.setStartHour(slot.getStartSlot());
                appointment.setStylist(slot.getShiftEmployee().getAccountForEmployee().getName());

                Appointment newAppointment = appointmentRepository.save(appointment);

                //SET OBJ APPOINTMENT VÀO CÁC OBJ KHÁC
                slot.setAppointments(newAppointment);
                slot.setAvailable(false);
                slotRepository.save(slot);

                for(HairSalonService hairSalonService : hairSalonServiceList){
                    List<Appointment> appointments = hairSalonService.getAppointments();
                    appointments.add(newAppointment);
                    hairSalonService.setAppointments(appointments);
                    serviceRepository.save(hairSalonService);
                }

                AppointmentResponse appointmentResponse = new AppointmentResponse();

                appointmentResponse.setId(newAppointment.getId());
                appointmentResponse.setCost(newAppointment.getCost());
                appointmentResponse.setDay(newAppointment.getSlot().getDate());
                appointmentResponse.setStartHour(newAppointment.getSlot().getStartSlot());
                appointmentResponse.setCustomer("Guest");
                appointmentResponse.setService(serviceNameList);
                appointmentResponse.setStylist(newAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getName());

                return appointmentResponse;
            } catch (Exception e) {
                throw new EntityNotFoundException("Can not create appointment: " + e.getMessage());
            }
            //-----------------------------------------------------------------------------------------------
        } else {
            throw new EntityNotFoundException("Can not find slot!");
        }
    }



    // UPDATE APPOINTMENT ->  CUSTOMER LÀM
    public AppointmentResponse updateAppointment(AppointmentUpdate appointmentUpdate, long idAppointment){
        AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
        Appointment oldAppointment = appointmentRepository
                .findAppointmentByIdAndAccountForCustomerAndIsDeletedFalse(idAppointment, accountForCustomer);
        if(oldAppointment == null){
            throw new EntityNotFoundException("Appointment not found!!!");
        }
        // LẤY LẠI APPOINTMENT REQUEST CŨ
        List<Long> oldServiceIdList = new ArrayList<>();
        List<HairSalonService> hairSalonServiceList = oldAppointment.getHairSalonServices();
        for(HairSalonService service : hairSalonServiceList){
            long idService = service.getId();
            oldServiceIdList.add(idService);
        }
        AppointmentRequest oldRequest = new AppointmentRequest();
        oldRequest.setDate(oldAppointment.getSlot().getDate());
        DiscountCode oldCode = oldAppointment.getDiscountCode();
        if(oldCode == null){
            oldRequest.setDiscountCode("");
        } else {
            oldRequest.setDiscountCode(oldCode.getId());
        }

        oldRequest.setStartHour(oldAppointment.getSlot().getStartSlot());
        oldRequest.setStylistId(oldAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getId());
        oldRequest.setServiceIdList(oldServiceIdList);


        // TẠO APPOINTMENT MỚI
        AppointmentRequest appointmentRequest = new AppointmentRequest();

        String newDate = appointmentUpdate.getDate();
        String newHour = appointmentUpdate.getStartHour();
        String newStylistId = appointmentUpdate.getStylistId();
        List<Long> newServiceIdList = appointmentUpdate.getServiceIdList();
        String newCode = appointmentUpdate.getDiscountCode();
        if(!newDate.isEmpty() && !newDate.equals(oldRequest.getDate())){
            appointmentRequest.setDate(newDate);
        } else {
            appointmentRequest.setDate(oldRequest.getDate());
        }

        if(!newHour.isEmpty() && !newHour.equals(oldRequest.getStartHour())){
            appointmentRequest.setStartHour(newHour);
        } else {
            appointmentRequest.setStartHour(oldRequest.getStartHour());
        }

        if (!newStylistId.isEmpty() && !newStylistId.equals(oldRequest.getStylistId())){
            appointmentRequest.setStylistId(newStylistId);
        } else {
            appointmentRequest.setStylistId(oldRequest.getStylistId());
        }

        if(!newServiceIdList.isEmpty() && !newServiceIdList.equals(oldRequest.getServiceIdList())){
            appointmentRequest.setServiceIdList(newServiceIdList);
        } else {
            appointmentRequest.setServiceIdList(oldRequest.getServiceIdList());
        }

        if(!newCode.isEmpty() && !newCode.equals(oldRequest.getDiscountCode())){
            appointmentRequest.setDiscountCode(newCode);
        } else {
            appointmentRequest.setDiscountCode(oldRequest.getDiscountCode());
        }

        //XÓA APPOINTMENT CŨ VÀ TẠO CÁI MỚI
        deleteAppointmentByCustomer(oldAppointment.getId());
        return createNewAppointment(appointmentRequest);
    }
/*    //CẬP NHẬT APPOINTMENT -> CUSTOMER LÀM
    // CẬP NHẬT APPOINTMENT CÓ 2 TRƯỜNG HỢP:
    //1. CUSTOMER CHƯA GỬI ĐƠN : TRƯỜNG HỢP NÀY KHÔNG VẤN ĐỀ GÌ, CUSTOMER CÓ THỂ THAO TÁC LẠI CÁC HÀM Ở TRÊN ĐỂ LỰA CHỌN LẠI
    // SAU ĐÓ CHỈNH SỬA ĐẦU VÀO CỦA createNewAppointment LÀ XONG
    //2. CUSTOMER ĐÃ GỬI ĐƠN VÀ MUỐN LẤY LẠI ĐƠN ĐỂ SỬA: CÓ 2 CÁCH GIẢI QUYẾT:
    // A. CUSTOMER HỦY ĐƠN VÀ LÀM LẠI ĐƠN KHÁC
    // B. CUSTOMER LẤY LẠI ĐƠN VÀ UPDATE LẠI THÔNG TIN (TRƯỚC KHI ĐC APPROVE)

    public AppointmentResponse updateAppointment(AppointmentUpdate appointmentUpdate, long id) {
        String status = "Appointment sent!";
        AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
        Appointment oldAppointment = appointmentRepository.findAppointmentByIdAndAccountForCustomerAndStatusAndIsDeletedFalse(id, accountForCustomer, status);  //TÌM LẠI APPOINTMENT CŨ
        if (oldAppointment != null) {   // TÌM THẤY
            try{
                double cost = 0;          // TÍNH TOÁN COST TỪ ĐẦU
                double newBonusEmployee = 0;
                double newBonusCode = 0;
                if (appointmentUpdate.getServiceId() != 0) {      // NẾU CUSTOMER CÓ NHẬP ID SERVICE
                    oldAppointment.setHairSalonService(getService(appointmentUpdate.getServiceId()));       // CẬP NHẬT
                    //cost += serviceRepository.findHairSalonServiceByIdAndIsAvailableTrue(appointmentUpdate.getServiceId()).getCost(); // CẬP NHẬT LẠI VÀO COST
                    cost += oldAppointment.getHairSalonService().getCost();
                } else {                                        // NẾU CUSTOMER KHÔNG NHẬP ID SERVICE
                    cost += oldAppointment.getHairSalonService().getCost();  //  COST SERVICE NHƯ CŨ
                }

                if (appointmentUpdate.getSlotId() != 0) {         // NẾU CUSTOMER CÓ NHẬP SLOT ID
                    oldAppointment.setSlot(getAvailableSlot(appointmentUpdate.getSlotId()));  // CẬP NHẬT
                }

                if (!appointmentUpdate.getStylistId().isEmpty()) {       // NẾU CUSTOMER NHẬP STYLIST ID -> STYLIST MỚI

                    if (getStylist(appointmentUpdate.getStylistId()).getExpertStylistBonus() != 0) {    // NẾU STYLIST MỚI LÀ DÂN CHUYÊN
                        AccountForEmployee accountForEmployee = getStylist(appointmentUpdate.getStylistId());
                        newBonusEmployee += (accountForEmployee.getExpertStylistBonus()) / 100;   // CẬP NHẬT BONUS CHO NÓ
                    }

                } else {                                                                        // NẾU CUSTOMER KO NHẬP -> STYLIST CŨ
                    String oldStylistId = oldAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getId();
                    AccountForEmployee account = getStylist(oldStylistId);

                    if (account.getExpertStylistBonus() != 0) {      // STYLIST CŨ VẪN LÀ DÂN CHUYÊN
                        newBonusEmployee += (account.getExpertStylistBonus()) / 100;
                    }

                }

                if (!appointmentUpdate.getDiscountCodeId().isEmpty()) {        //  CUSTOMER NHẬP MÃ MỚI

                    DiscountCode newCode = getDiscountCode(appointmentUpdate.getDiscountCodeId());
                    DiscountProgram discountProgram = newCode.getDiscountProgram();
                    newBonusCode += (discountProgram.getPercentage()) / 100;

                } else {      // MÃ CŨ

                    DiscountCode oldCode = oldAppointment.getDiscountCode();
                    DiscountProgram oldProgram = oldCode.getDiscountProgram();
                    newBonusCode += (oldProgram.getPercentage()) / 100;

                }

                double newCost = cost + (newBonusEmployee * cost) + (newBonusCode * cost);
                oldAppointment.setCost(newCost);

                Appointment newAppointment = appointmentRepository.save(oldAppointment);     // LƯU LẠI LÊN DB

                //AppointmentResponse appointmentResponse = modelMapper.map(newAppointment, AppointmentResponse.class);
                appointmentResponse.setServiceId(newAppointment.getHairSalonService().getId());
                appointmentResponse.setCustomerId(newAppointment.getAccountForCustomer().getPhoneNumber());
                appointmentResponse.setSlotId(newAppointment.getSlot().getId());
                appointmentResponse.setDiscountCodeId(newAppointment.getDiscountCode().getId());

                AppointmentResponse appointmentResponse = new AppointmentResponse();

                appointmentResponse.setId(newAppointment.getId());
                appointmentResponse.setCost(newAppointment.getCost());
                appointmentResponse.setDay(newAppointment.getSlot().getShiftEmployee().getShiftInWeek().getDayOfWeek());
                appointmentResponse.setStartHour(newAppointment.getSlot().getStartSlot());
                appointmentResponse.setCustomer(newAppointment.getAccountForCustomer().getCustomerName());
                appointmentResponse.setService(newAppointment.getHairSalonService().getName());
                appointmentResponse.setStylist(newAppointment.getSlot().getShiftEmployee().getName());
                appointmentResponse.setStatus(newAppointment.getStatus());

                return appointmentResponse;
            } catch (Exception e) {
                throw new EntityNotFoundException("Can not update appointment: " + e.getMessage());
            }
        } else {
            throw new EntityNotFoundException("Appointment not found!");
        }
    }*/

    // XÓA APPOINTMENT  -> STAFF LÀM KHI STYLIST CÓ VIỆC BẬN TRONG SLOT ĐÓ
    public String deleteAppointmentByStaff(long slotId){
        Appointment oldAppointment = appointmentRepository
                .findAppointmentBySlot_IdAndIsDeletedFalse(slotId);  //TÌM LẠI APPOINTMENT CŨ
        if(oldAppointment != null){
            oldAppointment.setDeleted(true);

            //SLOT
            Slot slot = oldAppointment.getSlot();
            slot.setAppointments(null);
            slot.setAvailable(false);
            slotRepository.save(slot);

            oldAppointment.setSlot(null);

            //DISCOUNT CODE
            DiscountCode discountCode = oldAppointment.getDiscountCode();
            if(discountCode != null){
                discountCode.setAppointment(null);
                discountCodeRepository.save(discountCode);
            }

            oldAppointment.setDiscountCode(null);

            Appointment newAppointment = appointmentRepository.save(oldAppointment);     // LƯU LẠI LÊN DB

            String message;
            if(newAppointment.getAccountForCustomer() == null){
                message = "Delete successfully!";
            } else {
                String phoneNumber = newAppointment.getAccountForCustomer().getPhoneNumber();
                String email = newAppointment.getAccountForCustomer().getEmail();

                message = "Delete successfully: " + "Phone = " + phoneNumber + "; Email = " + email;
            }

            return message;

        } else {  // KHÔNG CÓ APPOINTMENT NÀO ĐƯỢC ĐẶT TRONG SLOT ĐÓ
            Slot slot = slotRepository.findSlotById(slotId);
            slot.setAvailable(false);
            slotRepository.save(slot);
            String message = "Delete successfully!";
            return message;
        }
    }

    // XÓA APPOINTMENT -> CUSTOMER LÀM
    public String deleteAppointmentByCustomer(long idAppointment){
        AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
        Appointment oldAppointment = appointmentRepository
                .findAppointmentByIdAndAccountForCustomerAndIsDeletedFalse(idAppointment, accountForCustomer);  //TÌM LẠI APPOINTMENT CŨ
        if(oldAppointment != null){
            oldAppointment.setDeleted(true);

            //SLOT
            Slot slot = oldAppointment.getSlot();
            slot.setAppointments(null);
            slot.setAvailable(true);
            slotRepository.save(slot);

            oldAppointment.setSlot(null);

            //DISCOUNT CODE
            DiscountCode discountCode = oldAppointment.getDiscountCode();
            if(discountCode != null){
                discountCode.setAppointment(null);
                discountCodeRepository.save(discountCode);
            }

            oldAppointment.setDiscountCode(null);

            appointmentRepository.save(oldAppointment);     // LƯU LẠI LÊN DB

            String message = "Delete successfully!!!";
            return message;

        } else {
            throw new EntityNotFoundException("Appointment not found!");
        }
    }
    // CÓ 2 TÌNH HUỐNG KHI XÓA:
    //- CUSTOMER XÓA TRƯỚC , STAFF XÓA SAU -> KO VẤN ĐỀ VÌ STATUS STAFF CHÈN LÊN CUSTOMER
    //- STAFF XÓA TRƯỚC , SLOT ĐÓ COI NHƯ KO KHẢ DỤNG -> CUSTOMER CHẮC CHẮN KO CHỌN -> OK

    // NẾU CÓ VẤN ĐỀ ĐỘT XUẤT, STAFF GỬI EMAIL ĐẾN CUSTOMER
    // STAFF XÓA CÁC APPOINMENTS NẾU STYLIST NHẬN APPOINTMENT ĐÓ BẬN TRONG NGÀY
    public List<String> deleteAppointmentsOfStylist(DeleteAllAppointmentsRequest deleteAllAppointmentsRequest){
        /*List<AvailableSlot> availableSlotList = shiftEmployeeService.getAllAvailableSlots(deleteAllAppointmentsRequest.getDate()); // TÌM CÁC SLOT TRONG NGÀY
        List<String> messages = new ArrayList<>();
        if(availableSlotList != null){
            for(AvailableSlot availableSlot : availableSlotList) {
                Slot slot = slotRepository.findSlotByIdAndIsAvailableFalse(availableSlot.getSlotId());  // TÌM SLOT KO KHẢ DỤNG
                if(slot != null){
                    if(slot.getShiftEmployee().getAccountForEmployee().getId().equals(deleteAllAppointmentsRequest.getStylistId())){
                        String message = deleteAppointmentByStaff(slot.getId());
                        messages.add(message);
                    }
                }
            }

            return messages;
        } else {
            throw new EntityNotFoundException("Slots not found!");
        }*/

        List<Slot> slotList = slotRepository
                .findSlotsByShiftEmployee_AccountForEmployee_IdAndDate(
                        deleteAllAppointmentsRequest.getStylistId(),
                        deleteAllAppointmentsRequest.getDate());
        if(slotList == null){
            throw new EntityNotFoundException("Slot not found!");
        }
        List<String> messages = new ArrayList<>();
        for(Slot slot : slotList){
            String message = deleteAppointmentByStaff(slot.getId());
            messages.add(message);
        }
        return messages;
    }

    // CUSTOMER XEM LẠI LỊCH SỬ APPOINTMENT
    public List<AppointmentResponseInfo> checkAppointmentHistory(){
        AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
        List<Appointment> appointmentList = accountForCustomer.getAppointments();
        if(!appointmentList.isEmpty()){
            List<AppointmentResponseInfo> appointmentResponseList = new ArrayList<>();
            for(Appointment appointment : appointmentList){
                AppointmentResponseInfo appointmentResponse = new AppointmentResponseInfo();

                appointmentResponse.setId(appointment.getId());
                appointmentResponse.setCost(appointment.getCost());
                appointmentResponse.setDate(appointment.getDate());
                appointmentResponse.setStartHour(appointment.getStartHour());
                appointmentResponse.setCustomer(accountForCustomer.getCustomerName());
                appointmentResponse.setDeleted(appointment.isDeleted());
                appointmentResponse.setCompleted(appointment.isCompleted());

                List<String> serviceNameList = new ArrayList<>();
                List<HairSalonService> hairSalonServiceList = appointment.getHairSalonServices();
                for(HairSalonService service : hairSalonServiceList) {
                    String serviceName = service.getName();
                    serviceNameList.add(serviceName);
                }
                appointmentResponse.setService(serviceNameList);
                appointmentResponse.setStylist(appointment.getStylist());

                appointmentResponseList.add(appointmentResponse);
            }
            Collections.reverse(appointmentResponseList);
            return appointmentResponseList;

        } else {
            throw new EntityNotFoundException("Appointment not found!");
        }
    }


    //CUSTOMER TÍNH TIỀN, STAFF CHECK CHO APPOINTMENT
    public String completeAppointment(CompleteAppointmentRequest completeAppointmentRequest){
        Slot slot = slotRepository
                .findSlotByStartSlotAndShiftEmployee_AccountForEmployee_IdAndDate(
                        completeAppointmentRequest.getStartSlot(),
                        completeAppointmentRequest.getStylistId(),
                        completeAppointmentRequest.getDate()
                );
        if(slot != null){
            Appointment appointment = slot.getAppointments();
            if(appointment == null){
                throw new EntityNotFoundException("Appointment not found!");
            }

            appointment.setCompleted(true);
            Appointment newAppontment = appointmentRepository.save(appointment);

            AccountForCustomer accountForCustomer = newAppontment.getAccountForCustomer();
            if(accountForCustomer != null){  // ĐÂY LÀ CUSTOMER KHÔNG PHẢI GUEST
                long point = accountForCustomer.getPoint();
                long newPoint = point + 1;
                accountForCustomer.setPoint(newPoint);
                customerRepository.save(accountForCustomer);
            }
            // TRƯỜNG HỢP GUEST(KO CÓ ACC) MUỐN THANH TOÁN THÌ CHỈ CẦN CỘNG THÊM SLOT HOÀN THÀNH CHO STYLIST LÀ ĐỦ

            AccountForEmployee account = slot.getShiftEmployee().getAccountForEmployee();
            account.setCompletedSlot(account.getCompletedSlot() + 1);
            employeeRepository.save(account);

            /*slot.setAppointments(null);
            slot.setAvailable(true);
            slotRepository.save(slot);*/ // KHI TÍNH CHECK COMPLETE SLOT CÓ NGHĨA SLOT ĐÓ ĐÃ QUA RỒI, KO CÒN DÙNG NỮA

            String message = "Complete successfully!!!";
            return message;
        } else {
            throw new EntityNotFoundException("Slot not found!");
        }
    }

    // DANH SÁCH CÁC STYLIST KHẢ DỤNG -> HỖ TRỢ HÀM DƯỚI
    public List<AccountForEmployee> getAllStylistList(){
        String role = "Stylist";
        String status = "Workday";
        List<AccountForEmployee> list = employeeRepository.findAccountForEmployeesByRoleAndStatusAndIsDeletedFalse(role, status);
        if(list != null){
            return list;
        } else {
            throw new EntityNotFoundException("Stylist not found!");
        }
    }

    // HÀM TRẢ VỀ DANH SÁCH CÁC STYLIST VÀ KPI
    public List<KPITotal> getAllKPI(){
        List<KPITotal> kpiTotalList = new ArrayList<>();
        for(AccountForEmployee account : getAllStylistList()){
            KPITotal kpiTotal = new KPITotal();
            kpiTotal.setStylistId(account.getId());
            kpiTotal.setKPI(account.getKPI());
            kpiTotal.setTotal(account.getCompletedSlot());

            kpiTotalList.add(kpiTotal);
            account.setCompletedSlot(0);
            employeeRepository.save(account);
        }
        return kpiTotalList;
    }


    public long forFun(){
        Slot slot = slotRepository.findSlotById(1);
        return slot.getAppointments().getId();
    }


}
