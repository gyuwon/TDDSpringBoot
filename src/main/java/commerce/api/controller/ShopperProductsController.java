package commerce.api.controller;

import java.util.Base64;
import java.util.List;

import commerce.result.PageCarrier;
import commerce.view.ProductView;
import jakarta.persistence.EntityManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
public record ShopperProductsController(EntityManager entityManager) {

    private static final String JPQL = """
        SELECT new commerce.api.controller.ProductWithSeller(p, s)
        FROM Product p
        JOIN Seller s ON p.sellerId = s.id
        WHERE :next IS NULL OR p.sequence <= :next
        ORDER BY p.sequence DESC
        """;

    @GetMapping("/shopper/products")
    PageCarrier<?> getProducts(
        @RequestParam(required = false) String continuationToken
    ) {
        int pageSize = 10;

        List<ProductWithSeller> results = entityManager
            .createQuery(JPQL, ProductWithSeller.class)
            .setParameter("next", decodeNextSequence(continuationToken))
            .setMaxResults(pageSize + 1)
            .getResultStream()
            .toList();

        ProductView[] items = results
            .stream()
            .limit(pageSize)
            .map(ProductWithSeller::toView)
            .toArray(ProductView[]::new);

        String nextContinuationToken = results.size() <= pageSize
            ? null
            : encodeNextSequence(results.getLast().product().getSequence());

        return new PageCarrier<>(items, nextContinuationToken);
    }

    private static String encodeNextSequence(Long nextSequence) {
        byte[] bytes = nextSequence.toString().getBytes(UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static Long decodeNextSequence(String continuationToken) {
        if (continuationToken == null || continuationToken.isBlank()) {
            return null;
        } else {
            byte[] bytes = Base64.getDecoder().decode(continuationToken);
            return Long.parseLong(new String(bytes, UTF_8));
        }
    }
}
