package nl.backbase.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "USER")
@NoArgsConstructor
@EqualsAndHashCode
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "USERNAME", nullable = false)
    public String username;

    @Column(name = "PASSWORD", nullable = false)
    public String password;

    @Column(name = "APIKEY", nullable = false)
    public String apiKey;
}
