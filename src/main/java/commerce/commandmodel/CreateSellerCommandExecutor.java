package commerce.commandmodel;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import commerce.Seller;
import commerce.command.CreateSellerCommand;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateSellerCommandExecutor {

    private final UnaryOperator<String> passwordHasher;
    private final Consumer<Seller> sellerStore;

    public void execute(UUID id, CreateSellerCommand command) {
        validateCommand(command);
        var seller = createSeller(id, command);
        persistNewSeller(seller);
    }

    private static void validateCommand(CreateSellerCommand command) {
        CreateSellerCommandValidator.validate(command);
    }

    private Seller createSeller(UUID id, CreateSellerCommand command) {
        var seller = new Seller();
        seller.setId(id);
        seller.setEmail(command.email());
        seller.setUsername(command.username());
        seller.setHashedPassword(passwordHasher.apply(command.password()));
        return seller;
    }

    private void persistNewSeller(Seller seller) {
        sellerStore.accept(seller);
    }
}
