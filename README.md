# 🏥 MediConnect
 
A **Spring Boot REST API** project to practice core REST API concepts — a Hospital Appointment Management System with **role-based registration and login** (Patient, Doctor, Hospital).
 
It is built **without microservices, Spring Security filters, JWT, cookies or sessions**, keeping the focus on **clean REST design, layered architecture, validation and business logic**.
 
---
 
## 📖 About the Project
 
**MediConnect** simulates a real hospital appointment booking platform:
 
* Patients, Doctors and Hospitals can **register and log in** with their own role.
* Patients search hospitals, pick a doctor and book an appointment.
* Hospitals manage their doctors and **confirm / reject** appointments.
### 🔑 Authentication Flow
 
```text
Register as PATIENT / DOCTOR / HOSPITAL
      ↓
Profile (patient / doctor / hospital) + Login account (users table) created together
      ↓
Login with email + password
      ↓
API returns  { userId, email, role, profileId, name }
      ↓
Client opens the portal for that role and uses profileId to fetch its own data
```
 
### 👤 Patient Appointment Flow
 
```text
Patient logs in
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
 
### 🛎️ Hospital Reception Flow
 
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
* 🌱 Spring Boot 4.1
* 🌐 Spring Web — REST APIs
* 🗄️ Spring Data JPA
* ✅ Bean Validation (Jakarta)
* 🔒 `spring-security-crypto` — **only BCrypt password hashing** (no Spring Security filters)
* 🐬 MySQL
* 🧩 Lombok
* 📦 Maven
---
 
## 🗂️ Project Structure
 
```text
com.mediconnect
├── config            → PasswordEncoder (BCrypt) bean
├── controller        → REST API endpoints
├── dto.request       → Request DTOs
├── dto.response      → Response DTOs
├── entity            → JPA entities
├── exception         → Custom exceptions + global exception handler
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
| **User**        | id, email (unique), password (BCrypt hash), role, profileId                             |
 
**Enums:** `AppointmentStatus` → `PENDING`, `CONFIRMED`, `REJECTED` · `Role` → `PATIENT`, `DOCTOR`, `HOSPITAL`
 
### Entity Relationships
 
```text
Hospital  1 ──── * Doctor
Patient   1 ──── * Appointment
Doctor    1 ──── * Appointment
Hospital  1 ──── * Appointment
```
 
The `Appointment` and `Doctor` entities maintain these relationships with `@ManyToOne` foreign keys (e.g. `doctors.hospital_id`).
 
---
 
# ✨ Features
 
## 🔐 Authentication & Roles
 
* Register as **Patient**, **Doctor** or **Hospital** (profile + login account created in one transaction)
* Login with email and password
* Passwords stored as **BCrypt hashes**
* Email is unique and case-insensitive
* Same error message for wrong email or wrong password (`Invalid email or password`)
* Doctor registration validates that the selected hospital exists
## 🏥 Hospital Management
 
* Add Hospital
* Get All Hospitals
* Get Hospital By ID
* Update Hospital — partial update
* Delete Hospital
* Search Hospital By Name
## 👨‍⚕️ Doctor Management
 
* Add Doctor (linked to a hospital by `hospitalId`)
* Get All Doctors
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
 
* Create Appointment (status starts as `PENDING`, date must be in the future)
* Time slot is not assigned during creation
* Get All Appointments
* Get Appointment By ID
* Get Appointments By Patient
* Get Appointments By Doctor
* **Update Appointment** — change doctor, date or health issue (partial update)
  * Allowed **only while the status is `PENDING`**
  * A new doctor must belong to the appointment's hospital
* Delete Appointment
## 🛎️ Reception Management
 
Hospital staff manage appointments for their hospital through dedicated reception APIs.
 
* View reception dashboard (pending / confirmed / rejected counts)
* Get all appointments for a hospital
* Get a specific appointment's details
* Validate that an appointment belongs to the requested hospital
* Get a patient's appointments within a hospital
* Confirm or reject an appointment
* Assign `startTime` and `endTime` when confirming
* Reject confirmation if the time falls outside hospital opening/closing hours
* Reject confirmation if the slot overlaps with another confirmed appointment of the same doctor on the same date
---
 
# 📡 API Endpoints
 
**Base URL:** `http://localhost:8085/api`
 
## 🔐 Auth APIs
 
| Method | Endpoint                  | Description                     |
| ------ | ------------------------- | ------------------------------- |
| `POST` | `/auth/register/patient`  | Register a Patient              |
| `POST` | `/auth/register/doctor`   | Register a Doctor               |
| `POST` | `/auth/register/hospital` | Register a Hospital             |
| `POST` | `/auth/login`             | Login (returns role + profileId) |
 
<details>
<summary><b>Request body examples</b></summary>
**Register patient**
```json
{
  "email": "ram@gmail.com",
  "password": "123456",
  "name": "Ram",
  "age": 25,
  "phoneNumber": "9876543210",
  "address": "Bhopal"
}
```
 
**Register doctor** (`hospitalId` comes from the hospital dropdown in the UI)
```json
{
  "email": "dr.sharma@gmail.com",
  "password": "123456",
  "name": "Dr. Sharma",
  "specialization": "Cardiologist",
  "experience": 8,
  "phoneNumber": "9876500000",
  "hospitalId": 1
}
```
 
**Register hospital**
```json
{
  "email": "city@gmail.com",
  "password": "123456",
  "name": "City Hospital",
  "address": "MP Nagar, Bhopal",
  "phoneNumber": "9876511111",
  "openingTime": "09:00",
  "closingTime": "18:00"
}
```
 
**Login**
```json
{ "email": "ram@gmail.com", "password": "123456" }
```
 
**Login / register response**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "email": "ram@gmail.com",
    "role": "PATIENT",
    "profileId": 5,
    "name": "Ram"
  }
}
```
</details>
---
 
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
| `GET`    | `/doctors`                       | Get All Doctors         |
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
 
| Method   | Endpoint                            | Description                                |
| -------- | ----------------------------------- | ------------------------------------------ |
| `POST`   | `/appointments`                     | Create Appointment                         |
| `GET`    | `/appointments`                     | Get All Appointments                       |
| `GET`    | `/appointments/{id}`                | Get Appointment By ID                      |
| `PUT`    | `/appointments/{id}`                | Update Appointment (**PENDING only**)      |
| `GET`    | `/appointments/patient/{patientId}` | Get Patient Appointments                   |
| `GET`    | `/appointments/doctor/{doctorId}`   | Get Doctor Appointments                    |
| `DELETE` | `/appointments/{id}`                | Delete Appointment                         |
 
<details>
<summary><b>Request body examples</b></summary>
**Create appointment**
```json
{
  "patientId": 1,
  "hospitalId": 1,
  "doctorId": 1,
  "appointmentDate": "2026-12-15",
  "healthIssue": "Chest pain"
}
```
 
**Update appointment** (all fields optional, at least one required)
```json
{ "doctorId": 2, "appointmentDate": "2026-12-20", "healthIssue": "Chest pain and dizziness" }
```
</details>
---
 
## 🛎️ Reception APIs
 
| Method  | Endpoint                                                      | Description                         |
| ------- | ------------------------------------------------------------- | ----------------------------------- |
| `GET`   | `/reception/{hospitalId}`                                     | Get Reception Dashboard             |
| `GET`   | `/reception/{hospitalId}/appointments`                        | Get Hospital Appointments           |
| `GET`   | `/reception/{hospitalId}/appointments/{appointmentId}`        | Get Appointment Details             |
| `GET`   | `/reception/{hospitalId}/patients/{patientId}/appointments`   | Get Patient's Hospital Appointments |
| `PATCH` | `/reception/{hospitalId}/appointments/{appointmentId}/status` | Confirm / Reject Appointment        |
 
**Confirm appointment** (start and end time are required for `CONFIRMED`)
```json
{ "status": "CONFIRMED", "startTime": "10:00", "endTime": "10:30" }
```
 
**Reject appointment**
```json
{ "status": "REJECTED" }
```
 
---
 
## 📦 Response Format
 
**Success**
```json
{ "success": true, "message": "Hospital added successfully", "data": { } }
```
 
**Error**
```json
{
  "timeStamp": "2026-10-02T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/patients",
  "validationError": { "phoneNumber": "Phone number must be 10 digits" }
}
```
 
| Status | When it happens                                                          |
| ------ | ------------------------------------------------------------------------ |
| `200`  | Request successful                                                       |
| `201`  | Resource created / user registered                                       |
| `400`  | Validation failed, invalid operation, wrong id type                      |
| `401`  | Wrong email or password                                                  |
| `404`  | Hospital / doctor / patient / appointment not found                      |
| `409`  | Duplicate resource (e.g. email or phone number already registered)       |
| `500`  | Unexpected server error                                                  |
 
---
 
# 🚀 How to Run
 
## 1. Clone the Repository
 
```bash
git clone https://github.com/nisheerajpoot/medi-Connect.git
```
 
## 2. Open the Project
 
Open the project in Eclipse, Spring Tool Suite (STS) or IntelliJ IDEA.
 
## 3. Create the Database and Configure MySQL
 
```sql
CREATE DATABASE medi_connect_db;
```
 
Update your credentials in:
 
```text
src/main/resources/application.properties
```
 
```properties
server.port=8085
spring.datasource.url=jdbc:mysql://localhost:3306/medi_connect_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```
 
All tables (including `users`) are created automatically by Hibernate.
 
## 4. Run the Application
 
```bash
./mvnw spring-boot:run
```
 
On Windows:
 
```bash
mvnw.cmd spring-boot:run
```
 
The API runs at:
 
```text
http://localhost:8085/api
```
 
---
 
# 🧪 API Testing
 
A ready-made **Postman collection** (`MediConnect.postman_collection.json`) covers all endpoints.
 
1. Open Postman → **Import** → select the collection file
2. Start the backend
3. Right-click the collection → **Run collection** → **Run**
The collection saves ids automatically (hospitalId, doctorId, patientId, appointmentId…), generates unique phone numbers and emails on every run, and checks status codes.
 
| Folder                  | What it tests                                                        |
| ----------------------- | -------------------------------------------------------------------- |
| 1. Hospitals            | CRUD + search                                                        |
| 2. Doctors              | CRUD + get all + by hospital                                         |
| 3. Patients             | CRUD                                                                 |
| 4. Appointments         | Create, get, filter, update (PENDING only)                           |
| 5. Reception            | Dashboard, confirm with time slot, reject                            |
| 6. Auth                 | Register as 3 roles, login, wrong password (401), duplicate email (409) |
| 7. Validation & Errors  | 400 / 404 cases                                                      |
| 8. Cleanup              | Deletes all test data                                                |
 
Manual testing order:
 
```text
1. Create Hospital
        ↓
2. Create Doctor
        ↓
3. Create Patient
        ↓
4. Create Appointment  → status PENDING
        ↓
5. Open Reception Dashboard
        ↓
6. Confirm (with start/end time) or Reject
        ↓
7. If Confirmed → Time Slot Assigned
```
 
---
 
# 🔒 Security Note
 
Login is intentionally **simple** for learning:
 
* ✅ Passwords are hashed with BCrypt
* ✅ Email uniqueness and credential validation
* ❌ No token (JWT), session or cookie — the client stores the login response itself
* ❌ Other APIs are still open: role-based access is **not enforced on the backend yet**
Real API protection (Spring Security + JWT) is planned as a later step.
 
---
 
# 🖥️ Frontend (In Progress)
 
A React frontend is being built on top of this API with separate role-based portals:
 
```text
Public website  → Home (hero page), Hospitals, Login, Register (choose role)
Patient portal  → Dashboard, Find Hospitals, Book Appointment, My Appointments, Profile
Doctor portal   → Dashboard, My Appointments, Profile
Hospital portal → Dashboard, Appointments (confirm / reject), Doctors, Profile
```
 
---
 
# 📈 Future Enhancements
 
* 🔐 Spring Security & JWT — protect APIs by role
* ✅ Doctor approval by hospital after registration
* 🔗 Proper `User` ↔ profile database relationships
* 📄 Pagination & Sorting
* 📧 Email / Notification Integration
* 🔔 Appointment reminders
* 📊 Advanced hospital dashboards
* 🏗️ Microservices Architecture
---
 
## 🎯 Project Goal
 
The main goal of **MediConnect** is to build a clean and understandable Spring Boot REST API while learning:
 
* REST API design
* CRUD operations
* Layered architecture
* DTO-based request/response handling
* Spring Data JPA and entity relationships
* Validation and global exception handling
* Registration, login and password hashing
* Role-based user accounts
* Appointment business logic
* Hospital reception workflow
