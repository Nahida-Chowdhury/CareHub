# Hospital Management System - Receptionist Module

## Project Overview

This project is a comprehensive Hospital Management System with a focus on the Receptionist module. It provides an intuitive graphical interface for receptionists to manage patient appointments, billing, and basic patient/doctor information within a hospital setting.

## Key Features

### Core Functionalities
- **Appointment Management**: Schedule, view, and update patient appointments
- **Billing System**: Create and manage patient bills and payment statuses
- **Patient Records**: View and update basic patient information
- **Doctor Schedules**: Access doctor availability and specialization information

### Technical Highlights
- Java Swing-based graphical user interface
- Model-View-Controller (MVC) architecture
- Comprehensive data validation
- Responsive UI design with tabbed navigation
- Real-time data refresh capabilities

## System Requirements
- Java JDK 17 or higher
- Maven for dependency management
- 1024x768 minimum screen resolution

## Installation
1. Clone the repository
2. Build the project using Maven: `mvn clean install`
3. Run the application: `java -jar target/hospital-management-receptionist.jar`

## Usage Guide
1. **Login**: Receptionists must authenticate with valid credentials
2. **Dashboard**: Central hub with quick access to all features
3. **Appointments Tab**: 
   - View upcoming appointments
   - Schedule new appointments
   - Mark appointments as completed
4. **Billing Tab**:
   - Generate new bills
   - Track payment statuses
   - Print billing statements
5. **Patients Tab**:
   - Register new patients
   - Update patient information
6. **Doctors Tab**:
   - View doctor schedules
   - Check specialization information

## Testing Approach
The system includes comprehensive unit tests using JUnit 5 and Mockito to ensure reliability. Tests cover:
- UI component initialization
- Data validation
- Business logic
- Integration between components

## Documentation
- Javadoc comments throughout the codebase
- UML diagrams for major components
- User manual for receptionist staff

## Future Enhancements
- Integration with electronic health records
- Online appointment booking portal
- Advanced reporting features
- Mobile-friendly interface

## License
This project is licensed under the MIT License. See LICENSE file for details.

## Contact
For support or contributions, please contact the development team at hospital-management@example.com
