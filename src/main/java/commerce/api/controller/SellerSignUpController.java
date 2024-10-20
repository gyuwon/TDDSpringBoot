package commerce.api.controller;

import java.util.UUID;

import commerce.Patterns;
import commerce.Seller;
import commerce.SellerRepository;
import commerce.command.CreateSellerCommand;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (isCommandValid(command) == false) {
            return ResponseEntity.badRequest().build();
        }

        UUID id = UUID.randomUUID();
        String hashedPassword = passwordEncoder.encode(command.password());
        var seller = new Seller();
        seller.setId(id);
        seller.setEmail(command.email());
        seller.setUsername(command.username());
        seller.setHashedPassword(hashedPassword);

        try {
            repository.save(seller);
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    private static boolean isCommandValid(CreateSellerCommand command) {
        return isEmailValid(command.email())
            && isUsernameValid(command.username())
            && isPasswordValid(command.password());
    }

    private static boolean isUsernameValid(String username) {
        return username != null && username.matches(Patterns.USERNAME_REGEX);
    }

    private static boolean isEmailValid(String email) {
        return email != null && email.matches(Patterns.EMAIL_REGEX);
    }

    private static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }
}
