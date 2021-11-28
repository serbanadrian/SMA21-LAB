package model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Payment implements Serializable {

    public String timestamp;
    private double cost;
    private String name;
    private String type;

    public Payment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Payment(double cost, String name, String type) {
        this.timestamp = timestamp;
        this.cost = cost;
        this.name = name;
        this.type = type;
    }
    public Payment copy(){
        Payment copyPayment = new Payment(
                this.cost,
                this.name,
                this.type
        );

        copyPayment.timestamp = this.timestamp;
        return copyPayment;
    }
    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }
}
