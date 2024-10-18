package commerce;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequence;

    @Column(unique = true)
    private UUID id;

    private UUID sellerId;

    private String name;

    private String description;

    private BigDecimal priceAmount;

    private int stockQuantity;

    private LocalDateTime registeredTimeUtc;
}
