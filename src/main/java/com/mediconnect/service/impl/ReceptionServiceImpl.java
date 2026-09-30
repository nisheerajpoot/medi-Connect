package com.mediconnect.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mediconnect.dto.request.UpdateAppointmentStatusRequestDTO;
import com.mediconnect.dto.response.AppointmentResponseDTO;
import com.mediconnect.dto.response.DoctorResponseDTO;
import com.mediconnect.dto.response.HospitalResponseDTO;
import com.mediconnect.dto.response.PatientResponseDTO;
import com.mediconnect.dto.response.ReceptionDashboardResponseDTO;
import com.mediconnect.entity.Appointment;
import com.mediconnect.entity.AppointmentStatus;
import com.mediconnect.entity.Hospital;
import com.mediconnect.exception.InvalidOperationException;
import com.mediconnect.exception.ResourceNotFoundException;
import com.mediconnect.repository.AppointmentRepository;
import com.mediconnect.repository.HospitalRepository;
import com.mediconnect.service.ReceptionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReceptionServiceImpl implements ReceptionService {
	private final AppointmentRepository appointmentRepository;
	private final HospitalRepository hospitalRepository;

	@Override
	public ReceptionDashboardResponseDTO getDashboard(Long hospitalId) {
		Hospital hospital = hospitalRepository.findById(hospitalId)
				.orElseThrow(() -> new ResourceNotFoundException("Hospital", "id", hospitalId));

		long pendind = appointmentRepository.findByHospitalIdAndStatus(hospitalId, AppointmentStatus.PENDING).size();
		long confirmed = appointmentRepository.findByHospitalIdAndStatus(hospitalId, AppointmentStatus.CONFIRMED).size();
		long rejected = appointmentRepository.findByHospitalIdAndStatus(hospitalId, AppointmentStatus.REJECTED).size();

		return ReceptionDashboardResponseDTO.builder()
				.hospitalId(hospital.getId())
				.hospitalName(hospital.getName())
				.pendingCount(pendind)
				.confirmedCount(confirmed)
				.rejectedCount(rejected)
				.build();
	}

	@Override
	public List<AppointmentResponseDTO> getAppointmentsByHospital(Long hospitalId) {
		List<Appointment> appointments=appointmentRepository.findByHospitalId(hospitalId);
		List<AppointmentResponseDTO> responseList=new ArrayList<>();
		for(Appointment appointment:appointments) {
			responseList.add(mapToResponseDTO(appointment));
		}
		return responseList;
	}

	@Override
	public AppointmentResponseDTO getAppointmentDetail(Long hospitalId, Long appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				                  .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
	    if(!appointment.getHospital().getId().equals(hospitalId)) {
	    	throw new InvalidOperationException("This appointment does not belong to this hospital");
	    }
	    
	    return mapToResponseDTO(appointment);
	}

	@Override
	public List<AppointmentResponseDTO> getAppointmentsByHospitalAndPatient(Long hospitalId, Long patientId) {
		List<Appointment> appointments = appointmentRepository.findByHospitalIdAndPatientId(hospitalId, patientId);
		List<AppointmentResponseDTO> responseList = new ArrayList<>();
		for (Appointment appointment : appointments) {
			responseList.add(mapToResponseDTO(appointment));
		}
		return responseList;
	}

	@Override
	public AppointmentResponseDTO updateAppointmentStatus(Long hospitalId, Long appointmentId,
	        UpdateAppointmentStatusRequestDTO requestDTO) {

	    Appointment appointment = appointmentRepository.findById(appointmentId)
	            .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

	    if (requestDTO.getStatus() == AppointmentStatus.CONFIRMED) {
	        if (requestDTO.getStartTime() == null || requestDTO.getEndTime() == null) {
	            throw new IllegalArgumentException("Start time and end time are required to confirm an appointment");
	        }

	        if (!requestDTO.getStartTime().isBefore(requestDTO.getEndTime())) {
	            throw new IllegalArgumentException("Start time must be before end time");
	        }

	        // ⬇️ NAYA CHECK — Hospital ke opening/closing time ke andar hona chahiye
	        Hospital hospital = appointment.getHospital();
	        if (requestDTO.getStartTime().isBefore(hospital.getOpeningTime())
	                || requestDTO.getEndTime().isAfter(hospital.getClosingTime())) {
	            throw new IllegalArgumentException(
	                    "Appointment time must be within hospital hours: "
	                            + hospital.getOpeningTime() + " - " + hospital.getClosingTime());
	        }

	        List<Appointment> doctorAppointments = appointmentRepository
	                .findByDoctorIdAndAppointmentDate(appointment.getDoctor().getId(), appointment.getAppointmentDate());

	        for (Appointment existing : doctorAppointments) {
	            if (existing.getId().equals(appointment.getId())) {
	                continue;
	            }
	            if (existing.getStatus() != AppointmentStatus.CONFIRMED) {
	                continue;
	            }
	            boolean overlap = requestDTO.getStartTime().isBefore(existing.getEndTime())
	                    && existing.getStartTime().isBefore(requestDTO.getEndTime());
	            if (overlap) {
	                throw new InvalidOperationException("Doctor already has an appointment in this time slot");
	            }
	        }

	        appointment.setStartTime(requestDTO.getStartTime());
	        appointment.setEndTime(requestDTO.getEndTime());
	    }

	    appointment.setStatus(requestDTO.getStatus());

	    Appointment updated = appointmentRepository.save(appointment);
	    return mapToResponseDTO(updated);
	}
	private AppointmentResponseDTO mapToResponseDTO(Appointment appointment) {
	
	    HospitalResponseDTO hospitalDTO = HospitalResponseDTO.builder()
	            .id(appointment.getDoctor().getHospital().getId())
	            .name(appointment.getDoctor().getHospital().getName())
	            .address(appointment.getDoctor().getHospital().getAddress())
	            .phoneNumber(appointment.getDoctor().getHospital().getPhoneNumber())
	            .openingTime(appointment.getDoctor().getHospital().getOpeningTime())
	            .closingTime(appointment.getDoctor().getHospital().getClosingTime())
	            .build();

	    DoctorResponseDTO doctorDTO = DoctorResponseDTO.builder()
	            .id(appointment.getDoctor().getId())
	            .name(appointment.getDoctor().getName())
	            .specialization(appointment.getDoctor().getSpecialization())
	            .experience(appointment.getDoctor().getExperience())
	            .phoneNumber(appointment.getDoctor().getPhoneNumber())
	            .hospital(hospitalDTO)
	            .build();

	    PatientResponseDTO patientDTO = PatientResponseDTO.builder()
	            .id(appointment.getPatient().getId())
	            .name(appointment.getPatient().getName())
	            .age(appointment.getPatient().getAge())
	            .phoneNumber(appointment.getPatient().getPhoneNumber())
	            .address(appointment.getPatient().getAddress())
	            .build();

	    return AppointmentResponseDTO.builder()
	            .id(appointment.getId())
	            .patient(patientDTO)
	            .doctor(doctorDTO)
	            .appointmentDate(appointment.getAppointmentDate())
	            .startTime(appointment.getStartTime())
	            .endTime(appointment.getEndTime())
	            .healthIssue(appointment.getHealthIssue())
	            .status(appointment.getStatus())
	            .build();
	}

}
