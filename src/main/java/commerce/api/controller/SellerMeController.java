package commerce.api.controller;

import java.security.Principal;
import java.util.UUID;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.view.SellerMeView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerMeController(SellerRepository repository) {

    @GetMapping("/seller/me")
    SellerMeView me(Principal user) {
        UUID id = UUID.fromString(user.getName());
        Seller seller = repository.find(id).orElseThrow();
        return new SellerMeView(
            seller.getId(),
            seller.getEmail(),
            seller.getUsername()
        );
    }
}
