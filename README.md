🏥 MediConnect

A simple Spring Boot REST API project to practice core REST API concepts — built without microservices, Spring Security, JWT, or other advanced features, keeping focus purely on clean CRUD design and REST principles.

About the Project

MediConnect is a Hospital Appointment Management System that simulates a basic appointment booking flow:

User / Patient
     ↓
Search Hospital
     ↓
Get Hospital Details
     ↓
Get Doctors of Hospital
     ↓
Select Doctor
     ↓
Create Appointment
     ↓
Status = PENDING

Hospital reception staff then manage appointments through a dedicated reception flow:

Get Hospital's Appointments / Dashboard
     ↓
Check Appointment Details
     ↓
Confirm or Reject (assigns time slot on confirmation)
     ↓
CONFIRMED / REJECTED
Tech Stack
Java 17
Spring Boot
Spring Web (REST APIs)
Spring Data JPA
MySQL
Lombok
Maven
🗂️ Project Structure
com.mediconnect
├── controller        → REST API endpoints
├── dto.request        → Request DTOs (create + partial-update variants)
├── dto.response       → Response DTOs
├── entity             → JPA entities
├── exception          → Custom exception handling
├── repository         → Spring Data JPA repositories
├── service            → Service interfaces
└── service.impl       → Service implementations
🗂️ Main Entities
Entity	Description
Hospital	id, name, address, phoneNumber, openingTime, closingTime
Doctor	id, name, specialization, experience, phoneNumber, hospital
Patient	id, name, age, phoneNumber, address
Appointment	id, patient, doctor, hospital, appointmentDate, startTime, endTime, healthIssue, status

Relationships:

One Hospital → Many Doctors
One Patient / Doctor / Hospital → Many Appointments (via @ManyToOne foreign keys on Appointment)

startTime and endTime are left null when an appointment is created — they are only assigned by hospital reception at the time an appointment is confirmed.

Features
Hospital Management
Add Hospital
Get All Hospitals
Get Hospital By ID
Update Hospital (partial update — only provided fields are changed)
Delete Hospital
Search Hospital By Name
Doctor Management
Add Doctor
Get Doctor By ID
Update Doctor (partial update)
Delete Doctor
Get Doctors By Hospital
Patient Management
Add Patient
Get All Patients
Get Patient By ID
Update Patient (partial update)
Delete Patient
Appointment Management
Create Appointment (status starts as PENDING, no time slot assigned yet)
Get All Appointments
Get Appointment By ID
Get Appointments By Patient
Get Appointments By Doctor
Delete Appointment
Reception Management (Hospital Staff)
View reception dashboard for a hospital (pending / confirmed / rejected counts)
Get all appointments for a hospital
Get a specific appointment's details (validated to belong to that hospital)
Get a patient's appointments within that hospital
Confirm or reject an appointment, assigning startTime / endTime on confirmation
Rejects confirmation if the time falls outside the hospital's opening/closing hours
Rejects confirmation if it overlaps with another confirmed appointment for the same doctor on the same date
📡 API Endpoints
Hospital
POST   /hospitals
GET    /hospitals
GET    /hospitals/{id}
PUT    /hospitals/{id}
DELETE /hospitals/{id}
GET    /hospitals/search?name=
Doctor
POST   /doctors
GET    /doctors/{id}
PUT    /doctors/{id}
DELETE /doctors/{id}
GET    /doctors/hospital/{hospitalId}
Patient
POST   /patients
GET    /patients
GET    /patients/{id}
PUT    /patients/{id}
DELETE /patients/{id}
Appointment
POST   /appointments
GET    /appointments
GET    /appointments/{id}
GET    /appointments/patient/{patientId}
GET    /appointments/doctor/{doctorId}
DELETE /appointments/{id}
Reception
GET    /reception/{hospitalId}
GET    /reception/{hospitalId}/appointments
GET    /reception/{hospitalId}/appointments/{appointmentId}
GET    /reception/{hospitalId}/patients/{patientId}/appointments
PATCH  /reception/{hospitalId}/appointments/{appointmentId}/status

Example — confirming an appointment:

PATCH /reception/1/appointments/3/status

{
  "status": "CONFIRMED",
  "startTime": "10:00:00",
  "endTime": "10:30:00"
}
How to Run
Clone the repository
   git clone https://github.com/nisheerajpoot/medi-Connect.git
Open the project in your IDE (Eclipse / STS / IntelliJ)
Update database configuration in src/main/resources/application.properties
Run the application
   ./mvnw spring-boot:run
Test APIs using Postman at http://localhost:8080
📈 Future Enhancements
Spring Security & JWT Authentication
Email/Notification integration
Microservices architecture
Pagination & Sorting