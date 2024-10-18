package commerce.command;

import java.math.BigDecimal;

public record RegisterProductCommand(
    String name,
    String description,
    BigDecimal priceAmount,
    int stockQuantity
) {
}
