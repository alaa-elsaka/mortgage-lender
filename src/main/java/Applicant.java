import java.awt.image.PixelGrabber;

public class Applicant {

    private double dti;
    private int credit_score;
    private double savings;
    public Applicant(double dti, int credit_score, double savings) {
        this.dti =dti;
        this.credit_score = credit_score;
        this.savings = savings;
    }

    public double getDti() {
        return dti;
    }

    public int getCredit_score() {
        return credit_score;
    }

    public double getSavings() {
        return savings;
    }
}
