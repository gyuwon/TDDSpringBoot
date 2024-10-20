package test.commerce.api;

import commerce.command.CreateSellerCommand;
import commerce.query.IssueSellerToken;
import commerce.result.AccessTokenCarrier;
import org.springframework.boot.test.web.client.TestRestTemplate;

public record ApiFixture(TestRestTemplate client) {

    public void createSeller(String email, String username, String password) {
        var command = new CreateSellerCommand(email, username, password);
        client.postForEntity("/seller/signUp", command, Void.class);
    }

    public String issueSellerToken(String email, String password) {
        AccessTokenCarrier carrier = client.postForObject(
            "/seller/issueToken",
            new IssueSellerToken(email, password),
            AccessTokenCarrier.class
        );
        return carrier.accessToken();
    }
}
