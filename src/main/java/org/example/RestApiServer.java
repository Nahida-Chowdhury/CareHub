package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestApiServer {
    private HttpServer server;
    private final Gson gson = new Gson();

    // DAO instances
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    private UserDAO userDAO;

    public RestApiServer() {
        initializeDAOs();
    }

    private void initializeDAOs() {
        try {
            patientDAO = new PatientDAO();
            doctorDAO = new DoctorDAO();
            appointmentDAO = new AppointmentDAO();
            billDAO = new BillDAO();
            userDAO = new UserDAO();
            System.out.println("DAOs initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing DAOs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start(int port) {
        try {
            System.out.println("Starting REST API Server...");
            System.out.println("Testing database connection...");

            boolean dbConnected = DatabaseConnection.getInstance().testConnection();
            if (!dbConnected) {
                System.err.println("WARNING: Database connection failed. Server will start but some operations may fail.");
                System.err.println("Please check your MongoDB Atlas connection and network connectivity.");
            } else {
                System.out.println("Database connection successful!");
            }

            server = HttpServer.create(new InetSocketAddress(port), 0);

            // Setup endpoints
            setupPatientEndpoints();
            setupDoctorEndpoints();
            setupAppointmentEndpoints();
            setupBillEndpoints();
            setupUserEndpoints();
            setupHealthEndpoint();

            server.setExecutor(null);
            server.start();

            System.out.println("✅ REST API Server started successfully on port " + port);
            System.out.println("🌐 Server URL: http://localhost:" + port);
            System.out.println("\n📋 Available endpoints:");
            System.out.println("  Health Check: GET  http://localhost:" + port + "/health");
            System.out.println("  Patients:     GET  http://localhost:" + port + "/api/patients");
            System.out.println("  Patients:     POST http://localhost:" + port + "/api/patients");
            System.out.println("  Patient by ID: GET http://localhost:" + port + "/api/patients/{id}");
            System.out.println("  Update Patient: PUT http://localhost:" + port + "/api/patients/{id}");
            System.out.println("  Delete Patient: DELETE http://localhost:" + port + "/api/patients/{id}");
            System.out.println("  Doctors:      GET  http://localhost:" + port + "/api/doctors");
            System.out.println("  Appointments: GET  http://localhost:" + port + "/api/appointments");
            System.out.println("  Bills:        GET  http://localhost:" + port + "/api/bills");
            System.out.println("  Users:        GET  http://localhost:" + port + "/api/users");
            System.out.println("  Login:        POST http://localhost:" + port + "/api/auth/login");
            System.out.println("\n🔧 Test with: curl http://localhost:" + port + "/health");

        } catch (java.net.BindException e) {
            System.err.println("❌ ERROR: Port " + port + " is already in use!");
            System.err.println("   Please check if another application is using port " + port);
            System.err.println("   Or try starting the server on a different port.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ ERROR: Failed to start server on port " + port);
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupHealthEndpoint() {
        server.createContext("/health", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    boolean dbConnected = DatabaseConnection.getInstance().testConnection();

                    Map<String, Object> health = new HashMap<>();
                    health.put("status", dbConnected ? "UP" : "DOWN");
                    health.put("database", dbConnected ? "Connected" : "Disconnected");
                    health.put("timestamp", System.currentTimeMillis());

                    sendJsonResponse(exchange, 200, health);
                } else {
                    sendErrorResponse(exchange, 405, "Method not allowed");
                }
            }
        });
    }

    private void setupPatientEndpoints() {
        // GET /api/patients - Get all patients
        server.createContext("/api/patients", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();

                if ("GET".equals(method) && "/api/patients".equals(path)) {
                    handleGetAllPatients(exchange);
                } else if ("POST".equals(method) && "/api/patients".equals(path)) {
                    handleCreatePatient(exchange);
                } else {
                    sendErrorResponse(exchange, 404, "Endpoint not found");
                }
            }
        });

        // GET/PUT/DELETE /api/patients/{id}
        server.createContext("/api/patients/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String patientId = extractIdFromPath(path, "/api/patients/");

                if (patientId == null) {
                    sendErrorResponse(exchange, 400, "Patient ID is required");
                    return;
                }

                switch (method) {
                    case "GET":
                        handleGetPatient(exchange, patientId);
                        break;
                    case "PUT":
                        handleUpdatePatient(exchange, patientId);
                        break;
                    case "DELETE":
                        handleDeletePatient(exchange, patientId);
                        break;
                    default:
                        sendErrorResponse(exchange, 405, "Method not allowed");
                }
            }
        });

        // Add "Delete All Patients" endpoint
        server.createContext("/api/patients/deleteAll", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("DELETE".equals(exchange.getRequestMethod())) {
                    handleDeleteAllPatients(exchange);
                } else {
                    sendErrorResponse(exchange, 405, "Method not allowed");
                }
            }
        });
    }

    private void setupDoctorEndpoints() {
        server.createContext("/api/doctors", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();

                if ("GET".equals(method) && "/api/doctors".equals(path)) {
                    handleGetAllDoctors(exchange);
                } else if ("POST".equals(method) && "/api/doctors".equals(path)) {
                    handleCreateDoctor(exchange);
                } else {
                    sendErrorResponse(exchange, 404, "Endpoint not found");
                }
            }
        });

        server.createContext("/api/doctors/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String doctorId = extractIdFromPath(path, "/api/doctors/");

                if (doctorId == null) {
                    sendErrorResponse(exchange, 400, "Doctor ID is required");
                    return;
                }

                switch (method) {
                    case "GET":
                        handleGetDoctor(exchange, doctorId);
                        break;
                    case "PUT":
                        handleUpdateDoctor(exchange, doctorId);
                        break;
                    case "DELETE":
                        handleDeleteDoctor(exchange, doctorId);
                        break;
                    default:
                        sendErrorResponse(exchange, 405, "Method not allowed");
                }
            }
        });
    }

    private void setupAppointmentEndpoints() {
        server.createContext("/api/appointments", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();

                if ("GET".equals(method) && "/api/appointments".equals(path)) {
                    handleGetAllAppointments(exchange);
                } else if ("POST".equals(method) && "/api/appointments".equals(path)) {
                    handleCreateAppointment(exchange);
                } else {
                    sendErrorResponse(exchange, 404, "Endpoint not found");
                }
            }
        });

        server.createContext("/api/appointments/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String appointmentId = extractIdFromPath(path, "/api/appointments/");

                if (appointmentId == null) {
                    sendErrorResponse(exchange, 400, "Appointment ID is required");
                    return;
                }

                if (path.endsWith("/complete")) {
                    handleCompleteAppointment(exchange, appointmentId);
                    return;
                }

                switch (method) {
                    case "GET":
                        handleGetAppointment(exchange, appointmentId);
                        break;
                    case "PUT":
                        handleUpdateAppointment(exchange, appointmentId);
                        break;
                    case "DELETE":
                        handleDeleteAppointment(exchange, appointmentId);
                        break;
                    default:
                        sendErrorResponse(exchange, 405, "Method not allowed");
                }
            }
        });
    }

    private void setupBillEndpoints() {
        server.createContext("/api/bills", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();

                if ("GET".equals(method) && "/api/bills".equals(path)) {
                    handleGetAllBills(exchange);
                } else if ("POST".equals(method) && "/api/bills".equals(path)) {
                    handleCreateBill(exchange);
                } else {
                    sendErrorResponse(exchange, 404, "Endpoint not found");
                }
            }
        });

        server.createContext("/api/bills/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String billId = extractIdFromPath(path, "/api/bills/");

                if (billId == null) {
                    sendErrorResponse(exchange, 400, "Bill ID is required");
                    return;
                }

                if (path.endsWith("/pay")) {
                    handlePayBill(exchange, billId);
                    return;
                }

                switch (method) {
                    case "GET":
                        handleGetBill(exchange, billId);
                        break;
                    case "PUT":
                        handleUpdateBill(exchange, billId);
                        break;
                    case "DELETE":
                        handleDeleteBill(exchange, billId);
                        break;
                    default:
                        sendErrorResponse(exchange, 405, "Method not allowed");
                }
            }
        });
    }

    private void setupUserEndpoints() {
        server.createContext("/api/users", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();

                if ("GET".equals(method) && "/api/users".equals(path)) {
                    handleGetAllUsers(exchange);
                } else if ("POST".equals(method) && "/api/users".equals(path)) {
                    handleCreateUser(exchange);
                } else {
                    sendErrorResponse(exchange, 404, "Endpoint not found");
                }
            }
        });

        server.createContext("/api/auth/login", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    handleLogin(exchange);
                } else {
                    sendErrorResponse(exchange, 405, "Method not allowed");
                }
            }
        });
    }

    // Patient handlers
    private void handleGetAllPatients(HttpExchange exchange) throws IOException {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            sendJsonResponse(exchange, 200, patients);
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error fetching patients: " + e.getMessage());
        }
    }

    private void handleGetPatient(HttpExchange exchange, String patientId) throws IOException {
        try {
            Patient patient = patientDAO.getPatientById(patientId);
            if (patient != null) {
                sendJsonResponse(exchange, 200, patient);
            } else {
                sendErrorResponse(exchange, 404, "Patient not found");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error fetching patient: " + e.getMessage());
        }
    }

    private void handleCreatePatient(HttpExchange exchange) throws IOException {
        try {
            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            Patient patient = new Patient(
                    jsonObject.get("patientId").getAsString(),
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("age").getAsInt(),
                    jsonObject.get("gender").getAsString(),
                    jsonObject.get("address").getAsString(),
                    jsonObject.get("phone").getAsString()
            );

            boolean success = patientDAO.insertPatient(patient);
            if (success) {
                sendJsonResponse(exchange, 201, patient);
            } else {
                sendErrorResponse(exchange, 500, "Failed to create patient");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleUpdatePatient(HttpExchange exchange, String patientId) throws IOException {
        try {
            System.out.println("=== UPDATE PATIENT DEBUG ===");
            System.out.println("Attempting to update patient with ID: " + patientId);

            // First check if patient exists
            Patient existingPatient = patientDAO.getPatientById(patientId);
            System.out.println("Existing patient found: " + (existingPatient != null));

            if (existingPatient == null) {
                System.out.println("Patient not found, returning 404 error");
                sendErrorResponse(exchange, 404, "Patient with ID " + patientId + " not found");
                return;
            }

            System.out.println("Patient exists, proceeding with update...");
            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            // Create updated patient object with existing ID
            Patient patient = new Patient(
                    patientId, // Use the ID from URL, not from request body
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("age").getAsInt(),
                    jsonObject.get("gender").getAsString(),
                    jsonObject.get("address").getAsString(),
                    jsonObject.get("phone").getAsString()
            );

            // Preserve existing medical data
            for (String allergy : existingPatient.getAllergies()) {
                patient.addAllergy(allergy);
            }
            for (Medication med : existingPatient.getMedications()) {
                patient.addMedication(med);
            }
            for (MedicalRecord record : existingPatient.getMedicalHistory()) {
                patient.addMedicalRecord(record);
            }

            System.out.println("Calling patientDAO.updatePatient()...");
            boolean success = patientDAO.updatePatient(patient);
            System.out.println("Update result: " + success);

            if (success) {
                System.out.println("Sending success response");
                sendJsonResponse(exchange, 200, patient);
            } else {
                System.out.println("Sending failure response");
                sendErrorResponse(exchange, 500, "Failed to update patient");
            }
        } catch (Exception e) {
            System.err.println("Exception in handleUpdatePatient: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleDeletePatient(HttpExchange exchange, String patientId) throws IOException {
        try {
            // First check if patient exists
            Patient existingPatient = patientDAO.getPatientById(patientId);
            if (existingPatient == null) {
                sendErrorResponse(exchange, 404, "Patient with ID " + patientId + " not found");
                return;
            }

            boolean success = patientDAO.deletePatient(patientId);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Patient " + patientId + " deleted successfully");
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 500, "Failed to delete patient");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error deleting patient: " + e.getMessage());
        }
    }

    // Doctor handlers
    private void handleGetAllDoctors(HttpExchange exchange) throws IOException {
        try {
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            sendJsonResponse(exchange, 200, doctors);
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error fetching doctors: " + e.getMessage());
        }
    }

    private void handleGetDoctor(HttpExchange exchange, String doctorId) throws IOException {
        try {
            Doctor doctor = doctorDAO.getDoctorById(doctorId);
            if (doctor != null) {
                sendJsonResponse(exchange, 200, doctor);
            } else {
                sendErrorResponse(exchange, 404, "Doctor not found");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error fetching doctor: " + e.getMessage());
        }
    }

    private void handleCreateDoctor(HttpExchange exchange) throws IOException {
        try {
            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            Doctor doctor = new Doctor(
                    jsonObject.get("doctorId").getAsString(),
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("specialization").getAsString(),
                    jsonObject.get("availability").getAsString()
            );

            boolean success = doctorDAO.insertDoctor(doctor);
            if (success) {
                sendJsonResponse(exchange, 201, doctor);
            } else {
                sendErrorResponse(exchange, 500, "Failed to create doctor");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleUpdateDoctor(HttpExchange exchange, String doctorId) throws IOException {
        try {
            // First check if doctor exists
            Doctor existingDoctor = doctorDAO.getDoctorById(doctorId);
            if (existingDoctor == null) {
                sendErrorResponse(exchange, 404, "Doctor with ID " + doctorId + " not found");
                return;
            }

            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            Doctor doctor = new Doctor(
                    doctorId, // Use the ID from URL
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("specialization").getAsString(),
                    jsonObject.get("availability").getAsString()
            );

            boolean success = doctorDAO.updateDoctor(doctor);
            if (success) {
                sendJsonResponse(exchange, 200, doctor);
            } else {
                sendErrorResponse(exchange, 500, "Failed to update doctor");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleDeleteDoctor(HttpExchange exchange, String doctorId) throws IOException {
        try {
            boolean success = doctorDAO.deleteDoctor(doctorId);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Doctor deleted successfully");
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 404, "Doctor not found");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error deleting doctor: " + e.getMessage());
        }
    }

    // Appointment handlers
    private void handleGetAllAppointments(HttpExchange exchange) throws IOException {
        try {
            List<Appointment> appointments = appointmentDAO.getAllAppointments();
            sendJsonResponse(exchange, 200, appointments);
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error fetching appointments: " + e.getMessage());
        }
    }

    private void handleGetAppointment(HttpExchange exchange, String appointmentId) throws IOException {
        try {
            Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
            if (appointment != null) {
                sendJsonResponse(exchange, 200, appointment);
            } else {
                sendErrorResponse(exchange, 404, "Appointment not found");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error fetching appointment: " + e.getMessage());
        }
    }

    private void handleCreateAppointment(HttpExchange exchange) throws IOException {
        try {
            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            Appointment appointment = new Appointment(
                    jsonObject.get("appointmentId").getAsString(),
                    jsonObject.get("patientId").getAsString(),
                    jsonObject.get("doctorId").getAsString(),
                    jsonObject.get("date").getAsString(),
                    jsonObject.get("time").getAsString(),
                    jsonObject.get("description").getAsString()
            );

            boolean success = appointmentDAO.insertAppointment(appointment);
            if (success) {
                sendJsonResponse(exchange, 201, appointment);
            } else {
                sendErrorResponse(exchange, 500, "Failed to create appointment");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleCompleteAppointment(HttpExchange exchange, String appointmentId) throws IOException {
        try {
            // First check if appointment exists
            Appointment existingAppointment = appointmentDAO.getAppointmentById(appointmentId);
            if (existingAppointment == null) {
                sendErrorResponse(exchange, 404, "Appointment with ID " + appointmentId + " not found");
                return;
            }

            // Check if already completed
            if (existingAppointment.isCompleted()) {
                sendErrorResponse(exchange, 400, "Appointment " + appointmentId + " is already completed");
                return;
            }

            boolean success = appointmentDAO.markAppointmentCompleted(appointmentId);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Appointment " + appointmentId + " marked as completed");
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 500, "Failed to complete appointment");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error completing appointment: " + e.getMessage());
        }
    }

    private void handleUpdateAppointment(HttpExchange exchange, String appointmentId) throws IOException {
        try {
            // First check if appointment exists
            Appointment existingAppointment = appointmentDAO.getAppointmentById(appointmentId);
            if (existingAppointment == null) {
                sendErrorResponse(exchange, 404, "Appointment with ID " + appointmentId + " not found");
                return;
            }

            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            Appointment appointment = new Appointment(
                    appointmentId, // Use the ID from URL
                    jsonObject.get("patientId").getAsString(),
                    jsonObject.get("doctorId").getAsString(),
                    jsonObject.get("date").getAsString(),
                    jsonObject.get("time").getAsString(),
                    jsonObject.get("description").getAsString()
            );

            // Preserve completion status
            appointment.setCompleted(existingAppointment.isCompleted());

            boolean success = appointmentDAO.updateAppointment(appointment);
            if (success) {
                sendJsonResponse(exchange, 200, appointment);
            } else {
                sendErrorResponse(exchange, 500, "Failed to update appointment");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleDeleteAppointment(HttpExchange exchange, String appointmentId) throws IOException {
        try {
            boolean success = appointmentDAO.deleteAppointment(appointmentId);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Appointment deleted successfully");
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 404, "Appointment not found");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error deleting appointment: " + e.getMessage());
        }
    }

    // Bill handlers
    private void handleGetAllBills(HttpExchange exchange) throws IOException {
        try {
            List<Bill> bills = billDAO.getAllBills();
            sendJsonResponse(exchange, 200, bills);
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error fetching bills: " + e.getMessage());
        }
    }

    private void handleGetBill(HttpExchange exchange, String billId) throws IOException {
        try {
            Bill bill = billDAO.getBillById(billId);
            if (bill != null) {
                sendJsonResponse(exchange, 200, bill);
            } else {
                sendErrorResponse(exchange, 404, "Bill not found");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error fetching bill: " + e.getMessage());
        }
    }

    private void handleCreateBill(HttpExchange exchange) throws IOException {
        try {
            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            Bill bill = new Bill(
                    jsonObject.get("billId").getAsString(),
                    jsonObject.get("patientId").getAsString(),
                    jsonObject.get("amount").getAsDouble(),
                    jsonObject.get("description").getAsString()
            );

            boolean success = billDAO.insertBill(bill);
            if (success) {
                sendJsonResponse(exchange, 201, bill);
            } else {
                sendErrorResponse(exchange, 500, "Failed to create bill");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handlePayBill(HttpExchange exchange, String billId) throws IOException {
        try {
            // First check if bill exists
            Bill existingBill = billDAO.getBillById(billId);
            if (existingBill == null) {
                sendErrorResponse(exchange, 404, "Bill with ID " + billId + " not found");
                return;
            }

            // Check if already paid
            if (existingBill.isPaid()) {
                sendErrorResponse(exchange, 400, "Bill " + billId + " is already paid");
                return;
            }

            boolean success = billDAO.markBillPaid(billId);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Bill " + billId + " marked as paid");
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 500, "Failed to pay bill");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error paying bill: " + e.getMessage());
        }
    }

    private void handleUpdateBill(HttpExchange exchange, String billId) throws IOException {
        try {
            // First check if bill exists
            Bill existingBill = billDAO.getBillById(billId);
            if (existingBill == null) {
                sendErrorResponse(exchange, 404, "Bill with ID " + billId + " not found");
                return;
            }

            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            Bill bill = new Bill(
                    billId, // Use the ID from URL
                    jsonObject.get("patientId").getAsString(),
                    jsonObject.get("amount").getAsDouble(),
                    jsonObject.get("description").getAsString()
            );

            // Preserve payment status
            bill.setPaid(existingBill.isPaid());

            boolean success = billDAO.updateBill(bill);
            if (success) {
                sendJsonResponse(exchange, 200, bill);
            } else {
                sendErrorResponse(exchange, 500, "Failed to update bill");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleDeleteBill(HttpExchange exchange, String billId) throws IOException {
        try {
            boolean success = billDAO.deleteBill(billId);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Bill deleted successfully");
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 404, "Bill not found");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error deleting bill: " + e.getMessage());
        }
    }

    // User handlers
    private void handleGetAllUsers(HttpExchange exchange) throws IOException {
        try {
            Map<String, User> users = userDAO.getAllUsers();
            sendJsonResponse(exchange, 200, users.values());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error fetching users: " + e.getMessage());
        }
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        try {
            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            User user = new User(
                    jsonObject.get("username").getAsString(),
                    jsonObject.get("password").getAsString(),
                    UserRole.valueOf(jsonObject.get("role").getAsString())
            );

            boolean success = userDAO.insertUser(user);
            if (success) {
                sendJsonResponse(exchange, 201, user);
            } else {
                sendErrorResponse(exchange, 500, "Failed to create user");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        try {
            String requestBody = getRequestBody(exchange);
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            String username = jsonObject.get("username").getAsString();
            String password = jsonObject.get("password").getAsString();

            User user = userDAO.authenticateUser(username, password);
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("user", user);
                sendJsonResponse(exchange, 200, response);
            } else {
                sendErrorResponse(exchange, 401, "Invalid credentials");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid request: " + e.getMessage());
        }
    }

    // Utility methods
    private String extractIdFromPath(String path, String prefix) {
        if (path.startsWith(prefix)) {
            String remaining = path.substring(prefix.length());
            int slashIndex = remaining.indexOf('/');
            if (slashIndex == -1) {
                return remaining;
            } else {
                return remaining.substring(0, slashIndex);
            }
        }
        return null;
    }

    private String getRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String jsonResponse = gson.toJson(data);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(jsonResponse.getBytes());
        outputStream.close();
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("status", String.valueOf(statusCode));
        sendJsonResponse(exchange, statusCode, error);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("REST API Server stopped");
        }
    }

    public static void main(String[] args) {
        System.out.println("🏥 Hospital Management System - REST API Server");
        System.out.println("================================================");

        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                System.out.println("Using custom port: " + port);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + args[0] + ". Using default port 8080.");
            }
        }

        RestApiServer server = new RestApiServer();
        server.start(port);

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 Shutting down server...");
            server.stop();
            DatabaseConnection.getInstance().closeConnection();
            System.out.println("✅ Server shutdown complete.");
        }));

        System.out.println("\n⚡ Server is running. Press Ctrl+C to stop.");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("Server interrupted.");
        }
    }

    private void handleDeleteAllPatients(HttpExchange exchange) throws IOException {
        try {
            // Get all patients first
            List<Patient> patients = patientDAO.getAllPatients();
            int deletedCount = 0;

            // Delete each patient
            for (Patient patient : patients) {
                boolean success = patientDAO.deletePatient(patient.getPatientId());
                if (success) {
                    deletedCount++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Deleted " + deletedCount + " patients");
            response.put("deletedCount", deletedCount);
            sendJsonResponse(exchange, 200, response);

        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error deleting all patients: " + e.getMessage());
        }
    }
}
