import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLeaseTransaction {

    private Transaction transaction;

    private void setUp() {
        transaction = TestUtils.getTestLeaseTransaction();
    }

    @Test
    void testToString() {
        setUp();
        String expected = "ID: 1\n" +
                "State: DRAFT\n" +
                "Customer: Jake Strang\n" +
                "Vehicle: 2015 Mazda 6\n" +
                "Price: $360.00\n" +
                "Start Date: Tue Dec 01 00:00:00 EST 2020\n" +
                "Activation Date: null\n" +
                "Months: 12\n" +
                "Type: Lease";
        assertEquals(expected, transaction.toString());
    }

    @Test
    void testCalculatePrice() {
        setUp();
        assertEquals(360.0, transaction.getPrice());
    }
}
