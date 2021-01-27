import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MortageLenderService {


    private Lender lender = new Lender();
    List<Applicant> applicants = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();

    public double checkAvailableFunds() {
        return lender.getAvailableFunds();
    }

    public void depositFunds(double value) {

        lender.depositFunds(value);
    }

    //to do refactor
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

    // to do refactor -- clone applicant object
    public void addApplicant(Applicant applicant) {

        applicants.add(applicant);
    }

    public Request request(Applicant applicant, double amount) {
        Request request = new Request(applicant, amount);
        requests.add(request);
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
            request.setApprovalDate(LocalDate.now());
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
            lender.updatePendingFunds(getPendingFunds()-request.getAmount());
            lender.setAvailableFunds(checkAvailableFunds() + request.getAmount());
        }
    }

    public void checkRequest(Request request, LocalDate futureDate){

        long intervalDays = ChronoUnit.DAYS.between(
                request.getApprovalDate(), futureDate);

        if(intervalDays > 3){
            request.setStatus("expired");
            lender.setAvailableFunds(checkAvailableFunds() + request.getAmount());
            lender.updatePendingFunds(getPendingFunds()-request.getAmount());
        }
    }


    public List<Request> search(String status) {
        List<Request> result =
                requests.stream().filter(req -> req.getStatus()
                        .equals(status))
                        .collect(Collectors.toList());
        return result;
    }
}
