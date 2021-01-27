public class Lender {

    private double availableFunds;
    public Lender() {   }

    public double getAvailableFunds() {
        return availableFunds;
    }

    public void depositFunds(double value) {
        availableFunds += value;
    }

    public void setAvailableFunds(double availableFunds) {
        this.availableFunds = availableFunds;
    }
}
