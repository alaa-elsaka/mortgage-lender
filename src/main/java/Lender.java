public class Lender {

    private double availableFunds;
    public Lender(double initialFund) {

        this.availableFunds = initialFund;
    }

    public double checkAvailableFunds() {
        return availableFunds;
    }

    public void depositFunds(double value) {
        availableFunds += value;
    }
}
