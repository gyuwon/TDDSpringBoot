package test.commerce;

import java.util.concurrent.ThreadLocalRandom;

public class ProductStockQuantityGenerator {

    public static int generateProductStockQuantity() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(10, 100);
    }
}
