package test.commerce.api.shopper.me;

import commerce.Shopper;
import commerce.ShopperRepository;
import commerce.view.ShopperMeView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import test.commerce.api.ApiFixture;
import test.commerce.api.CommerceApiTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static test.commerce.EmailGenerator.generateEmail;
import static test.commerce.PasswordGenerator.generatePassword;
import static test.commerce.UsernameGenerator.generateUsername;

@CommerceApiTest
@DisplayName("GET /shopper/me")
public class GET_specs {

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();
        fixture.createShopper(email, generateUsername(), password);
        String token = fixture.issueShopperToken(email, password);

        // Act
        ResponseEntity<Void> actual = client.exchange(
            get("/shopper/me")
                .header("Authorization", "Bearer " + token)
                .build(),
            Void.class
        );

        // Assert
        assertThat(actual.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void 올바르게_요청하면_구매자_정보를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client,
        @Autowired ShopperRepository repository
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();
        String username = generateUsername();
        fixture.createShopper(email, username, password);
        String token = fixture.issueShopperToken(email, password);

        // Act
        ResponseEntity<ShopperMeView> response = client.exchange(
            get("/shopper/me")
                .header("Authorization", "Bearer " + token)
                .build(),
            ShopperMeView.class
        );

        // Assert
        ShopperMeView actual = response.getBody();
        Shopper shopper = repository.findByEmail(email).orElseThrow();
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(shopper.getId());
        assertThat(actual.email()).isEqualTo(email);
        assertThat(actual.username()).isEqualTo(username);
    }
}
