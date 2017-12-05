package itp341.liang.briana.finalproject.model.objects;

import java.util.Calendar;

/**
 * Fluid class
 */

public class Fluid extends NamedObject {
    private static final long serialVersionUID = 5L;

    private double amount;
    private Calendar timestamp = Calendar.getInstance();

    public Fluid(String name, double amount) {
        super(name);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }
}
