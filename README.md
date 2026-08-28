# 🏥 MediConnect

A simple **Spring Boot REST API** project to practice core REST API concepts — built without microservices, Spring Security, JWT, or other advanced features, keeping focus purely on clean CRUD design and REST principles.

##  About the Project

MediConnect is a Hospital Appointment Management System that simulates a basic appointment booking flow:

```
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
```

Hospital staff can then manage appointments:

```
Get All Appointments
     ↓
Check Appointment Details
     ↓
Update Status
     ↓
CONFIRMED / REJECTED
```

##  Tech Stack

- Java 17
- Spring Boot
- Spring Web (REST APIs)
- Spring Data JPA
- MySQL 
- Maven

## 🗂️ Project Structure

```
com.mediconnect
├── controller        → REST API endpoints
├── dto.request        → Request DTOs
├── dto.response       → Response DTOs
├── entity             → JPA entities
├── exception          → Custom exception handling
├── repository         → Spring Data JPA repositories
├── service            → Service interfaces
└── service.impl       → Service implementations
```

## 🗂️ Main Entities

| Entity          | Description                                                         |
| --------------- | ------------------------------------------------------------------- |
| **Hospital**    | id, name, address, phoneNumber                                      |
| **Doctor**      | id, name, specialization, experience, phoneNumber, hospital         |
| **Patient**     | id, name, age, phoneNumber, address                                 |
| **Appointment** | id, patient, hospital, doctor, appointmentDate, healthIssue, status |

**Relationship:** One Hospital → Many Doctors

##  Features

### Hospital Management

- Add Hospital
- Get All Hospitals
- Get Hospital By ID
- Update Hospital
- Delete Hospital
- Search Hospital By Name

### Doctor Management

- Add Doctor
- Get All Doctors
- Get Doctor By ID
- Update Doctor
- Delete Doctor
- Get Doctors By Hospital

### Patient Management

- Add Patient
- Get All Patients
- Get Patient By ID
- Update Patient
- Delete Patient

### Appointment Management 

- Create Appointment
- Get All Appointments
- Get Appointment By ID
- Update Appointment
- Delete Appointment
- Get Appointments By Patient
- Get Appointments By Doctor
- Update Appointment Status (PENDING / CONFIRMED / REJECTED)

## 📡 API Endpoints

### Hospital

```
POST   /hospitals
GET    /hospitals
GET    /hospitals/{id}
PUT    /hospitals/{id}
DELETE /hospitals/{id}
```

### Doctor

```
POST   /doctors
GET    /doctors
GET    /doctors/{id}
PUT    /doctors/{id}
DELETE /doctors/{id}
GET    /doctors/hospital/{hospitalId}
```

### Patient

```
POST   /patients
GET    /patients
GET    /patients/{id}
PUT    /patients/{id}
DELETE /patients/{id}
```

### Appointment

```
POST   /appointments
GET    /appointments
GET    /appointments/{id}
PUT    /appointments/{id}
DELETE /appointments/{id}
GET    /appointments/patient/{patientId}
GET    /appointments/doctor/{doctorId}
PUT    /appointments/{id}/status
```

##  How to Run

1. Clone the repository
   ```
   git clone https://github.com/<your-username>/mediconnect.git
   ```
2. Open the project in your IDE (Eclipse / STS / IntelliJ)
3. Update database configuration in `src/main/resources/application.properties`
4. Run the application
   ```
   ./mvnw spring-boot:run
   ```
5. Test APIs using Postman at `http://localhost:8080`

## 📈 Future Enhancements

- Spring Security & JWT Authentication
- Email/Notification integration
- Microservices architecture
- Pagination & Sorting
