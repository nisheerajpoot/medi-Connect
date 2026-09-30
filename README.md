# 🏥 MediConnect

A simple **Spring Boot REST API** project to practice core REST API concepts — built without microservices, Spring Security, JWT, or other advanced features, keeping the focus purely on **clean CRUD design and REST principles**.

---

## 📖 About the Project

**MediConnect** is a Hospital Appointment Management System that simulates a basic appointment booking flow.

### 👤 Patient Appointment Flow

```text
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

Hospital reception staff then manage appointments through a dedicated reception flow:

```text
Get Hospital's Appointments / Dashboard
      ↓
Check Appointment Details
      ↓
Confirm or Reject
      ↓
Assign Time Slot on Confirmation
      ↓
CONFIRMED / REJECTED
```

---

## 🛠️ Tech Stack

* ☕ Java 17
* 🌱 Spring Boot
* 🌐 Spring Web — REST APIs
* 🗄️ Spring Data JPA
* 🐬 MySQL
* 🧩 Lombok
* 📦 Maven

---

## 🗂️ Project Structure

```text
com.mediconnect
├── controller        → REST API endpoints
├── dto.request       → Request DTOs
├── dto.response      → Response DTOs
├── entity            → JPA entities
├── exception         → Custom exception handling
├── repository        → Spring Data JPA repositories
├── service           → Service interfaces
└── service.impl      → Service implementations
```

---

## 🗃️ Main Entities

| Entity          | Description                                                                             |
| --------------- | --------------------------------------------------------------------------------------- |
| **Hospital**    | id, name, address, phoneNumber, openingTime, closingTime                                |
| **Doctor**      | id, name, specialization, experience, phoneNumber, hospital                             |
| **Patient**     | id, name, age, phoneNumber, address                                                     |
| **Appointment** | id, patient, doctor, hospital, appointmentDate, startTime, endTime, healthIssue, status |

### Entity Relationships

```text
Hospital
   │
   └── One Hospital → Many Doctors

Patient
   │
   └── One Patient → Many Appointments

Doctor
   │
   └── One Doctor → Many Appointments

Hospital
   │
   └── One Hospital → Many Appointments
```

The `Appointment` entity maintains the relationships using `@ManyToOne` foreign keys.

### Appointment Time Slot

`startTime` and `endTime` remain `null` when an appointment is created.

The time slot is assigned only when the **hospital reception confirms the appointment**.

---

# ✨ Features

## 🏥 Hospital Management

* Add Hospital
* Get All Hospitals
* Get Hospital By ID
* Update Hospital — partial update
* Delete Hospital
* Search Hospital By Name

## 👨‍⚕️ Doctor Management

* Add Doctor
* Get Doctor By ID
* Update Doctor — partial update
* Delete Doctor
* Get Doctors By Hospital

## 🧑‍🤝‍🧑 Patient Management

* Add Patient
* Get All Patients
* Get Patient By ID
* Update Patient — partial update
* Delete Patient

## 📅 Appointment Management

* Create Appointment
* Appointment status starts as `PENDING`
* Time slot is not assigned during appointment creation
* Get All Appointments
* Get Appointment By ID
* Get Appointments By Patient
* Get Appointments By Doctor
* Delete Appointment

## 🏥 Reception Management

Hospital reception staff can manage appointments for their hospital through dedicated reception APIs.

* View reception dashboard for a hospital
* View pending / confirmed / rejected appointment counts
* Get all appointments for a hospital
* Get a specific appointment's details
* Validate that an appointment belongs to the requested hospital
* Get a patient's appointments within a hospital
* Confirm or reject an appointment
* Assign `startTime` and `endTime` when confirming
* Reject confirmation if the time falls outside hospital opening/closing hours
* Reject confirmation if the selected slot overlaps with another confirmed appointment for the same doctor on the same date

---

# 📡 API Endpoints

## 🏥 Hospital APIs

| Method   | Endpoint                  | Description             |
| -------- | ------------------------- | ----------------------- |
| `POST`   | `/hospitals`              | Add Hospital            |
| `GET`    | `/hospitals`              | Get All Hospitals       |
| `GET`    | `/hospitals/{id}`         | Get Hospital By ID      |
| `PUT`    | `/hospitals/{id}`         | Update Hospital         |
| `DELETE` | `/hospitals/{id}`         | Delete Hospital         |
| `GET`    | `/hospitals/search?name=` | Search Hospital By Name |

---

## 👨‍⚕️ Doctor APIs

| Method   | Endpoint                         | Description             |
| -------- | -------------------------------- | ----------------------- |
| `POST`   | `/doctors`                       | Add Doctor              |
| `GET`    | `/doctors/{id}`                  | Get Doctor By ID        |
| `PUT`    | `/doctors/{id}`                  | Update Doctor           |
| `DELETE` | `/doctors/{id}`                  | Delete Doctor           |
| `GET`    | `/doctors/hospital/{hospitalId}` | Get Doctors By Hospital |

---

## 🧑‍🤝‍🧑 Patient APIs

| Method   | Endpoint         | Description       |
| -------- | ---------------- | ----------------- |
| `POST`   | `/patients`      | Add Patient       |
| `GET`    | `/patients`      | Get All Patients  |
| `GET`    | `/patients/{id}` | Get Patient By ID |
| `PUT`    | `/patients/{id}` | Update Patient    |
| `DELETE` | `/patients/{id}` | Delete Patient    |

---

## 📅 Appointment APIs

| Method   | Endpoint                            | Description              |
| -------- | ----------------------------------- | ------------------------ |
| `POST`   | `/appointments`                     | Create Appointment       |
| `GET`    | `/appointments`                     | Get All Appointments     |
| `GET`    | `/appointments/{id}`                | Get Appointment By ID    |
| `GET`    | `/appointments/patient/{patientId}` | Get Patient Appointments |
| `GET`    | `/appointments/doctor/{doctorId}`   | Get Doctor Appointments  |
| `DELETE` | `/appointments/{id}`                | Delete Appointment       |

---

## 🏥 Reception APIs

| Method  | Endpoint                                                      | Description                         |
| ------- | ------------------------------------------------------------- | ----------------------------------- |
| `GET`   | `/reception/{hospitalId}`                                     | Get Reception Dashboard             |
| `GET`   | `/reception/{hospitalId}/appointments`                        | Get Hospital Appointments           |
| `GET`   | `/reception/{hospitalId}/appointments/{appointmentId}`        | Get Appointment Details             |
| `GET`   | `/reception/{hospitalId}/patients/{patientId}/appointments`   | Get Patient's Hospital Appointments |
| `PATCH` | `/reception/{hospitalId}/appointments/{appointmentId}/status` | Confirm / Reject Appointment        |

---

# 📌 Appointment Status Flow

```text
             Create Appointment
                     │
                     ▼
                 ┌─────────┐
                 │ PENDING │
                 └────┬────┘
                      │
             Reception Reviews
                ┌─────┴─────┐
                ▼           ▼
          ┌───────────┐  ┌──────────┐
          │ CONFIRMED │  │ REJECTED │
          └───────────┘  └──────────┘
                │
                ▼
        Assign Time Slot
```

---

# 🔄 Confirming an Appointment

### Request

```http
PATCH /reception/1/appointments/3/status
Content-Type: application/json
```

### Request Body

```json
{
  "status": "CONFIRMED",
  "startTime": "10:00:00",
  "endTime": "10:30:00"
}
```

When the reception confirms an appointment:

* Appointment status becomes `CONFIRMED`
* `startTime` is assigned
* `endTime` is assigned
* Hospital working hours are validated
* Doctor's existing confirmed appointments are checked for overlapping time slots

---

# ⏰ Appointment Slot Validation

Before confirming an appointment, the reception flow validates:

### 1. Hospital Working Hours

The selected appointment time must fall within the hospital's:

```text
Opening Time
      ↓
Selected Appointment Slot
      ↓
Closing Time
```

### 2. Doctor Availability

The selected time slot must not overlap with anoth
