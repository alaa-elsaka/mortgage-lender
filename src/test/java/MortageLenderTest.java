import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MortageLenderTest {

    Lender lender = new Lender(100000);

    @Test
    public void whenCheckAvailableFunds(){

        double result = lender.checkAvailableFunds();
        assertEquals(100000.0, result);

    }

    /**
     * Note for refactoring -- test for negative deposits
     */
    @Test
    public void whenDepositFunds(){
        lender.depositFunds(50000);

        double result = lender.checkAvailableFunds();
        assertEquals(150000.0, result);

    }


}
