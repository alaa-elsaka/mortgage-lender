public class Request {
    private final Applicant applicant;
    private double amount;
    private String qualification;
    private String status;


    public Request(Applicant applicant, double amount) {

        this.applicant = applicant;
        this.amount = amount;
    }

    public String getQualification() {
        return this.qualification;
    }

    public double getAmount() {
        return amount;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
