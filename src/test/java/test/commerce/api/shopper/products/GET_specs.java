package test.commerce.api.shopper.products;

import java.util.List;
import java.util.UUID;

import commerce.ProductRepository;
import commerce.command.RegisterProductCommand;
import commerce.result.PageCarrier;
import commerce.view.ProductView;
import commerce.view.SellerMeView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import test.commerce.api.ApiFixture;
import test.commerce.api.CommerceApiTest;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static test.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;

@CommerceApiTest
@DisplayName("GET /shopper/products")
public class GET_specs {

    @Test
    void 올바르게_요청하면_200_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        String token = fixture.createShopperThenIssueToken();

        String path = "/shopper/products";
        ResponseEntity<Void> response = client.exchange(
            get(path).header("Authorization", "Bearer " + token).build(),
            Void.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void 상품_속성을_올바르게_설정한다(
        @Autowired ProductRepository productRepository,
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        productRepository.deleteAll();

        String sellerToken = fixture.createSellerThenIssueToken();
        SellerMeView seller = fixture.getSeller(sellerToken);
        RegisterProductCommand command = generateRegisterProductCommand();
        UUID productId = fixture.registerProduct(sellerToken, command);

        String shopperToken = fixture.createShopperThenIssueToken();

        // Act
        String path = "/shopper/products";
        ResponseEntity<PageCarrier<ProductView>> response = client.exchange(
            get(path).header("Authorization", "Bearer " + shopperToken).build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        ProductView actual = requireNonNull(response.getBody()).items()[0];
        assertThat(actual.id()).isEqualTo(productId);
        assertThat(actual.seller().id()).isEqualTo(seller.id());
        assertThat(actual.seller().username()).isEqualTo(seller.username());
        assertThat(actual.name()).isEqualTo(command.name());
        assertThat(actual.description()).isEqualTo(command.description());
        assertThat(actual.priceAmount().doubleValue())
            .isEqualTo(command.priceAmount().doubleValue());
        assertThat(actual.stockQuantity()).isEqualTo(command.stockQuantity());
    }

    @Test
    void 상품_목록을_등록_시점_역순으로_정렬한다(
        @Autowired ProductRepository productRepository,
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        productRepository.deleteAll();

        String sellerToken = fixture.createSellerThenIssueToken();
        List<UUID> ids = fixture.registerProducts(sellerToken);
        String shopperToken = fixture.createShopperThenIssueToken();

        // Act
        String path = "/shopper/products";
        ResponseEntity<PageCarrier<ProductView>> response = client.exchange(
            get(path).header("Authorization", "Bearer " + shopperToken).build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        ProductView[] actual = requireNonNull(response.getBody()).items();
        assertThat(actual)
            .extracting(ProductView::id)
            .containsExactlyElementsOf(ids.reversed());
    }

    @Test
    void 첫번째_페이지를_올바르게_반환한다(
        @Autowired ProductRepository productRepository,
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        productRepository.deleteAll();

        String sellerToken = fixture.createSellerThenIssueToken();
        fixture.registerProducts(sellerToken, 5);
        fixture.registerProducts(sellerToken, 10);
        List<UUID> ids = fixture.registerProducts(sellerToken, 10);

        String shopperToken = fixture.createShopperThenIssueToken();

        // Act
        String path = "/shopper/products";
        ResponseEntity<PageCarrier<ProductView>> response = client.exchange(
            get(path).header("Authorization", "Bearer " + shopperToken).build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        ProductView[] actual = requireNonNull(response.getBody()).items();
        assertThat(actual)
            .extracting(ProductView::id)
            .containsExactlyElementsOf(ids.reversed());
    }

    @Test
    void 두번째_페이지를_올바르게_반환한다(
        @Autowired ProductRepository productRepository,
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        productRepository.deleteAll();

        String sellerToken = fixture.createSellerThenIssueToken();
        fixture.registerProducts(sellerToken, 5);
        List<UUID> ids = fixture.registerProducts(sellerToken, 10);
        fixture.registerProducts(sellerToken, 10);

        String shopperToken = fixture.createShopperThenIssueToken();

        String continuationToken = fixture.consumeProductPage(shopperToken);

        // Act
        ResponseEntity<PageCarrier<ProductView>> response = client.exchange(
            get("/shopper/products?continuationToken=" + continuationToken)
                .header("Authorization", "Bearer " + shopperToken)
                .build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        ProductView[] actual = requireNonNull(response.getBody()).items();
        assertThat(actual)
            .extracting(ProductView::id)
            .containsExactlyElementsOf(ids.reversed());
    }

    @Test
    void 마지막_페이지를_올바르게_반환한다(
        @Autowired ProductRepository productRepository,
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        productRepository.deleteAll();

        String sellerToken = fixture.createSellerThenIssueToken();
        List<UUID> ids = fixture.registerProducts(sellerToken, 5);
        fixture.registerProducts(sellerToken, 10);
        fixture.registerProducts(sellerToken, 10);

        String shopperToken = fixture.createShopperThenIssueToken();

        String continuationToken = fixture.consumeProductPages(shopperToken, 2);

        // Act
        ResponseEntity<PageCarrier<ProductView>> response = client.exchange(
            get("/shopper/products?continuationToken=" + continuationToken)
                .header("Authorization", "Bearer " + shopperToken)
                .build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        PageCarrier<ProductView> page = response.getBody();
        ProductView[] items = requireNonNull(page).items();
        assertThat(items)
            .extracting(ProductView::id)
            .containsExactlyElementsOf(ids.reversed());
        assertThat(page.continuationToken()).isNull();
    }

    @Test
    void 판매자_접근토큰을_사용하면_403_Forbidden_상태코드를_반환한다(
        @Autowired ApiFixture fixture,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String sellerToken = fixture.createSellerThenIssueToken();

        // Act
        String path = "/shopper/products";
        ResponseEntity<Void> response = client.exchange(
            get(path).header("Authorization", "Bearer " + sellerToken).build(),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }
}
