package test.commerce.api;

import commerce.command.CreateSellerCommand;
import commerce.command.CreateShopperCommand;
import commerce.query.IssueSellerToken;
import commerce.query.IssueShopperToken;
import commerce.result.AccessTokenCarrier;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static test.commerce.EmailGenerator.generateEmail;
import static test.commerce.PasswordGenerator.generatePassword;
import static test.commerce.UsernameGenerator.generateUsername;

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

    public String createSellerThenIssueToken() {
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();
        createSeller(email, username, password);
        return issueSellerToken(email, password);
    }

    public void createShopper(String email, String username, String password) {
        var command = new CreateShopperCommand(email, username, password);
        client.postForEntity("/shopper/signUp", command, Void.class);
    }

    public String issueShopperToken(String email, String password) {
        AccessTokenCarrier carrier = client.postForObject(
            "/shopper/issueToken",
            new IssueShopperToken(email, password),
            AccessTokenCarrier.class
        );
        return carrier.accessToken();
    }

    public String createShopperThenIssueToken() {
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();
        createShopper(email, username, password);
        return issueShopperToken(email, password);
    }
}
