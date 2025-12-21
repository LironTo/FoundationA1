import java.util.ArrayList;

// Bridge class for AgentServices and the tests
public class AgentTest {
    AgentServices agentServices;

    public AgentTest() {
        agentServices = new SampleAgentServices();
    }

    protected static class SampleAgentServices implements AgentServices {

        @Override
        public boolean connect(String username, String password) {
            return true;
        }

        @Override
        public boolean publishJobOffer(String title, String Desc) {
            if(title == null || title.trim().isEmpty()) return false;
            return true;
        }

        @Override
        public boolean updateJobOffer(int jobOfferId, boolean status) {
            return false;
        }

        @Override
        public ArrayList<JobOffer> getJobOffers(String username) {
            return new ArrayList<JobOffer>();
        }

        @Override
        public JobOffer getJobOffer(int id) {
            return null;
        }

        @Override
        public int availableJobOffersNum(String username) {
            return 0;
        }
    }
}
