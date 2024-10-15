package commerce.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("commerce")
@EntityScan("commerce")
public class CommerceApiApp {

    public static void main(String[] args) {
        SpringApplication.run(CommerceApiApp.class, args);
    }
}
