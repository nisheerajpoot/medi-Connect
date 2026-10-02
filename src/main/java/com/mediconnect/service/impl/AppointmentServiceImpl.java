package com.mediconnect.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mediconnect.dto.request.AppointmentRequestDTO;
import com.mediconnect.dto.request.UpdateAppointmentRequestDTO;
import com.mediconnect.dto.response.AppointmentResponseDTO;
import com.mediconnect.dto.response.DoctorResponseDTO;
import com.mediconnect.dto.response.HospitalResponseDTO;
import com.mediconnect.dto.response.PatientResponseDTO;
import com.mediconnect.entity.Appointment;
import com.mediconnect.entity.AppointmentStatus;
import com.mediconnect.entity.Doctor;
import com.mediconnect.entity.Hospital;
import com.mediconnect.entity.Patient;
import com.mediconnect.exception.InvalidOperationException;
import com.mediconnect.exception.ResourceNotFoundException;
import com.mediconnect.repository.AppointmentRepository;
import com.mediconnect.repository.DoctorRepository;
import com.mediconnect.repository.HospitalRepository;
import com.mediconnect.repository.PatientRepository;
import com.mediconnect.service.AppointmentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional

public class AppointmentServiceImpl implements AppointmentService {
	
	private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;

	@Override
	public AppointmentResponseDTO createAppointment(AppointmentRequestDTO requestDTO) {
		
		Patient patient=patientRepository.findById(requestDTO.getPatientId())
				.orElseThrow(()-> new ResourceNotFoundException("Patient","id",requestDTO.getPatientId()));
		
		Doctor doctor=doctorRepository.findById(requestDTO.getDoctorId())
				.orElseThrow(()-> new ResourceNotFoundException("Doctor","id",requestDTO.getDoctorId()));
		
		Hospital hospital =hospitalRepository.findById(requestDTO.getHospitalId())
				.orElseThrow(()-> new ResourceNotFoundException("Hospital","id",requestDTO.getHospitalId()));
		
		if (!doctor.getHospital().getId().equals(hospital.getId())) {
		    throw new InvalidOperationException("Doctor does not belong to the selected hospital");
		}
		
		Appointment appointment=Appointment.builder()
				.patient(patient)
				.doctor(doctor)
				.hospital(hospital)
				.appointmentDate(requestDTO.getAppointmentDate())
				.healthIssue(requestDTO.getHealthIssue())
				.status(AppointmentStatus.PENDING)
				.build();
		
		Appointment saveAppointment=appointmentRepository.save(appointment);
		
		
		return mapToResponseDTO(saveAppointment);
	}

	@Override
	public AppointmentResponseDTO getAppointmentById(Long id) {
		Appointment appointment = appointmentRepository.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
		return mapToResponseDTO(appointment);
	}

	@Override
	public List<AppointmentResponseDTO> getAllAppointments() {
		 List<Appointment> appointments =appointmentRepository.findAll();
	        List<AppointmentResponseDTO> responseList = new ArrayList<>();
	        for (Appointment appointment : appointments) {
	            responseList.add(mapToResponseDTO(appointment));
	        }
	        return responseList;
	}

	@Override
	public List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId) {
		 patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        List<AppointmentResponseDTO> responseList = new ArrayList<>();
        for (Appointment appointment : appointments) {
            responseList.add(mapToResponseDTO(appointment));
        }
        return responseList;
	}

	@Override
	public List<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId) {
		doctorRepository.findById(doctorId)
		                .orElseThrow(()-> new ResourceNotFoundException("Doctor","id",doctorId));
		List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        List<AppointmentResponseDTO> responseList = new ArrayList<>();
        for (Appointment appointment : appointments) {
            responseList.add(mapToResponseDTO(appointment));
        }
        return responseList;
	}
	
	@Override
	public AppointmentResponseDTO updateAppointment(Long id, UpdateAppointmentRequestDTO requestDTO) {
	    Appointment appointment = appointmentRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

	    if (appointment.getStatus() != AppointmentStatus.PENDING) {
	        throw new InvalidOperationException("Only PENDING appointments can be updated");
	    }
	    if (requestDTO.getDoctorId() == null
	            && requestDTO.getAppointmentDate() == null
	            && requestDTO.getHealthIssue() == null) {
	        throw new IllegalArgumentException("At least one field must be provided for update");
	    }
	    if (requestDTO.getDoctorId() != null) {
	        Doctor doctor = doctorRepository.findById(requestDTO.getDoctorId())
	                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", requestDTO.getDoctorId()));
	        if (!doctor.getHospital().getId().equals(appointment.getHospital().getId())) {
	            throw new InvalidOperationException("Doctor does not belong to this appointment's hospital");
	        }
	        appointment.setDoctor(doctor);
	    }
	    if (requestDTO.getAppointmentDate() != null) {
	        appointment.setAppointmentDate(requestDTO.getAppointmentDate());
	    }
	    if (requestDTO.getHealthIssue() != null) {
	        if (requestDTO.getHealthIssue().isBlank()) {
	            throw new IllegalArgumentException("Health issue cannot be blank");
	        }
	        appointment.setHealthIssue(requestDTO.getHealthIssue().trim());
	    }
	    return mapToResponseDTO(appointmentRepository.save(appointment));
	}


	@Override
	public void deleteAppointment(Long id) {
		Appointment appointment =appointmentRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Appointment","id",id));
		appointmentRepository.delete(appointment);
		
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
