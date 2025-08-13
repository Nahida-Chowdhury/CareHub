package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class BillDAO {
    private MongoCollection<Document> collection;

    public BillDAO() {
        this.collection = DatabaseConnection.getInstance()
                .getDatabase()
                .getCollection("bills");
    }

    // Create
    public boolean insertBill(Bill bill) {
        try {
            // Check if bill already exists
            if (getBillById(bill.getBillId()) != null) {
                System.err.println("Bill with ID " + bill.getBillId() + " already exists");
                return false;
            }

            Document doc = new Document("billId", bill.getBillId())
                    .append("patientId", bill.getPatientId())
                    .append("amount", bill.getAmount())
                    .append("description", bill.getDescription())
                    .append("paid", bill.isPaid());

            collection.insertOne(doc);
            System.out.println("Bill " + bill.getBillId() + " inserted successfully");
            return true;
        } catch (Exception e) {
            System.err.println("Error inserting bill: " + e.getMessage());
            return false;
        }
    }

    // Read
    public Bill getBillById(String billId) {
        try {
            Document doc = collection.find(eq("billId", billId)).first();
            if (doc != null) {
                return documentToBill(doc);
            }
        } catch (Exception e) {
            System.err.println("Error getting bill: " + e.getMessage());
        }
        return null;
    }

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                bills.add(documentToBill(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.err.println("Error getting all bills: " + e.getMessage());
        }
        return bills;
    }

    public List<Bill> getBillsByPatientId(String patientId) {
        List<Bill> bills = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find(eq("patientId", patientId)).iterator();
            while (cursor.hasNext()) {
                bills.add(documentToBill(cursor.next()));
            }
            cursor.close();
        } catch (Exception e) {
            System.err.println("Error getting bills by patient: " + e.getMessage());
        }
        return bills;
    }

    // Update - FIXED to check if record exists
    public boolean updateBill(Bill bill) {
        try {
            // First check if bill exists
            if (getBillById(bill.getBillId()) == null) {
                System.err.println("Cannot update: Bill with ID " + bill.getBillId() + " does not exist");
                return false;
            }

            Document updateDoc = new Document("$set", new Document()
                    .append("patientId", bill.getPatientId())
                    .append("amount", bill.getAmount())
                    .append("description", bill.getDescription())
                    .append("paid", bill.isPaid()));

            UpdateResult result = collection.updateOne(eq("billId", bill.getBillId()), updateDoc);

            if (result.getMatchedCount() > 0) {
                System.out.println("Bill " + bill.getBillId() + " updated successfully");
                return true;
            } else {
                System.err.println("No bill found with ID: " + bill.getBillId());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error updating bill: " + e.getMessage());
            return false;
        }
    }

    // Mark bill paid - FIXED to check if record exists
    public boolean markBillPaid(String billId) {
        try {
            // First check if bill exists
            if (getBillById(billId) == null) {
                System.err.println("Cannot pay: Bill with ID " + billId + " does not exist");
                return false;
            }

            Document updateDoc = new Document("$set", new Document("paid", true));
            UpdateResult result = collection.updateOne(eq("billId", billId), updateDoc);

            if (result.getMatchedCount() > 0) {
                System.out.println("Bill " + billId + " marked as paid");
                return true;
            } else {
                System.err.println("No bill found with ID: " + billId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error marking bill paid: " + e.getMessage());
            return false;
        }
    }

    // Delete - FIXED to check if record exists
    public boolean deleteBill(String billId) {
        try {
            // First check if bill exists
            if (getBillById(billId) == null) {
                System.err.println("Cannot delete: Bill with ID " + billId + " does not exist");
                return false;
            }

            DeleteResult result = collection.deleteOne(eq("billId", billId));

            if (result.getDeletedCount() > 0) {
                System.out.println("Bill " + billId + " deleted successfully");
                return true;
            } else {
                System.err.println("No bill found with ID: " + billId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting bill: " + e.getMessage());
            return false;
        }
    }

    // Check if bill exists
    public boolean billExists(String billId) {
        return getBillById(billId) != null;
    }

    // Helper method
    private Bill documentToBill(Document doc) {
        Bill bill = new Bill(
                doc.getString("billId"),
                doc.getString("patientId"),
                doc.getDouble("amount"),
                doc.getString("description")
        );
        bill.setPaid(doc.getBoolean("paid", false));
        return bill;
    }
}
