package org.example;

import java.io.Serializable;

class Medication implements Serializable {
    private String name;
    private String dosage;
    private String frequency;
    private String startDate;
    private String endDate;
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
    public String getDosage() { return dosage; }
    public String getFrequency() { return frequency; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }

    public void setActive(boolean active) { this.isActive = active; }

    @Override
    public String toString() {
        return name + " - " + dosage + " (" + frequency + ")";
    }
}
