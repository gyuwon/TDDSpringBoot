package test.commerce.api.seller.products;

import java.net.URI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.commerce.api.ApiFixture;
import test.commerce.api.CommerceApiTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.post;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;

@CommerceApiTest
@DisplayName("POST /seller/products")
public class POST_specs {

    @Test
    void 올바르게_요청하면_201_Created_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String token = fixture.createSellerThenIssueToken();

        // Act
        RequestEntity<?> request = post("/seller/products")
            .header("Authorization", "Bearer " + token)
            .body(generateRegisterProductCommand());
        ResponseEntity<?> response = client.exchange(request, Void.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    void 올바르게_요청하면_등록된_상품_정보에_접근하는_Location_헤더를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String token = fixture.createSellerThenIssueToken();

        // Act
        RequestEntity<?> request = post("/seller/products")
            .header("Authorization", "Bearer " + token)
            .body(generateRegisterProductCommand());
        ResponseEntity<?> response = client.exchange(request, Void.class);

        // Assert
        URI actual = response.getHeaders().getLocation();
        assertThat(actual).isNotNull();
        assertThat(actual.isAbsolute()).isFalse();
        assertThat(actual.getPath()).startsWith("/seller/products/");
    }

    @Test
    void 잘못된_접근토큰을_사용하면_401_Unauthorized_상태코드를_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Act
        RequestEntity<?> request = post("/seller/products")
            .header("Authorization", "Bearer " + "invalid-token")
            .body(generateRegisterProductCommand());
        ResponseEntity<?> response = client.exchange(request, Void.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void 구매자_접근토큰을_사용하면_403_Forbidden_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String token = fixture.createShopperThenIssueToken();

        // Act
        RequestEntity<?> request = post("/seller/products")
            .header("Authorization", "Bearer " + token)
            .body(generateRegisterProductCommand());
        ResponseEntity<?> response = client.exchange(request, Void.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }
}
