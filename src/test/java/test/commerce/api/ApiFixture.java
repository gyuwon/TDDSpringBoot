package test.commerce.api;

import java.net.URI;
import java.util.UUID;

import commerce.command.CreateSellerCommand;
import commerce.command.CreateShopperCommand;
import commerce.command.RegisterProductCommand;
import commerce.query.IssueSellerToken;
import commerce.query.IssueShopperToken;
import commerce.result.AccessTokenCarrier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.requireNonNull;
import static test.commerce.EmailGenerator.generateEmail;
import static test.commerce.PasswordGenerator.generatePassword;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;
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

    public String createSellerThenIssueToken(
        String email,
        String username,
        String password
    ) {
        createSeller(email, username, password);
        return issueSellerToken(email, password);
    }

    public String createSellerThenIssueToken() {
        return createSellerThenIssueToken(
            generateEmail(),
            generateUsername(),
            generatePassword()
        );
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

    public UUID registerProduct(String token, RegisterProductCommand command) {
        RequestEntity<?> request = RequestEntity
            .post("/seller/products")
            .header("Authorization", "Bearer " + token)
            .body(command);
        ResponseEntity<Void> response = client.exchange(request, Void.class);
        URI location = requireNonNull(response.getHeaders().getLocation());
        String path = location.getPath();
        return UUID.fromString(path.substring("/seller/products/".length()));
    }

    public UUID registerProduct(String token) {
        return registerProduct(token, generateRegisterProductCommand());
    }
}
