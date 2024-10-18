package com.blubank.doctorappointment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Builder
public class TimeSlotRequest {

    private LocalDateTime start;

    private LocalDateTime end;

}
