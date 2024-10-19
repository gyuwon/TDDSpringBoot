package commerce.querymodel;

import commerce.Product;
import commerce.Seller;
import commerce.view.ProductView;
import commerce.view.SellerView;

record ProductWithSeller(Product product, Seller seller) {

    public ProductView toView() {
        return new ProductView(
            product.getId(),
            new SellerView(seller.getId(), seller.getUsername()),
            product.getName(),
            product.getDescription(),
            product.getPriceAmount(),
            product.getStockQuantity()
        );
    }
}
