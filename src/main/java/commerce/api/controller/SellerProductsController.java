package commerce.api.controller;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import commerce.Product;
import commerce.ProductRepository;
import commerce.command.RegisterProductCommand;
import commerce.result.ArrayCarrier;
import commerce.view.SellerProductView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerProductsController(ProductRepository repository) {

    @PostMapping("/seller/products")
    ResponseEntity<?> registerProduct(
        @RequestBody RegisterProductCommand command,
        Principal user
    ) {
        var product = new Product();
        product.setId(UUID.randomUUID());
        product.setSellerId(UUID.fromString(user.getName()));
        product.setName(command.name());
        product.setDescription(command.description());
        product.setPriceAmount(command.priceAmount());
        product.setStockQuantity(command.stockQuantity());
        product.setRegisteredTimeUtc(LocalDateTime.now(ZoneOffset.UTC));
        repository.save(product);
        URI location = URI.create("/seller/products/" + product.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/seller/products/{id}")
    ResponseEntity<?> findProduct(@PathVariable UUID id, Principal user) {
        UUID sellerId = UUID.fromString(user.getName());
        return repository
            .find(id)
            .filter(product -> product.getSellerId().equals(sellerId))
            .map(SellerProductsController::toView)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/seller/products")
    ResponseEntity<?> getProducts(Principal user) {
        UUID sellerId = UUID.fromString(user.getName());
        SellerProductView[] items = repository
            .findBySellerIdOrderByRegisteredTimeUtcDesc(sellerId)
            .stream()
            .map(SellerProductsController::toView)
            .toArray(SellerProductView[]::new);
        return ResponseEntity.ok(new ArrayCarrier<>(items));
    }

    private static SellerProductView toView(Product product) {
        return new SellerProductView(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPriceAmount(),
            product.getStockQuantity(),
            product.getRegisteredTimeUtc()
        );
    }
}
