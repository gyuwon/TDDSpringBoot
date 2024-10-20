package test.commerce.api.seller.me;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.view.SellerMeView;
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
@DisplayName("GET /seller/me")
public class GET_specs {

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();
        fixture.createSeller(email, generateUsername(), password);
        String token = fixture.issueSellerToken(email, password);

        // Act
        ResponseEntity<Void> actual = client.exchange(
            get("/seller/me")
                .header("Authorization", "Bearer " + token)
                .build(),
            Void.class
        );

        // Assert
        assertThat(actual.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void 올바르게_요청하면_판매자_정보를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client,
        @Autowired SellerRepository repository
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();
        String username = generateUsername();
        fixture.createSeller(email, username, password);
        String token = fixture.issueSellerToken(email, password);

        // Act
        ResponseEntity<SellerMeView> response = client.exchange(
            get("/seller/me")
                .header("Authorization", "Bearer " + token)
                .build(),
            SellerMeView.class
        );

        // Assert
        SellerMeView actual = response.getBody();
        Seller seller = repository.findByEmail(email).orElseThrow();
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(seller.getId());
        assertThat(actual.email()).isEqualTo(email);
        assertThat(actual.username()).isEqualTo(username);
    }

    @Test
    void 접근토큰을_사용하지_않으면_401_Unauthorized_상태코드를_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Act
        ResponseEntity<Void> response = client.exchange(
            get("/seller/me").build(),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }
}
