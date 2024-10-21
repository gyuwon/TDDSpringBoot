package commerce.api;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    Pbkdf2PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        var secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    DefaultSecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtDecoder jwtDecoder
    ) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .oauth2ResourceServer(c -> c.jwt(jwt -> jwt.decoder(jwtDecoder)))
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/seller/signUp").permitAll()
                .requestMatchers("/seller/issueToken").permitAll()
                .requestMatchers("/shopper/signUp").permitAll()
                .requestMatchers("/shopper/issueToken").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }
}
