package com.mediconnect.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mediconnect.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	
}
