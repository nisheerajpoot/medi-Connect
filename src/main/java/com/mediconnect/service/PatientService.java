package com.mediconnect.service;

import java.util.List;

import com.mediconnect.dto.request.PatientRequestDTO;
import com.mediconnect.dto.request.UpdatePatientRequestDTO;
import com.mediconnect.dto.response.PatientResponseDTO;

public interface PatientService {
	
	PatientResponseDTO createPatient(PatientRequestDTO requestDTO);
	PatientResponseDTO getPatientById(Long id);
	List<PatientResponseDTO> getAllPatients();
	PatientResponseDTO updatePatient(Long id, UpdatePatientRequestDTO requestDTO);
	void deletePatient(Long id);
}
