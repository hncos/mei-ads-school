package pt.iscte.mei.school.appointments.application.dto;


import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "from")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@ApiModel(description = "Information's to register a new appointment")
public class RegisterAppointmentDTO {

    @NotNull(message = "{RegisterAppointmentDTO.startDate.NotNull}")
    private LocalDateTime startDate;

    @NotNull(message = "{RegisterAppointmentDTO.endDate.NotNull}")
    private LocalDateTime endDate;

    @NotBlank(message = "{RegisterAppointmentDTO.classroomId.NotBlank}")
    private String classroomId;

    @NotBlank(message = "{RegisterAppointmentDTO.courseId.NotBlank}")
    private String courseId;

    @NotNull(message = "{RegisterAppointmentDTO.capacity.NotNull}")
    private int capacity;
}
