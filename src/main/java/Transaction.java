import exceptions.InvalidTransactionException;

import java.util.Date;

public abstract class Transaction {

    final protected int id;
    protected TransactionState state;
    protected Customer customer;
    protected Vehicle vehicle;
    protected float price;
    protected Date startDate;
    protected Date activationDate;
    protected String type;

    public Transaction(int id, Customer customer, Vehicle vehicle, Date startDate) {
        this.id = id;
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.state = TransactionState.DRAFT;
    }

    /**
     * Switch the transction's state from DRAFT to ACTIVE if it is valid to do so
     */
    public void activate() {
        Date today = new Date();
        if (
                state != TransactionState.ACTIVE // State must not already be active
                && today.before(startDate) // Start date must be in the future
        ) {
            state = TransactionState.ACTIVE;
            activationDate = today;
        } else {
            throw new InvalidTransactionException("Transaction with ID " + id + "cannot be activated; must be before start date and in draft state");
        }
    }

    public int getId() {
        return id;
    }

    public TransactionState getState() {
        return state;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Update the vehicle for this transaction and recalculate the price
     */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        // Recalculate the price
        calculatePrice();
    }

    public float getPrice() {
        return price;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected abstract void calculatePrice();

    // This is a little strange, but gson doesn't deserialize the subclass differentiation field so we need this to load more than once
    protected abstract void labelType();

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ").append(id).append("\n")
                .append("State: ").append(state.toString()).append("\n")
                .append("Customer: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append("\n")
                .append("Vehicle: ").append(vehicle.getYear()).append(" ").append(vehicle.getMake()).append(" ").append(vehicle.getModel()).append("\n")
                .append("Price: $").append(String.format(java.util.Locale.US, "%.2f", price)).append("\n")
                .append("Start Date: ").append(startDate).append("\n")
                .append("Activation Date: ").append(activationDate);
        return builder.toString();
    }

}
