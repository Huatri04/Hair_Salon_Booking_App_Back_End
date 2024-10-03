package com.hairsalonbookingapp.hairsalon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlotRequest {
    @NotBlank(message = "Invalid id!")
    @Size(min = 1, message = "Invalid id!")
    long shiftEmployeeId;

    @NotBlank(message = "Invalid hour!")
    @Size(min = 6, message = "Invalid hour!")
    int startHour;

    @NotBlank(message = "Invalid hour!")
    @Size(min = 7, message = "Invalid hour!")
    int endHour;

    @NotBlank(message = "Invalid duration!")
    @Size(min = 10, message = "Invalid duration!")
    long duration;
}
