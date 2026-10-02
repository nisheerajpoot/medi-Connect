package com.mediconnect.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mediconnect.dto.request.DoctorRequestDTO;
import com.mediconnect.dto.request.UpdateDoctorRequestDTO;
import com.mediconnect.dto.response.ApiResponseDTO;
import com.mediconnect.dto.response.DoctorResponseDTO;
import com.mediconnect.service.DoctorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
	private  final DoctorService doctorService;
	
	@PostMapping
	public ResponseEntity<ApiResponseDTO<DoctorResponseDTO>> createDoctor(
			@Valid @RequestBody DoctorRequestDTO requestDTO){
		
		DoctorResponseDTO response=doctorService.createDoctor(requestDTO);
		ApiResponseDTO<DoctorResponseDTO> apiResponse= ApiResponseDTO.success("Doctor created successfully", response);
		
		return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseDTO<DoctorResponseDTO>> getDoctorById(@PathVariable Long id) {
	    DoctorResponseDTO response = doctorService.getDoctorById(id);
	    return ResponseEntity.ok(ApiResponseDTO.success(response));
	}
	
	@GetMapping
	public ResponseEntity<ApiResponseDTO<List<DoctorResponseDTO>>> getAllDoctors() {
	    List<DoctorResponseDTO> doctors = doctorService.getAllDoctors();
	    return ResponseEntity.ok(ApiResponseDTO.success("Fetched " + doctors.size() + " doctors", doctors));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponseDTO<DoctorResponseDTO>> updateDoctor(
			@PathVariable Long id,
			@Valid @RequestBody UpdateDoctorRequestDTO requestDTO){
		DoctorResponseDTO updateDoctor=doctorService.updateDoctor(id, requestDTO);
		
		return ResponseEntity.ok(ApiResponseDTO.success("Doctor updated successfully",updateDoctor));
	}
	
	 @DeleteMapping("/{id}")
	 public ResponseEntity<ApiResponseDTO<Void>> deleteDoctor(@PathVariable Long id){
		doctorService.deleteDoctor(id);
		 return ResponseEntity.ok(ApiResponseDTO.success("Doctor deleted  successfully"));
	 }
	 
	 @GetMapping("/hospital/{hospitalId}")
	 public ResponseEntity<ApiResponseDTO<List<DoctorResponseDTO>>> getDoctorsByHospital(
	         @PathVariable Long hospitalId) {

	     List<DoctorResponseDTO> doctors = doctorService.getDoctorsByHospital(hospitalId);
	     return ResponseEntity.ok(ApiResponseDTO.success(doctors));
	 }
	 

}


