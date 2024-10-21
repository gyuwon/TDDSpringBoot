package commerce.api.controller;

import java.util.UUID;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.command.CreateSellerCommand;
import commerce.commandmodel.CreateSellerCommandExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerSignUpController(
    Pbkdf2PasswordEncoder passwordEncoder,
    SellerRepository repository
) {

    @PostMapping("/seller/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateSellerCommand command) {
        var executor = new CreateSellerCommandExecutor(
            passwordEncoder::encode,
            repository::save
        );
        executor.execute(UUID.randomUUID(), command);
        return ResponseEntity.noContent().build();
    }
}
