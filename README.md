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

# 🚀 How to Run

## 1. Clone the Repository

```bash
git clone https://github.com/nisheerajpoot/medi-Connect.git
```

## 2. Open the Project

Open the project in:

* Eclipse
* Spring Tool Suite (STS)
* IntelliJ IDEA

## 3. Configure MySQL

Update your database configuration in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mediconnect
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Use your own MySQL username, password, and database configuration.

## 4. Run the Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

## 5. Test the APIs

The application runs by default at:

```text
http://localhost:8080
```

You can test the REST APIs using **Postman**.

---

# 🧪 API Testing

Recommended API testing flow:

```text
1. Create Hospital
        ↓
2. Create Doctor
        ↓
3. Create Patient
        ↓
4. Create Appointment
        ↓
5. Check Appointment → PENDING
        ↓
6. Open Reception Dashboard
        ↓
7. View Appointment
        ↓
8. Confirm / Reject Appointment
        ↓
9. If Confirmed → Time Slot Assigned
```

---

# 📈 Future Enhancements

The project can be extended with:

* 🔐 Spring Security & JWT Authentication
* 📧 Email / Notification Integration
* 🏗️ Microservices Architecture
* 📄 Pagination & Sorting
* 👤 Role-based access control
* 📊 Advanced hospital/reception dashboards
* 🔔 Appointment notifications

---

## 🎯 Project Goal

The main goal of **MediConnect** is to build a clean and understandable Spring Boot REST API while learning:

* REST API design
* CRUD operations
* Layered architecture
* DTO-based request/response handling
* Spring Data JPA
* Entity relationships
* Validation
* Exception handling
* Appointment business logic
* Hospital reception workflow

---

## 👨‍💻 Author

**Nishee Rajpoot**

GitHub: [nisheerajpoot](https://github.com/nisheerajpoot)

---

⭐ If you find this project useful, feel free to star the repository.
