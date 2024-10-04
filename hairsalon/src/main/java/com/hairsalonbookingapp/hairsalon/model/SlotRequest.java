package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlotRequest {
    //@NotBlank(message = "Invalid id!")
    long shiftEmployeeId;

    //@NotBlank(message = "Invalid hour!")
    int startHour;

    //@NotBlank(message = "Invalid hour!")
    int endHour;

    //@NotBlank(message = "Invalid duration!")
    long duration;
}
