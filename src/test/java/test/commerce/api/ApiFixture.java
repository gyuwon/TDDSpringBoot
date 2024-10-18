package test.commerce.api;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import commerce.command.CreateSeller;
import commerce.command.CreateShopper;
import commerce.command.RegisterProductCommand;
import commerce.query.IssueSellerToken;
import commerce.query.IssueShopperToken;
import commerce.result.AccessTokenCarrier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.requireNonNull;

public record ApiFixture(TestRestTemplate client) {

    public void createSeller(String email, String username, String password) {
        var command = new CreateSeller(email, username, password);
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

    public void createShopper(String email, String username, String password) {
        var command = new CreateShopper(email, username, password);
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
}
