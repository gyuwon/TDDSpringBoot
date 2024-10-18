package test.commerce;

import java.util.UUID;

public class ProductDescriptionGenerator {

    public static String generateProductDescription() {
        return "description " + UUID.randomUUID();
    }
}
