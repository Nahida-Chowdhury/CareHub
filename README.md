# CareHub: Hospital Management System

## 📌 Project Overview
CareHub is a comprehensive **desktop application** and **REST API backend** designed to streamline daily hospital operations.  
The desktop app offers an interface for receptionists, doctors, and administrators to manage appointments, billing, and patient/doctor records.  
The backend provides a robust data layer and RESTful API for programmatic access.

---

## 🚀 Key Features

### Core Functionalities
- **Role-Based Access Control** – Secure login for Administrators, Doctors, and Receptionists.
- **Appointment Management** – Schedule, view, and update patient appointments.
- **Billing System** – Create and manage patient bills and payment statuses.
- **Patient Records** – View and update patient information.
- **Doctor Schedules** – Access doctor availability and specialization.
- **User Management (Admin only)** – Manage all user accounts.

### Technical Highlights
- Java Swing-based graphical UI.
- **MVC architecture** for maintainable code.
- Comprehensive data validation.
- Responsive UI design with tabbed navigation.
- Real-time data refresh.

---

## 🖥 Backend & API Details
- **Backend**: Java  
- **Database**: MongoDB  
- **Server**: Runs on `http://localhost:8000` by default  
- **API Style**: RESTful

### REST API Endpoints
**Health Check**
- `GET /health`

**Patients**
- `GET /api/patients`
- `POST /api/patients`
- `GET /api/patients/{id}`
- `PUT /api/patients/{id}`
- `DELETE /api/patients/{id}`
- `DELETE /api/patients/deleteAll`

**Doctors**
- `GET /api/doctors`
- `POST /api/doctors`
- `GET /api/doctors/{id}`
- `PUT /api/doctors/{id}`
- `DELETE /api/doctors/{id}`

**Appointments**
- `GET /api/appointments`
- `POST /api/appointments`
- `GET /api/appointments/{id}`
- `PUT /api/appointments/{id}`
- `DELETE /api/appointments/{id}`
- `PUT /api/appointments/{id}/complete`

**Bills**
- `GET /api/bills`
- `POST /api/bills`
- `GET /api/bills/{id}`
- `PUT /api/bills/{id}`
- `DELETE /api/bills/{id}`
- `PUT /api/bills/{id}/pay`

**Users**
- `GET /api/users`
- `POST /api/users`

**Authentication**
- `POST /api/auth/login`

---

## ⚙️ System Requirements
- Java JDK **21** or higher
- Maven
- MongoDB installation or MongoDB Atlas cluster
- Minimum resolution: **1024x768**

---

## 📥 Installation

**1. Clone the repository**
```bash
git clone https://github.com/Nahida-Chowdhury/CareHub.git
cd CareHub
```

**2. Configure MongoDB**
- Create a MongoDB database and collections.
- Update the database connection URI in the configuration file (`src/main/resources/application.properties` or similar).

**3. Build with Maven**
```bash
mvn clean install
```

---

## ▶️ Usage Guide
After building:
```bash
java -jar target/hospital-management-receptionist.jar
```

- **Login** with valid credentials.  
- **Dashboard** – Central hub for quick access to features.  
- **Appointments Tab** – View, schedule, mark complete.  
- **Billing Tab** – Generate and track bills.  
- **Patients/Doctors Tabs** – Register patients, view doctor info.

---

## 🧪 Testing Approach
- Unit tests with **JUnit 5** & **Mockito**.
- Covers UI initialization, data validation, business logic, integration.

---

## 📚 Documentation
- Javadoc comments.
- UML diagrams for core components.
- Staff user manual.

---

## 🔮 Future Enhancements
- Integration with EHR systems.
- Online appointment booking.
- Advanced reporting.
- Mobile-friendly interface.

---

## 📜 License
Licensed under the **MIT License** – See `LICENSE` for details.

---

## 📩 Contact
For support or contributions: **hospital-management@example.com**
