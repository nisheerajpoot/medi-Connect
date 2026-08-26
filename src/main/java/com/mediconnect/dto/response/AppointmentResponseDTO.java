package com.mediconnect.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mediconnect.entity.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDTO {
	private Long id;
	private PatientResponseDTO patient;
	private DoctorResponseDTO doctor;
	private LocalDate appointmentDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private String healthIssue;
	private AppointmentStatus status;
	
}
