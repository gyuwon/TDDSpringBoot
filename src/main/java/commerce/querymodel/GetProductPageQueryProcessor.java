package commerce.querymodel;

import java.util.Base64;
import java.util.List;

import commerce.query.GetProductPage;
import commerce.result.PageCarrier;
import commerce.view.ProductView;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import static java.nio.charset.StandardCharsets.UTF_8;

@AllArgsConstructor
public class GetProductPageQueryProcessor {

    private static final int PAGE_SIZE = 10;

    private static final String JPQL = """
        SELECT new commerce.querymodel.ProductWithSeller(p, s)
        FROM Product p
        JOIN Seller s ON p.sellerId = s.id
        WHERE :next IS NULL OR p.sequence <= :next
        ORDER BY p.sequence DESC
        """;

    private final EntityManager entityManager;

    public PageCarrier<ProductView> process(GetProductPage query) {
        List<ProductWithSeller> results = entityManager
            .createQuery(JPQL, ProductWithSeller.class)
            .setParameter("next", decodeNextSequence(query.continuationToken()))
            .setMaxResults(PAGE_SIZE + 1)
            .getResultStream()
            .toList();

        ProductView[] items = results
            .stream()
            .limit(PAGE_SIZE)
            .map(ProductWithSeller::toView)
            .toArray(ProductView[]::new);

        String nextContinuationToken = results.size() <= PAGE_SIZE
            ? null
            : encodeNextSequence(results.getLast().product().getSequence());

        return new PageCarrier<>(items, nextContinuationToken);
    }

    private static Long decodeNextSequence(String continuationToken) {
        if (continuationToken == null || continuationToken.isBlank()) {
            return null;
        } else {
            byte[] bytes = Base64.getDecoder().decode(continuationToken);
            return Long.parseLong(new String(bytes, UTF_8));
        }
    }

    private static String encodeNextSequence(Long nextSequence) {
        byte[] bytes = nextSequence.toString().getBytes(UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
