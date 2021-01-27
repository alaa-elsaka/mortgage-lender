public class Lender {

    private double availableFunds;
    public Lender() {   }

    public double getAvailableFunds() {
        return availableFunds;
    }

    public void depositFunds(double value) {
        availableFunds += value;
    }

    public String qualify(Applicant applicant) {
        return null;
    }
}
