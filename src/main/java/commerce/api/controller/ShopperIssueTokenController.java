package commerce.api.controller;

import javax.crypto.spec.SecretKeySpec;

import commerce.Shopper;
import commerce.ShopperRepository;
import commerce.query.IssueShopperToken;
import commerce.result.AccessTokenCarrier;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public record ShopperIssueTokenController(
    ShopperRepository repository,
    PasswordEncoder passwordEncoder,
    @Value("${security.jwt.secret}") String jwtSecret
) {

    @PostMapping("/shopper/issueToken")
    ResponseEntity<?> issueToken(@RequestBody IssueShopperToken query) {
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

    private String composeToken(Shopper shopper) {
        return Jwts
            .builder()
            .setSubject(shopper.getId().toString())
            .signWith(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256"))
            .claim("scp", "ROLE_SHOPPER")
            .compact();
    }
}
