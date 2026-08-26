package com.mediconnect.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediconnect.dto.request.DoctorRequestDTO;
import com.mediconnect.dto.request.UpdateDoctorRequestDTO;
import com.mediconnect.dto.response.DoctorResponseDTO;
import com.mediconnect.dto.response.HospitalResponseDTO;
import com.mediconnect.entity.Doctor;
import com.mediconnect.entity.Hospital;
import com.mediconnect.exception.DuplicateResourceException;
import com.mediconnect.exception.ResourceNotFoundException;
import com.mediconnect.repository.DoctorRepository;
import com.mediconnect.repository.HospitalRepository;
import com.mediconnect.service.DoctorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService{
	
	public final DoctorRepository doctorRepository;
	private final HospitalRepository hospitalRepository; 

	@Override
	public DoctorResponseDTO createDoctor(DoctorRequestDTO requestDTO) {
		if(doctorRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())) {
			throw new DuplicateResourceException("Doctor","phoneNumber", requestDTO.getPhoneNumber());
		}

	    Hospital hospital = hospitalRepository.findById(requestDTO.getHospitalId())
	            .orElseThrow(() -> new ResourceNotFoundException("Hospital", "id", requestDTO.getHospitalId()));
		Doctor doctor=Doctor.builder()
				.name(requestDTO.getName())
				.specialization(requestDTO.getSpecialization())
				.experience(requestDTO.getExperience())
				.phoneNumber(requestDTO.getPhoneNumber())
				.hospital(hospital)
				.build();
		
		Doctor savedDoctor = doctorRepository.save(doctor);

	    return mapToResponseDTO(savedDoctor);
	}
	
	@Override
	public DoctorResponseDTO updateDoctor(Long id, UpdateDoctorRequestDTO requestDTO) {

	    Doctor doctor = doctorRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));

	    if (requestDTO.getName() == null &&
	        requestDTO.getSpecialization() == null &&
	        requestDTO.getPhoneNumber() == null &&
	        requestDTO.getExperience() == null &&
	        requestDTO.getHospitalId() == null) {
	        throw new IllegalArgumentException("At least one field must be provided for update");
	    }

	    if (requestDTO.getName() != null) {
	        if (requestDTO.getName().isBlank()) {
	            throw new IllegalArgumentException("Name cannot be blank");
	        }
	        doctor.setName(requestDTO.getName().trim());
	    }

	    if (requestDTO.getSpecialization() != null) {
	        if (requestDTO.getSpecialization().isBlank()) {
	            throw new IllegalArgumentException("Specialization cannot be blank");
	        }
	        doctor.setSpecialization(requestDTO.getSpecialization().trim());
	    }

	    if (requestDTO.getExperience() != null) {
	        doctor.setExperience(requestDTO.getExperience());
	    }

	    if (requestDTO.getPhoneNumber() != null) {
	        if (requestDTO.getPhoneNumber().isBlank()) {
	            throw new IllegalArgumentException("Phone cannot be blank");
	        }
	        String newPhoneNumber = requestDTO.getPhoneNumber().trim();
	        if (!newPhoneNumber.equals(doctor.getPhoneNumber()) &&
	                doctorRepository.existsByPhoneNumber(newPhoneNumber)) {
	            throw new DuplicateResourceException("Doctor", "phoneNumber", newPhoneNumber);
	        }
	        doctor.setPhoneNumber(newPhoneNumber);
	    }

	    if (requestDTO.getHospitalId() != null) {
	        Hospital hospital = hospitalRepository.findById(requestDTO.getHospitalId())
	                .orElseThrow(() -> new ResourceNotFoundException("Hospital", "id", requestDTO.getHospitalId()));
	        doctor.setHospital(hospital);
	    }

	    Doctor updatedDoctor = doctorRepository.save(doctor);
	    return mapToResponseDTO(updatedDoctor);
	}   

	@Override
	public void deleteDoctor(Long id) {
	    Doctor doctor = doctorRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
	    doctorRepository.delete(doctor);
	}   // ← deleteDoctor yahan khatam

	@Override
	public List<DoctorResponseDTO> getDoctorsByHospital(Long hospitalId) {
	    List<Doctor> doctors = doctorRepository.findByHospitalId(hospitalId);
	    List<DoctorResponseDTO> responseList = new ArrayList<>();
	    for (Doctor doctor : doctors) {
	        responseList.add(mapToResponseDTO(doctor));
	    }
	    return responseList;
	}  

	
	private DoctorResponseDTO mapToResponseDTO(Doctor doctor) {
	    HospitalResponseDTO hospitalDTO = HospitalResponseDTO.builder()
	            .id(doctor.getHospital().getId())
	            .name(doctor.getHospital().getName())
	            .address(doctor.getHospital().getAddress())
	            .phoneNumber(doctor.getHospital().getPhoneNumber())
	            .openingTime(doctor.getHospital().getOpeningTime())
	            .closingTime(doctor.getHospital().getClosingTime())
	            .build();

	    return DoctorResponseDTO.builder()
	            .id(doctor.getId())
	            .name(doctor.getName())
	            .specialization(doctor.getSpecialization())
	            .experience(doctor.getExperience())
	            .phoneNumber(doctor.getPhoneNumber())
	            .hospital(hospitalDTO)
	            .build();
	}

	@Override
	public DoctorResponseDTO getDoctorById(Long id) {
		Doctor doctor = doctorRepository.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
		return mapToResponseDTO(doctor);
	}

}
