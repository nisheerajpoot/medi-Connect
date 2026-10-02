package com.mediconnect.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediconnect.dto.request.DoctorRequestDTO;
import com.mediconnect.dto.request.HospitalRequestDTO;
import com.mediconnect.dto.request.LoginRequest;
import com.mediconnect.dto.request.PatientRequestDTO;
import com.mediconnect.dto.request.RegisterDoctorRequest;
import com.mediconnect.dto.request.RegisterHospitalRequest;
import com.mediconnect.dto.request.RegisterPatientRequest;
import com.mediconnect.dto.response.AuthResponseDTO;
import com.mediconnect.dto.response.DoctorResponseDTO;
import com.mediconnect.dto.response.HospitalResponseDTO;
import com.mediconnect.dto.response.PatientResponseDTO;
import com.mediconnect.entity.Role;
import com.mediconnect.entity.User;
import com.mediconnect.exception.DuplicateResourceException;
import com.mediconnect.exception.UnauthorizedException;
import com.mediconnect.repository.UserRepository;
import com.mediconnect.service.AuthService;
import com.mediconnect.service.DoctorService;
import com.mediconnect.service.HospitalService;
import com.mediconnect.service.PatientService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional // profile ya user me kuch fail ho to dono rollback ho jaate hain
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PatientService patientService;
	private final DoctorService doctorService;
	private final HospitalService hospitalService;

	@Override
	public AuthResponseDTO registerPatient(RegisterPatientRequest request) {
		String email = checkEmailFree(request.getEmail());

		PatientResponseDTO patient = patientService.createPatient(PatientRequestDTO.builder()
				.name(request.getName())
				.age(request.getAge())
				.phoneNumber(request.getPhoneNumber())
				.address(request.getAddress())
				.build());

		return saveUser(email, request.getPassword(), Role.PATIENT, patient.getId(), patient.getName());
	}

	@Override
	public AuthResponseDTO registerDoctor(RegisterDoctorRequest request) {
		String email = checkEmailFree(request.getEmail());

		// hospital exist karta hai ya nahi, ye DoctorService khud check karta hai
		DoctorResponseDTO doctor = doctorService.createDoctor(DoctorRequestDTO.builder()
				.name(request.getName())
				.specialization(request.getSpecialization())
				.experience(request.getExperience())
				.phoneNumber(request.getPhoneNumber())
				.hospitalId(request.getHospitalId())
				.build());

		return saveUser(email, request.getPassword(), Role.DOCTOR, doctor.getId(), doctor.getName());
	}

	@Override
	public AuthResponseDTO registerHospital(RegisterHospitalRequest request) {
		String email = checkEmailFree(request.getEmail());

		HospitalResponseDTO hospital = hospitalService.createHospital(HospitalRequestDTO.builder()
				.name(request.getName())
				.address(request.getAddress())
				.phoneNumber(request.getPhoneNumber())
				.openingTime(request.getOpeningTime())
				.closingTime(request.getClosingTime())
				.build());

		return saveUser(email, request.getPassword(), Role.HOSPITAL, hospital.getId(), hospital.getName());
	}

	@Override
	@Transactional(readOnly = true)
	public AuthResponseDTO login(LoginRequest request) {
		// email ya password galat - dono case me same message (attacker ko hint nahi milta)
		User user = userRepository.findByEmail(normalize(request.getEmail()))
				.orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new UnauthorizedException("Invalid email or password");
		}

		return toResponse(user, resolveName(user));
	}

	// ---------- helpers ----------

	private String normalize(String email) {
		return email == null ? "" : email.trim().toLowerCase();
	}

	private String checkEmailFree(String rawEmail) {
		String email = normalize(rawEmail);
		if (userRepository.existsByEmail(email)) {
			throw new DuplicateResourceException("User", "email", email);
		}
		return email;
	}

	private AuthResponseDTO saveUser(String email, String rawPassword, Role role, Long profileId, String name) {
		User user = userRepository.save(User.builder()
				.email(email)
				.password(passwordEncoder.encode(rawPassword))
				.role(role)
				.profileId(profileId)
				.build());
		return toResponse(user, name);
	}

	private String resolveName(User user) {
		return switch (user.getRole()) {
			case PATIENT -> patientService.getPatientById(user.getProfileId()).getName();
			case DOCTOR -> doctorService.getDoctorById(user.getProfileId()).getName();
			case HOSPITAL -> hospitalService.getHospitalById(user.getProfileId()).getName();
		};
	}

	private AuthResponseDTO toResponse(User user, String name) {
		return AuthResponseDTO.builder()
				.userId(user.getId())
				.email(user.getEmail())
				.role(user.getRole())
				.profileId(user.getProfileId())
				.name(name)
				.build();
	}
}
