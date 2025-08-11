package org.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Patient implements Serializable {
    private String patientId;
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phone;

    private List<MedicalRecord> medicalHistory;
    private List<String> allergies;
    private List<Medication> medications;

    public Patient(String patientId, String name, int age, String gender, String address, String phone) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phone = phone;

        this.medicalHistory = new ArrayList<>();
        this.allergies = new ArrayList<>();
        this.medications = new ArrayList<>();
    }

    // Existing getters
    public String getPatientId() { return patientId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    public List<MedicalRecord> getMedicalHistory() { return new ArrayList<>(medicalHistory); }
    public List<String> getAllergies() { return new ArrayList<>(allergies); }
    public List<Medication> getMedications() { return new ArrayList<>(medications); }

    public void addMedicalRecord(MedicalRecord record) {
        medicalHistory.add(record);
    }

    public void addAllergy(String allergy) {
        if (!allergies.contains(allergy)) {
            allergies.add(allergy);
        }
    }

    public void removeAllergy(String allergy) {
        allergies.remove(allergy);
    }

    public void addMedication(Medication medication) {
        medications.add(medication);
    }

    public void removeMedication(String medicationName) {
        medications.removeIf(med -> med.getName().equals(medicationName));
    }

    public String getLatestDiagnosis() {
        if (medicalHistory.isEmpty()) return "No diagnosis recorded";
        return medicalHistory.get(medicalHistory.size() - 1).getDiagnosis();
    }
}
