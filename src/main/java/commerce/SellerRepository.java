package commerce;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    @Query("SELECT s FROM Seller s WHERE s.id = :id")
    Optional<Seller> find(UUID id);

    Optional<Seller> findByEmail(String email);
}
