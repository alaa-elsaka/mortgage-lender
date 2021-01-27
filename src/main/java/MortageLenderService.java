public class MortageLenderService {


    private Lender lender = new Lender();

    public double checkAvailableFunds() {
        return lender.getAvailableFunds();
    }

    public void depositFunds(double value) {

        lender.depositFunds(value);
    }

    public String qualify(Applicant applicant) {
        return null;
    }
}
