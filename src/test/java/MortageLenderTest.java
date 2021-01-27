import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

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

    @Test
    public void whenPrecessRequest(){
        mortageLenderService.depositFunds(100000);
        Applicant applicant = new Applicant(21,700,100000);
        mortageLenderService.addApplicant(applicant);
        Request request = mortageLenderService.request(applicant,125000);
        Request result = mortageLenderService.qualify(request);

        mortageLenderService.processRequest(request);

        assertTrue(result.getStatus().equals("on hold"));




        mortageLenderService.depositFunds(100000);
        mortageLenderService.addApplicant(applicant);
        request = mortageLenderService.request(applicant,125000);
        result = mortageLenderService.qualify(request);

        mortageLenderService.processRequest(request);

        assertTrue(result.getStatus().equals("approved"));
        double availableFunds = mortageLenderService.checkAvailableFunds();

        double expectedAvailableFunds = 75000;

        assertEquals(expectedAvailableFunds, availableFunds);
    }

    @Test
    public void whenPrecessNotQualifiedRequest() {
        mortageLenderService.depositFunds(100000);
        Applicant applicant = new Applicant(30,600,100000);
        mortageLenderService.addApplicant(applicant);
        Request request = mortageLenderService.request(applicant,250000);
        Request result = mortageLenderService.qualify(request);
        String processRequest = mortageLenderService.processRequest(request);
        assertTrue(processRequest.equals("This is a denied request"));
    }

    @Test
    public void checkAvailablePendingFundsChanges() {
        Applicant applicant = new Applicant(21,700,100000);
        mortageLenderService.depositFunds(200000);
        mortageLenderService.addApplicant(applicant);
        Request request = mortageLenderService.request(applicant,125000);
        mortageLenderService.qualify(request);

        mortageLenderService.processRequest(request);

        double availableFunds = mortageLenderService.checkAvailableFunds();

        double expectedAvailableFunds = 75000;

        assertEquals(expectedAvailableFunds, availableFunds);
        double pendingFunds = mortageLenderService.getPendingFunds();
        double expectedPendingFunds = 125000;
        assertEquals(expectedPendingFunds, pendingFunds);
    }

    @Test
    public void checkApplicantAcceptsLoan() {
        Applicant applicant = new Applicant(21,700,100000);
        mortageLenderService.depositFunds(200000);
        mortageLenderService.addApplicant(applicant);
        Request request = mortageLenderService.request(applicant,125000);
        mortageLenderService.qualify(request);

        mortageLenderService.processRequest(request);

        mortageLenderService.accept(applicant,request,true);

        double pendingFunds = mortageLenderService.getPendingFunds();
        double expectedPendingFunds = 0;
        String resultStatus = request.getStatus();
        assertTrue("Accept".equals(resultStatus));
        assertEquals(expectedPendingFunds,pendingFunds);

    }

    @Test
    public void checkApplicantRejectsLoan() {
        Applicant applicant = new Applicant(21,700,100000);
        mortageLenderService.depositFunds(200000);
        mortageLenderService.addApplicant(applicant);
        Request request = mortageLenderService.request(applicant,125000);
        mortageLenderService.qualify(request);

        mortageLenderService.processRequest(request);

        mortageLenderService.accept(applicant,request,false);

        double pendingFunds = mortageLenderService.getPendingFunds();
        double expectedPendingFunds = 0;
        String resultStatus = request.getStatus();
        assertTrue("Rejected".equals(resultStatus));
        assertEquals(expectedPendingFunds,pendingFunds);
        double availableFunds = mortageLenderService.checkAvailableFunds();
        double expectedAvailableFunds = 200000;
        assertEquals(expectedAvailableFunds,availableFunds);

    }

    @Test
    public void checkLoadExpiration() {
        Applicant applicant = new Applicant(21, 700, 100000);
        mortageLenderService.depositFunds(200000);
        mortageLenderService.addApplicant(applicant);
        Request request = mortageLenderService.request(applicant, 125000);
        mortageLenderService.qualify(request);

        mortageLenderService.processRequest(request);
        LocalDate futureDate = LocalDate.now().plusDays(4);

        mortageLenderService.checkRequest(request, futureDate);

        assertTrue(request.getStatus().equals("expired"));
        double pendingFunds = mortageLenderService.getPendingFunds();
        double expectedPendingFunds = 0;
        assertEquals(expectedPendingFunds,pendingFunds);
    }



    }
