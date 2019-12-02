import java.util.Date;

public class RentTransaction extends Transaction {

    private int days;

    public RentTransaction(int id, Customer customer, Vehicle vehicle, Date startDate, int days) {
        super(id, customer, vehicle, startDate);
        this.days = days;
        labelType();
        calculatePrice();
    }

    protected void calculatePrice() {
        price = (float) (days * vehicle.getType().getFee());
    }

    @Override
    protected void labelType() {
        this.type = "rent";
    }

    public int getDays() {
        return days;
    }

    /**
     * Update the number of days and recalculate the transaction price
     */
    public void setDays(int days) {
        this.days = days;
        // Recalculate the price
        calculatePrice();
    }

    @Override
    public String toString() {
        return super.toString() + "\nDays: " + days + "\nType: Rent";
    }

}
