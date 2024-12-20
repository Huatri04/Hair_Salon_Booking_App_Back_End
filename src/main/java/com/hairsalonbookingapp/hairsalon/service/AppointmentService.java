package com.hairsalonbookingapp.hairsalon.service;

import com.hairsalonbookingapp.hairsalon.entity.*;
import com.hairsalonbookingapp.hairsalon.exception.EntityNotFoundException;
import com.hairsalonbookingapp.hairsalon.model.EmailDetail;
import com.hairsalonbookingapp.hairsalon.model.EmailDetailDeleteAppointment;
import com.hairsalonbookingapp.hairsalon.model.request.*;
import com.hairsalonbookingapp.hairsalon.model.response.*;
import com.hairsalonbookingapp.hairsalon.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    AccountForCustomerRepository accountForCustomerRepository;

    @Autowired
    SlotRepository slotRepository;

    @Autowired
    TimeService timeService;
    @Autowired
    EmailService emailService;

    @Autowired
    DiscountCodeRepository discountCodeRepository;

    @Autowired
    ShiftEmployeeRepository shiftEmployeeRepository;

    @Autowired
    KPIMonthRepository kpiMonthRepository;

    //CUSTOMER XEM VÀ CHỌN DỊCH VỤ
    //- CHỨC NĂNG getAllAvailableService(); BÊN HAIR SALON BOOKING APP SERVICE : CUSTOMER XEM CÁC DỊCH VỤ KHẢ DỤNG

    //CUSTOMER XEM NGÀY HÔM NAY VÀ NGÀY TIẾP THEO CÓ CÁC CA LÀM VIỆC CỦA AI
    //- CHỨC NĂNG getAllAvailableSlots(); BÊN SHIFT EMPLOYEE SERVICE: CUSTOMER XEM CA LÀM VIỆC CỦA STYLIST VÀ LỰA CHỌN


    //CUSTOMER NHẬP MÃ GIẢM GIÁ (TÙY CHỌN)
    public DiscountCode getDiscountCode(String code) {    // HÀM LẤY MÃ GIẢM GIÁ
        DiscountCode discountCode = discountCodeRepository.findDiscountCodeByDiscountCodeId(code);
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
                    .findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_EmployeeIdAndIsAvailableTrue(
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
            if (accountForEmployee.getStylistSelectionFee() != 0) {
                bonusEmployee += (accountForEmployee.getStylistSelectionFee());
            }

            double totalCost = serviceFee - (bonusDiscountCode * serviceFee) + (bonusEmployee);
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
            accountForCustomerRepository.save(accountForCustomer);

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

            appointmentResponse.setId(newAppointment.getAppointmentId());
            appointmentResponse.setCost(newAppointment.getCost());
            appointmentResponse.setDay(newAppointment.getSlot().getDate());
            appointmentResponse.setStartHour(newAppointment.getSlot().getStartSlot());
            appointmentResponse.setCustomer(accountForCustomer.getName());
            appointmentResponse.setService(serviceNameList);
            appointmentResponse.setStylist(newAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getName());

            EmailDetailCreateAppointment emailDetail = new EmailDetailCreateAppointment();
            emailDetail.setReceiver(appointment.getAccountForCustomer());
            emailDetail.setSubject("You have scheduled an appointment at our salon!");
            emailDetail.setAppointmentId(appointmentResponse.getId());
            emailDetail.setServiceName(appointmentResponse.getService());
            emailDetail.setNameStylist(appointmentResponse.getStylist());
            emailDetail.setDay(appointmentResponse.getDay());
            emailDetail.setStartHour(appointmentResponse.getStartHour());
            emailService.sendEmailCreateAppointment(emailDetail);

            return appointmentResponse;
        } catch (Exception e) {
            throw new EntityNotFoundException("Can not create appointment: " + e.getMessage());
        }
    }


    // XÓA APPOINTMENT  -> STAFF LÀM KHI STYLIST CÓ VIỆC BẬN TRONG SLOT ĐÓ
    public String deleteAppointmentByStaff(long slotId){
        Appointment oldAppointment = appointmentRepository
                .findAppointmentBySlot_SlotIdAndIsDeletedFalse(slotId);  //TÌM LẠI APPOINTMENT CŨ
        if(oldAppointment != null){
            oldAppointment.setDeleted(true);

            if(oldAppointment.getAccountForCustomer() != null){
                EmailDetailDeleteAppointment emailDetail = new EmailDetailDeleteAppointment();
                emailDetail.setReceiver(oldAppointment.getAccountForCustomer());
                emailDetail.setSubject("You have canceled scheduled an appointment at our salon!");
                emailDetail.setAppointmentId(oldAppointment.getAppointmentId());
                emailDetail.setServiceName(oldAppointment.getHairSalonServices());
                emailDetail.setNameStylist(oldAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getName());
                emailDetail.setDay(oldAppointment.getDate());
                emailDetail.setStartHour(oldAppointment.getSlot().getStartSlot());
                emailService.sendEmailChangedAppointment(emailDetail);
            }

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
            Slot slot = slotRepository.findSlotBySlotId(slotId);
            slot.setAvailable(false);
            slotRepository.save(slot);
            String message = "Delete successfully!";
            return message;
        }
    }



    // XÓA APPOINTMENT -> CUSTOMER LÀM
    public String deleteAppointmentByCustomer(long idAppointment){
        Appointment oldAppointment = appointmentRepository
                .findAppointmentByAppointmentId(idAppointment);  //TÌM LẠI APPOINTMENT CŨ
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
                .findSlotsByShiftEmployee_AccountForEmployee_EmployeeIdAndDate(
                        deleteAllAppointmentsRequest.getStylistId(),
                        deleteAllAppointmentsRequest.getDate());
        if(slotList.isEmpty()){
            throw new EntityNotFoundException("Slot not found!");
        }

        ShiftEmployee shiftEmployee = slotList.get(0).getShiftEmployee();
        shiftEmployee.setAvailable(false);
        shiftEmployeeRepository.save(shiftEmployee);

        List<String> messages = new ArrayList<>();
        for(Slot slot : slotList){
            String message = deleteAppointmentByStaff(slot.getSlotId());
            messages.add(message);
        }
        return messages;
    }


    //CUSTOMER TÍNH TIỀN, STAFF CHECK CHO APPOINTMENT
    public Appointment completeAppointmentById(long appointmentId) {
        Appointment appointment = appointmentRepository.findAppointmentByAppointmentId(appointmentId);
        if (appointment == null) {
            throw new EntityNotFoundException("Appointment not found!");
        }

        appointment.setCompleted(true);
        appointment.setStatus("Đã thanh toán");
        Appointment newAppointment = appointmentRepository.save(appointment);

        AccountForCustomer accountForCustomer = newAppointment.getAccountForCustomer();
        if (accountForCustomer != null) {  // CUSTOMER (Không phải GUEST)
            long point = accountForCustomer.getPoint();
            long newPoint = point + 1;
            accountForCustomer.setPoint(newPoint);
            accountForCustomerRepository.save(accountForCustomer);
        }

        // GUEST không có tài khoản -> Chỉ cần cộng slot hoàn thành cho stylist
        AccountForEmployee account = newAppointment.getSlot().getShiftEmployee().getAccountForEmployee();
        account.setKPI(account.getKPI() + 1);
        employeeRepository.save(account);

        return newAppointment;
    }


    // CUSTOMER XEM LẠI LỊCH SỬ APPOINTMENT
    public List<AppointmentResponseInfo> checkAppointmentHistory(){
        AccountForCustomer accountForCustomer = authenticationService.getCurrentAccountForCustomer();
        List<Appointment> appointmentList = accountForCustomer.getAppointments();
        if(!appointmentList.isEmpty()){
            List<AppointmentResponseInfo> appointmentResponseList = new ArrayList<>();
            for(Appointment appointment : appointmentList){
                AppointmentResponseInfo appointmentResponse = new AppointmentResponseInfo();

                appointmentResponse.setId(appointment.getAppointmentId());
                appointmentResponse.setCost(appointment.getCost());
                appointmentResponse.setDate(appointment.getDate());
                appointmentResponse.setStartHour(appointment.getStartHour());
                appointmentResponse.setCustomer(accountForCustomer.getName());
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

//    // HÀM TRẢ VỀ DANH SÁCH CÁC STYLIST VÀ KPI
//    public List<KPITotal> getAllKPI(){
//        List<KPITotal> kpiTotalList = new ArrayList<>();
//        for(AccountForEmployee account : getAllStylistList()){
//            KPITotal kpiTotal = new KPITotal();
//            kpiTotal.setStylistId(account.getEmployeeId());
//            kpiTotal.setKPI(account.getKPI());
//            kpiTotal.setTargetKPI(account.getTargetKPI());
//
//            kpiTotalList.add(kpiTotal);
//            account.setKPI(0);
//            employeeRepository.save(account);
//        }
//        return kpiTotalList;
//    }

    public KPIMonthListResponse getAllKPI(String month, int page, int size) {
        // Lấy dữ liệu từ repository và phân trang
        Page<KPIMonth> kpiMonthPage = kpiMonthRepository.findByMonthAndEmployeeRole(month,"Stylist", PageRequest.of(page, size));

        // Chuyển đổi từng KPIMonth thành KPIMonthResponseDTO
        List<KPIMonthResponseDTO> content = new ArrayList<>();
        for (KPIMonth kpiMonth : kpiMonthPage.getContent()) {
            KPIMonthResponseDTO dto = new KPIMonthResponseDTO();
            dto.setId(kpiMonth.getId());
            dto.setKpi(kpiMonth.getKpi());
            dto.setTargetKPI(kpiMonth.getTargetKPI());
            dto.setMonth(kpiMonth.getMonth());
            dto.setEmployeeId(kpiMonth.getEmployee().getEmployeeId());
            dto.setEmployeeName(kpiMonth.getEmployee().getName());
            content.add(dto);
        }

        // Tạo đối tượng phản hồi và thiết lập các thuộc tính
        KPIMonthListResponse kpiMonthListResponse = new KPIMonthListResponse();
        kpiMonthListResponse.setTotalPage(kpiMonthPage.getTotalPages());
        kpiMonthListResponse.setContent(content);
        kpiMonthListResponse.setPageNumber(kpiMonthPage.getNumber());
        kpiMonthListResponse.setTotalElement(kpiMonthPage.getTotalElements());

        return kpiMonthListResponse;
    }

    public KPIMonthListResponse getAllKPIAllMonth(int page, int size) {
        // Lấy dữ liệu từ repository và phân trang
        Page<KPIMonth> kpiMonthPage = kpiMonthRepository.findByEmployeeRole("Stylist", PageRequest.of(page, size));

        // Chuyển đổi từng KPIMonth thành KPIMonthResponseDTO
        List<KPIMonthResponseDTO> content = new ArrayList<>();
        for (KPIMonth kpiMonth : kpiMonthPage.getContent()) {
            KPIMonthResponseDTO dto = new KPIMonthResponseDTO();
            dto.setId(kpiMonth.getId());
            dto.setKpi(kpiMonth.getKpi());
            dto.setTargetKPI(kpiMonth.getTargetKPI());
            dto.setMonth(kpiMonth.getMonth());
            dto.setEmployeeId(kpiMonth.getEmployee().getEmployeeId());
            dto.setEmployeeName(kpiMonth.getEmployee().getName());
            content.add(dto);
        }

        // Tạo đối tượng phản hồi và thiết lập các thuộc tính
        KPIMonthListResponse kpiMonthListResponse = new KPIMonthListResponse();
        kpiMonthListResponse.setTotalPage(kpiMonthPage.getTotalPages());
        kpiMonthListResponse.setContent(content);
        kpiMonthListResponse.setPageNumber(kpiMonthPage.getNumber());
        kpiMonthListResponse.setTotalElement(kpiMonthPage.getTotalElements());

        return kpiMonthListResponse;
    }


    // UPDATE APPOINTMENT ->  CUSTOMER LÀM
// UPDATE APPOINTMENT
    public AppointmentResponse updateAppointment(AppointmentUpdate appointmentUpdate, long idAppointment){
        boolean StylistChange = false;

        Appointment oldAppointment = appointmentRepository
                .findAppointmentByAppointmentId(idAppointment);
        boolean isSystemChose = oldAppointment.isSystemChose();
        AccountForCustomer accountForCustomer = oldAppointment.getAccountForCustomer();
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
            oldRequest.setDiscountCode(oldCode.getDiscountCodeId());
        }

        oldRequest.setStartHour(oldAppointment.getSlot().getStartSlot());
        oldRequest.setStylistId(oldAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getEmployeeId());
        oldRequest.setServiceIdList(oldServiceIdList);


        // TẠO APPOINTMENT REQUEST MỚI
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
            StylistChange = true;
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
        deleteAppointmentByCustomer(oldAppointment.getAppointmentId());
        return createNewAppointmentByStaffWithCustomerAcc(appointmentRequest, accountForCustomer, StylistChange, isSystemChose);
    }
    //HỆ THỐNG TỰ TÌM STYLIST PHÙ HỢP VÀ ĐẶT LỊCH LUÔN CHO KHÁCH - LÀM BỞI CUSTOMER
    public  AppointmentResponse createNewAppointmentBySystem(AppointmentRequestSystem appointmentRequestSystem){
        List<Slot> slotList = slotRepository
                .findSlotsByDateAndStartSlotAndIsAvailableTrue(
                        appointmentRequestSystem.getDate(),
                        appointmentRequestSystem.getStartHour()
                );
        if(!slotList.isEmpty()){
            List<AccountForEmployee> accountForEmployeeList = new ArrayList<>();
            for(Slot slot : slotList){
                AccountForEmployee account = slot.getShiftEmployee().getAccountForEmployee();
                accountForEmployeeList.add(account);
            }

            AccountForEmployee accountForEmployee = getStylistWithLeastKPI(accountForEmployeeList);


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

                appointment.setSystemChose(true);

                // SLOT
                //Slot slot = slotRepository.findSlotByIdAndIsAvailableTrue(appointmentRequest.getSlotId());
                Slot slot = slotRepository
                        .findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_EmployeeIdAndIsAvailableTrue(
                                appointmentRequestSystem.getStartHour(),
                                appointmentRequestSystem.getDate(),
                                accountForEmployee.getEmployeeId()
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
                accountForCustomerRepository.save(accountForCustomer);

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

                appointmentResponse.setId(newAppointment.getAppointmentId());
                appointmentResponse.setCost(newAppointment.getCost());
                appointmentResponse.setDay(newAppointment.getSlot().getDate());
                appointmentResponse.setStartHour(newAppointment.getSlot().getStartSlot());
                appointmentResponse.setCustomer(accountForCustomer.getName());
                appointmentResponse.setService(serviceNameList);
                appointmentResponse.setStylist(newAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getName());

                EmailDetailCreateAppointment emailDetail = new EmailDetailCreateAppointment();
                emailDetail.setReceiver(appointment.getAccountForCustomer());
                emailDetail.setSubject("You have scheduled an appointment at our salon!");
                emailDetail.setAppointmentId(appointmentResponse.getId());
                emailDetail.setServiceName(appointmentResponse.getService());
                emailDetail.setNameStylist(appointmentResponse.getStylist());
                emailDetail.setDay(appointmentResponse.getDay());
                emailDetail.setStartHour(appointmentResponse.getStartHour());
                emailService.sendEmailCreateAppointment(emailDetail);


                return appointmentResponse;
            } catch (Exception e) {
                throw new EntityNotFoundException("Can not create appointment: " + e.getMessage());
            }
            //-----------------------------------------------------------------------------------------------
        } else {
            throw new EntityNotFoundException("Can not find slot!");
        }
    }


    //STAFF ĐẶT LỊCH HẸN CHO GUEST
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
                    .findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_EmployeeIdAndIsAvailableTrue(
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
            if (accountForEmployee.getStylistSelectionFee() != 0) {
                bonusEmployee += (accountForEmployee.getStylistSelectionFee());
            }

            double totalCost = serviceFee + (bonusEmployee);
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

            appointmentResponse.setId(newAppointment.getAppointmentId());
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
            List<AccountForEmployee> accountForEmployeeList = new ArrayList<>();
            for(Slot slot : slotList){
                AccountForEmployee account = slot.getShiftEmployee().getAccountForEmployee();
                accountForEmployeeList.add(account);
            }

            AccountForEmployee accountForEmployee = getStylistWithLeastKPI(accountForEmployeeList);

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

                appointment.setSystemChose(true);

                // SLOT
                //Slot slot = slotRepository.findSlotByIdAndIsAvailableTrue(appointmentRequest.getSlotId());
                Slot slot = slotRepository
                        .findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_EmployeeIdAndIsAvailableTrue(
                                appointmentRequestSystem.getStartHour(),
                                appointmentRequestSystem.getDate(),
                                accountForEmployee.getEmployeeId()
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

                appointmentResponse.setId(newAppointment.getAppointmentId());
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



    //HÀM TÌM KIẾM THÔNG TIN APPOINTMENT -> STAFF LÀM
    public List<AppointmentResponseInfo> getAppointmentBySĐT(String phoneNumber, String date){
        List<Appointment> appointmentList = appointmentRepository
                .findAppointmentsByDateAndAccountForCustomer_PhoneNumberAndIsDeletedFalse(date, phoneNumber);
        if(appointmentList.isEmpty()){
            throw new EntityNotFoundException("Appointment not found!");
        }
        List<AppointmentResponseInfo> appointmentResponseList = new ArrayList<>();
        for(Appointment appointment : appointmentList) {
            AppointmentResponseInfo appointmentResponse = new AppointmentResponseInfo();

            appointmentResponse.setId(appointment.getAppointmentId());
            appointmentResponse.setCost(appointment.getCost());
            appointmentResponse.setDate(appointment.getDate());
            appointmentResponse.setStartHour(appointment.getStartHour());
            appointmentResponse.setCustomer(appointment.getAccountForCustomer().getName());
            appointmentResponse.setDeleted(appointment.isDeleted());
            appointmentResponse.setCompleted(appointment.isCompleted());

            List<String> serviceNameList = new ArrayList<>();
            List<HairSalonService> hairSalonServiceList = appointment.getHairSalonServices();
            for (HairSalonService service : hairSalonServiceList) {
                String serviceName = service.getName();
                serviceNameList.add(serviceName);
            }
            appointmentResponse.setService(serviceNameList);
            appointmentResponse.setStylist(appointment.getStylist());

            appointmentResponseList.add(appointmentResponse);
        }
        return appointmentResponseList;
    }

    //HÀM LẤY TOÀN BỘ APPOINTMENT CHƯA HOÀN THÀNH TRONG NGÀY -> STAFF LÀM
    public AppointmentResponsePage getAllUnCompletedAppontmentsInDay(String date, String hour, int page, int size){
        Page<Appointment> appointmentPage = appointmentRepository
                .findAppointmentsByDateAndStartHourAndIsCompletedFalseAndIsDeletedFalse(date, hour, PageRequest.of(page, size));
        if(appointmentPage.getContent().isEmpty()){
            throw new EntityNotFoundException("Appointment not found!");
        }
        List<AppointmentResponseInfo> appointmentResponseInfoList = new ArrayList<>();
        for(Appointment appointment : appointmentPage.getContent()){
            AppointmentResponseInfo appointmentResponse = new AppointmentResponseInfo();

            appointmentResponse.setId(appointment.getAppointmentId());
            appointmentResponse.setCost(appointment.getCost());
            appointmentResponse.setDate(appointment.getDate());
            appointmentResponse.setStartHour(appointment.getStartHour());

            if(appointment.getAccountForCustomer() == null){
                appointmentResponse.setCustomer("Guest");
            } else {
                appointmentResponse.setCustomer(appointment.getAccountForCustomer().getName());
            }

            appointmentResponse.setDeleted(appointment.isDeleted());
            appointmentResponse.setCompleted(appointment.isCompleted());

            List<String> serviceNameList = new ArrayList<>();
            List<HairSalonService> hairSalonServiceList = appointment.getHairSalonServices();
            for(HairSalonService service : hairSalonServiceList) {
                String serviceName = service.getName();
                serviceNameList.add(serviceName);
            }
            appointmentResponse.setService(serviceNameList);
            appointmentResponse.setStylist(appointment.getSlot().getShiftEmployee().getAccountForEmployee().getEmployeeId());

            appointmentResponseInfoList.add(appointmentResponse);
        }

        //TẠO RESPONSE
        AppointmentResponsePage appointmentResponsePage = new AppointmentResponsePage();
        appointmentResponsePage.setContent(appointmentResponseInfoList);
        appointmentResponsePage.setPageNumber(appointmentPage.getNumber());
        appointmentResponsePage.setTotalElements(appointmentPage.getTotalElements());
        appointmentResponsePage.setTotalPages(appointmentPage.getTotalPages());

        return appointmentResponsePage;
    }

    //HÀM LẤY TOÀN BỘ APPOINTMENT CHƯA HOÀN THÀNH -> STAFF LÀM
    public AppointmentResponsePage getAllUnCompletedAppontments(String date, int page, int size){
        Page<Appointment> appointmentPage = appointmentRepository
                .findAppointmentsByDateAndIsCompletedFalseAndIsDeletedFalse(date, PageRequest.of(page, size));
        if(appointmentPage.getContent().isEmpty()){
            throw new EntityNotFoundException("Appointment not found!");
        }
        List<AppointmentResponseInfo> appointmentResponseInfoList = new ArrayList<>();
        for(Appointment appointment : appointmentPage.getContent()){
            AppointmentResponseInfo appointmentResponse = new AppointmentResponseInfo();

            appointmentResponse.setId(appointment.getAppointmentId());
            appointmentResponse.setCost(appointment.getCost());
            appointmentResponse.setDate(appointment.getDate());
            appointmentResponse.setStartHour(appointment.getStartHour());

            if(appointment.getAccountForCustomer() == null){
                appointmentResponse.setCustomer("Guest");
            } else {
                appointmentResponse.setCustomer(appointment.getAccountForCustomer().getName());
            }

            appointmentResponse.setDeleted(appointment.isDeleted());
            appointmentResponse.setCompleted(appointment.isCompleted());

            List<String> serviceNameList = new ArrayList<>();
            List<HairSalonService> hairSalonServiceList = appointment.getHairSalonServices();
            for(HairSalonService service : hairSalonServiceList) {
                String serviceName = service.getName();
                serviceNameList.add(serviceName);
            }
            appointmentResponse.setService(serviceNameList);
            appointmentResponse.setStylist(appointment.getSlot().getShiftEmployee().getAccountForEmployee().getEmployeeId());

            appointmentResponseInfoList.add(appointmentResponse);
        }

        //TẠO RESPONSE
        AppointmentResponsePage appointmentResponsePage = new AppointmentResponsePage();
        appointmentResponsePage.setContent(appointmentResponseInfoList);
        appointmentResponsePage.setPageNumber(appointmentPage.getNumber());
        appointmentResponsePage.setTotalElements(appointmentPage.getTotalElements());
        appointmentResponsePage.setTotalPages(appointmentPage.getTotalPages());

        return appointmentResponsePage;
    }


    //XEM CHI TIẾT LỊCH HẸN
    public AppointmentDetail getAppontmentDetail(long appointmentId){
        Appointment appointment = appointmentRepository.findAppointmentByAppointmentId(appointmentId);
        AppointmentDetail appointmentDetail = new AppointmentDetail();
        appointmentDetail.setId(appointmentId);
        AccountForEmployee accountForEmployee = appointment.getSlot().getShiftEmployee().getAccountForEmployee();
        appointmentDetail.setStylist(modelMapper.map(accountForEmployee, EmployeeInfo.class));
        appointmentDetail.setCustomer("Guest");
        if(appointment.getAccountForCustomer() != null){
            appointmentDetail.setCustomer(appointment.getAccountForCustomer().getName());
        }
        appointmentDetail.setCompleted(appointment.isCompleted());
        appointmentDetail.setTotalCost(appointment.getCost());
        appointmentDetail.setDate(appointment.getDate());
        appointmentDetail.setStartHour(appointment.getStartHour());

        appointmentDetail.setStatus(appointment.getStatus());
        String discountCode = null;
        if(appointment.getDiscountCode() != null){
            discountCode = appointment.getDiscountCode().getDiscountCodeId() + "(" + appointment.getDiscountCode().getDiscountProgram().getPercentage() + "%" + ")";
        }

        appointmentDetail.setDiscountCode(discountCode);

        List<HairSalonServiceResponse> hairSalonServiceResponseList = new ArrayList<>();
        for(HairSalonService service : appointment.getHairSalonServices()){
            hairSalonServiceResponseList.add(modelMapper.map(service, HairSalonServiceResponse.class));
        }
        appointmentDetail.setService(hairSalonServiceResponseList);
        appointmentDetail.setSystemChose(appointment.isSystemChose());
        appointmentDetail.setStylistFee(appointment.getSlot().getShiftEmployee().getAccountForEmployee().getStylistSelectionFee());
        if(appointment.isSystemChose()){
            appointmentDetail.setStylistFee(0);
        }
        return appointmentDetail;
    }


    //STAFF ACCEPT APPOINTMENT
    public AppointmentDetail acceptAppointment(long appointmentId){
        Appointment appointment = appointmentRepository.findAppointmentByAppointmentId(appointmentId);
        appointment.setStatus("Đang phục vụ");
        Appointment newAppointment = appointmentRepository.save(appointment);
        AppointmentDetail appointmentDetail = new AppointmentDetail();
        appointmentDetail.setId(appointmentId);
        AccountForEmployee accountForEmployee = newAppointment.getSlot().getShiftEmployee().getAccountForEmployee();
        appointmentDetail.setStylist(modelMapper.map(accountForEmployee, EmployeeInfo.class));
        appointmentDetail.setCustomer("Guest");
        if(appointment.getAccountForCustomer() != null){
            appointmentDetail.setCustomer(appointment.getAccountForCustomer().getName());
        }
        appointmentDetail.setCompleted(newAppointment.isCompleted());
        appointmentDetail.setTotalCost(newAppointment.getCost());
        appointmentDetail.setDate(newAppointment.getDate());
        appointmentDetail.setStartHour(newAppointment.getStartHour());

        appointmentDetail.setStatus(newAppointment.getStatus());
        String discountCode = null;
        if(newAppointment.getDiscountCode() != null){
            discountCode = newAppointment.getDiscountCode().getDiscountCodeId() + "(" + newAppointment.getDiscountCode().getDiscountProgram().getPercentage() + "%" + ")";
        }

        appointmentDetail.setDiscountCode(discountCode);

        List<HairSalonServiceResponse> hairSalonServiceResponseList = new ArrayList<>();
        for(HairSalonService service : newAppointment.getHairSalonServices()){
            hairSalonServiceResponseList.add(modelMapper.map(service, HairSalonServiceResponse.class));
        }
        appointmentDetail.setService(hairSalonServiceResponseList);
        appointmentDetail.setSystemChose(newAppointment.isSystemChose());
        appointmentDetail.setStylistFee(newAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getStylistSelectionFee());
        if(newAppointment.isSystemChose()){
            appointmentDetail.setStylistFee(0);
        }
        return appointmentDetail;
    }

    //HÀM CHỌN STYLIST CÓ SỐ KPI ÍT NHẤT HIỆN TẠI
    public AccountForEmployee getStylistWithLeastKPI(List<AccountForEmployee> accountForEmployeeList){
        List<Integer> allKPI = new ArrayList<>();
        for(AccountForEmployee account : accountForEmployeeList){
            allKPI.add(account.getKPI());
        }
        int min = Collections.min(allKPI);
        for(AccountForEmployee account : accountForEmployeeList){
            if(account.getKPI() == min){
                return account;
            }
        }
        return null;
    }

    //HỖ TRỢ HÀM UPDATE
    public AppointmentResponse createNewAppointmentByStaffWithCustomerAcc(AppointmentRequest appointmentRequest, AccountForCustomer accountForCustomer, boolean Stylistchange, boolean isSystemChose) {
        try {
            List<String> serviceNameList = new ArrayList<>();  //TẠO LIST CHỨA TÊN CÁC DỊCH VỤ CUSTOMER CHỌN
            List<Long> serviceIdList = appointmentRequest.getServiceIdList();  // LẤY DANH SÁCH ID DỊCH VỤ CUSTOMER CHỌN
            List<HairSalonService> hairSalonServiceList = new ArrayList<>();   // TẠO LIST CHỨA OBJ DỊCH VỤ
            double bonusDiscountCode = 0;
            double bonusEmployee = 0;
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
                    .findSlotByStartSlotAndDateAndShiftEmployee_AccountForEmployee_EmployeeIdAndIsAvailableTrue(
                            appointmentRequest.getStartHour(),
                            appointmentRequest.getDate(),
                            appointmentRequest.getStylistId()
                    );
            appointment.setSlot(slot);  // TÌM SLOT DỰA TRÊN THÔNG TIN REQUEST VÀ GÁN VÀO APPOINTMENT

            //ACCOUNT FOR CUSTOMER
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
            if(Stylistchange){
                appointment.setSystemChose(false);
                if (accountForEmployee.getStylistSelectionFee() != 0) {
                    bonusEmployee += (accountForEmployee.getStylistSelectionFee());
                }

            } else {  // NẾU KO CHỌN STYLIST MỚI -> CÓ 2 TH:
                // 1. LỊCH CŨ DO HỆ THỐNG CHỌN STYLIST
                // 2. LỊCH CŨ DO NGƯỜI DÙNG CHỌN
                appointment.setSystemChose(isSystemChose);
                if(!appointment.isSystemChose()){
                    bonusEmployee += (accountForEmployee.getStylistSelectionFee());
                }
            }



            double totalCost = serviceFee - (bonusDiscountCode * serviceFee) + (bonusEmployee);
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
            accountForCustomerRepository.save(accountForCustomer);

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

            appointmentResponse.setId(newAppointment.getAppointmentId());
            appointmentResponse.setCost(newAppointment.getCost());
            appointmentResponse.setDay(newAppointment.getSlot().getDate());
            appointmentResponse.setStartHour(newAppointment.getSlot().getStartSlot());
            appointmentResponse.setCustomer(accountForCustomer.getName());
            appointmentResponse.setService(serviceNameList);
            appointmentResponse.setStylist(newAppointment.getSlot().getShiftEmployee().getAccountForEmployee().getName());

            return appointmentResponse;
        } catch (Exception e) {
            throw new EntityNotFoundException("Can not create appointment: " + e.getMessage());
        }
    }

    // UPDATE DANH SACH DICH VU
    public AppointmentResponse updateServiceList(AppointmentUpdate appointmentUpdate, long idAppointment){
        Appointment oldAppointment = appointmentRepository
                .findAppointmentByAppointmentId(idAppointment);
        AccountForCustomer accountForCustomer = oldAppointment.getAccountForCustomer();
        // LẤY THÔNG TIN CŨ
        double total = oldAppointment.getCost();
        DiscountCode oldCode = oldAppointment.getDiscountCode();
        double discount = 0;
        if(oldCode != null){
            discount = (oldCode.getDiscountProgram().getPercentage())/100;
        }

        AccountForEmployee stylist = oldAppointment.getSlot().getShiftEmployee().getAccountForEmployee();
        double stylistFee = stylist.getStylistSelectionFee();
        //LẤY LẠI LIST CŨ
        double oldServicesCost = 0;
        List<HairSalonService> oldHairSalonServiceList = oldAppointment.getHairSalonServices();
        for(HairSalonService service : oldHairSalonServiceList){
            oldServicesCost += service.getCost();
        }
        //double totalWithoutServices = total - servicesCost;
        // GENERATE LIST MỚI TỪ INPUT
        List<HairSalonService> newHairSalonServiceList = new ArrayList<>();

        for(long id : appointmentUpdate.getServiceIdList()){
            HairSalonService hairSalonService = serviceRepository.findHairSalonServiceById(id);
            newHairSalonServiceList.add(hairSalonService);
        }

        if(!appointmentUpdate.getServiceIdList().isEmpty() && !newHairSalonServiceList.equals(oldHairSalonServiceList)){
            oldAppointment.setHairSalonServices(newHairSalonServiceList);
            double newServiceCost = 0;
            for(HairSalonService service : newHairSalonServiceList){
                newServiceCost += service.getCost();
            }
            if(oldAppointment.isSystemChose()){
                stylistFee = 0;
            }
            double newTotal = newServiceCost + stylistFee - newServiceCost * discount;
            oldAppointment.setCost(newTotal);
        }

        List<String> serviceNameList = new ArrayList<>();
        for(HairSalonService hairSalonService : oldAppointment.getHairSalonServices()){
            String name = hairSalonService.getName();
            serviceNameList.add(name);
        }

        Appointment appointment = appointmentRepository.save(oldAppointment);

        AppointmentResponse appointmentResponse = new AppointmentResponse();

        appointmentResponse.setId(appointment.getAppointmentId());
        appointmentResponse.setCost(appointment.getCost());
        appointmentResponse.setDay(appointment.getSlot().getDate());
        appointmentResponse.setStartHour(appointment.getSlot().getStartSlot());
        if(accountForCustomer != null){
            appointmentResponse.setCustomer(accountForCustomer.getName());
        } else {
            appointmentResponse.setCustomer("Guest");
        }

        appointmentResponse.setService(serviceNameList);
        appointmentResponse.setStylist(appointment.getSlot().getShiftEmployee().getAccountForEmployee().getName());

        return appointmentResponse;
    }

}
