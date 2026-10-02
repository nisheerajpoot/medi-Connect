package com.mediconnect.service;

import com.mediconnect.dto.request.LoginRequest;
import com.mediconnect.dto.request.RegisterDoctorRequest;
import com.mediconnect.dto.request.RegisterHospitalRequest;
import com.mediconnect.dto.request.RegisterPatientRequest;
import com.mediconnect.dto.response.AuthResponseDTO;

public interface AuthService {
	AuthResponseDTO registerPatient(RegisterPatientRequest request);

	AuthResponseDTO registerDoctor(RegisterDoctorRequest request);

	AuthResponseDTO registerHospital(RegisterHospitalRequest request);

	AuthResponseDTO login(LoginRequest request);
}
