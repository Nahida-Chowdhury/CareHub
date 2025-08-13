package org.example;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.io.Serializable;

class Medication implements Serializable {
    private String name;
    private String dosage;
    private String frequency;
    @BsonProperty("start_date")
    private String startDate;

    @BsonProperty("end_date")
    private String endDate;

    @BsonProperty("is_active")
    private boolean isActive;

    public Medication(String name, String dosage, String frequency, String startDate, String endDate) {
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = true;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return name + " - " + dosage + " (" + frequency + ")";
    }
}
