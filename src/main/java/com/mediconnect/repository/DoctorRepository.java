package com.mediconnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mediconnect.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	List<Doctor> findByHospitalId(Long hospitalId);
	boolean existsByPhoneNumber(String phoneNumber);
}
