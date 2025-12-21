import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class SearchForAJobTest extends JobSearcherTest{

    // 3) Apply search filters on job listings page -> success returns filtered results
    @Test
    public void search_withFilters_shouldReturnFilteredResults() {
        // filter: salary 10000-20000 and type HR (matches HR Manager)
        ArrayList<Filter> filters = new ArrayList<>();
        filters.add(new Filter(FilterType.SALARY, "10000-20000"));
        filters.add(new Filter(FilterType.TYPE, "Supervisor"));
        filters.add(new Filter(FilterType.ROLE, "HR"));

        ArrayList<JobOffer> results = jobSearchServices.getJobOffers(filters);
        Assert.assertNotNull(results);
        Assert.assertFalse("Filtered search should return at least one matching offer", results.isEmpty());
    }

    // 4) Apply search with empty filters -> return all job listings (success)
    @Test
    public void search_withEmptyFilters_shouldReturnAllOffers() {
        ArrayList<Filter> emptyFilters = new ArrayList<>();
        ArrayList<JobOffer> results = jobSearchServices.getJobOffers(emptyFilters);
        Assert.assertNotNull(results);
        // we pre-populated 2 offers in SampleJobSearchServices
        Assert.assertTrue("Empty filter search should return all pre-populated offers (>=2)", results.size() >= 2);
    }

}
