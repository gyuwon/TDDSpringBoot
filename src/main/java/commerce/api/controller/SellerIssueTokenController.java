package commerce.api.controller;

import javax.crypto.spec.SecretKeySpec;

import commerce.Seller;
import commerce.SellerRepository;
import commerce.query.IssueSellerToken;
import commerce.result.AccessTokenCarrier;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record SellerIssueTokenController(
    SellerRepository repository,
    PasswordEncoder passwordEncoder,
    @Value("${security.jwt.secret}") String jwtSecret
) {

    @PostMapping("/seller/issueToken")
    ResponseEntity<?> issueToken(@RequestBody IssueSellerToken query) {
        return repository
            .findByEmail(query.email())
            .filter(seller -> passwordEncoder.matches(
                query.password(),
                seller.getHashedPassword()
            ))
            .map(this::composeToken)
            .map(AccessTokenCarrier::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private String composeToken(Seller seller) {
        return Jwts
            .builder()
            .signWith(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256"))
            .setSubject(seller.getId().toString())
            .compact();
    }
}
