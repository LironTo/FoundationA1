import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTest {

    JobSearchServices jobSearchServices;
    AgentServices agentServices;

    @Before
    public void setUp() {
        // prepare fake services and sample data
        FakeAgentServices fas = new FakeAgentServices();
        this.agentServices = fas;
        // create an agent with some available quota
        fas.createAgent("companyA", "pwd", 5);

        // add sample offers
        fas.forcePublishSampleOffer("companyA", new TestJobOffer(1, "HR Manager", "Manage HR team", 12000, "HR", "BA", "Full-time", "Tel Aviv", "Manager", "Services", true));
        fas.forcePublishSampleOffer("companyA", new TestJobOffer(2, "Junior Nurse", "Assist nurses", 8000, "Medical", "Diploma", "Part-time", "Haifa", "Nurse", "Healthcare", true));
        fas.forcePublishSampleOffer("companyA", new TestJobOffer(3, "Senior Coach", "Lead sports team", 22000, "Sports", "BA", "Full-time", "Jerusalem", "Coach", "Sports", true));

        this.jobSearchServices = new FakeJobSearchServices(fas.getAllOffers());
    }

    // 1) Enter job title and advertisement content and confirm publication (happy)
    @Test
    public void publish_withValidTitleAndDescription_shouldSucceed() {
        boolean connected = agentServices.connect("companyA", "pwd");
        Assert.assertTrue("connect should succeed", connected);

        boolean published = agentServices.publishJobOffer("New Role", "This is a valid job description.");
        Assert.assertTrue("Publishing with non-empty title and description should succeed", published);
    }

    // 2) Enter job title and advertisement content with empty values and confirm publication (sad / failure)
    @Test
    public void publish_withEmptyTitleOrDescription_shouldFail() {
        boolean connected = agentServices.connect("companyA", "pwd");
        Assert.assertTrue(connected);

        // empty title
        boolean publishedEmptyTitle = agentServices.publishJobOffer("", "Some description");
        Assert.assertFalse("Publishing with empty title should be rejected", publishedEmptyTitle);

        // empty description
        boolean publishedEmptyDesc = agentServices.publishJobOffer("Some title", "   ");
        Assert.assertFalse("Publishing with empty description should be rejected", publishedEmptyDesc);
    }

    // 3) Apply search filters on job listings page -> success returns filtered results
    @Test
    public void search_withFilters_shouldReturnFilteredResults() {
        // filter: salary 10000-20000 and type HR (matches HR Manager)
        ArrayList<Filter> filters = new ArrayList<>();
        Filter fSalary = new Filter();
        fSalary.filterType = FilterType.SALARY;
        fSalary.data = "10000-20000";
        filters.add(fSalary);

        Filter fType = new Filter();
        fType.filterType = FilterType.TYPE;
        fType.data = "HR";
        filters.add(fType);

        ArrayList<JobOffer> results = jobSearchServices.getJobOffers(filters);
        Assert.assertNotNull(results);
        Assert.assertFalse("Filtered search should return at least one matching offer", results.isEmpty());

        // verify the returned offers follow the high-level criteria
        for (JobOffer jo : results) {
            if (jo instanceof TestJobOffer) {
                TestJobOffer t = (TestJobOffer) jo;
                Assert.assertTrue("Salary should be within the requested range", t.salary >= 10000 && t.salary <= 20000);
                Assert.assertEquals("Type should match filter", "HR", t.type);
            }
        }
    }

    // 4) Apply search with empty filters -> return all job listings (success)
    @Test
    public void search_withEmptyFilters_shouldReturnAllOffers() {
        ArrayList<Filter> emptyFilters = new ArrayList<>();
        ArrayList<JobOffer> results = jobSearchServices.getJobOffers(emptyFilters);
        Assert.assertNotNull(results);

        // we pre-populated 3 offers in setUp
        Assert.assertTrue("Empty filter search should return all pre-populated offers (>=3)", results.size() >= 3);
    }

    // ----------------- Test doubles and helpers -----------------

    static class TestJobOffer extends JobOffer {
        int id;
        String title;
        String desc;
        int salary;
        String type;
        String education;
        String scope;
        String zone;
        String role;
        String domain;
        boolean published;

        TestJobOffer(int id, String title, String desc, int salary, String type, String education, String scope, String zone, String role, String domain, boolean published) {
            this.id = id; this.title = title; this.desc = desc; this.salary = salary; this.type = type; this.education = education; this.scope = scope; this.zone = zone; this.role = role; this.domain = domain; this.published = published;
        }
    }

    static class FakeAgentServices implements AgentServices {
        Map<String, CompanyAgent> agents = new HashMap<>();
        Map<String, Integer> available = new HashMap<>();
        String connectedUser = null;
        Map<String, ArrayList<JobOffer>> storedOffers = new HashMap<>();

        void createAgent(String username, String password, int availableCount) {
            CompanyAgent ca = new CompanyAgent();
            ca.username = username; ca.password = password; ca.jobOffers = new ArrayList<>();
            agents.put(username, ca);
            available.put(username, availableCount);
            storedOffers.put(username, ca.jobOffers);
        }

        void forcePublishSampleOffer(String username, TestJobOffer offer) {
            CompanyAgent ca = agents.get(username);
            if (ca != null) {
                if (ca.jobOffers == null) ca.jobOffers = new ArrayList<>();
                ca.jobOffers.add(offer);
                storedOffers.put(username, ca.jobOffers);
            }
        }

        List<JobOffer> getAllOffers() {
            ArrayList<JobOffer> all = new ArrayList<>();
            for (ArrayList<JobOffer> list : storedOffers.values()) all.addAll(list);
            return all;
        }

        @Override
        public boolean connect(String username, String password) {
            CompanyAgent ca = agents.get(username);
            if (ca != null && ca.password.equals(password)) { connectedUser = username; return true; }
            return false;
        }

        @Override
        public boolean publishJobOffer(String title, String Desc) {
            if (connectedUser == null) return false;
            if (title == null || title.trim().isEmpty()) return false;
            if (Desc == null || Desc.trim().isEmpty()) return false;
            CompanyAgent ca = agents.get(connectedUser);
            if (ca == null) return false;
            if (available.getOrDefault(connectedUser, 0) <= 0) return false;
            int newId = ca.jobOffers.size() + 1;
            TestJobOffer tjo = new TestJobOffer(newId, title, Desc, 10000, "Generic", "", "", "", "", "", true);
            ca.jobOffers.add(tjo);
            available.put(connectedUser, available.get(connectedUser) - 1);
            return true;
        }

        @Override
        public boolean updateJobOffer(int jobOfferId, boolean status) { return true; }

        @Override
        public ArrayList<JobOffer> getJobOffers(String username) {
            ArrayList<JobOffer> list = storedOffers.get(username);
            if (list == null) return new ArrayList<>();
            return list;
        }

        @Override
        public JobOffer getJobOffer(int id) {
            for (ArrayList<JobOffer> list : storedOffers.values()) for (JobOffer jo : list) if (jo instanceof TestJobOffer && ((TestJobOffer) jo).id == id) return jo;
            return null;
        }

        @Override
        public int availableJobOffersNum(String username) { return available.getOrDefault(username, 0); }
    }

    static class FakeJobSearchServices implements JobSearchServices {
        ArrayList<JobOffer> allOffers = new ArrayList<>();

        FakeJobSearchServices(List<JobOffer> initial) { if (initial != null) this.allOffers.addAll(initial); }

        @Override
        public ArrayList<JobOffer> getJobOffers(ArrayList<Filter> filters) {
            if (filters == null || filters.isEmpty()) return new ArrayList<>(allOffers);
            ArrayList<JobOffer> res = new ArrayList<>();
            for (JobOffer jo : allOffers) {
                if (!(jo instanceof TestJobOffer)) continue;
                TestJobOffer t = (TestJobOffer) jo;
                boolean match = true;
                for (Filter f : filters) {
                    if (f.filterType == FilterType.SALARY) {
                        String[] parts = f.data.split("-");
                        try {
                            int min = Integer.parseInt(parts[0]);
                            int max = Integer.parseInt(parts[1]);
                            if (t.salary < min || t.salary > max) { match = false; break; }
                        } catch (Exception e) { match = false; break; }
                    } else if (f.filterType == FilterType.TYPE) {
                        if (!t.type.equalsIgnoreCase(f.data)) { match = false; break; }
                    }
                }
                if (match) res.add(t);
            }
            return res;
        }

        @Override
        public JobOffer getJobOffer(int id) {
            for (JobOffer jo : allOffers) if (jo instanceof TestJobOffer && ((TestJobOffer) jo).id == id) return jo;
            return null;
        }
    }

}