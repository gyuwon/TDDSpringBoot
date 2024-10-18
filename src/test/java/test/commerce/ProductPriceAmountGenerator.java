package test.commerce;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class ProductPriceAmountGenerator {

    public static BigDecimal generateProductPriceAmount() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new BigDecimal(random.nextInt(10000, 100000));
    }
}
