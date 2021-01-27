public class Lender {

    private double availableFunds;
    private double pendingFunds;

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

    public double getPendingFunds() {
        return this.pendingFunds;
    }

    public void updatePendingFunds(double value) {
        this.pendingFunds = value;
    }
}
