import java.util.ArrayList;

public interface JobSearchServices {
    public ArrayList<JobOffer> getJobOffers(ArrayList<Filter> filters);
    public JobOffer getJobOffer(int id);
}
