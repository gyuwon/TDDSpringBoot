package test.commerce.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import commerce.api.CommerceApiApp;
import org.springframework.boot.test.context.SpringBootTest;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { CommerceApiApp.class, ApiFixtureConfiguration.class },
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public @interface CommerceApiTest {
}
