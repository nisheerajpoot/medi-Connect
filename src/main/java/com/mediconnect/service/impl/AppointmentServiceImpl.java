package com.mediconnect.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mediconnect.dto.request.AppointmentRequestDTO;
import com.mediconnect.dto.request.UpdateAppointmentStatusRequestDTO;
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
	public AppointmentResponseDTO updateAppointmentStatus(Long id, UpdateAppointmentStatusRequestDTO requestDTO) {

	    // Step 1: Appointment dhoondo, nahi mili to exception
	    Appointment appointment = appointmentRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

	    // Step 2: Agar CONFIRM karna hai, to time dena zaroori hai
	    if (requestDTO.getStatus() == AppointmentStatus.CONFIRMED) {

	        if (requestDTO.getStartTime() == null || requestDTO.getEndTime() == null) {
	            throw new InvalidOperationException("startTime and endTime are required to confirm appointment");
	        }

	        // Step 3: Double-booking check
	        // Isi doctor ki, isi date ki saari appointments nikaalo
	        List<Appointment> doctorAppointments = appointmentRepository
	                .findByDoctorIdAndAppointmentDate(appointment.getDoctor().getId(), appointment.getAppointmentDate());

	        for (Appointment existing : doctorAppointments) {

	            // khud ki appointment ko skip karo (update kar rahe ho usi ko)
	            if (existing.getId().equals(appointment.getId())) {
	                continue;
	            }

	            // sirf CONFIRMED appointments se hi clash check karna hai
	            if (existing.getStatus() == AppointmentStatus.CONFIRMED) {

	                boolean isOverlapping = requestDTO.getStartTime().isBefore(existing.getEndTime())
	                        && requestDTO.getEndTime().isAfter(existing.getStartTime());

	                if (isOverlapping) {
	                    throw new InvalidOperationException(
	                            "Doctor already has a confirmed appointment in this time slot");
	                }
	            }
	        }

	        // Step 4: Sab sahi hai to time set karo
	        appointment.setStartTime(requestDTO.getStartTime());
	        appointment.setEndTime(requestDTO.getEndTime());
	    }

	    // Step 5: Status update karo (CONFIRMED ho ya REJECTED, dono case me chalega)
	    appointment.setStatus(requestDTO.getStatus());

	    // Step 6: Save karo aur response bhejo
	    Appointment updatedAppointment = appointmentRepository.save(appointment);
	    return mapToResponseDTO(updatedAppointment);
	}

	@Override
	public void deleteAppointment(Long id) {
		Appointment appointment =appointmentRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Appointment","id",id));
		appointmentRepository.delete(appointment);
		
	}
	private AppointmentResponseDTO mapToResponseDTO(Appointment appointment) {

	    // Pehle Hospital ko convert karo (Doctor ke andar chahiye)
	    HospitalResponseDTO hospitalDTO = HospitalResponseDTO.builder()
	            .id(appointment.getDoctor().getHospital().getId())
	            .name(appointment.getDoctor().getHospital().getName())
	            .address(appointment.getDoctor().getHospital().getAddress())
	            .phoneNumber(appointment.getDoctor().getHospital().getPhoneNumber())
	            .openingTime(appointment.getDoctor().getHospital().getOpeningTime())
	            .closingTime(appointment.getDoctor().getHospital().getClosingTime())
	            .build();

	    // Phir Doctor ko convert karo (Hospital nested karke)
	    DoctorResponseDTO doctorDTO = DoctorResponseDTO.builder()
	            .id(appointment.getDoctor().getId())
	            .name(appointment.getDoctor().getName())
	            .specialization(appointment.getDoctor().getSpecialization())
	            .experience(appointment.getDoctor().getExperience())
	            .phoneNumber(appointment.getDoctor().getPhoneNumber())
	            .hospital(hospitalDTO)
	            .build();

	    // Phir Patient ko convert karo
	    PatientResponseDTO patientDTO = PatientResponseDTO.builder()
	            .id(appointment.getPatient().getId())
	            .name(appointment.getPatient().getName())
	            .age(appointment.getPatient().getAge())
	            .phoneNumber(appointment.getPatient().getPhoneNumber())
	            .address(appointment.getPatient().getAddress())
	            .build();

	    // Aakhir me poora Appointment response banao
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
