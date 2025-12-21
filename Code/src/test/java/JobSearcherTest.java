import java.util.ArrayList;


// Bridge class for JobSearchServices and the tests
public class JobSearcherTest {

    JobSearchServices jobSearchServices;

    public JobSearcherTest() {
        this.jobSearchServices = new SampleJobSearchServices();
    }


    protected static class SampleJobSearchServices implements JobSearchServices{

        @Override
        public ArrayList<JobOffer> getJobOffers(ArrayList<Filter> filters) {
            ArrayList<JobOffer> jobOffers = new ArrayList<>();
            jobOffers.add(new JobOffer("S1", "D1"));
            jobOffers.add(new JobOffer("S2", "D2"));
            return jobOffers;
        }

        @Override
        public JobOffer getJobOffer(int id) {
            return null;
        }
    }
}
