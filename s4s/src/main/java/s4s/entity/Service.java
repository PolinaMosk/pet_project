package s4s.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "services", nullable = false)
    private String name;

    @Column(name = "prices", nullable = false)
    private String price;
}
