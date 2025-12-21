import org.junit.Assert;
import org.junit.Test;

public class CreateJobAdTest extends AgentTest{

    // 1) Enter job title and advertisement content and confirm publication (happy)
    @Test
    public void publish_withValidTitleAndDescription_shouldSucceed() {
        boolean connected = agentServices.connect("Dr.Lek", "Aa123456");
        Assert.assertTrue("connect should succeed", connected);

        boolean published = agentServices.publishJobOffer("Ice Cream Specialist", "Master of frozen delights needed.");
        Assert.assertTrue("Publishing with non-empty title and description should succeed", published);
    }

    // 2) Enter job title and advertisement content with empty values and confirm publication (sad)
    @Test
    public void publish_withEmptyTitleOrDescription_shouldFail() {
        boolean connected = agentServices.connect("Dr.Lek", "Aa123456");
        Assert.assertTrue(connected);

        // empty title
        boolean publishedEmptyTitle = agentServices.publishJobOffer("", "Some description");
        Assert.assertFalse("Publishing with empty title should be rejected", publishedEmptyTitle);
    }

}
