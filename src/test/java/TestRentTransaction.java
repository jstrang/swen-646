import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRentTransaction {

    private Transaction transaction;

    private void setUp() {
        transaction = TestUtils.getTestRentTransaction();
    }

    @Test
    void testToString() {
        setUp();
        String expected = "ID: 1\n" +
                "State: DRAFT\n" +
                "Customer: Jake Strang\n" +
                "Vehicle: 2015 Mazda 6\n" +
                "Price: $3999.00\n" +
                "Start Date: Tue Dec 01 00:00:00 EST 2020\n" +
                "Activation Date: null\n" +
                "Days: 100\n" +
                "Type: Rent";
        assertEquals(expected, transaction.toString());
    }

    @Test
    void testCalculatePrice() {
        setUp();
        assertEquals(3999, transaction.getPrice());
    }
}
