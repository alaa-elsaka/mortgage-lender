import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void whenSearchLoansByStatus(){
        Applicant applicant1 = new Applicant(21, 700, 100000);
        Applicant applicant2 = new Applicant(21, 700, 100000);
        Applicant applicant3 = new Applicant(30,700,50000);
        Applicant applicant4 = new Applicant(30,700,50000);
        Applicant applicant5 = new Applicant(30,600,100000);
        Applicant applicant6 = new Applicant(30,600,100000);

        mortageLenderService.depositFunds(300000);

        mortageLenderService.addApplicant(applicant1);
        mortageLenderService.addApplicant(applicant2);
        mortageLenderService.addApplicant(applicant3);
        mortageLenderService.addApplicant(applicant4);
        mortageLenderService.addApplicant(applicant5);
        mortageLenderService.addApplicant(applicant6);


        Request request1 = mortageLenderService.request(applicant1, 125000);
        Request request2 = mortageLenderService.request(applicant2, 125000);
        Request request3 = mortageLenderService.request(applicant3, 125000);
        Request request4 = mortageLenderService.request(applicant4, 125000);
        Request request5 = mortageLenderService.request(applicant5, 125000);
        Request request6 = mortageLenderService.request(applicant6, 125000);
/*
        mortageLenderService.addRequest(request1);
        mortageLenderService.addRequest(request2);
        mortageLenderService.addRequest(request3);
        mortageLenderService.addRequest(request4);
        mortageLenderService.addRequest(request5);
        mortageLenderService.addRequest(request6);
*/
        mortageLenderService.qualify(request1);  //qualified
        mortageLenderService.qualify(request2);//qualified
        mortageLenderService.qualify(request3);  //qualified
        mortageLenderService.qualify(request4);//qualified
        mortageLenderService.qualify(request5); //denied
        mortageLenderService.qualify(request6); //denied

        mortageLenderService.processRequest(request1); //approved
        mortageLenderService.processRequest(request2); //expired after checkRequest call
        mortageLenderService.processRequest(request3);//on hold
        //mortageLenderService.processRequest(request4);//qualified
        mortageLenderService.processRequest(request5);//denied
        mortageLenderService.processRequest(request6);//denied


        LocalDate futureDate = LocalDate.now().plusDays(4);

        mortageLenderService.checkRequest(request2, futureDate); //request2 now is expired

        List<Request> expected = new ArrayList<>();

        //search for approved requests
        expected.add(request1);

        List<Request> actuaulResult =
                mortageLenderService.search("approved");

        assertEquals(expected, actuaulResult);

        // search for denied loans
        expected.clear();
        expected.add(request5);
        expected.add(request6);
        actuaulResult =
                mortageLenderService.search("denied");
        assertEquals(expected, actuaulResult);

        // search for on hold loans
        expected.clear();
        expected.add(request3);

        actuaulResult =
                mortageLenderService.search("on hold");
        assertEquals(expected, actuaulResult);

        // search for on hold loans
        expected.clear();
        expected.add(request4);

        actuaulResult =
                mortageLenderService.search("qualified");
        assertEquals(expected, actuaulResult);


    }



    }
