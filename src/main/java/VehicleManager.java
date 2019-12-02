import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exceptions.InvalidOperationException;
import exceptions.InvalidTransactionException;
import exceptions.InvalidVehicleException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class VehicleManager {

    // Create a type adapter to deserialize the correct subclass based on the "type" field
    private static final RuntimeTypeAdapterFactory<Transaction> typeFactory = RuntimeTypeAdapterFactory.of(Transaction.class, "type")
            .registerSubtype(RentTransaction.class, "rent")
            .registerSubtype(BuyTransaction.class, "buy")
            .registerSubtype(LeaseTransaction.class, "lease");
    private static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();

    // I would normally make these private and final, but for the purposes of the test scenarios of this assignment
    // I'm making them accessible so that it's easy to change them to a nonexistent file and demo the exception
    static File transactionsFile = new File("transactions.json");
    static File vehiclesFile = new File("vehicles.json");

    private ArrayList<Vehicle> vehicles;

    /**
     * Creates a new instance of the VehicleManger class, which serves as the interface into the system.
     */
    public VehicleManager() {

        // Preemptively load vehicles into an in-memory ArrayList
        try {

            // Parse vehicle list from json file into an arraylist
            vehicles = gson.fromJson(new FileReader(vehiclesFile), new TypeToken<ArrayList<Vehicle>>(){}.getType());

            // If file was empty, list comes back null so we need to initialize an empty list of vehicles
            if (vehicles == null) {
                vehicles = new ArrayList<Vehicle>();
            }

        } catch (FileNotFoundException e) {
            // Unable to load file, throw an exception
            throw new InvalidOperationException("Unable to load vehicles: " + e.getMessage());
        }


    }

    /**
     * Takes in a transaction object, checks that it is valid, and appends it to the list in the transactions file.
     * If a transaction with the same ID already exists in the file, this will update it as well.
     *
     * @param transaction new Transaction object passed in to be appended and persisted to the list
     */
    public void addTransaction(final Transaction transaction) {

        // Load transactions from file
        ArrayList<Transaction> transactions = getTransactions().asList();

        // Check that transaction's vehicle is in the available vehicles list by VIN
        if (!vehicles.contains(transaction.getVehicle())) {
            throw new InvalidTransactionException("Vehicle for this transaction is not in the list of available vehicles");
        }

        // Check that no duplicate transaction IDs exist
        if (getTransactions().withID(transaction.getId()).asList().size() > 0) {
            throw new InvalidTransactionException("Transaction with this ID already exists");
        }

        // Append transaction to the list
        transactions.add(transaction);

        // Persist transactions to file
        persistTransactions(transactions);
    }

    /**
     * Update a transaction with any changes that have been applied to it. Validate that it is valid to update the
     * transaction (DRAFT mode for the version in the system) and
     */
    public void updateTransaction(final Transaction transaction) {

        // Load all transactions from file
        ArrayList<Transaction> transactions = getTransactions().asList();

        // Get existing version of this transaction, or throw exception if it doesn't exist
        Transaction oldTransaction;
        try {
            oldTransaction = getTransactions().withID(transaction.getId()).asList().get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidTransactionException("No existing transaction with ID: " + transaction.getId());
        }

        // If transaction state retrieved is ACTIVE and either the start date or vehicle have been changed, throw an
        // exception. These can only be changed in draft mode
        if (oldTransaction.getState() == TransactionState.ACTIVE &&
                (!oldTransaction.getStartDate().equals(transaction.getStartDate())
                 || !oldTransaction.getVehicle().getVin().equals(transaction.getVehicle().getVin()))) {
            throw new InvalidTransactionException("Cannot update vehicle or start date for active transaction");
        }

        // If vehicle is not in the list of available vehicles, throw exception
        if (!vehicles.contains(transaction.vehicle)) {
            throw new InvalidVehicleException("Vehicle on transaction with ID " + transaction.getId() + " is not in the list of available vehicles");
        }

        // Filter old transaction out of loaded list if this is an update
        transactions.removeIf(t -> t.getId() == transaction.id);

        // Append transaction to the list
        transactions.add(transaction);

        // Persist transactions to file
        persistTransactions(transactions);
    }

    /**
     * Change the transaction state of an existing transaction to Active by object
     */
    public void activateTransaction(Transaction transaction) {

        // Activate the transaction. It will perform validation
        transaction.activate();

        // Update the record in the transactions file
        updateTransaction(transaction);
    }

    /**
     * Change the transaction state of an existing transaction to Active by ID
     */
    public void activateTransaction(int id) {
        // Query for transaction with this ID
        try {
            Transaction transaction = getTransactions().withID(id).asList().get(0);
            activateTransaction(transaction);
        } catch (IndexOutOfBoundsException e) {
            // Search came back empty, there is no transaction with this ID
            throw new InvalidTransactionException("No transaction with ID " + id + " was found");
        }
    }

    /**
     * Remove a transaction from the existing transactions if it is valid (DRAFT or before start date)
     */
    public void cancelTransaction(Transaction transaction) {
        // Load transactions from file
        cancelTransaction(transaction.getId());
    }

    /**
     * Remove a transaction from the existing transactions if it is valid (DRAFT or before start date)
     */
    public void cancelTransaction(int id) {

        // Get all transactions. We can't just query for the one because we need to alter the full list to remove it
        ArrayList<Transaction> transactions = getTransactions().asList();

        // Find the transaction to cancel
        Transaction transaction = transactions.stream().filter(t -> t.getId() == id).findFirst().orElse(null);

        // If null, no transaction existed with that ID
        if (transaction == null) {
            throw new InvalidTransactionException("No active transaction found in system with ID: " + id);
        }

        // Transaction must be in draft, or before the start date if it is active in order to cancel
        if (transaction.state == TransactionState.DRAFT || new Date().before(transaction.getStartDate())) {
            transactions.remove(transaction);
        }

        // Update the transactions file
        persistTransactions(transactions);

    }

    /**
     * Entry point to querying for transactions by creating a query object
     */
    public TransactionQuery getTransactions() {
        return new TransactionQuery();
    }

    /**
     * Add a vehicle object to the available vehicles list
     */
    public void addVehicle(Vehicle vehicle) {
        // Add vehicle
        vehicles.add(vehicle);

        // Persist to file
        persistVehicles();
    }

    /**
     * Create a vehicle object from a json file and add it to the available vehicles list
     */
    public void addVehicle(String fileName) {
        // Create new object from file
        Vehicle vehicle = new Vehicle(fileName);

        // Add new object to available vehicles
        addVehicle(vehicle);
    }

    /**
     * Return list of all available vehicles
     */
    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * Remove a vehicle object from the available vehicles list
     */
    public void removeVehicle(Vehicle vehicle) {
        if (!vehicles.remove(vehicle)) {
            throw new InvalidVehicleException("Vehicle was not in the list of available vehicles, no action taken");
        }
    }

    /**
     * Remove a vehicle from the available vehicles list by VIN
     */
    public void removeVehicle(String vin) {
        Vehicle vehicle = vehicles.stream().filter(v -> v.getVin().equals(vin)).findFirst().orElse(null);
        if (vehicle == null) {
            throw new InvalidVehicleException("No vehicle with VIN " + vin + " in list of available vehicles");
        }
    }

    /**
     * Save the list of vehicles back out to file
     */
    private void persistVehicles() {
        try {
            FileWriter writer = new FileWriter(vehiclesFile);
            gson.toJson(vehicles, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new InvalidOperationException("Unable to write vehicles out to file: " + e.getMessage());
        }
    }

    /**
     * Replace the contents of the transactions file with the list provided here
     */
    private void persistTransactions(ArrayList<Transaction> transactions) {
        try {
            FileWriter writer = new FileWriter(transactionsFile);

            // We need to re-label each transaction with its type because deserialization drops the differentiator field
            transactions.forEach(Transaction::labelType);

            gson.toJson(transactions, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new InvalidOperationException("Unable to write transactions out to file: " + e.getMessage());
        }
    }

    /**
     * This is an inner class belonging to VehicleManager that is used to query for transactions by filter parameter.
     * Constructing it in this way allows seamless query patterns through the owning manager using a syntax like:
     * manager.getTransactions().withState(DRAFT).withEmail(jake@fake.null).asList().
     *
     * (NOTE for class: I know the requirements said one class per file, but I believe holding this as a static method
     * here follows separation of concerns most effectively. The TransactionQuery class operates as a standalone object
     * with attributes representing a set of filter parameters, but it should exist under the context of the manager
     * where it needs access to the transactions file, and this keeps it to one place instead of divided logic)
     */
    public static class TransactionQuery {
        private Integer id;
        private String email;
        private Date startDate;
        private TransactionState state;

        /**
         * Add a filter by email address
         */
        public TransactionQuery withEmail(String email) {
            this.email = email;
            return this;
        }

        /**
         * Add a filter by transaction ID
         */
        public TransactionQuery withID(int id) {
            this.id = id;
            return this;
        }

        /**
         * Add filter for transaction state
         */
        public TransactionQuery withState(TransactionState state) {
            this.state = state;
            return this;
        }

        /**
         * Add a filter for start date
         */
        public TransactionQuery withStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Return the results of a query using the currently set filters in the form of an ArrayList of Transactions
         */
        public ArrayList<Transaction> asList() {
            // Load the transaction file
            try {
                ArrayList<Transaction> transactions =  gson.fromJson(new FileReader(transactionsFile), new TypeToken<ArrayList<Transaction>>(){}.getType());

                // If the file is empty it will come back null, so create a new list
                if (transactions == null) {
                    return new ArrayList<Transaction>();
                }

                // Apply each filter that was set
                if (id != null) {
                    transactions.removeIf(t -> t.getId() != id);
                }
                if (email != null) {
                    transactions.removeIf(t -> !t.getCustomer().getEmail().equals(email));
                }
                if (startDate != null) {
                    transactions.removeIf(t -> !t.getStartDate().equals(startDate));
                }
                if (state != null) {
                    transactions.removeIf(t -> !t.getState().equals(state));
                }

                return transactions;
            } catch (FileNotFoundException e) {
                throw new InvalidOperationException("Unable to load transactions: " + e.getMessage());
            }
        }
    }

}
