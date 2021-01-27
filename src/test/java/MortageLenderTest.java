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
        mortageLenderService.addApplicant(applicant);
        Request request = mortageLenderService.request(applicant,250000);
        Request result = mortageLenderService.qualify(request);
        String statusResult = result.getStatus();
        String qualificationResult = result.getQualification();

        assertTrue(statusResult.equals("qualified"));
        assertTrue(qualificationResult.equals("qualified"));

        applicant = new Applicant(30,700,50000);
        mortageLenderService.addApplicant(applicant);
         request = mortageLenderService.request(applicant,250000);
         result = mortageLenderService.qualify(request);
         statusResult = result.getStatus();
         qualificationResult = result.getQualification();

        assertTrue(statusResult.equals("qualified"));
        assertTrue(qualificationResult.equals("partially qualified"));


        applicant = new Applicant(30,600,100000);
        mortageLenderService.addApplicant(applicant);
        request = mortageLenderService.request(applicant,250000);
        result = mortageLenderService.qualify(request);
        statusResult = result.getStatus();
        qualificationResult = result.getQualification();

        assertTrue(statusResult.equals("denied"));
        assertTrue(qualificationResult.equals("not qualified"));

    }


}
