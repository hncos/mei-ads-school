package pt.iscte.mei.school.files.excel.application;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.iscte.mei.school.appointments.application.AppointmentApplicationService;
import pt.iscte.mei.school.appointments.application.dto.ReadXLSXFileAppointmentDTO;
import pt.iscte.mei.school.appointments.model.Appointment;
import pt.iscte.mei.school.classrooms.application.ClassroomApplicationService;
import pt.iscte.mei.school.classrooms.model.Classroom;
import pt.iscte.mei.school.classrooms.repository.ClassroomRepository;
import pt.iscte.mei.school.courses.application.CourseApplicationService;
import pt.iscte.mei.school.courses.model.Course;
import pt.iscte.mei.school.courses.repository.CourseRepository;
import pt.iscte.mei.school.curricularunits.application.CurricularUnitApplicationService;
import pt.iscte.mei.school.curricularunits.model.CurricularUnit;
import pt.iscte.mei.school.curricularunits.repository.CurricularUnitRepository;
import pt.iscte.mei.school.features.application.FeatureApplicationService;
import pt.iscte.mei.school.features.model.Feature;
import pt.iscte.mei.school.features.repository.FeatureRepository;
import pt.iscte.mei.school.files.excel.helper.ExcelHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
@Log4j2
public class ExcelApplicationService {


    private AppointmentApplicationService appointmentService;
    private CourseRepository courseRepository;
    private ClassroomRepository classroomRepository;
    private CurricularUnitRepository curricularUnitRepository;
    private FeatureRepository featureRepository;

    @Autowired
    public ExcelApplicationService(AppointmentApplicationService appointmentService, CourseRepository courseRepository,
                                   ClassroomRepository classroomRepository, CurricularUnitRepository curricularUnitRepository,
                                   FeatureRepository featureRepository) {
        this.appointmentService = appointmentService;
        this.courseRepository = courseRepository;
        this.classroomRepository = classroomRepository;
        this.curricularUnitRepository = curricularUnitRepository;
        this.featureRepository = featureRepository;
    }

    public void save(MultipartFile file) {
        try {
            List<ReadXLSXFileAppointmentDTO> dto = ExcelHelper.excelToAppointments(file.getInputStream());

            dto.forEach(o -> {
                CurricularUnit curricularUnit = curricularUnitRepository.save(o.getCurricularUnit());
                Feature feature = featureRepository.save(o.getFeature());

                Classroom classroomBuilder = Classroom.builder()
                        .name(o.getClassroom().getName())
                        .caracteristics(List.of(feature.getName()))
                        .capacity(o.getClassroom().getCapacity())
                        .build();

                Classroom classroom = classroomRepository.save(classroomBuilder);
                Course course = courseRepository.save(o.getCourse());

                Appointment appointment = o.getAppointment();
                appointment.setCourse(course.getId());
                appointment.setClassroom(classroom.getId());
                appointment.setCurricularUnit(curricularUnit.getId());
                appointmentService.register(appointment);
            });
            //TODO: call all repositories to save the models
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream load() {
        List<Appointment> appointments = appointmentService.searchAll();

        ByteArrayInputStream in = ExcelHelper.appointmentsToExcel(appointments);
        return in;
    }

}
