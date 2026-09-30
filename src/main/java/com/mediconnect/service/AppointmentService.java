package com.mediconnect.service;

import java.util.List;

import com.mediconnect.dto.request.AppointmentRequestDTO;
import com.mediconnect.dto.request.UpdateAppointmentStatusRequestDTO;
import com.mediconnect.dto.response.AppointmentResponseDTO;

public interface AppointmentService {
	
	AppointmentResponseDTO createAppointment(AppointmentRequestDTO requestDTO);
	AppointmentResponseDTO getAppointmentById(Long id);
	List<AppointmentResponseDTO> getAllAppointments();
	List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId);
	List<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId);
	void deleteAppointment (Long id);
	
}
