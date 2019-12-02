import exceptions.InvalidOperationException;
import exceptions.InvalidTransactionException;
import exceptions.InvalidVehicleException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class TestRequirements {

    private VehicleManager manager;

    private Transaction rentTransaction;
    private Transaction buyTransaction;
    private Transaction leaseTransaction;


    void setUp() {
        // clear the vehicles and transactions files
        try {
            new FileWriter(VehicleManager.vehiclesFile).close();
            new FileWriter(VehicleManager.transactionsFile).close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Create the manager
        manager = new VehicleManager();

        // Add a couple initial vehicles into the system
        manager.addVehicle(new Vehicle("Toyota", "Sienna", "qwertyuiopasdfgjk", 2008, 3200,
                VehicleType.TRUCK_OR_VAN));
        manager.addVehicle(new Vehicle("Ford", "Focus", "12345678901234567", 2004, 1800,
                VehicleType.SEDAN));
        manager.addVehicle(new Vehicle("Toyota", "Rav 4", "09876543210987654", 20017, 18000,
                VehicleType.SEDAN));
    }

    void addOneOfEachTransaction() {
        // Get all vehicles
        ArrayList<Vehicle> vehicles = manager.getVehicles();

        // Create and add rent transaction
        Vehicle vehicle = vehicles.stream().filter(v -> v.getVin().equals("qwertyuiopasdfgjk")).findFirst().orElse(null);
        if (vehicle == null) fail();
        rentTransaction = new RentTransaction(1, TestUtils.getTestCustomer(), vehicle,
                new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), 100);
        manager.addTransaction(rentTransaction);

        // Create and add lease transaction
        vehicle = vehicles.stream().filter(v -> v.getVin().equals("12345678901234567")).findFirst().orElse(null);
        if (vehicle == null) fail();
        leaseTransaction = new LeaseTransaction(2, TestUtils.getTestCustomer(), vehicle,
                new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), 12);
        manager.addTransaction(leaseTransaction);

        // Create and add buy transaction
        vehicle = vehicles.stream().filter(v -> v.getVin().equals("09876543210987654")).findFirst().orElse(null);
        if (vehicle == null) fail();
        buyTransaction = new BuyTransaction(3, TestUtils.getTestCustomer(), vehicle,
                new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), (float)240.50);
        manager.addTransaction(buyTransaction);
    }

    /**
     * Test that an InvalidOperationException is thrown if the vehicles file does not exist
     */
    @Test
    void scenarioOne() {
        // Set vehicle file to a non-existent file
        VehicleManager.vehiclesFile = new File("not_a_real_file.json");

        // Assert that an InvalidOperationException is thrown
        Exception e = assertThrows(InvalidOperationException.class, () -> {
            manager = new VehicleManager();
        });
        assertEquals("Unable to load vehicles: not_a_real_file.json (No such file or directory)", e.getMessage());

        // Set the file back to the correct file
        VehicleManager.vehiclesFile = new File("vehicles.json");
    }

    /**
     * Test that two created vehicles are properly loaded from file on manager creation. Print to console, verify
     * expected length of vehicles list, and check by VIN that both are in the list
     */
    @Test
    void scenarioTwo() {
        setUp();

        // Get list of loaded vehicles
        ArrayList<Vehicle> vehicles = manager.getVehicles();

        // Print each vehicle to console
        System.out.println("Printing vehicles from scenario two");
        vehicles.forEach(System.out::println);

        // Check there were two vehicles loaded
        assertEquals(3, vehicles.size());

        // Check that each of the vin numbers is in the returned vehicles
        assertTrue(vehicles.stream().anyMatch(v -> v.getVin().equals("qwertyuiopasdfgjk")));
        assertTrue(vehicles.stream().anyMatch(v -> v.getVin().equals("12345678901234567")));
    }

    /**
     * Create a new rent transaction, activate it, and retrieve by email to verify that it was correctly persisted to file
     */
    @Test
    void scenarioThree() {
        setUp();

        // Pick a vehicle out of the loaded list to use for this transaction
        ArrayList<Vehicle> vehicles = manager.getVehicles();
        Vehicle vehicle = vehicles.stream().filter(v -> v.getVin().equals("qwertyuiopasdfgjk")).findFirst().orElse(null);
        if (vehicle == null) fail();

        // Create and add rent transaction
        Transaction transaction = new RentTransaction(1, TestUtils.getTestCustomer(), vehicle,
                new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), 100);

        // Add the rent transaction
        manager.addTransaction(transaction);

        // Activate the transaction
        manager.activateTransaction(transaction);

        // Get transactions by email
        ArrayList<Transaction> transactions = manager.getTransactions().withEmail(TestUtils.getTestCustomer().getEmail()).asList();

        // Print the retrieved transaction to console
        System.out.println("Printing rent transaction created and retrieved for scenario 3");
        System.out.println(transactions.get(0));

        // Verify one transaction found for this email
        assertEquals(1, transactions.size());

        // Verify the found transaction has the correct ID
        assertEquals(1, transactions.get(0).getId());

    }

    /**
     * Create a new lease transaction, activate it, and retrieve by start date to verify that it was correctly persisted to file
     */
    @Test
    void scenarioFour() {
        setUp();

        // Pick a vehicle out of the loaded list to use for this transaction
        ArrayList<Vehicle> vehicles = manager.getVehicles();
        Vehicle vehicle = vehicles.stream().filter(v -> v.getVin().equals("qwertyuiopasdfgjk")).findFirst().orElse(null);
        if (vehicle == null) fail();

        // Create rent transaction
        Transaction transaction = new LeaseTransaction(1, TestUtils.getTestCustomer(), vehicle,
                new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), 12);

        // Add the rent transaction
        manager.addTransaction(transaction);

        // Activate the transaction
        manager.activateTransaction(transaction);

        // Get transactions by start date
        Date startDate = new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime();
        ArrayList<Transaction> transactions = manager.getTransactions().withStartDate(startDate).asList();

        // Print the retrieved transaction to console
        System.out.println("Printing lease transaction created and retrieved for scenario 4");
        System.out.println(transactions.get(0));

        // Verify one transaction found for this email
        assertEquals(1, transactions.size());

        // Verify the found transaction has the correct ID
        assertEquals(1, transactions.get(0).getId());

    }

    /**
     * Add one of each transaction type and request by DRAFT status to verify they were added
     */
    @Test
    void scenarioFive() {
        setUp();

        addOneOfEachTransaction();

        // Request all draft transactions
        ArrayList<Transaction> transactions = manager.getTransactions().withState(TransactionState.DRAFT).asList();

        // Print each transaction
        System.out.println("Printing all transactions created and retrieved for scenario 5");
        transactions.forEach(System.out::println);

        assertEquals(3, transactions.size());

    }

    /**
     * Add one of each transaction type. Activate one, cancel the other two. Validate that all succeeded
     */
    @Test
    void scenarioSix() {
        setUp();
        addOneOfEachTransaction();

        // Activate buy transaction
        manager.activateTransaction(buyTransaction);

        // Request transaction by ID
        Transaction retrievedTransaction = manager.getTransactions().withID(buyTransaction.getId()).asList().get(0);

        // Print out the retrieved transaction
        System.out.println("Printing retrieved activated buy transaction for scenario 6");
        System.out.println(retrievedTransaction);

        // Verify that the transaction was activated and has an activation date
        assertEquals(TransactionState.ACTIVE, retrievedTransaction.getState());
        assertNotNull(retrievedTransaction.getActivationDate());

        // Cancel lease and buy transactions
        manager.cancelTransaction(leaseTransaction);
        manager.cancelTransaction(buyTransaction);

        // Verify requesting by ID returns null
        assertEquals(0, manager.getTransactions().withID(leaseTransaction.getId()).asList().size());
        assertEquals(0, manager.getTransactions().withID(buyTransaction.getId()).asList().size());

        // Check that total number of transactions in file is 1
        assertEquals(1, manager.getTransactions().asList().size());

    }

    /**
     * Check that trying to activate a transaction that doesn't exist in the file throws an InvalidTransactionException
     */
    @Test
    void scenarioSeven() {
        setUp();
        addOneOfEachTransaction();

        assertThrows(InvalidTransactionException.class, () -> {
            manager.activateTransaction(999);
        });
    }

    /**
     * Test that Vehicle constructor throws an InvalidVehicleException if the VIN is too short
     */
    @Test
    void scenarioEight() {
        assertThrows(InvalidVehicleException.class, () -> {
            new Vehicle("Honda", "Civic", "shortvin", 2021, (float)50000, VehicleType.SEDAN);
        });
    }

    /**
     * Test that vehicles created with one manager are visible to a second (ie are persisted to file)
     */
    @Test
    void scenarioNine() {
        // New vehicles are added through the first manager within the setup function
        setUp();

        // Make sure a second vehicle manager also sees the new vehicles
        VehicleManager second = new VehicleManager();

        // Fetch list of vehicles through the second manager
        ArrayList<Vehicle> secondManagerVehicles = second.getVehicles();

        // Print full list of vehicles from second manager to console
        System.out.println("Printing all loaded vehicles from second manager for scenario nine");
        secondManagerVehicles.forEach(System.out::println);

        // Check that one of the vehicles created by the first manager is visible to the second
        assertTrue(secondManagerVehicles.stream().anyMatch(v -> v.getVin().equals("12345678901234567")));
    }

    @Test
    void scenarioTen() {
        setUp();

        // Add vehicle by filename (this creates it wish the Vehicle constructor by filename, and then adds it to the list)
        manager.addVehicle("test_vehicle.json");

        // Create a second vehicle manager
        VehicleManager second = new VehicleManager();

        // Fetch list of vehicles through the second manager
        ArrayList<Vehicle> secondManagerVehicles = second.getVehicles();

        // Print full list of vehicles from second manager to console
        System.out.println("Printing all loaded vehicles from second manager for scenario ten");
        secondManagerVehicles.forEach(System.out::println);

        // Make sure the new vehicle is visible to the second manager
        assertTrue(secondManagerVehicles.stream().anyMatch(v -> v.getVin().equals("12345678901234567")));

    }
}
