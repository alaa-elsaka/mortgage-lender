import java.util.ArrayList;
import java.util.List;

public class MortageLenderService {


    private Lender lender = new Lender();
    List<Applicant> applicants = new ArrayList<>();
    private Request request;

    public double checkAvailableFunds() {
        return lender.getAvailableFunds();
    }

    public void depositFunds(double value) {

        lender.depositFunds(value);
    }

    public Request qualify(Request request) {

        if ((request.getApplicant().getDti() < 36) &&
        (request.getApplicant().getCredit_score() > 620) &&
                (request.getApplicant().getSavings() > (0.25 * request.getAmount()))
        )
        {
            request.setQualification("qualified");
            request.setStatus("qualified");
        }
        else if ((request.getApplicant().getDti() < 36) &&
                (request.getApplicant().getCredit_score() > 620) &&
                ((4 * request.getApplicant().getSavings()) < ( request.getAmount()))
        ) {
            request.setQualification("partially qualified");
            request.setStatus("qualified");
        }
        else {
            request.setQualification("not qualified");
            request.setStatus("denied");
        }


        return request;
    }

    public void addApplicant(Applicant applicant) {

        applicants.add(applicant);
    }

    public Request request(Applicant applicant, double amount) {
        request = new Request(applicant, amount);
        return request;
    }

    public double getPendingFunds() {
        return lender.getPendingFunds();
    }

    public String processRequest(Request request) {
        if(request.getStatus().equals("denied"))
            return "This is a denied request";
        if(request.getStatus().equals("qualified") && request.getAmount() > checkAvailableFunds()){
            request.setStatus("on hold");
            return "processed";

        }
        else if(request.getStatus().equals("qualified") &&  request.getAmount() < checkAvailableFunds()){
            request.setStatus("approved");
            lender.setAvailableFunds(checkAvailableFunds() - request.getAmount());
            lender.updatePendingFunds(getPendingFunds()+request.getAmount());
            return "Successfully processed";
        }
        return "";
    }

    public void accept(Applicant applicant, Request request,boolean accepted) {

        if(accepted) {
            applicant.accept(request);
            lender.updatePendingFunds(getPendingFunds()-request.getAmount());
        }else {
            applicant.reject(request);
            lender.setAvailableFunds(checkAvailableFunds() + request.getAmount());
        }
    }
}
