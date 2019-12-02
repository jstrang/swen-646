import java.util.Date;

public class LeaseTransaction extends Transaction {

    private int months;

    public LeaseTransaction(int id, Customer customer, Vehicle vehicle, Date startDate, int months) {
        super(id, customer, vehicle, startDate);
        this.months = months;
        this.type = "lease";
        calculatePrice();
    }

    /**
     * Calculates the price using the formula:
     * 1% of vehicle price * number of months of lease
     */
    protected void calculatePrice() {
        price = (float) 0.01 * vehicle.getPrice() * months;
    }

    @Override
    protected void labelType() {
        this.type = "lease";
    }

    public int getMonths() {
        return months;
    }

    /**
     * Set the months attribute. Recalculate the price with the new months
     */
    public void setMonths(int months) {
        this.months = months;
        // Recalculate the price
        calculatePrice();
    }

    public String toString() {
        return super.toString() + "\n" + "Months: " + months + "\nType: Lease";
    }
}
