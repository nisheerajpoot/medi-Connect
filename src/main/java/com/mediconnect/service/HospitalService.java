package com.mediconnect.service;

import java.util.List;

import com.mediconnect.dto.request.HospitalRequestDTO;
import com.mediconnect.dto.request.UpdateHospitalRequestDTO;
import com.mediconnect.dto.response.HospitalResponseDTO;

public interface HospitalService {
	
	HospitalResponseDTO createHospital(HospitalRequestDTO requestDTO);

	HospitalResponseDTO getHospitalById(Long id);

	List<HospitalResponseDTO> getAllHospitals();

	HospitalResponseDTO updateHospital(Long id, UpdateHospitalRequestDTO  requestDTO);

	void deleteHospital(Long id);

	List<HospitalResponseDTO> searchHospitalByName(String name);
}