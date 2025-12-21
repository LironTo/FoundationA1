import java.util.ArrayList;

public interface AgentServices {
    public boolean connect(String username, String password);
    public boolean publishJobOffer(String title, String Desc);
    public boolean updateJobOffer(int jobOfferId, boolean status);
    public ArrayList<JobOffer> getJobOffers(String username);
    public JobOffer getJobOffer(int id);
    public int availableJobOffersNum(String username);
}
