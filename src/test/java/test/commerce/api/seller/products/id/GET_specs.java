package test.commerce.api.seller.products.id;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import commerce.command.RegisterProductCommand;
import commerce.view.SellerProductView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.commerce.api.ApiFixture;
import test.commerce.api.CommerceApiTest;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.RequestEntity.get;
import static test.commerce.EmailGenerator.generateEmail;
import static test.commerce.PasswordGenerator.generatePassword;
import static test.commerce.ProductDescriptionGenerator.generateProductDescription;
import static test.commerce.ProductNameGenerator.generateProductName;
import static test.commerce.ProductPriceAmountGenerator.generateProductPriceAmount;
import static test.commerce.ProductStockQuantityGenerator.generateProductStockQuantity;
import static test.commerce.UsernameGenerator.generateUsername;

@CommerceApiTest
@DisplayName("GET /seller/product/{id}")
public class GET_specs {
    
    @Test
    void 올바르게_요청하면_200_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();

        fixture.createSeller(email, username, password);
        String token = fixture.issueSellerToken(email, password);

        var command = new RegisterProductCommand(
            generateProductName(),
            generateProductDescription(),
            generateProductPriceAmount(),
            generateProductStockQuantity()
        );
        UUID id = fixture.registerProduct(token, command);

        // Act
        String path = "/seller/products/" + id;
        ResponseEntity<Void> response = client.exchange(
            get(path).header("Authorization", "Bearer " + token).build(),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
    
    @Test
    void 올바르게_요청하면_상품_정보를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();

        fixture.createSeller(email, username, password);
        String token = fixture.issueSellerToken(email, password);

        var command = new RegisterProductCommand(
            generateProductName(),
            generateProductDescription(),
            generateProductPriceAmount(),
            generateProductStockQuantity()
        );
        UUID id = fixture.registerProduct(token, command);

        // Act
        String path = "/seller/products/" + id;
        ResponseEntity<SellerProductView> response = client.exchange(
            get(path).header("Authorization", "Bearer " + token).build(),
            SellerProductView.class
        );

        // Assert
        SellerProductView actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(id);
        assertThat(actual.name()).isEqualTo(command.name());
        assertThat(actual.description()).isEqualTo(command.description());
        assertThat(actual.priceAmount().doubleValue())
            .isEqualTo(command.priceAmount().doubleValue());
        assertThat(actual.stockQuantity()).isEqualTo(command.stockQuantity());
        assertThat(actual.registeredTimeUtc()).isCloseTo(
            LocalDateTime.now(ZoneOffset.UTC),
            within(1, SECONDS)
        );
    }
    
    @Test
    void 잘못된_접근토큰을_사용하면_401_Unauthorized_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();

        fixture.createSeller(email, username, password);
        String token = fixture.issueSellerToken(email, password);

        var command = new RegisterProductCommand(
            generateProductName(),
            generateProductDescription(),
            generateProductPriceAmount(),
            generateProductStockQuantity()
        );
        UUID id = fixture.registerProduct(token, command);

        // Act
        RequestEntity<Void> request = get("/seller/products/" + id)
            .header("Authorization", "Bearer " + "invalid-token")
            .build();
        ResponseEntity<Void> response = client.exchange(request, Void.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }
    
    @Test
    void 구매자_접근토큰을_사용하면_403_Forbidden_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();
        fixture.createShopper(email, username, password);
        String token = fixture.issueShopperToken(email, password);

        // Act
        String location = "/seller/products/" + UUID.randomUUID();
        ResponseEntity<Void> response = client.exchange(
            get(location).header("Authorization", "Bearer " + token).build(),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }
    
    @Test
    void 존재하지_않는_식별자를_사용하면_404_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();
        fixture.createSeller(email, username, password);
        String token = fixture.issueSellerToken(email, password);

        // Act
        String location = "/seller/products/" + UUID.randomUUID();
        ResponseEntity<Void> response = client.exchange(
            get(location).header("Authorization", "Bearer " + token).build(),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }
    
    @Test
    void 다른_판매자가_등록한_상품_식별자를_사용하면_404_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String seller1Email = generateEmail();
        String seller1Username = generateUsername();
        String seller1Password = generatePassword();
        fixture.createSeller(seller1Email, seller1Username, seller1Password);
        String seller1Token = fixture.issueSellerToken(
            seller1Email,
            seller1Password
        );

        var command = new RegisterProductCommand(
            generateProductName(),
            generateProductDescription(),
            generateProductPriceAmount(),
            generateProductStockQuantity()
        );
        UUID id = fixture.registerProduct(seller1Token, command);

        String seller2Email = generateEmail();
        String seller2Username = generateUsername();
        String seller2Password = generatePassword();
        fixture.createSeller(seller2Email, seller2Username, seller2Password);
        String seller2Token = fixture.issueSellerToken(
            seller2Email,
            seller2Password
        );

        // Act
        ResponseEntity<Void> response = client.exchange(
            get("/seller/products/" + id)
                .header("Authorization", "Bearer " + seller2Token)
                .build(),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }
}
