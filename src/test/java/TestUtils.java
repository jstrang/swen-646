import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestUtils {
    private static Address address = new Address("123 1st St.", "Maryland", "55555",
                "USA", "Baltimore");
    private static Customer customer = new Customer("Jake", "Strang", address,
                "5551234567", "jake@notadomain.null");
    private static Vehicle vehicle = new Vehicle("Mazda", "6", "mnbvcxzkjhgfdsapo", 2015,
                3000, VehicleType.SEDAN);
    private static BuyTransaction buyTransaction = new BuyTransaction(1, customer, vehicle,
            new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), (float) 50.12);
    private static RentTransaction rentTransaction = new RentTransaction(1, customer, vehicle,
            new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), 100);
    private static LeaseTransaction leaseTransaction = new LeaseTransaction(1, customer, vehicle,
            new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), 12);

    static Address getTestAddress() {
        return address;
    }

    static Customer getTestCustomer() {
        return customer;
    }

    static Vehicle getTestVehicle() {
        return vehicle;
    }

    static BuyTransaction getTestBuyTransaction() {
        return buyTransaction;
    }

    static RentTransaction getTestRentTransaction() {
        return rentTransaction;
    }

    static LeaseTransaction getTestLeaseTransaction() {
        return leaseTransaction;
    }

}
