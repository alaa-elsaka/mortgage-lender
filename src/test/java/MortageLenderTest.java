import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MortageLenderTest {

    MortageLenderService mortageLenderService = new MortageLenderService();

    @Test
    public void whenCheckAvailableFunds(){

        double result = mortageLenderService.checkAvailableFunds();
        assertEquals(0, result);

    }

    /**
     * Note for refactoring -- test for negative deposits
     */
    @Test
    public void whenDepositFunds(){
        mortageLenderService.depositFunds(50000);
        double result = mortageLenderService.checkAvailableFunds();
        assertEquals(50000.0, result);
    }


    @Test
    public void whenQualifyLoan(){
        Applicant applicant = new Applicant(21,700,100000);
        applicant.submit(250000);
        String result = mortageLenderService.qualify(applicant);

        assertTrue(result.equals("qualified"));
    }


}
