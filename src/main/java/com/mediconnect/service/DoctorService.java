package com.mediconnect.service;

import java.util.List;

import com.mediconnect.dto.request.DoctorRequestDTO;
import com.mediconnect.dto.request.UpdateDoctorRequestDTO;
import com.mediconnect.dto.response.DoctorResponseDTO;


public interface DoctorService {
	DoctorResponseDTO createDoctor(DoctorRequestDTO requestDTO);
	DoctorResponseDTO getDoctorById(Long id);
	List<DoctorResponseDTO> getAllDoctors();
	DoctorResponseDTO updateDoctor(Long id, UpdateDoctorRequestDTO  requestDTO);
	void deleteDoctor(Long id);
	List<DoctorResponseDTO> getDoctorsByHospital(Long hospitalId);
}
