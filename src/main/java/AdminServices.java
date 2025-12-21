import java.util.Date;

public interface AdminServices {
    public CompanyAgent getCompanyAgent();
    public boolean createSubscription(Subscription subscription, CompanyAgent companyAgent, Date startDate);

}
