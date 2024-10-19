package commerce.api.controller;

import commerce.query.GetProductPage;
import commerce.querymodel.GetProductPageQueryProcessor;
import commerce.result.PageCarrier;
import jakarta.persistence.EntityManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class ShopperProductsController {

    private final GetProductPageQueryProcessor processor;

    public ShopperProductsController(EntityManager entityManager) {
        this.processor = new GetProductPageQueryProcessor(entityManager);
    }

    @GetMapping("/shopper/products")
    PageCarrier<?> getProducts(
        @RequestParam(required = false) String continuationToken
    ) {
        var query = new GetProductPage(continuationToken);
        return processor.process(query);
    }
}
