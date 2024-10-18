package commerce.api.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerProductsController() {

    @PostMapping("/seller/products")
    ResponseEntity<?> registerProduct() {
        URI location = URI.create("/seller/products/1");
        return ResponseEntity.created(location).build();
    }
}
