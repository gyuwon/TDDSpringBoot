package test.commerce.api;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ApiFixtureConfiguration {

    @Bean
    ApiFixture apiFixture(BeanFactory factory) {
        return new ApiFixture(factory.getBean(TestRestTemplate.class));
    }
}
