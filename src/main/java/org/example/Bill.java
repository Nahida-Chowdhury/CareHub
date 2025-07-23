package org.example;

import java.io.Serializable;

class Bill implements Serializable {
    private String billId, patientId, description;
    private double amount;
    private boolean paid;

    public Bill(String billId, String patientId, double amount, String description) {
        this.billId = billId;
        this.patientId = patientId;
        this.amount = amount;
        this.description = description;
    }

    // Getters and setters
    public String getBillId() { return billId; }
    public String getPatientId() { return patientId; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
}
