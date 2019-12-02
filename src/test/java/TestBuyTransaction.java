import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBuyTransaction {

    private Transaction transaction;

    private void setUp() {
        transaction = TestUtils.getTestBuyTransaction();
    }

    @Test
    void testToString() {
        setUp();
        String expected = "ID: 1\n" +
                "State: DRAFT\n" +
                "Customer: Jake Strang\n" +
                "Vehicle: 2015 Mazda 6\n" +
                "Price: $3200.12\n" +
                "Start Date: Tue Dec 01 00:00:00 EST 2020\n" +
                "Activation Date: null\n" +
                "Warranty Price: $50.12\n" +
                "Type: Buy";
        assertEquals(expected, transaction.toString());
    }

    @Test
    void testCalculatePrice() {
        setUp();
        assertEquals(3200.12, Math.round(transaction.getPrice() * 100.0) / 100.0);
    }
}
