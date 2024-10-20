package commerce;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShopperRepository extends JpaRepository<Shopper, Long> {

    @Query("SELECT s FROM Shopper s WHERE s.id = :id")
    Optional<Shopper> find(UUID id);

    Optional<Shopper> findByEmail(String email);
}
