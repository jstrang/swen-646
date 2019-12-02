import java.util.Date;
import java.util.Locale;

public class BuyTransaction extends Transaction {

    private float warrantyPrice;

    public BuyTransaction(int id, Customer customer, Vehicle vehicle, Date startDate, float warrantyPrice) {
        super(id, customer, vehicle, startDate);
        this.warrantyPrice = warrantyPrice;
        this.type = "buy";
        calculatePrice();
    }

    /**
     * Calculates the price using the formula: car price + warranty + tax fee (5% of price)
     * Note that this will not round to two decimal places, we leave it to other systems to decide how to treat rounding
     */
    protected void calculatePrice() {
        price = (float) (vehicle.getPrice() * 1.05 + warrantyPrice);
    }

    @Override
    protected void labelType() {
        this.type = "buy";
    }

    public float getWarrantyPrice() {
        return warrantyPrice;
    }

    /**
     * Update the warranty price and recalculate the transaction price
     */
    public void setWarrantyPrice(float warrantyPrice) {
        this.warrantyPrice = warrantyPrice;
        // Recalculate the price
        calculatePrice();
    }

    @Override
    public String toString() {
        return super.toString() + "\nWarranty Price: $" + String.format(Locale.US, "%.2f", warrantyPrice) + "\nType: Buy";
    }
}
