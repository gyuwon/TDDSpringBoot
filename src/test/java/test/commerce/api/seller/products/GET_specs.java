package test.commerce.api.seller.products;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import commerce.command.RegisterProductCommand;
import commerce.result.ArrayCarrier;
import commerce.view.SellerProductView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import test.commerce.api.ApiFixture;
import test.commerce.api.CommerceApiTest;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.RequestEntity.get;
import static test.commerce.EmailGenerator.generateEmail;
import static test.commerce.PasswordGenerator.generatePassword;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;
import static test.commerce.UsernameGenerator.generateUsername;

@CommerceApiTest
@DisplayName("GET /seller/products")
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

        // Act
        String path = "/seller/products";
        ResponseEntity<Void> response = client.exchange(
            get(path).header("Authorization", "Bearer " + token).build(),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void 판매자가_등록한_모든_상품을_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();
        fixture.createSeller(email, username, password);
        String token = fixture.issueSellerToken(email, password);

        List<UUID> ids = fixture.registerProducts(token);

        // Act
        String path = "/seller/products";
        ResponseEntity<ArrayCarrier<SellerProductView>> response =
            client.exchange(
                get(path).header("Authorization", "Bearer " + token).build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        ArrayCarrier<SellerProductView> actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.items())
            .extracting(SellerProductView::id)
            .contains(ids.toArray(new UUID[0]));
    }

    @Test
    void 상품_목록을_등록_시점_역순으로_정렬한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();
        fixture.createSeller(email, username, password);
        String token = fixture.issueSellerToken(email, password);

        List<UUID> ids = fixture.registerProducts(token);

        // Act
        String path = "/seller/products";
        ResponseEntity<ArrayCarrier<SellerProductView>> response =
            client.exchange(
                get(path).header("Authorization", "Bearer " + token).build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        SellerProductView[] actual = requireNonNull(response.getBody()).items();
        assertThat(actual)
            .extracting(SellerProductView::id)
            .containsExactly(ids.reversed().toArray(new UUID[0]));
    }

    @Test
    void 다른_판매자가_등록한_상품이_포함되지_않는다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String seller1Email = generateEmail();
        String seller1Username = generateUsername();
        String seller1Password = generatePassword();
        fixture.createSeller(seller1Email, seller1Username, seller1Password);
        String token = fixture.issueSellerToken(seller1Email, seller1Password);
        UUID id = fixture.registerProduct(
            token,
            generateRegisterProductCommand()
        );

        String seller2Email = generateEmail();
        String seller2Username = generateUsername();
        String seller2Password = generatePassword();
        fixture.createSeller(seller2Email, seller2Username, seller2Password);
        fixture.registerProduct(
            fixture.issueSellerToken(seller2Email, seller2Password),
            generateRegisterProductCommand()
        );

        // Act
        String path = "/seller/products";
        ResponseEntity<ArrayCarrier<SellerProductView>> response =
            client.exchange(
                get(path).header("Authorization", "Bearer " + token).build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        assertThat(requireNonNull(response.getBody()).items())
            .extracting(SellerProductView::id)
            .containsExactly(id);
    }

    @Test
    void 상품_속성을_올바르게_설정한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();
        fixture.createSeller(email, username, password);
        String token = fixture.issueSellerToken(email, password);

        RegisterProductCommand command = generateRegisterProductCommand();
        fixture.registerProduct(token, command);

        // Act
        String path = "/seller/products";
        ResponseEntity<ArrayCarrier<SellerProductView>> response =
            client.exchange(
                get(path).header("Authorization", "Bearer " + token).build(),
                new ParameterizedTypeReference<>() { }
            );

        // Assert
        ArrayCarrier<SellerProductView> body = response.getBody();
        SellerProductView actual = requireNonNull(body).items()[0];
        assertThat(actual.name()).isEqualTo(command.name());
        assertThat(actual.description()).isEqualTo(command.description());
        assertThat(actual.priceAmount().doubleValue())
            .isEqualTo(command.priceAmount().doubleValue());
        assertThat(actual.stockQuantity()).isEqualTo(command.stockQuantity());
        assertThat(actual.registeredTimeUtc())
            .isCloseTo(LocalDateTime.now(ZoneOffset.UTC), within(1, SECONDS));
    }
}
