package test.commerce;

import commerce.command.RegisterProductCommand;

import static test.commerce.ProductDescriptionGenerator.generateProductDescription;
import static test.commerce.ProductNameGenerator.generateProductName;
import static test.commerce.ProductPriceAmountGenerator.generateProductPriceAmount;
import static test.commerce.ProductStockQuantityGenerator.generateProductStockQuantity;

public class RegisterProductCommandGenerator {

    public static RegisterProductCommand generateRegisterProductCommand() {
        return new RegisterProductCommand(
            generateProductName(),
            generateProductDescription(),
            generateProductPriceAmount(),
            generateProductStockQuantity()
        );
    }
}
