package test.commerce.api.seller.issuetoken;

import javax.crypto.spec.SecretKeySpec;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.command.CreateSeller;
import commerce.query.IssueSellerToken;
import commerce.result.AccessTokenCarrier;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import test.commerce.api.CommerceApiTest;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static test.commerce.EmailGenerator.generateEmail;
import static test.commerce.PasswordGenerator.generatePassword;
import static test.commerce.UsernameGenerator.generateUsername;

@CommerceApiTest
@DisplayName("POST /seller/issueToken")
public class POST_specs {
    
    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        client.postForEntity(
            "/seller/signUp",
            new CreateSeller(email, generateUsername(), password),
            Void.class
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/issueToken",
            new IssueSellerToken(email, password),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
    
    @Test
    void 올바르게_요청하면_토큰을_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        client.postForEntity(
            "/seller/signUp",
            new CreateSeller(email, generateUsername(), password),
            Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
            "/seller/issueToken",
            new IssueSellerToken(email, password),
            AccessTokenCarrier.class
        );

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotBlank();
    }
    
    @Test
    void 토큰에_판매자_식별자를_담는다(
        @Autowired TestRestTemplate client,
        @Autowired SellerRepository repository,
        @Value("${security.jwt.secret}") String secret
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        client.postForEntity(
            "/seller/signUp",
            new CreateSeller(email, generateUsername(), password),
            Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
            "/seller/issueToken",
            new IssueSellerToken(email, password),
            AccessTokenCarrier.class
        );

        // Assert
        String token = requireNonNull(response.getBody()).accessToken();
        String actual = Jwts
            .parserBuilder()
            .setSigningKey(new SecretKeySpec(secret.getBytes(), "HmacSHA256"))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
        Seller seller = repository.findByEmail(email).orElseThrow();
        assertThat(actual).isEqualTo(seller.getId().toString());
    }
    
    @Test
    void 존재하지_않는_이메일이_사용되면_400_Bad_Request_상태코드를_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/issueToken",
            new IssueSellerToken(generateEmail(), generatePassword()),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
    
    @Test
    void 잘못된_비밀번호가_사용되면_400_Bad_Request_상태코드를_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();
        String wrongPassword = generatePassword();

        client.postForEntity(
            "/seller/signUp",
            new CreateSeller(email, generateUsername(), password),
            Void.class
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/issueToken",
            new IssueSellerToken(email, wrongPassword),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}
