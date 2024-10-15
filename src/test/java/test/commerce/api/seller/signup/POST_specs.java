package test.commerce.api.seller.signup;

import commerce.api.CommerceApiApp;
import commerce.command.CreateSellerCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = CommerceApiApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@DisplayName("POST /seller/signUp")
public class POST_specs {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
            "seller@test.com",
            "seller",
            "password"
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
            null,
            "seller",
            "password"
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-email",
        "invalid-email@",
        "invalid-email@test",
        "invalid-email@test."
    })
    void email_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
        String email,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
            email,
            "seller",
            "password"
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void username_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
            "seller@test.com",
            null,
            "password"
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "se",
        "seller ",
        "seller!",
        "seller@"
    })
    void username_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
        String username,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
            "seller@test.com",
            username,
            "password"
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
            "seller@test.com",
            "seller",
            null
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "pass",
        "pass123"
    })
    void password_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(
        String password,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        var command = new CreateSellerCommand(
            "seller@test.com",
            "seller",
            password
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            "/seller/signUp",
            command,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}
