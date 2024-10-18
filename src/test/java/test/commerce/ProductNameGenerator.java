package test.commerce;

import java.util.UUID;

public class ProductNameGenerator {

    public static String generateProductName() {
        return "name " + UUID.randomUUID();
    }
}
