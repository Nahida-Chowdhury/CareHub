package org.example;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Patient implements Serializable {

    private ObjectId id;

    @BsonProperty("patient_id")
    private String patientId;
    private String name;
    private int age;
    private String gender;
    private String address;
    private String phone;

    @BsonProperty("medical_history")
    private List<MedicalRecord> medicalHistory;
    private List<String> allergies;
    private List<Medication> medications;

    public Patient() {} // Required no-arg constructor

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

    // Getters and setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<MedicalRecord> getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(List<MedicalRecord> medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public List<Medication> getMedications() { return medications; }
    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

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
    @Override
    public String toString() {
        return this.name + " (" + this.patientId + ")";
    }
}
